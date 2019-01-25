package navigator.database;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionDB {

    private static Connection connection;
    private static final String NAME_DB = "test";

    /**
     * Соединение с ьазой данных
     *
     * @return ссылка на соединение
     */
    private static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + NAME_DB + ".db");

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

    /**
     * Получить всех водителей
     *
     * @return список водителей
     */
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    /**
     * Редактировать водителя
     *
     * @param driver редактированный водитель
     * @return true, если водитель изменен успешно
     */
    public static boolean updateDriver(Driver driver) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet drivers = stmt.executeQuery("SELECT fio FROM DRIVER");
            if (checkDriversDuplicate(driver, stmt, drivers)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Driver SET FIO = ? WHERE ID = ?");
            statement.setString(1, driver.getFullName());
            statement.setInt(2, driver.getId());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Добавить водителя
     *
     * @param driver водитель для добавления
     * @return true, если водитель добавлен успешно
     */
    public static boolean addDriver(Driver driver) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet drivers = stmt.executeQuery("SELECT fio FROM DRIVER");
            if (checkDriversDuplicate(driver, stmt, drivers)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Driver VALUES (null, ?)");
            statement.setString(1, driver.getFullName());
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверка водителя на наличие дубликата
     * @return true, если дубликат найден
     */
    private static boolean checkDriversDuplicate(Driver driver, Statement stmt, ResultSet drivers) throws SQLException {
        while (drivers.next()) {
            if (driver.getFullName().equals(drivers.getString("fio"))) {
                drivers.close();
                stmt.close();
                return true;
            }
        }
        return false;
    }

    /**
     * Удалить водителя
     *
     * @param id идентификтаор удаляемого водителя
     */
    public static void removeDriver(int id) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Driver WHERE ID = ?");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Получить все автомобили
     *
     * @return список автомобилей
     */
    public static ArrayList<Car> selectAllFromCar() {

        ArrayList<Car> cars = new ArrayList<>();
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Car");
            while (rs.next()) {
                cars.add(new Car(rs.getInt("id"),
                        rs.getString("model"),
                        rs.getInt("max_speed"),
                        getFuelById(rs.getInt("fuel")),
                        rs.getDouble("consumption")
                ));
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    /**
     * Добавить автомобиль
     * @param car автомобиль для добавления
     */
    public static void addCar(Car car) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Car VALUES (null, ?, ?, ?, ?)");
            statement.setString(1, car.getModel());
            statement.setInt(2, car.getFuel().getId());
            statement.setDouble(3, car.getFuelConsumption());
            statement.setInt(4, car.getMaxSpeed());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Редактировать автомобиль
     *
     * @param car редактированный автомобиль
     */
    public static void updateCar(Car car) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Car SET Model = ?, Max_Speed = ?, Fuel = ?, Consumption = ? WHERE ID = ?");
            statement.setString(1, car.getModel());
            statement.setInt(2, car.getMaxSpeed());
            statement.setInt(3, car.getFuel().getId());
            statement.setDouble(4, car.getFuelConsumption());
            statement.setInt(5, car.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удалить автомобиль
     *
     * @param id идентификатор удаляемого автомобиля
     */
    public static void removeCar(int id) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Car WHERE ID = ?");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить все типы топлива
     *
     * @return список типов топлива
     */
    public static ArrayList<FuelType> selectAllFromFuel() {

        ArrayList<FuelType> fuels = new ArrayList<>();
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fuels;
    }

    /**
     * Получить топливо по идентификатору
     * @param id идентификатор топлива
     * @return топливо
     */
    public static FuelType getFuelById(int id) {
        FuelType fuelType = null;
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM Fuel WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            fuelType = rs.next() ? new FuelType(rs.getInt("id"), rs.getString("type"), rs.getDouble("cost")) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fuelType;
    }

    /**
     * Добавить топливо
     * @param fuel топливо для добавления
     */
    public static void addFuel(FuelType fuel) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Fuel VALUES (null, ?, ?)");
            statement.setString(1, fuel.getName());
            statement.setDouble(2, fuel.getCost());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Редактировать топливо
     *
     * @param fuel редактированное топливо
     */
    public static void updateFuel(FuelType fuel) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Fuel SET Type = ?, Cost = ? WHERE ID = ?");
            statement.setString(1, fuel.getName());
            statement.setDouble(2, fuel.getCost());
            statement.setInt(3, fuel.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удалить топливо
     *
     * @param id идентификатор удаляемого топлива
     */
    public static void removeFuel(int id) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Fuel WHERE ID = ?");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить все дорожные покрытия
     * @return список покрытий
     */
    public static ArrayList<RoadSurface> selectAllFromSurface() {

        ArrayList<RoadSurface> surfaces = new ArrayList<>();
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return surfaces;
    }

    /**
     * Получить все названия улиц
     * @return список названий
     */
    public static ArrayList<String> selectAllFromStreet() {

        ArrayList<String> streets = new ArrayList<>();
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Street");
            while (rs.next()) {
                streets.add(rs.getString("name"));
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return streets;
    }

    /**
     * Добавить название улицы
     * @param streetName название для добавления
     */
    public static void addStreet(String streetName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Street VALUES (?)");
            statement.setString(1, streetName);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Редактировать название улицы
     *
     * @param oldName старое название
     * @param newName новое название
     */
    public static void updateStreet(String oldName, String newName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Street SET Name = ? WHERE Name = ?");
            statement.setString(1, newName);
            statement.setString(2, oldName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удалить название улицы
     *
     * @param streetName удаляемое название
     */
    public static void removeStreet(String streetName) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Street WHERE Name = ?");
            statement.setString(1, streetName);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
