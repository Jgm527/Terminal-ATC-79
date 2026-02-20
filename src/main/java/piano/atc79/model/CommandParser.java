package piano.atc79.model;

import java.util.List;

public class CommandParser {
    public String parse(String input, List<Flight> flights, Airport airport, Game game) throws CommandExceptions {
        String[] commands = input.trim().split( "\\s");

        if (commands.length < 2) {
            throw new CommandExceptions("Comando incompleto. Formato: [CALLSIGN] [ACCION] [VALOR]");
        }

        String callsign = commands[0].toUpperCase();
        Flight flight = findFlight(callsign, flights);

        String action = commands[1].toUpperCase();
        return processAction(action, commands, flight, airport, game);

    }

    private Flight findFlight(String callsign, List<Flight> flights) throws CommandExceptions {
        for (Flight f : flights) {
            if (f.getCallsign().equals(callsign)) {
                return f;
            }
        }
        throw new CommandExceptions("Vuelo " + callsign + " no identificado.");
    }

    private String processAction(String action, String[] commands, Flight flight, Airport airport, Game game)
            throws CommandExceptions {
        try {
            switch (action) {
                case "H", "A", "S":
                    return handleBasicMovement(action, commands[2], flight, game);
                case "CLR":
                    return handleClearance(commands, flight, airport, game);
                default:
                    throw new CommandExceptions("Acción desconocida:" + action);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandExceptions("Faltan parámetros para la acción " + action);
        } catch (NumberFormatException e) {
            throw new CommandExceptions("El valor debe ser numérico.");
        }
    }

    private String handleBasicMovement(String action, String valueStr, Flight f, Game game) throws CommandExceptions {
        int val = Integer.parseInt(valueStr);
        String msg = "";
        switch (action) {
            case "H" -> {
                f.setTargetHeading(val);
                msg = String.format(game.getTemplate("CMD_H"), f.getCallsign(), val);
            }
            case "A" -> {
                f.setTargetAltitude(val);
                msg = String.format(game.getTemplate("CMD_A"), f.getCallsign(), val);
            }
            case "S" -> {
                f.setTargetSpeed(val);
                msg = String.format(game.getTemplate("CMD_S"), f.getCallsign(), val);
            }
        }
        return msg;
    }

    private String handleClearance(String[] parts, Flight f, Airport airport, Game game) throws CommandExceptions {
        if (parts.length < 4) throw new CommandExceptions("Uso: CLR [TIPO] [PISTA]");


        String type = parts[2].toUpperCase();
        String runwayId = parts[3].toUpperCase();
        Runway rw = airport.findRunway(runwayId);

        if (rw == null) throw new CommandExceptions("Pista " + runwayId + " no encontrada.");

        if (type.equals("VIS") || type.equals("ILS")) {
            f.setApproachType(type);
            f.setAssignedRunway(rw);
            f.setStatus(FlightStatus.APPROACH);
            return String.format(game.getTemplate("CMD_CLR"), f.getCallsign(), type, runwayId);
        } else {
            throw new CommandExceptions("Tipo de aproximación no encontrada");
        }
    }
}