package navigator.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import navigator.database.*;
import java.util.Optional;

public class TablesController {
    @FXML
    private TableView<Driver> driverTableView;
    @FXML
    private TableView<Car> carTableView;
    @FXML
    private TableView<FuelType> fuelTableView;
    @FXML
    private TableView<String> streetTableView;
    @FXML
    private TableView<RoadSurface> surfaceTableView;
    @FXML
    private TableView<DriverCar> driverCarTableView;
    @FXML
    private Button addDriverButton;
    @FXML
    private Button deleteDriverButton;
    @FXML
    private Button addCarButton;
    @FXML
    private Button deleteCarButton;
    @FXML
    private Button addFuelButton;
    @FXML
    private Button deleteFuelButton;
    @FXML
    private Button addStreetButton;
    @FXML
    private Button deleteStreetButton;
    @FXML
    private Button addSurfaceButton;
    @FXML
    private Button deleteSurfaceButton;
    @FXML
    private Button addDriverCarButton;
    @FXML
    private Button deleteDriverCarButton;

    private TableColumn<Car, FuelType> fuelColumn = new TableColumn<>("Тип топлива");
    private TableColumn<DriverCar, Driver> driverColumn = new TableColumn<>("Водитель");
    private TableColumn<DriverCar, Car> carColumn = new TableColumn<>("Автомобиль");

    public void initialize() {
        initializeDriverTable();
        initializeDriverButtons();
        initializeCarTable();
        initializeCarButtons();
        initializeFuelTable();
        initializeFuelButtons();
        initializeSurfaceTable();
        initializeSurfaceButtons();
        initializeStreetTable();
        initializeStreetButtons();
        initializeDriverCarTable();
        initializeDriverCarButtons();
    }

    /**
     * Таблица водителей
     */
    @SuppressWarnings("unchecked")
    private void initializeDriverTable() {
        TableColumn<Driver, String> fullName = new TableColumn<>("ФИО");

        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullName.setCellFactory(TextFieldTableCell.forTableColumn());
        fullName.setMinWidth(200);

        fullName.setOnEditCommit((TableColumn.CellEditEvent<Driver, String> event) -> {
            Driver driver = event.getTableView().getItems().get(event.getTablePosition().getRow());
            String newFullName = event.getNewValue();
            driver.setFullName(newFullName);
            if(!ConnectionDB.updateDriver(driver)){
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования","Водитель с такими данными уже существует!");
                alertError.showAndWait();
            }
            driverTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriver()));
            driverColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromDriver())));
            driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
        });
        driverTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriver()));
        driverTableView.getColumns().addAll(getIndexColumn(driverTableView), fullName);
    }

    /**
     * Кнопки таблицы водителей
     */
    private void initializeDriverButtons() {
        addDriverButton.setOnAction(actionEvent -> {
            Optional<Driver> result = DialogsController.getAddDriverDialog().showAndWait();
            result.ifPresent(list -> {
                if(ConnectionDB.addDriver(result.get())) {
                    driverTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriver()));
                    driverColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromDriver())));
                }
                else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления водителя","Водитель с такими данными уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteDriverButton.setOnAction(actionEvent -> {
            int row = driverTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить водителя?", "Водитель будет безвозвратно удален.");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeDriver(driverTableView.getItems().get(row).getId());
                    driverTableView.getItems().remove(row);
                    driverColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromDriver())));
                    driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
                }
            }
        });
    }

    /**
     * Таблица автомобилей
     */
    @SuppressWarnings("unchecked")
    private void initializeCarTable() {
        TableColumn<Car, String> model = new TableColumn<>("Модель");
        TableColumn<Car, Integer> maxSpeed = new TableColumn<>("Максимальная скорость");
        TableColumn<Car, Double> consumption = new TableColumn<>("Расход топлива");

        model.setCellValueFactory(new PropertyValueFactory<>("model"));
        model.setCellFactory(TextFieldTableCell.forTableColumn());
        model.setMinWidth(100);
        model.setOnEditCommit((TableColumn.CellEditEvent<Car, String> event) -> {
            Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
            String newModel = event.getNewValue();
            car.setModel(newModel);
            if (!ConnectionDB.updateCar(car)) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования автомобиля","Автомобиль такой модели уже существует!");
                alertError.showAndWait();
            }
            carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            carColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromCar())));
            driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
        });

        maxSpeed.setCellValueFactory(new PropertyValueFactory<>("maxSpeed"));
        maxSpeed.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        maxSpeed.setMinWidth(130);
        maxSpeed.setOnEditCommit((TableColumn.CellEditEvent<Car, Integer> event) -> {
            int newMaxSpeed = event.getNewValue();
            if (newMaxSpeed >= 20 && newMaxSpeed <= 300) {
                Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
                car.setMaxSpeed(newMaxSpeed);
                ConnectionDB.updateCar(car);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования автомобиля", "Введено некорректное значение максимальной скорости.");
                alert.showAndWait();
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            }
        });

        fuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        fuelColumn.setMinWidth(120);
        fuelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromFuel())));
        fuelColumn.setOnEditCommit((TableColumn.CellEditEvent<Car, FuelType> event) -> {
            Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
            car.setFuel(event.getNewValue());
            ConnectionDB.updateCar(car);
        });

        consumption.setCellValueFactory(new PropertyValueFactory<>("fuelConsumption"));
        consumption.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        consumption.setMinWidth(100);
        consumption.setOnEditCommit((TableColumn.CellEditEvent<Car, Double> event) -> {
            double newConsumption = event.getNewValue();
            Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (newConsumption >= 3 && newConsumption <= 50) {
                car.setFuelConsumption(newConsumption);
                ConnectionDB.updateCar(car);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования автомобиля", "Введено некорректное значение расхода топлива.");
                alert.showAndWait();
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            }
        });
        carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
        carTableView.getColumns().addAll(getIndexColumn(carTableView), model, maxSpeed, fuelColumn, consumption);
    }

    /**
     * Кнопки таблицы автомобилей
     */
    private void initializeCarButtons() {
        addCarButton.setOnAction(actionEvent -> {
            Optional<Car> result = DialogsController.getAddCarDialog().showAndWait();
            result.ifPresent(list -> {
                if (ConnectionDB.addCar(result.get())) {
                    carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
                    carColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromCar())));
                } else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления автомобиля","Автомобиль такой модели уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteCarButton.setOnAction(actionEvent -> {
            int row = carTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить автомобиль?", "Автомобиль будет безвозвратно удалён.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeCar(carTableView.getItems().get(row).getId());
                    carTableView.getItems().remove(row);
                    carColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromCar())));
                    driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
                }
            }
        });
    }

    /**
     * Таблица названий улиц
     */
    @SuppressWarnings("unchecked")
    private void initializeStreetTable() {
        TableColumn<String, String> nameColumn = new TableColumn<>("Название");

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setMinWidth(100);
        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<String, String> event) -> {
            String newName = event.getNewValue();
            String oldName = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (!ConnectionDB.updateStreet(oldName, newName)) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования названия улицы","Такое название уже существует!");
                alertError.showAndWait();
            }
            streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));
        });
        streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));
        streetTableView.getColumns().addAll(getIndexColumn(streetTableView), nameColumn);
    }

    /**
     * Кнопки таблицы названий улиц
     */
    private void initializeStreetButtons() {
        addStreetButton.setOnAction(actionEvent -> {
            Optional<String> result = DialogsController.getAddStreetDialog().showAndWait();
            result.ifPresent(list -> {
                if(ConnectionDB.addStreet(result.get()))
                    streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));
                else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления названия улицы","Такое название уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteStreetButton.setOnAction(actionEvent -> {
            int row = streetTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить название улицы?", "Название будет безвозвратно удалено.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeStreet(streetTableView.getItems().get(row));
                    streetTableView.getItems().remove(row);
                }
            }
        });
    }

    /**
     * Таблица типов топлива
     */
    @SuppressWarnings("unchecked")
    private void initializeFuelTable() {
        TableColumn<FuelType, String> name = new TableColumn<>("Название");
        TableColumn<FuelType, Double> cost = new TableColumn<>("Стоимость, руб");

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setMinWidth(200);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit((TableColumn.CellEditEvent<FuelType, String> event) -> {
            FuelType fuelType = event.getTableView().getItems().get(event.getTablePosition().getRow());
            fuelType.setName(event.getNewValue());
            if (!ConnectionDB.updateFuel(fuelType)) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования топлива","Топливо с таким названием уже существует!");
                alertError.showAndWait();
            }
            fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
        });

        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setMinWidth(200);
        cost.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cost.setOnEditCommit((TableColumn.CellEditEvent<FuelType, Double> event) -> {
            double newCost = event.getNewValue();
            if (newCost >= 15 && newCost <= 100) {
                FuelType fuelType = event.getTableView().getItems().get(event.getTablePosition().getRow());
                fuelType.setCost(newCost);
                ConnectionDB.updateFuel(fuelType);
                fuelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromFuel())));
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования топлива", "Введено некорректное значение стоимости.");
                alert.showAndWait();
                fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
            }
        });
        fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
        fuelTableView.getColumns().addAll(getIndexColumn(fuelTableView), name, cost);
    }

    /**
     * Кнопки таблицы типов топлива
     */
    private void initializeFuelButtons() {
        addFuelButton.setOnAction(actionEvent -> {
            Optional<FuelType> result = DialogsController.getAddFuelDialog().showAndWait();
            result.ifPresent(list -> {
                if (ConnectionDB.addFuel(result.get())) {
                    fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
                    fuelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromFuel())));
                } else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления топлива","Топливо с таким названием уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteFuelButton.setOnAction(actionEvent -> {
            int row = fuelTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить тип топлива?", "Тип топлива будет безвозвратно удалён.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeFuel(fuelTableView.getItems().get(row).getId());
                    fuelTableView.getItems().remove(row);
                    fuelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromFuel())));
                    carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
                }
            }
        });
    }

    /**
     * Таблица дорожных покрытий
     */
    @SuppressWarnings("unchecked")
    private void initializeSurfaceTable() {
        TableColumn<RoadSurface, String> name = new TableColumn<>("Название");
        TableColumn<RoadSurface, Double> coefficient = new TableColumn<>("Коэффициент торможения");

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setMinWidth(200);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit((TableColumn.CellEditEvent<RoadSurface, String> event) -> {
            RoadSurface roadSurface = event.getTableView().getItems().get(event.getTablePosition().getRow());
            roadSurface.setName(event.getNewValue());
            if (!ConnectionDB.updateSurface(roadSurface)) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования дорожного покрытия","Покрытие с таким названием уже существует!");
                alertError.showAndWait();
            }
            surfaceTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromSurface()));
        });

        coefficient.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        coefficient.setMinWidth(200);
        coefficient.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        coefficient.setOnEditCommit((TableColumn.CellEditEvent<RoadSurface, Double> event) -> {
            double newCoefficient = event.getNewValue();
            if (newCoefficient > 0 && newCoefficient <= 1.0) {
                RoadSurface roadSurface = event.getTableView().getItems().get(event.getTablePosition().getRow());
                roadSurface.setDecelerationCoefficient(newCoefficient);
                ConnectionDB.updateSurface(roadSurface);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования дорожного покрытия", "Введено некорректное значение коэффициента торможения.");
                alert.showAndWait();
                surfaceTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromSurface()));
            }
        });
        surfaceTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromSurface()));
        surfaceTableView.getColumns().addAll(getIndexColumn(surfaceTableView), name, coefficient);
    }

    /**
     * Кнопки таблицы дорожных покрытий
     */
    private void initializeSurfaceButtons() {
        addSurfaceButton.setOnAction(actionEvent -> {
            Optional<RoadSurface> result = DialogsController.getAddSurfaceDialog().showAndWait();
            result.ifPresent(list -> {
                if(ConnectionDB.addSurface(result.get()))
                    surfaceTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromSurface()));
                else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления дорожного покрытия","Покрытие с таким названием уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteSurfaceButton.setOnAction(actionEvent -> {
            int row = surfaceTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить дорожное покрытие?", "Дорожное покрытие будет безвозвратно удалено.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeSurface(surfaceTableView.getItems().get(row).getId());
                    surfaceTableView.getItems().remove(row);
                }
            }
        });
    }


    /**
     * Таблица связей водитель-автомобиль
     */
    @SuppressWarnings("unchecked")
    private void initializeDriverCarTable() {

        driverColumn.setCellValueFactory(new PropertyValueFactory<>("driver"));
        driverColumn.setMinWidth(120);
        driverColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromDriver())));
        driverColumn.setOnEditCommit((TableColumn.CellEditEvent<DriverCar, Driver> event) -> {
            DriverCar oldDriverCar = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (!ConnectionDB.updateDriverCar(oldDriverCar.getDriver().getId(), oldDriverCar.getCar().getId(), event.getNewValue().getId(), oldDriverCar.getCar().getId())) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования связи","Такая связь уже существует!");
                alertError.showAndWait();
            }
            driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
        });

        carColumn.setCellValueFactory(new PropertyValueFactory<>("car"));
        carColumn.setMinWidth(120);
        carColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromCar())));
        carColumn.setOnEditCommit((TableColumn.CellEditEvent<DriverCar, Car> event) -> {
            DriverCar oldDriverCar = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (!ConnectionDB.updateDriverCar(oldDriverCar.getDriver().getId(), oldDriverCar.getCar().getId(), oldDriverCar.getDriver().getId(), event.getNewValue().getId())) {
                Alert alertError = DialogsController.getErrorAlert("Ошибка редактирования связи","Такая связь уже существует!");
                alertError.showAndWait();
            }
            driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
        });
        driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
        driverCarTableView.getColumns().addAll(getIndexColumn(driverCarTableView), driverColumn, carColumn);
    }

    /**
     * Кнопки таблицы связей водитель-автомобиль
     */
    private void initializeDriverCarButtons() {
        addDriverCarButton.setOnAction(actionEvent -> {
            Optional<DriverCar> result = DialogsController.getAddDriverCarDialog().showAndWait();
            result.ifPresent(list -> {
                if (ConnectionDB.addDriverCar(result.get().getDriver().getId(), result.get().getCar().getId()))
                    driverCarTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriverCar()));
                else {
                    Alert alertError = DialogsController.getErrorAlert("Ошибка добавления связи","Такая связь уже существует!");
                    alertError.showAndWait();
                }
            });
        });

        deleteDriverCarButton.setOnAction(actionEvent -> {
            int row = driverCarTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить связь?", "Связь водитель-автомобиль будет безвозвратно удалена.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ConnectionDB.removeDriverCar(
                            driverCarTableView.getItems().get(row).getDriver().getId(),
                            driverCarTableView.getItems().get(row).getCar().getId());
                    driverCarTableView.getItems().remove(row);
                }
            }
        });
    }

    private <T> TableColumn<T, Integer> getIndexColumn(TableView table) {
        TableColumn<T, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1));
        indexColumn.setSortable(false);
        indexColumn.setResizable(false);
        indexColumn.setMinWidth(30);
        indexColumn.setMaxWidth(30);
        return indexColumn;
    }
}
