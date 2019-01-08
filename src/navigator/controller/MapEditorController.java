package navigator.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import navigator.database.DAO;
import navigator.model.*;

/**
 * Контроллер для редактора карт
 */
public class MapEditorController {

    @FXML
    private Pane mapArea = new Pane();
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label scaleLabel;
    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    private Button saveButton = new Button();
    @FXML
    private Button loadButton = new Button();

    @FXML
    private Pane junctionSettings;
    @FXML
    private Pane roadSettings;

    @FXML
    private JunctionSettingsController junctionSettingsController = new JunctionSettingsController();
    @FXML
    private RoadSettingsController roadSettingsController = new RoadSettingsController();

    private DropShadow dropShadow;
    private Map map;
    private Junction pickedJunction;

    private double offsetX;
    private double offsetY;
    private double scale = 1;
    private double translationX = 0;
    private double translationY = 0;
    private boolean isJunctionDragging = false;
    private double startDraggingX;
    private double startDraggingY;
    private double deltaX;
    private double deltaY;

    /**
     * Инициализация формы
     */
    @FXML
    public void initialize() {

        map = new Map(offsetX, offsetY, scale, translationX, translationY, Color.BURLYWOOD);

        dropShadow = new DropShadow();
        pickedJunction = null;

        toggleGroup.selectedToggleProperty().addListener(getChangeListener());

        //Изменение размеров окна
        mapArea.widthProperty().addListener(e -> {
            offsetX = mapArea.getWidth() / 2;
            map.setOffsetX(offsetX);
        });
        mapArea.heightProperty().addListener(e -> {
            offsetY = mapArea.getHeight() / 2;
            map.setOffsetY(offsetY);
            loadButton.setLayoutY(mapArea.getHeight() - 41);
            saveButton.setLayoutY(mapArea.getHeight() - 84);
        });

        //Перемещение карты
        mapArea.setOnMousePressed(e -> {
            deltaX = 0;
            deltaY = 0;
            startDraggingX = e.getX();
            startDraggingY = e.getY();
            if (e.getButton() == MouseButton.PRIMARY) closeSettings();
        });
        mapArea.setOnMouseDragged(e -> {
            if (!isJunctionDragging) {
                deltaX = e.getX() - startDraggingX;
                deltaY = e.getY() - startDraggingY;
                map.setTranslation(translationX + (deltaX / scale), translationY + (deltaY / scale));
            }
        });
        mapArea.setOnMouseReleased(e -> {
            translationX += (deltaX / scale);
            translationY += (deltaY / scale);
        });

        //Масштабирование скроллингом
        mapArea.setOnScroll(e -> {
            if (e.getDeltaY() < 0) zoomSlider.setValue(zoomSlider.getValue() + 0.1);
            else zoomSlider.setValue(zoomSlider.getValue() - 0.1);
            sliderDragged();
        });

        //Кнопки сохранить и загрузить
        saveButton.setOnMouseClicked(e -> DAO.writeMapToFile(map, "samara.map"));
        loadButton.setOnMouseClicked(e -> DAO.readMapFromFile(this,"samara.map"));
    }

    /**
     * Нажатие на кнопку приближения
     */
    @FXML
    private void zoomButtonClicked() {
        scale += 0.5;
        if (scale > 2) scale = 2;
        zoomSlider.setValue(scale);

        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Нажатие на кнопку отдаления
     */
    @FXML
    private void zoomOutButtonClicked() {
        scale -= 0.5;
        if (scale < 0.20) scale = 0.20;
        zoomSlider.setValue(scale);

        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Перемещение слайдера масштаба
     */
    @FXML
    void sliderDragged() {
        scale = zoomSlider.getValue();
        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Нажатие на слайдер масштаба
     */
    @FXML
    void sliderClicked() {
        scale = zoomSlider.getValue();
        map.setScale(scale);
        updateScaleLabel();
    }

    /**
     * Выбор шаблона
     */
    private ChangeListener<Toggle> getChangeListener() {
        return (observable, oldValue, newValue) -> {
            clearListeners();

            if (newValue != null) {
                switch (newValue.getUserData().toString()) {

                    //Добавить перекрёсток
                    case "addJunction": {
                        mapArea.setOnMouseClicked(e -> {
                            if(e.getButton() == MouseButton.PRIMARY) {
                                Junction junction = map.addJunction(e.getX(), e.getY());

                                //Нажатие на перекрёсток
                                junction.setOnMousePressed(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) isJunctionDragging = true;
                                    else showJunctionSettings(junction);
                                });

                                //Перемещение перекрэстка
                                junction.setOnMouseDragged(event -> {
                                    junction.setScreenXY(event.getX(), event.getY());
                                    for (Road r : junction.getRoads()) r.updateLocation();
                                });
                                junction.setOnMouseReleased(event -> isJunctionDragging = false);

                                mapArea.getChildren().add(junction);
                            }
                        });
                        break;
                    }

                    //Добавить дорогу
                    case "addRoad": {
                        for (Junction j : map.getJunctionList()) {
                            j.setOnMouseClicked(e -> {
                                if (j.pick()) {
                                    if (pickedJunction == null) pickedJunction = j;
                                    else {
                                        Road road = map.addRoad(pickedJunction, j);
                                        if (road != null) {
                                            Line forward = road.getForwardLine();
                                            Line backward = road.getBackwardLine();
                                            mapArea.getChildren().add(0, forward);
                                            mapArea.getChildren().add(0, backward);
                                            forward.setEffect(dropShadow);
                                            backward.setEffect(dropShadow);

                                            //Нажатие на дорогу
                                            forward.setOnMousePressed(event -> {
                                                if (event.getButton() == MouseButton.SECONDARY) showRoadSettings(road);
                                            });
                                            backward.setOnMousePressed(event -> {
                                                if (event.getButton() == MouseButton.SECONDARY) showRoadSettings(road);
                                            });
                                        }
                                        pickedJunction = null;
                                    }
                                } else pickedJunction = null;
                            });
                        }
                        break;
                    }
                }
            }
        };
    }

    /**
     * Отобразить окно настройки параметров перекрёстка
     *
     * @param junction настраиваемый перекрёсток
     */
    public void showJunctionSettings(Junction junction) {
        junctionSettings.setVisible(true);
        roadSettings.setVisible(false);
        junctionSettings.setLayoutX(junction.getCenterX());
        junctionSettings.setLayoutY(junction.getCenterY());
        junctionSettingsController.setJunction(junction);
    }

    /**
     * Отобразить окно настройки параметров дороги
     *
     * @param road настраиваемая дорога
     */
    public void showRoadSettings(Road road) {
        junctionSettings.setVisible(false);
        roadSettings.setVisible(true);
        roadSettings.setLayoutX((road.getForwardLine().getStartX() + road.getForwardLine().getEndX()) / 2);
        roadSettings.setLayoutY((road.getForwardLine().getStartY() + road.getForwardLine().getEndY()) / 2);
        roadSettingsController.setRoad(road);
    }

    /**
     * Закрыть окна настройки параметров
     */
    private void closeSettings() {
        junctionSettings.setVisible(false);
        roadSettings.setVisible(false);
    }

    /**
     * Сбросить выбор шаблона
     */
    private void clearListeners() {
        //Убираем слушателя на добавление перекрёстков
        mapArea.setOnMouseClicked(null);

        //Убираем слушателей на добавление дорог
        for (Junction j : map.getJunctionList()) j.setOnMouseClicked(null);

        //Сбрасываем выбор вершин
        if (pickedJunction != null) {
            pickedJunction.pick();
            pickedJunction = null;
        }
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

    public Map getMap() {
        return map;
    }

    public Pane getMapArea() {
        return mapArea;
    }

    public void setJunctionDragging(boolean junctionDragging) {
        isJunctionDragging = junctionDragging;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }
}
