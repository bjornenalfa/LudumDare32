package ludumdare32mapeditor;

import java.awt.Graphics2D;
import java.awt.Point;

public class Camera {

    private double translateX = 0, translateY = 0;
    int halfWidth = 400, halfHeight = 304;
    int width = 800, height = 608;
    private double scale = 1;

    public Camera() {
//        halfWidth = panel.getWidth() / 2;
//        halfHeight = panel.getHeight() / 2;
    }

    public void zoom(double scaleMultiplier, Point.Double point) {
        scale *= scaleMultiplier;
    }

    public void zoomCenter(double scaleMultiplier) {
        
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
