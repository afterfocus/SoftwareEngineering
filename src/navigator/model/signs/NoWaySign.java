package navigator.model.signs;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import navigator.model.Road;

/**
 * Класс знака ограничения скорости
 */
public class NoWaySign {
    private Circle outerCircle;
    private Circle innerCircle;
    private Rectangle rect;

    /**
     * Инициализация знака
     * @param road дорога для установки знака
     */
    public NoWaySign(Road road) {

        outerCircle = new Circle(15, Color.WHITE);
        outerCircle.centerXProperty().bind(road.getStart().centerXProperty().add(road.getEnd().centerXProperty()).divide(2));
        outerCircle.centerYProperty().bind(road.getStart().centerYProperty().add(road.getEnd().centerYProperty()).divide(2));
        innerCircle = new Circle(14, Color.RED);
        innerCircle.centerXProperty().bind(outerCircle.centerXProperty());
        innerCircle.centerYProperty().bind(outerCircle.centerYProperty());

        rect = new Rectangle(19, 5, Color.WHITE);
        rect.xProperty().bind(outerCircle.centerXProperty().subtract(9.5));
        rect.yProperty().bind(outerCircle.centerYProperty().subtract(2.5));

        ((Pane)road.getForwardLine().getParent()).getChildren().addAll(outerCircle, innerCircle, rect);
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        Pane pane = (Pane)outerCircle.getParent();
        pane.getChildren().removeAll(rect, outerCircle, innerCircle);
    }
}