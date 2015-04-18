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
    static int[][] textureMap2;
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
                /*boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                
                int argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (texture1 << 12 ) | texture2;*/
                
                int alpha = 255 & (argb >> 24);
                int red = 255 & (argb >> 16);
                int green = 255 & (argb >> 8);
                int blue = 255 & (argb >> 0);
                textureMap[x][y] = red + green * 256;
                //textureMap[x][y] = texture1;
                //textureMap2[x][y] = texture2;
                collisionMap[x][y] = blue == 1;
            }
        }
    }
}
