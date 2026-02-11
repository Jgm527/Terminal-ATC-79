package piano.atc79.view;

import piano.atc79.model.Airport;
import piano.atc79.model.Flight;
import piano.atc79.model.Score;

import java.util.List;

public class ConsoleView {
    public void showInformation(Airport airport, List<Flight> flights, Score score) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("===========================================================================");
        System.out.println("  RADAR ATC - Aeropuerto: " + airport.getName() + " [" + airport.getId() + "]");
        System.out.println("  Puntos: " + score.getTotalPoints() + " | Aterrizajes: " + score.getSuccessfulLandings());
        System.out.println("===========================================================================");

        System.out.printf("%-10s %-20s %-10s %-8s %-10s %-12s%n",
                "CALLSIGN", "POSICIÓN (X,Y)", "ALT", "HDG", "FUEL", "STATUS");
        System.out.println("---------------------------------------------------------------------------");
        for (Flight f : flights) {
            System.out.printf("%-10s [%6.2f, %6.2f]    %-10d %-8d %-10.2f %-12s%n",
                    f.getCallsign(),
                    f.getCurrentPosition().getX(),
                    f.getCurrentPosition().getY(),
                    f.getAltitude(),
                    f.getHeading(),
                    f.getFuel(),
                    f.getStatus()
            );
        }
        System.out.println("===========================================================================");
    }

    public void showGameOver(Score finalScore) {
        System.out.println("\n\n***********************************************************");
        System.out.println("         ¡¡¡ CATÁSTROFE AÉREA - GAME OVER !!!");
        System.out.println("***********************************************************");
        System.out.println("  Puntuación final: " + finalScore.getTotalPoints());
        System.out.println("  Aterrizajes logrados: " + finalScore.getSuccessfulLandings());
        System.out.println("***********************************************************\n");
    }
}
