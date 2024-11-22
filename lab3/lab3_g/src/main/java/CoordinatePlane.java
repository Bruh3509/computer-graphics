import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class CoordinatePlane extends JPanel {
    private static final int GRID_SIZE = 20;
    private static final Set<Point> filledSquares = new HashSet<>();
    private static final Color COLOR_BRESENHAM = Color.RED;
    private static final Color COLOR_LINEAR = Color.GREEN;
    private static final Color COLOR_DDA = Color.BLUE;
    private static final Color COLOR_CIRCLE = Color.MAGENTA;

    public CoordinatePlane() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (e.getX() / GRID_SIZE) * GRID_SIZE;
                int y = (e.getY() / GRID_SIZE) * GRID_SIZE;
                Point square = new Point(x, y);

                if (filledSquares.contains(square)) {
                    filledSquares.remove(square);
                } else {
                    filledSquares.add(square);
                }
                repaint();
            }
        });

        // Create and set up the settings frame
        JFrame frame2 = new JFrame("Coordinate Plane Settings");
        SettingsPanel controlPanel = new SettingsPanel();

        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(400, 600);
        frame2.add(controlPanel);
        frame2.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int xOffset = (width / 2) / GRID_SIZE * GRID_SIZE;
        int yOffset = (height / 2) / GRID_SIZE * GRID_SIZE;

        drawGrid(g2d, width, height);
        drawAxes(g2d, width, height, xOffset, yOffset);
        drawTicks(g2d, width, height, xOffset, yOffset);
        drawFilledSquares(g2d);
    }

    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= width; x += GRID_SIZE) {
            g2d.drawLine(x, 0, x, height);
        }
        for (int y = 0; y <= height; y += GRID_SIZE) {
            g2d.drawLine(0, y, width, y);
        }
    }

    private void drawAxes(Graphics2D g2d, int width, int height, int xOffset, int yOffset) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(xOffset, 0, xOffset, height);
        g2d.drawLine(0, yOffset, width, yOffset);
    }

    private void drawTicks(Graphics2D g2d, int width, int height, int xOffset, int yOffset) {
        int tickSize = 5;
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // Horizontal ticks and labels
        for (int x = 0; x <= width; x += GRID_SIZE) {
            if (x == xOffset) continue;
            g2d.drawLine(x, yOffset - tickSize, x, yOffset + tickSize);
            int unit = (x - xOffset) / GRID_SIZE;
            g2d.drawString(Integer.toString(unit), x - 10, yOffset - 10);
        }

        // Vertical ticks and labels
        for (int y = 0; y <= height; y += GRID_SIZE) {
            if (y == yOffset) continue;
            g2d.drawLine(xOffset - tickSize, y, xOffset + tickSize, y);
            int unit = (yOffset - y) / GRID_SIZE;
            g2d.drawString(Integer.toString(unit), xOffset + 10, y + 5);
        }
    }

    private void drawFilledSquares(Graphics2D g2d) {
        for (Point p : filledSquares) {
            g2d.fillRect(p.x, p.y, GRID_SIZE, GRID_SIZE);
        }
        for (Point p : filledSquares) {
            g2d.drawRect(p.x, p.y, GRID_SIZE, GRID_SIZE);
        }
    }

    private void drawPoint(Graphics2D g2d, double x, double y) {
        int roundedY = (int) Math.round(y);
        int screenX = ((int) x) * GRID_SIZE + getWidth() / 2;
        int screenY = getHeight() / 2 - (roundedY + 1) * GRID_SIZE;

        filledSquares.add(new Point(screenX / GRID_SIZE * GRID_SIZE, screenY / GRID_SIZE * GRID_SIZE));
        repaint();
    }

    private void linearMethod(Graphics2D g2d, int x1, int x2, int y1, int y2) {
        g2d.setColor(COLOR_LINEAR);
        double k = ((double) (y2 - y1)) / (x2 - x1);
        double b = y1 - k * x1;
        for (int x = x1; x <= x2; ++x) {
            double y = k * x + b;
            drawPoint(g2d, x, y);
        }
    }

    private void drawLineDDA(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(COLOR_DDA);
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xStep = (double) dx / steps;
        double yStep = (double) dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            drawPoint(g2d, x, y);
            x += xStep;
            y += yStep;
        }
    }

    private void brasenhamLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(COLOR_BRESENHAM);
        int dx = x2 - x1;
        int dy = y2 - y1;

        int sx = dx >= 0 ? 1 : -1;
        int sy = dy >= 0 ? 1 : -1;

        dx = Math.abs(dx);
        dy = Math.abs(dy);

        boolean isSteep = dy > dx;

        if (isSteep) {
            int temp = dx;
            dx = dy;
            dy = temp;
        }

        double e = (double) dy / dx - 0.5;
        int x = x1;
        int y = y1;

        for (int i = 0; i <= dx; i++) {
            drawPoint(g2d, x, y);

            if (e >= 0) {
                if (isSteep) {
                    x += sx;
                } else {
                    y += sy;
                }
                e -= 1;
            }

            if (isSteep) {
                y += sy;
            } else {
                x += sx;
            }
            e += (double) dy / dx;
        }
    }

    private void drawCircleBresenham(Graphics2D g2d, int centerX, int centerY, int r) {
        g2d.setColor(COLOR_CIRCLE);
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;

        while (x <= y) {
            drawSymmetricPoints(g2d, centerX, centerY, x, y);
            if (d >= 0) {
                d += 4 * (x - y) + 10;
                y--;
            } else {
                d += 4 * x + 6;
            }
            x++;
        }
    }

    private void drawSymmetricPoints(Graphics2D g2d, int centerX, int centerY, int x, int y) {
        drawPoint(g2d, centerX + x, centerY + y);
        drawPoint(g2d, centerX - x, centerY + y);
        drawPoint(g2d, centerX + x, centerY - y);
        drawPoint(g2d, centerX - x, centerY - y);
        drawPoint(g2d, centerX + y, centerY + x);
        drawPoint(g2d, centerX - y, centerY + x);
        drawPoint(g2d, centerX + y, centerY - x);
        drawPoint(g2d, centerX - y, centerY - x);
    }

    private void clearDrawings() {
        filledSquares.clear();
    }

    public class SettingsPanel extends JPanel {
        public SettingsPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField startXField = new JTextField(5);
            JTextField startYField = new JTextField(5);
            JTextField endXField = new JTextField(5);
            JTextField endYField = new JTextField(5);
            JTextField centerXField = new JTextField(5);
            JTextField centerYField = new JTextField(5);
            JTextField radiusField = new JTextField(5);

            JButton btnBrasenham = new JButton("Draw Line (Bresenham)");
            JButton btnLinear = new JButton("Draw Line (Linear)");
            JButton btnDDA = new JButton("Draw Line (DDA)");
            JButton btnDrawCircle = new JButton("Draw Circle (Bresenham)");
            JButton btnClear = new JButton("Clear");

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            add(new JLabel("Start X:"), gbc);
            gbc.gridx = 2;
            add(startXField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(new JLabel("Start Y:"), gbc);
            gbc.gridx = 2;
            add(startYField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            add(new JLabel("End X:"), gbc);
            gbc.gridx = 2;
            add(endXField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            add(new JLabel("End Y:"), gbc);
            gbc.gridx = 2;
            add(endYField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            add(new JLabel("Center X:"), gbc);
            gbc.gridx = 2;
            add(centerXField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            add(new JLabel("Center Y:"), gbc);
            gbc.gridx = 2;
            add(centerYField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 6;
            add(new JLabel("Radius:"), gbc);
            gbc.gridx = 2;
            add(radiusField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.gridwidth = 4;
            add(btnBrasenham, gbc);

            gbc.gridy = 8;
            add(btnLinear, gbc);

            gbc.gridy = 9;
            add(btnDDA, gbc);

            gbc.gridy = 10;
            add(btnDrawCircle, gbc);

            gbc.gridy = 11;
            add(btnClear, gbc);

            btnClear.addActionListener(e -> {
                filledSquares.clear();
                repaint();
            });

            btnBrasenham.addActionListener(e -> {
                int startX = Integer.parseInt(startXField.getText());
                int startY = Integer.parseInt(startYField.getText());
                int endX = Integer.parseInt(endXField.getText());
                int endY = Integer.parseInt(endYField.getText());
                brasenhamLine((Graphics2D) getGraphics(), startX, startY, endX, endY);
            });

            btnLinear.addActionListener(e -> {
                int startX = Integer.parseInt(startXField.getText());
                int startY = Integer.parseInt(startYField.getText());
                int endX = Integer.parseInt(endXField.getText());
                int endY = Integer.parseInt(endYField.getText());
                linearMethod((Graphics2D) getGraphics(), startX, endX, startY, endY);
            });

            btnDDA.addActionListener(e -> {
                int startX = Integer.parseInt(startXField.getText());
                int startY = Integer.parseInt(startYField.getText());
                int endX = Integer.parseInt(endXField.getText());
                int endY = Integer.parseInt(endYField.getText());
                drawLineDDA((Graphics2D) getGraphics(), startX, startY, endX, endY);
            });

            btnDrawCircle.addActionListener(e -> {
                int centerX = Integer.parseInt(centerXField.getText());
                int centerY = Integer.parseInt(centerYField.getText());
                int radius = Integer.parseInt(radiusField.getText());
                drawCircleBresenham((Graphics2D) getGraphics(), centerX, centerY, radius);
            });
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Coordinate Plane");
        CoordinatePlane panel = new CoordinatePlane();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(panel);
        frame.setVisible(true);
    }
}