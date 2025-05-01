package org.droneDSL.compile.preprocess.partition;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.List;


public class Visualizer extends JPanel implements MouseMotionListener {

  private final Polygon polygon;
  private final List<Point2D> points;

  private static final int PADDING = 50;
  private static final int VIRTUAL_SIZE = 120;
  private static final int POINT_SIZE = 6;
  private static final int HOVER_RADIUS = 8; // pixels

  private double scaleX, scaleY;
  private int panelWidth, panelHeight;

  public Visualizer(Polygon polygon, List<Point2D> points) {
    this.polygon = polygon;
    this.points = points;
    setPreferredSize(new Dimension(600, 600));
    addMouseMotionListener(this);
    setToolTipText(""); // Enable tooltip feature
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    panelWidth = getWidth();
    panelHeight = getHeight();

    scaleX = (panelWidth - 2 * PADDING) / (double) VIRTUAL_SIZE;
    scaleY = (panelHeight - 2 * PADDING) / (double) VIRTUAL_SIZE;

    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(2));
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.translate(PADDING, panelHeight - PADDING);
    g2.scale(1, -1);

    drawAxes(g2);
    drawPolygon(g2);
    drawPoints(g2);
  }

  private void drawAxes(Graphics2D g2) {
    g2.setColor(Color.LIGHT_GRAY);
    int step = 20;
    int max = 120;

    for (int x = 0; x <= max; x += step) {
      int screenX = (int) (x * scaleX);
      g2.drawLine(screenX, 0, screenX, (int) (max * scaleY));
    }
    for (int y = 0; y <= max; y += step) {
      int screenY = (int) (y * scaleY);
      g2.drawLine(0, screenY, (int) (max * scaleX), screenY);
    }

    g2.scale(1, -1);
    g2.setColor(Color.BLACK);
    for (int x = 0; x <= max; x += step) {
      int screenX = (int) (x * scaleX);
      g2.drawString(Integer.toString(x), screenX - 5, -5);
    }
    for (int y = 0; y <= max; y += step) {
      int screenY = -(int) (y * scaleY);
      g2.drawString(Integer.toString(y), -30, screenY + 5);
    }
    g2.scale(1, -1);
  }

  private void drawPolygon(Graphics2D g2) {
    g2.setColor(Color.BLUE);
    Path2D scaledPolygon = new Path2D.Double();
    scaledPolygon.moveTo(polygon.xpoints[0] * scaleX, polygon.ypoints[0] * scaleY);
    for (int i = 1; i < polygon.npoints; i++) {
      scaledPolygon.lineTo(polygon.xpoints[i] * scaleX, polygon.ypoints[i] * scaleY);
    }
    scaledPolygon.closePath();
    g2.draw(scaledPolygon);
  }

  private void drawPoints(Graphics2D g2) {
    g2.setColor(Color.RED);
    for (Point2D point : points) {
      double x = point.getX() * scaleX;
      double y = point.getY() * scaleY;
      g2.fill(new Ellipse2D.Double(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE));
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    int mouseX = e.getX() - PADDING;
    int mouseY = panelHeight - e.getY() - PADDING; // invert Y

    for (Point2D p : points) {
      double screenX = p.getX() * scaleX;
      double screenY = p.getY() * scaleY;

      double dist = Math.hypot(screenX - mouseX, screenY - mouseY);
      if (dist <= HOVER_RADIUS) {
        setToolTipText(String.format("(%.2f, %.2f)", p.getX(), p.getY()));
        return;
      }
    }
    setToolTipText(null); // No point nearby
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // Not used
  }

  public static void main(String[] args) {
    Polygon polygon = new Polygon();
    polygon.addPoint(0, 0);
    polygon.addPoint(100, 0);
    polygon.addPoint(80, 60);
    polygon.addPoint(40, 100);
    polygon.addPoint(0, 80);

    double spacing = 10;
    double angle = 90;
    double triggerDistance = 15;
    CorridorPartition cPartition = new CorridorPartition(spacing, angle);

    List<Point2D> points = cPartition.generateTransectsAndPoints(polygon);
    System.out.println("Generated " + points.size() + " points:");
    JFrame frame = new JFrame("Polygon Partition Visualizer");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new Visualizer(polygon, points));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
