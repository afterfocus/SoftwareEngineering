package navigator;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("view/primary_stage.fxml"));
        primaryStage.setTitle("Поиск маршрута");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(675);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        hostServices = getHostServices();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static HostServices getHostService() {
        return hostServices;
    }
}
