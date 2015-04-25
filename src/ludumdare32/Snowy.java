package ludumdare32;

import java.awt.geom.Point2D;

public class Snowy extends Weather {

    static TileSet tileSet = new TileSet("img/Spritesheet/snowy.png", 16, 1);
    
    public static void activate(Point2D.Double point) {
        Weather.startTransition(point);
        //Tile.loadTileSet("img/Spritesheet/snowy.png", 16, 1);
        World.changeTileSet(tileSet);
        World.renderMap();
        Weather.current = Weather.SNOWY;
    }
    
}
