# A\* Pathfinding Algorithm

This project implements the A\* pathfinding algorithm in Java. The algorithm finds the shortest path between two points on a grid, considering obstacles.

## Features

- Implements the A\* pathfinding algorithm
- Supports custom grid sizes and obstacle configurations
- Outputs the path from the start node to the target node

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/ArdaKoksall/pathfinder.git
    ```
2. Navigate to the project directory:
    ```sh
    cd your-repo-name
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```

### Usage

1. Run the `AStarPathfinder` class:
    ```sh
    mvn exec:java -Dexec.mainClass="AStarPathfinder"
    ```

2. The program will output the path found by the algorithm, if any.

### Example

The grid used in the example is as follows:
0 = free space, 1 = obstacle
    
```
{1, 1, 1, 0, 1, 1, 1, 1, 0, 0} 
{1, 0, 1, 0, 1, 0, 0, 0, 0, 1}
{1, 0, 1, 1, 1, 0, 1, 1, 1, 1}
{1, 0, 0, 0, 0, 0, 1, 0, 1, 0}
{1, 1, 1, 1, 1, 0, 1, 0, 1, 1}
{0, 0, 0, 0, 1, 0, 0, 0, 0, 0}
{1, 1, 1, 0, 1, 1, 1, 1, 1, 0}
{0, 0, 0, 0, 1, 0, 0, 0, 0, 0}
{1, 1, 1, 1, 1, 1, 1, 1, 1, 0}
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
```
    

The start node is at (9, 0) and the target node is at (0, 9).

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
