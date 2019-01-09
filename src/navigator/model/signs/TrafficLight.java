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

        body = new Rectangle(12, 26, Color.rgb(25,25,25));
        body.setArcHeight(6);
        body.setArcWidth(6);
        body.xProperty().bind(junction.centerXProperty().add(junction.radiusProperty().multiply(0.4)));
        body.yProperty().bind(junction.centerYProperty().subtract(junction.radiusProperty().multiply(2.2)));

        red = new Circle(3, Color.rgb(255, 0, 0));
        red.centerXProperty().bind(body.xProperty().add(6));
        red.centerYProperty().bind(body.yProperty().add(6));
        yellow = new Circle(3, Color.rgb(230, 220, 0));
        yellow.centerXProperty().bind(body.xProperty().add(6));
        yellow.centerYProperty().bind(body.yProperty().add(13));
        green = new Circle(3, Color.rgb(0, 220, 0));
        green.centerXProperty().bind(body.xProperty().add(6));
        green.centerYProperty().bind(body.yProperty().add(20));

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