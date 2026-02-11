package piano.atc79.logic;

import piano.atc79.model.Flight;

public class ConflictDetector {
    private static final int MIN_VERTICAL_SEPARATION = 1000;

    public boolean areInConflict(Flight f1, Flight f2) {
        double horizontalDistance = f1.getCurrentPosition().distanceTo(f2.getCurrentPosition());
        double verticalDistance = Math.abs(f1.getAltitude() - f2.getAltitude());
        double minHorizontalSeparation = Math.max(f1.getModel().getCategory().getMinSeparationNM(),
                f2.getModel().getCategory().getMinSeparationNM());

        if (horizontalDistance < minHorizontalSeparation &&
            verticalDistance < MIN_VERTICAL_SEPARATION) {
            return true;
        }
        return false;
    }
}
