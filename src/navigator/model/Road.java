package navigator.model;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import navigator.database.DAO;
import navigator.model.signs.NoWaySign;
import navigator.model.signs.SpeedLimitSign;

/**
 * Класс дороги
 */
public class Road {

    private Map map;

    private Junction start;
    private Junction end;
    private String name;
    private char direction;
    private int length;
    private int speedLimit;
    private RoadSurface roadSurface;

    private Line forwardLine;
    private Line backwardLine;
    private SpeedLimitSign speedLimitSign;
    private NoWaySign noWaySign;
    private Text nameLabel;

    /**
     * Инициализация дороги
     * @param map ссылка на карту-родителя
     * @param start перекрёсток-начало дороги
     * @param end перекрёсток-конец дороги
     */
    Road(Map map, Junction start, Junction end) {
        this.map = map;
        this.start = start;
        this.end = end;

        name = "";
        direction = '↔';
        length = (int)(Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)) / 5) * 5;
        speedLimit = 60;

        forwardLine = new Line(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY());
        backwardLine = new Line(end.getCenterX(), end.getCenterY(), start.getCenterX(), start.getCenterY());

        setRoadSurface(DAO.getRoadSurfaces()[2]);
        updateTranslate();
        updateWidth();
        updateLength();

        start.addRoad(this);
        end.addRoad(this);
    }

    /**
     * Десериализация дороги
     */
    public Road (Map map, Pane mapArea, DropShadow dropShadow, Junction start, Junction end, String name, char direction, int speedLimit, RoadSurface roadSurface) {
        this.map = map;
        this.start = start;
        this.end = end;
        this.name = name;
        length = (int)(Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)) / 5) * 5;

        forwardLine = new Line(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY());
        backwardLine = new Line(end.getCenterX(), end.getCenterY(), start.getCenterX(), start.getCenterY());
        forwardLine.setEffect(dropShadow);
        backwardLine.setEffect(dropShadow);

        map.addRoad(this);
        mapArea.getChildren().add(0, forwardLine);
        mapArea.getChildren().add(0, backwardLine);

        setDirection(direction);
        setRoadSurface(roadSurface);
        setSpeedLimit(speedLimit);
        updateTranslate();
        updateLength();
        updateWidth();

        start.addRoad(this);
        end.addRoad(this);
    }

    /**
     * @return название улицы
     */
    public String getName() {
        return name;
    }

    /**
     * Изменить название улицы
     * @param name новое название улицы
     */
    public void setName(String name) {
        this.name = name;
        if (nameLabel != null) {
            nameLabel.setText(name);
            nameLabel.xProperty().bind(start.centerXProperty().add(end.centerXProperty()).divide(2).subtract(nameLabel.getLayoutBounds().getWidth() / 2));
        }
    }

    /**
     * @return покрытие дороги
     */
    public RoadSurface getRoadSurface() {
        return roadSurface;
    }

    /**
     * Изменить покрытие дороги
     * @param roadSurface новое покрытие
     */
    public void setRoadSurface(RoadSurface roadSurface) {
        this.roadSurface = roadSurface;
        if (roadSurface.getCoefficient() < 0.01) {
            forwardLine.setStroke(Color.rgb(25, 25, 25));
            backwardLine.setStroke(Color.rgb(25, 25, 25));

            if (noWaySign == null) noWaySign = new NoWaySign(this);
            speedLimitSign = null;

        } else {
            forwardLine.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));
            backwardLine.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));

            if (noWaySign != null) noWaySign.dispose();
            noWaySign = null;
            setSpeedLimit(speedLimit);
        }
        forwardLine.setStrokeWidth(1 + 3 * map.getScale() + 3 * roadSurface.getCoefficient());
        backwardLine.setStrokeWidth(1 + 3 * map.getScale() + 3 * roadSurface.getCoefficient());
        if (nameLabel != null) nameLabel.yProperty().bind(start.centerYProperty().add(end.centerYProperty()).divide(2).add(speedLimitSign == null && noWaySign == null ? 3 : 24));
    }

    /**
     * @return ограничение скорости
     */
    public Integer getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Изменить ограничение скорости
     * @param speedLimit новое ограничение скорости
     */
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
        if (speedLimit == 60) {
            if (speedLimitSign != null) {
                speedLimitSign.dispose();
                speedLimitSign = null;
            }
        }
        else if (roadSurface.getCoefficient() > 0.01) {
            if (speedLimitSign == null) speedLimitSign = new SpeedLimitSign(this);
            else speedLimitSign.setSpeed(speedLimit);
        }
        if (nameLabel != null) nameLabel.yProperty().bind(start.centerYProperty().add(end.centerYProperty()).divide(2).add(speedLimitSign == null && noWaySign == null ? 3 : 24));
    }

    /**
     * Изменить отображение названий улиц
     */
    void setNameLabelVisible(boolean visible) {
        if (visible && nameLabel == null) {
            nameLabel = new Text(name);
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            nameLabel.setFill(Color.WHITE);
            nameLabel.setEffect(new DropShadow());
            nameLabel.xProperty().bind(start.centerXProperty().add(end.centerXProperty()).divide(2).subtract(nameLabel.getLayoutBounds().getWidth() / 2));
            nameLabel.yProperty().bind(start.centerYProperty().add(end.centerYProperty()).divide(2).add(speedLimitSign == null && noWaySign == null ? 3 : 24));
            ((Pane) forwardLine.getParent()).getChildren().add(nameLabel);
            nameLabel.setMouseTransparent(true);

        }
        else if (nameLabel != null) {
            ((Pane) nameLabel.getParent()).getChildren().remove(nameLabel);
            nameLabel = null;
        }
    }

    /**
     * Изменить направление дороги
     * @param direction новое направление дороги
     */
    public void setDirection(char direction) {
        this.direction = direction;
        if (direction == '↔') {
            forwardLine.setVisible(true);
            backwardLine.setVisible(true);
        }
        else if (direction == '→') {
            forwardLine.setVisible(true);
            backwardLine.setVisible(false);
        }
        else {
            forwardLine.setVisible(false);
            backwardLine.setVisible(true);
        }
    }

    /**
     * @return начальный перекрёсток дороги
     */
    public Junction getStart() {
        return start;
    }

    /**
     * @return конечый перекрёсток дороги
     */
    public Junction getEnd() {
        return end;
    }

    /**
     * @return длина дороги
     */
    public int getLength() {
        return length;
    }

    /**
     * @return направление дороги
     */
    public Character getDirection() {
        return direction;
    }

    /**
     * @return линия - графическое представление направления движения дороги
     */
    public Line getForwardLine() {
        return forwardLine;
    }

    /**
     * @return линия - графическое представление направления движения дороги
     */
    public Line getBackwardLine() {
        return backwardLine;
    }


    /**
     * Пересчитать экранные координаты дороги
     */
    public void updateLocation() {
        forwardLine.setStartX(start.getCenterX());
        forwardLine.setStartY(start.getCenterY());
        forwardLine.setEndX(end.getCenterX());
        forwardLine.setEndY(end.getCenterY());
        backwardLine.setStartX(end.getCenterX());
        backwardLine.setStartY(end.getCenterY());
        backwardLine.setEndX(start.getCenterX());
        backwardLine.setEndY(start.getCenterY());
    }

    void updateWidth() {
        forwardLine.setStrokeWidth(1 + 3 * map.getScale() + 3 * roadSurface.getCoefficient());
        backwardLine.setStrokeWidth(1 + 3 * map.getScale() + 3 * roadSurface.getCoefficient());
    }

    public void updateLength() {
        length = (int)(Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)) / 5) * 5;
    }

    public void updateTranslate(){
        calcTranslate(forwardLine);
        calcTranslate(backwardLine);
    }

    /**
     * Вычислить смещение линий дороги
     */
    private void calcTranslate(Line line) {
        double a = line.getEndY() - line.getStartY();
        double b = line.getEndX() - line.getStartX();
        double c = Math.sqrt(a * a + b * b);
        line.setTranslateX(-3 * (map.getScale()+1) * a / c);
        line.setTranslateY(3 * (map.getScale()+1) * b / c);
    }

    /**
     * Уничтожение объекта
     */
    public void dispose() {
        if (noWaySign != null) noWaySign.dispose();
        if (speedLimitSign != null) speedLimitSign.dispose();
        if (nameLabel != null) ((Pane)nameLabel.getParent()).getChildren().remove(nameLabel);
        start.removeRoad(this);
        end.removeRoad(this);
        map.removeRoad(this);
    }

    /**
     * Проверка на равенство
     * @param obj объект для сравнения
     * @return true, если у дорог оба перекрёстка одинаковые
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Road) {
            Road r = (Road) obj;
            return (r.start == start && r.end == end) || (r.start == end && r.end == start);
        }
        else return false;
    }

    /**
     * @return хэшкод = id начального перекрёстка * id конечного перекрёстка
     */
    @Override
    public int hashCode() {
        return start.hashCode() * end.hashCode();
    }
}
