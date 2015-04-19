package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Objects;

public class WindParticle extends Particles {

    public WindParticle(double x, double y) {
        super(x, y, Color.WHITE);
    }

    private int counter = 0;

    private ArrayList<Integer> xList = new ArrayList();
    private ArrayList<Integer> yList = new ArrayList();
    double distance = 10 * Wind.power;
    double angle = Wind.direction;
    private int spiralLength = 0;

    @Override
    public void update() {
        if (Math.random() < 0.2 / 60 && counter > spiralLength) {
            spiralLength = counter + (int) (Math.random() * 10 + 20);
        }

        if (counter == 0) {
            xList.add((int) x);
            yList.add((int) y);
        } else if (counter < spiralLength) {
            angle += (Math.PI * 2) / (double) (spiralLength);
            xList.add((int) (xList.get(xList.size() - 1) + Math.cos(angle) * distance));
            yList.add((int) (yList.get(yList.size() - 1) + Math.sin(angle) * distance));
        } else {
            xList.add((int) (xList.get(xList.size() - 1) + Math.cos(angle) * distance));
            yList.add((int) (yList.get(yList.size() - 1) + Math.sin(angle) * distance));
        }
        counter++;
        if (counter > 100) {
            xList.remove(0);
            xList.remove(0);
        }
        if (!Objects.equals(xList, null) && xList.isEmpty()) {
            Particles.removeList.add(this);
            System.out.println("REMOVING");
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if (Wind.power > 0) {
            g.setColor(color);
            for (int i = 1; i < xList.size(); i++) {
                g.drawLine(xList.get(i - 1), yList.get(i - 1), yList.get(i), yList.get(i));
            }
        }
    }
}
