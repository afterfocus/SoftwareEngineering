package navigator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import navigator.database.DAO;
import navigator.model.Road;
import navigator.model.RoadSurface;

import java.util.Optional;

/**
 * Контроллер настройки параметров дороги
 */
public class RoadSettingsController {
    @FXML
    private Pane roadSettings;
    @FXML
    private ComboBox<String> streetNameComboBox;
    @FXML
    private ComboBox<Character> directionComboBox;
    @FXML
    private TextField lengthTextField;
    @FXML
    private ComboBox<Integer> speedLimitComboBox;
    @FXML
    private ComboBox<RoadSurface> roadSurfaceComboBox;
    @FXML
    private Button closeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;

    private Alert alert;
    private Road road;

    /**
     * Инициализация
     */
    @FXML
    public void initialize() {

        //Инициализация окна подтверждения
        ButtonType delete = new ButtonType("Удалить");
        ButtonType cancel = new ButtonType("Отменить");
        alert = JunctionSettingsController.getAlert(delete, cancel);

        //Кнопка закрыть
        closeButton.setOnMouseClicked(e -> roadSettings.setVisible(false));

        //Параметры
        streetNameComboBox.setItems(FXCollections.observableArrayList(""));
        streetNameComboBox.getItems().addAll(FXCollections.observableArrayList(DAO.getStreetNames()));
        directionComboBox.setItems(FXCollections.observableArrayList('↔', '→', '←'));
        speedLimitComboBox.setItems(FXCollections.observableArrayList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110));
        roadSurfaceComboBox.setItems(FXCollections.observableArrayList(DAO.getRoadSurfaces()));

        //Кнопка сохранить
        saveButton.setOnMouseClicked(e -> {
            road.setName(streetNameComboBox.getSelectionModel().getSelectedItem());
            road.setDirection(directionComboBox.getSelectionModel().getSelectedItem());
            road.setSpeedLimit(speedLimitComboBox.getSelectionModel().getSelectedItem());
            road.setRoadSurface(roadSurfaceComboBox.getSelectionModel().getSelectedItem());
            roadSettings.setVisible(false);
        });

        //Кнопка удалить
        deleteButton.setOnMouseClicked(e -> {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == delete) {
                roadSettings.setVisible(false);
                road.dispose();
            }
        });
    }

    /**
     * Передача дороги для настройки
     * @param road настраиваемая дорога
     */
    void setRoad(Road road) {
        this.road = road;
        streetNameComboBox.getSelectionModel().select(road.getName());
        directionComboBox.getSelectionModel().select(road.getDirection());
        lengthTextField.setText("" + road.getLength());
        speedLimitComboBox.getSelectionModel().select(road.getSpeedLimit());
        roadSurfaceComboBox.getSelectionModel().select(road.getRoadSurface());
    }
}