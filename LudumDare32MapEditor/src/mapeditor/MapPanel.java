package mapeditor;

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
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import static mapeditor.MapEditor.ctrlDown;

public class MapPanel extends JPanel {

    static Camera camera = new Camera(800, 608);
    Point lastPoint = new Point(0, 0);
    boolean mouseDown = false;
    boolean draggingWorld = false;

    int showLayers = 3;

    public MapPanel() {
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
            for (int i = 0; i < MapEditor.worlds.size(); i++) {
                MapEditor.worlds.get(i).paintLayer1(g);
            }
        }
        if ((showLayers & 2) == 2) {
            for (int i = 0; i < MapEditor.worlds.size(); i++) {
                MapEditor.worlds.get(i).paintLayer2(g);
            }
        }

        for (int i = 0; i < MapEditor.worlds.size(); i++) {
            MapEditor.worlds.get(i).drawBorder(g, Color.WHITE);
        }
        MapEditor.worlds.get(MapEditor.selectedWorld).drawBorder(g, Color.RED);

        MapEditor.toolPanel.paintTool(g);

//      if (mouseDown) {
//          Point.Double worldPoint = camera.windowToWorldCoordinates(lastPoint.x, lastPoint.y);
//          g.fillOval((int)(worldPoint.x-5), (int)(worldPoint.y-5), 10, 10);
//      }
        camera.resetTransform(g);
    }

    private void addKeyBindings() {
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("1"), "one");
        getActionMap().put("one", one());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("2"), "two");
        getActionMap().put("two", two());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("3"), "three");
        getActionMap().put("three", three());

        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("4"), "four");
        getActionMap().put("four", four());
    }

    private Action one() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MapEditor.tileSet = new TileSet(".//..//src//ludumdare32/img/Spritesheet/sunny.png", 16, 1);
                MapEditor.worlds.get(MapEditor.selectedWorld).changeTileSet(MapEditor.tileSet);
                repaint();
            }
        };
    }

    private Action two() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MapEditor.worlds.get(MapEditor.selectedWorld).expand(1, 1, 1, 1);
                repaint();
            }
        };
    }

    private Action three() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MapEditor.worlds.get(MapEditor.selectedWorld).contract(1, 1, 1, 1);
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

    public World getWorldFromWindowCoordinates(Point p) {
        //System.out.println("before x:"+p.x+" y:"+p.y);
        Point.Double worldCoordinates = camera.windowToWorldCoordinates(p);
        //System.out.println("after x:"+worldCoordinates.x+" y:"+worldCoordinates.y);
        for (int i = 0; i < MapEditor.worlds.size(); i++) {
            if (MapEditor.worlds.get(i).worldPointInWorld(worldCoordinates)) {
                return MapEditor.worlds.get(i);
            }
        }
        return null;
    }

    int button;

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int scrolls = e.getWheelRotation(); //negative if scroll upwards
                camera.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                //camera.constrainToWorld(LudumDare32MapEditor.worlds[LudumDare32MapEditor.selectedWorld]);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mouseDown = false;
                draggingWorld = false;
                MapEditor.toolPanel.mouseReleasedTool(me, button);
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                MapEditor.toolPanel.mouseDraggedTool(me, button);
                if (mouseDown && button == 3) {
                    Point newPoint = me.getPoint();
                    if (draggingWorld) {
                        Point.Double np = camera.windowToWorldCoordinates(newPoint);
                        Point.Double lp = camera.windowToWorldCoordinates(lastPoint);
                        MapEditor.worlds.get(MapEditor.selectedWorld).move(np.x - lp.x, np.y - lp.y);
                    } else {
                        camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                    }
                    //camera.constrainToWorld(LudumDare32MapEditor.worlds[LudumDare32MapEditor.selectedWorld]);
                    lastPoint = newPoint;
                    repaint();
                } else if (button == 1) {
                    //MapEditor.toolPanel.mouseDraggedTool(me, button);
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                requestFocus();
                MapEditor.toolPanel.mouseMovedTool(me, button);
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (!mouseDown) {
                    button = me.getButton();
                    lastPoint = me.getPoint();
                    mouseDown = true;
                    boolean changedSelectedWorld = false;
                    Point.Double p = camera.windowToWorldCoordinates(lastPoint);
                    for (int i = 0; i < MapEditor.worlds.size(); i++) {
                        if (MapEditor.worlds.get(i).worldPointInWorld(p)) {
                            if (MapEditor.selectedWorld != i) {
                                MapEditor.selectedWorld = i;
                                changedSelectedWorld = true;
                                button = button == 1 ? 4711 : button;
                            }
                            repaint();
                            if (button == 3 && ctrlDown) {
                                draggingWorld = true;
                            }
                            break;
                        }
                    }
                    if (button == 1 && !changedSelectedWorld) {
                        MapEditor.toolPanel.mousePressedTool(me, button);
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
