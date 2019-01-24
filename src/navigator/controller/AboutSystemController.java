package navigator.controller;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.File;
import java.io.IOException;

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
