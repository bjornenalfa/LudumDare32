package mapeditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class World {

//    static int worldNumber = 0;
    static String[] worlds = {"test", "test2"};

    TileSet currentTileSet;

    static final int squareSize = 16;

    boolean[][][] collisionMap;
    boolean[][] renderAbove;
    int[][] textureMap1;
    int[][] textureMap2;
    int width = 0;
    int height = 0;
    int pixelWidth = 0;
    int pixelHeight = 0;

    double xOffset = 0;
    double yOffset = 0;

    BufferedImage layer1;
    BufferedImage layer2;

    String name = "";

    static AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
    static ImageObserver nothing = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public World(BufferedImage image, TileSet tileSet) {
        currentTileSet = tileSet;
        width = image.getWidth();
        height = image.getHeight();
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap1 = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = image.getRGB(x, y);
                boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                boolean aboveCharacters = 1 == (1 & (argb >> 25));

                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                textureMap1[x][y] = texture1;
                textureMap2[x][y] = texture2;
                renderAbove[x][y] = aboveCharacters;
                collisionMap[x][y][0] = cloudy;
                collisionMap[x][y][1] = sunny;
                collisionMap[x][y][2] = rainy;
                collisionMap[x][y][3] = snowy;
            }
        }
        renderMap();
    }

    public World(BufferedImage image, String name, TileSet tileSet) {
        currentTileSet = tileSet;
        width = image.getWidth();
        height = image.getHeight();
        this.name = name;
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap1 = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = image.getRGB(x, y);
                boolean cloudy = 1 == (1 & (argb >> 31));
                boolean rainy = 1 == (1 & (argb >> 30));
                boolean sunny = 1 == (1 & (argb >> 29));
                boolean snowy = 1 == (1 & (argb >> 28));
                boolean aboveCharacters = 1 == (1 & (argb >> 25));

                int texture1 = 4095 & (argb >> 12);
                int texture2 = 4095 & argb;
                textureMap1[x][y] = texture1;
                textureMap2[x][y] = texture2;
                renderAbove[x][y] = aboveCharacters;
                collisionMap[x][y][0] = cloudy;
                collisionMap[x][y][1] = sunny;
                collisionMap[x][y][2] = rainy;
                collisionMap[x][y][3] = snowy;
            }
        }
        renderMap();
    }

    public World(int w, int h, TileSet tileSet) {
        currentTileSet = tileSet;
        width = w;
        height = h;
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap1 = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        renderMap();
    }

    public World(int w, int h, int x, int y, String name, TileSet tileSet) {
        currentTileSet = tileSet;
        width = w;
        height = h;
        xOffset = x;
        yOffset = y;
        this.name = name;
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap1 = new int[width][height];
        textureMap2 = new int[width][height];
        renderAbove = new boolean[width][height];
        collisionMap = new boolean[width][height][4];
        renderMap();
    }

    public void setName(String newName) {
        name = newName;
    }

    public void move(double dx, double dy) {
        xOffset += dx;
        yOffset += dy;
    }

    public void setOffset(double x, double y) {
        xOffset = x;
        yOffset = y;
    }

    public void expand(int up, int right, int down, int left) {
        int newWidth = width + right + left;
        int newHeight = height + up + down;
        int[][] newTM = new int[newWidth][newHeight];
        int[][] newTM2 = new int[newWidth][newHeight];
        boolean[][] newRA = new boolean[newWidth][newHeight];
        boolean[][][] newCM = new boolean[newWidth][newHeight][4];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newTM[x + left][y + up] = textureMap1[x][y];
                newTM2[x + left][y + up] = textureMap2[x][y];
                newRA[x + left][y + up] = renderAbove[x][y];
                newCM[x + left][y + up] = collisionMap[x][y];
            }
        }

        width = newWidth;
        height = newHeight;
        pixelWidth = width * squareSize;
        pixelHeight = height * squareSize;
        textureMap1 = newTM;
        textureMap2 = newTM2;
        renderAbove = newRA;
        collisionMap = newCM;
        renderMap();
        move(-left * squareSize, -up * squareSize);
    }

    public void contract(int up, int right, int down, int left) {
        int newWidth = width - right - left;
        int newHeight = height - up - down;
        int[][] newTM = new int[newWidth][newHeight];
        int[][] newTM2 = new int[newWidth][newHeight];
        boolean[][] newRA = new boolean[newWidth][newHeight];
        boolean[][][] newCM = new boolean[newWidth][newHeight][4];

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                newTM[x][y] = textureMap1[x + left][y + up];
                newTM2[x][y] = textureMap2[x + left][y + up];
                newRA[x][y] = renderAbove[x + left][y + up];
                newCM[x][y] = collisionMap[x + left][y + up];
            }
        }
        if (newWidth > 0 && newHeight > 0) {
            width = newWidth;
            height = newHeight;
            pixelWidth = width * squareSize;
            pixelHeight = height * squareSize;
            textureMap1 = newTM;
            textureMap2 = newTM2;
            renderAbove = newRA;
            collisionMap = newCM;
            renderMap();
            move(left * squareSize, up * squareSize);
        }
    }

    public void setTileSet(TileSet tileSet) {
        currentTileSet = tileSet;
    }

    public void changeTileSet(TileSet tileSet) {
        setTileSet(tileSet);
        renderMap();
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //int data = (collisionMap[x][y][0] ? 1 : 0) << 31 | (collisionMap[x][y][1] ? 1 : 0) << 30 | (collisionMap[x][y][2] ? 1 : 0) << 29 | (collisionMap[x][y][3] ? 1 : 0) << 28 | (renderAbove[x][y] ? 1 : 0) << 25 | textureMap[x][y] << 12 | textureMap2[x][y];
                image.setRGB(x, y, (collisionMap[x][y][0] ? 1 : 0) << 31 | (collisionMap[x][y][1] ? 1 : 0) << 30 | (collisionMap[x][y][2] ? 1 : 0) << 29 | (collisionMap[x][y][3] ? 1 : 0) << 28 | (renderAbove[x][y] ? 1 : 0) << 25 | textureMap1[x][y] << 12 | textureMap2[x][y]);
            }
        }
        return image;
    }

    public Tile getTileFromWorldCoordinates(double x, double y) {
        x -= xOffset;
        y -= yOffset;
        x /= squareSize;
        y /= squareSize;
        return getTileFromGridCoordinates((int) x, (int) y);
    }

    public Tile getTileFromGridCoordinates(int x, int y) {
        if (gridPointInWorld(x, y)) {
            return new Tile(textureMap1[x][y], textureMap2[x][y], renderAbove[x][y], collisionMap[x][y]);
        }
        return null;
    }

    public boolean worldPointInWorld(Point p) {
        return worldPointInWorld(p.x, p.y);
    }

    public boolean worldPointInWorld(Point.Double p) {
        return worldPointInWorld(p.x, p.y);
    }

    public boolean worldPointInWorld(double x, double y) {
        x -= xOffset;
        y -= yOffset;
        return !(x < 0 || x >= pixelWidth || y < 0 || y >= pixelHeight);
    }

    public boolean gridPointInWorld(int x, int y) {
        return !(x < 0 || x >= width || y < 0 || y >= height);
    }

    public Point worldPointToGridPoint(Point.Double p) {
        return new Point((int) ((p.x - xOffset) / squareSize), (int) ((p.y - yOffset) / squareSize));
    }
    
    public Point.Double gridPointToWorldPoint(Point p) {
        return new Point.Double(p.x*squareSize+xOffset,p.y*squareSize+yOffset);
    }

    public void changeTileWorldCoordinates(double x, double y, int tile1, int tile2, boolean cloudy, boolean sunny, boolean rainy, boolean snowy, boolean renderAbov) {
        x -= xOffset;
        y -= yOffset;
        x /= squareSize;
        y /= squareSize;
        changeTileGridCoordinates((int) x, (int) y, tile1, tile2, cloudy, sunny, rainy, snowy, renderAbov);
    }

    public void changeTileGridCoordinates(int x, int y, int tile1, int tile2, boolean cloudy, boolean sunny, boolean rainy, boolean snowy, boolean renderAbov) {
        if (gridPointInWorld(x, y)) {
            collisionMap[x][y][0] = cloudy;
            collisionMap[x][y][1] = sunny;
            collisionMap[x][y][2] = rainy;
            collisionMap[x][y][3] = snowy;
            textureMap1[x][y] = tile1;
            textureMap2[x][y] = tile2;
            renderAbove[x][y] = renderAbov;
            Graphics2D g2d = (Graphics2D) layer1.getGraphics();
            g2d.setComposite(composite);
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
            layer1.createGraphics().drawImage(currentTileSet.images[textureMap1[x][y]], x * squareSize, y * squareSize, nothing);

            g2d = (Graphics2D) layer2.getGraphics();
            g2d.setComposite(composite);
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
            layer2.createGraphics().drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);
        }
    }

    public void changeTileWorldCoordinates(double x, double y, Tile tile, int layer) {
        x -= xOffset;
        y -= yOffset;
        x /= squareSize;
        y /= squareSize;
        changeTileGridCoordinates((int) x, (int) y, tile, layer);
    }

    public void changeTileGridCoordinates(int x, int y, Tile tile, int layer) {
        if (gridPointInWorld(x, y)) {
            collisionMap[x][y] = tile.collisionMap;
            if (layer == 1 || layer == 0) {
                textureMap1[x][y] = tile.texture1;
                Graphics2D g2d = (Graphics2D) layer1.getGraphics();
                g2d.setComposite(composite);
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
                layer1.createGraphics().drawImage(currentTileSet.images[textureMap1[x][y]], x * squareSize, y * squareSize, nothing);
            }
            if (layer == 2 || layer == 0) {
                renderAbove[x][y] = tile.renderAbove;
                textureMap2[x][y] = tile.texture2;
                Graphics2D g2d = (Graphics2D) layer2.getGraphics();
                g2d.setComposite(composite);
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
                layer2.createGraphics().drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);
            }
        }
    }

//    public void changeTileLayer1WorldCoordinates(double x, double y, int tile) {
//        x -= xOffset;
//        y -= yOffset;
//        x /= squareSize;
//        y /= squareSize;
//        changeTileLayer1GridCoordinates((int) x, (int) y, tile);
//    }
//
//    public void changeTileLayer1GridCoordinates(int x, int y, int tile) {
//        if (gridPointInWorld(x, y)) {
//            textureMap1[x][y] = tile;
//
//            Graphics2D g2d = (Graphics2D) layer1.getGraphics();
//            g2d.setComposite(composite);
//            g2d.setColor(new Color(0, 0, 0, 0));
//            g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
//            layer1.createGraphics().drawImage(currentTileSet.images[textureMap1[x][y]], x * squareSize, y * squareSize, nothing);
//        }
//    }
//
//    public void changeTileLayer1WorldCoordinates(double x, double y, int tile, boolean renderAbove) {
//        x -= xOffset;
//        y -= yOffset;
//        x /= squareSize;
//        y /= squareSize;
//        changeTileLayer2GridCoordinates((int) x, (int) y, tile, renderAbove);
//    }
//    
//    public void changeTileLayer2GridCoordinates(int x, int y, int tile, boolean ra) {
//        if (gridPointInWorld(x, y)) {
//            textureMap2[x][y] = tile;
//            renderAbove[x][y] = ra;
//
//            Graphics2D g2d = (Graphics2D) layer2.getGraphics();
//            g2d.setComposite(composite);
//            g2d.setColor(new Color(0, 0, 0, 0));
//            g2d.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
//            layer2.createGraphics().drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);
//        }
//    }
//    
//    public void changeTileLayer1WorldCoordinates(double x, double y, int tile, boolean[] collisions) {
//        x -= xOffset;
//        y -= yOffset;
//        x /= squareSize;
//        y /= squareSize;
//        changeTileCollisionGridCoordinates((int) x, (int) y, collisions);
//    }
//    
//    public void changeTileCollisionGridCoordinates(int x, int y, boolean[] collisions) {
//        if (gridPointInWorld(x, y)) {
//            collisionMap[x][y] = collisions;
//        }
//    }
    public void renderMap() {
        layer1 = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) layer1.getGraphics();
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, pixelWidth, pixelHeight);
        g2d = layer1.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    g2d.drawImage(currentTileSet.images[textureMap1[x][y]], x * squareSize, y * squareSize, nothing);
                } catch (Exception e) {
                    g2d.drawImage(currentTileSet.images[0], x * squareSize, y * squareSize, nothing);
                }
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
                try {
                    g2d.drawImage(currentTileSet.images[textureMap2[x][y]], x * squareSize, y * squareSize, nothing);
                } catch (Exception e) {
                    g2d.drawImage(currentTileSet.images[0], x * squareSize, y * squareSize, nothing);
                }
            }
        }
    }

    public void paintLayer1(Graphics2D g) {
        g.drawImage(layer1, (int) xOffset, (int) yOffset, nothing);
    }

    public void paintLayer2(Graphics2D g) {
        g.drawImage(layer2, (int) xOffset, (int) yOffset, nothing);
    }

    public void drawBorder(Graphics2D g, Color c) {
        g.setColor(c);
        g.drawRect((int) xOffset, (int) yOffset, (int) pixelWidth, (int) pixelHeight);
    }

    public static World loadFromFile(String path, TileSet tileSet) {
        try {
            return loadFromImage(ImageIO.read(new File(path)), tileSet);
        } catch (IOException e) {
            System.out.println("Level not found");
            return null;
        }
    }

    public static World loadFromImage(BufferedImage image, TileSet tileSet) {
        return new World(image, tileSet);
    }

    public static World loadFromFile(String path, String name, TileSet tileSet) {
        try {
            return loadFromImage(ImageIO.read(new File(path)), name, tileSet);
        } catch (IOException e) {
            System.out.println("Level not found");
            return null;
        }
    }

    public static World loadFromImage(BufferedImage image, String name, TileSet tileSet) {
        return new World(image, name, tileSet);
    }
}
