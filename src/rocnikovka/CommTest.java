package rocnikovka;

import java.util.ArrayList;
import java.util.List;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;

/**
 *
 * @author viki
 */
public class CommTest {

    public volatile static List prikazy = new ArrayList();
    public volatile static int zapsat = 0;

    public static void main(String[] args) throws Exception {
        String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        UltrasonicSensor sonicSensor = new UltrasonicSensor(SensorPort.S3);

        while (true) {
            LCD.drawString(waiting, 0, 0);
            NXTConnection connection = Bluetooth.waitForConnection();
            LCD.clear();
            LCD.drawString(connected, 0, 0);

            //Thread ctecka = new Ctecka(connection);
            Thread zapisnik = new Zapisnik(connection);
            //ctecka.start();
            zapisnik.start();
            LCD.clear();
            LCD.drawString("pobiham a jodluju", 0, 0);
            for (int i = 0; i < 100; i++) {
                zapsat = sonicSensor.getDistance();
                LCD.drawInt(zapsat, 0, 3);
                Delay.msDelay(400);
                LCD.clear(3);
            }
            LCD.clear();
            LCD.drawString("koncim", 0, 0);
            zapisnik.interrupt();
            Delay.msDelay(1000);

            LCD.clear();
            LCD.drawString(closing, 0, 0);

            connection.close();

            LCD.clear();
        }
    }

    public static void statusMonitor(UltrasonicSensor ulSensor, ColorSensor clrSensor, TouchSensor touchSensor) {
        //monitor
        LCD.clear(1);
        LCD.drawString("A s:" + Motor.A.getSpeed() + " p:" + Motor.A.getTachoCount(), 0, 1);
        LCD.clear(2);
        LCD.drawString("C s:" + Motor.C.getSpeed() + " p:" + Motor.C.getTachoCount(), 0, 2);
        if (ulSensor != null) {
            LCD.clear(3);
            int distance = ulSensor.getDistance();
            if (distance < 255) {
                LCD.drawString("Dist:" + distance, 0, 3);
            } else {
                LCD.drawString("Dist: E:" + distance, 0, 3);
            }

        }
        if (clrSensor != null) {
            String color;
            if (clrSensor.isFloodlightOn()) {
                color = colorName(clrSensor.getColorID());
            } else {
                color = "flood light";
            }
            LCD.clear(4);
            LCD.drawString("Clr:" + color, 0, 4);
            if (touchSensor != null) {
                LCD.clear(5);
                LCD.drawString("Touch:" + touchSensor.isPressed(), 0, 5);
            }
        }
    }

    public static String colorName(int colorInt) {
        String color;
        switch (colorInt) {
            case ColorSensor.Color.BLACK:
                color = "BLACK";
                break;
            case ColorSensor.Color.RED:
                color = "RED";
                break;
            case ColorSensor.Color.BLUE:
                color = "BLUE";
                break;
            case ColorSensor.Color.GREEN:
                color = "GREEN";
                break;
            case ColorSensor.Color.YELLOW:
                color = "YELLOW";
                break;
            case ColorSensor.Color.WHITE:
                color = "WHITE";
                break;
            default:
                color = "NaN";
                break;
        }
        return color;
    }
}