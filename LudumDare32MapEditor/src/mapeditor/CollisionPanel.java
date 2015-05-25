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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CollisionPanel extends JPanel {
    
    static final int amountOfButtons = 8;

    JButton[] buttons = new JButton[amountOfButtons];
    boolean[] selected = new boolean[amountOfButtons];
    byte result = 0;
    JLabel preview = new JLabel();
    
    WeatherPanel weatherPanel = new WeatherPanel();

    public CollisionPanel() {
        addButtons();
        add(preview);
        updatePreview();
        add(weatherPanel);
        setPreferredSize(new Dimension(80, 608));
    }

    private void addButtons() {
        for (int i = 0; i < amountOfButtons; i++) {
            addButton(i);
        }
    }

    int squareSize = 20;
    int glowRadius = 5;

    private void addButton(int i) {
        JButton button = new JButton(new ImageIcon(getImage(1<<i,squareSize,glowRadius,false)));
        button.setFocusPainted(false);
        final int ID = i;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressed(ID);
            }
        });
        buttons[i] = button;
        button.setPreferredSize(new Dimension(squareSize+2*glowRadius, squareSize+2*glowRadius));
        add(button);
    }

    private void pressed(int ID) {
        selected[ID] = !selected[ID];
        buttons[ID].setIcon(new ImageIcon(getImage(1<<ID,squareSize,glowRadius,selected[ID])));
        updatePreview();
    }

    public void updatePreview() {
        result = 0;
        for (int i = 0;i<selected.length;i++) {
            if (selected[i]) {
                result += 1<<i;
            }
        }
        preview.setIcon(new ImageIcon(getImage(result,45,10,false)));
    }
    
    public BufferedImage getImage(int i, int squareSize, int glowRadius, boolean selected) {
        int width = squareSize+2*glowRadius;
        int rsq = glowRadius*glowRadius;
        
        BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, width);
        if (selected) {
            g.setColor(new Color(70,255,70,70));
            g.fillRect(0, 0, width, width);
        }

        for (int x = 0; x < (width); x++) {
            for (int y = 0; y < (width); y++) {
                double cx = x - glowRadius;
                double cy = y - glowRadius;
                if ((i & 1) == 1) {// BIT 1 = BASE SQUARE COLLISION
                    cx = Math.max(0, Math.min(squareSize-1, cx));
                    cy = Math.max(0, Math.min(squareSize-1, cy));
                }
                if ((i & 2) == 2) {// BIT 2 = TOP LEFT NO COLLISION
                    if (cx + cy < squareSize) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - squareSize;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + squareSize;
                    }
                }
                if ((i & 4) == 4) {// BIT 3 = TOP RIGHT NO COLLISION
                    if ((squareSize - cx) + cy < squareSize) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if ((i & 8) == 8) {// BIT 4 = BOTTOM LEFT NO COLLISION
                    if (cx + (squareSize - cy) < squareSize) {
                        // Ortogonal projektion av vektor från (0,0) till c på normaliserad vektor (1,1)
                        double scalar = (cx / Math.sqrt(2) + cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,1)
                        cy = scalar / Math.sqrt(2);
                    }
                }
                if ((i & 16) == 16) {// BIT 5 = BOTTOM RIGHT NO COLLISION
                    if ((squareSize - cx) + (squareSize - cy) < squareSize) {
                        // Ortogonal projektion av vektor från (0,32) till c på normaliserad vektor (1,-1)
                        cy = cy - squareSize;
                        double scalar = (cx / Math.sqrt(2) - cy / Math.sqrt(2)); // Skalär produkt mellan c och normaliserad vektor (1,-1)
                        cx = scalar / Math.sqrt(2); // c = length * normaliserad vektor (1,-1)
                        cy = -scalar / Math.sqrt(2);
                        cy = cy + squareSize;
                    }
                }
                if ((i & 32) == 32) {// BIT 6 = LEFT EDGE NO COLLISION
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
                if ((i & 64) == 64) {// BIT 7 = RIGHT EDGE NO COLLISION
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
                if ((i & 128) == 128) {// BIT 8 = TOP EDGE NO COLLISION
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
        return img;
    }
}
