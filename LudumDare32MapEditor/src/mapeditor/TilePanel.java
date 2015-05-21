package mapeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Objects;
import javax.swing.JPanel;
import static mapeditor.MapEditor.ctrlDown;
import static mapeditor.MapEditor.tileSet;

class TilePanel extends JPanel {
    static Camera camera = new Camera(800, 608);
    boolean mouseDown = false;
    Point lastPoint = new Point(0, 0);

    Point first = new Point(0, 0);
    Point second = new Point(0, 0);

    public TilePanel() {
        MouseAdapter ma = mouseAdapter();
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);

        addComponentListener(componentAdapterTile());
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.BLACK);
        //g.fillRect(0, 0, 1824, 992);
        camera.clearScreen(g, Color.BLACK);

        camera.transformGraphics(g);
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

        Point.Double worldPoint = camera.windowToWorldCoordinates(lastPoint);
        worldPoint.x -= worldPoint.x % 16;
        worldPoint.y -= worldPoint.y % 16;
        g.setColor(Color.DARK_GRAY);
        g.drawRect((int) worldPoint.x, (int) worldPoint.y, World.squareSize, World.squareSize);
        camera.resetTransform(g);
    }

    boolean bFirst = true;
    int button;
    boolean movingCamera = false;

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int scrolls = e.getWheelRotation(); //negative if scroll upwards
                camera.zoomOnWindowPoint(Math.pow(1.1, -scrolls), e.getPoint());
                camera.constrainToTileSet();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (movingCamera) {
                    movingCamera = false;
                } else {
                    if (button == 1) {
                        first = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        first.x = (int) (p.x - p.x % World.squareSize);
                        first.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.changeActiveTileTexture(first.x / World.squareSize + first.y / World.squareSize * tileSet.horizontalTiles, 1);
                    } else if (button == 3) {
                        second = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        second.x = (int) (p.x - p.x % World.squareSize);
                        second.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.changeActiveTileTexture(second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles, 2);
                    }
                }
                repaint();
                mouseDown = false;
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                if (movingCamera) {
                    Point newPoint = me.getPoint();
                    camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                    camera.constrainToTileSet();
                } else {
                    if (button == 1) {
                        first = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        first.x = (int) (p.x - p.x % World.squareSize);
                        first.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.changeActiveTileTexture(first.x / World.squareSize + first.y / World.squareSize * tileSet.horizontalTiles, 1);
                    } else if (button == 3) {
                        second = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        second.x = (int) (p.x - p.x % World.squareSize);
                        second.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.changeActiveTileTexture(second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles, 2);
                    }
                }
                lastPoint = me.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                lastPoint = me.getPoint();
                repaint();
                requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (!mouseDown) {
                    button = me.getButton();
                    if (button == 3 && ctrlDown) {
                        movingCamera = true;
                    }
                    lastPoint = me.getPoint();
                    mouseDown = true;
                }
            }
        };
    }

    private ComponentAdapter componentAdapterTile() {
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
