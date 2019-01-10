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

    private Alert alert;
    private Road road;
    private ButtonType delete;

    /**
     * Инициализация
     */
    @FXML
    public void initialize() {

        //Инициализация окна подтверждения
        alert = getAlert();

        //Кнопка закрыть
        closeButton.setOnMouseClicked(e -> roadSettings.setVisible(false));

        //Параметры
        streetNameComboBox.setItems(FXCollections.observableArrayList(""));
        streetNameComboBox.getItems().addAll(FXCollections.observableArrayList(DAO.getStreetNames()));
        directionComboBox.setItems(FXCollections.observableArrayList('↔', '→', '←'));
        speedLimitComboBox.setItems(FXCollections.observableArrayList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110));
        roadSurfaceComboBox.setItems(FXCollections.observableArrayList(DAO.getRoadSurfaces()));
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

    /**
     * Нажатие на кнопку сохранить устанавливает новые значения параметров дороги и закрывает окно настроек
     */
    @FXML
    void save() {
        road.setName(streetNameComboBox.getSelectionModel().getSelectedItem());
        road.setDirection(directionComboBox.getSelectionModel().getSelectedItem());
        road.setSpeedLimit(speedLimitComboBox.getSelectionModel().getSelectedItem());
        road.setRoadSurface(roadSurfaceComboBox.getSelectionModel().getSelectedItem());
        roadSettings.setVisible(false);
    }

    /**
     * Нажатие на кнопку удалить удаляет дорогу в случае подтверждения удаления
     */
    @FXML
    void delete() {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == delete) {
            roadSettings.setVisible(false);
            road.dispose();
        }
    }

    /**
     * Инициализация окна подтверждения
     * @return окно подтверждения
     */
    private Alert getAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Подтвердите удаление");
        alert.setContentText("Вы действительно хотите удалить дорогу?");
        alert.getButtonTypes().clear();
        delete = new ButtonType("Удалить");
        ButtonType cancel = new ButtonType("Отменить");
        alert.getButtonTypes().addAll(delete, cancel);
        return alert;
    }
}