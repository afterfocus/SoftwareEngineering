package navigator.model.signs;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import navigator.model.Road;

/**
 * Класс знака ограничения скорости
 */
public class SpeedLimitSign {
    private Circle outerCircle;
    private Circle innerCircle;
    private Text speedLabel;

    /**
     * Инициализация знака
     * @param road дорога для установки знака
     */
    public SpeedLimitSign(Road road) {

        outerCircle = new Circle(15, Color.RED);
        outerCircle.centerXProperty().bind(road.getStart().centerXProperty().add(road.getEnd().centerXProperty()).divide(2));
        outerCircle.centerYProperty().bind(road.getStart().centerYProperty().add(road.getEnd().centerYProperty()).divide(2));
        innerCircle = new Circle(11, Color.WHITE);
        innerCircle.centerXProperty().bind(outerCircle.centerXProperty());
        innerCircle.centerYProperty().bind(outerCircle.centerYProperty());

        int speed = road.getSpeedLimit();
        speedLabel = new Text(speed + "");
        speedLabel.xProperty().bind(innerCircle.centerXProperty().subtract(outerCircle.radiusProperty().divide(speed < 100 ? 1.96 : 1.4)));
        speedLabel.yProperty().bind(innerCircle.centerYProperty().add(outerCircle.radiusProperty().divide(speed < 100 ? 2.65 : 2.8)));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, speed < 100 ? 15 : 13));

        ((Pane)road.getForwardLine().getParent()).getChildren().addAll(outerCircle, innerCircle, speedLabel);
    }

    /**
     * Изменение ограничения скорости
     * @param speed новая максимально-разрешенная скорость
     */
    public void setSpeedLimit(int speed) {
        speedLabel.setText(speed + "");
        speedLabel.xProperty().bind(innerCircle.centerXProperty().subtract(outerCircle.radiusProperty().divide(speed < 100 ? 1.96 : 1.4)));
        speedLabel.yProperty().bind(innerCircle.centerYProperty().add(outerCircle.radiusProperty().divide(speed < 100 ? 2.65 : 2.8)));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, speed < 100 ? 15 : 13));
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        Pane pane = (Pane)outerCircle.getParent();
        pane.getChildren().removeAll(speedLabel, outerCircle, innerCircle);
    }
}
