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


    private static FuelType[] getFuels() {
        return new FuelType[] {
                new FuelType(1, "АИ-92", 40.5),
                new FuelType(2, "АИ-95", 43.6),
                new FuelType(3, "АИ-98", 47.25),
                new FuelType(4, "ДТ",  44.65),
                new FuelType(5, "Метан", 23.5)
        };
    }

    public static Driver[] getDrivers() {
        return new Driver [] {
                new Driver(1, "Голов М.Е."),
                new Driver(2, "Иванов Д.А."),
                new Driver(3,"Филатова Е.А."),
                new Driver(4, "Шепелев Ф.О.")
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
                        new Car(1, "Datsun On-Do", 165, getFuels()[1],9.4),
                        new Car(2, "Lada Granta I", 166, getFuels()[1],9.0),
                        new Car(3, "Lada Kalina I", 160, getFuels()[0],9.8),
                        new Car(4, "Lada Vesta", 182, getFuels()[0],9.1),
                        new Car(5, "Lada Vesta CNG", 170, getFuels()[4],8.3),
                };
            }
            case 2: {
                return new Car[] {
                        new Car(10, "Беларус 892", 34, getFuels()[3],15.5),
                        new Car(11, "МАЗ 103", 72, getFuels()[3],29),
                        new Car(12, "Лиаз 525 CNG", 90, getFuels()[4],38),
                };
            }
            case 3: {
                return new Car[] {
                        new Car(6, "Toyota Camry 3.5", 220, getFuels()[2],12.5),
                        new Car(7, "Toyota Land Cruiser 4.0", 180, getFuels()[1],14.7),
                        new Car(8, "Toyota Tundra II 5.7", 185, getFuels()[1],18.1),
                        new Car(9, "Volkswagen Tiguan 2.0 TDI", 190, getFuels()[3],7.6),
                };
            }
            default: return new Car[] { };
        }
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
