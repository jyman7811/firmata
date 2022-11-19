package org.jyman;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.jyman.gui.RadarFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
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

//                    measure.run();
//                    double distance = toy.getMessage() * coefficient;
//                    System.out.println(distance);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Timer servoTimer = new Timer("Servo");
        servoTimer.schedule(servoTask, 0, servoInterval);
        new RadarFrame();
    }


}