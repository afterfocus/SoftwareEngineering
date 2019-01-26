package navigator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import navigator.controller.properties.JunctionPropertiesController;
import navigator.controller.properties.RoadPropertiesController;
import navigator.database.Car;
import navigator.database.ConnectionDB;
import navigator.database.DAO;
import navigator.database.Driver;
import navigator.model.Junction;
import navigator.model.enums.LabelType;
import navigator.model.Map;
import navigator.model.Road;
import navigator.model.enums.SearchCriterion;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SearchRouteController {

    @FXML
    private Pane mapArea;
    @FXML
    private Slider zoomSlider;
    @FXML
    private ToggleGroup radioGroup;
    @FXML
    private Pane roadProperties;
    @FXML
    private Pane junctionProperties;
    @FXML
    private Pane routePane;
    @FXML
    private Pane routePointSelector;
    @FXML
    private ComboBox<String> criterionComboBox;
    @FXML
    private ComboBox<Driver> driverComboBox;
    @FXML
    private ComboBox<Car> carComboBox;
    @FXML
    private Label scaleLabel;
    @FXML
    private Label departureLabel;
    @FXML
    private Label arrivalLabel;
    @FXML
    private Label speedLabel;
    @FXML
    private Label fuelTypeLabel;
    @FXML
    private Label fuelConsumptionLabel;
    @FXML
    private Label criterionLabel;
    @FXML
    private Button departureButton;
    @FXML
    private Button arrivalButton;

    @FXML
    private JunctionPropertiesController junctionPropertiesController = new JunctionPropertiesController();
    @FXML
    private RoadPropertiesController roadPropertiesController = new RoadPropertiesController();

    private final FileChooser fileChooser = new FileChooser();

    private Map map;
    private double scale = 1;
    private double translationX = 0;
    private double translationY = 0;
    private double startDraggingX;
    private double startDraggingY;
    private double deltaX;
    private double deltaY;

    private boolean isDeparturePicked;
    private boolean isArrivalPicked;

    private DropShadow dropShadow = new DropShadow();
    private Color junctionColor = Color.web("#ddc18c");

    /**
     * Инициализация формы
     */
    @FXML
    public void initialize() {

        //Инициализация карты
        map = new Map(LabelType.NAME, junctionColor);

        //Изменение размеров окна
        mapArea.widthProperty().addListener(e -> map.setOffsetX(mapArea.getWidth() / 2));
        mapArea.heightProperty().addListener(e -> map.setOffsetY(mapArea.getHeight() / 2));

        //Перемещение карты
        mapArea.setOnMousePressed(e -> {
            deltaX = 0;
            deltaY = 0;
            startDraggingX = e.getX();
            startDraggingY = e.getY();
            if (e.getTarget() == mapArea) closeProperties();
        });
        mapArea.setOnMouseDragged(e -> {
            deltaX = e.getX() - startDraggingX;
            deltaY = e.getY() - startDraggingY;
            map.setTranslation(translationX + (deltaX / scale), translationY + (deltaY / scale));
        });
        mapArea.setOnMouseReleased(e -> {
            translationX += (deltaX / scale);
            translationY += (deltaY / scale);
        });

        //Масштабирование скроллингом
        mapArea.setOnScroll(e -> {
            if (e.getDeltaY() < 0) zoomSlider.setValue(zoomSlider.getValue() + 0.04);
            else zoomSlider.setValue(zoomSlider.getValue() - 0.04);
            sliderDragged();
        });

        //Отображение надписей
        radioGroup.selectedToggleProperty().addListener(e -> {
            switch (((RadioButton) radioGroup.getSelectedToggle()).getText()) {
                case "Нет":
                    map.setLabelsType(LabelType.NONE);
                    break;
                case "Название улицы":
                    map.setLabelsType(LabelType.NAME);
                    break;
                case "Протяженность":
                    map.setLabelsType(LabelType.LENGTH);
                    break;
                case "Время в пути":
                    map.setLabelsType(LabelType.TIME);
                    break;
                case "Скорость движения":
                    map.setLabelsType(LabelType.SPEED);
                    break;
                case "Расход топлива":
                    map.setLabelsType(LabelType.FUEL);
                    break;
            }
        });

        //Заполнение списков
        criterionComboBox.setItems(FXCollections.observableArrayList("По времени", "По стоимости", "По расстоянию"));
        driverComboBox.setItems(FXCollections.observableArrayList(ConnectionDB.selectAllFromDriver()));
        // FIXME: 25/01/2019 Автомобили по водителю
        carComboBox.setItems(FXCollections.observableArrayList(ConnectionDB.selectAllFromCar()));

        //Выбор критерия
        criterionComboBox.valueProperty().addListener(e -> {
            switch (criterionComboBox.getValue()) {
                case "По времени":
                    map.setCriterion(SearchCriterion.TIME);
                    radioGroup.selectToggle(radioGroup.getToggles().get(3));
                    break;
                case "По стоимости":
                    map.setCriterion(SearchCriterion.COST);
                    radioGroup.selectToggle(radioGroup.getToggles().get(5));
                    break;
                case "По расстоянию":
                    map.setCriterion(SearchCriterion.DISTANCE);
                    radioGroup.selectToggle(radioGroup.getToggles().get(2));
                    break;
            }
        });

        //Выбор водителя
        driverComboBox.valueProperty().addListener(e -> {
            Driver driver = driverComboBox.getValue();
            // FIXME: 25/01/2019 Автомобили по водителю
            carComboBox.setItems(FXCollections.observableArrayList(ConnectionDB.selectAllFromCar()));
            carComboBox.getSelectionModel().select(0);
            map.setCar(carComboBox.getValue());
        });

        //Выбор автомобиля
        carComboBox.valueProperty().addListener(e -> {
            Car car = carComboBox.getValue();
            if (car != null) {
                speedLabel.setText(car.getMaxSpeed() + " км/ч");
                fuelTypeLabel.setText(car.getFuel() + "");
                fuelConsumptionLabel.setText(car.getFuelConsumption() + " л / 100 км");
                map.setCar(car);
            }
        });

        //Начальные значения
        criterionComboBox.getSelectionModel().select(0);
        driverComboBox.getSelectionModel().select(0);
        carComboBox.getSelectionModel().select(0);

        fileChooser.setTitle("Выберите файл карты");
        fileChooser.setInitialDirectory(new File("Карты"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Map Files", "*.map"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    /**
     * Загрузить карту из файла
     */
    @FXML
    private void loadMap() {

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            mapArea.getChildren().clear();
            translationX = 0;
            translationY = 0;
            scale = 1;
            zoomSlider.setValue(1);
            departureLabel.setText("Не выбрана");
            arrivalLabel.setText("Не выбрана");
            isDeparturePicked = false;
            isArrivalPicked = false;
            routePane.setVisible(false);
            criterionComboBox.getSelectionModel().select(0);

            map = DAO.readMapFromFile(file, mapArea, junctionColor);
            //map.setCar(ConnectionDB.selectAllFromCar().get(0));
            radioGroup.selectToggle(radioGroup.getToggles().get(1));

            //Нажатие на перекрёсток
            for (Junction j : map.getJunctionList()) {
                j.setOpacity(0.85);
                j.setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) showRoutePointSelector(j);
                    else showJunctionProperties(j);
                });
            }

            for (Road r : map.getRoadList()) {
                r.getForwardLine().setEffect(dropShadow);
                r.getBackwardLine().setEffect(dropShadow);

                //Нажатие на дорогу
                r.getForwardLine().setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) showRoadProperties(r);
                });
                r.getBackwardLine().setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) showRoadProperties(r);
                });
            }
        }
    }


    //================================== Функции, связанные с элементами управления ===================================

    @FXML
    private void search() {
        if(isDeparturePicked && isArrivalPicked) {
            int[] optimalRoute = map.getOptimalRoute();

            if (optimalRoute != null) {
                List<Line> optimalRouteLines = new LinkedList<>();

                for (int i = 0; i < optimalRoute.length - 1; i++)
                    optimalRouteLines.add(map.getLineByJunctionId(optimalRoute[i], optimalRoute[i + 1]));

                for (Road r : map.getRoadList()) {
                    r.getForwardLine().setOpacity(0.3);
                    r.getBackwardLine().setOpacity(0.3);
                    r.getForwardLine().setEffect(dropShadow);
                    r.getBackwardLine().setEffect(dropShadow);
                }

                for (Line l : optimalRouteLines) {
                    l.setOpacity(1);
                    l.setEffect(new DropShadow(5, Color.rgb(255, 255, 255, 0.5)));
                }

                switch (map.getSearchCriterion()) {
                    case TIME:
                        criterionLabel.setText(((double) Math.round(map.getCriterionValue() / 60 * 10)) / 10 + " мин");
                        break;
                    case COST:
                        criterionLabel.setText(((double) Math.round(map.getCriterionValue() * 100)) / 100 + " руб");
                        break;
                    case DISTANCE:
                        criterionLabel.setText(map.getCriterionValue() + " м");
                        break;
                }
                routePane.setVisible(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Маршрут между выбранными точками не существует.");
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не удалось построить маршрут");
                alert.show();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите точку отправления и точку прибытия на карте.");
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не выбраны точки маршрута");
            alert.show();
        }
    }

    @FXML
    private void clearRouteButtonClicked() {
        routePane.setVisible(false);
        for (Road r : map.getRoadList()) {
            r.getForwardLine().setOpacity(1);
            r.getBackwardLine().setOpacity(1);
            r.getForwardLine().setEffect(dropShadow);
            r.getBackwardLine().setEffect(dropShadow);
        }
    }

    /**
     * Перемещение слайдера масштаба
     */
    @FXML
    private void sliderDragged() {
        scale = zoomSlider.getValue();
        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Нажатие на слайдер масштаба
     */
    @FXML
    private void sliderClicked() {
        scale = zoomSlider.getValue();
        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Нажатие на кнопку приближения
     */
    @FXML
    private void zoomInButtonClicked() {
        scale += 0.25;
        if (scale > 1.5) scale = 1.5;

        map.setScale(scale);
        zoomSlider.setValue(scale);
        updateScaleLabel();
    }

    /**
     * Нажатие на кнопку отдаления
     */
    @FXML
    private void zoomOutButtonClicked() {
        scale -= 0.25;
        if (scale < 0.2) scale = 0.2;

        map.setScale(scale);
        zoomSlider.setValue(scale);
        updateScaleLabel();
    }

    /**
     * Обновить линейку масштаба
     */
    private void updateScaleLabel() {
        double interval = Math.round(20 / scale) * 10;
        if (interval < 1000) scaleLabel.setText((int) interval + " м");
        else {
            interval = (double) Math.round(interval / 100) / 10;
            scaleLabel.setText(interval + " км");
        }
    }

    //===================================== Функции, связанные с окнами свойств =======================================


    private void showRoutePointSelector(Junction junction) {
        junctionProperties.setVisible(false);
        roadProperties.setVisible(false);
        routePointSelector.setVisible(true);
        routePointSelector.setLayoutX(junction.getCenterX());
        routePointSelector.setLayoutY(junction.getCenterY());
        departureButton.setOnMouseClicked(e -> {
            if (map.setDepartureJunction(junction)) {
                isArrivalPicked = false;
                arrivalLabel.setText("Не выбрана");
            }
            isDeparturePicked = true;
            departureLabel.setText("Выбрана");
            routePointSelector.setVisible(false);
        });
        arrivalButton.setOnMouseClicked(e -> {
            if (map.setArrivalJunction(junction)) {
                isDeparturePicked = false;
                departureLabel.setText("Не выбрана");
            }
            isArrivalPicked = true;
            arrivalLabel.setText("Выбрана");
            routePointSelector.setVisible(false);
        });
    }

    /**
     * Отобразить окно свойств перекрёстка
     *
     * @param junction настраиваемый перекрёсток
     */
    private void showJunctionProperties(Junction junction) {
        routePointSelector.setVisible(false);
        roadProperties.setVisible(false);
        junctionProperties.setVisible(true);
        junctionPropertiesController.setJunction(junction);
    }

    /**
     * Отобразить окно свойств дороги
     *
     * @param road настраиваемая дорога
     */
    private void showRoadProperties(Road road) {
        routePointSelector.setVisible(false);
        roadProperties.setVisible(true);
        junctionProperties.setVisible(false);
        roadPropertiesController.setRoad(road);
    }

    /**
     * Закрыть окна свойств
     */
    private void closeProperties() {
        routePointSelector.setVisible(false);
        junctionProperties.setVisible(false);
        roadProperties.setVisible(false);
    }
}
