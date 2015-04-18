
package ludumdare32;

public class Wind {
    static double direction = 0;
    static double power = 0.1;
    
    public static void update() {
        if (power > 0) {
            double dvx = Math.cos(direction)*power;
            double dvy = Math.sin(direction)*power;

            for (Character character : Character.characters) {
                character.changeVx(dvx);
                character.changeVy(dvy);
            }
        }
    }
}
