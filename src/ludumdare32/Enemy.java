package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy extends Character {

    public Enemy(double x, double y, double hp, double r, double acc) {
        super(x, y, hp, r, acc);
    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(Color.red);
        g.fillOval((int)(getX()-getR()), (int)(getY()-getR()), (int)getR()*2, (int)getR()*2);
    }
}
