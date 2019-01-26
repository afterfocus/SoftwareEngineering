package navigator.database;

public class DriverCar {
    private Driver driver;
    private Car car;

    public DriverCar(Driver driver, Car car) {
        this.driver = driver;
        this.car = car;
    }

    public Driver getDriver() {
        return driver;
    }

    public Car getCar() {
        return car;
    }
}
