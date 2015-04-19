package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;

public class WindParticle extends Particles {

    public WindParticle(double x, double y) {
        super(x, y, Color.WHITE);
    }

    private int counter = 0;
    private int lineLength;
    private int spiralLength;
    private int[] xList;
    private int[] yList;

    public void calcSpiral() {
        double distance = 10 * Wind.power;
        double angle = Wind.direction;

        lineLength = (int) (Math.random() * 10 + 20);
        spiralLength = (int) (Math.random() * 10 + 20);

        xList = new int[2 * lineLength + spiralLength];
        yList = new int[2 * lineLength + spiralLength];

        xList[0] = (int) x;
        yList[0] = (int) y;

        for (int i = 1; i < lineLength; i++) {
            xList[i] = (int) (xList[i - 1] + Math.cos(angle) * distance);
            yList[i] = (int) (yList[i - 1] + Math.sin(angle) * distance);
        }
        for (int i = lineLength; i < lineLength + spiralLength; i++) {
            angle += (Math.PI * 2) / (double) (spiralLength);
            xList[i] = (int) (xList[i - 1] + Math.cos(angle) * distance);
            yList[i] = (int) (yList[i - 1] + Math.sin(angle) * distance);
        }
        for (int i = lineLength + spiralLength; i < 2 * lineLength + spiralLength; i++) {
            xList[i] = (int) (xList[i - 1] + Math.cos(angle) * distance);
            yList[i] = (int) (yList[i - 1] + Math.sin(angle) * distance);
        }
    }

    @Override
    public void update() {
        if (counter > 2 * lineLength + spiralLength + (2 * lineLength + spiralLength) / 2) {
            Particles.removeList.add(this);
        } else if (counter > (2 * lineLength + spiralLength) / 2) {
            System.arraycopy(xList, 1, xList, 0, xList.length - 1);
            System.arraycopy(yList, 1, yList, 0, yList.length - 1);
        }
        if (counter == 0) {
            calcSpiral();
        }
        counter++;
    }

    @Override
    public void paint(Graphics2D g) {
        if (Wind.power > 0) {
            g.setColor(color);
            g.drawPolyline(xList, yList, Math.min(counter, 2 * lineLength + spiralLength));
        }
    }
}
