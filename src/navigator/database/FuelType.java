package navigator.database;

public class FuelType {
    private int id;
    private String name;
    private double price;

    public FuelType(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (" + price + " руб)";
    }
}
