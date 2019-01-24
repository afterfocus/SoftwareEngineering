package navigator.database;

public class FuelType {
    private int id;
    private double price;
    private String name;

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

    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + price + " руб)";
    }
}
