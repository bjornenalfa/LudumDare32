package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static mapeditor.MapEditor.getSelectedWorld;

public class ToolPanel extends JPanel {

    final static int PENCIL = 0;
    final static int SCREEN_BRUSH = 1;
    final static int WORLD_BRUSH = 2;
    final static int FILL = 3;
    final static int PICKTILE = 4;
    final static int RESIZE = 5;
    final static int PASTE_IN = 6;

    static Tile tile = new Tile();
    static boolean[] collisions = new boolean[4];
    static int amountOfButtons;
    JButton[] buttons;
    int[] imageIDs;

    static int currentTool = 0;

    Point lastPoint = new Point(0, 0);

    int weather = 1;
    int layer = 1;
    boolean changingCollisions = false;

    TileSet toolTiles = new TileSet("img/Art-Icons-Transparent.png", 64, 23, 34, 77, 74);
    TileSet selectedToolTiles = new TileSet("img/Art-Icons-Blue-Transparent.png", 64, 23, 34, 77, 74);

    //BRUSH VARIABLES
    double cr = 2;
    double dr = 3.1;
    double r = 4.1;

    //SLIDER VARIABLES
    int sliderTarget = 0;

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
        amountOfButtons = 8;
        buttons = new JButton[amountOfButtons];
        imageIDs = new int[amountOfButtons];
        addButton(1); //Pen
        addButton(0); //Screen Constant Brush
        addButton(2); //World Constant Brush
        addButton(14); //Fill
        addButton(16); //Pipett
        addButton(46); //Resize
        addButton(10); //PasteIn
        addButton(32); //Something
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
        changeSliderTarget(toolID);
        for (int i = 0; i < amountOfButtons; i++) {
            buttons[i].setIcon(new ImageIcon(toolTiles.images[imageIDs[i]]));
        }
        buttons[toolID].setIcon(new ImageIcon(selectedToolTiles.images[imageIDs[toolID]]));
    }

    static JLabel texture1Label = new JLabel();
    static JLabel texture2Label = new JLabel();

    public void addLabels() {
        updateTileImage();
        add(texture1Label);
        add(texture2Label);
        add(new SliderPanel(1, 10));
    }

    public static void changeActiveTile(Tile t) {
        tile = t;
        updateTileImage();
    }

    public static void changeActiveTileTexture(int texture, int layer) {
        tile.layer = layer;
        if (layer == 1) {
            tile.texture1 = texture;
        }
        if (layer == 2) {
            tile.texture2 = texture;
        }
        updateTileImage();
    }

    public static void updateTileImage() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 32, 32);
        g.drawImage(MapEditor.tileSet.images[tile.texture1], 0, 0, 32, 32, null);
        texture1Label.setIcon(new ImageIcon(new BufferedImage(img.getColorModel(), img.copyData(null), img.isAlphaPremultiplied(), null)));
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 32, 32);
        g.drawImage(MapEditor.tileSet.images[tile.texture2], 0, 0, 32, 32, null);
        texture2Label.setIcon(new ImageIcon(img));
    }

    public void changeTileFromWindowCoordinates(Point p) {
        if (layer > 0) {
            MapEditor.changeTileFromWindowCoordinates(p, tile, layer);
        }
        if (changingCollisions) {
            MapEditor.changeCollisionsFromWindowCoordinates(p, collisions, weather);
        }
    }

    public void changeTileFromGridCoordinates(Point p) {
        if (layer > 0) {
            MapEditor.changeTileFromGridCoordinates(p, tile, layer);
        }
        if (changingCollisions) {
            MapEditor.changeCollisionsFromGridCoordinates(p, collisions, weather);
        }
    }

    public void mousePressedTool(MouseEvent m, int button) {
        switch (currentTool) {
            case PENCIL:
                changeTileFromWindowCoordinates(m.getPoint());
                break;
            case SCREEN_BRUSH:
                Point.Double worldPoint = MapPanel.camera.windowToWorldCoordinates(m.getPoint());
                Point gridPoint = getSelectedWorld().worldPointToGridPoint(worldPoint);
                for (int x = -(int) (r + .99) + 1; x < r; x++) {
                    for (int y = -(int) (r + .99) + 1; y < r; y++) {
                        if (x * x + y * y < r * r) {
                            changeTileFromGridCoordinates(new Point(gridPoint.x + x, gridPoint.y + y));
                        }
                    }
                }
                break;
            case WORLD_BRUSH:
                worldPoint = MapPanel.camera.windowToWorldCoordinates(m.getPoint());
                gridPoint = getSelectedWorld().worldPointToGridPoint(worldPoint);
                for (int x = -(int) (r + .99) + 1; x < r; x++) {
                    for (int y = -(int) (r + .99) + 1; y < r; y++) {
                        if (x * x + y * y < r * r) {
                            changeTileFromGridCoordinates(new Point(gridPoint.x + x, gridPoint.y + y));
                        }
                    }
                }
                break;
            case FILL:

                break;
            case PICKTILE:
                changeActiveTile(MapEditor.getTileFromWindowCoordinates(m.getPoint()));
                break;
        }
    }

    public void mouseDraggedTool(MouseEvent m, int button) {
        lastPoint = m.getPoint();
        if (button == 1) {
            switch (currentTool) {
                case PENCIL:
                    changeTileFromWindowCoordinates(m.getPoint());
                    break;
                case SCREEN_BRUSH:
                    Point.Double worldPoint = MapPanel.camera.windowToWorldCoordinates(m.getPoint());
                    Point gridPoint = getSelectedWorld().worldPointToGridPoint(worldPoint);
                    for (int x = -(int) (r + .99) + 1; x < r; x++) {
                        for (int y = -(int) (r + .99) + 1; y < r; y++) {
                            if (x * x + y * y < r * r) {
                                changeTileFromGridCoordinates(new Point(gridPoint.x + x, gridPoint.y + y));
                            }
                        }
                    }
                    break;
                case WORLD_BRUSH:
                    worldPoint = MapPanel.camera.windowToWorldCoordinates(m.getPoint());
                    gridPoint = getSelectedWorld().worldPointToGridPoint(worldPoint);
                    for (int x = -(int) (r + .99) + 1; x < r; x++) {
                        for (int y = -(int) (r + .99) + 1; y < r; y++) {
                            if (x * x + y * y < r * r) {
                                changeTileFromGridCoordinates(new Point(gridPoint.x + x, gridPoint.y + y));
                            }
                        }
                    }
                    break;
                case FILL:

                    break;
                case PICKTILE:
                    MapEditor.getTileFromWindowCoordinates(m.getPoint());
                    break;
            }
        }
    }

    public void mouseMovedTool(MouseEvent m, int button) {
        lastPoint = m.getPoint();
        switch (currentTool) {
            case PENCIL:
                MapEditor.mapPanel.repaint();
                break;
            case SCREEN_BRUSH:
                MapEditor.mapPanel.repaint();
                break;
            case WORLD_BRUSH:
                MapEditor.mapPanel.repaint();
                break;
            case FILL:

                break;
            case PICKTILE:
                MapEditor.mapPanel.repaint();
                break;
        }
    }

    public void mouseReleasedTool(MouseEvent m, int button) {
        switch (currentTool) {
            case PENCIL:
                break;
            case SCREEN_BRUSH:
                break;
            case WORLD_BRUSH:
                break;
            case FILL:
                break;
            case PICKTILE:
                break;
        }
    }

    public void paintTool(Graphics2D g) {
        World world = MapEditor.mapPanel.getWorldFromWindowCoordinates(lastPoint);
        if (world != null) {
            Point.Double worldPoint = MapPanel.camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
            Point gridPoint = world.worldPointToGridPoint(worldPoint);
            Point.Double worldPoint2 = world.gridPointToWorldPoint(gridPoint);
            switch (currentTool) {
                case PENCIL:
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect((int) worldPoint2.x, (int) worldPoint2.y, 16, 16);
                    break;
                case SCREEN_BRUSH:
                    r = cr / MapPanel.camera.scale;
                    for (int x = -(int) (r + .99) + 1; x < r; x++) {
                        for (int y = -(int) (r + .99) + 1; y < r; y++) {
                            if (x * x + y * y < r * r) {
                                g.setColor(Color.DARK_GRAY);
                                g.drawRect((int) worldPoint2.x + x * World.squareSize, (int) worldPoint2.y + y * World.squareSize, 16, 16);
                            }
                        }
                    }
                    break;
                case WORLD_BRUSH:
                    r = dr;
                    for (int x = -(int) (r + .99) + 1; x < r; x++) {
                        for (int y = -(int) (r + .99) + 1; y < r; y++) {
                            if (x * x + y * y < r * r) {
                                g.setColor(Color.DARK_GRAY);
                                g.drawRect((int) worldPoint2.x + x * World.squareSize, (int) worldPoint2.y + y * World.squareSize, 16, 16);
                            }
                        }
                    }
                    break;
                case FILL:
                    break;
                case PICKTILE:
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect((int) worldPoint2.x, (int) worldPoint2.y, 16, 16);
                    break;
            }
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

    JSlider slider;
    int sliderPrecision = 100;

    class SliderPanel extends JPanel implements ChangeListener {

        public SliderPanel(int lowestValue, int highestValue) {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            slider = new JSlider(SwingConstants.HORIZONTAL, lowestValue * sliderPrecision, highestValue * sliderPrecision, lowestValue * sliderPrecision);
            slider.setPreferredSize(new Dimension(75, 50));
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(1);
            slider.setPaintLabels(true);
            slider.addChangeListener(this);
            Hashtable labelTable = new Hashtable();
            labelTable.put(new Integer(lowestValue * sliderPrecision), new JLabel("" + lowestValue));
            labelTable.put(new Integer(highestValue * sliderPrecision), new JLabel("" + highestValue));
            slider.setLabelTable(labelTable);
            add(slider);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (source == slider) {
                switch (currentTool) {
                    case PENCIL:
                        break;
                    case SCREEN_BRUSH:
                        cr = source.getValue() / (double) sliderPrecision;
                        MapEditor.mapPanel.repaint();
                        break;
                    case WORLD_BRUSH:
                        dr = source.getValue() / (double) sliderPrecision;
                        MapEditor.mapPanel.repaint();
                        break;
                    case FILL:
                        break;
                    case PICKTILE:
                        break;
                }
            }

        }
    }

    public void setSliderValue(double value) {
        slider.setValue((int) (value * sliderPrecision));
    }

    private void changeSliderTarget(int toolID) {
        sliderTarget = toolID;
        switch (toolID) {
            case PENCIL:
                hideSlider();
                break;
            case SCREEN_BRUSH:
                showSlider();
                setSliderValue(cr);
                break;
            case WORLD_BRUSH:
                showSlider();
                setSliderValue(dr);
                break;
            case FILL:
                hideSlider();
                break;
            case PICKTILE:
                hideSlider();
                break;
        }
    }

    private void showSlider() {
        slider.setVisible(true);
    }

    private void hideSlider() {
        if (slider != null) {
            slider.setVisible(false);
        }
    }
}
