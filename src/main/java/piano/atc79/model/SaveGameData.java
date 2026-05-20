package piano.atc79.model;

/**
 * DTO que representa una partida guardada en formato JSON.
 * Contiene metadatos del guardado (nombre, aeropuerto, timestamp)
 * y el estado completo de la partida (puntuacion, vuelos, fin de juego).
 */
public class SaveGameData {

    private String saveName;
    private String airportCode;
    private long timestamp;
    private ScoreData score;
    private FlightData[] flights;
    private boolean gameOver;

    public SaveGameData() {}

    public SaveGameData(String saveName, String airportCode, long timestamp,
                        ScoreData score, FlightData[] flights, boolean gameOver) {
        this.saveName = saveName;
        this.airportCode = airportCode;
        this.timestamp = timestamp;
        this.score = score;
        this.flights = flights;
        this.gameOver = gameOver;
    }

    // --- Getters ---

    public String getSaveName() { return saveName; }
    public String getAirportCode() { return airportCode; }
    public long getTimestamp() { return timestamp; }
    public ScoreData getScore() { return score; }
    public FlightData[] getFlights() { return flights; }
    public boolean isGameOver() { return gameOver; }

    // =========================================================
    //  ScoreData — snapshot de Score
    // =========================================================

    public static class ScoreData {
        private int totalPoints;
        private int successfulLandings;
        private int successfulTakesOff;

        public ScoreData() {}

        public ScoreData(int totalPoints, int successfulLandings, int successfulTakesOff) {
            this.totalPoints = totalPoints;
            this.successfulLandings = successfulLandings;
            this.successfulTakesOff = successfulTakesOff;
        }

        public int getTotalPoints() { return totalPoints; }
        public int getSuccessfulLandings() { return successfulLandings; }
        public int getSuccessfulTakesOff() { return successfulTakesOff; }
    }

    // =========================================================
    //  FlightData — snapshot de un Flight
    // =========================================================

    public static class FlightData {
        private String callsign;
        private String modelId;
        private double x, y;
        private int z;
        private int heading, targetHeading;
        private int speed, targetSpeed;
        private int targetAltitude;
        private double fuel;
        private String status;
        private String assignedRunwayId;
        private String approachType;
        private String holdingPointId;
        private boolean enteringHolding;

        public FlightData() {}

        public FlightData(String callsign, String modelId,
                          double x, double y, int z,
                          int heading, int targetHeading,
                          int speed, int targetSpeed,
                          int targetAltitude, double fuel,
                          String status, String assignedRunwayId,
                          String approachType, String holdingPointId,
                          boolean enteringHolding) {
            this.callsign = callsign;
            this.modelId = modelId;
            this.x = x; this.y = y; this.z = z;
            this.heading = heading;
            this.targetHeading = targetHeading;
            this.speed = speed;
            this.targetSpeed = targetSpeed;
            this.targetAltitude = targetAltitude;
            this.fuel = fuel;
            this.status = status;
            this.assignedRunwayId = assignedRunwayId;
            this.approachType = approachType;
            this.holdingPointId = holdingPointId;
            this.enteringHolding = enteringHolding;
        }

        // --- Getters ---

        public String getCallsign() { return callsign; }
        public String getModelId() { return modelId; }
        public double getX() { return x; }
        public double getY() { return y; }
        public int getZ() { return z; }
        public int getHeading() { return heading; }
        public int getTargetHeading() { return targetHeading; }
        public int getSpeed() { return speed; }
        public int getTargetSpeed() { return targetSpeed; }
        public int getTargetAltitude() { return targetAltitude; }
        public double getFuel() { return fuel; }
        public String getStatus() { return status; }
        public String getAssignedRunwayId() { return assignedRunwayId; }
        public String getApproachType() { return approachType; }
        public String getHoldingPointId() { return holdingPointId; }
        public boolean isEnteringHolding() { return enteringHolding; }
    }
}
