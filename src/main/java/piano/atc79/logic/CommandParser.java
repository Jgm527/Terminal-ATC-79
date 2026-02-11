package piano.atc79.logic;

import piano.atc79.model.Flight;

import java.util.List;

public class CommandParser {
    public void parse(String input, List<Flight> flights) {
        String[] commands = input.split(" ");
        String callsign = commands[0].toUpperCase();

        for (Flight f : flights) {
            if (f.getCallsign().equals(callsign) && commands.length == 3) {
                String action = commands[1].toUpperCase();
                int value = Integer.parseInt(commands[2]); //Se podrían añadir Try Catch por si el valor no es numerico o verificarlo antes

                switch (action) {
                    case "H":
                        f.setTargetHeading(value);
                        break;
                    case "A":
                        f.setTargetAltitude(value);
                        break;
                    case "S":
                        f.setTargetSpeed(value);
                }
                return;
            }
        }
    }
}
