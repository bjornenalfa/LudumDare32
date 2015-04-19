package ludumdare32;

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
    static double transitionSpeed = 100; // Pixels per second
    
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
            transitionTime += 1/60d;
            transitionCircle.setFrame(transitionPoint.x-transitionTime*transitionSpeed*0.5, transitionPoint.y-transitionTime*transitionSpeed*0.5, transitionTime*transitionSpeed, transitionTime*transitionSpeed);
            if (transitionTime > 20) {
                transitioning = false;
            }
            System.out.println("Transition: "+transitionTime);
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
