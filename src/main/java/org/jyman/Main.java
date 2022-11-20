package org.jyman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox();

        primaryStage.setScene(new Scene(hBox, 500, 500));
        primaryStage.setTitle("하야세 유우카 카와이 ><");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Toy toy = new Toy();
        JSerialCommTransport jSerial = new JSerialCommTransport("COM3", toy);
        IODevice device = new FirmataDevice(jSerial);
        device.start();
        device.ensureInitializationIsDone();
        Pin echoPin = device.getPin(6);
        Pin trigPin = device.getPin(7);
        Pin servoPin = device.getPin(12);
        echoPin.setMode(Pin.Mode.INPUT);
        trigPin.setMode(Pin.Mode.OUTPUT);
        servoPin.setMode(Pin.Mode.SERVO);

        double coefficient = 0.00014634146;
        int[] azimuthArray = new int[] {20, 40, 60, 80, 100};
        int azimuthIndex = 0;
        int centre = 90;
        long servoInterval = 300;
        servoPin.setValue(centre);

        toy.setAngle(centre);
        int index = 4;
        Runnable measure = () -> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(-16); // START_SYSEX
            stream.write(0x74); // PULSE IN
            stream.write(6);
            stream.write(7);
            stream.write(-9);
            try {
                jSerial.write(stream.toByteArray());
                Thread.sleep(100);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        };

        TimerTask servoTask = new TimerTask() {
            @Override
            public void run() {
                if (toy.getStuckCount() >= 3) {
                    try {
                        System.out.println("시리얼이 응답하지 않습니다. 연결을 초기화합니다.");
                        device.stop();
                        Thread.sleep(5000);
                        toy.resetStuckCount();
                        device.start();
                        device.ensureInitializationIsDone();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                measure.run();
                double distance = toy.getMessage() * coefficient;
                if (distance > 1300) {
                    try {
                        System.out.println("올바르지 않은 측정값, 0.5초 대기합니다.");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    measure.run();
                    distance = toy.getMessage() * coefficient;
                }
                System.out.println("각도: " + toy.getAngle() + ", 거리: " + distance + "cm");

                if (!toy.isReversed()) {
                    if (toy.getAngle() <= centre - azimuthArray[azimuthIndex] / 2) { // Limit
                        toy.setReversed(true);
                        toy.setAngle(toy.getAngle() + index);
                    }
                    else {
                        toy.setAngle(toy.getAngle() - index);
                    }
                } else {
                    if (toy.getAngle() >= centre + azimuthArray[azimuthIndex] / 2) { // Limit
                        toy.setReversed(false);
                        toy.setAngle(toy.getAngle() - index);
                    }
                    else {
                        toy.setAngle(toy.getAngle() + index);
                    }
                }
                try {
                    servoPin.setValue(toy.getAngle());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Timer servoTimer = new Timer("Servo");
        servoTimer.schedule(servoTask, 0, servoInterval);

        launch(args);
    }
}