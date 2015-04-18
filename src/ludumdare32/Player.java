package ludumdare32;

import java.awt.Graphics2D;

public class Player extends Character {

    public Player(double x, double y, double hp) {
        super(x, y, hp, 20);
    }

    @Override
    public void paint(Graphics2D g) {
        g.fillOval((int)getX(), (int)getY(), (int)getR()*2, (int)getR()*2);
    }
}
