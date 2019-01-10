package navigator.database;

public class Car {
    private int id;
    private String model;
    private int maxSpeed;
    private int fuelId;
    private double fuelConsumption;

    public Car(int id, String model, int maxSpeed, int fuelId, double fuelConsumption) {
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.fuelId = fuelId;
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

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getFuelId() {
        return fuelId;
    }

    public void setFuelId(int fuelId) {
        this.fuelId = fuelId;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(int fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }
}
