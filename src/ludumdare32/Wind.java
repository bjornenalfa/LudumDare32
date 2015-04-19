package ludumdare32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

public class Wind {

    static double direction = 0;
    static double power = 0;

    public static void update() {
        if (power > 0) {
            double dvx = Math.cos(direction) * power;
            double dvy = Math.sin(direction) * power;

            for (Character character : Character.characters) {
                character.changeVx(dvx);
                character.changeVy(dvy);
            }

            if (counter > (lineLength + spiralLength) * 11) {
                counter = 0;
            }
            if (counter == 0) {
                calcSpiral();
                System.out.println(Arrays.toString(xList));
                System.out.println(Arrays.toString(yList));
                System.out.println("");
            }
        }
    }

    private static int counter = 0;
    private static final int lineLength = 20;
    private static final int spiralLength = 50;

    private static final int[] xList = new int[lineLength + spiralLength];
    private static final int[] yList = new int[lineLength + spiralLength];

    public static void paint(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawPolyline(xList, yList, Math.min(counter / 10, lineLength + spiralLength));
        counter++;
    }

    public static void calcSpiral() {
        int distance = (int) (10 * Wind.power);
        xList[0] = (int) (Math.random() * 800);
        yList[0] = (int) (Math.random() * 608);

        for (int i = 1; i < lineLength; i++) {
            xList[i] = (int) (xList[i - 1] + Math.cos(direction) * distance);
            yList[i] = (int) (yList[i - 1] + Math.sin(direction) * distance);
        }
        for (int i = lineLength; i < lineLength + spiralLength; i++) {
            double radius = distance / 2;
            xList[i] = (int) (xList[i - 1] + Math.cos(i / Math.sqrt(lineLength + spiralLength - i)) * radius);
            yList[i] = (int) (yList[i - 1] + Math.sin(i / Math.sqrt(lineLength + spiralLength - i)) * radius);
        }
    }
}
