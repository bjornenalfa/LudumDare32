package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Particles {
    
    static ArrayList<Particles> particleList = new ArrayList();
    static ArrayList<Particles> removeList = new ArrayList();
    
    double x, y;
    Color color;
    
    public Particles(double x, double y, Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    
    public void update(){
    }
    
    public void paint(Graphics2D g){
    }
    
}
