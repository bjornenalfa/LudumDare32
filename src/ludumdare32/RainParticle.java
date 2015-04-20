package ludumdare32;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class RainParticle extends Particles {

    static BasicStroke rainStroke = new BasicStroke(1.5f);
    static BasicStroke normalStroke = new BasicStroke();

    double length = 10;
    double size = 5;
    int type = 1;
    double lifeTime = 0;
    double life = 0;
    double speed = 0;

    public RainParticle(double x, double y, int type) {
        super(x, y, type == 1 ? new Color(0, 100, 255, 100) : new Color(255, 255, 255, 255));
        if (Weather.transitioning && Weather.old == Weather.RAINY) {
            Weather.oldWeatherParticles.add(this);
        } else {
            Weather.currentWeatherParticles.add(this);
        }
        this.type = type;
        if (type == 1) {
            length += Math.random() * 20;
            lifeTime = 35 + Math.random() * 100;
            speed = 5 + Math.random()*5;
        } else {
            size = 5;
            lifeTime = 20 + Math.random() * 10;
            speed = 0.3;
        }
    }

    @Override
    public void update() {
        life += 1;
        if (type == 1) {
            y += speed;
            if (life > lifeTime) {
                remove();
                RainParticle splash = new RainParticle(x,y+length,2);
            }
        } else {
            size += speed;
            if (life > lifeTime) {
                remove();
            }
        }
    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(color);
        if (type == 1) {
            g.setStroke(rainStroke);
            g.drawLine((int) x, (int) y, (int) x, (int) (y+length));
            g.setStroke(normalStroke);
        } else {
            g.drawOval((int)(x-size), (int)(y-size*0.5), (int)(size*2), (int)(size));
        }
    }

}
