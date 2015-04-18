package ludumdare32;

import java.awt.Graphics2D;

/**
 *
 * @author Letfik
 */
public class Character {

    private double x, y, vx, vy, hp;
    private final double maxhp;

    public Character(double x, double y, double hp) {
        this.x = x;
        this.y = y;
        this.hp = maxhp = hp;
    }

    public void move(){
        setX(getX() + getVx());
        setY(getY() + getVy());
    }
    
    public void paint(Graphics2D g) {
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
        if(this.hp > maxhp){
            this.hp = maxhp;
        }
        if(this.hp < 0){
            //die
        }
    }
    
    public double getMaxHp(){
        return maxhp;
    }
}
