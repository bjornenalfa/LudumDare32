package ludumdare32;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Tile {
    //static HashMap<String,BufferedImage> images = new HashMap();
    static BufferedImage[] images;
    static HashMap<String,Tile> tiles = new HashMap();
    
    int imageID;
    BufferedImage image;
    String name;
    boolean collides;
    
    public static void loadTileSet(String path,int size,int margin) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(Tile.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Tiles not found");
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int horizontalTiles = (int) Math.ceil((double)width/(size+margin));
        int verticalTiles = (int) Math.ceil((double)height/(size+margin));
        
        System.out.println("width:"+width);
        
        images = new BufferedImage[horizontalTiles*verticalTiles];
        int iterator = 0;
        
        for (int y = 0;y<verticalTiles;y++) {
            for (int x = 0;x<horizontalTiles;x++) {
                BufferedImage img2 = img.getSubimage(x*(size+margin), y*(size+margin), size, size);
                images[iterator] = img2;
                iterator++;
            }
        }
    }
    
    public Tile(String nam, int tileImage, boolean collision) {
        collides = collision;
        name = nam;
        imageID = tileImage;
        image = images[tileImage];
        
        tiles.put(name, this);
    }
}
