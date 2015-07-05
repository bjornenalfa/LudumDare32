package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class SnowParticle extends Particles {

    double length = 10;
    double size = 5;
    double lifeTime = 0;
    double life = 0;
    double speed = 0;

    double ox = 0;
    double oy = 0;

    public SnowParticle(double x, double y) {
        super(x, y, Color.WHITE);
        if (Weather.transitioning && Weather.old == Weather.SNOWY) {
            Weather.oldWeatherParticles.add(this);
        } else {
            Weather.currentWeatherParticles.add(this);
        }
        length = 15 + Math.random() * 15;
        lifeTime = 100 + Math.random() * 50;
        speed = 2 + Math.random() * 4;
        ox = x;
        oy = y;
        size = 2 + Math.random() * 3;
    }

    @Override
    public void update() {
        life += 1;
        if (life > lifeTime) {
            remove();
        } else {
            oy += speed;
            //WIND
            ox += Wind.dvx * 6;
            oy += Wind.dvy;
            // SWAY
            x = ox + Math.cos(life/length)*10;
            y = oy + Math.sin(life/length)*10;
        }
    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (x - size), (int) (y - size), (int) (size * 2), (int) (size * 2));
    }

}
