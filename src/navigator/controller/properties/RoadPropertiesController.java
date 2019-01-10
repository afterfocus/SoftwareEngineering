package navigator.controller.properties;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import navigator.model.map.Road;

public class RoadPropertiesController {
    @FXML
    private Pane roadProperties;
    @FXML
    private Button closeButton;
    @FXML
    private Label streetNameLabel;
    @FXML
    private Label directionLabel;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label speedLimitLabel;
    @FXML
    private Label roadSurfaceLabel;

    /**
     * Инициализация
     */
    @FXML
    public void initialize() {
        //Кнопка закрыть
        closeButton.setOnMouseClicked(e -> roadProperties.setVisible(false));
    }

    public void setRoad(Road road) {
        streetNameLabel.setText(road.getName().equals("") ? "—" : road.getName());
        directionLabel.setText(road.getDirection() + "");
        lengthLabel.setText(road.getLength() + " м");
        speedLimitLabel.setText(road.getSpeedLimit() + " км/ч");
        roadSurfaceLabel.setText(road.getRoadSurface() + "");
        roadProperties.setLayoutX((road.getForwardLine().getStartX() + road.getForwardLine().getEndX()) / 2);
        roadProperties.setLayoutY((road.getForwardLine().getStartY() + road.getForwardLine().getEndY()) / 2);
    }
}