package ludumdare32mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import static ludumdare32mapeditor.MapPanel.camera;

public class ToolPanel extends JPanel {

    final static int PENCIL = 0;
    final static int BRUSH = 1;
    final static int BUCKET = 2;
    final static int PICKTILE = 3;
    final static int RECTANGLE = 4;
    
    static Tile tile = new Tile();
    final static int amountOfButtons = 5;
    JButton[] buttons = new JButton[amountOfButtons];
    int[] imageIDs = new int[amountOfButtons];
    
    static int currentTool = 0;
    
    Point lastPoint = new Point(0,0);
    
    TileSet toolTiles = new TileSet("img/Art-Icons-Transparent.png", 64, 23, 34, 77, 74);
    TileSet selectedToolTiles = new TileSet("img/Art-Icons-Blue-Transparent.png", 64, 23, 34, 77, 74);

    public ToolPanel() {
        setPreferredSize(new Dimension(80, 608));
        toolTiles.changeTileSize(30);
        selectedToolTiles.changeTileSize(28);
        
        addToolButtons();
        addLabels();
    }

    private void addToolButtons() {
        addButton(1); //Pen
        addButton(2); //Brush
        addButton(14); //Fill
        addButton(16); //Pipett
        addButton(46); //Resize
        changeTool(0);
    }

    int buttonsIterator = 0;

    private void addButton(int imageID) {
        JButton button = new JButton(new ImageIcon(toolTiles.images[imageID]));
        button.setFocusPainted(false);
        final int ID = buttonsIterator;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTool(ID);
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        add(button);
        buttons[buttonsIterator] = button;
        imageIDs[buttonsIterator] = imageID;
        buttonsIterator++;
    }

    private void changeTool(int toolID) {
        currentTool = toolID;
        for (int i = 0; i < amountOfButtons; i++) {
            buttons[i].setIcon(new ImageIcon(toolTiles.images[imageIDs[i]]));
        }
        buttons[toolID].setIcon(new ImageIcon(selectedToolTiles.images[imageIDs[toolID]]));
    }

    public void addLabels() {

    }
    
    public void mousePressedTool(MouseEvent m) {
        int button = m.getButton();
        switch (currentTool) {
            case 0:
                MapEditor.mapPanel.changeTile(m.getPoint());
                break;
            case 1:
                break;
            case 2:
                break;
            case 3: 
                break;
            case 4: 
                break;
        }
    }
    
    public void mouseDraggedTool(MouseEvent m) {
        int button = m.getButton();
        switch (currentTool) {
            case 0:
                lastPoint = m.getPoint();
                MapEditor.mapPanel.changeTile(m.getPoint());
                break;
            case 1:
                break;
            case 2:
                break;
            case 3: 
                break;
            case 4: 
                break;
        }
    }
    
    public void mouseMovedTool(MouseEvent m) {
        int button = m.getButton();
        switch (currentTool) {
            case 0:
                lastPoint = m.getPoint();
                MapEditor.mapPanel.repaint();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3: 
                break;
            case 4: 
                break;
        }
    }

    public void mouseReleasedTool(MouseEvent m) {
        int button = m.getButton();
        switch (currentTool) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3: 
                break;
            case 4: 
                break;
        }
    }
    
    public void paintTool(Graphics2D g) {
        switch (currentTool) {
            case 0:
                Point.Double worldPoint = camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
                //g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
                g.setColor(Color.DARK_GRAY);
                g.drawRect((int) (worldPoint.x / 16) * 16, (int) (worldPoint.y / 16) * 16, 16, 16);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3: 
                break;
            case 4: 
                break;
        }
    }
}
