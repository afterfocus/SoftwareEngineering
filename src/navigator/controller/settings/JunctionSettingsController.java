package navigator.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import navigator.model.Junction;

import java.util.Optional;

/**
 * Контроллер настройки параметров перекрёстка
 */
public class JunctionSettingsController {

    @FXML
    private Pane junctionSettings;
    @FXML
    private Button closeButton;
    @FXML
    private CheckBox trafficLightsCheckBox = new CheckBox();
    @FXML
    private Spinner<Integer> redPhaseSpinner = new Spinner<>();
    @FXML
    private Spinner<Integer> greenPhaseSpinner = new Spinner<>();

    private Junction junction;
    private Alert alert;
    private ButtonType delete;

    /**
     * Инициализация
     */
    @FXML
    public void initialize() {

        //Инициализация окна подтверждения
        alert = getAlert();

        //Кнопка закрыть
        closeButton.setOnMouseClicked(e -> junctionSettings.setVisible(false));

        //Светофор
        trafficLightsCheckBox.selectedProperty().addListener(e -> {
            if(trafficLightsCheckBox.isSelected()) {
                redPhaseSpinner.setDisable(false);
                greenPhaseSpinner.setDisable(false);
            }
            else {
                redPhaseSpinner.setDisable(true);
                greenPhaseSpinner.setDisable(true);
            }
        });

        //Длительность фаз
        redPhaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 120, 30));
        greenPhaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 120, 30));
    }

    /**
     * Передача перекрёстка для настройки
     * @param junction настраиваемый перекрёсток
     */
    public void setJunction(Junction junction) {
        this.junction = junction;
        if(junction.isTrafficLights()) {
            trafficLightsCheckBox.setSelected(true);
            redPhaseSpinner.setDisable(false);
            greenPhaseSpinner.setDisable(false);
        }
        else {
            trafficLightsCheckBox.setSelected(false);
            redPhaseSpinner.setDisable(true);
            greenPhaseSpinner.setDisable(true);
        }
        redPhaseSpinner.getValueFactory().setValue(junction.getRedPhase());
        greenPhaseSpinner.getValueFactory().setValue(junction.getGreenPhase());
        junctionSettings.setLayoutX(junction.getCenterX());
        junctionSettings.setLayoutY(junction.getCenterY());
    }

    /**
     * Нажатие на кнопку сохранить устанавливает новые значения параметров перекрёстка и закрывает окно настроек
     */
    @FXML
    private void save() {
        junction.setTrafficLights(trafficLightsCheckBox.isSelected());
        junction.setRedPhase(redPhaseSpinner.getValue());
        junction.setGreenPhase(greenPhaseSpinner.getValue());
        junctionSettings.setVisible(false);
    }

    /**
     * Нажатие на кнопку удалить удаляет перекрёсток в случае подтверждения удаления
     */
    @FXML
    private void delete() {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == delete) {
            junction.getMap().removeJunction(junction);
            junctionSettings.setVisible(false);
        }
    }

    /**
     * Инициализация окна подтверждения
     * @return окно подтверждения
     */
    private Alert getAlert() {
        delete = new ButtonType("Удалить");
        ButtonType cancel = new ButtonType("Отменить");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы действительно хотите удалить перекрёсток?", delete, cancel);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Подтвердите удаление");
        return alert;
    }
}
