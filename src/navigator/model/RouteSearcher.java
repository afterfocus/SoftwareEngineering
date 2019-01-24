package navigator.model;

import navigator.database.Car;
import navigator.model.enums.SearchCriterion;

import java.util.Stack;

import static java.lang.Math.min;
import static java.util.Arrays.fill;

class RouteSearcher {

    private Map map;
    private SearchCriterion criterion;
    private Car car;
    private Junction departureJunction;
    private Junction arrivalJunction;
    private double criterionValue;

    RouteSearcher(Map map) {
        this.map = map;
        this.criterion = SearchCriterion.TIME;
    }

    SearchCriterion getSearchCriterion() {
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

    Junction getDepartureJunction() {
        return departureJunction;
    }

    void setDepartureJunction(Junction departureJunction) {
        this.departureJunction = departureJunction;
    }

    Junction getArrivalJunction() {
        return arrivalJunction;
    }

    void setArrivalJunction(Junction arrivalJunction) {
        this.arrivalJunction = arrivalJunction;
    }

    double getCriterionValue() {
        return criterionValue;
    }

    /**
     * Генерация матрицы смежности
     *
     * @return матрица смежности
     */
    private double[][] getAdjacencyMatrix() {
        //размерность матрицы равна максимальному ID в массиве перекрёстков
        n = map.getJunctionList().get(map.getJunctionList().size() - 1).getID() + 1;
        adjacencyMatrix = new double[n][n];

        //заполняем бесконечными значениями
        for (int i = 0; i < n; i++) fill(adjacencyMatrix[i], INF);

        //заполняем матрицу
        for (int i = 0; i < n; i++) {
            Junction junction = map.getJunctionById(i);
            if (junction != null) {
                for (Road road : junction.getRoads()) {
                    double criterion = road.getCriterionValue(i);
                    if (road.getStart().getID() == i)
                        adjacencyMatrix[i][road.getEnd().getID()] = criterion == -1 ? INF : criterion;
                    else adjacencyMatrix[i][road.getStart().getID()] = criterion == -1 ? INF : criterion;
                }
            }
        }
        return adjacencyMatrix;
    }

    private double INF = Double.MAX_VALUE / 2; // "Бесконечность"
    private int n; // количество вершин
    private double[][] adjacencyMatrix; // матрица смежности

    /**
     * Алгоритм Дейкстры
     * @param start идентификатор перкрёстка отправления
     * @param end   идентификатор перекрёстка назначения
     * @return  маршрут в виде массива идентификаторов перекрёстков
     */
    int[] getRoute(int start, int end) {

        //размерность матрицы равна максимальному ID в массиве перекрёстков
        n = map.getJunctionList().get(map.getJunctionList().size() - 1).getID() + 1;
        adjacencyMatrix = getAdjacencyMatrix();

        boolean[] used = new boolean[n]; // массив пометок
        int[] ancestor = new int[n];
        double[] distance = new double[n]; // массив расстояния. dist[v] = минимальное_расстояние(start, v)

        fill(distance, INF);
        distance[start] = 0; // для начальной вершины положим 0

        for (; ; ) {
            int v1 = -1;

            for (int v2 = 0; v2 < n; v2++) // перебираем вершины
                if (!used[v2] && distance[v2] < INF && (v1 == -1 || distance[v1] > distance[v2])) // выбираем самую близкую непомеченную вершину
                    v1 = v2;

            if (v1 == -1) break; // ближайшая вершина не найдена
            used[v1] = true; // помечаем ее

            for (int v2 = 0; v2 < n; v2++) {
                if (!used[v2] && adjacencyMatrix[v1][v2] < INF) {// для всех непомеченных смежных
                    if (distance[v1] + adjacencyMatrix[v1][v2] < distance[v2]) {
                        distance[v2] = distance[v1] + adjacencyMatrix[v1][v2]; // улучшаем оценку расстояния (релаксация)
                        ancestor[v2] = v1;
                    }
                }
            }
        }
        criterionValue = distance[end];

        if (criterionValue < INF) {
            /* Восстановление пути */
            Stack<Integer> stack = new Stack<>();
            stack.clear();
            for (int v = end; v != start; v = ancestor[v]) {
                stack.push(v);
            }
            stack.push(start);
            int[] optimalRoute = new int[stack.size()];
            for (int i = 0; i < optimalRoute.length; i++) optimalRoute[i] = stack.pop();

            return optimalRoute;
        }
        else return null;
    }
}
