package ludumdare32;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import javax.imageio.ImageIO;

public class World {

    static boolean[][] collisionMap;
    static int[][] textureMap;
    static int width = 0;
    static int height = 0;
    static int pixelWidth = 0;
    static int pixelHeight = 0;

    static ImageObserver nothing = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    static void paint(Graphics2D g) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.drawImage(Tile.images[textureMap[x][y]], x * 32, y * 32, 32, 32, nothing);
//                if (collisionMap[x][y]) {
//                    g.drawImage(Tile.images[549], x*32, y*32, nothing);
//                }
            }
        }
        /*for (int y = 0;y<Tile.verticalTiles;y++) {
         for (int x = 0;x<Tile.horizontalTiles;x++) {
         //g.drawImage(Tile.images[y*Tile.horizontalTiles+x], x*16, y*16, nothing);
         g.drawImage(Tile.images[y*Tile.horizontalTiles+x], x*32, y*32, 32, 32, nothing);
         }
         }
         g.drawImage(Tile.images[0], 0, 0, nothing);*/
    }

    public static void loadFromFile(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(World.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Level not found");
            return;
        }
        width = img.getWidth();
        height = img.getHeight();
        pixelWidth = width*32;
        pixelHeight = height*32;
        textureMap = new int[width][height];
        collisionMap = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = img.getRGB(x, y);
                int alpha = 0xFF & (argb >> 24);
                int red = 0xFF & (argb >> 16);
                int green = 0xFF & (argb >> 8);
                int blue = 0xFF & (argb >> 0);
                textureMap[x][y] = red + green * 256;
                collisionMap[x][y] = blue == 1;
            }
        }
    }
}
