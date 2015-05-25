package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CollisionPanel extends JPanel {

    JButton[] buttons = new JButton[8];
    boolean[] selected = new boolean[8];
    byte result = 0;
    JLabel preview = new JLabel();
    
    public CollisionPanel() {
        addButtons();
        add(preview);
        setPreferredSize(new Dimension(80, 608));
    }

    private void addButtons() {
        for (int i = 0; i < 8; i++) {
            addButton(i + 1);
        }
    }

    int s = 20;
    int r = 5;

    private void addButton(int i) {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, s + 2 * r, s + 2 * r);

        for (int x = 0; x < (s + 2 * r); x++) {
            for (int y = 0; y < (s + 2 * r); y++) {
                double cx = x - r;
                double cy = y - r;
                if (i == 1) {// BIT 1 = BASE SQUARE COLLISION
                    cx = Math.max(0, Math.min(31, cx));
                    cy = Math.max(0, Math.min(31, cy));
                }
                if (i == 2) {// BIT 2 = TOP LEFT NO COLLISION
                    if (cx + cy < 32) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - 32;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + 32;
                    }
                }
                if (i == 3) {// BIT 3 = TOP RIGHT NO COLLISION
                    if ((32 - cx) + cy < 32) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if (i == 4) {// BIT 4 = BOTTOM LEFT NO COLLISION
                    if (cx + (32 - cy) < 32) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if (i == 5) {// BIT 5 = BOTTOM RIGHT NO COLLISION
                    if ((32 - cx) + (32 - cy) < 32) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - 32;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + 32;
                    }
                }
                if (i == 6) {// BIT 6 = LEFT EDGE NO COLLISION
                    if (cx < 1) {
                        cx = 100000;
                    }
                }
                if (i == 7) {// BIT 7 = RIGHT EDGE NO COLLISION
                    if (cx > 30) {
                        cx = 100000;
                    }
                }
                if (i == 8) {// BIT 8 = TOP EDGE NO COLLISION
                    if (cy > 30) {
                        cy = 100000;
                    }
                }
                Point.Double collisionVector = new Point.Double(x - cx - r, y - cy - r);
                double sqDist = collisionVector.x * collisionVector.x + collisionVector.y * collisionVector.y;
                if (sqDist < 16 * 16) {
                    if (sqDist == 0) {
                        g.setColor(new Color(255, 255, 255, 32));
                    } else {
                        //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                        int k = (int) (255 * (sqDist / (16 * 16)));
                        g.setColor(new Color(255 - k, 255, 255, 255 - k));
                    }
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        JButton button = new JButton(new ImageIcon(img));
        button.setFocusPainted(false);
        final int ID = i;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if ((result & (1 << (ID-1))) == (1 << (ID-1))) {
//                    result -= Math.pow(2, ID-1);
//                } else {
//                    result += Math.pow(2, ID-1);
//                }
                pressed(ID);
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        buttons[i - 1] = button;
        add(button);
    }

    private void pressed(int ID) {
        selected[ID] = !selected[ID];
        
    }

    public static void updateTileImage() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 32, 32);
        preview.setIcon(new ImageIcon(img));
    }
}
