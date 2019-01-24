package navigator.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import navigator.database.Driver;

import java.sql.SQLException;
import java.util.Date;

public class Dialogs {

    private static Dialog<Driver> driverDialog;

    public static Dialog<Driver> getAddDriverDialog() {
        if (driverDialog == null) {
            driverDialog = new Dialog<>();
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

            Node loginButton = driverDialog.getDialogPane().lookupButton(confirmButton);
            loginButton.setDisable(true);

            fioField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
            driverDialog.getDialogPane().setContent(grid);

            driverDialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButton)
                    return new Driver(-1, fioField.getText());
                else return null;
            });
        }
        return driverDialog;
    }

/*
    public static Dialog<Enterprise> getAddEnterpriseDialog() {
        Dialog<Enterprise> dialog = new Dialog<>();
        dialog.setTitle("Добавление предприятия");
        dialog.setHeaderText("Заполните информацию о предприятии");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        nameField.setPromptText("Название");
        phoneField.setPromptText("Номер телефона");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(new Label("Телефон:"), 0, 1);
        grid.add(nameField, 1, 0);
        grid.add(phoneField, 1, 1);
        Node loginButton = dialog.getDialogPane().lookupButton(confirmButton);
        loginButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton)
                return new Enterprise(-1, nameField.getText(), phoneField.getText());
            else return null;
        });
        return dialog;
    }

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
