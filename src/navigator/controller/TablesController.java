package navigator.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import navigator.database.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TablesController {
    @FXML
    private TableView<Driver> driverTableView;
    @FXML
    private TableView<Car> carTableView;
    @FXML
    private TableView<FuelType> fuelTableView;
    @FXML
    private TableView<String> streetTableView;
    //    @FXML
//    private TableView<Vacancy> vacancyTableView;
//    @FXML
//    private TableView<Resume> resumeTableView;
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
    private Button addResumeButton;
    @FXML
    private Button deleteResumeButton;

    ConnectionDB connectionDB = new ConnectionDB();
    Connection connection = connectionDB.connectionSQLite("test");

    public void initialize() {
        try {
            initializeCarTable();
//            initializeCarButtons();
            initializeDriverTable();
            // initializeDriverButtons();
            initializeFuelTable();
            initializeStreetTable();
//            initializeSpecialityButtons();
//            initializeVacancyTable();
//            initializeVacancyButtons();
//            initializeResumeTable();
//            initializeResumeButtons();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeCarTable() throws SQLException {
        TableColumn<Car, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Car, String> model = new TableColumn<>("Модель");
        TableColumn<Car, Integer> maxSpeed = new TableColumn<>("Максимальная скорость");
        TableColumn<Car, FuelType> fuel = new TableColumn<>("Тип топлива");
        TableColumn<Car, Double> consumption = new TableColumn<>("Расход топлива");

        // Defines how to fill data for each cell.
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        //idColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        idColumn.setMinWidth(50);
//        idColumn.setOnEditCommit((TableColumn.CellEditEvent<Car, Integer> event) -> {

//            TablePosition<Car, String> position = event.getTablePosition();
//            Car employee = event.getTableView().getItems().get(position.getRow());
//            String newFio = event.getNewValue();
//            employee.setFio(newFio);
//            try {
//                DataBases.updateEmployee(employee);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        // });

        model.setCellValueFactory(new PropertyValueFactory<>("model"));
        //model.setCellFactory(TextFieldTableCell.forTableColumn());
        model.setMinWidth(100);
        // model.setOnEditCommit((TableColumn.CellEditEvent<Car, String> event) -> {

        //  Car employee = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            String newNumber = event.getNewValue();
//            employee.setPhonenumber(newNumber);
//            try {
//                DataBases.updateEmployee(employee);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        // });

        maxSpeed.setCellValueFactory(new PropertyValueFactory<>("maxSpeed"));
        // maxSpeed.setCellFactory(TextFieldTableCell.forTableColumn());
        maxSpeed.setMinWidth(130);
        maxSpeed.setOnEditCommit((TableColumn.CellEditEvent<Car, Integer> event) -> {

//            Employee employee = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            String newEmail = event.getNewValue();
//            employee.setEmail(newEmail);
//            try {
//                DataBases.updateEmployee(employee);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        });

        fuel.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        //fuel.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fuel.setMinWidth(120);
        fuel.setOnEditCommit((TableColumn.CellEditEvent<Car, FuelType> event) -> {

//            Employee employee = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            try {
//                int newAge = event.getNewValue();
//                if (newAge < 14 || newAge > 100) throw new NumberFormatException();
//                employee.setAge(newAge);
//                DataBases.updateEmployee(employee);
//            } catch (NumberFormatException e) {
//                Alert alert = Dialogs.getErrorAlert("Ошибка редактирования сотрудника", "Введено некорректное значение возраста сотрудника.");
//                alert.showAndWait();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        });

        consumption.setCellValueFactory(new PropertyValueFactory<>("fuelConsumption"));
        //fuel.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        consumption.setMinWidth(100);

        ObservableList<Car> carsData = FXCollections.observableArrayList();
        //ConnectionDB con = new ConnectionDB();
        ArrayList<Car> cars = connectionDB.selectAllFromCar("Car", "*");
        for (Car car : cars
        ) {
            carsData.add(car);
        }

        carTableView.setItems(FXCollections.observableList(carsData));
        carTableView.getColumns().addAll(getIndexColumn(carTableView), idColumn, model, maxSpeed, fuel, consumption);
    }

    @SuppressWarnings("unchecked")
    private void initializeStreetTable() throws SQLException {
        TableColumn<String, Integer> nameColumn = new TableColumn<>("name");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        //model.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setMinWidth(100);
        ObservableList<String> streetsData = FXCollections.observableArrayList();
        //ConnectionDB con = new ConnectionDB();
        ArrayList<String> streets = connectionDB.selectAllFromStreet("Street", "*");
        for (String street : streets
        ) {
            streetsData.add(street);
        }

        streetTableView.setItems(FXCollections.observableList(streetsData));
        streetTableView.getColumns().addAll(getIndexColumn(streetTableView), nameColumn);

    }

    //    private void initializeCarButtons() {
//        addCarButton.setOnAction(actionEvent -> {
//            Optional<Employee> result = Dialogs.getAddEmployeeDialog().showAndWait();
//            result.ifPresent(list -> {
//                try {
//                    DataBases.addEmployee(result.get());
//                    employeeTableView.setItems(FXCollections.observableList(DataBases.getEmployees()));
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
//
//        deleteEmployeeButton.setOnAction(actionEvent -> {
//            int row = employeeTableView.getSelectionModel().getSelectedIndex();
//            if (row != -1) {
//                Alert alert = Dialogs.getConfirmationAlert("Удалить сотрудника?", "Сотрудник будет безвозвратно удалён.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeEmployee(employeeTableView.getItems().get(row).getId());
//                        employeeTableView.getItems().remove(row);
//                    } catch (SQLException e) {
//                        if (e.getErrorCode() == 2292) {
//                            alert = Dialogs.getErrorAlert("Ошибка удаления сотрудника", "Невозможно удалить сотрудника, связанного с существующими резюме.");
//                            alert.showAndWait();
//                        } else e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//
    @SuppressWarnings("unchecked")
    private void initializeDriverTable() throws SQLException {
        TableColumn<Driver, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Driver, String> fio = new TableColumn<>("ФИО");

        // Defines how to fill data for each cell.
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        //idColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        idColumn.setMinWidth(20);
//        phoneColumn.setOnEditCommit((TableColumn.CellEditEvent<Enterprise, String> event) -> {
//
//            Enterprise enterprise = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            String newNumber = event.getNewValue();
//            enterprise.setPhone(newNumber);
//            try {
//                DataBases.updateEnterprise(enterprise);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
        fio.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fio.setCellFactory(TextFieldTableCell.forTableColumn());
        fio.setMinWidth(200);
//        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<Driver, String> event) -> {
//
//            Driver enterprise = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            String newName = event.getNewValue();
//            enterprise.setName(newName);
//            try {
//                DataBases.updateEnterprise(enterprise);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });

        ObservableList<Driver> driversData = FXCollections.observableArrayList();
        ConnectionDB con = new ConnectionDB();
        ArrayList<Driver> drivers = con.selectAllFromDriver("Driver", "id,fio");
        for (Driver driver : drivers
        ) {
            driversData.add(driver);
        }


        driverTableView.setItems(FXCollections.observableList(driversData));
        driverTableView.getColumns().addAll(getIndexColumn(driverTableView), idColumn, fio);
    }

    //    private void initializeDriverButtons() {
//        addEnterpriseButton.setOnAction(actionEvent -> {
//            Optional<Enterprise> result = Dialogs.getAddEnterpriseDialog().showAndWait();
//            result.ifPresent(list -> {
//                try {
//                    DataBases.addEnterprise(result.get());
//                    enterpriseTableView.setItems(FXCollections.observableList(DataBases.getEnterprises()));
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
//
//        deleteEnterpriseButton.setOnAction(actionEvent -> {
//            int row = enterpriseTableView.getSelectionModel().getSelectedIndex();
//            if (row != -1) {
//                Alert alert = Dialogs.getConfirmationAlert("Удалить предприятие?", "Предприятие будет безвозвратно удалено.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeEnterprise(enterpriseTableView.getItems().get(row).getId());
//                        enterpriseTableView.getItems().remove(row);
//                    } catch (SQLException e) {
//                        if (e.getErrorCode() == 2292) {
//                            alert = Dialogs.getErrorAlert("Ошибка удаления предприятия", "Невозможно удалить предприятие, связанное с существующей вакансией.");
//                            alert.showAndWait();
//                        } else e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
    @SuppressWarnings("unchecked")
    private void initializeFuelTable() throws SQLException {
        TableColumn<FuelType, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<FuelType, String> typeColumn = new TableColumn<>("Название");
        TableColumn<FuelType, Double> costColumn = new TableColumn<>("Стоимость, руб");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        idColumn.setMinWidth(20);

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        //typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColumn.setMinWidth(200);

//        typeColumn.setCellValueFactory(new PropertyValueFactory<Fuel, String>("name"){
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<Fuel, String> param) {
//                return new ReadOnlyObjectWrapper(param.getValue().getUsername());
//            }
//        });
//        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<Fuel, String> event) -> {
//
//            Fuel speciality = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            String newName = event.getNewValue();
//            speciality.setName(newName);
//            try {
//                DataBases.updateSpeciality(speciality);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });

        costColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        // costColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        costColumn.setMinWidth(200);

        ObservableList<FuelType> fuelsData = FXCollections.observableArrayList();
        ConnectionDB con = new ConnectionDB();
        // ArrayList<Fuel> fuels= new ArrayList<>();
        FuelType fuel = new FuelType(1, "AI-100", 50);
//        ArrayList<Fuel> fuels = con.selectAllFromFuel("Fuel", "id,type,cost");
//        for (Fuel fuel : fuels
//        ) {
//            fuelsData.add(fuel);
//        }
        fuelsData.add(fuel);

        fuelTableView.setItems(FXCollections.observableList(fuelsData));
        fuelTableView.getColumns().addAll(getIndexColumn(fuelTableView), idColumn, typeColumn, costColumn);
    }
//
//    private void initializeSpecialityButtons() {
//        addSpecialityButton.setOnAction(actionEvent -> {
//            Optional<Speciality> result = Dialogs.getAddSpecialityDialog().showAndWait();
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
//                Alert alert = Dialogs.getConfirmationAlert("Удалить специальность?", "Специальность будет безвозвратно удалена.");
//                Optional<ButtonType> result = alert.showAndWait();
//
//                if (result.get() == ButtonType.OK) {
//                    try {
//                        DataBases.removeSpeciality(specialityTableView.getItems().get(row).getId());
//                        specialityTableView.getItems().remove(row);
//                    } catch (SQLException e) {
//                        if (e.getErrorCode() == 2292) {
//                            alert = Dialogs.getErrorAlert("Ошибка удаления специальности", "Невозможно удалить специальность, связанную с существующей вакансией.");
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
//                Alert alert = Dialogs.getErrorAlert("Ошибка редактирования вакансии", "Введено некорректное значение требуемого опыта работы.");
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
//                Alert alert = Dialogs.getErrorAlert("Ошибка редактирования вакансии", "Введено некорректное значение заработной платы.");
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
//            Optional<Vacancy> result = Dialogs.getAddVacancyDialog().showAndWait();
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
//                Alert alert = Dialogs.getConfirmationAlert("Удалить вакансию?", "Вакансия будет безвозвратно удалена.");
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
//                Alert alert = Dialogs.getErrorAlert("Ошибка редактирования резюме", "Введено некорректное значение опыта работы.");
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
//                Alert alert = Dialogs.getErrorAlert("Ошибка редактирования резюме", "Введена некорректная дата создания резюме.");
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
//            Optional<Resume> result = Dialogs.getAddResumeDialog().showAndWait();
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
//                Alert alert = Dialogs.getConfirmationAlert("Удалить резюме?", "Резюме будет безвозвратно удалено.");
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
