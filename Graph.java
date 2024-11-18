import java.awt.*;
import java.util.ArrayList;

public class Graph {
    public ArrayList<Location> locations; // An array of location objects to store the locations
    public double[][] distances; // Distance between each node is stored in this matrix
    public double[][] pheromones; // pheromone levels of each connection is stored in this matrix
    private final double INITIALPHEROMONE; // initial pheromone level of every connection
    private final double Q; // Q is used to calculate delta (delta = Q/total cycle distance)
    private final double DEGREDATION_FACTOR; // degredation factor of current pheromone levels, after every iteration
    public final double ALPHA;
    public final double BETA;

    /**
     * Constructor of the Location class.
     * @param locations
     */
    public Graph(ArrayList<Location> locations, double INITIALPHEROMONE, double Q, double DEGREDATION_FACTOR, double ALPHA, double BETA){
        this.ALPHA = ALPHA;
        this.BETA = BETA;
        this.DEGREDATION_FACTOR = DEGREDATION_FACTOR;
        this.INITIALPHEROMONE = INITIALPHEROMONE;
        this.Q = Q;
        this.locations = locations;
        // distances of connections between nodes is stored in distances matrix
        distanceMatrixCreator();
        // pheromone levels of connections are stored in pheromones matrix
        // this method initializes the pheromone levels of
        pheromoneMatrixCreator();
    }

    /**
     * This method calculates the distances between nodes,
     * and stores them in the distance matrix.
     */
    private void distanceMatrixCreator() {
        distances = new double[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j< locations.size();j++){
                distances[i][j] = Math.sqrt(Math.pow(locations.get(i).getX() - locations.get(j).getX(), 2) +
                        Math.pow(locations.get(i).getY() - locations.get(j).getY(), 2));
            }
        }
    }

    /**
     * This method initializes the pheromone level of connections.
     * Pheromone levels between nodes are stored in pheromone matrix
     */
    private void pheromoneMatrixCreator(){
        pheromones = new double[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j< locations.size();j++){
                pheromones[i][j] = INITIALPHEROMONE;
            }
        }
    }

    /**
     * After every iteration, since the pheromone levels are calculated cumulatively,
     * every further iteration's generated paths will be more accurate and precise.
     * By decreasing the levels of previous routes' pheromone levels,
     * every further iteration will have stronger influence on the endstate.
     * This method decreases the pheromone levels of connections by multiplying them by a degredation factor.
     */
    public void pheromoneDegradation(){
        for(int l = 0; l < locations.size();l++){
            for (int m = 0; m < locations.size();m++){
                pheromones[l][m] = pheromones[l][m]*DEGREDATION_FACTOR;
            }
        }
    }

    /**
     * After every generated path, that path's phreomone levels are increased by delta amount.
     * Delta amount is calculated via (Q/total distance of cycle).
     * This method calculates delta for every path, and updates the pheromone levels accordingly.
     * @param route
     * @param distance
     */
    public void routePheromoneUpdate(ArrayList<Integer> route, double distance){
        double delta = Q/distance;
        for(int k = 0;k < route.size() - 1;k++){
            pheromones[route.get(k)][route.get(k+1)] += delta;
            pheromones[route.get(k+1)][route.get(k)] += delta;
        }
    }

    /**
     * This method displays every connection's pheromone levels by proportioning pheromone levels to connection line thickness.
     * @param bestRoute
     */
    public void graphDisplayer(Integer[] bestRoute){
        // sets canvas size and x-y scales, specifies connection colour
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0.0, 1.0);
        StdDraw.setYscale(0.0, 1.0);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.enableDoubleBuffering();

        // Draws connection between nodes
        for(int i = 0; i < pheromones.length;i++) {
            for (int k = 0; k < bestRoute.length - 1; k++) {
                if (i != k) {
                    // adjusts line thickness according to pheromone levels
                    StdDraw.setPenRadius(pheromones[i][k]);
                    // draws lines between nodes
                    StdDraw.line(locations.get(i).getX(), locations.get(i).getY(), locations.get(k).getX(), locations.get(k).getY());
                }
            }
        }
        // draws circles representing nodes
        for(int destination: bestRoute){
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(locations.get(destination).getX(),locations.get(destination).getY(), 0.015);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
            StdDraw.text(locations.get(destination).getX(), locations.get(destination).getY(), "" + (destination+1));
        }
        StdDraw.show();
    }
}
