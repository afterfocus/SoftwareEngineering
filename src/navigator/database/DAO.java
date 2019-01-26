package navigator.database;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import navigator.model.Junction;
import navigator.model.enums.LabelType;
import navigator.model.Map;
import navigator.model.Road;

import java.io.*;

/**
 * Класс работы с БД и файлами
 */
public class DAO {

    /**
     * Сохранить карту в файл
     *
     * @param map      карта для сохранения
     * @param file     имя файла
     */
    public static void writeMapToFile(Map map, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
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
     *
     * @param file     файл карты
     * @param mapArea  контейнер для отрисовки карты
     * @param color    цвет перекрёстков
     */
    public static Map readMapFromFile(File file, Pane mapArea, Color color) {
        Map map = new Map(LabelType.NAME, color);
        map.setOffsetX(mapArea.getWidth() / 2);
        map.setOffsetY(mapArea.getHeight() / 2);

        try (FileInputStream fis = new FileInputStream(file)) {
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
                        new RoadSurface(-1, (String) ois.readObject(), ois.readDouble()));

            //Чтение счетчика
            map.setIdCounter(ois.readInt());
            ois.close();

        }
        catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл с именем " + file.getName() + " не найден.");
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось открыть файл");
            alert.show();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл повреждён.");
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось открыть файл");
            alert.show();
            map = new Map(LabelType.NAME, color);
            map.setOffsetX(mapArea.getWidth() / 2);
            map.setOffsetY(mapArea.getHeight() / 2);
            mapArea.getChildren().clear();
        }
        return map;
    }
}
