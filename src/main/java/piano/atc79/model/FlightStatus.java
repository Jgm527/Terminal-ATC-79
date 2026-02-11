package piano.atc79.model;

public enum FlightStatus {
    EN_ROUTE(true),
    HOLDING(true),
    APPROACH(true),
    LANDING(false),
    EMERGENCY(true),
    CRASHED(false);

    private final boolean canReceiveOrders;

    FlightStatus(boolean canReceiveOrders) {
        this.canReceiveOrders = canReceiveOrders;
    }

    public boolean isCanReceiveOrders() {
        return canReceiveOrders;
    }
}
