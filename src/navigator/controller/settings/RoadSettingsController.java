package navigator.controller.settings;

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
    private ComboBox<String> streetNameComboBox = new ComboBox<>();
    @FXML
    private ComboBox<Character> directionComboBox = new ComboBox<>();
    @FXML
    private TextField lengthTextField = new TextField();
    @FXML
    private ComboBox<Integer> speedLimitComboBox = new ComboBox<>();
    @FXML
    private ComboBox<RoadSurface> roadSurfaceComboBox = new ComboBox<>();
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
     *
     * @param road настраиваемая дорога
     */
    public void setRoad(Road road) {
        this.road = road;
        streetNameComboBox.getSelectionModel().select(road.getName());
        directionComboBox.getSelectionModel().select(road.getDirection());
        lengthTextField.setText("" + road.getLength());
        speedLimitComboBox.getSelectionModel().select(road.getSpeedLimit());
        roadSurfaceComboBox.getSelectionModel().select(road.getRoadSurface());
        roadSettings.setLayoutX((road.getForwardLine().getStartX() + road.getForwardLine().getEndX()) / 2);
        roadSettings.setLayoutY((road.getForwardLine().getStartY() + road.getForwardLine().getEndY()) / 2);
    }

    /**
     * Нажатие на кнопку сохранить устанавливает новые значения параметров дороги и закрывает окно настроек
     */
    @FXML
    private void save() {
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
    private void delete() {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == delete) {
            road.getMap().removeRoad(road);
            roadSettings.setVisible(false);
        }
    }

    /**
     * Инициализация окна подтверждения
     *
     * @return окно подтверждения
     */
    private Alert getAlert() {
        delete = new ButtonType("Удалить");
        ButtonType cancel = new ButtonType("Отменить");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы действительно хотите удалить дорогу?", delete, cancel);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Подтвердите удаление");
        return alert;
    }
}