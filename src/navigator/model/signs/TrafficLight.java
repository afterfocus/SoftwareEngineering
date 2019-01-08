package navigator.model.signs;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import navigator.model.Junction;

/**
 * Класс светофора
 */
public class TrafficLight {
    private Rectangle body;
    private Circle red;
    private Circle yellow;
    private Circle green;

    /**
     * Инициализация светофора
     * @param junction перекрёсток для установки светофора
     */
    public TrafficLight(Junction junction) {

        body = new Rectangle(10, 22, Color.rgb(35,35,35));
        body.xProperty().bind(junction.centerXProperty().add(junction.radiusProperty().multiply(0.5)));
        body.yProperty().bind(junction.centerYProperty().subtract(junction.radiusProperty().multiply(2.2)));

        red = new Circle(3, Color.RED);
        red.centerXProperty().bind(body.xProperty().add(5));
        red.centerYProperty().bind(body.yProperty().add(5));
        yellow = new Circle(3, Color.YELLOW);
        yellow.centerXProperty().bind(body.xProperty().add(5));
        yellow.centerYProperty().bind(body.yProperty().add(11));
        green = new Circle(3, Color.GREEN);
        green.centerXProperty().bind(body.xProperty().add(5));
        green.centerYProperty().bind(body.yProperty().add(17));

        ((Pane)junction.getParent()).getChildren().addAll(body, red, yellow, green);
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        Pane pane = (Pane)body.getParent();
        pane.getChildren().removeAll(red, yellow, green, body);
    }
}