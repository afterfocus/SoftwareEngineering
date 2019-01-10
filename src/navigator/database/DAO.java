package navigator.database;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import navigator.model.entities.RoadSurface;
import navigator.model.map.Junction;
import navigator.model.map.LabelType;
import navigator.model.map.Map;
import navigator.model.map.Road;

import java.io.*;

/**
 * Класс работы с БД и файлами
 */
public class DAO {

    /**
     * Получить названия улиц из БД
     *
     * @return массив названий улиц
     */
    public static String[] getStreetNames() {
        return new String[]{"22 Партсъезда", "Академика Солдатова", "Авроры", "Ботанический пер.", "Гагарина", "Гаражная", "Гая",
                "Ерошевского", "Карла Маркса пр.", "Лукачева", "Луначарского", "Маломосковская", "Масленникова пр.", "Мичурина",
                "Московское шоссе", "Ново-Вокзальная", "Ново-Садовая", "Подшипниковая", "Потапова", "Революционная", "Советской армии", "Стара-Загора",
                "Ташкентская", "Фадеева"};
    }

    /**
     * Получить дорожные покрытия из БД
     *
     * @return массив дорожных покрытий
     */
    public static RoadSurface[] getRoadSurfaces() {
        return new RoadSurface[]{
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
     *
     * @param map      карта для сохранения
     * @param filename имя файла
     */
    public static void writeMapToFile(Map map, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //сохранение перекрёстков
            oos.writeInt(map.getJunctionList().size());
            for (Junction j : map.getJunctionList()) {
                oos.writeInt(j.getID());
                oos.writeDouble(j.getX());
                oos.writeDouble(j.getY());
                oos.writeBoolean(j.isTrafficLights());
                oos.writeInt(j.getRedPhase());
                oos.writeInt(j.getGreenPhase());
            }

            //сохранение дорог
            oos.writeInt(map.getRoadList().size());
            for (Road r : map.getRoadList()) {
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
     * @param filename имя файла
     * @param mapArea контейнер для отрисовки карты
     * @param color цвет перекрёстков
     */
    public static Map readMapFromFile(String filename, Pane mapArea, Color color) {
        Map map = new Map(LabelType.NAME, color);
        map.setOffsetX(mapArea.getWidth() / 2);
        map.setOffsetY(mapArea.getHeight() / 2);

        try (FileInputStream fis = new FileInputStream(filename)) {
            ObjectInputStream ois = new ObjectInputStream(fis);

            //Чтение перекрёстков
            int n = ois.readInt();
            for (int i = 0; i < n; i++)
                new Junction(map, mapArea, ois.readInt(), ois.readDouble(), ois.readDouble(),
                        ois.readBoolean(), ois.readInt(), ois.readInt());

            //Чтение дорог
            n = ois.readInt();
            for (int i = 0; i < n; i++)
                new Road(map, mapArea,
                        map.getJunctionById(ois.readInt()),
                        map.getJunctionById(ois.readInt()),
                        (String) ois.readObject(), ois.readChar(), ois.readInt(),
                        new RoadSurface((String) ois.readObject(), ois.readDouble()));

            //Чтение счетчика
            map.setIdCounter(ois.readInt());
            ois.close();

        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        }
        return map;
    }
}
