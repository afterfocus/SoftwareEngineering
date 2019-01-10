package navigator.database;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    public static Driver[] getDrivers() {
        return new Driver [] {
                new Driver(1, "Голов Максим"),
                new Driver(3, "Иванов Дмитрий"),
                new Driver(4,"Филатова Евгения"),
                new Driver(2, "Шепелев Фёдор")
        };
    }

    /**
     * Получить доступные водителю автомобили
     * @param driverId идентификатор водителя
     * @return массив названий улиц
     */
    public static Car[] getCars(int driverId) {
        switch (driverId) {
            case 1: {
                return new Car[] {
                        new Car(1, "Datsun On-Do", 165,2,9.4),
                        new Car(2, "Lada Granta I", 166,2,9.0),
                        new Car(3, "Lada Kalina I", 160,1,9.8),
                        new Car(4, "Lada Vesta", 182,1,9.3),
                        new Car(5, "Lada Vesta CNG", 170,5,6.3),
                };
            }
            case 2: {
                return new Car[] {
                        new Car(10, "Беларус 892", 34,4,15.5),
                        new Car(11, "МАЗ 103", 72,4,29),
                        new Car(12, "Лиаз 525 CNG", 90,5,38),
                };
            }
            case 3: {
                return new Car[] {
                        new Car(6, "Toyota Camry 3.5", 220,3,12.5),
                        new Car(7, "Toyota Land Cruiser 4.0", 180,2,14.7),
                        new Car(8, "Toyota Tundra II 5.7", 185,2,18.1),
                        new Car(9, "Volkswagen Tiguan 2.0 TDI", 190,4,7.6),
                };
            }
            default: return new Car[] { };
        }
    }

    public static Fuel[] getFuels() {
        return new Fuel[] {
                new Fuel(1, "АИ-92", 40.5),
                new Fuel(2, "АИ-95", 43.6),
                new Fuel(3, "АИ-98", 47.25),
                new Fuel(4, "ДТ",  44.65),
                new Fuel(5, "Метан", 23.5)
        };
    }

    /**
     * Получить дорожные покрытия из БД
     *
     * @return массив дорожных покрытий
     */
    public static RoadSurface[] getRoadSurfaces() {
        return new RoadSurface[]{
                new RoadSurface(1, "Шоссе", 1.0),
                new RoadSurface(2, "Гладкий асфальт", 0.8),
                new RoadSurface(3, "Асфальт", 0.7),
                new RoadSurface(4, "Внутриквартальная дорога", 0.5),
                new RoadSurface(5, "Разбитый асфальт", 0.4),
                new RoadSurface(6, "Проселочная дорога", 0.3),
                new RoadSurface(7, "Грунт", 0.2),
                new RoadSurface(8, "Перекрыто", 0.001)
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
     *
     * @param filename имя файла
     * @param mapArea  контейнер для отрисовки карты
     * @param color    цвет перекрёстков
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
                        new RoadSurface(-1, (String) ois.readObject(), ois.readDouble()));

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
