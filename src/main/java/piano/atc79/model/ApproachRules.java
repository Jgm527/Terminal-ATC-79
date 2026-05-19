package piano.atc79.model;

/**
 * Encapsula las reglas de aproximación (ILS/VIS) y guiado final durante la fase de aterrizaje.
 * Durante la aproximación final, tambien ajusta el rumbo del avion hacia el umbral
 * de la pista (simulando un localizer ILS) para evitar que el avion pase de largo.
 */
public class ApproachRules {

    /**
     * Determina si un vuelo puede entrar en fase LANDING con su configuración actual.
     *
     * @param flight vuelo a evaluar
     * @return true si puede iniciar aterrizaje
     */
    public boolean isReadyToLand(Flight flight) {
        if (flight.getAssignedRunway() == null) {
            return false;
        }

        double dist = flight.getCurrentPosition().distanceTo(flight.getAssignedRunway().getStartPoint());
        boolean aligned = flight.getAssignedRunway().isAligned(flight);
        int glideAltitude = (int) (dist * 300);
        int currentAltitude = flight.getCurrentPosition().getZ();

        if ("ILS".equals(flight.getApproachType()) && dist < 12.0 && aligned) {
            if (currentAltitude <= glideAltitude + 150) {
                return true;
            }
        }

        if ("VIS".equals(flight.getApproachType()) && dist < 6.0 && aligned && flight.getCurrentPosition().getZ() <= 3000) {
            if (currentAltitude <= 3000 && Math.abs(currentAltitude - glideAltitude) < 500) {
                return true;
            }
        }

        return false;
    }

    /**
     * Aplica la lógica de guiado durante LANDING según el tipo de aproximación actual.
     * Incluye guiado lateral para que el avion se dirija al umbral de la pista.
     *
     * @param flight vuelo en fase de aterrizaje
     */
    public void applyLandingGuidance(Flight flight) {
        Runway assignedRunway = flight.getAssignedRunway();
        if (assignedRunway == null) {
            return;
        }

        double distToThreshold = flight.getCurrentPosition().distanceTo(assignedRunway.getStartPoint());

        // Si ya está sobre pista (o prácticamente tocando), forzamos parada.
        if (distToThreshold < 0.1 || flight.getCurrentPosition().getZ() < 10) {
            flight.setTargetAltitude(0);
            flight.setTargetSpeed(0);
            return;
        }

        int glideAltitude = (int) (distToThreshold * 300);

        if ("ILS".equals(flight.getApproachType())) {
            applyIlsGuidance(flight, distToThreshold, glideAltitude);
        } else if ("VIS".equals(flight.getApproachType())) {
            applyVisGuidance(flight, distToThreshold, glideAltitude);
        } else {
            // Fallback: sin tipo de aproximación, mantener descenso suave hacia senda.
            flight.setTargetAltitude(Math.min(flight.getTargetAltitude(), glideAltitude));
        }
    }

    /**
     * Guiado ILS: ajusta altitud, velocidad y rumbo para seguir el localizer
     * y la senda de planeo hacia el umbral de la pista.
     */
    private void applyIlsGuidance(Flight flight, double dist, int glideAltitude) {
        flight.setTargetAltitude(glideAltitude);
        flight.setTargetSpeed(calculateApproachVelocity(flight, dist));
        // Simular localizer ILS: rumbo continuo hacia el umbral
        flight.setTargetHeading(calculateHeadingToThreshold(
                flight.getCurrentPosition(),
                flight.getAssignedRunway().getStartPoint()
        ));
    }

    /**
     * Guiado visual: ajusta altitud, velocidad y rumbo hacia el umbral.
     */
    private void applyVisGuidance(Flight flight, double dist, int glideAltitude) {
        flight.setTargetAltitude(glideAltitude);
        flight.setTargetSpeed(calculateApproachVelocity(flight, dist));
        // Guiado visual: mantener rumbo hacia el umbral
        flight.setTargetHeading(calculateHeadingToThreshold(
                flight.getCurrentPosition(),
                flight.getAssignedRunway().getStartPoint()
        ));
    }

    /**
     * Calcula el rumbo magnetico desde una posicion origen hasta un destino,
     * usando la convencion aeronautica (0° = norte, 90° = este).
     *
     * @param from posicion de origen
     * @param to   posicion de destino
     * @return rumbo en grados (0-359)
     */
    private static int calculateHeadingToThreshold(Position from, Position to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        int heading = (int) Math.round(Math.toDegrees(Math.atan2(dx, dy)));
        return (heading + 360) % 360;
    }

    private int calculateApproachVelocity(Flight flight, double dist) {
        if (dist < 0.05) {
            return 0;
        }
        if (dist < 1.0) {
            return flight.getModel().getMinSpeed();
        }
        if (dist < 4.0) {
            return flight.getModel().getMinSpeed() + 20;
        }
        return flight.getModel().getMinSpeed() + 50;
    }
}
