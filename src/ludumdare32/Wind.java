package ludumdare32;

public class Wind {

    static double direction = 0;
    static double power = 0;
    
    static boolean calculatedPower = false;
    
    static double targetX = 0;
    static double targetY = 0;
    
    static double dvx = 0;
    static double dvy = 0;

    public static void update() {
        dvx += (targetX-dvx)*0.025;
        dvy += (targetY-dvy)*0.025;
        calculatedPower = false;
        direction = Math.atan2(dvy,dvx);
        //if (power > 0) {

            for (Character character : Character.characters) {
                character.changeVx(dvx);
                character.changeVy(dvy);
            }
        //} else {
        //    dvx = 0;
        //    dvy = 0;
        //}
    }
    
    public static double getPower() {
        if (!calculatedPower) {
            power = Math.sqrt(dvx*dvx);
            calculatedPower = true;
        }
        return power;
    }
    
    public static void change(double newDirection, double newStrength) {
        targetX = Math.cos(newDirection)*newStrength;
        targetY = Math.sin(newDirection)*newStrength;
    }
}
