package piano.atc79.model;

/**
 * Encapsula las reglas de aproximación (ILS/VIS) y guiado final durante la fase de aterrizaje.
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

    private void applyIlsGuidance(Flight flight, double dist, int glideAltitude) {
        // Solo verifica alineación, no la altura actual.
        if (dist < 12.0 && flight.getAssignedRunway().isAligned(flight)) {
            flight.setTargetAltitude(glideAltitude);
            flight.setTargetSpeed(calculateApproachVelocity(flight, dist));
        }
    }

    private void applyVisGuidance(Flight flight, double dist, int glideAltitude) {
        if (dist < 6.0 && flight.getAssignedRunway().isAligned(flight)) {
            flight.setTargetAltitude(glideAltitude);
            flight.setTargetSpeed(calculateApproachVelocity(flight, dist));
        }
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
