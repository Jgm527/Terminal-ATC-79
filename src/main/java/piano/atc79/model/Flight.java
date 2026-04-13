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
    private static final int MIN_VERTICAL_SEPARATION = 1000;

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
        if (this.status == FlightStatus.LANDING && assignedRunway != null) {
            landLogic();
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

    public boolean isReadyToLand() {
        if (this.assignedRunway == null) return false;

        double dist = this.getCurrentPosition().distanceTo(assignedRunway.getStartPoint());
        boolean alineado = assignedRunway.isAligned(this);

        // TODO Arreglar la altura (calcular curva VIS/ILS)
        if ("ILS".equals(approachType) && dist < 12.0 && alineado) return true;

        // TODO Arreglar la altura (calcular curva VIS/ILS)
        // si se calcula la curva hay que revisar si engancha o no, y si no engancha mostrarlo, y si engancha se evita el
        //rebote porque ya tiene un Path asignado
        if ("VIS".equals(approachType) && dist < 6.0 && alineado && currentPosition.getZ() <= 3000) return true;

        return false;
    }

    public boolean checkLandingCondition() {
        boolean altitudeOk = currentPosition.getZ() < 1000;
        boolean speedOk = this.speed < 160;
        boolean distanceOK = this.getCurrentPosition().distanceTo(assignedRunway.getStartPoint()) < 0.3;
        boolean approachOK = this.getApproachType().equals("ILS") && assignedRunway.hasILS() || this.getApproachType().equals("VIS");

        return altitudeOk && speedOk && distanceOK && approachOK;
    }

    private void landLogic() {
        if (assignedRunway == null) return;

        double distInicio = currentPosition.distanceTo(assignedRunway.getStartPoint());

        // 1. Si ya estamos en pista, forzamos aterrizaje y detenemos lógica
        if (distInicio < 0.1 || currentPosition.getZ() < 10) {
            this.targetAltitude = 0;
            this.targetSpeed = 0;
            return;
        }

        int altitudSenda = (int) (distInicio * 300);

        // 2. Ejecutar lógica según tipo
        if ("ILS".equals(approachType)) {
            approachILS(distInicio, altitudSenda);
        } else if ("VIS".equals(approachType)) {
            approachVIS(distInicio, altitudSenda);
        } else {
            // Fallback: Si no tiene tipo, forzar descenso suave
            this.targetAltitude = Math.min(this.targetAltitude, altitudSenda);
        }
    }

    private void approachILS(double dist, int altSenda) {
        // Solo verifica alineación, no verifiques la altura actual
        if (dist < 12.0 && assignedRunway.isAligned(this)) {
            // Asigna la senda directamente. El avión bajará (o subirá nivelado)
            // para alcanzarla, tal como dicta la física de updateAltitude.
            this.targetAltitude = altSenda;
            this.targetSpeed = calculateApproachVelocity(dist);
        }
    }

    private void approachVIS(double dist, int altSenda) {
        // Si está alineado y en rango visual, engancha la senda
        if (dist < 6.0 && assignedRunway.isAligned(this)) {
            this.targetAltitude = altSenda;
            this.targetSpeed = calculateApproachVelocity(dist);
        }
    }

    private int calculateApproachVelocity(double dist) {
        if (dist < 0.05) return 0;

        if (dist < 1.0) return model.getMinSpeed();

        if (dist < 4.0) return model.getMinSpeed() + 20;

        return model.getMinSpeed() + 50;
    }

    /**
     * Comprueba si dos vuelos están en conflicto (violando la separación mínima).
     * 
     * @param f el otro {@link Flight}
     * @return true si existe un conflicto, false en caso contrario
     */
    public boolean areInConflict(Flight f) {
        //TODO Arreglar conflictos frontales (No se debe tener en cuenta el wakeIntensity (min separation)
        double horizontalDistance = this.getCurrentPosition().distanceTo(f.getCurrentPosition());
        double verticalDistance = Math.abs(this.getCurrentPosition().getZ() - f.getCurrentPosition().getZ());
        double minHorizontalSeparation = Math.max(this.getModel().getCategory().getMinSeparationNM(),
                f.getModel().getCategory().getMinSeparationNM());

        if (horizontalDistance < minHorizontalSeparation &&
                verticalDistance < MIN_VERTICAL_SEPARATION) {
            return true;
        }
        return false;
    }

}