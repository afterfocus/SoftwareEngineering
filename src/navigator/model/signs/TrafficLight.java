package navigator.model.signs;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import navigator.model.Junction;

/**
 * Класс светофора
 */
public class TrafficLight extends Rectangle {
    private Circle red;
    private Circle yellow;
    private Circle green;
    private Text time;

    /**
     * Инициализация светофора
     * @param junction перекрёсток для установки светофора
     */
    public TrafficLight(Junction junction, boolean isTimeShown) {

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

        if(isTimeShown) {
            double averageTime = (double) (junction.getRedPhase() + junction.getGreenPhase()) / 2;
            time = new Text(Math.round(averageTime * 10) / 10 + " с");
            time.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            time.setFill(Color.WHITE);
            time.setEffect(new DropShadow());
            time.xProperty().bind(xProperty());
            time.yProperty().bind(yProperty().subtract(3));
            time.setMouseTransparent(true);
            ((Pane) junction.getParent()).getChildren().addAll(this, red, yellow, green, time);
        }
        else ((Pane) junction.getParent()).getChildren().addAll(this, red, yellow, green);

    }

    public void setTimeShown(Junction junction, boolean isTimeShown) {
        if (isTimeShown) {
            if (time == null) {
                double averageTime = (double) (junction.getRedPhase() + junction.getGreenPhase()) / 2;
                time = new Text(Math.round(averageTime * 10) / 10 + " с");
                time.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                time.setFill(Color.WHITE);
                time.setEffect(new DropShadow());
                time.xProperty().bind(xProperty());
                time.yProperty().bind(yProperty().subtract(3));
                time.setMouseTransparent(true);
                ((Pane) getParent()).getChildren().add(time);
            }
        }
        else {
            if (time != null) {
                ((Pane) getParent()).getChildren().remove(time);
                time = null;
            }
        }
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        if (time != null) ((Pane) getParent()).getChildren().removeAll(time, red, yellow, green, this);
        else ((Pane) getParent()).getChildren().removeAll( red, yellow, green, this);
    }
}