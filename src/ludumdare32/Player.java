package ludumdare32;

import java.awt.Graphics2D;

public class Player extends Character {
    
    double acceleration = 0.5;

    public Player(double x, double y, double hp) {
        super(x, y, hp, 13);
    }

    @Override
    public void paint(Graphics2D g) {
        g.fillOval((int)(getX()-getR()), (int)(getY()-getR()), (int)getR()*2, (int)getR()*2);
    }
}
