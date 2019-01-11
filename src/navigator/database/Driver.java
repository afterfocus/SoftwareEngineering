package navigator.database;

public class Driver {
    private int id;
    private String fullName;

    Driver(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
