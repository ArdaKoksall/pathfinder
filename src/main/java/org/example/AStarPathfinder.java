package org.example;

import java.util.*;

/**
 * Implements the A* pathfinding algorithm on a 2D grid.
 */
public class AStarPathfinder {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private final int[][] grid;
    private final int height;
    private final int width;
    private final int obstacleValue;

    /**
     * Creates an A* pathfinder instance.
     *
     * @param grid The grid map where 0 represents traversable space and 1 (or obstacleValue) represents an obstacle.
     * @param obstacleValue The integer value marking an obstacle in the grid.
     * @throws IllegalArgumentException if the grid is invalid (null, empty, or non-rectangular).
     */
    public AStarPathfinder(int[][] grid, int obstacleValue) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            throw new IllegalArgumentException("Grid cannot be null or empty.");
        }
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
        this.obstacleValue = obstacleValue;

        for (int[] row : grid) {
            if (row.length != width) {
                throw new IllegalArgumentException("Grid must be rectangular.");
            }
        }
    }

    /**
     * Finds the shortest path between a start and target node using the A* algorithm.
     *
     * @param startRow The starting row index.
     * @param startCol The starting column index.
     * @param targetRow The target row index.
     * @param targetCol The target column index.
     * @return A list of Nodes representing the shortest path (including start and target),
     *         or an empty list if no path is found or start/target are invalid.
     */
    public List<Node> findShortestPath(int startRow, int startCol, int targetRow, int targetCol) {

        if (isValid(startRow, startCol) || isValid(targetRow, targetCol)) {
            System.err.println("Start or Target node is out of bounds.");
            return Collections.emptyList();
        }
        if (isObstacle(startRow, startCol) || isObstacle(targetRow, targetCol)) {
            System.err.println("Start or Target node is an obstacle.");
            return Collections.emptyList();
        }

        Node startNode = new Node(startRow, startCol, 0, heuristic(startRow, startCol, targetRow, targetCol), null);
        Node targetNode = new Node(targetRow, targetCol, 0, 0, null);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.equals(targetNode)) {
                return reconstructPath(current);
            }

            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            for (int[] direction : DIRECTIONS) {
                int neighborRow = current.getRow() + direction[0];
                int neighborCol = current.getCol() + direction[1];

                if (isValid(neighborRow, neighborCol) || isObstacle(neighborRow, neighborCol)) {
                    continue;
                }

                Node neighbor = new Node(neighborRow, neighborCol, 0, 0, null);

                if (closedSet.contains(neighbor)) {
                    continue;
                }

                int tentativeG = current.getG() + 1;

                int hCost = heuristic(neighborRow, neighborCol, targetRow, targetCol);
                Node actualNeighbor = new Node(neighborRow, neighborCol, tentativeG, hCost, current);

                openList.add(actualNeighbor);
            }
        }

        System.out.println("No path found.");
        return Collections.emptyList();
    }

    /**
     * Checks if coordinates are within the grid boundaries.
     */
    private boolean isValid(int row, int col) {
        return row < 0 || row >= height || col < 0 || col >= width;
    }

    /**
     * Checks if the cell at the given coordinates is an obstacle.
     */
    private boolean isObstacle(int row, int col) {
        return grid[row][col] == this.obstacleValue;
    }

    /**
     * Calculates the heuristic cost (Manhattan distance) between two points.
     *
     * @param r1 Row of the first point.
     * @param c1 Column of the first point.
     * @param r2 Row of the second point.
     * @param c2 Column of the second point.
     * @return The Manhattan distance.
     */
    private int heuristic(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    /**
     * Reconstructs the path from the target node back to the start node.
     *
     * @param targetNode The target node reached by the algorithm.
     * @return A list of nodes representing the path from start to target.
     */
    private List<Node> reconstructPath(Node targetNode) {
        List<Node> path = new LinkedList<>();
        Node current = targetNode;
        while (current != null) {
            path.addFirst(current);
            current = current.getParent();
        }
        return path;
    }

    public static void main(String[] args) {
        final int OBSTACLE = 1;
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

        AStarPathfinder astar = new AStarPathfinder(grid, OBSTACLE);

        int startRow = 9;
        int startCol = 0;
        int targetRow = 0;
        int targetCol = 9;

        System.out.printf("Finding path from (%d, %d) to (%d, %d)\n", startRow, startCol, targetRow, targetCol);

        long startTime = System.nanoTime();
        List<Node> path = astar.findShortestPath(startRow, startCol, targetRow, targetCol);
        long endTime = System.nanoTime();

        if (!path.isEmpty()) {
            System.out.println("Path found (" + path.size() + " steps):");
            for (Node node : path) {
                System.out.printf(" -> (%d, %d)\n", node.getRow(), node.getCol());
            }
            System.out.println("Target hit!");
        }
        System.out.printf("Time taken: %.3f ms\n", (endTime - startTime) / 1_000_000.0);
    }
}