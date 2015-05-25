package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
            addButton((int)Math.pow(2, i));
        }
    }

    private void addButton(int i) {
        BufferedImage img = new BufferedImage(30,30,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 30, 30);
        
        
        
        JButton button = new JButton(new ImageIcon(img));
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        add(button);
    }

}
