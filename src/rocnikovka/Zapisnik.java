package rocnikovka;

import java.io.IOException;
import java.io.OutputStream;
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;

/**
 *
 * @author viki
 */
public class Zapisnik extends Thread {

    private NXTConnection connection;

    Zapisnik(NXTConnection connection) {
        super();
        this.connection = connection;
    }

    @Override
    public void run() {
        LCD.drawString("Zapisnik bezi", 0, 6);
        OutputStream dos = connection.openOutputStream();
        int localZapsat = 255;
        while (true) {
            if (localZapsat != CommTest.zapsat) {
                LCD.drawString("ZAPISUJI: " + localZapsat, 0, 7);
                try {
                    dos.write(localZapsat);
                    dos.flush();
                } catch (IOException e) {
                    LCD.drawString("IO Send", 0, 7, true);
                }
                localZapsat = CommTest.zapsat;
                Delay.msDelay(10);
            }
            LCD.clear(6);
            LCD.clear(7);
        }
    }
}
