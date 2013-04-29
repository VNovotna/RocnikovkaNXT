package rocnikovka;

import java.io.IOException;
import java.io.InputStream;
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;

/**
 *
 * @author viki
 */
public class Ctecka extends Thread {

    private NXTConnection connection;

    Ctecka(NXTConnection connection) {
        super();
        this.connection = connection;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        LCD.drawString("Ctecka bezi", 0, 5);
        InputStream dis = connection.openInputStream();
        while (true) {
            try {
                int readedInt = dis.read();
                LCD.drawInt(readedInt, 3, 0, 1);
                CommTest.prikazy.add(readedInt);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                LCD.clear();
            } catch (IOException e) {
                LCD.drawString("IO Recive", 0, 7, true);
            }
        }
    }
}
