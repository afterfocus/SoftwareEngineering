package navigator.model.signs;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import navigator.model.Junction;

/**
 * Класс светофора
 */
public class TrafficLight extends Rectangle {
    private Circle red;
    private Circle yellow;
    private Circle green;

    /**
     * Инициализация светофора
     * @param junction перекрёсток для установки светофора
     */
    public TrafficLight(Junction junction) {

        super(12, 26, Color.rgb(25,25,25));
        setArcHeight(6);
        setArcWidth(6);
        xProperty().bind(junction.centerXProperty().add(junction.radiusProperty().multiply(0.4)));
        yProperty().bind(junction.centerYProperty().subtract(junction.radiusProperty().multiply(2.2)));

        red = new Circle(3, Color.rgb(255, 0, 0));
        red.centerXProperty().bind(xProperty().add(6));
        red.centerYProperty().bind(yProperty().add(6));
        yellow = new Circle(3, Color.rgb(230, 220, 0));
        yellow.centerXProperty().bind(xProperty().add(6));
        yellow.centerYProperty().bind(yProperty().add(13));
        green = new Circle(3, Color.rgb(0, 220, 0));
        green.centerXProperty().bind(xProperty().add(6));
        green.centerYProperty().bind(yProperty().add(20));

        ((Pane)junction.getParent()).getChildren().addAll(this, red, yellow, green);
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        ((Pane) getParent()).getChildren().removeAll(red, yellow, green, this);
    }
}