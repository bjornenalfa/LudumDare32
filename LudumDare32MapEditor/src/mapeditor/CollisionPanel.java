package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CollisionPanel extends JPanel {

    public CollisionPanel() {
        addButtons();
    }

    private void addButtons() {
        for (int i = 0; i < 8; i++) {
            addButton(i + 1);
        }
    }

    int squareSize = 20;
    int glowRadius = 5;

    private void addButton(int i) {
        int width = squareSize+2*glowRadius;
        int rsq = glowRadius*glowRadius;
        
        BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, width);

        for (int x = 0; x < (width); x++) {
            for (int y = 0; y < (width); y++) {
                double cx = x - glowRadius;
                double cy = y - glowRadius;
                if (i == 1) {// BIT 1 = BASE SQUARE COLLISION
                    cx = Math.max(0, Math.min(squareSize-1, cx));
                    cy = Math.max(0, Math.min(squareSize-1, cy));
                }
                if (i == 2) {// BIT 2 = TOP LEFT NO COLLISION
                    if (cx + cy < squareSize) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - squareSize;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + squareSize;
                    }
                }
                if (i == 3) {// BIT 3 = TOP RIGHT NO COLLISION
                    if ((squareSize - cx) + cy < squareSize) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if (i == 4) {// BIT 4 = BOTTOM LEFT NO COLLISION
                    if (cx + (squareSize - cy) < squareSize) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if (i == 5) {// BIT 5 = BOTTOM RIGHT NO COLLISION
                    if ((squareSize - cx) + (squareSize - cy) < squareSize) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - squareSize;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + squareSize;
                    }
                }
                if (i == 6) {// BIT 6 = LEFT EDGE NO COLLISION
                    cx = Math.max(0, Math.min(width, cx));
                    if (cx < 1) {
                        Point.Double collisionVector = new Point.Double(x - cx - glowRadius, y - cy - glowRadius);
                        double sqDist = collisionVector.x * collisionVector.x + collisionVector.y * collisionVector.y;
                        if (sqDist < rsq) {
                            if (sqDist == 0) {
                                g.setColor(new Color(255, 255, 255, 64));
                            } else {
                                //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                                int k = (int) (255 * (sqDist / (rsq)));
                                g.setColor(new Color(255, 255-k, 255-k, 255 - k));
                            }
                            g.fillRect(x, y, 1, 1);
                        }
                        cx = 100000;
                    }
                }
                if (i == 7) {// BIT 7 = RIGHT EDGE NO COLLISION
                    cx = Math.max(-glowRadius, Math.min(squareSize-1, cx));
                    if (cx > squareSize-2) {
                        Point.Double collisionVector = new Point.Double(x - cx - glowRadius, y - cy - glowRadius);
                        double sqDist = collisionVector.x * collisionVector.x + collisionVector.y * collisionVector.y;
                        if (sqDist < rsq) {
                            if (sqDist == 0) {
                                g.setColor(new Color(255, 255, 255, 64));
                            } else {
                                //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                                int k = (int) (255 * (sqDist / (rsq)));
                                g.setColor(new Color(255, 255-k, 255-k, 255 - k));
                            }
                            g.fillRect(x, y, 1, 1);
                        }
                        cx = 100000;
                    }
                }
                if (i == 8) {// BIT 8 = TOP EDGE NO COLLISION
                    cy = Math.max(0, Math.min(width, cy));
                    if (cy < 1) {
                        Point.Double collisionVector = new Point.Double(x - cx - glowRadius, y - cy - glowRadius);
                        double sqDist = collisionVector.x * collisionVector.x + collisionVector.y * collisionVector.y;
                        if (sqDist < rsq) {
                            if (sqDist == 0) {
                                g.setColor(new Color(255, 255, 255, 64));
                            } else {
                                //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                                int k = (int) (255 * (sqDist / (rsq)));
                                g.setColor(new Color(255, 255-k, 255-k, 255 - k));
                            }
                            g.fillRect(x, y, 1, 1);
                        }
                        cy = 100000;
                    }
                }
                Point.Double collisionVector = new Point.Double(x - cx - glowRadius, y - cy - glowRadius);
                double sqDist = collisionVector.x * collisionVector.x + collisionVector.y * collisionVector.y;
                if (sqDist < rsq) {
                    if (sqDist == 0) {
                        g.setColor(new Color(255, 255, 255, 64));
                    } else {
                        //int k = (int) (255*(Math.sqrt(sqDist)/(16)));
                        int k = (int) (255 * (sqDist / (rsq)));
                        g.setColor(new Color(255-k, 255, 255, 255 - k));
                    }
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        JButton button = new JButton(new ImageIcon(img));
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        button.setPreferredSize(new Dimension(width, width));
        add(button);
    }

}
