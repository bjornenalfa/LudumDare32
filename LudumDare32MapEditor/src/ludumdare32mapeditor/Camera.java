package ludumdare32mapeditor;

import java.awt.Graphics2D;
import java.awt.Point;

public class Camera {

    private double translateX = 0, translateY = 0;
    private double halfWidth = 400, halfHeight = 304;
    int width = 800, height = 608;
    private double scale = 1;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        halfWidth = width / 2d;
        halfHeight = height / 2d;
    }
    
    public void changeSize(int width, int height) {
        this.width = width;
        this.height = height;
        halfWidth = width / 2d;
        halfHeight = height / 2d;
    }

    public void zoom(double scaleMultiplier, Point.Double point) {
        scale *= scaleMultiplier;
    }

    public void zoomCenter(double scaleMultiplier) {
        double x = -res * ((halfWidth) / scale - translateX);
        double y = -res * ((halfHeight) / scale - translateY);
        scale -= res * (scale * 0.1);
        translateX -= x * 0.1;
        translateY -= y * 0.1;
        translateX *= 1 / (1 - res * 0.1);
        translateY *= 1 / (1 - res * 0.1);
    }

    public void move(double dx, double dy) {
        translateX += dx;
        translateY += dy;
    }

    public void moveScaled(double dx, double dy) {
        translateX += dx / scale;
        translateY += dy / scale;
    }

    public void setTranslate(double x, double y) {
        translateX = x;
        translateY = y;
    }

    public void setScale(double scale) {
        scale = 1;
    }

    public void transformGraphics(Graphics2D g) {
        g.translate(translateX, translateY);
        g.scale(scale, scale);
    }

    public void resetTransform(Graphics2D g) {
        g.translate(0, 0);
    }

//    public void update(Player player, boolean centered) {
//        if (centered) {
//            translateX = player.getX() - halfWidth;
//            translateY = player.getY() - halfHeight;
//            translateX = -Math.max(0, Math.min(World.pixelWidth - width, translateX));
//            translateY = -Math.max(0, Math.min(World.pixelHeight - height, translateY));
//        }
//    }
}
