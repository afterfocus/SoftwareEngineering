package navigator.model.signs;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import navigator.model.Road;

/**
 * Класс знака ограничения скорости
 */
public class NoWaySign extends Circle {

    private Circle innerCircle;
    private Rectangle rect;

    /**
     * Инициализация знака
     * @param road дорога для установки знака
     */
    public NoWaySign(Road road) {

        super(15, Color.WHITE);
        centerXProperty().bind(road.getStart().centerXProperty().add(road.getEnd().centerXProperty()).divide(2));
        centerYProperty().bind(road.getStart().centerYProperty().add(road.getEnd().centerYProperty()).divide(2));
        innerCircle = new Circle(14, Color.RED);
        innerCircle.centerXProperty().bind(centerXProperty());
        innerCircle.centerYProperty().bind(centerYProperty());

        rect = new Rectangle(19, 5, Color.WHITE);
        rect.xProperty().bind(centerXProperty().subtract(9.5));
        rect.yProperty().bind(centerYProperty().subtract(2.5));

        ((Pane)road.getForwardLine().getParent()).getChildren().addAll(this, innerCircle, rect);

        setMouseTransparent(true);
        innerCircle.setMouseTransparent(true);
        rect.setMouseTransparent(true);
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        ((Pane)getParent()).getChildren().removeAll(rect, this, innerCircle);
    }
}