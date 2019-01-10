package navigator.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import navigator.controller.settings.JunctionSettingsController;
import navigator.controller.settings.RoadSettingsController;
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
    private CheckBox streetNamesCheckBox = new CheckBox();

    @FXML
    private Pane junctionSettings;
    @FXML
    private Pane roadSettings;

    @FXML
    private JunctionSettingsController junctionSettingsController = new JunctionSettingsController();
    @FXML
    private RoadSettingsController roadSettingsController = new RoadSettingsController();

    private Map map;
    private Junction pickedJunction;

    private double scale = 1;
    private double translationX = 0;
    private double translationY = 0;
    private boolean isJunctionDragging = false;
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
        pickedJunction = null;

        //Выбор шаблона
        toggleGroup.selectedToggleProperty().addListener(getChangeListener());

        //Изменение размеров окна
        mapArea.widthProperty().addListener(e -> map.setOffsetX(mapArea.getWidth() / 2));
        mapArea.heightProperty().addListener(e -> {
            map.setOffsetY(mapArea.getHeight() / 2);
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
            if (e.getDeltaY() < 0) zoomSlider.setValue(zoomSlider.getValue() + 0.05);
            else zoomSlider.setValue(zoomSlider.getValue() - 0.05);
            sliderDragged();
        });

        //Отображение названий улиц
        streetNamesCheckBox.selectedProperty().addListener(e -> map.setLabelsVisible(streetNamesCheckBox.isSelected()));
    }

    /**
     * Сохранить карту в файл
     */
    @FXML
    private void saveMap() {
        DAO.writeMapToFile(map, "samara.map");
    }

    /**
     * Загрузить карту из файла
     */
    @FXML
    private void loadMap() {
        map.clear();
        mapArea.getChildren().clear();
        translationX = 0;
        translationY = 0;
        scale = 1;
        zoomSlider.setValue(1);

        DAO.readMapFromFile(map, mapArea, "samara.map");

        for (Junction j : map.getJunctionList()) {
            j.setEffect(dropShadow);

            //Нажатие на перекрёсток
            j.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) isJunctionDragging = true;
                else showJunctionSettings(j);
            });

            //Перемещение перекрёстка
            j.setOnMouseDragged(e -> {
                j.setScreenXY(e.getX(), e.getY());
                for (Road r : j.getRoads()) {
                    r.notifyLengthChanged();
                    r.notifyLocationChanged();
                }
            });
            j.setOnMouseReleased(event -> isJunctionDragging = false);
        }

        for (Road r : map.getRoadList()) {
            r.getForwardLine().setEffect(dropShadow);
            r.getBackwardLine().setEffect(dropShadow);

            //Нажатие на дорогу
            r.getForwardLine().setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) showRoadSettings(r);
            });
            r.getBackwardLine().setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) showRoadSettings(r);
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
        scale -= 0.25;
        if (scale < 0.2) scale = 0.2;
        zoomSlider.setValue(scale);

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
                            if (e.getButton() == MouseButton.PRIMARY) {
                                Junction junction = map.addJunction(e.getX(), e.getY());

                                //Нажатие на перекрёсток
                                junction.setOnMousePressed(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) isJunctionDragging = true;
                                    else showJunctionSettings(junction);
                                });

                                //Перемещение перекрёстка
                                junction.setOnMouseDragged(event -> {
                                    junction.setScreenXY(event.getX(), event.getY());
                                    for (Road r : junction.getRoads()) {
                                        r.notifyLocationChanged();
                                        r.notifyLengthChanged();
                                    }
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

    //====================================== Функции, связанные с меню настроек =======================================

    /**
     * Отобразить окно настройки параметров перекрёстка
     *
     * @param junction настраиваемый перекрёсток
     */
    private void showJunctionSettings(Junction junction) {
        junctionSettings.setVisible(true);
        roadSettings.setVisible(false);
        junctionSettingsController.setJunction(junction);
    }

    /**
     * Отобразить окно настройки параметров дороги
     *
     * @param road настраиваемая дорога
     */
    private void showRoadSettings(Road road) {
        junctionSettings.setVisible(false);
        roadSettings.setVisible(true);
        roadSettingsController.setRoad(road);
    }

    /**
     * Закрыть окна настройки параметров
     */
    private void closeSettings() {
        junctionSettings.setVisible(false);
        roadSettings.setVisible(false);
    }
}
