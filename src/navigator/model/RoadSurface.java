package navigator.model;

/**
 * Класс дорожного покрытия
 */
public class RoadSurface {

    private String name;

    private double decelerationCoefficient;

    /**
     * Инициализация дорожного покрытия
     * @param name название дорожного покрытия
     * @param decelerationCoefficient коэфиициент торможения
     */
    public RoadSurface(String name, double decelerationCoefficient) {
        this.name = name;
        this.decelerationCoefficient = decelerationCoefficient;
    }

    /**
     * @return название покрытия
     */
    public String getName() {
        return name;
    }

    /**
     * @return коэффициент торможения
     */
    public double getCoefficient() {
        return decelerationCoefficient;
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
