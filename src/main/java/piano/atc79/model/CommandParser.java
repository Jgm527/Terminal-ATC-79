package piano.atc79.model;

import java.util.List;

/**
 * Analiza los comandos de texto del usuario y aplica las acciones correspondientes 
 * a los vuelos en el juego.
 * 
 * <p>Ejemplos de comandos manejados:</p>
 * <ul>
 *   <li>{@code IBE1234 H 250} - Cambiar el rumbo del vuelo a 250 grados</li>
 *   <li>{@code IBE1234 HLD H1 3000} - Enviar vuelo al punto de espera H1 a 3000 pies</li>
 *   <li>{@code IBE1234 CLR ILS 10} - Autorizar aterrizaje instrumental en pista 10</li>
 * </ul>
 */
public class CommandParser {
    /**
     * Analiza el texto introducido y aplica la lógica de comando a los vuelos especificados.
     * 
     * @param input el comando de texto introducido por el usuario
     * @param flights la lista de vuelos activos en el juego
     * @param airport el aeropuerto donde podrían tener lugar las acciones
     * @param game el modelo del juego que maneja el estado global
     * @return un mensaje describiendo el resultado de la ejecución del comando
     * @throws CommandExceptions si el comando está mal formado o es inválido
     */
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
                case "HLD":
                    return handleHolding(commands, flight, airport, game);
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
                f.exitHolding();
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

        if (type.equals("VIS")) {
            f.exitHolding();
            f.setApproachType(type);
            f.setAssignedRunway(rw);
            f.setStatus(FlightStatus.VIS_APPROACH);
            return String.format(game.getTemplate("CMD_CLRVIS"), f.getCallsign(), type, runwayId);
        } else if (type.equals("ILS")) {
            f.exitHolding();
            f.setApproachType(type);
            f.setAssignedRunway(rw);
            f.setStatus(FlightStatus.ILS_APPROACH);
            return String.format(game.getTemplate("CMD_CLRILS"), f.getCallsign(), type, runwayId);
        } else {
            throw new CommandExceptions("Tipo de aproximación no encontrada");
        }
    }

    private String handleHolding(String[] parts, Flight f, Airport airport, Game game) throws CommandExceptions {
        if (parts.length < 3) {
            throw new CommandExceptions("Uso: HLD [PUNTO] [ALT_OPCIONAL]");
        }

        String holdingPointId = parts[2].toUpperCase();
        HoldingPoint holdingPoint = airport.findHoldingPoint(holdingPointId);
        if (holdingPoint == null) {
            throw new CommandExceptions("Holding point " + holdingPointId + " no encontrado.");
        }

        int holdAltitude = f.getTargetAltitude();
        if (parts.length >= 4) {
            holdAltitude = Integer.parseInt(parts[3]);
            f.setTargetAltitude(holdAltitude);
        }

        f.enterHolding(holdingPoint);
        return String.format(game.getTemplate("CMD_HLD"), f.getCallsign(), holdingPoint.getId(), holdAltitude);
    }
}