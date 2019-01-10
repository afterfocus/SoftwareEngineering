package navigator.controller;

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
    private CheckBox trafficLightsCheckBox;
    @FXML
    private Spinner<Integer> redPhaseSpinner;
    @FXML
    private Spinner<Integer> greenPhaseSpinner;

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

        //Признак наличия светофора
        trafficLightsCheckBox.selectedProperty().addListener(e -> {
            if(trafficLightsCheckBox.isSelected()) {
                redPhaseSpinner.setDisable(false);
                redPhaseSpinner.getValueFactory().setValue(junction.getRedPhase());
                greenPhaseSpinner.setDisable(false);
                greenPhaseSpinner.getValueFactory().setValue(junction.getGreenPhase());
            }
            else {
                trafficLightsCheckBox.setSelected(false);
                redPhaseSpinner.setDisable(true);
                greenPhaseSpinner.setDisable(true);
            }
        });

        //Длительность фаз
        redPhaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 30));
        greenPhaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 30));
    }

    /**
     * Передача перекрёстка для настройки
     * @param junction настраиваемый перекрёсток
     */
    void setJunction(Junction junction) {
        this.junction = junction;
        if(junction.isTrafficLights()) {
            trafficLightsCheckBox.setSelected(true);
            redPhaseSpinner.setDisable(false);
            redPhaseSpinner.getValueFactory().setValue(junction.getRedPhase());
            greenPhaseSpinner.setDisable(false);
            greenPhaseSpinner.getValueFactory().setValue(junction.getGreenPhase());
        }
        else {
            trafficLightsCheckBox.setSelected(false);
            redPhaseSpinner.setDisable(true);
            greenPhaseSpinner.setDisable(true);
        }
    }

    /**
     * Нажатие на кнопку сохранить устанавливает новые значения параметров перекрёстка и закрывает окно настроек
     */
    @FXML
    void save() {
        junction.setTrafficLights(trafficLightsCheckBox.isSelected());
        junction.setRedPhase(redPhaseSpinner.getValue());
        junction.setGreenPhase(greenPhaseSpinner.getValue());
        junctionSettings.setVisible(false);
    }

    /**
     * Нажатие на кнопку удалить удаляет перекрёсток в случае подтверждения удаления
     */
    @FXML
    void delete() {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == delete) {
            junction.dispose();
            junctionSettings.setVisible(false);
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
        alert.setContentText("Вы действительно хотите удалить перекрёсток?");
        alert.getButtonTypes().clear();
        delete = new ButtonType("Удалить");
        ButtonType cancel = new ButtonType("Отменить");
        alert.getButtonTypes().addAll(delete, cancel);
        return alert;
    }
}
