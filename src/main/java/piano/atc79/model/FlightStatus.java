package piano.atc79.model;

/**
 * Enumeración que indica el estado actual de un vuelo en el juego.
 */
public enum FlightStatus {
    EN_ROUTE(true),
    HOLDING(true),
    ILS_APPROACH(true),
    VIS_APPROACH(true),
    LANDING(false),
    EMERGENCY(true),
    CRASHED(false);

    private final boolean canReceiveOrders;

    /**
     * Constructor para FlightStatus.
     * 
     * @param canReceiveOrders true si el avión puede recibir órdenes en este estado, false en caso contrario
     */
    FlightStatus(boolean canReceiveOrders) {
        this.canReceiveOrders = canReceiveOrders;
    }

    /**
     * Comprueba si una aeronave con este estado puede recibir órdenes actualmente.
     * 
     * @return true si puede recibir órdenes, false en caso contrario
     */
    public boolean isCanReceiveOrders() {
        return canReceiveOrders;
    }
}
