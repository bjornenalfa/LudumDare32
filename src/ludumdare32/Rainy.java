package ludumdare32;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Rainy extends Weather{
    
    static BasicStroke normal = new BasicStroke();
    static BasicStroke rainStroke = new BasicStroke(1.5f);

    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        Tile.loadTileSet("img/Spritesheet/rainy.png", 16, 1);
        World.renderMap();
        Weather.current = Weather.RAINY;
    }
    
    public static void makeParticles() {
        if (transitioning && old == RAINY) {
            for (int i = 0;i<3;i++) {
                RainParticle particle = new RainParticle(-LudumDare32.camera.translateX+Math.random()*1600-400,-LudumDare32.camera.translateY-50-Math.random()*100,1);
                oldWeatherParticles.add(particle);
            }
        } else {
            for (int i = 0;i<3;i++) {
                RainParticle particle = new RainParticle(-LudumDare32.camera.translateX+Math.random()*1600-400,-LudumDare32.camera.translateY-50-Math.random()*100,1);
            }
        }
    }
    
    public static void update() {
        makeParticles();
    }
    
    public static void paint(Graphics2D g) {
        g.setColor(new Color(100,100,100,100));
        g.fillRect(0,0,World.pixelWidth,World.pixelHeight);
//        g.setColor(new Color(0,50,255,100));
//        g.setStroke(rainStroke);
//        for (int i = 0;i < 30;i++) {
//            int x = (int) (-LudumDare32.camera.translateX+Math.random()*800);
//            int y = (int) (-LudumDare32.camera.translateY+Math.random()*608);
//            g.drawLine(x, y, x, y+(int)(Math.random()*5-2.5+15));
//        }
//        g.setStroke(normal);
    }
    
}
