package navigator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import navigator.model.enums.LabelType;
import navigator.model.signs.TrafficLight;

import java.util.LinkedList;
import java.util.List;

/**
 * Класс перекрёстка
 */
public class Junction extends Circle {

    private Map map;
    private int id;
    private double x;
    private double y;
    private boolean isPicked;
    private boolean isTrafficLights;
    private int redPhase;
    private int greenPhase;

    private TrafficLight trafficLight;
    private List<Road> roads;

    /**
     * Инициализация перекрёстка
     * @param map ссылка на карту-родителя
     * @param id уникальный идентификатор перекрёстка
     * @param screenX экранная координата Х перекрёстка
     * @param screenY экранная координата Y перекрёстка
     */
    Junction(Map map, int id, double screenX, double screenY) {
        super(screenX, screenY, 4.5 * (map.getScale() + 1.5) + 1, map.getJunctionColor());

        x = (getCenterX() - map.getOffsetX()) / map.getScale() - map.getTranslationX();
        y = (getCenterY() - map.getOffsetY()) / map.getScale() - map.getTranslationY();

        this.map = map;
        this.id = id;

        isPicked = false;
        roads = new LinkedList<>();
        isTrafficLights = false;
        redPhase = 30;
        greenPhase = 30;
    }

    /**
     * Десериализация перекрёстка
     */
    public Junction (Map map, Pane mapArea, int id, double x, double y, boolean isTrafficLights, int redPhase, int greenPhase) {
        super(  (x + map.getTranslationX()) * map.getScale() + map.getOffsetX(),
                (y + map.getTranslationY()) * map.getScale() + map.getOffsetY(),
                4.5 * (map.getScale() + 1.5) + 1, map.getJunctionColor());

        this.map = map;
        this.id = id;
        this.x = x;
        this.y = y;
        this.redPhase = redPhase;
        this.greenPhase = greenPhase;

        isPicked = false;
        roads = new LinkedList<>();

        map.addJunction(this);
        mapArea.getChildren().add(this);
        setTrafficLights(isTrafficLights);
    }

    /**
     * @return уникальный идентификатор перекрёстка
     */
    public int getID(){
        return id;
    }

    /**
     * @return координата Х мировых координат
     */
    public double getX() {
        return x;
    }

    /**
     * @return координата Y мировых координат
     */
    public double getY() {
        return y;
    }

    /**
     * @return признак наличия светофора
     */
    public boolean isTrafficLights() {
        return isTrafficLights;
    }

    /**
     * Изменить признак наличия светофора
     * @param isTrafficLights новое значение признака
     */
    public void setTrafficLights(boolean isTrafficLights) {
        this.isTrafficLights = isTrafficLights;
        if (isTrafficLights) {
            if (trafficLight == null) trafficLight = new TrafficLight(this, false);
        }
        else {
            if (trafficLight != null) trafficLight.dispose();
            trafficLight = null;
        }
    }

    /**
     * @return длительность зелёной фазы светофора
     */
    public int getGreenPhase() {
        return greenPhase;
    }

    /**
     * Изменить длительность зеленой фазы светофора
     * @param greenPhase новое значение зеленой фазы
     */
    public void setGreenPhase(int greenPhase) {
        this.greenPhase = greenPhase;
    }

    /**
     * @return длительность красной фазы светофора
     */
    public int getRedPhase() {
        return redPhase;
    }

    /**
     * Изменить длительность красной фазы светофора
     * @param redPhase новое значение красной фазы
     */
    public void setRedPhase(int redPhase) {
        this.redPhase = redPhase;
    }

    /**
     * Присоеденить дорогу
     * @param road дорога для присоединения
     */
    void addRoad(Road road) {
        roads.add(road);
    }

    /**
     * Удалить дорогу из списка присоединенных дорог
     * @param road удаляемая дорога
     */
    void removeRoad(Road road) {
        roads.remove(road);
    }

    /**
     * Получить список дорог
     * @return список дорог, присоединенных к перекрёстку
     */
    public List<Road> getRoads() {
        return roads;
    }


    //================================= Методы, связанные с отображением перекрёстка ==================================

    public Map getMap() {
        return map;
    }

    /**
     * Изменить экранные координаты перекрёстка
     * @param screenX новая экранная координата Х
     * @param screenY новая экранная координата Y
     */
    public void setScreenXY(double screenX, double screenY) {
        setCenterX(screenX);
        setCenterY(screenY);
        x = (getCenterX() - map.getOffsetX()) / map.getScale() - map.getTranslationX();
        y = (getCenterY() - map.getOffsetY()) / map.getScale() - map.getTranslationY();
    }

    /**
     * Отметить перекрёсток (сбрасывает отметку при повторном вызове)
     * @return true, если перекрёсток отмечен или false, если перекрёсток уже был отмечен ранее
     */
    public boolean pick() {
        if(!isPicked) {
             isPicked = true;
             setFill(Color.RED);
             setRadius(6 * (map.getScale() + 1.5) + 1);
        }
        else {
            isPicked = false;
            setFill(map.getJunctionColor());
            setRadius(4.5 * (map.getScale() + 1.5) + 1);
        }
        return isPicked;
    }

    void notifyScaleChanged() {
        setRadius(isPicked ? 6 : 4.5 * (map.getScale() + 1.5) + 1);
        notifyLocationChanged();
    }

    /**
     * Пересчитать экранные координаты перекрёстка
     */
    void notifyLocationChanged() {
        setCenterX((x + map.getTranslationX()) * map.getScale() + map.getOffsetX());
        setCenterY((y + map.getTranslationY()) * map.getScale() + map.getOffsetY());
    }

    void notifyLabelTypeChanged() {
        if (trafficLight != null) trafficLight.setTimeShown(this, map.getLabelsType() == LabelType.TIME);
    }


    //============================================== Утилитарные методы ===============================================

    /**
     * @param obj объект для сравнения
     * @return true, если уникальные идентификаторы перекрёстков равны
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Junction) {
            Junction j = (Junction) obj;
            return j.id == id;
        }
        else return false;
    }

    /**
     * @return хешкод = id перекрёстка
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Уничтожить обьект
     */
    void dispose() {
        if (trafficLight != null) trafficLight.dispose();
        for(Object r: roads.toArray()) map.removeRoad((Road)r);
    }
}
