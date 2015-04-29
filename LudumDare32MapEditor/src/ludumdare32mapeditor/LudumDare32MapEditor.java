package ludumdare32mapeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LudumDare32MapEditor extends JFrame {

    boolean cloudyCollision = false;
    boolean sunnyCollision = false;
    boolean rainyCollision = false;
    boolean snowyCollision = false;
    boolean renderAboveCharacters = false;
    int tile1 = 0;
    int tile2 = 0;

    boolean mouseDown = false;
    boolean mouseDown2 = false;
    Point lastPoint;
    Point lastPoint2;

    static TileSet tileSet = new TileSet("img/Spritesheet/cloudy.png", 16, 1);

    MyPanel mapPanel;
    MyTilePanel tilePanel;
    JLabel label = new JLabel("");

    static Camera camera;
    static Camera camera2;

    public LudumDare32MapEditor() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LudumDare32MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        World.setTileSet(tileSet);
        World.loadFromFile("levels/test.jpg");

        setTitle("LudumDare32 Map Editor");

        camera = new Camera(800, 608);
        camera2 = new Camera(800, 608);

        MyCheckBoxPanel checkBoxPanel = new MyCheckBoxPanel();
        JButton button1 = new JButton("SAVE");
        button1.addActionListener(save());
        JButton button2 = new JButton("LOAD");
        button2.addActionListener(load());

        JPanel inPanel = new JPanel(new BorderLayout());
        inPanel.setPreferredSize(new Dimension(1600, 20));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        inPanel.add(buttonPanel, BorderLayout.WEST);
        inPanel.add(label, BorderLayout.CENTER);
        inPanel.add(checkBoxPanel, BorderLayout.EAST);

        mapPanel = new MyPanel();
        mapPanel.setFocusable(true);

        tilePanel = new MyTilePanel();
        tilePanel.setFocusable(true);

        JPanel anotherPanel = new JPanel(new GridLayout(1, 2));
        anotherPanel.add(mapPanel);
        anotherPanel.add(tilePanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(anotherPanel, BorderLayout.CENTER);
        panel.add(inPanel, BorderLayout.NORTH);

        panel.addComponentListener(componentAdapter());

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(1600, 608));
        setResizable(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private ComponentAdapter componentAdapter() {
        return new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component component = e.getComponent();
                int h = component.getHeight();
                int w = component.getWidth();

                mapPanel.setBounds(0, 0, w / 2, h);
                tilePanel.setBounds(w / 2, 0, w / 2, h);
                camera.changeSize(w / 2, h);
                camera2.changeSize(w / 2, h);
            }
        };
    }

    private Action save() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG", "png");
                FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG", "jpeg");
                chooser.setFileFilter(jpegFilter);
                chooser.setFileFilter(pngFilter);
                chooser.setCurrentDirectory(new File(".//..//src//ludumdare32/levels//"));
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String saveFormat = "";
                    File file = chooser.getSelectedFile();
                    if (chooser.getFileFilter() == pngFilter) {
                        if (!file.getAbsolutePath().toLowerCase().endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }
                        saveFormat = "png";
                    } else if (chooser.getFileFilter() == jpegFilter) {
                        if (!file.getAbsolutePath().toLowerCase().endsWith(".jpeg")) {
                            file = new File(file.getAbsolutePath() + ".jpeg");
                        }
                        saveFormat = "jpeg";
                    }

                    try {
                        ImageIO.write(World.getImage(), saveFormat, file);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    System.out.println("Saving: " + file);
                }
            }
        };
    }

    private Action load() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(".//..//src//ludumdare32/levels//"));
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        BufferedImage img;
                        img= ImageIO.read(file);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                    System.out.println("Loading: " + file);
                }
            }
        };
    }

    class MyPanel extends JPanel {

        public MyPanel() {
            addKeyBindings();

            MouseAdapter ma = mouseAdapter();
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            camera.clearScreen(g, Color.BLACK);
            camera.transformGraphics(g);

            World.paint(g);
            World.paint2(g);

//            if (mouseDown) {
//                Point.Double worldPoint = camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
//                g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
//            }
            camera.resetTransform(g);
        }

        private void addKeyBindings() {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
            getActionMap().put("exit", exit());

            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("1"), "half");
            getActionMap().put("half", half());

            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("2"), "one");
            getActionMap().put("one", one());

            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("3"), "two");
            getActionMap().put("two", two());
        }

        private Action exit() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
        }

        private Action half() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tileSet = new TileSet("img/Spritesheet/sunny.png", 16, 1);
                    World.changeTileSet(tileSet);
                    repaint();
                }
            };
        }

        private Action one() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            };
        }

        private Action two() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    camera.setScale(2);
                    repaint();
                }
            };
        }

        int button;

        public void changeTile(Point screenPoint) {
            Point.Double p = camera.windowToWorldCoordinates(screenPoint.x, screenPoint.y);
            //int x = (int) (screenPoint.x) / World.squareSize;
            //int y = (int) (screenPoint.y) / World.squareSize;
            World.changeTile((int) (p.x / World.squareSize), (int) (p.y / World.squareSize), tile1, tile2, cloudyCollision, sunnyCollision, rainyCollision, snowyCollision, renderAboveCharacters);
            //World.changeTile(x, y, tile1, tile2, cloudyCollision, sunnyCollision, rainyCollision, snowyCollision, renderAboveCharacters);
            repaint();
        }

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int scrolls = e.getWheelRotation(); //negative if scroll upwards
                    camera.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                    camera.constrainToWorld();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (button == 1) {
                        changeTile(me.getPoint());
                    }
                    mouseDown = false;
                }

                @Override
                public void mouseDragged(MouseEvent me) {
                    if (mouseDown && button == 3) {
                        Point newPoint = me.getPoint();
                        camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                        camera.constrainToWorld();
                        lastPoint = newPoint;
                        repaint();
                    } else if (button == 1) {
                        changeTile(me.getPoint());
                    }
                }

                @Override
                public void mouseMoved(MouseEvent me) {
                    requestFocus();
                }

                @Override
                public void mousePressed(MouseEvent me) {
                    if (!mouseDown) {
                        button = me.getButton();
                        lastPoint = me.getPoint();
                        mouseDown = true;
                    }
                }
            };
        }

    }

    class MyCheckBoxPanel extends JPanel implements ItemListener {

        JCheckBox cloudyBox;
        JCheckBox rainyBox;
        JCheckBox sunnyBox;
        JCheckBox snowyBox;
        JCheckBox aboveBox;

        public MyCheckBoxPanel() {
            setLayout(new GridLayout(1, 4));
            cloudyBox = new JCheckBox("CloudyCollision", false);
            rainyBox = new JCheckBox("RainyCollision", false);
            sunnyBox = new JCheckBox("SunnyCollision", false);
            snowyBox = new JCheckBox("SnowyCollision", false);
            aboveBox = new JCheckBox("RenderAboveCharacters", false);

            cloudyBox.addItemListener(this);
            rainyBox.addItemListener(this);
            sunnyBox.addItemListener(this);
            snowyBox.addItemListener(this);
            aboveBox.addItemListener(this);

            add(cloudyBox);
            add(rainyBox);
            add(sunnyBox);
            add(snowyBox);
            add(aboveBox);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();

            if (source == cloudyBox) {
                cloudy = 2 - e.getStateChange();
            } else if (source == rainyBox) {
                rainy = 2 - e.getStateChange();
            } else if (source == sunnyBox) {
                sunny = 2 - e.getStateChange();
            } else if (source == snowyBox) {
                snowy = 2 - e.getStateChange();
            } else if (source == aboveBox) {
                renderAbove = 2 - e.getStateChange();
            }
            tilePanel.calcColor();
        }
    }

    int cloudy = 0;
    int rainy = 0;
    int sunny = 0;
    int snowy = 0;
    int renderAbove = 0;

    int argbBackwards = 0;

    Point first = new Point(0, 0);
    Point second = new Point(0, 0);

    class MyTilePanel extends JPanel {

        public MyTilePanel() {
            addMouseListener(mouseAdapter());
            addMouseMotionListener(mouseAdapter());
            addMouseWheelListener(mouseAdapter());
            calcColor();
        }

        public final void calcColor() {
//            argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (renderAbove << 25) | (first.x / World.squareSize + first.y / World.squareSize * tileSet.horizontalTiles << 12) | second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles;
//            int alpha = 0xFF & (argbBackwards >> 24);
//            int red = 0xFF & (argbBackwards >> 16);
//            int green = 0xFF & (argbBackwards >> 8);
//            int blue = 0xFF & (argbBackwards);
//            label.setText(" - Red:" + red + ",Green:" + green + ",Blue:" + blue + ",Alpha:" + alpha);
//                StringSelection selection = new StringSelection(Integer.toHexString((first.x / World.squareSize + first.y / World.squareSize * tileSet.horizontalTiles << 12) | second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles));
//                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                clipboard.setContents(selection, selection);
            cloudyCollision = cloudy == 1;
            sunnyCollision = sunny == 1;
            rainyCollision = rainy == 1;
            snowyCollision = snowy == 1;
            renderAboveCharacters = renderAbove == 1;
            tile1 = first.x / World.squareSize + first.y / World.squareSize * tileSet.horizontalTiles;
            tile2 = second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles;
        }

        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            g.setColor(Color.BLACK);
            //g.fillRect(0, 0, 1824, 992);
            camera2.clearScreen(g, Color.BLACK);

            camera2.transformGraphics(g);
            for (int y = 0; y < tileSet.verticalTiles; y++) {
                for (int x = 0; x < tileSet.horizontalTiles; x++) {
                    g.drawImage(tileSet.images[y * tileSet.horizontalTiles + x], x * World.squareSize, y * World.squareSize, World.squareSize, World.squareSize, null);
                }
            }
            g.setColor(Color.YELLOW);
            g.drawRect(first.x, first.y, World.squareSize, World.squareSize);
            g.setColor(Color.MAGENTA);
            g.drawRect(second.x, second.y, World.squareSize, World.squareSize);
            if (Objects.equals(first, second)) {
                g.setColor(Color.GREEN);
                g.drawRect(first.x, first.y, World.squareSize, World.squareSize);
            }
            camera2.resetTransform(g);
        }

        boolean bFirst = true;
        int button;

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int scrolls = e.getWheelRotation(); //negative if scroll upwards
                    camera2.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                    camera2.constrainToTileSet();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (button == 1) {
                        if (bFirst) {
                            first = new Point();
                            Point.Double p = camera2.windowToWorldCoordinates(me.getX(), me.getY());
                            first.x = (int) (p.x - p.x % World.squareSize);
                            first.y = (int) (p.y - p.y % World.squareSize);
                            bFirst = false;
                        } else {
                            second = new Point();
                            Point.Double p = camera2.windowToWorldCoordinates(me.getX(), me.getY());
                            second.x = (int) (p.x - p.x % World.squareSize);
                            second.y = (int) (p.y - p.y % World.squareSize);
                            bFirst = true;
                        }
                        calcColor();
                        repaint();
                    }
                    mouseDown2 = false;
                }

                @Override
                public void mouseDragged(MouseEvent me) {
                    if (mouseDown2 && button == 3) {
                        Point newPoint = me.getPoint();
                        camera2.moveWindowPixels(lastPoint2.x - newPoint.x, lastPoint2.y - newPoint.y);
                        camera2.constrainToTileSet();
                        lastPoint2 = newPoint;
                        repaint();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent me) {
                    requestFocus();
                }

                @Override
                public void mousePressed(MouseEvent me) {
                    if (!mouseDown2) {
                        button = me.getButton();
                        lastPoint2 = me.getPoint();
                        mouseDown2 = true;
                    }
                }
            };
        }
    }

    public static void main(String[] args) {
        new LudumDare32MapEditor();
    }
}
