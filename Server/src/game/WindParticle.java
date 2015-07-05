package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class WindParticle extends Particles {

    public WindParticle(double x, double y) {
        super(x, y, Color.WHITE);
    }

    private int counter = 0;

    private final ArrayList<Integer> xList = new ArrayList();
    private final ArrayList<Integer> yList = new ArrayList();
    double distance = 10 * Wind.getPower();
    double angle = Wind.direction;
    private int spiralLengthCounter = 0;
    private final int spiralLength = (int) (Math.random() * 10 + 20);

    @Override
    public void update() {
        if (Math.random() < 0.5 / 60 && counter > spiralLengthCounter) {
            spiralLengthCounter = counter + spiralLength;
        }

        if (counter == 0) {
            xList.add((int) x);
            yList.add((int) y);
        } else if (counter < spiralLengthCounter) {
            angle += (Math.PI * 2) / (double) (spiralLength);
            xList.add((int) (xList.get(xList.size() - 1) + Math.cos(angle) * distance));
            yList.add((int) (yList.get(yList.size() - 1) + Math.sin(angle) * distance));
        } else {
            angle -= (angle-Wind.direction);
            xList.add((int) (xList.get(xList.size() - 1) + Math.cos(angle) * distance));
            yList.add((int) (yList.get(yList.size() - 1) + Math.sin(angle) * distance));
        }
        
        counter++;
        
        if (counter > 100) {
            xList.remove(0);
            yList.remove(0);
            xList.remove(0);
            yList.remove(0);
            if(xList.isEmpty()){
                remove();
            }
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if (Wind.power > 0) {
            g.setColor(color);
            for (int i = 1; i < xList.size(); i++) {
                g.drawLine(xList.get(i - 1), yList.get(i - 1), xList.get(i), yList.get(i));
            }
        }
    }
}
