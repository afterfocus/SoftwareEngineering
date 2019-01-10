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
public class SpeedLimitSign extends Circle {
    private Circle innerCircle;
    private Text speedLabel;

    /**
     * Инициализация знака
     * @param road дорога для установки знака
     */
    public SpeedLimitSign(Road road) {
        super(15, Color.RED);
        centerXProperty().bind(road.getStart().centerXProperty().add(road.getEnd().centerXProperty()).divide(2));
        centerYProperty().bind(road.getStart().centerYProperty().add(road.getEnd().centerYProperty()).divide(2));
        innerCircle = new Circle(11, Color.WHITE);
        innerCircle.centerXProperty().bind(centerXProperty());
        innerCircle.centerYProperty().bind(centerYProperty());

        int speed = road.getSpeedLimit();
        speedLabel = new Text(speed + "");

        speedLabel.xProperty().bind(innerCircle.centerXProperty().subtract(speedLabel.getLayoutBounds().getWidth() / 2));
        speedLabel.yProperty().bind(innerCircle.centerYProperty().add(speed < 100 ? 5.66 : 5.357));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, speed < 100 ? 15 : 13));

        ((Pane)road.getForwardLine().getParent()).getChildren().addAll(this, innerCircle, speedLabel);

        setMouseTransparent(true);
        innerCircle.setMouseTransparent(true);
        speedLabel.setMouseTransparent(true);
    }

    /**
     * Изменение ограничения скорости
     * @param speed новая максимально-разрешенная скорость
     */
    public void setSpeed(int speed) {
        speedLabel.setText(speed + "");
        speedLabel.xProperty().bind(innerCircle.centerXProperty().subtract(speedLabel.getLayoutBounds().getWidth() / 2));
        speedLabel.yProperty().bind(innerCircle.centerYProperty().add(speed < 100 ? 5.66 : 5.357));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, speed < 100 ? 15 : 13));
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        ((Pane)getParent()).getChildren().removeAll(speedLabel, this, innerCircle);
    }
}
