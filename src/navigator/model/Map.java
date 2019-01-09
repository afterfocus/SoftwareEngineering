package navigator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Хранит информацию о перекрёстках и дорогах
 */
public class Map {

    private int idCounter;
    private List<Junction> junctionList;
    private Set<Road> roadList;

    private double offsetX;
    private double offsetY;
    private double scale;
    private double translationX;
    private double translationY;

    private Color junctionColor;

    /**
     * Инициализация карты
     * @param offsetX центровка карты по ширине (середина окна)
     * @param offsetY центровка карты по высоте (середина окна)
     * @param scale масштаб
     * @param translationX сдвиг по Х
     * @param translationY сдвиг по Y
     * @param junctionColor цвет перекрёстков
     */
    public Map(double offsetX, double offsetY, double scale, double translationX, double translationY, Color junctionColor) {

        idCounter = -1;
        junctionList = new LinkedList<>();
        roadList = new HashSet<>();

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.translationX = translationX;
        this.translationY = translationY;

        this.junctionColor = junctionColor;
    }

    /**
     * Изменить масштаб
     * @param scale новый масштаб
     */
    public void setScale(double scale) {
        this.scale = scale;
        for (Junction j : junctionList) {
            j.setRadius(4.5 * (scale + 1.5) + 1);
            j.updateLocation();
        }
        for (Road r : roadList) r.updateLocation();
    }

    /**
     * Изменить сдвиг карты
     * @param translationX новый сдвиг по Х
     * @param translationY новый сдвиг по У
     */
    public void setTranslation(double translationX, double translationY) {
        this.translationX = translationX;
        this.translationY = translationY;
        for (Junction j : junctionList) j.updateLocation();
        for (Road r : roadList) r.updateLocation();
    }

    /**
     * Добавить перекрёсток
     * @param screenX экранная координата Х перекрёстка
     * @param screenY экранная координата У перекрэстка
     * @return ссылка на добавленный перекрёсток
     */
    public Junction addJunction(double screenX, double screenY) {
        idCounter++;
        Junction junction = new Junction(this, idCounter, screenX, screenY);
        junctionList.add(junction);
        return junction;
    }

    /**
     * Добавить перекрёсток
     */
    void addJunction(Junction junction) {
        junctionList.add(junction);
    }

    /**
     * Добавить дорогу
     * @param first перекрёсток - начало дороги
     * @param second перекрёсток - конец дороги
     * @return ссылка на добавленную дорогу или null, если такая дорога уже есть
     */
    public Road addRoad(Junction first, Junction second) {
        first.pick();
        second.pick();
        Road road = new Road(this, first, second);
        if (roadList.add(road)) return road;
        else return null;
    }

    /**
     * Добавить перекрёсток
     */
    void addRoad(Road road) {
        roadList.add(road);
    }

    /**
     * Получить список перекрёстков
     * @return список перекрёстков
     */
    public List<Junction> getJunctionList() {
        return junctionList;
    }

    /**
     * Получить список дорог
     * @return список дорог
     */
    public Set<Road> getRoadList() {
        return roadList;
    }

    /**
     * Изменить центровку карты по X
     * @param offsetX новая координата Х центра
     */
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        for (Junction j : junctionList) j.updateLocation();
        for (Road r : roadList) r.updateLocation();
    }

    /**
     * Изменить центровку карты по X
     * @param offsetY новая координата Y центра
     */
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        for (Junction j : junctionList) j.updateLocation();
        for (Road r : roadList) r.updateLocation();
    }

    /**
     * Получить центровку карты по X
     * @return координата Х центра
     */
    double getOffsetX() {
        return offsetX;
    }

    /**
     * Получить центровку карты по Y
     * @return координата Y центра
     */
    double getOffsetY() {
        return offsetY;
    }

    /**
     * Получить текущий масштаб
     * @return масштаб
     */
    double getScale() {
        return scale;
    }

    /**
     * Получить сдвиг по X
     * @return сдвиг карты по X
     */
    double getTranslationX() {
        return translationX;
    }

    /**
     * Получить сдвиг по Y
     * @return сдвиг карты по Y
     */
    double getTranslationY() {
        return translationY;
    }

    /**
     * Удалить перекрёсток с карты
     * @param junction удаляемый перекрёсток
     */
    void removeJunction(Junction junction) {
        ((Pane)junction.getParent()).getChildren().remove(junction);
        junctionList.remove(junction);
    }

    /**
     * Удалить дорогу с карты
     * @param road удаляемая дорога
     */
    void removeRoad(Road road) {
        ((Pane)road.getForwardLine().getParent()).getChildren().remove(road.getForwardLine());
        ((Pane)road.getBackwardLine().getParent()).getChildren().remove(road.getBackwardLine());
        roadList.remove(road);
    }

    /**
     * Получить перекрёсток по его идентификатору
     * @param id уникальный идентификатор перекрёстка
     * @return найденный перекрёсток (null, если не найден)
     */
    public Junction getJunctionById(int id) {
        for (Junction j: junctionList) {
            if (j.getID() == id) return j;
        }
        return null;
    }

    /**
     * @return текущее значение счетчика идентификаторов
     */
    public int getIdCounter() {
        return idCounter;
    }

    /**
     * Изменить значение счетчика идентификаторов
     * @param idCounter новое значение счетчика
     */
    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    /**
     * @return цвет перекрёстков
     */
    Color getJunctionColor() {
        return junctionColor;
    }

    /**
     * Удалить все объекты с карты
     */
    public void clear() {
        junctionList.clear();
        roadList.clear();
    }
}
