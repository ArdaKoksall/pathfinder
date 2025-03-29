# Java A* Pathfinding Visualizer

A simple Java program using Swing to show the A* pathfinding algorithm on a grid.


## What it Does

*   Finds the shortest path between a start (Green) and target (Red) point on a grid.
*   Avoids obstacles (Dark Gray).
*   Animates the path found (Cyan path with a moving Blue square).
*   Lets you click grid squares to add/remove obstacles (recalculates path automatically).
*   Shows "No Path Found" if the target can't be reached.

## How to Run

1.  **You need Java installed (JDK 8 or newer).**
2.  **Download the code files:** `Node.java`, `AStarPathfinder.java`, `AStarVisualizer.java`.
3.  **Open a terminal or command prompt** in the folder where you saved the `.java` files.
4.  **Compile:**
    ```bash
    javac Node.java AStarPathfinder.java AStarVisualizer.java
    ```
5.  **Run:**
    ```bash
    java AStarVisualizer
    ```
    (Make sure you type `AStarVisualizer` exactly as the filename).

## How to Use

*   The window will open showing the grid.
*   If a path is possible, it will draw it and animate the blue square.
*   Click any white or dark gray square to turn it into an obstacle or back into empty space. The path will update.

## Code Files

*   `AStarVisualizer.java`: Handles the drawing, animation, and user clicks (Main program).
*   `AStarPathfinder.java`: Contains the A* algorithm logic.
*   `Node.java`: Represents a single square on the grid.

---
That's it! Enjoy visualizing A*.