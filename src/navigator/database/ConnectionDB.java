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

            } catch (Exception e) {
                e.printStackTrace();
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
     * Получить водителя по идентификатору
     * @param id идентификатор водителя
     * @return водитель
     */
    private static Driver getDriverById(int id) {
        Driver driver = null;
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM Driver WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            driver = rs.next() ? new Driver(rs.getInt("id"), rs.getString("fio")) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driver;
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

            statement = getConnection().prepareStatement("DELETE FROM Car_Driver WHERE driver_id = ?");
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
     * Получить автомобиль по идентификатору
     * @param id идентификатор автомобиля
     * @return автомобиль
     */
    private static Car getCarById(int id) {
        Car car = null;
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM Car WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            car = rs.next() ? new Car(rs.getInt("id"),
                    rs.getString("model"),
                    rs.getInt("max_speed"),
                    getFuelById(rs.getInt("fuel")),
                    rs.getDouble("consumption")) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return car;
    }

    /**
     * Добавить автомобиль
     * @param car автомобиль для добавления
     */
    public static boolean addCar(Car car) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet cars = stmt.executeQuery("SELECT model FROM CAR");
            if (checkCarDuplicate(car, stmt, cars)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Car VALUES (null, ?, ?, ?, ?)");
            statement.setString(1, car.getModel());
            statement.setInt(2, car.getFuel().getId());
            statement.setDouble(3, car.getFuelConsumption());
            statement.setInt(4, car.getMaxSpeed());
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Редактировать автомобиль
     *
     * @param car редактированный автомобиль
     */
    public static boolean updateCar(Car car) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet cars = stmt.executeQuery("SELECT model FROM CAR");
            if (checkCarDuplicate(car, stmt, cars)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Car SET Model = ?, Max_Speed = ?, Fuel = ?, Consumption = ? WHERE ID = ?");
            statement.setString(1, car.getModel());
            statement.setInt(2, car.getMaxSpeed());
            statement.setInt(3, car.getFuel().getId());
            statement.setDouble(4, car.getFuelConsumption());
            statement.setInt(5, car.getId());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

            statement = getConnection().prepareStatement("DELETE FROM Car_Driver WHERE car_id = ?");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка автомобиля на наличие дубликата
     * @return true, если дубликат найден
     */
    private static boolean checkCarDuplicate(Car car, Statement stmt, ResultSet cars) throws SQLException {
        while (cars.next()) {
            if (car.getModel().equals(cars.getString("model"))) {
                cars.close();
                stmt.close();
                return true;
            }
        }
        return false;
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
    private static FuelType getFuelById(int id) {
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
    public static boolean addFuel(FuelType fuel) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet fuels = stmt.executeQuery("SELECT type FROM Fuel");
            if (checkFuelDuplicate(fuel, stmt, fuels)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Fuel VALUES (null, ?, ?)");
            statement.setString(1, fuel.getName());
            statement.setDouble(2, fuel.getCost());
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Редактировать топливо
     *
     * @param fuel редактированное топливо
     */
    public static boolean updateFuel(FuelType fuel) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet fuels = stmt.executeQuery("SELECT type FROM Fuel");
            if (checkFuelDuplicate(fuel, stmt, fuels)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Fuel SET Type = ?, Cost = ? WHERE ID = ?");
            statement.setString(1, fuel.getName());
            statement.setDouble(2, fuel.getCost());
            statement.setInt(3, fuel.getId());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    private static boolean checkFuelDuplicate(FuelType fuel, Statement stmt, ResultSet fuels) throws SQLException {
        while (fuels.next()) {
            if (fuel.getName().equals(fuels.getString("type"))) {
                fuels.close();
                stmt.close();
                return true;
            }
        }
        return false;
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

    public static boolean addSurface(RoadSurface roadSurface) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet surfaces = stmt.executeQuery("SELECT name FROM Surface");
            if (checkSurfaceDuplicate(roadSurface, stmt, surfaces)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Surface VALUES (null, ?, ?)");
            statement.setString(1, roadSurface.getName());
            statement.setDouble(2, roadSurface.getCoefficient());
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateSurface(RoadSurface roadSurface) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet surfaces = stmt.executeQuery("SELECT name FROM Surface");
            if (checkSurfaceDuplicate(roadSurface, stmt, surfaces)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Surface SET Name = ?, Coefficient = ? WHERE ID = ?");
            statement.setString(1, roadSurface.getName());
            statement.setDouble(2, roadSurface.getCoefficient());
            statement.setInt(3, roadSurface.getId());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeSurface(int id) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Surface WHERE ID = ?");
            statement.setInt(1, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkSurfaceDuplicate(RoadSurface surface, Statement stmt, ResultSet surfaces) throws SQLException {
        while (surfaces.next()) {
            if (surface.getName().equals(surfaces.getString("name"))) {
                surfaces.close();
                stmt.close();
                return true;
            }
        }
        return false;
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
    public static boolean addStreet(String streetName) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet names = stmt.executeQuery("SELECT name FROM Street");
            if (checkStreetDuplicate(streetName, stmt, names)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Street VALUES (?)");
            statement.setString(1, streetName);
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Редактировать название улицы
     *
     * @param oldName старое название
     * @param newName новое название
     */
    public static boolean updateStreet(String oldName, String newName) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet names = stmt.executeQuery("SELECT name FROM Street");
            if (checkStreetDuplicate(newName, stmt, names)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Street SET Name = ? WHERE Name = ?");
            statement.setString(1, newName);
            statement.setString(2, oldName);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    private static boolean checkStreetDuplicate(String name, Statement stmt, ResultSet names) throws SQLException {
        while (names.next()) {
            if (name.equals(names.getString("name"))) {
                names.close();
                stmt.close();
                return true;
            }
        }
        return false;
    }

    /**
     * Получить все связи водитель-автомобиль
     * @return список связей водитель-автомобиль
     */
    public static ArrayList<DriverCar> selectAllFromDriverCar() {

        ArrayList<DriverCar> driverCars = new ArrayList<>();
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Car_Driver");
            while (rs.next()) {
                driverCars.add(new DriverCar(
                        getDriverById(rs.getInt("driver_id")),
                        getCarById(rs.getInt("car_id"))));
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driverCars;
    }

    public static boolean addDriverCar(int driverID, int carID) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet driverCars = stmt.executeQuery("SELECT * FROM Car_Driver");
            if (checkDriverCarDuplicate(driverID, carID, stmt, driverCars)) return false;
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Car_Driver VALUES (?, ?)");
            statement.setInt(1, carID);
            statement.setInt(2, driverID);
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean updateDriverCar(int oldDriverID, int oldCarID, int newDriverID, int newCarID) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet driverCars = stmt.executeQuery("SELECT * FROM Car_Driver");
            if (checkDriverCarDuplicate(newDriverID, newCarID, stmt, driverCars)) return false;
            PreparedStatement statement = getConnection().prepareStatement("UPDATE Car_Driver SET Car_id = ?, Driver_id = ? WHERE Car_id = ? AND Driver_id = ?");
            statement.setInt(1, newCarID);
            statement.setInt(2, newDriverID);
            statement.setInt(3, oldCarID);
            statement.setInt(4, oldDriverID);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkDriverCarDuplicate(int driverID, int carID, Statement stmt, ResultSet driverCars) throws SQLException {
        while (driverCars.next()) {
            if (driverID == driverCars.getInt("Driver_id") && carID == driverCars.getInt("Car_id")) {
                driverCars.close();
                stmt.close();
                return true;
            }
        }
        return false;
    }

    public static void removeDriverCar(int driverID, int carID) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Car_Driver WHERE Car_id = ? AND Driver_id = ?");
            statement.setInt(1, driverID);
            statement.setInt(2, carID);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
