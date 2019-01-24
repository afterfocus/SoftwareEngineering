package navigator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import navigator.database.Car;
import navigator.database.ConnectionDB;
import navigator.database.FuelType;
import navigator.database.FuelType;

import java.sql.SQLException;
import java.util.ArrayList;

public class CarController {

    private ObservableList<Car> carsData = FXCollections.observableArrayList();

    @FXML
    private TableView<Car> carTableView;

    @FXML
    private TableColumn<Car, Integer> idColumn;

    @FXML
    private TableColumn<Car, String> modelColumn;

    @FXML
    private TableColumn<Car, Integer> maxSpeed;

    @FXML
    private TableColumn<Car, FuelType> fuel;

    @FXML
    private TableColumn<Car, Double> consumption;

    // инициализируем форму данными
    @FXML
    private void initialize() throws SQLException {
        initData();

//         //устанавливаем тип и значение которое должно хранится в колонке
//        idColumn.setCellValueFactory(new PropertyValueFactory<Car, Integer>("id"));
//        modelColumn.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
//        maxSpeed.setCellValueFactory(new PropertyValueFactory<Car, Integer>("maxSpeed"));
//        fuel.setCellValueFactory(new PropertyValueFactory<Car, Fuel>("fuel"));
//        consumption.setCellValueFactory(new PropertyValueFactory<Car, Double>("fuelConsumption"));

        TableColumn<Car, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Car, String> model = new TableColumn<>("model");
        TableColumn<Car, Integer> maxSpeed = new TableColumn<>("maxSpeed");
        TableColumn<Car, FuelType> fuel = new TableColumn<>("fuel");
        TableColumn<Car, Double> consumption = new TableColumn<>("fuelConsumption");

        // заполняем таблицу данными
        carTableView.setItems(carsData);
       // employeeTableView.setItems(FXCollections.observableList(DataBases.getEmployees()));
        //carTableView.getColumns().addAll(idColumn, model, maxSpeed, fuel,consumption);
    }

    // подготавливаем данные для таблицы
    // вы можете получать их с базы данных
//    private void initData() {
//        carsData.add(new Car(1, "Lada",111 ,new Fuel(1,"AI-95",44.5), 7));
//        carsData.add(new Car(2, "Aston",333 ,new Fuel(1,"AI-95",44.5), 76));
//        carsData.add(new Car(3, "Mazda",122, new Fuel(1,"AI-95",44.5), 7));
//        carsData.add(new Car(4, "Lexus",222 ,new Fuel(1,"AI-95",44.5), 7));
//        carsData.add(new Car(5, "Opel",121 ,new Fuel(1,"AI-95",44.5), 7));
//    }

    private void initData() throws SQLException {
        ConnectionDB con = new ConnectionDB();
        ArrayList<Car> cars = con.selectAllFromCar("Car", "*");
        for (Car car : cars
        ) {
           carsData.add(car);
        }
    }

}

