package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Swing JPanel to visualize the A* pathfinding algorithm results.
 * Allows clicking to toggle obstacles and visualizes the found path.
 */
public class AStarVisualizer extends JPanel {

    private static final int TILE_SIZE = 40;
    private static final int ANIMATION_DELAY_MS = 150;

    private static final Color COLOR_GRID_LINES = Color.LIGHT_GRAY;
    private static final Color COLOR_FREE_SPACE = Color.WHITE;
    private static final Color COLOR_OBSTACLE = Color.DARK_GRAY;
    private static final Color COLOR_START = new Color(0, 150, 0);
    private static final Color COLOR_TARGET = new Color(200, 0, 0);
    private static final Color COLOR_PATH = Color.CYAN;
    private static final Color COLOR_CURRENT_NODE = Color.BLUE;
    private static final Color COLOR_TEXT = Color.BLACK;

    private static final int OBSTACLE_VAL = 1;
    private static final int FREE_SPACE_VAL = 0;

    private final int[][] grid;
    private final int startRow;
    private final int startCol;
    private final int targetRow;
    private final int targetCol;

    private List<Node> path;
    private final AtomicReference<Node> currentNode = new AtomicReference<>(null);
    private volatile boolean pathFound = false;
    private Thread visualizationThread;

    /**
     * Creates a new AStarVisualizer panel.
     *
     * @param initialGrid The initial grid layout.
     * @param startRow    The starting row.
     * @param startCol    The starting column.
     * @param targetRow   The target row.
     * @param targetCol   The target column.
     */
    public AStarVisualizer(int[][] initialGrid, int startRow, int startCol, int targetRow, int targetCol) {
        this.grid = initialGrid;
        this.startRow = startRow;
        this.startCol = startCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;

        this.path = findPath();

        int height = grid.length * TILE_SIZE;
        int width = grid[0].length * TILE_SIZE;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.GRAY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;

                if (row >= 0 && row < grid.length && col >= 0 && col < grid[0].length) {
                    if (row == startRow && col == startCol || row == targetRow && col == targetCol) {
                        System.out.println("Cannot change start or target node.");
                        return;
                    }

                    grid[row][col] = (grid[row][col] == OBSTACLE_VAL) ? FREE_SPACE_VAL : OBSTACLE_VAL;

                    interruptAnimation();

                    path = findPath();
                    currentNode.set(null);
                    repaint();
                    if (pathFound) {
                        startPathAnimation();
                    }
                }
            }
        });
    }

    /**
     * Recalculates the path using the AStarPathfinder.
     * @return The list of nodes in the path, or empty list if none found.
     */
    private List<Node> findPath() {
        AStarPathfinder astar = new AStarPathfinder(grid, OBSTACLE_VAL);
        List<Node> calculatedPath = astar.findShortestPath(startRow, startCol, targetRow, targetCol);
        this.pathFound = !calculatedPath.isEmpty();
        return calculatedPath;
    }

    /**
     * Starts the animation thread to visualize the path step-by-step.
     */
    public void startPathAnimation() {
        interruptAnimation();

        if (!pathFound || path.isEmpty()) {
            return;
        }

        visualizationThread = new Thread(() -> {
            try {
                Thread.sleep(200);

                for (Node node : path) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Animation interrupted");
                    }

                    SwingUtilities.invokeLater(() -> {
                        currentNode.set(node);
                        repaint();
                    });

                    Thread.sleep(ANIMATION_DELAY_MS);
                }

            } catch (InterruptedException e) {
                System.out.println("Visualization thread interrupted.");
                Thread.currentThread().interrupt();
                SwingUtilities.invokeLater(() -> {
                    currentNode.set(null);
                    repaint();
                });
            } finally {
                System.out.println("Animation finished or stopped.");
            }
        }, "A* Animation Thread");
        visualizationThread.start();
    }

    /**
     * Safely interrupts the visualization thread if it's running.
     */
    private void interruptAnimation() {
        if (visualizationThread != null && visualizationThread.isAlive()) {
            visualizationThread.interrupt();
            try {
                visualizationThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        visualizationThread = null;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == OBSTACLE_VAL) {
                    g.setColor(COLOR_OBSTACLE);
                } else {
                    g.setColor(COLOR_FREE_SPACE);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (pathFound && path != null) {
            g.setColor(COLOR_PATH);
            for (Node node : path) {
                if (!(node.getRow() == startRow && node.getCol() == startCol) &&
                        !(node.getRow() == targetRow && node.getCol() == targetCol)) {
                    g.fillRect(node.getCol() * TILE_SIZE, node.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        g.setColor(COLOR_GRID_LINES);
        for (int row = 0; row < grid.length; row++) {
            g.drawLine(0, row * TILE_SIZE, grid[0].length * TILE_SIZE, row * TILE_SIZE);
        }
        for (int col = 0; col < grid[0].length; col++) {
            g.drawLine(col * TILE_SIZE, 0, col * TILE_SIZE, grid.length * TILE_SIZE);
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, grid[0].length * TILE_SIZE -1 , grid.length * TILE_SIZE -1);


        g.setColor(COLOR_START);
        g.fillRect(startCol * TILE_SIZE, startRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(COLOR_TARGET);
        g.fillRect(targetCol * TILE_SIZE, targetRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        Node current = currentNode.get();
        if (current != null) {
            if (!(current.getRow() == startRow && current.getCol() == startCol) &&
                    !(current.getRow() == targetRow && current.getCol() == targetCol)) {
                g.setColor(COLOR_CURRENT_NODE);
                g.fillRect(current.getCol() * TILE_SIZE, current.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (!pathFound) {
            g.setColor(COLOR_TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String msg = "No Path Found";
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgAscent = fm.getAscent();
            int x = (getWidth() - msgWidth) / 2;
            int y = (getHeight() - msgAscent) / 2 + msgAscent;
            g.drawString(msg, x, y);
        }

        g.setColor(Color.BLACK);
        g.drawRect(startCol * TILE_SIZE, startRow * TILE_SIZE, TILE_SIZE-1, TILE_SIZE-1);
        g.drawRect(targetCol * TILE_SIZE, targetRow * TILE_SIZE, TILE_SIZE-1, TILE_SIZE-1);
        if (current != null) {
            g.drawRect(current.getCol() * TILE_SIZE, current.getRow() * TILE_SIZE, TILE_SIZE-1, TILE_SIZE-1);
        }
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 1, 1, 0, 1, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
                {1, 1, 0, 1, 1, 1, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 1, 1, 1, 1, 0, 1, 0, 1, 0},
                {0, 1, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 1, 0, 1, 1, 0, 1, 1, 1, 0}
        };

        int startRow = 9;
        int startCol = 0;
        int targetRow = 0;
        int targetCol = 9;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("A* Pathfinding Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AStarVisualizer visualizerPanel = new AStarVisualizer(grid, startRow, startCol, targetRow, targetCol);

            frame.add(visualizerPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);

            if (visualizerPanel.pathFound) {
                visualizerPanel.startPathAnimation();
            }
        });
    }
}