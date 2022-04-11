import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Graph {

    Set<StopNode> stops = new HashSet<>();

    public void addStop(StopNode newStop) {
        stops.add(newStop);
    }

    Graph() {

        try {
            FileReader reader = new FileReader("stops.txt");

            Scanner scanner = new Scanner(reader).useDelimiter(",");


            // Skip the first line as that is just the headers for the values
            scanner.nextLine();
            int currentStop;

            while(scanner.hasNextLine()) {
                currentStop = scanner.nextInt();
                this.addStop(new StopNode(currentStop));
                scanner.nextLine();
            }

            // adding adjacent nodes and their costs from transfers.txt
            reader = new FileReader("transfers.txt");
            scanner = new Scanner(reader).useDelimiter(",|\\n");

            // skipping the first line
            scanner.nextLine();

            while(scanner.hasNextLine()) {
                int stopFrom = scanner.nextInt();
                int transferTo = scanner.nextInt();

                double cost;
                int transferType = scanner.nextInt();

                if(transferType == 0) {
                    cost = 0;
                }
                else {
                    cost = scanner.nextDouble();
                }
                StopNode stopA = getStopFromList(this.stops, stopFrom);
                StopNode stopB = getStopFromList(this.stops, transferTo);

                stopA.addDestination(stopB, cost);

                scanner.nextLine();
            }

            // adding adjacent nodes and their costs from stop_times.txt
            reader = new FileReader("stop_times.txt");
            scanner = new Scanner(reader).useDelimiter(",|\\n");
            FileReader secondReader = new FileReader("stop_times.txt");
            Scanner secondLine = new Scanner(secondReader).useDelimiter(",|\\n");

            // skip the first line of the first scanner and the first two for the second
            scanner.nextLine();
            secondLine.nextLine();
            secondLine.nextLine();



            while(secondLine.hasNextLine()) {
                // checking if the trip IDs are equal
                if ( scanner.nextInt() == secondLine.nextInt()) {

                    // skip the times on each line
                    scanner.next();
                    scanner.next();
                    secondLine.next();
                    secondLine.next();

                    int stopFrom = scanner.nextInt();
                    int stopTo = secondLine.nextInt();
                    secondLine.next();
                    secondLine.next();
                    secondLine.next();
                    secondLine.next();
                    double cost = secondLine.nextDouble();

                    StopNode stopA = getStopFromList(this.stops, stopFrom);
                    StopNode stopB = getStopFromList(this.stops, stopTo);
                    stopA.addDestination(stopB, cost);

                }
                scanner.nextLine();
                secondLine.nextLine();
            }



        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public StopNode getStopFromList(Set<StopNode> stops, int stopName) {

        for (StopNode stop : stops) {
            if (stop.stopId == stopName) {
                return stop;
            }
        }

        return null;
    }

    public static Graph calculateDijkstra(Graph graph, StopNode source) {
        if ( source == null) {
            System.out.println("This stop doesn't exist");
            return graph;
        }
        source.setCost(0);

        Set<StopNode> settledNodes = new HashSet<>();
        Set<StopNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            StopNode currentNode = getLowestDistanceNode(unsettledNodes);

            unsettledNodes.remove(currentNode);
            for (Map.Entry<StopNode, Double> adjacencyPair: currentNode.adjacentStops.entrySet()) {
                StopNode adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue();
                if(!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static StopNode getLowestDistanceNode(Set <StopNode> unsettledNodes) {
        StopNode lowestDistanceNode = null;
        double lowestDistance = Double.POSITIVE_INFINITY;
        for(StopNode node : unsettledNodes) {
            double nodeDistance = node.cost;
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(StopNode evalNode, double edgeWeight, StopNode source) {
        double sourceCost = source.cost;
        if (sourceCost + edgeWeight < evalNode.cost) {
            evalNode.setCost(sourceCost + edgeWeight);
            LinkedList<StopNode> shortestPath = new LinkedList<>(source.shortest);
            shortestPath.add(source);
            evalNode.shortest = shortestPath;
        }
    }

    public double getCost(StopNode destNode) {
        if( stops.contains(destNode)) {
            return destNode.cost;
        }
        else {
            System.out.println("That stop isn't in the graph");
            return Double.POSITIVE_INFINITY;
        }
    }

    public void printPath(StopNode destination) {
        if (destination.shortest.size() > 0) {
            for (int i = 0; i < destination.shortest.size() - 1; i++) {
                System.out.print(destination.shortest.get(i).stopId + " to " + destination.shortest.get(i + 1).stopId);
                System.out.printf(", Cost: %.4f\n", (destination.shortest.get(i + 1).cost - destination.shortest.get(i).cost));
            }
            System.out.print(destination.shortest.get(destination.shortest.size() - 1).stopId + " to " + destination.stopId);
            System.out.printf(", Cost: %.4f\n", (destination.cost - destination.shortest.get(destination.shortest.size() - 1).cost));

            System.out.printf("Total Cost: %.4f\n", this.getCost(destination));
        }
        else {
            System.out.println("There is no path between these stops");
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Sauce Node?");
        int sauceNode = scanner.nextInt();
        System.out.println("Where to chief?");
        int destinyNode = scanner.nextInt();

        StopNode startNode = graph.getStopFromList(graph.stops, sauceNode);

        StopNode endNode = graph.getStopFromList(graph.stops, destinyNode);

        graph = calculateDijkstra(graph, startNode);


        graph.printPath(endNode);

    }


}