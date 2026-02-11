package piano.atc79.logic;

import piano.atc79.model.Flight;

public class FuelManager {
    public void updateFuel(Flight flight) {
        double consumptionPerSecond = flight.getModel().getFuelConsumption() / 3600.0;

        double newFuel = flight.getFuel() - consumptionPerSecond;

        if (newFuel < 0) { newFuel = 0; }

        flight.setFuel(newFuel);
    }
}