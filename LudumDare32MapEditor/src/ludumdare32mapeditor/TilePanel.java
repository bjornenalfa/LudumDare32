package ludumdare32mapeditor;

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
import static ludumdare32mapeditor.LudumDare32MapEditor.tileSet;

class TilePanel extends JPanel {

    static Camera camera = new Camera(800, 608);
    boolean mouseDown = false;
    Point lastPoint;

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
        camera.resetTransform(g);
    }

    boolean bFirst = true;
    int button;

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
                if (button == 1) {
                    if (bFirst) {
                        first = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        first.x = (int) (p.x - p.x % World.squareSize);
                        first.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.tile.texture1 = (int) (p.x - p.x % World.squareSize) / World.squareSize + (int) (p.y - p.y % World.squareSize) / World.squareSize * tileSet.horizontalTiles;
                        bFirst = false;
                    } else {
                        second = new Point();
                        Point.Double p = camera.windowToWorldCoordinates(me.getX(), me.getY());
                        second.x = (int) (p.x - p.x % World.squareSize);
                        second.y = (int) (p.y - p.y % World.squareSize);
                        ToolPanel.tile.texture2 = second.x / World.squareSize + second.y / World.squareSize * tileSet.horizontalTiles;
                        bFirst = true;
                    }
                    repaint();
                }
                mouseDown = false;
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                if (mouseDown && button == 3) {
                    Point newPoint = me.getPoint();
                    camera.moveWindowPixels(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
                    camera.constrainToTileSet();
                    lastPoint = newPoint;
                    repaint();
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
