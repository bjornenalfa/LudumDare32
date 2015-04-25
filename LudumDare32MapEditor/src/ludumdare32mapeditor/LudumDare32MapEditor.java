package ludumdare32mapeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import static ludumdare32mapeditor.World.textureMap;
import static ludumdare32mapeditor.World.textureMap2;
import static ludumdare32mapeditor.World.textureMap3;

public class LudumDare32MapEditor extends JFrame {

    boolean mouseDown = false;
    boolean mouseDown2 = false;
    Point lastPoint;
    Point lastPoint2;

    MyPanel mapPanel;
    MyTilePanel tilePanel;
    JLabel label = new JLabel("");

    static Camera camera;
    static Camera camera2;

    public LudumDare32MapEditor() {
        Tile.loadTileSet("img/Spritesheet/cloudy.png", 16, 1);
        World.loadFromFile("levels/test3.png");

        setTitle("LudumDare32 Map Editor");

        camera = new Camera(800, 608);
        camera2 = new Camera(800, 608);

        MyCheckBoxPanel checkBoxPanel = new MyCheckBoxPanel();
        JButton button = new JButton("SAVE");
        button.addActionListener(save());

        JPanel inPanel = new JPanel(new BorderLayout());
        inPanel.setPreferredSize(new Dimension(1600, 20));
        inPanel.add(button, BorderLayout.WEST);
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

        setContentPane(panel);
        getContentPane().setPreferredSize(new Dimension(1600, 608));
        setResizable(false);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Action save() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("saving");
                String s = JOptionPane.showInputDialog(mapPanel, "", "Save as", JOptionPane.QUESTION_MESSAGE);
                String path = "levels/" + s + ".png";
                BufferedImage img = new BufferedImage(World.width, World.height, BufferedImage.TYPE_INT_ARGB);
                //String string = getClass().getResource(path).getPath();
                //System.out.println(string);
                File file = new File(path);
                try {
                    ImageIO.write(img, "png", file);
                } catch (IOException ex) {
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
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, World.width * 32, World.height * 32);
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
                    camera.setScale(0.5);
                    repaint();
                }
            };
        }

        private Action one() {
            return new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    camera.setScale(1);
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

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int scrolls = e.getWheelRotation(); //negative if scroll upwards
                    camera.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (button == 1 && Objects.nonNull(first) && Objects.nonNull(second)) {
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        int x = (int) (p.getX() - p.getX() % 16) / 16;
                        int y = (int) (p.getY() - p.getY() % 16) / 16;
                        textureMap[x][y] = 4095 & (argbBackwards >> 12);
                        if (renderAbove == 1) {
                            textureMap3[x][y] = 4095 & argbBackwards;
                            textureMap2[x][y] = Tile.INVISIBLE;
                        } else {
                            textureMap2[x][y] = 4095 & argbBackwards;
                            textureMap3[x][y] = Tile.INVISIBLE;
                        }
                        World.renderMap();
                        World.changeImg(x, y, argbBackwards);
                        repaint();
                    }
                    mouseDown = false;
                }

                @Override
                public void mouseDragged(MouseEvent me) {
                    if (mouseDown && button == 3) {
                        Point newPoint = me.getPoint();
                        camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                        lastPoint = newPoint;
                        repaint();
                    } else if (button == 1 && Objects.nonNull(first) && Objects.nonNull(second)) {
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        int x = (int) (p.getX() - p.getX() % 16) / 16;
                        int y = (int) (p.getY() - p.getY() % 16) / 16;
                        textureMap[x][y] = 4095 & (argbBackwards >> 12);
                        if (renderAbove == 1) {
                            textureMap3[x][y] = 4095 & argbBackwards;
                            textureMap2[x][y] = Tile.INVISIBLE;
                        } else {
                            textureMap2[x][y] = 4095 & argbBackwards;
                            textureMap3[x][y] = Tile.INVISIBLE;
                        }
                        World.renderMap();
                        World.changeImg(x, y, argbBackwards);
                        repaint();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent me) {
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

    Point first;
    Point second;

    class MyTilePanel extends JPanel {

        public MyTilePanel() {
            addMouseListener(mouseAdapter());
            addMouseMotionListener(mouseAdapter());
            addMouseWheelListener(mouseAdapter());
        }

        public void calcColor() {
            if (Objects.nonNull(first) && Objects.nonNull(second)) {
                argbBackwards = (cloudy << 31) | (rainy << 30) | (sunny << 29) | (snowy << 28) | (renderAbove << 25) | (first.x / 32 + first.y / 32 * 57 << 12) | second.x / 32 + second.y / 32 * 57;
                int alpha = 0xFF & (argbBackwards >> 24);
                int red = 0xFF & (argbBackwards >> 16);
                int green = 0xFF & (argbBackwards >> 8);
                int blue = 0xFF & (argbBackwards);
                label.setText(" - Red:" + red + ",Green:" + green + ",Blue:" + blue + ",Alpha:" + alpha);
                StringSelection selection = new StringSelection(Integer.toHexString((first.x / 32 + first.y / 32 * 57 << 12) | second.x / 32 + second.y / 32 * 57));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }

        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1824, 992);

            camera2.transformGraphics(g);
            for (int y = 0; y < Tile.verticalTiles; y++) {
                for (int x = 0; x < Tile.horizontalTiles; x++) {
                    g.drawImage(Tile.images[y * Tile.horizontalTiles + x], x * 32, y * 32, 32, 32, null);
                }
            }
            if (Objects.nonNull(first)) {
                g.setColor(Color.YELLOW);
                g.drawRect(first.x, first.y, 32, 32);
            }
            if (Objects.nonNull(second)) {
                g.setColor(Color.MAGENTA);
                g.drawRect(second.x, second.y, 32, 32);
                if (Objects.equals(first, second)) {
                    g.setColor(Color.GREEN);
                    g.drawRect(first.x, first.y, 32, 32);
                }
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
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (button == 1) {
                        if (bFirst) {
                            if (Objects.isNull(first)) {
                                first = new Point();
                            }
                            Point.Double p = camera2.windowToWorldCoordinates(me.getX(), me.getY());
                            first.x = (int) (p.x - p.x % 32);
                            first.y = (int) (p.y - p.y % 32);
                            bFirst = false;
                        } else {
                            if (Objects.isNull(second)) {
                                second = new Point();
                            }
                            Point.Double p = camera2.windowToWorldCoordinates(me.getX(), me.getY());
                            second.x = (int) (p.x - p.x % 32);
                            second.y = (int) (p.y - p.y % 32);
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
                        lastPoint2 = newPoint;
                        repaint();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent me) {
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
