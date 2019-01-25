package navigator.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import navigator.database.*;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private Button addVacancyButton;
    @FXML
    private Button deleteVacancyButton;
    @FXML
    private Button addStreetButton;
    @FXML
    private Button deleteStreetButton;

    public void initialize() {
        initializeDriverTable();
        initializeDriverButtons();
        initializeCarTable();
        initializeCarButtons();
        initializeFuelTable();
        initializeFuelButtons();
        initializeStreetTable();
        initializeStreetButtons();
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
                if(ConnectionDB.addDriver(result.get())) driverTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromDriver()));
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

                if (result.get() == ButtonType.OK) {
                    ConnectionDB.removeDriver(driverTableView.getItems().get(row).getId());
                    driverTableView.getItems().remove(row);
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
        TableColumn<Car, FuelType> fuel = new TableColumn<>("Тип топлива");
        TableColumn<Car, Double> consumption = new TableColumn<>("Расход топлива");

        model.setCellValueFactory(new PropertyValueFactory<>("model"));
        model.setCellFactory(TextFieldTableCell.forTableColumn());
        model.setMinWidth(100);
        model.setOnEditCommit((TableColumn.CellEditEvent<Car, String> event) -> {
            Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
            String newModel = event.getNewValue();
            car.setModel(newModel);
            ConnectionDB.updateCar(car);
        });

        maxSpeed.setCellValueFactory(new PropertyValueFactory<>("maxSpeed"));
        maxSpeed.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        maxSpeed.setMinWidth(130);
        maxSpeed.setOnEditCommit((TableColumn.CellEditEvent<Car, Integer> event) -> {
            int newMaxSpeed = event.getNewValue();
            // FIXME: 25/01/2019
            if (newMaxSpeed > 30 && newMaxSpeed < 260) {
                Car car = event.getTableView().getItems().get(event.getTablePosition().getRow());
                car.setMaxSpeed(newMaxSpeed);
                ConnectionDB.updateCar(car);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования автомобиля", "Введено некорректное значение максимальной скорости.");
                alert.showAndWait();
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            }
        });

        fuel.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        fuel.setMinWidth(120);
        fuel.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableList(ConnectionDB.selectAllFromFuel())));
        fuel.setOnEditCommit((TableColumn.CellEditEvent<Car, FuelType> event) -> {
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
            // FIXME: 25/01/2019 
            if (newConsumption > 3 && newConsumption < 30) {
                car.setFuelConsumption(newConsumption);
                ConnectionDB.updateCar(car);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования автомобиля", "Введено некорректное значение расхода топлива.");
                alert.showAndWait();
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            }
        });
        carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
        carTableView.getColumns().addAll(getIndexColumn(carTableView), model, maxSpeed, fuel, consumption);
    }

    /**
     * Кнопки таблицы автомобилей
     */
    @SuppressWarnings("unchecked")
    private void initializeCarButtons() {
        addCarButton.setOnAction(actionEvent -> {
            Optional<Car> result = DialogsController.getAddCarDialog().showAndWait();
            result.ifPresent(list -> {
                ConnectionDB.addCar(result.get());
                carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
            });
        });

        deleteCarButton.setOnAction(actionEvent -> {
            int row = carTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить автомобиль?", "Автомобиль будет безвозвратно удалён.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ConnectionDB.removeCar(carTableView.getItems().get(row).getId());
                    carTableView.getItems().remove(row);
                }
            }
        });
    }

    /**
     * Таблица названий улиц
     */
    @SuppressWarnings("unchecked")
    private void initializeStreetTable() {
        TableColumn<String, String> nameColumn = new TableColumn<>("name");

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setMinWidth(100);
        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<String, String> event) -> {
            String newName = event.getNewValue();
            String oldName = event.getTableView().getItems().get(event.getTablePosition().getRow());
            ConnectionDB.updateStreet(oldName, newName);
            streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));

        });
        streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));
        streetTableView.getColumns().addAll(getIndexColumn(streetTableView), nameColumn);
    }

    /**
     * Кнопки таблицы названий улиц
     */
    @SuppressWarnings("unchecked")
    private void initializeStreetButtons() {
        addStreetButton.setOnAction(actionEvent -> {
            Optional<String> result = DialogsController.getAddStreetDialog().showAndWait();
            result.ifPresent(list -> {
                ConnectionDB.addStreet(result.get());
                streetTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromStreet()));
            });
        });

        deleteStreetButton.setOnAction(actionEvent -> {
            int row = streetTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить название улицы?", "Название будет безвозвратно удалено.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
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
            ConnectionDB.updateFuel(fuelType);
        });

        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setMinWidth(200);
        cost.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cost.setOnEditCommit((TableColumn.CellEditEvent<FuelType, Double> event) -> {
            double newCost = event.getNewValue();
            // FIXME: 25/01/2019 Интервал допустимых значений
            if (newCost > 10 && newCost < 100) {
                FuelType fuelType = event.getTableView().getItems().get(event.getTablePosition().getRow());
                fuelType.setCost(newCost);
                ConnectionDB.updateFuel(fuelType);
            } else {
                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования топлива", "Введено некорректное значение стоимости.");
                alert.showAndWait();
                fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
            }
        });
        fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
        fuelTableView.getColumns().addAll(getIndexColumn(fuelTableView), name, cost);
    }


    @SuppressWarnings("unchecked")
    private void initializeFuelButtons() {
        addFuelButton.setOnAction(actionEvent -> {
            Optional<FuelType> result = DialogsController.getAddFuelDialog().showAndWait();
            result.ifPresent(list -> {
                ConnectionDB.addFuel(result.get());
                fuelTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
            });
        });

        deleteFuelButton.setOnAction(actionEvent -> {
            int row = fuelTableView.getSelectionModel().getSelectedIndex();
            if (row != -1) {
                Alert alert = DialogsController.getConfirmationAlert("Удалить тип топлива?", "Тип топлива будет безвозвратно удалён.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ConnectionDB.removeFuel(fuelTableView.getItems().get(row).getId());
                    fuelTableView.getItems().remove(row);
                    carTableView.setItems(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
                }
            }
        });
    }


//
//    private void initializeSpecialityButtons() {
//        addSpecialityButton.setOnAction(actionEvent -> {
//            Optional<Speciality> result = DialogsController.getAddSpecialityDialog().showAndWait();
//            result.ifPresent(list -> {
//                try {
//                    DataBases.addSpeciality(result.get());
//                    specialityTableView.setItems(FXCollections.observableList(DataBases.getSpecialities()));
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
//
//        deleteSpecialityButton.setOnAction(actionEvent -> {
//            int row = specialityTableView.getSelectionModel().getSelectedIndex();
//            if (row != -1) {
//                Alert alert = DialogsController.getConfirmationAlert("Удалить специальность?", "Специальность будет безвозвратно удалена.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeSpeciality(specialityTableView.getItems().get(row).getId());
//                        specialityTableView.getItems().remove(row);
//                    } catch (SQLException e) {
//                        if (e.getErrorCode() == 2292) {
//                            alert = DialogsController.getErrorAlert("Ошибка удаления специальности", "Невозможно удалить специальность, связанную с существующей вакансией.");
//                            alert.showAndWait();
//                        } else e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    @SuppressWarnings("unchecked")
//    private void initializeStreetTable() throws SQLException {
//        TableColumn<, Enterprise> enterpriseColumn = new TableColumn<>("ID");
//        TableColumn<Vacancy, Speciality> specialityColumn = new TableColumn<>("Тип покрытия");
//        TableColumn<Vacancy, Integer> experienceColumn = new TableColumn<>("Коэффициент");
//
//        enterpriseColumn.setCellValueFactory(param -> {
//            try {
//                Enterprise enterprise = DataBases.getEnterprise(param.getValue().getEnterpriseID());
//                return new SimpleObjectProperty<>(enterprise);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//        enterpriseColumn.setCellFactory(ComboBoxTableCell.forTableColumn(enterpriseTableView.getItems()));
//
//        enterpriseColumn.setOnEditCommit((TableColumn.CellEditEvent<Vacancy, Enterprise> event) -> {
//            try {
//                int row = event.getTablePosition().getRow();
//                Enterprise newEnterprise = event.getNewValue();
//                Vacancy vacancy = event.getTableView().getItems().get(row);
//                vacancy.setEnterpriseID(newEnterprise.getId());
//                DataBases.updateVacancy(vacancy);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        enterpriseColumn.setMinWidth(130);
//
//        specialityColumn.setCellValueFactory(param -> {
//            try {
//                Speciality speciality = DataBases.getSpeciality(param.getValue().getSpecialityID());
//                return new SimpleObjectProperty<>(speciality);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//        specialityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(specialityTableView.getItems()));
//
//        specialityColumn.setOnEditCommit((TableColumn.CellEditEvent<Vacancy, Speciality> event) -> {
//            try {
//                int row = event.getTablePosition().getRow();
//                Speciality newSpeciality = event.getNewValue();
//                Vacancy vacancy = event.getTableView().getItems().get(row);
//                vacancy.setSpecialityID(newSpeciality.getId());
//                DataBases.updateVacancy(vacancy);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        specialityColumn.setMinWidth(130);
//
//        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
//        experienceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        experienceColumn.setMinWidth(130);
//        experienceColumn.setOnEditCommit((TableColumn.CellEditEvent<Vacancy, Integer> event) -> {
//
//            Vacancy vacancy = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            try {
//                int newExperience = event.getNewValue();
//                if (newExperience < 0 || newExperience > 50) throw new NumberFormatException();
//                vacancy.setExperience(newExperience);
//                DataBases.updateVacancy(vacancy);
//            } catch (NumberFormatException e) {
//                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования вакансии", "Введено некорректное значение требуемого опыта работы.");
//                alert.showAndWait();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
//        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        salaryColumn.setMinWidth(80);
//        salaryColumn.setOnEditCommit((TableColumn.CellEditEvent<Vacancy, Integer> event) -> {
//
//            Vacancy vacancy = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            try {
//                int newSalary = event.getNewValue();
//                if (newSalary < 0) throw new NumberFormatException();
//                vacancy.setSalary(newSalary);
//                DataBases.updateVacancy(vacancy);
//            } catch (NumberFormatException e) {
//                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования вакансии", "Введено некорректное значение заработной платы.");
//                alert.showAndWait();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        vacancyTableView.setItems(FXCollections.observableList(DataBases.getVacancies()));
//        vacancyTableView.getColumns().addAll(getIndexColumn(vacancyTableView), enterpriseColumn, specialityColumn, experienceColumn, salaryColumn);
//    }
//
//    private void initializeVacancyButtons() {
//        addVacancyButton.setOnAction(actionEvent -> {
//            Optional<Vacancy> result = DialogsController.getAddVacancyDialog().showAndWait();
//            result.ifPresent(vacancy -> {
//                try {
//                    DataBases.addVacancy(result.get());
//                    vacancyTableView.setItems(FXCollections.observableList(DataBases.getVacancies()));
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
//
//        deleteVacancyButton.setOnAction(actionEvent -> {
//            int row = vacancyTableView.getSelectionModel().getSelectedIndex();
//            if (row != -1) {
//                Alert alert = DialogsController.getConfirmationAlert("Удалить вакансию?", "Вакансия будет безвозвратно удалена.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeVacancy(vacancyTableView.getItems().get(row).getId());
//                        vacancyTableView.getItems().remove(row);
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    @SuppressWarnings("unchecked")
//    private void initializeResumeTable() throws SQLException {
//        TableColumn<Resume, Employee> employeeColumn = new TableColumn<>("Претендент");
//        TableColumn<Resume, Speciality> specialityColumn = new TableColumn<>("Специальность");
//        TableColumn<Resume, Integer> experienceColumn = new TableColumn<>("Опыт работы");
//        TableColumn<Resume, Date> dateColumn = new TableColumn<>("Дата создания");
//
//        employeeColumn.setCellValueFactory(param -> {
//            try {
//                Employee employee = DataBases.getEmployee(param.getValue().getEmployeeID());
//                return new SimpleObjectProperty<>(employee);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//        employeeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(employeeTableView.getItems()));
//
//        employeeColumn.setOnEditCommit((TableColumn.CellEditEvent<Resume, Employee> event) -> {
//            try {
//                int row = event.getTablePosition().getRow();
//                Employee newEmployee = event.getNewValue();
//                Resume resume = event.getTableView().getItems().get(row);
//                resume.setEmployeeID(newEmployee.getId());
//                DataBases.updateResume(resume);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        employeeColumn.setMinWidth(250);
//
//        specialityColumn.setCellValueFactory(param -> {
//            try {
//                return new SimpleObjectProperty<>(DataBases.getSpeciality(param.getValue().getSpecialityID()));
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//        specialityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(specialityTableView.getItems()));
//
//        specialityColumn.setOnEditCommit((TableColumn.CellEditEvent<Resume, Speciality> event) -> {
//            try {
//                int row = event.getTablePosition().getRow();
//                Speciality newSpeciality = event.getNewValue();
//                Resume resume = event.getTableView().getItems().get(row);
//                resume.setSpecialityID(newSpeciality.getId());
//                DataBases.updateResume(resume);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        specialityColumn.setMinWidth(130);
//
//        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
//        experienceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        experienceColumn.setMinWidth(100);
//        experienceColumn.setOnEditCommit((TableColumn.CellEditEvent<Resume, Integer> event) -> {
//
//            Resume resume = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            try {
//                int newExperience = event.getNewValue();
//                if (newExperience < 0 || newExperience > 60) throw new NumberFormatException();
//                resume.setExperience(newExperience);
//                DataBases.updateResume(resume);
//            } catch (NumberFormatException e) {
//                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования резюме", "Введено некорректное значение опыта работы.");
//                alert.showAndWait();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
//        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
//        dateColumn.setMinWidth(110);
//        dateColumn.setOnEditCommit((TableColumn.CellEditEvent<Resume, Date> event) -> {
//
//            Resume resume = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            try {
//                Date newDate = event.getNewValue();
//                if (newDate.after(new Date())) throw new ParseException("", 0);
//                resume.setDate(newDate);
//                DataBases.updateResume(resume);
//            } catch (ParseException e) {
//                Alert alert = DialogsController.getErrorAlert("Ошибка редактирования резюме", "Введена некорректная дата создания резюме.");
//                alert.showAndWait();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        resumeTableView.setItems(FXCollections.observableList(DataBases.getResumes()));
//        resumeTableView.getColumns().addAll(getIndexColumn(resumeTableView), employeeColumn, specialityColumn, experienceColumn, dateColumn);
//    }
//
//    private void initializeResumeButtons() {
//        addResumeButton.setOnAction(actionEvent -> {
//            Optional<Resume> result = DialogsController.getAddResumeDialog().showAndWait();
//            result.ifPresent(resume -> {
//                try {
//                    DataBases.addResume(result.get());
//                    resumeTableView.setItems(FXCollections.observableList(DataBases.getResumes()));
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
//
//        deleteResumeButton.setOnAction(actionEvent -> {
//            int row = resumeTableView.getSelectionModel().getSelectedIndex();
//            if (row != -1) {
//                Alert alert = DialogsController.getConfirmationAlert("Удалить резюме?", "Резюме будет безвозвратно удалено.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeResume(resumeTableView.getItems().get(row).getId());
//                        resumeTableView.getItems().remove(row);
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
// }

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
