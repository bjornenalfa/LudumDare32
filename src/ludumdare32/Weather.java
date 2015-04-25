package ludumdare32;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Weather {

    static final int CLOUDY = 0;
    static final int SUNNY = 1;
    static final int RAINY = 2;
    static final int SNOWY = 3;
    
    //static TileSet tileSet;

    static int current = 0;
    static int old = 0;

    static ArrayList<Particles> currentWeatherParticles = new ArrayList();
    static ArrayList<Particles> oldWeatherParticles = new ArrayList();

    static BufferedImage oldLayer1;
    static BufferedImage oldLayer2;
    static BufferedImage oldLayer3;
    static Ellipse2D.Double transitionCircle;
    static double transitionTime = 0;
    static Point.Double transitionPoint;
    static boolean transitioning = false;
    static double transitionSpeed = 100; // Pixels per second^2

    public static void startTransition(Point.Double point) {
        old = current;
        oldLayer1 = World.layer1;
        oldLayer2 = World.layer2;
        oldLayer3 = World.layer3;
        transitionTime = 0;
        transitionPoint = point;
        transitionCircle = new Ellipse2D.Double(transitionPoint.x, transitionPoint.y, 0, 0);
        transitioning = true;
        oldWeatherParticles = (ArrayList<Particles>) currentWeatherParticles.clone();
        currentWeatherParticles.clear();
    }

    public static void stopTransition() {
        transitioning = false;
        for (Particles particle : oldWeatherParticles) {
            particle.remove();
        }
        oldWeatherParticles.clear();
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

    public static void paintCurrentParticles(Graphics2D g) {
        for (Particles particle : currentWeatherParticles) {
            particle.paint(g);
        }
    }

    public static void paintOldParticles(Graphics2D g) {
        for (Particles particle : oldWeatherParticles) {
            particle.paint(g);
        }
    }

    public static void updateParticles() {
        ArrayList<Particles> updatingParticles = (ArrayList<Particles>) currentWeatherParticles.clone();
        for (Particles particle : updatingParticles) {
            particle.update();
        }
        updatingParticles = (ArrayList<Particles>) oldWeatherParticles.clone();
        for (Particles particle : updatingParticles) {
            particle.update();
        }
    }

    public static void transitionClearClip(Graphics2D g) {
        if (transitioning) {
            g.setClip(null);
        }
    }

    public static void updateTransition() {
        if (transitioning) {
            transitionTime += 1 / 60d;
            double diameter = transitionTime * transitionTime * transitionTime * transitionTime * transitionSpeed * 0.5;
            transitionCircle.setFrame(transitionPoint.x - diameter, transitionPoint.y - diameter, diameter * 2, diameter * 2);
            //SHAKY
            //transitionCircle.setFrame(transitionPoint.x - diameter-Math.cos(transitionTime*20)*diameter*0.1, transitionPoint.y - diameter-Math.sin(transitionTime*20)*diameter*0.1, diameter*2+Math.cos(transitionTime*20)*diameter*0.2, diameter*2+Math.sin(transitionTime*20)*diameter*0.2);
            Graphics2D g = oldLayer3.createGraphics();
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
            g.setComposite(composite);
            g.setColor(new Color(0, 0, 0, 0));
            g.fill(transitionCircle);

            if ((transitionPoint.x + diameter * 0.71 > World.pixelWidth) && (transitionPoint.y + diameter * 0.71 > World.pixelHeight) && (transitionPoint.x - diameter * 0.71 < 0) && (transitionPoint.y - diameter * 0.71 < 0)) {
                stopTransition();
            }
//            if (transitionTime > 5) {
//                transitioning = false;
//            }
        }
    }

    public static void update() {
        updateParticles();
        switch (current) {
            case CLOUDY:
                //Cloudy.paint(g);
                break;
            case SUNNY:
                //Sunny.paint(g);
                break;
            case RAINY:
                Rainy.update();
                break;
            case SNOWY:
                //Snowy.paint(g);
                break;
        }
        if (transitioning) {
            switch (old) {
                case CLOUDY:
                    //Cloudy.paint(g);
                    break;
                case SUNNY:
                    //Sunny.paint(g);
                    break;
                case RAINY:
                    Rainy.update();
                    break;
                case SNOWY:
                    //Snowy.paint(g);
                    break;
            }
        }
    }

    public static void paintOldWeather(Graphics2D g) {
        if (transitioning) {
            Area outside = new Area(new Rectangle2D.Double(0, 0, World.pixelWidth, World.pixelHeight));
            outside.subtract(new Area(transitionCircle));
            g.setClip(outside);
            paintOldParticles(g);
            switch (old) {
                case CLOUDY:
                    //Cloudy.paint(g);
                    break;
                case SUNNY:
                    //Sunny.paint(g);
                    break;
                case RAINY:
                    Rainy.paint(g);
                    break;
                case SNOWY:
                    //Snowy.paint(g);
                    break;
            }
            g.setClip(null);
        }
    }

    public static void paint(Graphics2D g) {
        paintCurrentParticles(g);
        switch (current) {
            case CLOUDY:
                //Cloudy.paint(g);
                break;
            case SUNNY:
                //Sunny.paint(g);
                break;
            case RAINY:
                Rainy.paint(g);
                break;
            case SNOWY:
                //Snowy.paint(g);
                break;
        }
    }

    public static void activate() {

    }

    public static void deactivate() {

    }

}
