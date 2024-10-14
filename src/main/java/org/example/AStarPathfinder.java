package org.example;

import java.util.*;

class Node implements Comparable<Node> {
    int x, y; // Coordinates
    int g, h; // g = cost from start to node, h = estimated cost to target
    Node parent; // Parent node for path reconstruction

    public Node(int x, int y, int g, int h, Node parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public int getF() {
        return g + h;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

public class AStarPathfinder {
    private static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right
    private final int[][] grid;
    private final int width, height;

    public AStarPathfinder(int[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
    }

    public List<Node> findShortestPath(Node start, Node target) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.equals(target)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int[] direction : directions) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (isInBounds(newX, newY) && grid[newX][newY] == 0) {
                    Node neighbor = new Node(newX, newY, current.g + 1, heuristic(newX, newY, target), current);

                    if (closedList.contains(neighbor)) continue;

                    if (!openList.contains(neighbor) || current.g + 1 < neighbor.g) {
                        neighbor.g = current.g + 1;
                        neighbor.parent = current;
                        openList.add(neighbor);
                    }
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private int heuristic(int x, int y, Node target) {
        // Manhattan distance (use Euclidean distance if diagonal movement is allowed)
        return Math.abs(x - target.x) + Math.abs(y - target.y);
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        // 0 = free space, 1 = obstacle
        int[][] grid = {
                {1, 1, 1, 0, 1, 1, 1, 1, 0, 0}, // Row 0 (top)
                {1, 0, 1, 0, 1, 0, 0, 0, 0, 1}, // Row 1
                {1, 0, 1, 1, 1, 0, 1, 1, 1, 1}, // Row 2
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 0}, // Row 3
                {1, 1, 1, 1, 1, 0, 1, 0, 1, 1}, // Row 4
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // Row 5
                {1, 1, 1, 0, 1, 1, 1, 1, 1, 0}, // Row 6
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // Row 7
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, // Row 8
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0}  // Row 9 (bottom)
        }; // Column 0 1 2 3 4 5 6 7 8 9

        AStarPathfinder astar = new AStarPathfinder(grid);
        Node start = new Node(9, 0, 0, 0, null); // Bottom-left (row 9, col 0)
        Node target = new Node(0, 9, 0, 0, null); // Top-right (row 0, col 9)


        List<Node> path = astar.findShortestPath(start, target);

        if (!path.isEmpty()) {
            System.out.println("Path found:");
            for (Node node : path) {
                System.out.println("(" + node.x + ", " + node.y + ")");
                if(node.x == 0 && node.y == 9){
                    System.out.println("Target hit!");
                }
            }
        } else {
            System.out.println("No path found.");
        }
    }
}
