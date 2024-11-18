/**
 * This program finds the shortest cycle for a salesman either using ant colony optimization
 * or brute force calculation by visiting every node in a given graph.
 * @author Yusuf Can Ekin, Student ID: 2022400207
 * @since Date: 12.06.2024
 */

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    /**
     * Main method of the program.
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Parameters of ant colony method
        final int ANT_NUM = 50; // number of ants which will be created at every iteration
        final int ITERATION_NUM = 100; // number of iterations
        final double INITIAL_PHEROMONE = 0.1; // initial pheromone level of every connection
        final double Q = 0.0001; // Q is used to calculate delta (delta = Q/total cycle distance)
        final double DEGREDATION_FACTOR = 0.9; // degredation factor of current pheromone levels, after every iteration
        final double ALPHA = 1.5;
        final double BETA = 4;

        // 1 indicates brute force calculation,
        // 2 indicates ant colony calculation.
        int chosenMethod = 2;

        // 1 indicates shortest path map
        // 2 indicates pheromone level map
        int chosenGraph = 2;

        // Every location's x and y coordinates are stored in .txt files.
        String fileName = "Input05.txt";

        // fileReader method takes the fileName as input and returns locations as an arraylist of Location objects.
        // Location class consists of x and y coordinates of locations.
        ArrayList<Location> locations = fileReader(fileName);

        Integer[] bestRoute = null; // an Integer array to store the shortest route, locations' indexes are stored in this array.
        double solutionTime = 0.0; // shortest path calculation duration is stored in this variable
        Graph graph = null; // for ant colony method, this graph object will store every parameter of ACO, and provide methods to implement the algoriitmh

        // chosen method name will be assigned at relevant if-else blocks,
        // if 1 is chosen method, then its name will be Brute-Force method
        // if 2 is chosen method, then its name will be Ant Colony Method
        String chosenMethodName = null;

        if (chosenMethod == 1){

            // takes the current time before the execution of the brute force method
            double initialTime = System.currentTimeMillis();

            // Shortest route is returned by the function, then assigned to bestRoute array
            bestRoute = bruteForceCalculator(locations,0);

            // takes the time after the execution
            double finishingTime = System.currentTimeMillis();

            // calculates the duration in milliseconds, then converts it to seconds by dividing with 1000
            solutionTime = (finishingTime - initialTime) / 1000.0;

            chosenMethodName = "Brute-Force Method";
        }
        else if (chosenMethod == 2) {
            double initialTime = System.currentTimeMillis();

            // Creates a graph object of Graph class,
            // Graph class consists of pheromone and distance values between every pair of nodes.
            // It also contains methods for implementing the ACO algorithm
            graph = new Graph(locations, INITIAL_PHEROMONE, Q, DEGREDATION_FACTOR, ALPHA, BETA);

            bestRoute = antColonyCalculator(locations,ANT_NUM,ITERATION_NUM, graph);
            double finishingTime = System.currentTimeMillis();
            solutionTime = (finishingTime - initialTime) / 1000.0;
            chosenMethodName = "Ant Colony Method";

        }
        if (chosenGraph == 1) shortestPathDisplayer(bestRoute, locations);
        // Displays the nodes and their connections' pheromone levels, which indicates the algorithm's way of working.
        else if (chosenMethod == 2) graph.graphDisplayer(bestRoute);

        // Distance of the shortest route is calculated.
        double distance = calculateRouteDistance(bestRoute, locations);

        for (int i = 0; i < bestRoute.length; i++){
            bestRoute[i]++;
        }
        System.out.println("Chosen Method:" + chosenMethodName);
        System.out.println("Time it takes to find the shortest path: " + solutionTime + " seconds");
        System.out.println("Shortest Distance: " + distance);
        System.out.println("Shortest Path: " + Arrays.toString(bestRoute));
    }

    /**
     * This method takes the file's name as input.
     * Files consist of lines which contain every node's x and y coordinates seperated by a comma, for instance "0.4638,0.0487".
     * This method then creates Location objects for every node and sets their x and y coordinates,
     * stores the objects in an arraylist and returns it.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<Location> fileReader(String fileName) throws FileNotFoundException{
        ArrayList<Location> locations = new ArrayList<>();
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");

            Location location = new Location(); // creates an object for current location

            // sets the x and y coordinates of locations
            location.setX(Double.parseDouble(parts[0]));
            location.setY(Double.parseDouble(parts[1]));

            // adds the current location's object to the locations arraylist
            locations.add(location);
        }
        scanner.close();
        return locations;
    }


    /**
     * This method implements Ant Colony Algorithm to find the shortest cycle from a given starting node.
     * Updates the best route at every iteration and keeps it in an array.
     * After all iterations are done, returns the bestRoute array which contains locations' indexes at locations arraylist.
     * @param locations
     * @param antNumber
     * @param iterationNumber
     * @param graph
     * @return
     */
    public static Integer[] antColonyCalculator(ArrayList<Location> locations, int antNumber, int iterationNumber, Graph graph){
        ArrayList<Integer> bestRoute = new ArrayList<>();
        // first initialize the bestRouteDistance to max value of the double data type so that this variable can be updated properly.
        double bestRouteDistance = Double.MAX_VALUE;

        // outer for loop represents every iteration of created ants
        // inner for loop represents every ant created at each iteration
        for(int i = 0; i < iterationNumber;i++){
            for(int j = 0;j < antNumber;j++){

                // for every ant, antPathGenerator generates a route
                // route is generated according to every possible connection's relative possibility,
                // which is calculated according to connection's pheromone level and distance
                // further explanation of this method is provided on its comment lines
                ArrayList<Integer> route = antPathGenerator(graph);


                // returned route starts from the given first index, but does not end with that index
                // for instance, [0, 2, 5, 3, 1, 4] can be the returned route,
                // but in order to calculate the cycle's total distance, route must end with the starting index
                // in order to accomplish that, this line of code adds the initial index to the end of the route
                route.add(0);

                // calculateRouteDistance method calculates the total distance of a given cycle
                // further explanation of this method is provided on its comment lines
                double distance = calculateRouteDistance(route,locations);

                // at every current generated route, total distance of it will be compared to previous best route's total distance
                // if it is shorter than the best route, bestRoute arraylist is updated to the current generated route
                // also best route's distance is updated to current generated route's total distance
                if (distance < bestRouteDistance) {
                    bestRoute = route;
                    bestRouteDistance = distance;
                }

                // after every generated route, pheromone level of that path is updated in order to make the path more feasible for the next ant
                // further explanation of this method is provided on its comment lines
                graph.routePheromoneUpdate(route, distance);

            }
            // after every iteration, pheromone levels of the graph are decreased by a degredation factor
            // since the pheromone levels are calculated cumulatively, every further iteration's generated paths will be more accurate and precise
            // by decreasing the levels of previous routes' pheromone levels,
            // every further iteration will have stronger influence on the endstate
            graph.pheromoneDegradation();
        }
        // best route is stored in an arraylist
        // bestRoute arraylist's values are passed to an array in order to make it compatible with other data at the main method
        Integer[] bestRouteArray = new Integer[bestRoute.size()];
        bestRoute.toArray(bestRouteArray);
        return bestRouteArray;
    }

    /**
     * This method creates a possible route for a single ant, and then returns it.
     * @param graph
     * @return
     */

    public static ArrayList<Integer> antPathGenerator(Graph graph){
        ArrayList<Integer> route = new ArrayList<>(); //route will be stored in this list
        int initialIdx = 0;
        // since every node can be visited only once in a cycle, available nodes are stored in this list
        ArrayList<Integer> availableNodes = new ArrayList<>();

        // starting index is added to the route
        route.add(initialIdx);

        // available nodes are filled with the indexes of the locations'
        for(int i = 0; i < graph.locations.size();i++){
            availableNodes.add(i);
        }

        // since starting index is added to the route, it is not available anymore
        availableNodes.remove((Integer)initialIdx);

        // in order to evaluate the connection's preferability, current node is stored in this variable
        int currentNode = initialIdx;

        // while there is still available node(s), next node will be chosen
        while(!availableNodes.isEmpty()) {

            // chooseNextNode method looks at the available nodes,
            // looks at the connections of the current node and available nodes
            // calculates every connection's possibility to be chosen among all available connections
            // while calculating the preferability of a connection, this method looks at the pheromone level and distance of the connection
            // more pheromone means more preferable, more distance means less preferable
            // further explanation of this method is provided on its comment lines
            int nextNode = chooseNextNode(currentNode, availableNodes, graph);
            route.add(nextNode);

            // after choosing the next node, it is removed from the available nodes
            availableNodes.remove((Integer)nextNode);

            // since next node is chosen, ant will choose the node after the chosen one
            // our current node is updated to chosen next node
            currentNode = nextNode;
        }
        return route;
    }

    /**
     * This method chooses the next node according to connections' preferability, then returns the index of chosen node.
     * @param currentNode
     * @param availableNodes
     * @param graph
     * @return
     */
    public static int chooseNextNode(int currentNode, ArrayList<Integer> availableNodes, Graph graph){
        //every connection's probability of being chosen is stored in this array
        double[] probabilities = new double[availableNodes.size()];
        double totalEdgeValue = 0.0;

        int nextNode = 0;
        for(int i = 0; i < availableNodes.size();i++){
            int nextProbableNode = availableNodes.get(i);

            // a connection's value is calculated via this way:
            // p = pheromone level of the connection
            // d = distance of the connection
            // alpha, beta are arbitrary constant values
            // Probability = (p ^ alpha) / (d ^ beta)

            double pheromone = Math.pow(graph.pheromones[currentNode][nextProbableNode], graph.ALPHA);
            double distance = Math.pow(graph.distances[currentNode][nextProbableNode], graph.BETA);
            double edgeValue = pheromone/distance;

            // calculated edge value is stored in probabilities array
            probabilities[i] = edgeValue;

            // calculated edge value is added to total probability
            totalEdgeValue += edgeValue;
        }

        // in order to calculate every connection's possibility to be chosen among all available connections,
        // every connection's edge value is divided by total edge value
        // after updating the probabilities with this way, sum of the probabilities array will be equal to 1
        for (int i = 0; i < probabilities.length ; i++){
            probabilities[i] = probabilities[i] / totalEdgeValue;
        }

        // with generating a random number between 0-1.0
        // corresponding interval of every connection can be chosen probabilistically
        // for instance;
        // edgevalue1 = 4
        // edgevalue2 = 7
        // edgevalue3 = 11
        // total edge value then becomes 22, dividing every edge value to the total edge value;
        // probability1 = 4/22 = 0.18
        // probability2 = 7/22 = 0.32
        // probability3 = 11/22 = 0.50
        // if the generated number is 0.48, until exceeding this number, probabilities will be summed
        // 0.18 is smaller than 0.48
        // since 0.48 is not reached or exceeded, summation will continue
        // 0.18 + 0.32 is exceeding 0.48, so second node is chosen
        // if the generated number was 0.11, first node was going to be chosen


        // this code does the explained implementation
        // a random double value is generated
        double randomValue = Math.random();
        double summedProbabilities = 0.0;
        for (int i = 0; i < probabilities.length;i++){
            // until the generated number is reached, probabilities are summed
            summedProbabilities += probabilities[i];
            if (summedProbabilities >= randomValue){
                // just after reaching or exceeding the generated number, next node is chosen and the loop is broken
                nextNode = availableNodes.get(i);
                break;
            }
        }
        return nextNode;
    }

    /**
     * This method calculates every permutation of a set of locations to find the shortest cycle from a given starting node.
     * Returns the bestRoute array which contains locations' indexes at locations arraylist.
     * @param locations
     * @param initialIdx
     * @return
     */
    public static Integer[] bruteForceCalculator(ArrayList<Location> locations, int initialIdx){
        // Creates a clone array of the location indexes,
        // which will be later used to calculate the permutations of indexes
        Integer[] initialRoute = new Integer[locations.size() - 1];

        // for optimization, migros's index is not included to the list which will be permuted
        // with this update, permuted paths won't contain migros, which accelerates the calculation
        for (int i = 1; i < locations.size(); i++) {
            initialRoute[i - 1] = i;
        }

        // bestRoute will be updated during the calculations of permutations
        // in order to preserve the source list for permutation, bestRoute is cloned from initialRoute, which is the source list for permute method
        Integer[] bestRoute = initialRoute.clone();

        // shortest path is calculated and returned from permute method
        bestRoute = permute(bestRoute, initialRoute, locations, 0, initialIdx);

        // Since migros is not included in the bestRoute,
        // migros's index is added to the beginning and the end of the bestRoute in order to complete the cycle
        Integer[] bestRouteUpdated = new Integer[bestRoute.length + 2];
        System.arraycopy(bestRoute, 0 , bestRouteUpdated, 1,bestRoute.length);
        bestRouteUpdated[0] = 0;
        bestRouteUpdated[bestRouteUpdated.length - 1] = 0;
        return bestRouteUpdated;
    }

    /**
     * This method generates all permutations of the given list. Then returns the shortest path.
     * @param arr
     * @param k
     */
    private static Integer[] permute(Integer[] shortestRoute, Integer[] arr, ArrayList<Location> locations, int k, int initialIdx) {
        if (k == arr.length) {
            // Calculate the route distance for the current permutation
            double currentDistance = calculateRouteDistance(arr, locations);
            // If the current permutation yields a shorter route, update shortestRoute
            if (calculateRouteDistance(shortestRoute, locations) > currentDistance)
                System.arraycopy(arr, 0, shortestRoute, 0, arr.length);
        } else {
            // Recursive case: generate permutations for the subarray starting from index k
            for (int i = k; i < arr.length; i++) {
                Integer temp = arr[i];
                arr[i] = arr[k];
                arr[k] = temp;
                // Recursively generate permutations for the subarray with the next index k + 1
                permute(shortestRoute, arr, locations, k + 1, initialIdx);
                // Swap elements back to restore the original array for the next iteration
                temp = arr[k];
                arr[k] = arr[i];
                arr[i] = temp;
            }
        }
        return shortestRoute;
    }

    /**
     * This method calculates the distance of a given route. Brute-Force Method will use this overloaded method to calculate distances.
     * @param route
     * @param locations
     */
    private static double calculateRouteDistance(Integer[] route, ArrayList<Location> locations) {
        double distance = 0;
        int prevIdx = 0;
        // Since this overloaded method will be used to calculate permuted paths,
        // migros's index will not be included while generating permutations
        // in order to calculate cycle's total distance properly,
        // first destination's distance to migros is added to total distance
        distance+= Math.sqrt(Math.pow(locations.get(0).getX() - locations.get(route[0]).getX(), 2) +
                Math.pow(locations.get(0).getY() - locations.get(route[0]).getY(), 2));
        for (int locationIdx : route) {
            distance += Math.sqrt(Math.pow(locations.get(locationIdx).getX() - locations.get(prevIdx).getX(), 2) +
                    Math.pow(locations.get(locationIdx).getY() - locations.get(prevIdx).getY(), 2));
            prevIdx = locationIdx;
        }

        // in order to calculate cycle's total distance properly,
        // last destination's distance to migros is added to total distance
        distance += Math.sqrt(Math.pow(locations.get(0).getX() - locations.get(prevIdx).getX(), 2) +
                Math.pow(locations.get(0).getY() - locations.get(prevIdx).getY(), 2));
        return distance;
    }

    /** This method calculates the distance of a given route. Ant Colony Method will use this overloaded method to calculate distances.
     * @param route
     * @param locations
     */
    private static double calculateRouteDistance(ArrayList<Integer> route, ArrayList<Location> locations) {
        double distance = 0;
        int prevIdx = 0;
        for (int locationIdx : route) {
            distance += Math.sqrt(Math.pow(locations.get(locationIdx).getX() - locations.get(prevIdx).getX(), 2) +
                    Math.pow(locations.get(locationIdx).getY() - locations.get(prevIdx).getY(), 2));
            prevIdx = locationIdx;
        }
        return distance;
    }

    /**
     * This method displays the shortest route using StdDraw library.
     * @param bestRoute
     * @param locations
     */
    public static void shortestPathDisplayer(Integer[] bestRoute, ArrayList<Location> locations){

        // Sets canvas size and x-y scales
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0.0, 1.0);
        StdDraw.setYscale(0.0, 1.0);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.enableDoubleBuffering();
        for(int i = 0; i < bestRoute.length - 1;i++) {
            // Draws the connections between nodes
            StdDraw.line(locations.get(bestRoute[i]).getX(), locations.get(bestRoute[i]).getY(), locations.get(bestRoute[i+1]).getX(), locations.get(bestRoute[i+1]).getY());
        }
        for(int destination: bestRoute){
            // if the node is migros, then it will be painted orange
            if (destination == 0) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            } else {
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            }
            // Draws the circles representing nodes
            StdDraw.filledCircle(locations.get(destination).getX(), locations.get(destination).getY(), 0.015);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));

            // writes the nodes' order
            StdDraw.text(locations.get(destination).getX(), locations.get(destination).getY(), "" + (destination + 1));
        }
        // Displays the canvas
        StdDraw.show();
    }
}