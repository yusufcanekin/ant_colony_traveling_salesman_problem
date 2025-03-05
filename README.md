# Ant Colony Optimization for Traveling Salesman Problem (TSP)

## Project Overview
This project implements the Traveling Salesman Problem (TSP) using two different approaches:
- **Brute-Force Method:** Computes all possible routes to find the shortest one.
- **Ant Colony Optimization (ACO):** Uses artificial ants and pheromone-based heuristics to iteratively improve route selection.

The program can visualize results by drawing nodes, edges, and pheromone trails to show how the solution evolves over time.

## Features
- **Pathfinding Approaches:**
  - Brute-force method for small-scale problems.
  - Ant Colony Optimization for efficient, large-scale solutions.
- **Graph Representation:**
  - Models locations as nodes with coordinates.
  - Uses a pheromone matrix to guide ACO-based path selection.
- **Visualization:**
  - Displays paths using **StdDraw**.
  - Highlights pheromone intensity using line thickness.

## Project Structure
```
📂 src/
 ├── Graph.java          # Defines the graph, distance matrix, and pheromone updates.
 ├── Location.java       # Represents a location with X, Y coordinates.
 ├── Main.java           # Main execution file, handles input and runs TSP algorithms.
```

## Usage
### Compilation
To compile the project, run:
```sh
javac -d out *.java
```

### Running the Program
Run the program to compute the shortest cycle:
```sh
java -cp out Main
```
The program will use predefined parameters to determine the method (Brute Force or ACO) and visualize the results.

### Input Data Format
The program reads input from a file (`Input05.txt`), where each line contains an X, Y coordinate:
```
0.4638,0.0487
0.2751,0.9832
...
```
Each line represents a location in the TSP graph.

## Example Output
```
Chosen Method: Ant Colony Method
Time to find the shortest path: 2.5 seconds
Shortest Distance: 23.4567
Shortest Path: [1, 3, 5, 2, 4, 1]
```

## Visualization
The program displays the computed paths using **StdDraw**:
- **Brute Force:** Shows the shortest route by connecting all nodes.
- **Ant Colony Optimization:** Uses pheromone-based visualization where thicker lines indicate stronger paths.

## Dependencies
- Java Standard Library (no external dependencies required)
- **StdDraw** for visualization

## Authors
- **Developer:** Yusuf Can Ekin

## License
This project is open-source and can be modified as needed.

