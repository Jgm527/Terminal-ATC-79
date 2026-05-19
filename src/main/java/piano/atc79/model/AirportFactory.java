package piano.atc79.model;

import java.util.List;

/**
 * Factory estatica para la creacion de los 7 aeropuertos disponibles en el juego.
 * Cada metodo devuelve un objeto {@link Airport} completamente configurado con sus pistas
 * y puntos de espera correspondientes.
 */
public final class AirportFactory {

    private AirportFactory() {}

    /**
     * Crea el aeropuerto de Alicante-Elche (LEAL).
     * Aeropuerto de dificultad muy baja con una unica pista (10/28) y un punto de espera.
     *
     * @return el aeropuerto de Alicante completamente configurado
     */
    public static Airport createLEAL() {
        Airport airport = new Airport("LEAL", "Alicante-Elche", 2000);

        Position start10 = new Position(0, 0, 0);
        Position end10 = new Position(1.6, 0.5, 0);
        airport.addRunway(new Runway("10", start10, end10, true));

        Position start28 = new Position(1.6, 0.5, 0);
        Position end28 = new Position(0, 0, 0);
        airport.addRunway(new Runway("28", start28, end28, false));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-5, 3.2, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);

        return airport;
    }

    /**
     * Crea el aeropuerto de Barcelona-El Prat (LEBL).
     * Aeropuerto de dificultad media con dos pistas y tres puntos de espera.
     *
     * @return el aeropuerto de Barcelona completamente configurado
     */
    public static Airport createLEBL() {
        Airport airport = new Airport("LEBL", "Barcelona-El Prat", 2500);

        Position start07L = new Position(-0.8, -1.2, 0);
        Position end25R = new Position(2.2, 1.0, 0);
        airport.addRunway(new Runway("07L", start07L, end25R, true));

        Position start25R = new Position(2.2, 1.0, 0);
        Position end07L = new Position(-0.8, -1.2, 0);
        airport.addRunway(new Runway("25R", start25R, end07L, true));

        Position start02 = new Position(0.5, -0.8, 0);
        Position end20 = new Position(2.0, 0.6, 0);
        airport.addRunway(new Runway("02", start02, end20, false));

        Position start20 = new Position(2.0, 0.6, 0);
        Position end02 = new Position(0.5, -0.8, 0);
        airport.addRunway(new Runway("20", start20, end02, false));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-6, 4, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H2", new Position(4.7, -5.4, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H3", new Position(1.7, 5.7, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);
        airport.addEntryRoute(new EntryRoute("NORTHEAST", 225, List.of(
            new Position(10, 10, 0),
            new Position(12, 12, 0),
            new Position(14, 14, 0)
        )));

        return airport;
    }

    /**
     * Crea el aeropuerto de Los Angeles International (KLAX).
     * Aeropuerto de dificultad alta con tres pistas paralelas y multiples puntos de espera.
     *
     * @return el aeropuerto de Los Angeles completamente configurado
     */
    public static Airport createKLAX() {
        Airport airport = new Airport("KLAX", "Los Angeles International", 3000);

        Position start24L = new Position(-1.5, -0.6, 0);
        Position end06R = new Position(1.5, 0.6, 0);
        airport.addRunway(new Runway("24L", start24L, end06R, true));

        Position start06R = new Position(1.5, 0.6, 0);
        Position end24L = new Position(-1.5, -0.6, 0);
        airport.addRunway(new Runway("06R", start06R, end24L, true));

        Position start24R = new Position(-1.5, -0.2, 0);
        Position end06L = new Position(1.5, 1.0, 0);
        airport.addRunway(new Runway("24R", start24R, end06L, true));

        Position start06L = new Position(1.5, 1.0, 0);
        Position end24R = new Position(-1.5, -0.2, 0);
        airport.addRunway(new Runway("06L", start06L, end24R, true));

        Position start25L = new Position(-1.5, 0.2, 0);
        Position end07R = new Position(1.5, 1.4, 0);
        airport.addRunway(new Runway("25L", start25L, end07R, true));

        Position start07R = new Position(1.5, 1.4, 0);
        Position end25L = new Position(-1.5, 0.2, 0);
        airport.addRunway(new Runway("07R", start07R, end25L, true));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-4.0, 3.0, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H2", new Position(4.0, -2.0, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H3", new Position(0.0, 5.0, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);
        airport.addEntryRoute(new EntryRoute("NORTHWEST", 135, List.of(
            new Position(-10, 10, 0),
            new Position(-12, 12, 0),
            new Position(-14, 14, 0)
        )));

        return airport;
    }

    /**
     * Crea el aeropuerto de Londres Heathrow (EGLL).
     * Aeropuerto de dificultad muy alta con pistas paralelas y cruzadas.
     *
     * @return el aeropuerto de Heathrow completamente configurado
     */
    public static Airport createEGLL() {
        Airport airport = new Airport("EGLL", "London Heathrow", 3000);

        Position start09L = new Position(-1.2, -0.4, 0);
        Position end27R = new Position(1.8, 0.6, 0);
        airport.addRunway(new Runway("09L", start09L, end27R, true));

        Position start27R = new Position(1.8, 0.6, 0);
        Position end09L = new Position(-1.2, -0.4, 0);
        airport.addRunway(new Runway("27R", start27R, end09L, true));

        Position start09R = new Position(-1.2, 0.1, 0);
        Position end27L = new Position(1.8, 1.1, 0);
        airport.addRunway(new Runway("09R", start09R, end27L, true));

        Position start27L = new Position(1.8, 1.1, 0);
        Position end09R = new Position(-1.2, 0.1, 0);
        airport.addRunway(new Runway("27L", start27L, end09R, true));

        Position start23 = new Position(0.8, -1.0, 0);
        Position end05 = new Position(1.8, 0.5, 0);
        airport.addRunway(new Runway("23", start23, end05, false));

        Position start05 = new Position(1.8, 0.5, 0);
        Position end23 = new Position(0.8, -1.0, 0);
        airport.addRunway(new Runway("05", start05, end23, false));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-4.5, 4.5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H2", new Position(5, -6, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H3", new Position(0.0, 6.5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H4", new Position(-2.9, -5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);
        airport.addEntryRoute(new EntryRoute("SOUTHEAST", 315, List.of(
            new Position(10, -10, 0),
            new Position(12, -12, 0),
            new Position(14, -14, 0)
        )));

        return airport;
    }

    /**
     * Crea el aeropuerto de Tenerife Norte (GCXO).
     * Aeropuerto de dificultad extrema con una unica pista y un punto de espera.
     *
     * @return el aeropuerto de Tenerife Norte completamente configurado
     */
    public static Airport createGCXO() {
        Airport airport = new Airport("GCXO", "Tenerife Norte", 3500);

        Position start12 = new Position(0, 0, 0);
        Position end30 = new Position(1.2, 2.0, 0);
        airport.addRunway(new Runway("12", start12, end30, true));

        Position start30 = new Position(1.2, 2.0, 0);
        Position end12 = new Position(0, 0, 0);
        airport.addRunway(new Runway("30", start30, end12, false));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(6.5, 8, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);

        return airport;
    }

    /**
     * Crea el aeropuerto de Keflavik (BIKF).
     * Aeropuerto de dificultad extrema con dos pistas cruzadas.
     *
     * @return el aeropuerto de Keflavik completamente configurado
     */
    public static Airport createBIKF() {
        Airport airport = new Airport("BIKF", "Keflavik", 2500);

        Position start11 = new Position(-0.8, -0.8, 0);
        Position end29 = new Position(1.2, 0.8, 0);
        airport.addRunway(new Runway("11", start11, end29, true));

        Position start29 = new Position(1.2, 0.8, 0);
        Position end11 = new Position(-0.8, -0.8, 0);
        airport.addRunway(new Runway("29", start29, end11, true));

        Position start01 = new Position(0.0, -1.0, 0);
        Position end19 = new Position(0.0, 1.0, 0);
        airport.addRunway(new Runway("01", start01, end19, false));

        Position start19 = new Position(0.0, 1.0, 0);
        Position end01 = new Position(0.0, -1.0, 0);
        airport.addRunway(new Runway("19", start19, end01, false));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-3.0, 4.7, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H2", new Position(3.6, -4.2, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);

        return airport;
    }

    /**
     * Crea el aeropuerto de Nueva York JFK (KJFK).
     * Aeropuerto de dificultad maxima con cuatro pistas en dos pares paralelos.
     *
     * @return el aeropuerto de JFK completamente configurado
     */
    public static Airport createKJFK() {
        Airport airport = new Airport("KJFK", "John F. Kennedy International", 3000);

        Position start04L = new Position(-1.0, -0.8, 0);
        Position end22R = new Position(1.5, 0.7, 0);
        airport.addRunway(new Runway("04L", start04L, end22R, true));

        Position start22R = new Position(1.5, 0.7, 0);
        Position end04L = new Position(-1.0, -0.8, 0);
        airport.addRunway(new Runway("22R", start22R, end04L, true));

        Position start04R = new Position(-1.0, -0.4, 0);
        Position end22L = new Position(1.5, 1.1, 0);
        airport.addRunway(new Runway("04R", start04R, end22L, true));

        Position start22L = new Position(1.5, 1.1, 0);
        Position end04R = new Position(-1.0, -0.4, 0);
        airport.addRunway(new Runway("22L", start22L, end04R, true));

        Position start13L = new Position(0.2, -1.0, 0);
        Position end31R = new Position(1.2, 1.5, 0);
        airport.addRunway(new Runway("13L", start13L, end31R, true));

        Position start31R = new Position(1.2, 1.5, 0);
        Position end13L = new Position(0.2, -1.0, 0);
        airport.addRunway(new Runway("31R", start31R, end13L, true));

        Position start13R = new Position(0.6, -1.0, 0);
        Position end31L = new Position(1.6, 1.5, 0);
        airport.addRunway(new Runway("13R", start13R, end31L, true));

        Position start31L = new Position(1.6, 1.5, 0);
        Position end13R = new Position(0.6, -1.0, 0);
        airport.addRunway(new Runway("31L", start31L, end13R, true));

        airport.addHoldingPoint(new HoldingPoint("H1", new Position(-3.7, 5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H2", new Position(4.0, -4.5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H3", new Position(0.6, 10.5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H4", new Position(-4.7, -6, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));
        airport.addHoldingPoint(new HoldingPoint("H5", new Position(8, 8.5, 0), HoldingPoint.DEFAULT_HOLD_RADIUS_NM));

        addStandardEntryRoutes(airport);
        airport.addEntryRoute(new EntryRoute("NORTHEAST", 225, List.of(
            new Position(10, 10, 0),
            new Position(12, 12, 0),
            new Position(14, 14, 0)
        )));
        airport.addEntryRoute(new EntryRoute("SOUTHWEST", 45, List.of(
            new Position(-10, -10, 0),
            new Position(-12, -12, 0),
            new Position(-14, -14, 0)
        )));

        return airport;
    }

    /**
     * Anade las cuatro rutas de entrada estandar (N, E, S, W) a un aeropuerto.
     * Cada ruta tiene 3 puntos de spawn distribuidos a lo largo del corredor
     * a una distancia aproximada de 15 millas del centro.
     *
     * @param airport el aeropuerto al que se anadiran las rutas
     */
    private static void addStandardEntryRoutes(Airport airport) {
        airport.addEntryRoute(new EntryRoute("NORTH", 170, List.of(
            new Position(-3, 15, 0),
            new Position(0, 15, 0),
            new Position(3, 15, 0)
        )));
        airport.addEntryRoute(new EntryRoute("EAST", 260, List.of(
            new Position(15, -3, 0),
            new Position(15, 0, 0),
            new Position(15, 3, 0)
        )));
        airport.addEntryRoute(new EntryRoute("SOUTH", 350, List.of(
            new Position(3, -15, 0),
            new Position(0, -15, 0),
            new Position(-3, -15, 0)
        )));
        airport.addEntryRoute(new EntryRoute("WEST", 80, List.of(
            new Position(-15, 3, 0),
            new Position(-15, 0, 0),
            new Position(-15, -3, 0)
        )));
    }
}