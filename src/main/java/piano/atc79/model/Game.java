package piano.atc79.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private String player;
    private Airport airport;
    private List<Flight> flights;
    private Score score;
    private CommandParser commandParser;
    private boolean gameOver;
    private Map<String, String> radioTemplates;
    private List<String> eventLog;

    public Game(Airport airport) {
        this.airport = airport;
        this.flights = new ArrayList<>();
        this.score = new Score();
        this.commandParser = new CommandParser();
        this.gameOver = false;
        this.eventLog = new ArrayList<>();
        initRadioTemplates();
    }

    private void initRadioTemplates() {
        radioTemplates = new HashMap<>();
        radioTemplates.put("CMD_H", "%s giró a rumbo %d");
        radioTemplates.put("CMD_A", "%s se mantendrá un nivel de %d pies");
        radioTemplates.put("CMD_S", "%s cambió su velocidad a %d nudos");
        radioTemplates.put("CMD_CLR", "%s, autorizado a aproximación %s pista %s");

        radioTemplates.put("EVT_LANDED", ">>> %s ha aterrizado con éxito");
        radioTemplates.put("EVT_TCAS", "TCAS: Alerta de proximidad entre %s y %s.");
        radioTemplates.put("EVT_FUEL", "EMERGENCIA: %s con combustible crítico.");
    }

    public String getTemplate(String key) {
        return radioTemplates.getOrDefault(key, "Mensaje desconocido: " + key);
    }

    public void addEvent(String key, Object... args) {
        String template = getTemplate(key);
        eventLog.add(String.format(template, args));
    }

    public List<String> pullEvents() {
        List<String> currentEvents = new ArrayList<>(eventLog);
        eventLog.clear();
        return currentEvents;
    }

    public void update() {
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);
            f.updatePosition();
            f.updateFuel();

            if (f.getStatus().equals(FlightStatus.LANDING)) {
                if (f.getCurrentPosition().distanceTo(f.getAssignedRunway().getStartPoint()) < 0.3) {
                    f.land();
                }
                if (f.getSpeed() <= 0) {
                    addEvent("EVT_LANDED", f.getCallsign());
                    flights.remove(i);
                    continue;
                }
            }
            else if (f.isReadyToLand()) {
                f.setStatus(FlightStatus.LANDING);
            }
            if (f.getFuel() < 20) {
                if (Math.random() < 0.05) {
                    addEvent("EVT_FUEL", f.getCallsign());
                }
            }
        }
        checkGameOver();
    }

    public void checkGameOver() {
        // Buscar aviones sin combustible
        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight f = flights.get(i);

            // Buscar aviones que han colisionado
            for (int j = i + 1; j < flights.size(); j++) {
                Flight f2 = flights.get(j);

                if (f.areInConflict(f2)) {
                    addEvent("EVT_TCAS", f.getCallsign(), f2.getCallsign());
                    gameOver = true;
                    return;
                }
            }

            if (f.getFuel() <= 0) {
                gameOver = true;
            }
        }
    }

    public String executeCommand(String command) throws CommandExceptions {
        return commandParser.parse(command, this.flights, this.airport, this);
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

    public Score getScore() {
        return score;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public Airport getAirport() {
        return airport;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
