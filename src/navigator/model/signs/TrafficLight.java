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
import navigator.model.enums.LabelType;

/**
 * Класс светофора
 */
public class TrafficLight extends Rectangle {
    private Circle red;
    private Circle yellow;
    private Circle green;
    private Text label;

    /**
     * Инициализация светофора
     * @param junction перекрёсток для установки светофора
     */
    public TrafficLight(Junction junction, LabelType labelType) {

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

        if(labelType == LabelType.TIME || labelType == LabelType.FUEL) {
            String text;
            if (labelType == LabelType.TIME) text = ((double)Math.round((junction.getRedPhase() + junction.getGreenPhase()) / 2 * 10)) / 10 + " с";
            else text = ((double)Math.round(((double)((junction.getRedPhase() + junction.getGreenPhase()) / 2)) / 3.6 * junction.getMap().getCar().getFuelConsumption() / 9 * 10)) / 10 + " мл";
            label = new Text(text);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            label.setFill(Color.WHITE);
            label.setEffect(new DropShadow());
            label.xProperty().bind(xProperty());
            label.yProperty().bind(yProperty().subtract(3));
            label.setMouseTransparent(true);
            ((Pane) junction.getParent()).getChildren().addAll(this, red, yellow, green, label);
        }
        else ((Pane) junction.getParent()).getChildren().addAll(this, red, yellow, green);
    }

    public void setLabelType(Junction junction, LabelType labelType) {
        if (labelType == LabelType.TIME || labelType == LabelType.FUEL) {
            String text;
            if (label == null) {
                if (labelType == LabelType.TIME) text = ((double)Math.round((junction.getRedPhase() + junction.getGreenPhase()) / 2 * 10)) / 10 + " с";
                else text = ((double)Math.round(((double)((junction.getRedPhase() + junction.getGreenPhase()) / 2)) / 3.6 * junction.getMap().getCar().getFuelConsumption() / 9 * 10)) / 10 + " мл";
                label = new Text(text);
                label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                label.setFill(Color.WHITE);
                label.setEffect(new DropShadow());
                label.xProperty().bind(xProperty());
                label.yProperty().bind(yProperty().subtract(3));
                label.setMouseTransparent(true);
                ((Pane) getParent()).getChildren().add(label);
            }
            else {
                if (labelType == LabelType.TIME) text = ((double)Math.round((junction.getRedPhase() + junction.getGreenPhase()) / 2 * 10)) / 10 + " с";
                else text = ((double)Math.round(((double)((junction.getRedPhase() + junction.getGreenPhase()) / 2)) / 3.6 * junction.getMap().getCar().getFuelConsumption() / 9 * 10)) / 10 + " мл";
                label.setText(text);
            }
        } else {
            if (label != null) {
                ((Pane) getParent()).getChildren().remove(label);
                label = null;
            }
        }
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        if (label != null) ((Pane) getParent()).getChildren().removeAll(label, red, yellow, green, this);
        else ((Pane) getParent()).getChildren().removeAll( red, yellow, green, this);
    }
}