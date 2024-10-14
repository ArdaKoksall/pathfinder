package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AStarVisualizer extends JPanel {
    private static final int TILE_SIZE = 50;
    private final int[][] grid;
    private List<Node> path;
    private final Node start;
    private final Node target;
    private Node currentNode;
    private Thread visualizationThread;

    public AStarVisualizer(int[][] grid, Node start, Node target) {
        this.grid = grid;
        this.start = start;
        this.target = target;
        this.path = findPath();

        // Set the preferred size based on grid size and tile size
        int width = grid[0].length * TILE_SIZE;
        int height = grid.length * TILE_SIZE;
        setPreferredSize(new Dimension(width, height));

        // Add mouse listener for clicking on the grid
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;

                if (row >= 0 && row < grid.length && col >= 0 && col < grid[0].length) {
                    grid[row][col] = grid[row][col] == 1 ? 0 : 1;
                    path = findPath();
                    if (visualizationThread != null && visualizationThread.isAlive()) {
                        visualizationThread.interrupt();
                    }
                    startPathVisualization();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                int value = grid[row][col];
                if (row == start.x && col == start.y) {
                    g.setColor(Color.LIGHT_GRAY);
                } else if (row == target.x && col == target.y) {
                    g.setColor(Color.DARK_GRAY);
                } else if (value == 1) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (path != null) {
            for (Node node : path) {
                g.setColor(Color.CYAN);
                g.fillRect(node.y * TILE_SIZE, node.x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(node.y * TILE_SIZE, node.x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (currentNode != null) {
            g.setColor(Color.BLUE);
            g.fillRect(currentNode.y * TILE_SIZE, currentNode.x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(currentNode.y * TILE_SIZE, currentNode.x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    public void updateCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
        repaint();
    }

    private List<Node> findPath() {
        AStarPathfinder astar = new AStarPathfinder(grid);
        return astar.findShortestPath(start, target);
    }

    private void startPathVisualization() {
        visualizationThread = new Thread(() -> {
            int startIndex = 0;
            if (currentNode != null) {
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i).equals(currentNode)) {
                        startIndex = i;
                        break;
                    }
                }
            }

            for (int i = startIndex; i < path.size(); i++) {
                updateCurrentNode(path.get(i));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        visualizationThread.start();
    }

    public static void main(String[] args) {
        int[][] grid = {
                {1, 1, 1, 0, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1, 0, 1, 0, 1, 1},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        Node start = new Node(9, 0, 0, 0, null);
        Node target = new Node(0, 9, 0, 0, null);

        JFrame frame = new JFrame("PathFinder");
        AStarVisualizer visualizer = new AStarVisualizer(grid, start, target);
        frame.add(visualizer);
        frame.pack(); // Automatically sizes frame based on panel preferred size
        frame.setResizable(false); // Disable resizing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        visualizer.startPathVisualization();
    }
}
