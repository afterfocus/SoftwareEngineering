package navigator.model;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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

    private Line forward;
    private Line backward;
    private SpeedLimitSign speedLimitSign;
    private NoWaySign noWaySign;

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
        roadSurface = new RoadSurface("Асфальт", 0.8);

        start.addRoad(this);
        end.addRoad(this);

        forward = new Line(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY());
        forward.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));
        forward.setStrokeWidth(2 * (map.getScale() + 2));
        backward = new Line(end.getCenterX(), end.getCenterY(), start.getCenterX(), start.getCenterY());
        backward.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));
        backward.setStrokeWidth(2 * (map.getScale() + 2));
        calcTranslate(forward);
        calcTranslate(backward);
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

        start.addRoad(this);
        end.addRoad(this);

        forward = new Line(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY());
        forward.setStrokeWidth(2 * (map.getScale() + 2));
        forward.setEffect(dropShadow);
        backward = new Line(end.getCenterX(), end.getCenterY(), start.getCenterX(), start.getCenterY());
        backward.setStrokeWidth(2 * (map.getScale() + 2));
        backward.setEffect(dropShadow);

        calcTranslate(forward);
        calcTranslate(backward);

        map.addRoad(this);
        mapArea.getChildren().add(0, forward);
        mapArea.getChildren().add(0, backward);

        setDirection(direction);
        setRoadSurface(roadSurface);
        setSpeedLimit(speedLimit);
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
            forward.setStroke(Color.rgb(25, 25, 25));
            backward.setStroke(Color.rgb(25, 25, 25));

            if (noWaySign == null) noWaySign = new NoWaySign(this);
            speedLimitSign = null;

        } else {
            forward.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));
            backward.setStroke(Color.rgb((int) (220 - 120 * roadSurface.getCoefficient()), (int) (170 * roadSurface.getCoefficient() + 50), 0));

            if (noWaySign != null) noWaySign.dispose();
            noWaySign = null;
            setSpeedLimit(speedLimit);
        }
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
            if(speedLimitSign != null) speedLimitSign.dispose();
            speedLimitSign = null;
        }
        else if (roadSurface.getCoefficient() > 0.01) {
            if(speedLimitSign == null) speedLimitSign = new SpeedLimitSign(this);
            else speedLimitSign.setSpeedLimit(speedLimit);
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
     * Изменить направление дороги
     * @param direction новое направление дороги
     */
    public void setDirection(char direction) {
        this.direction = direction;
        if (direction == '↔') {
            forward.setVisible(true);
            backward.setVisible(true);
        }
        else if (direction == '→') {
            forward.setVisible(true);
            backward.setVisible(false);
        }
        else {
            forward.setVisible(false);
            backward.setVisible(true);
        }
    }

    /**
     * @return линия - графическое представление направления движения дороги
     */
    public Line getForwardLine() {
        return forward;
    }

    /**
     * @return линия - графическое представление направления движения дороги
     */
    public Line getBackwardLine() {
        return backward;
    }

    /**
     * Пересчитать экранные координаты дороги
     */
    public void updateLocation() {
        forward.setStartX(start.getCenterX());
        forward.setStartY(start.getCenterY());
        forward.setEndX(end.getCenterX());
        forward.setEndY(end.getCenterY());
        forward.setStrokeWidth(2 * (map.getScale() + 2));
        backward.setStartX(end.getCenterX());
        backward.setStartY(end.getCenterY());
        backward.setEndX(start.getCenterX());
        backward.setEndY(start.getCenterY());
        backward.setStrokeWidth(2 * (map.getScale() + 2));
        calcTranslate(forward);
        calcTranslate(backward);
        length = (int)(Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2)) / 5) * 5;
    }

    /**
     * Вычислить смещение дороги
     */
    private void calcTranslate(Line line) {
        double a = line.getEndY() - line.getStartY();
        double b = line.getEndX() - line.getStartX();
        double c = Math.sqrt(a * a + b * b);
        line.setTranslateX(-3 * (map.getScale()+1) * a / c);
        line.setTranslateY(3 * (map.getScale()+1) * b / c);
    }

    /**
     * Удалить себя
     */
    public void dispose() {
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
