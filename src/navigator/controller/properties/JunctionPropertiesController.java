package navigator.controller.properties;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import navigator.model.map.Junction;


public class JunctionPropertiesController {
    @FXML
    private Pane junctionProperties;
    @FXML
    private Button closeButton;
    @FXML
    private Label trafficLightsLabel;
    @FXML
    private Label redPhaseLabel;
    @FXML
    private Label greenPhaseLabel;

    /**
     * Инициализация
     */
    @FXML
    public void initialize() {
        //Кнопка закрыть
        closeButton.setOnMouseClicked(e -> junctionProperties.setVisible(false));
    }

    public void setJunction(Junction junction) {
        trafficLightsLabel.setText(junction.isTrafficLights() ? "Да" : "Нет");
        redPhaseLabel.setText(junction.isTrafficLights() ? junction.getRedPhase() + " с" : "—");
        greenPhaseLabel.setText(junction.isTrafficLights() ? junction.getGreenPhase() + " с" : "—");
        junctionProperties.setLayoutX(junction.getCenterX());
        junctionProperties.setLayoutY(junction.getCenterY());
    }
}
