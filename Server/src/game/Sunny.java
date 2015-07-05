package game;

import java.awt.Point;

public class Sunny extends Weather{
    
    static TileSet tileSet = new TileSet("img/Spritesheet/sunny.png", 16, 1);
    
    public static void activate(Point.Double point) {
        Weather.startTransition(point);
        //Tile.loadTileSet("img/Spritesheet/sunny.png", 16, 1);
        World.changeTileSet(tileSet);
        World.renderMap();
        Weather.current = Weather.SUNNY;
    }
}
