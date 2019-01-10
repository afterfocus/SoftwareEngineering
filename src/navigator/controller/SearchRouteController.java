package navigator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import navigator.controller.properties.JunctionPropertiesController;
import navigator.controller.properties.RoadPropertiesController;
import navigator.database.DAO;
import navigator.model.Junction;
import navigator.model.LabelType;
import navigator.model.Map;
import navigator.model.Road;

public class SearchRouteController {

    @FXML
    private Pane mapArea;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label scaleLabel;
    @FXML
    private CheckBox streetNamesCheckBox;
    @FXML
    private Pane roadProperties;
    @FXML
    private Pane junctionProperties;

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

    /**
     * Инициализация формы
     */
    @FXML
    public void initialize() {

        //Инициализация карты
        map = new Map(LabelType.NAME, Color.BURLYWOOD);

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

        //Отображение названий улиц
        streetNamesCheckBox.selectedProperty().addListener(e -> map.setLabelsVisible(streetNamesCheckBox.isSelected()));
    }

    /**
     * Загрузить карту из файла
     */
    @FXML
    private void loadMap() {
        map.clear();
        mapArea.getChildren().clear();

        DAO.readMapFromFile(map, mapArea,"samara.map");

        //Нажатие на перекрёсток
        for (Junction j: map.getJunctionList()) j.setOnMousePressed(event -> showJunctionProperties(j));

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
