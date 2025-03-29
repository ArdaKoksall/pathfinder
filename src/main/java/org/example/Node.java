package org.example;

import java.util.Objects;

/**
 * Represents a node in the A* search grid.
 * Stores coordinates (row, col), cost values (g, h), and the parent node for path reconstruction.
 */
public class Node implements Comparable<Node> {
    private final int row;
    private final int col;
    private final int g;
    private final int h;
    private final Node parent;

    /**
     * Constructs a new Node.
     *
     * @param row    The row index.
     * @param col    The column index.
     * @param g      The cost from the start node to this node.
     * @param h      The estimated heuristic cost from this node to the target.
     * @param parent The parent node in the path.
     */
    public Node(int row, int col, int g, int h, Node parent) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getG() { return g; }

    public Node getParent() { return parent; }


    /**
     * Calculates the total estimated cost (F = G + H).
     * @return The F cost.
     */
    public int getF() {
        return g + h;
    }

    /**
     * Compares nodes based on their F cost for the PriorityQueue.
     */
    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getF(), other.getF());
    }

    /**
     * Checks if two nodes represent the same grid cell.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return row == node.row && col == node.col;
    }

    /**
     * Generates a hash code based on the node's grid cell coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Node{" +
                "row=" + row +
                ", col=" + col +
                ", g=" + g +
                ", h=" + h +
                ", f=" + getF() +
                '}';
    }
}