package piano.atc79.logic;

import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Runway;

import java.util.List;

public class CommandParser {
    public void parse(String input, List<Flight> flights, Airport airport) throws CommandExceptions {
        String[] commands = input.trim().split( "\\s");

        if (commands.length < 2) {
            throw new CommandExceptions("Comando incompleto. Formato: [CALLSIGN] [ACCION] [VALOR]");
        }

        String callsign = commands[0].toUpperCase();
        Flight flight = findFlight(callsign, flights);

        String action = commands[1].toUpperCase();
        processAction(action, commands, flight, airport);
    }

    private Flight findFlight(String callsign, List<Flight> flights) throws CommandExceptions {
        for (Flight f : flights) {
            if (f.getCallsign().equals(callsign)) {
                return f;
            }
        }
        throw new CommandExceptions("Vuelo " + callsign + " no identificado.");
    }

    private void processAction(String action, String[] commands, Flight flight, Airport airport)
            throws CommandExceptions {
        try {
            switch (action) {
                case "H", "A", "S":
                    handleBasicMovement(action, commands[2], flight);
                    break;
                case "CLR":
                    handleClearance(commands, flight, airport);
                    break;
                default:
                    throw new CommandExceptions("Acción desconocida:" + action);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandExceptions("Faltan parámetros para la acción " + action);
        } catch (NumberFormatException e) {
            throw new CommandExceptions("El valor debe ser numérico.");
        }
    }

    private void handleBasicMovement(String action, String valueStr, Flight f) throws CommandExceptions {
        int val = Integer.parseInt(valueStr);
        switch (action) {
            case "H" -> f.setTargetHeading(val);
            case "A" -> f.setTargetAltitude(val);
            case "S" -> f.setTargetSpeed(val);
        }
    }

    private void handleClearance(String[] parts, Flight f, Airport airport) throws CommandExceptions {
        if (parts.length < 4) throw new CommandExceptions("Uso: CLR [TIPO] [PISTA]");

        String type = parts[2].toUpperCase();
        String runwayId = parts[3].toUpperCase();
        Runway rw = airport.findRunway(runwayId);

        if (rw == null) throw new CommandExceptions("Pista " + runwayId + " no encontrada.");

        if (type.equals("VIS") || type.equals("ILS")) {
            f.setApproachType(type);
            f.setAssignedRunway(rw);
        } else {
            throw new CommandExceptions("Tipo de aproximación no encontrada");
        }
    }
}