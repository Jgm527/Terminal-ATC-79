package piano.atc79.model;

/**
 * Representa un vuelo monitorizado por el juego, incluyendo sus propiedades físicas
 * y estado dinámico como la posición, velocidad y el rumbo.
 */
public class Flight {
    private String callsign;
    private AircraftModel model;
    private Position currentPosition;
    private int heading;
    private int speed;
    private double fuel;
    private FlightStatus status;
    private int targetHeading;
    private int targetAltitude;
    private int targetSpeed;
    private Runway assignedRunway;
    private String approachType;
    private static final ApproachRules APPROACH_RULES = new ApproachRules();
    private HoldingPoint holdingPoint;
    private boolean enteringHolding;
    private static final double HOLD_RADIUS_TOLERANCE_NM = 0.10;
    private static final double HOLD_ENTRY_CAPTURE_NM = 0.20;
    private static final int HOLD_MAX_RADIAL_CORRECTION_DEGREES = 40;

    /** Distancia al umbral en el tick anterior durante LANDING, para detectar overshoot. */
    private double previousLandingDist;

    /**
     * Construye un Flight (Vuelo) con datos de posición y cinéticos básicos.
     * 
     * @param callsign el indicativo de llamada (identificador) del vuelo
     * @param model el {@link AircraftModel} que describe las características del avión
     * @param currentPosition la {@link Position} inicial del avión
     * @param heading el rumbo inicial en grados
     * @param speed la velocidad inicial sobre el terreno
     */
    public Flight(String callsign, AircraftModel model, Position currentPosition, int heading, int speed) {
        this.callsign = callsign;
        this.model = model;
        this.currentPosition = currentPosition;
        this.heading = heading;
        this.speed = speed;
        this.fuel = calculateFuel();
        this.status = FlightStatus.EN_ROUTE;
        this.targetHeading = heading;
        this.targetAltitude = getCurrentPosition().getZ();
        this.targetSpeed = speed;
        this.previousLandingDist = -1;
    }

    public String getCallsign() {
        return callsign;
    }

    public AircraftModel getModel() {
        return model;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public int getHeading() {
        return heading;
    }

    public int getSpeed() {
        return speed;
    }

    public double getFuel() {
        return fuel;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public int getTargetHeading() {
        return targetHeading;
    }

    public int getTargetAltitude() {
        return targetAltitude;
    }

    public int getTargetSpeed() {
        return targetSpeed;
    }

    public Runway getAssignedRunway() {
        return assignedRunway;
    }

    public String getApproachType() {
        return approachType;
    }

    public HoldingPoint getHoldingPoint() {
        return holdingPoint;
    }

    private void setHeading(int heading) {
        this.heading = heading;
    }

    private void setSpeed(int speed) {
        this.speed = speed;
    }

    private void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public void setTargetHeading(int targetHeading) {
        this.targetHeading = targetHeading;
    }

    private void setAltitude(int altitude) {
        currentPosition.setZ(altitude);
    }

    public void setTargetAltitude(int targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    public void setTargetSpeed(int targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    public void setAssignedRunway(Runway assignedRunway) {
        this.assignedRunway = assignedRunway;
    }

    public void setApproachType(String approachType) {
        this.approachType = approachType;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    private double calculateFuel() {
        return model.getMaxFuel() * (0.7 + Math.random() * 0.3);
    }

    /**
     * Actualiza la posición del vuelo durante cada ciclo/tick de la simulación,
     * teniendo en cuenta la velocidad actual, rumbo, y parámetros objetivos.
     */
    public void updatePosition() {
        if (this.status == FlightStatus.HOLDING && holdingPoint != null) {
            updateHoldingPattern();
        }

        if (this.status == FlightStatus.LANDING && assignedRunway != null) {
            APPROACH_RULES.applyLandingGuidance(this);
            checkLandingOvershoot();
        }

        updateHeading();
        updateLatitude();
        updateAltitude();
        updateSpeed();
    }

    private void updateLatitude() {
        double speedPerSeconds = getSpeed() / 3600.0;
        double rad = Math.toRadians(getHeading());

        double deltaX = speedPerSeconds * Math.sin(rad);
        double deltaY = speedPerSeconds * Math.cos(rad);

        Position pos = getCurrentPosition();
        pos.setX(pos.getX() + deltaX);
        pos.setY(pos.getY() + deltaY);
    }

    private void updateAltitude() {
        int current = currentPosition.getZ();
        int target = getTargetAltitude();

        if (this.status == FlightStatus.LANDING) {
            target = Math.min(current, target);
        }

        if (current < target) {
            setAltitude(Math.min(current + getModel().getClimbRate(), target));
        } else if (current > target) {
            setAltitude(Math.max(current - getModel().getClimbRate(), target));
        }
    }

    private void updateHeading() {
        int current = getHeading();
        int target = getTargetHeading();

        int diff = (target - current + 360) % 360;

        if (diff <= getModel().getTurningRate()) {
            setHeading(target);
        } else if (diff < 180) {
            setHeading(current + getModel().getTurningRate());
        } else if (diff > 180) {
            setHeading(current - getModel().getTurningRate());
        }

        if (Math.abs(target - getHeading()) < getModel().getTurningRate() || Math.abs(target - getHeading()) > 360 - getModel().getTurningRate()) {
            setHeading(target);
        }

        setHeading((getHeading() + 360) % 360);
    }

    private void updateSpeed() {
        int current = getSpeed();
        int target = getTargetSpeed();

        if (current < target) {
            setSpeed(Math.min(current + getModel().getSpeedingRate(), target));
        } else if (current > target) {
            setSpeed(Math.max(current - getModel().getSpeedingRate(), target));
        }
    }

    /**
     * Actualiza la cantidad de combustible de forma basada en tasas de consumo y el tiempo transcurrido.
     */
    public void updateFuel() {
        double consumptionPerSecond = getModel().getFuelConsumption() / 3600.0;
        double newFuel = getFuel() - consumptionPerSecond;

        if (newFuel < 0) { newFuel = 0; }

        setFuel(newFuel);
    }

    /**
     * Evalúa si el vuelo puede iniciar la fase de aterrizaje con la aproximación actual.
     *
     * @return true si el vuelo está en condiciones de entrar en LANDING
     */
    public boolean isReadyToLand() {
        return APPROACH_RULES.isReadyToLand(this);
    }

    /**
     * Detecta si el avion ha pasado de largo la pista durante el aterrizaje.
     * Si la distancia al umbral aumenta mientras esta a baja altitud, se
     * ejecuta una aproximacion frustrada (go-around).
     */
    private void checkLandingOvershoot() {
        if (assignedRunway == null) {
            return;
        }

        double dist = currentPosition.distanceTo(assignedRunway.getStartPoint());

        if (previousLandingDist < 0) {
            previousLandingDist = dist;
            return;
        }

        // Si la distancia crece estando cerca y a baja cota, hemos pasado de largo
        if (dist > previousLandingDist + 0.02
                && currentPosition.getZ() < 500
                && dist < 5.0) {
            goAround();
            return;
        }

        previousLandingDist = dist;
    }

    /**
     * Ejecuta una aproximacion frustrada (go-around). Restablece el vuelo a
     * EN_ROUTE, asigna una altitud de escape y limpia los datos de aproximacion.
     * El jugador debera re-vectorizar el avion para un nuevo intento.
     */
    public void goAround() {
        int safeAltitude = currentPosition.getZ() + 1000;
        if (safeAltitude > 5000) {
            safeAltitude = 5000;
        }
        this.targetAltitude = safeAltitude;
        this.targetSpeed = model.getCruiseSpeed();
        this.assignedRunway = null;
        this.approachType = null;
        this.status = FlightStatus.EN_ROUTE;
        this.previousLandingDist = -1;
    }

    public boolean checkLandingCondition() {
        boolean altitudeOk = currentPosition.getZ() < 1000;
        boolean speedOk = this.speed < 160;
        boolean distanceOK = this.getCurrentPosition().distanceTo(assignedRunway.getStartPoint()) < 0.3;
        boolean approachOK = this.getApproachType().equals("ILS") && assignedRunway.hasILS() || this.getApproachType().equals("VIS");

        return altitudeOk && speedOk && distanceOK && approachOK;
    }

    /**
     * Activa un patrón de espera circular alrededor de un punto.
     *
     * @param holdingPoint punto de espera asignado
     */
    public void enterHolding(HoldingPoint holdingPoint) {
        this.holdingPoint = holdingPoint;
        this.enteringHolding = true;
        this.assignedRunway = null;
        this.approachType = null;
        this.status = FlightStatus.HOLDING;
        this.targetSpeed = this.model.getMinSpeed() + 20;
    }

    /**
     * Cancela el patrón de espera actual y devuelve el vuelo a EN_ROUTE.
     */
    public void exitHolding() {
        this.holdingPoint = null;
        this.enteringHolding = false;
        if (this.status == FlightStatus.HOLDING) {
            this.status = FlightStatus.EN_ROUTE;
        }
    }

    private void updateHoldingPattern() {
        Position center = holdingPoint.getPosition();
        double radius = holdingPoint.getRadiusNm();
        double distanceToCenter = this.currentPosition.distanceTo(center);

        if (enteringHolding) {
            Position interceptPoint = getRadialInterceptPoint(center, radius);
            this.targetHeading = calculateHeadingTo(interceptPoint);
            if (this.currentPosition.distanceTo(interceptPoint) <= getDynamicCaptureRadius(HOLD_ENTRY_CAPTURE_NM)) {
                enteringHolding = false;
            }
            return;
        }

        int radialHeading = calculateHeadingBetween(center, this.currentPosition);
        int tangentClockwiseHeading = (radialHeading + 90) % 360;
        int radialCorrection = computeRadialCorrection(distanceToCenter, radius);
        this.targetHeading = (tangentClockwiseHeading + radialCorrection + 360) % 360;
    }

    private int calculateHeadingTo(Position target) {
        double dx = target.getX() - this.currentPosition.getX();
        double dy = target.getY() - this.currentPosition.getY();
        int headingToTarget = (int) Math.round(Math.toDegrees(Math.atan2(dx, dy)));
        return (headingToTarget + 360) % 360;
    }

    private int calculateHeadingBetween(Position from, Position to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        int heading = (int) Math.round(Math.toDegrees(Math.atan2(dx, dy)));
        return (heading + 360) % 360;
    }

    private double getDynamicCaptureRadius(double baseRadius) {
        double nmPerSecond = this.speed / 3600.0;
        return baseRadius + (nmPerSecond * 2.0);
    }

    private Position getRadialInterceptPoint(Position center, double radius) {
        double dx = this.currentPosition.getX() - center.getX();
        double dy = this.currentPosition.getY() - center.getY();
        double norm = Math.sqrt((dx * dx) + (dy * dy));

        if (norm < 1e-6) {
            return new Position(center.getX(), center.getY() + radius, 0);
        }

        double unitX = dx / norm;
        double unitY = dy / norm;
        return new Position(
                center.getX() + (unitX * radius),
                center.getY() + (unitY * radius),
                0
        );
    }

    private int computeRadialCorrection(double distanceToCenter, double radius) {
        double radialError = distanceToCenter - radius;
        if (Math.abs(radialError) <= HOLD_RADIUS_TOLERANCE_NM) {
            return 0;
        }
        double normalizedError = radialError / Math.max(radius, 0.1);
        int correction = (int) Math.round(normalizedError * HOLD_MAX_RADIAL_CORRECTION_DEGREES);
        if (correction > HOLD_MAX_RADIAL_CORRECTION_DEGREES) {
            return HOLD_MAX_RADIAL_CORRECTION_DEGREES;
        }
        if (correction < -HOLD_MAX_RADIAL_CORRECTION_DEGREES) {
            return -HOLD_MAX_RADIAL_CORRECTION_DEGREES;
        }
        return correction;
    }

    // ---------------------------------------------------------------
    //  Persistencia (guardado / carga)
    // ---------------------------------------------------------------

    /**
     * Indica si el vuelo esta actualmente en fase de entrada a un holding.
     *
     * @return true si el vuelo esta maniobrando para entrar en el patron de espera
     */
    public boolean isEnteringHolding() {
        return enteringHolding;
    }

    /**
     * Reconstruye un vuelo a partir de los datos persistidos en una partida guardada.
     * <p>
     * Crea un nuevo {@link Flight}, luego sobrescribe los valores por defecto
     * del constructor con el estado exacto que tenia la partida en el momento
     * del guardado.</p>
     *
     * @param data    los datos planos del vuelo desde el JSON
     * @param airport el aeropuerto de la partida (para resolver referencias como pistas y puntos de espera)
     * @return el {@link Flight} reconstruido, o null si no se pudo resolver el modelo
     */
    public static Flight reconstructFrom(SaveGameData.FlightData data, Airport airport) {
        AircraftModel model = AircraftModelRegistry.get(data.getModelId());
        if (model == null) {
            return null;
        }

        Position pos = new Position(data.getX(), data.getY(), data.getZ());
        Flight flight = new Flight(data.getCallsign(), model, pos, data.getHeading(), data.getSpeed());

        // Acceso directo a campos privados (estamos dentro de la propia clase Flight)
        flight.fuel = data.getFuel();
        flight.status = FlightStatus.valueOf(data.getStatus());
        flight.targetHeading = data.getTargetHeading();
        flight.targetAltitude = data.getTargetAltitude();
        flight.targetSpeed = data.getTargetSpeed();
        flight.enteringHolding = data.isEnteringHolding();
        flight.previousLandingDist = -1;

        // Referencias a objetos dentro del aeropuerto (pueden ser null)
        if (data.getAssignedRunwayId() != null && !data.getAssignedRunwayId().isEmpty()) {
            flight.assignedRunway = airport.findRunway(data.getAssignedRunwayId());
        }
        if (data.getApproachType() != null && !data.getApproachType().isEmpty()) {
            flight.approachType = data.getApproachType();
        }
        if (data.getHoldingPointId() != null && !data.getHoldingPointId().isEmpty()) {
            flight.holdingPoint = airport.findHoldingPoint(data.getHoldingPointId());
        }

        return flight;
    }
}