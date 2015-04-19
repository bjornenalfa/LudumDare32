package ludumdare32;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Weather {

    static BufferedImage oldLayer1;
    static BufferedImage oldLayer2;
    static BufferedImage oldLayer3;
    static Ellipse2D.Double transitionCircle;
    static double transitionTime = 0;
    static Point.Double transitionPoint;
    static boolean transitioning = false;
    static double transitionSpeed = 200; // Pixels per second^2

    public static void startTransition(Point.Double point) {
        oldLayer1 = World.layer1;
        oldLayer2 = World.layer2;
        oldLayer3 = World.layer3;
        transitionTime = 0;
        transitionPoint = point;
        transitionCircle = new Ellipse2D.Double(transitionPoint.x, transitionPoint.y, 0, 0);
        transitioning = true;
    }

    public static void paintTransition(Graphics2D g) {
        if (transitioning) {
            g.drawImage(oldLayer1, 0, 0, null);
            g.drawImage(oldLayer2, 0, 0, null);
            g.setClip(transitionCircle);
        }
    }

    public static void paintTransition2(Graphics2D g) {
        if (transitioning) {
            g.drawImage(oldLayer3, 0, 0, null);
            g.setClip(transitionCircle);
        }
    }

    public static void paintTransitionClearClip(Graphics2D g) {
        if (transitioning) {
            g.setClip(null);
        }
    }

    public static void updateTransition() {
        if (transitioning) {
            transitionTime += 1 / 60d;
            double diameter = transitionTime * transitionTime * transitionSpeed * 0.5;
            transitionCircle.setFrame(transitionPoint.x - diameter, transitionPoint.y - diameter, diameter*2, diameter*2);
            //SHAKY
            //transitionCircle.setFrame(transitionPoint.x - diameter-Math.cos(transitionTime*20)*diameter*0.1, transitionPoint.y - diameter-Math.sin(transitionTime*20)*diameter*0.1, diameter*2+Math.cos(transitionTime*20)*diameter*0.2, diameter*2+Math.sin(transitionTime*20)*diameter*0.2);
            Graphics2D g = oldLayer3.createGraphics();
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
            g.setComposite(composite);
            g.setColor(new Color(0, 0, 0, 0));
            g.fill(transitionCircle);

            if ((transitionPoint.x + diameter * 0.71 > World.pixelWidth) && (transitionPoint.y + diameter * 0.71 > World.pixelHeight) && (transitionPoint.x - diameter * 0.71 < 0) && (transitionPoint.y - diameter * 0.71 < 0)) {
                transitioning = false;
            }
//            if (transitionTime > 5) {
//                transitioning = false;
//            }
        }
    }

    public static void update() {

    }

    public static void paint(Graphics2D g) {

    }

    public static void activate() {

    }

    public static void deactivate() {

    }

}
