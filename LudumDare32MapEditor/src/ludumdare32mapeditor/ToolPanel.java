package ludumdare32mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

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

    Point lastPoint = new Point(0, 0);

    int layer = 1;

    TileSet toolTiles = new TileSet("img/Art-Icons-Transparent.png", 64, 23, 34, 77, 74);
    TileSet selectedToolTiles = new TileSet("img/Art-Icons-Blue-Transparent.png", 64, 23, 34, 77, 74);

    public ToolPanel() {
        addKeyBindings();

        MouseAdapter ma = mouseAdapter();
        addMouseMotionListener(ma);

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
    
    public void changeTileFromWindowCoordinates(Point p) {
        MapEditor.changeTileFromWindowCoordinates(p, tile, layer);
    }
    
    public void changeTileFromGridCoordinates(Point p) {
        MapEditor.changeTileFromWindowCoordinates(p, tile, layer);
    }

    public void mousePressedTool(MouseEvent m, int button) {
        switch (currentTool) {
            case 0:
                changeTileFromWindowCoordinates(m.getPoint());
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                tile = MapEditor.getTileFromWindowCoordinates(m.getPoint());
                break;
            case 4:
                break;
        }
    }

    public void mouseDraggedTool(MouseEvent m, int button) {
        lastPoint = m.getPoint();
        if (button == 1) {
            switch (currentTool) {
                case 0:
                    changeTileFromWindowCoordinates(m.getPoint());
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    MapEditor.getTileFromWindowCoordinates(m.getPoint());
                    break;
                case 4:
                    break;
            }
        }
    }

    public void mouseMovedTool(MouseEvent m, int button) {
        lastPoint = m.getPoint();
        switch (currentTool) {
            case 0:
                MapEditor.mapPanel.repaint();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                MapEditor.mapPanel.repaint();
                break;
            case 4:
                break;
        }
    }

    public void mouseReleasedTool(MouseEvent m, int button) {
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
                World world = MapEditor.mapPanel.getWorldFromWindowCoordinates(lastPoint);
                if (world != null) {
                    Point.Double worldPoint = MapPanel.camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
                    //g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect((int) ((worldPoint.x - (int) (world.xOffset) % 16) / 16) * 16 + (int) (world.xOffset) % 16, (int) ((worldPoint.y -  (int) (world.yOffset) % 16) / 16) * 16 + (int) (world.yOffset) % 16, 16, 16);
                }
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                world = MapEditor.mapPanel.getWorldFromWindowCoordinates(lastPoint);
                if (world != null) {
                    Point.Double worldPoint = MapPanel.camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
                    //g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect((int) ((worldPoint.x - (int) (world.xOffset) % 16) / 16) * 16 + (int) (world.xOffset) % 16, (int) ((worldPoint.y -  (int) (world.yOffset) % 16) / 16) * 16 + (int) (world.yOffset) % 16, 16, 16);
                }
                break;
            case 4:
                break;
        }
    }

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                requestFocus();
            }
        };
    }

    private void addKeyBindings() {
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("1"), "one");
        getActionMap().put("one", one());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("2"), "two");
        getActionMap().put("two", two());
        
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("3"), "three");
        getActionMap().put("three", three());
    }

    private Action one() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layer = 1;
                repaint();
            }
        };
    }

    private Action two() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layer = 2;
                repaint();
            }
        };
    }
    
    private Action three() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layer = 0;
                repaint();
            }
        };
    }
}
