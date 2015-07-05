package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends Character {
    
    
    
    double acceleration = 0.5;

    public Player(double x, double y, double hp) {
        super(x, y, hp, 13, 0.5);
    }

    @Override
    public void paint(Graphics2D g) {
        //g.setXORMode(Color.WHITE);
        g.setColor(Color.BLUE);
        //g.fillOval((int)(getX()-getR()), (int)(getY()-getR()), (int)getR()*2, (int)getR()*2);
        g.drawImage(spriteSheet.images[0], (int)(getX() - 16), (int)(getY() - 16), 32, 32, null);
    }
    
    public void paintBehind(Graphics2D g) {
        //g.setXORMode(Color.WHITE);
        g.setColor(Color.DARK_GRAY);
        //g.fillOval((int)(getX()-getR()), (int)(getY()-getR()), (int)getR()*2, (int)getR()*2);
        g.drawImage(spriteSheet.images[108], (int)(getX() - 16), (int)(getY() - 16), 32, 32, null);
    }
}
