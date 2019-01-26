package navigator.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import navigator.database.*;

class DialogsController {

    /**
     * Добавление водителя
     *
     * @return диалог добавления водителя
     */
    static Dialog<Driver> getAddDriverDialog() {
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
            if (dialogButton == confirmButton) return new Driver(-1, fioField.getText());
            else return null;
        });
        return driverDialog;
    }

    /**
     * Добавление автомобиля
     *
     * @return диалог добавления автомобиля
     */
     static Dialog<Car> getAddCarDialog() {
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
                    if (maxSpeed <= 20 || maxSpeed >= 300 || fuelConsumption <= 3 || fuelConsumption >= 50)
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
     * @return диалог добавления названия улицы
     */
    static Dialog<String> getAddStreetDialog() {
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


    /**
     * Добавление топлива
     *
     * @return диалог добалвения топлива
     */
    static Dialog<FuelType> getAddFuelDialog() {
        Dialog<FuelType> fuelDialog = new Dialog<>();
        fuelDialog.setTitle("Добавление топлива");
        fuelDialog.setHeaderText("Заполните информацию о топливе");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        fuelDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField nameField = new TextField();
        TextField costField = new TextField();
        nameField.setPromptText("Название топлива");
        costField.setPromptText("Стоимость за литр");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(new Label("Стоимость:"), 0, 1);
        grid.add(nameField, 1, 0);
        grid.add(costField, 1, 1);

        Node saveButton = fuelDialog.getDialogPane().lookupButton(confirmButton);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));
        fuelDialog.getDialogPane().setContent(grid);

        fuelDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                try {
                    double cost = Double.parseDouble(costField.getText());
                    if (cost <= 15 || cost >= 100) throw new NumberFormatException();
                    return new FuelType(-1, nameField.getText(), cost);
                } catch (NumberFormatException e) {
                    Alert alert = getErrorAlert("Ошибка добавления топлива", "Введено некорректное значение стоимости.");
                    alert.showAndWait();
                    return null;
                }
            } else return null;
        });
        return fuelDialog;
    }

    static Dialog<RoadSurface> getAddSurfaceDialog() {
        Dialog<RoadSurface> surfaceDialog = new Dialog<>();
        surfaceDialog.setTitle("Добавление дорожного покрытия");
        surfaceDialog.setHeaderText("Заполните информацию о покрытии");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        surfaceDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField nameField = new TextField();
        TextField coefficientField = new TextField();
        nameField.setPromptText("Название топлива");
        coefficientField.setPromptText("Коэффициент торможения");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(new Label("Стоимость:"), 0, 1);
        grid.add(nameField, 1, 0);
        grid.add(coefficientField, 1, 1);

        Node saveButton = surfaceDialog.getDialogPane().lookupButton(confirmButton);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(newValue.trim().isEmpty()));
        surfaceDialog.getDialogPane().setContent(grid);

        surfaceDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                try {
                    double coefficient = Double.parseDouble(coefficientField.getText());
                    if (coefficient <= 0 || coefficient > 1) throw new NumberFormatException();
                    return new RoadSurface(-1, nameField.getText(), coefficient);
                } catch (NumberFormatException e) {
                    Alert alert = getErrorAlert("Ошибка добавления дорожного покрытия", "Введено некорректное значение коэффициента торможения.");
                    alert.showAndWait();
                    return null;
                }
            } else return null;
        });
        return surfaceDialog;
    }

    /**
     * Добавление связи водитель-автомобиль
     *
     * @return диалог добавления связи водитель-автомобиль
     */
    static Dialog<DriverCar> getAddDriverCarDialog() {
        Dialog<DriverCar> driverCarDialog = new Dialog<>();
        driverCarDialog.setTitle("Добавление связи водитель-автомобиль");
        driverCarDialog.setHeaderText("Заполните информацию о связи");

        ButtonType confirmButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        driverCarDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        ComboBox<Driver> driverComboBox = new ComboBox<>(FXCollections.observableList(ConnectionDB.selectAllFromDriver()));
        driverComboBox.setMinWidth(250);
        driverComboBox.setPromptText("Водитель");
        ComboBox<Car> carComboBox = new ComboBox<>(FXCollections.observableList(ConnectionDB.selectAllFromCar()));
        carComboBox.setMinWidth(250);
        carComboBox.setPromptText("Автомобиль");

        grid.add(new Label("Водитель:"), 0, 0);
        grid.add(new Label("Автомобиль:"), 0, 1);
        grid.add(driverComboBox, 1, 0);
        grid.add(carComboBox, 1, 1);

        driverCarDialog.getDialogPane().setContent(grid);

        driverCarDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                try {
                    if (driverComboBox.getValue() == null || carComboBox.getValue() == null) throw new NullPointerException();
                    return new DriverCar(driverComboBox.getValue(), carComboBox.getValue());
                } catch (NullPointerException e) {
                    Alert alert = getErrorAlert("Ошибка добавления связи", "Не выбран водитель или автомобиль.");
                    alert.showAndWait();
                    return null;
                }
            } else return null;
        });
        return driverCarDialog;
    }


    static Alert getConfirmationAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    static Alert getErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

}
