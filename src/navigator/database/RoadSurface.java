package navigator.database;

/**
 * Класс дорожного покрытия
 */
public class RoadSurface {

    private int id;
    private String name;
    private double decelerationCoefficient;

    /**
     * Инициализация дорожного покрытия
     * @param name название дорожного покрытия
     * @param decelerationCoefficient коэфиициент торможения
     */
    public RoadSurface(int id, String name, double decelerationCoefficient) {
        this.id = id;
        this.name = name;
        this.decelerationCoefficient = decelerationCoefficient;
    }

    public int getId() {
        return id;
    }

    /**
     * @return название покрытия
     */
    @SuppressWarnings("WeakerAccess")
    public String getName() {
        return name;
    }

    /**
     * @return коэффициент торможения
     */
    public double getCoefficient() {
        return decelerationCoefficient;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDecelerationCoefficient(double decelerationCoefficient) {
        this.decelerationCoefficient = decelerationCoefficient;
    }

    /**
     * @return <название (коэфф.торможения)>
     */
    @Override
    public String toString() {
        return name + " (" + decelerationCoefficient + ")";
    }

    /**
     * @param obj объект для сравнения
     * @return true, если идентичны имена
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoadSurface) {
            RoadSurface r = (RoadSurface) obj;
            return r.name.equals(name);
        }
        else return false;
    }
}
