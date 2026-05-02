package piano.atc79.model;

/**
 * Reglas de separación horizontal/vertical entre pares de aeronaves.
 */
public class SeparationRules {
    private static final int MIN_VERTICAL_SEPARATION = 1000;
    private static final double COLLISION_HORIZONTAL_THRESHOLD_NM = 0.05;
    private static final int COLLISION_VERTICAL_THRESHOLD_FT = 100;
    private static final int FRONTAL_HEADING_DELTA_DEGREES = 20;

    /**
     * Comprueba si dos vuelos están en conflicto (violando separación mínima).
     *
     * @param first primer vuelo
     * @param second segundo vuelo
     * @return true si existe conflicto de separación
     */
    public boolean areInConflict(Flight first, Flight second) {
        double horizontalDistance = first.getCurrentPosition().distanceTo(second.getCurrentPosition());
        double verticalDistance = Math.abs(first.getCurrentPosition().getZ() - second.getCurrentPosition().getZ());
        double minHorizontalSeparation = getMinHorizontalSeparation(first, second);

        return horizontalDistance < minHorizontalSeparation && verticalDistance < MIN_VERTICAL_SEPARATION;
    }

    /**
     * Determina si dos vuelos han entrado en colisión real.
     *
     * <p>La colisión requiere proximidad extrema (horizontal y vertical) y
     * configuración frontal de rumbos para diferenciarla del aviso TCAS.</p>
     *
     * @param first primer vuelo
     * @param second segundo vuelo
     * @return true si se considera colisión
     */
    public boolean areInCollision(Flight first, Flight second) {
        double horizontalDistance = first.getCurrentPosition().distanceTo(second.getCurrentPosition());
        double verticalDistance = Math.abs(first.getCurrentPosition().getZ() - second.getCurrentPosition().getZ());
        return horizontalDistance <= COLLISION_HORIZONTAL_THRESHOLD_NM
                && verticalDistance <= COLLISION_VERTICAL_THRESHOLD_FT
                && isFrontalEncounter(first, second);
    }

    private double getMinHorizontalSeparation(Flight first, Flight second) {
        int angleDiff = Math.abs(first.getHeading() - second.getHeading());
        boolean isEncounter = angleDiff > 90 && angleDiff < 270;

        if (isEncounter) {
            return 1.0;
        }

        return Math.max(
                first.getModel().getCategory().getMinSeparationNM(),
                second.getModel().getCategory().getMinSeparationNM()
        );
    }

    private boolean isFrontalEncounter(Flight first, Flight second) {
        int angleDiff = Math.abs(first.getHeading() - second.getHeading()) % 360;
        int normalized = Math.min(angleDiff, 360 - angleDiff);
        return Math.abs(180 - normalized) <= FRONTAL_HEADING_DELTA_DEGREES;
    }
}
