package navigator.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import navigator.database.Car;
import navigator.database.ConnectionDB;
import navigator.database.Driver;
import navigator.database.FuelType;

import java.sql.SQLException;
import java.util.Date;

public class DialogsController {

    /**
     * Добавление водителя
     *
     * @return водитель
     */
    public static Dialog<Driver> getAddDriverDialog() {
        Dialog<Driver> driverDialog = new Dialog<>();
        driverDialog.setTitle("Добавление водителя");
        driverDialog.setHeaderText("Заполните информацию о сотруднике");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        driverDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField fioField = new TextField();
        fioField.setPromptText("ФИО");

        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(fioField, 1, 0);

        Node saveButton = driverDialog.getDialogPane().lookupButton(confirmButton);
        saveButton.setDisable(true);

        fioField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));
        driverDialog.getDialogPane().setContent(grid);

        driverDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton)
                return new Driver(-1, fioField.getText());
            else return null;
        });
        return driverDialog;
    }

    /**
     * Добавление автомобиля
     *
     * @return автомобиль
     */
    public static Dialog<Car> getAddCarDialog() {
        Dialog<Car> carDialog = new Dialog<>();
        carDialog.setTitle("Добавление автомобиля");
        carDialog.setHeaderText("Заполните информацию об автомобиле");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        carDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField modelField = new TextField();
        TextField maxSpeedField = new TextField();
        // FIXME: 25/01/2019
        ComboBox<FuelType> fuelComboBox = new ComboBox<>(FXCollections.observableList(ConnectionDB.selectAllFromFuel()));
        fuelComboBox.setMinWidth(200);
        TextField fuelConsumptionField = new TextField();
        modelField.setPromptText("Модель");
        maxSpeedField.setPromptText("Максимальная скорость");
        fuelComboBox.setPromptText("Тип топлива");
        fuelConsumptionField.setPromptText("Расход топлива");

        grid.add(new Label("Модель:"), 0, 0);
        grid.add(new Label("Макс.скорость:"), 0, 1);
        grid.add(new Label("Топливо:"), 0, 2);
        grid.add(new Label("Расход топлива:"), 0, 3);
        grid.add(modelField, 1, 0);
        grid.add(maxSpeedField, 1, 1);
        grid.add(fuelComboBox, 1, 2);
        grid.add(fuelConsumptionField, 1, 3);

        Node saveButton = carDialog.getDialogPane().lookupButton(confirmButton);
        saveButton.setDisable(true);

        modelField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));
        carDialog.getDialogPane().setContent(grid);

        carDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                try {
                    int maxSpeed = Integer.parseInt(maxSpeedField.getText());
                    double fuelConsumption = Double.parseDouble(fuelConsumptionField.getText());
                    // FIXME: 25/01/2019
                    if (maxSpeed < 30 || maxSpeed > 260 || fuelConsumption < 3 || fuelConsumption > 30)
                        throw new NumberFormatException();
                    if (fuelComboBox.getValue() == null) throw new NullPointerException();
                    return new Car(-1, modelField.getText(), maxSpeed, fuelComboBox.getValue(), fuelConsumption);
                } catch (NumberFormatException e) {
                    Alert alert = getErrorAlert("Ошибка добавления автомобиля", "Введено некорректное значение максимальной скорости или расхода топлива.");
                    alert.showAndWait();
                    return null;
                } catch (NullPointerException e) {
                    Alert alert = getErrorAlert("Ошибка добавления автомобиля", "Не выбран тип топлива.");
                    alert.showAndWait();
                    return null;
                }
            } else return null;
        });
        return carDialog;
    }


    /**
     * Добавление названия улицы
     *
     * @return название улицы
     */
    public static Dialog<String> getAddStreetDialog() {
        Dialog<String> streetDialog = new Dialog<>();
        streetDialog.setTitle("Добавление названия улицы");
        streetDialog.setHeaderText("Заполните информацию о названии");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        streetDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField streetField = new TextField();
        streetField.setPromptText("Название улицы");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(streetField, 1, 0);

        Node saveButton = streetDialog.getDialogPane().lookupButton(confirmButton);
        saveButton.setDisable(true);

        streetField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));
        streetDialog.getDialogPane().setContent(grid);

        streetDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) return streetField.getText();
            else return null;
        });
        return streetDialog;
    }


    /*
        public static Dialog<Speciality> getAddSpecialityDialog() {
            Dialog<Speciality> dialog = new Dialog<>();
            dialog.setTitle("Добавление специальности");
            dialog.setHeaderText("Заполните информацию о специальности");

            ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 100, 10, 10));

            TextField nameField = new TextField();
            nameField.setPromptText("Название");

            grid.add(new Label("Название:"), 0, 0);
            grid.add(nameField, 1, 0);
            Node loginButton = dialog.getDialogPane().lookupButton(confirmButton);
            loginButton.setDisable(true);

            nameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButton)
                    return new Speciality(-1, nameField.getText());
                else return null;
            });
            return dialog;
        }

        public static Dialog<Vacancy> getAddVacancyDialog() {
            Dialog<Vacancy> dialog = new Dialog<>();
            try {
                dialog.setTitle("Добавление вакансии");
                dialog.setHeaderText("Заполните информацию о вакансии");

                ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 100, 10, 10));

                ComboBox<Enterprise> enterpriseComboBox = new ComboBox<>(FXCollections.observableList(DataBases.getEnterprises()));
                ComboBox<Speciality> specialityComboBox = new ComboBox<>(FXCollections.observableList(DataBases.getSpecialities()));
                enterpriseComboBox.setMinWidth(150);
                specialityComboBox.setMinWidth(150);
                enterpriseComboBox.setPromptText("Предприятие");
                specialityComboBox.setPromptText("Специальность");

                TextField experienceField = new TextField();
                TextField salaryField = new TextField();
                experienceField.setPromptText("Опыт работы");
                salaryField.setPromptText("Зарплата");

                grid.add(new Label("Предприятие:"), 0, 0);
                grid.add(new Label("Специальность:"), 0, 1);
                grid.add(new Label("Требуемый опыт работы:"), 0, 2);
                grid.add(new Label("Зарплата:"), 0, 3);
                grid.add(enterpriseComboBox, 1, 0);
                grid.add(specialityComboBox, 1, 1);
                grid.add(experienceField, 1, 2);
                grid.add(salaryField, 1, 3);

                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmButton) {
                        try {
                            if (enterpriseComboBox.getValue() == null || specialityComboBox.getValue() == null || experienceField.getText().equals("") || salaryField.getText().equals(""))
                                throw new NullPointerException();

                            int experience = Integer.parseInt(experienceField.getText());
                            int salary = Integer.parseInt(salaryField.getText());
                            if (experience < 0 || experience > 50 || salary < 0) throw new NumberFormatException();
                            return new Vacancy(-1, enterpriseComboBox.getValue().getId(), specialityComboBox.getValue().getId(), experience, salary);

                        } catch (NullPointerException e) {
                            Alert alert = getErrorAlert("Ошибка добавления вакансии", "Все поля должны быть заполнены.");
                            alert.showAndWait();
                            return null;
                        } catch (NumberFormatException e) {
                            Alert alert = getErrorAlert("Ошибка добавления вакансии", "Введено некорректное значение зарплаты или требуемого опыта работы.");
                            alert.showAndWait();
                            return null;
                        }
                    } else return null;
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dialog;
        }

        public static Dialog<Resume> getAddResumeDialog() {
            Dialog<Resume> dialog = new Dialog<>();
            dialog.setTitle("Добавление резюме");
            dialog.setHeaderText("Заполните информацию о резюме");

            ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 100, 10, 10));

            try {
                ComboBox<Employee> employeeComboBox = new ComboBox<>(FXCollections.observableList(DataBases.getEmployees()));
                ComboBox<Speciality> specialityComboBox = new ComboBox<>(FXCollections.observableList(DataBases.getSpecialities()));
                employeeComboBox.setMinWidth(150);
                specialityComboBox.setMinWidth(150);
                employeeComboBox.setPromptText("Претендент");
                specialityComboBox.setPromptText("Специальность");

                TextField experienceField = new TextField();
                experienceField.setPromptText("Опыт работы");

                grid.add(new Label("Претендент:"), 0, 0);
                grid.add(new Label("Специальность:"), 0, 1);
                grid.add(new Label("Опыт работы:"), 0, 2);
                grid.add(employeeComboBox, 1, 0);
                grid.add(specialityComboBox, 1, 1);
                grid.add(experienceField, 1, 2);

                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmButton) {
                        try {
                            if (employeeComboBox.getValue() == null || specialityComboBox.getValue() == null || experienceField.getText().equals(""))
                                throw new NullPointerException();

                            int experience = Integer.parseInt(experienceField.getText());
                            if (experience < 0 || experience > 60) throw new NumberFormatException();
                            return new Resume(-1, employeeComboBox.getValue().getId(), specialityComboBox.getValue().getId(), experience, new Date());

                        } catch (NullPointerException e) {
                            Alert alert = getErrorAlert("Ошибка добавления резюме", "Все поля должны быть заполнены.");
                            alert.showAndWait();
                            return null;
                        } catch (NumberFormatException e) {
                            Alert alert = getErrorAlert("Ошибка добавления резюме", "Введено некорректное значение опыта работы.");
                            alert.showAndWait();
                            return null;
                        }
                    } else return null;
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dialog;
        }
    */
    public static Alert getConfirmationAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public static Alert getErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

}
