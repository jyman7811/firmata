package org.jyman.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.jyman.Toy;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoController implements Initializable {

    @FXML Label distance;
    @FXML Label angle;
    private Toy toy;
    private Task<Void> disTask;
    private Task<Void> angleTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            disTask = new Task<>() {
                @Override
                protected Void call() {
                    while (true) {
                        updateMessage(String.valueOf(toy.getMessage() * 0.00014634146).subSequence(0, 3) + "cm");
                    }
                }
            };
            angleTask = new Task<>() {
                @Override
                protected Void call() {
                    while (true) {
                        updateMessage(toy.getAngle() + "°");
                    }
                }
            };

            distance.textProperty().bind(disTask.messageProperty());
            angle.textProperty().bind(angleTask.messageProperty());

            Thread thread1 = new Thread(angleTask);
            Thread thread2 = new Thread(disTask);
            thread1.setDaemon(true);
            thread2.setDaemon(true);
            thread1.start();
            thread2.start();
        });
    }

    public void setToy(Toy toy) {
        this.toy = toy;
    }
}
