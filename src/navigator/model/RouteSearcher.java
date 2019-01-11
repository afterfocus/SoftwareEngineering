package navigator.model;

import navigator.database.Car;
import navigator.model.enums.SearchCriterion;

class RouteSearcher {

    private Map map;
    private SearchCriterion criterion;
    private Car car;
    private Junction departureJunction;
    private Junction arrivalJunction;
    private double criterionValue;

    RouteSearcher(Map map) {
        this.map = map;
    }

    SearchCriterion getCriterion() {
        return criterion;
    }

    void setCriterion(SearchCriterion criterion) {
        this.criterion = criterion;
    }

    Car getCar() {
        return car;
    }

    void setCar(Car car) {
        this.car = car;
    }

    public Junction getDepartureJunction() {
        return departureJunction;
    }

    public void setDepartureJunction(Junction departureJunction) {
        this.departureJunction = departureJunction;
    }

    public Junction getArrivalJunction() {
        return arrivalJunction;
    }

    public void setArrivalJunction(Junction arrivalJunction) {
        this.arrivalJunction = arrivalJunction;
    }

    public double getCriterionValue() {
        return criterionValue;
    }
}
