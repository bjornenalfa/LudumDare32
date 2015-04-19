package ludumdare32;

import java.awt.geom.Point2D;

public class Snowy extends Weather {

    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        Tile.loadTileSet("img/Spritesheet/snowy.png", 16, 1);
        World.renderMap();
        Weather.current = Weather.SNOWY;
    }
    
}
