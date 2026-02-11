package piano.atc79.logic;

import piano.atc79.model.Flight;
import piano.atc79.model.Position;

public class PhysicsEngine {
    private static final int CLIMB_RATE = 15;
    private static final int TURNING_RATE = 3;

    public void updatePosition(Flight flight) {
        updateHeading(flight);
        updateLatitude(flight);
        updateAltitude(flight);
    }

    private void updateLatitude(Flight flight) {
        double speedPerSeconds = flight.getSpeed() / 3600.0;
        double rad = Math.toRadians(flight.getHeading());

        double deltaX = speedPerSeconds * Math.sin(rad);
        double deltaY = speedPerSeconds * Math.cos(rad);

        Position pos = flight.getCurrentPosition();
        pos.setX(pos.getX() + deltaX);
        pos.setY(pos.getY() + deltaY);
    }

    private void updateAltitude(Flight flight) {
        int current = flight.getAltitude();
        int target = flight.getTargetAltitude();

        if (current < target) {
            flight.setAltitude(Math.min(current + CLIMB_RATE, target));
        } else if (current > target) {
            flight.setAltitude(Math.max(current - CLIMB_RATE, target));
        }
    }

    private void updateHeading(Flight flight) {
        int current = flight.getHeading();
        int target = flight.getTargetHeading();

        if (current < target) {
            flight.setHeading(Math.min(current + TURNING_RATE, target));
        } else if (current > target) {
            flight.setHeading(Math.max(current - TURNING_RATE, target));
        }

        if (flight.getHeading() >= 360) flight.setHeading(flight.getHeading() - 360);
        if (flight.getHeading() < 0) flight.setHeading(flight.getHeading() + 360);
    }
}
