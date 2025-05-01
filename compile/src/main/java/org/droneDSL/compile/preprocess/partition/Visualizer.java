package org.droneDSL.compile.preprocess.partition;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;

public class Visualizer extends JPanel implements MouseMotionListener {

  private final Polygon jtsPolygon;
  private final List<Coordinate> coordinates;
  private static final int PADDING = 50;
  private static final int PANEL_SIZE = 600;
  private static final int POINT_SIZE = 6;
  private static final int HOVER_RADIUS = 8;

  private double scaleX, scaleY;
  private int panelWidth, panelHeight;

  public Visualizer(Polygon jtsPolygon, List<Coordinate> coordinates) {
    this.jtsPolygon = jtsPolygon;
    this.coordinates = coordinates;
    setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
    addMouseMotionListener(this);
    setToolTipText("");
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    panelWidth = getWidth();
    panelHeight = getHeight();

    Envelope env = jtsPolygon.getEnvelopeInternal();
    scaleX = (panelWidth - 2.0 * PADDING) / env.getWidth();
    scaleY = (panelHeight - 2.0 * PADDING) / env.getHeight();

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.translate(PADDING, panelHeight - PADDING);
    g2.scale(1, -1);

    drawPolygon(g2);
    drawPoints(g2);
  }

  private void drawPolygon(Graphics2D g2) {
    g2.setColor(Color.BLUE);
    Path2D path = new Path2D.Double();
    Coordinate[] coords = jtsPolygon.getCoordinates();
    path.moveTo(coords[0].x * scaleX, coords[0].y * scaleY);
    for (int i = 1; i < coords.length; i++) {
      path.lineTo(coords[i].x * scaleX, coords[i].y * scaleY);
    }
    path.closePath();
    g2.draw(path);
  }

  private void drawPoints(Graphics2D g2) {
    g2.setColor(Color.RED);
    for (Coordinate coord : coordinates) {
      double x = coord.x * scaleX;
      double y = coord.y * scaleY;
      g2.fill(new Ellipse2D.Double(x - POINT_SIZE / 2.0, y - POINT_SIZE / 2.0, POINT_SIZE, POINT_SIZE));
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    int mouseX = e.getX() - PADDING;
    int mouseY = panelHeight - e.getY() - PADDING;

    for (Coordinate coord : coordinates) {
      double screenX = coord.x * scaleX;
      double screenY = coord.y * scaleY;
      double dist = Math.hypot(screenX - mouseX, screenY - mouseY);
      if (dist <= HOVER_RADIUS) {
        setToolTipText(String.format("(%.2f, %.2f)", coord.x, coord.y));
        return;
      }
    }
    setToolTipText(null);
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  public static void main(String[] args) {
    GeometryFactory gf = new GeometryFactory();
    Coordinate[] coords = new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(100, 0),
        new Coordinate(80, 60),
        new Coordinate(40, 100),
        new Coordinate(0, 80),
        new Coordinate(0, 0)
    };
    Polygon polygon = gf.createPolygon(coords);

    double spacing = 10;
    double angle = 90;
    double triggerDistance = 15;
    CorridorPartition partition = new CorridorPartition(spacing, angle);
    List<Coordinate> points = partition.generateTransectsAndPoints(polygon);

    JFrame frame = new JFrame("Partition Visualizer (JTS)");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new Visualizer(polygon, points));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
