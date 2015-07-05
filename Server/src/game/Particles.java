package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Particles {

    static ArrayList<Particles> particleList = new ArrayList();
    static ArrayList<Particles> removeList = new ArrayList();
    static ArrayList<Particles> drawParticles = new ArrayList();
    static ArrayList<Particles> newParticles = new ArrayList();

    double x, y;
    Color color;

    public static void updateAll() {
        for (Particles particle : newParticles) {
            particleList.add(particle);
        }
        newParticles.clear();
        for (Particles particle : particleList) {
            particle.update();
        }
        particleList.removeAll(removeList);
        Weather.currentWeatherParticles.removeAll(removeList);
        Weather.oldWeatherParticles.removeAll(removeList);
        removeList.clear();
    }

    public static void paintAll(Graphics2D g) {
        drawParticles = (ArrayList<Particles>)particleList.clone();
        for (Particles particle : drawParticles) {
            particle.paint(g);
        }
    }

    public Particles(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        //newParticles.add(this);
    }

    public void update() {
    }

    public void paint(Graphics2D g) {
    }
    
    public void remove() {
        removeList.add(this);
    }

}
