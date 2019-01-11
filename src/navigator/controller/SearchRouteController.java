package navigator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import navigator.controller.properties.JunctionPropertiesController;
import navigator.controller.properties.RoadPropertiesController;
import navigator.database.Car;
import navigator.database.DAO;
import navigator.database.Driver;
import navigator.model.Junction;
import navigator.model.enums.LabelType;
import navigator.model.Map;
import navigator.model.Road;
import navigator.model.enums.SearchCriterion;

public class SearchRouteController {

    @FXML
    private Pane mapArea;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label scaleLabel;
    @FXML
    private ToggleGroup radioGroup;
    @FXML
    private Pane roadProperties;
    @FXML
    private Pane junctionProperties;
    @FXML
    private ComboBox<String> criterionComboBox;
    @FXML
    private ComboBox<Driver> driverComboBox;
    @FXML
    private ComboBox<Car> carComboBox;
    @FXML
    private Label speedLabel;
    @FXML
    private Label fuelTypeLabel;
    @FXML
    private Label fuelConsumptionLabel;

    @FXML
    private JunctionPropertiesController junctionPropertiesController = new JunctionPropertiesController();
    @FXML
    private RoadPropertiesController roadPropertiesController = new RoadPropertiesController();

    private Map map;
    private double scale = 1;
    private double translationX = 0;
    private double translationY = 0;
    private double startDraggingX;
    private double startDraggingY;
    private double deltaX;
    private double deltaY;

    private DropShadow dropShadow = new DropShadow();
    private Color junctionColor = Color.BURLYWOOD;

    /**
     * Инициализация формы
     */
    @FXML
    public void initialize() {

        //Инициализация карты
        map = new Map(LabelType.NAME, junctionColor);

        //Изменение размеров окна
        mapArea.widthProperty().addListener(e -> map.setOffsetX(mapArea.getWidth() / 2));
        mapArea.heightProperty().addListener(e -> map.setOffsetY( mapArea.getHeight() / 2));

        //Перемещение карты
        mapArea.setOnMousePressed(e -> {
            deltaX = 0;
            deltaY = 0;
            startDraggingX = e.getX();
            startDraggingY = e.getY();
            if (e.getButton() == MouseButton.PRIMARY) closeProperties();
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
            switch (((RadioButton)radioGroup.getSelectedToggle()).getText()) {
                case "Нет": map.setLabelsType(LabelType.NONE); break;
                case "Название улицы": map.setLabelsType(LabelType.NAME); break;
                case "Протяженность": map.setLabelsType(LabelType.LENGTH); break;
                case "Время в пути": map.setLabelsType(LabelType.TIME); break;
                case "Скорость движения": map.setLabelsType(LabelType.SPEED); break;
                case "Расход топлива": map.setLabelsType(LabelType.FUEL); break;
            }
        });

        criterionComboBox.setItems(FXCollections.observableArrayList("По времени", "По стоимости", "По расстоянию"));
        driverComboBox.setItems(FXCollections.observableArrayList(DAO.getDrivers()));
        carComboBox.setItems(FXCollections.observableArrayList(DAO.getCars(DAO.getDrivers()[0].getId())));
        criterionComboBox.valueProperty().addListener(e -> {
            switch (criterionComboBox.getValue()) {
                case "По времени": map.setCriterion(SearchCriterion.TIME); radioGroup.selectToggle(radioGroup.getToggles().get(3)); break;
                case "По стоимости": map.setCriterion(SearchCriterion.COST); radioGroup.selectToggle(radioGroup.getToggles().get(5)); break;
                case "По расстоянию": map.setCriterion(SearchCriterion.DISTANCE); radioGroup.selectToggle(radioGroup.getToggles().get(2));break;
            }
        });
        driverComboBox.valueProperty().addListener(e -> {
            Driver driver = driverComboBox.getValue();
            carComboBox.setItems(FXCollections.observableArrayList(DAO.getCars(driver.getId())));
            carComboBox.getSelectionModel().select(0);
            map.setCar(carComboBox.getValue());
        });
        carComboBox.valueProperty().addListener(e -> {
            Car car = carComboBox.getValue();
            if (car != null) {
                speedLabel.setText(car.getMaxSpeed() + " км/ч");
                fuelTypeLabel.setText(car.getFuel() + "");
                fuelConsumptionLabel.setText(car.getFuelConsumption() + " л / 100 км");
                map.setCar(car);
            }
        });
        criterionComboBox.getSelectionModel().select(0);
        driverComboBox.getSelectionModel().select(0);
        carComboBox.getSelectionModel().select(0);
    }

    /**
     * Загрузить карту из файла
     */
    @FXML
    private void loadMap() {
        mapArea.getChildren().clear();
        translationX = 0;
        translationY = 0;
        scale = 1;
        zoomSlider.setValue(1);
        radioGroup.selectToggle(radioGroup.getToggles().get(1));

        map = DAO.readMapFromFile("samara.map", mapArea, junctionColor);
        map.setCar(DAO.getCars(DAO.getDrivers()[0].getId())[0]);

        //Нажатие на перекрёсток
        for (Junction j: map.getJunctionList()) {
            j.setOnMousePressed(event -> {
                showJunctionProperties(j);

            });
        }

        for (Road r: map.getRoadList()) {
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


    //================================== Функции, связанные с элементами управления ===================================

    @FXML
    private void search() {

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

    /**
     * Отобразить окно свойств перекрёстка
     *
     * @param junction настраиваемый перекрёсток
     */
    private void showJunctionProperties(Junction junction) {
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
        roadProperties.setVisible(true);
        junctionProperties.setVisible(false);
        roadPropertiesController.setRoad(road);
    }

    /**
     * Закрыть окна свойств
     */
    private void closeProperties() {
        junctionProperties.setVisible(false);
        roadProperties.setVisible(false);
    }
}
