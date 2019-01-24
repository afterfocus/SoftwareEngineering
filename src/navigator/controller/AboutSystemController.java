package navigator.controller;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.fxml.FXML;
import navigator.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutSystemController {

    @FXML
    public void initialize() {

    }

    @FXML
    private void aboutSystemButtonClicked() {
        try {
            File file = new File("manual/manual.html");
            Desktop.getDesktop().open(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
