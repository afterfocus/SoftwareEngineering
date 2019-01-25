package navigator.database;

public class FuelType {
    private int id;
    private double cost;
    private String name;

   public FuelType(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.cost = price;
    }

    public int getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public String getName() { return name; }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (" + cost + " руб)";
    }
}
