package navigator.database;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionDB {

    private static Connection connection;
    private static final String nameDB = "test";

    private static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + nameDB + ".db");

                System.out.println("DONE " + connection);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(e);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        return connection;
    }

    public static void updateDriver(Driver driver) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Driver SET FIO = ? WHERE ID = ?");
            statement.setString(1, driver.getFullName());
            statement.setInt(2, driver.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addDriver(Driver driver) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Driver VALUES (null, ?)");
        statement.setString(1, driver.getFullName());
        statement.execute();
        statement.close();
    }

    public static void removeDriver(int id) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Driver WHERE ID = ?");
        statement.setInt(1, id);
        statement.execute();
        statement.close();
    }


    public static ArrayList<Car> selectAllFromCar() {

        ArrayList<Car> cars = new ArrayList<>();

        try{
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Car");

        while (rs.next()) {
            cars.add(new Car(rs.getInt("id"),
                    rs.getString("model"),
                    rs.getInt("max_speed"),
                    new FuelType(rs.getInt("fuel"), "AI-95", 46.5),
                    rs.getDouble("consumption")
            ));
        }
        stmt.close();
        rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public static ArrayList<Driver> selectAllFromDriver() {

        ArrayList<Driver> drivers = new ArrayList<>();
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Driver");
            while (rs.next()) {
                drivers.add(new Driver(rs.getInt("id"),
                        rs.getString("fio")
                ));
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public static ArrayList<FuelType> selectAllFromFuel() throws SQLException {

        ArrayList<FuelType> fuels = new ArrayList<>();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Fuel");
        while (rs.next()) {
            fuels.add(new FuelType(rs.getInt("id"),
                    rs.getString("type"),
                    rs.getDouble("cost")
            ));
        }
        stmt.close();
        rs.close();
        return fuels;
    }

    public static ArrayList<String> selectAllFromStreet() throws SQLException {

        ArrayList<String> streets = new ArrayList<>();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Street");
        while (rs.next()) {
            streets.add(rs.getString("name"));
        }
        stmt.close();
        rs.close();
        return streets;
    }

    public static ArrayList<RoadSurface> selectAllFromSurface() throws SQLException {

        ArrayList<RoadSurface> surfaces = new ArrayList<>();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Surface");
        while (rs.next()) {
            surfaces.add(new RoadSurface(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("coefficient")
            ));
        }
        stmt.close();
        rs.close();
        return surfaces;
    }

    /*
    public Car getCarByID(String nameTable, String parametrsQuery, int driverID) {
        String sql = "SELECT " + parametrsQuery + " FROM " + nameTable + "WHERE driver_id =" + driverID;
        Car car1 = new Car();
        try {

            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Car car = new Car(rs.getInt("id"),
                    rs.getString("model"),
                    rs.getInt("max_speed"),
                    new FuelType(rs.getInt("fuel"), "AI-95", 46.5),
                    rs.getDouble("consumption")
            );
            return car;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return car1;
    }*/


//    public Car selectAllFromCar(String nameTable, String parametrsQuery) {
//        String sql = "SELECT " + parametrsQuery + " FROM " + nameTable;
//        Car car1=new Car();
//        try (Connection conn = this.connectionSQLite("test");
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            // loop through the result set
//            while (rs.next()) {
//                Car car = new Car(rs.getInt("id"),
//                        rs.getString("model"),
//                        rs.getInt("max_speed"),
//                        new Fuel(rs.getInt("fuel"), "AI-95", 46.5),
//                        rs.getDouble("consumption")
//                );
//                return  car;
//            }
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return car1;
//    }


    public static void createTable() {
        try {
            Statement stmt = getConnection().createStatement();
            String sql = "CREATE TABLE FUEL (\n" +
                    "    id   INT          PRIMARY KEY\n" +
                    "                      NOT NULL,\n" +
                    "    type VARCHAR (30),\n" +
                    "    cost DOUBLE (20) \n" +
                    ");";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public static void insertSQL() {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            con.setAutoCommit(false);
            String sql = "INSERT INTO Fuel (id,type,cost)" +
                    "VALUES (3,'АИ-92',40.5);";
            stmt.executeUpdate(sql);
            stmt.close();
            con.commit();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }

}
