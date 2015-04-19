package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Rainy extends Weather{

    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        Tile.loadTileSet("img/Spritesheet/rainy.png", 16, 1);
        World.renderMap();
        Weather.current = Weather.RAINY;
    }
    
    public static void paint(Graphics2D g) {
        g.setColor(new Color(100,100,100,100));
        g.fillRect(0,0,World.pixelWidth,World.pixelHeight);
    }
    
}
