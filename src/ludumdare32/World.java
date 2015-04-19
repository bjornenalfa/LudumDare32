package ludumdare32;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import javax.imageio.ImageIO;

public class World {

    static int worldNumber = 0;
    static String[] worlds = {"test", "test2"};

    static boolean[][] collisionMap;
    static int[][] textureMap;
    static int[][] textureMap2;
    static int[][] textureMap3;
    static int width = 0;
    static int height = 0;
    static int pixelWidth = 0;
    static int pixelHeight = 0;

    static BufferedImage layer1;
    static BufferedImage layer2;
    static BufferedImage layer3;

    static ImageObserver nothing = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    static void renderMap() {
        layer1 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
        Graphics2D g2d = (Graphics2D) layer1.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer1.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2d.drawImage(Tile.images[textureMap[x][y]], x * 32, y * 32, 32, 32, nothing);

            }
        }

        layer2 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) layer2.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer2.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2d.drawImage(Tile.images[textureMap2[x][y]], x * 32, y * 32, 32, 32, nothing);
            }
        }

        layer3 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) layer3.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer3.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2d.drawImage(Tile.images[textureMap3[x][y]], x * 32, y * 32, 32, 32, nothing);
            }
        }
    }

    static void paint(Graphics2D g) {
        g.drawImage(layer1, 0, 0, nothing);
        g.drawImage(layer2, 0, 0, nothing);

//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                g.drawImage(Tile.images[textureMap[x][y]], x * 32, y * 32, 32, 32, nothing);
//                g.drawImage(Tile.images[textureMap2[x][y]], x * 32, y * 32, 32, 32, nothing);
//            }
//        }
    }

    static void paint2(Graphics2D g) {
        g.drawImage(layer3, 0, 0, nothing);
    }

    public static void loadWorld(int ID) {
        try {
            loadFromFile("levels/" + worlds[ID] + ".png");
            worldNumber = ID;
        } catch (Exception e) {
            System.out.println("Unknown world");
            System.out.println(e);
        }
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
        pixelWidth = width * 32;
        pixelHeight = height * 32;
        textureMap = new int[width][height];
        textureMap2 = new int[width][height];
        textureMap3 = new int[width][height];
        collisionMap = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = img.getRGB(x, y);
                boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                boolean aboveCharacters = 1 == (1 & (argb >> 25));
                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                textureMap[x][y] = texture1;
                if (aboveCharacters) {
                    textureMap3[x][y] = texture2;
                    textureMap2[x][y] = Tile.INVISIBLE;
                } else {
                    textureMap2[x][y] = texture2;
                    textureMap3[x][y] = Tile.INVISIBLE;
                }
                collisionMap[x][y] = cloudy;

                /*int argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (texture1 << 12 ) | texture2;
                
                 int alpha = 255 & (argb >> 24);
                 int red = 255 & (argb >> 16);
                 int green = 255 & (argb >> 8);
                 int blue = 255 & (argb >> 0);
                 textureMap[x][y] = red + green * 256;*/
            }
        }
        renderMap();
    }

    public static void update(Player player) {
        int playerTileX = (int) (player.getX() / 32);
        int playerTileY = (int) (player.getY() / 32);
        switch (worldNumber) {
            case 0:
                if (playerTileX == 11 && playerTileY == 6) {
                    loadWorld(1);
                    player.setX(9*32+16);
                    player.setY(5*32+16);
                    player.setVy(player.getVy()*-1);
                }
                break;
            case 1:
                if (playerTileX == 9 && playerTileY == 4) {
                    loadWorld(0);
                    player.setX(11*32+16);
                    player.setY(7*32+16);
                    player.setVy(player.getVy()*-1);
                }
                break;
        }
    }
}
