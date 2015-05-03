package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Rainy extends Weather{
    
    static TileSet tileSet = new TileSet("img/Spritesheet/rainy.png", 16, 1);

    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        //Tile.loadTileSet("img/Spritesheet/rainy.png", 16, 1);
        World.changeTileSet(tileSet);
        World.renderMap();
        Weather.current = Weather.RAINY;
    }
    
    public static void makeParticles() {
        if (transitioning && old == RAINY) {
            for (int i = 0;i<3;i++) {
                RainParticle particle = new RainParticle(-LudumDare32.camera.translateX+Math.random()*1600-400,-LudumDare32.camera.translateY-50-Math.random()*100,1);
                //oldWeatherParticles.add(particle);
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
    }
    
}
