package navigator.database;

/**
 * Хранит информацию об автомобилях
 */
public class Car {

    private int id;
    private String model;
    private int maxSpeed;
    private FuelType fuel;
    //Расход топлива для средней скорости движения в 40 км/ч (городские условия)
    private double fuelConsumption;

    public Car() {
    }

    public Car(int id, String model, int maxSpeed, FuelType fuel, double fuelConsumption) {
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.fuel = fuel;
        this.fuelConsumption = fuelConsumption;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public FuelType getFuel() {
        return fuel;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getFuelConsumption(int atSpeed) {
        return 0.7 * fuelConsumption + 0.3 * fuelConsumption * (40 / atSpeed);
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setFuel(FuelType fuel) {
        this.fuel = fuel;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    @Override
    public String toString() {
        return model;
    }
}
