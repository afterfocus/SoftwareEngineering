package navigator.database;

import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import navigator.controller.MapEditorController;
import navigator.model.Junction;
import navigator.model.Map;
import navigator.model.Road;
import navigator.model.RoadSurface;

import java.io.*;

/**
 * Класс работы с БД и файлами
 */
public class DAO {

    /**
     * Получить названия улиц из БД
     * @return массив названий улиц
     */
    public static String[] getStreetNames() {
        return new String[]{ "Московское шоссе", "Ташкентская", "Стара-Загора", "Ново-Садовая", "Лукачева",
                "Революционная", "Маломосковская", "Авроры", "Гагарина", "Ботанический пер.", "Луначарского",
                "Гаражная", "Гая", "Подшипниковая", "Ерошевского", "Мичурина", "пр.Масленникова", "Академика Солдатова"};
    }

    /**
     * Получить дорожные покрытия из БД
     * @return массив дорожных покрытий
     */
    public static RoadSurface[] getRoadSurfaces() {
        return new RoadSurface[] {
                new RoadSurface("Шоссе", 1.0),
                new RoadSurface("Гладкий асфальт", 0.8),
                new RoadSurface("Асфальт", 0.7),
                new RoadSurface("Внутриквартальная дорога", 0.5),
                new RoadSurface("Разбитый асфальт", 0.4),
                new RoadSurface("Проселочная дорога", 0.3),
                new RoadSurface("Грунт", 0.2),
                new RoadSurface("Перекрыто", 0.001)
        };
    }

    /**
     * Сохранить карту в файл
     * @param map карта для сохранения
     * @param filename имя файла
     */
    public static void writeMapToFile(Map map, String filename) {
        try(FileOutputStream fos = new FileOutputStream(filename)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //сохранение перекрёстков
            oos.writeInt(map.getJunctionList().size());
            for (Junction j: map.getJunctionList()) {
                oos.writeInt(j.getID());
                oos.writeDouble(j.getX());
                oos.writeDouble(j.getY());
                oos.writeBoolean(j.isTrafficLights());
                oos.writeInt(j.getRedPhase());
                oos.writeInt(j.getGreenPhase());
            }

            //сохранение дорог
            oos.writeInt(map.getRoadList().size());
            for (Road r: map.getRoadList()) {
                oos.writeInt(r.getStart().getID());
                oos.writeInt(r.getEnd().getID());
                oos.writeObject(r.getName());
                oos.writeChar(r.getDirection());
                oos.writeInt(r.getSpeedLimit());
                oos.writeObject(r.getRoadSurface().getName());
                oos.writeDouble(r.getRoadSurface().getCoefficient());
            }

            //сохранение счетчика
            oos.writeInt(map.getIdCounter());
            oos.flush();
            oos.close();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        }
    }

    /**
     * Загрузить карту из файла
     * @param controller контроллер для доступа к родительским компонентам
     * @param filename имя файла
     */
    public static void readMapFromFile(MapEditorController controller, String filename) {
        try(FileInputStream fis = new FileInputStream(filename)) {
            ObjectInputStream ois = new ObjectInputStream(fis);

            Map map = controller.getMap();
            Pane mapArea = controller.getMapArea();
            DropShadow dropShadow = controller.getDropShadow();

            map.clear();
            controller.getMapArea().getChildren().clear();

            //Чтение перекрёстков
            int n = ois.readInt();
            for(int i = 0; i < n; i++) {
                Junction j = new Junction(map, mapArea, ois.readInt(), ois.readDouble(), ois.readDouble(), ois.readBoolean(), ois.readInt(), ois.readInt());

                //Нажатие на перекрёсток
                j.setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) controller.setJunctionDragging(true);
                    else controller.showJunctionSettings(j);
                });

                //Перемещение перекрёстка
                j.setOnMouseDragged(event -> {
                    j.setScreenXY(event.getX(), event.getY());
                    for (Road r : j.getRoads()) r.updateLocation();
                });
                j.setOnMouseReleased(event -> controller.setJunctionDragging(false));
            }

            //Чтение дорог
            n = ois.readInt();
            for(int i = 0; i < n; i++) {
                Road road = new Road(map, mapArea, dropShadow,
                        map.getJunctionById(ois.readInt()),
                        map.getJunctionById(ois.readInt()),
                        (String)ois.readObject(), ois.readChar(), ois.readInt(),
                        new RoadSurface((String)ois.readObject(), ois.readDouble()));

                //Нажатие на дорогу
                road.getForwardLine().setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) controller.showRoadSettings(road);
                });
                road.getBackwardLine().setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) controller.showRoadSettings(road);
                });
            }

            //Чтение счетчика
            map.setIdCounter(ois.readInt());
            ois.close();

        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        }
    }
}
