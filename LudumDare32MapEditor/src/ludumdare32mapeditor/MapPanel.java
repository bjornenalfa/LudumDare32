package ludumdare32mapeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.JComponent.WHEN_FOCUSED;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class MapPanel extends JPanel {

    World[] worlds;
    int selectedWorld;
    
    static Camera camera = new Camera(800, 608);
    Point lastPoint;
    boolean mouseDown = false;
    boolean draggingWorld = false;

    int showLayers = 3;

    public MapPanel(World[] worlds, int selectedWorld) {
        this.worlds = worlds;
        this.selectedWorld = selectedWorld;
        addKeyBindings();

        MouseAdapter ma = mouseAdapter();
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);

        addComponentListener(componentAdapterMap());
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        camera.clearScreen(g, Color.BLACK);
        camera.transformGraphics(g);

        if ((showLayers & 1) == 1) {
            for (int i = 0; i < worlds.length; i++) {
                worlds[i].paintLayer1(g);
            }
        }
        if (((showLayers >> 1) & 1) == 1) {
            for (int i = 0; i < worlds.length; i++) {
                worlds[i].paintLayer2(g);
            }
        }

        for (int i = 0; i < worlds.length; i++) {
            worlds[i].drawBorder(g, Color.WHITE);
        }
        worlds[selectedWorld].drawBorder(g, Color.RED);

//      if (mouseDown) {
//          Point.Double worldPoint = camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
//          g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
//      }
        camera.resetTransform(g);
    }

    private void addKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        getActionMap().put("exit", exit());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("1"), "one");
        getActionMap().put("one", one());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("2"), "two");
        getActionMap().put("two", two());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("3"), "three");
        getActionMap().put("three", three());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("4"), "four");
        getActionMap().put("four", four());
    }

    private Action exit() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }

    private Action one() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LudumDare32MapEditor.tileSet = new TileSet("img/Spritesheet/sunny.png", 16, 1);
                worlds[selectedWorld].changeTileSet(LudumDare32MapEditor.tileSet);
                repaint();
            }
        };
    }

    private Action two() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worlds[selectedWorld].expand(1, 1, 1, 1);
                repaint();
            }
        };
    }

    private Action three() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worlds[selectedWorld].contract(1, 1, 1, 1);
                repaint();
            }
        };
    }

    private Action four() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLayers = (showLayers) % 3 + 1;
                repaint();
            }
        };
    }

    int button;

    public void changeTile(Point screenPoint) {
        Point.Double p = camera.windowToWorldCoordinates(screenPoint.x, screenPoint.y);
        worlds[selectedWorld].changeTileWorldCoordinates((int) (p.x), (int) (p.y), ToolPanel.tile);
        repaint();
    }

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int scrolls = e.getWheelRotation(); //negative if scroll upwards
                camera.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                //camera.constrainToWorld(worlds[selectedWorld]);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (button == 1) {
                    changeTile(me.getPoint());
                }
                mouseDown = false;
                draggingWorld = false;
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                if (mouseDown && button == 3) {
                    Point newPoint = me.getPoint();
                    if (draggingWorld) {
                        Point.Double np = camera.windowToWorldCoordinates(newPoint);
                        Point.Double lp = camera.windowToWorldCoordinates(lastPoint);
                        worlds[selectedWorld].move(np.x - lp.x, np.y - lp.y);
                    } else {
                        camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                    }
                    //camera.constrainToWorld(worlds[selectedWorld]);
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
                    Point.Double p = camera.windowToWorldCoordinates(lastPoint);
                    for (int i = 0; i < worlds.length; i++) {
                        if (worlds[i].worldPointInWorld(p)) {
                            selectedWorld = i;
                            repaint();
                            if (button == 3) {
                                draggingWorld = true;
                            }
                            break;
                        }
                    }
                }
            }
        };
    }

    private ComponentAdapter componentAdapterMap() {
        return new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component component = e.getComponent();
                int h = component.getHeight();
                int w = component.getWidth();
                camera.changeSize(w, h);
                repaint();
            }
        };
    }
}
