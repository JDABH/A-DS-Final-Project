import javax.swing.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.DoubleToIntFunction;

public class mainProgram {


    public static void main(String[] args) {

        final String tableOfContents =
                "|========================================================================|\n" +
                "| Input |                  Request                                       |\n" +
                "|=======|================================================================|\n" +
                "|   1   | Get the shortest path between 2 bus stops, and their costs     |\n" +
                "|=======|================================================================|\n" +
                "|   2   | Search for bus stops by their name or the first few characters |\n" +
                "|=======|================================================================|\n" +
                "|   3   | Search for all bus trips with a given arrival time             |\n" +
                "|=======|================================================================|\n" +
                "| quit  | Exit the program                                               |\n" +
                "|=======|================================================================|\n";

        // objects for each input that can be called later
        SearchByTime<Integer> timeSearch = null;

        Graph tripGraph = null;

        TST<Integer> busStopSearch = null;



        System.out.println("Welcome to the Vancouver Public Transport System!");

        Scanner scanner = new Scanner(System.in);

        boolean quit = false;

        // booleans set to prevent remaking the graph objects if the user goes back into a program
        boolean graphMade = false;
        boolean timeSearchMade = false;
        boolean stopSearchMade = false;

        while(!quit) {
            System.out.println(tableOfContents);

            String input = scanner.nextLine();

            if(input.equalsIgnoreCase("quit")) {
                quit = true;
            }
            else if(input.equalsIgnoreCase("1")) {
                if (!graphMade) {
                    graphMade = true;

                    tripGraph = new Graph();
                }
                boolean checkingNodes = true;

                while(checkingNodes) {
                    System.out.println("Which stop is the start of your journey?");
                    if (scanner.hasNextInt()) {

                        int sourceNode = scanner.nextInt();

                        System.out.println("Which stop is your destination?");

                        boolean getSecondNode = false;
                        while (!getSecondNode) {
                            if (scanner.hasNextInt()) {

                                getSecondNode = true;
                                int destNode = scanner.nextInt();

                                if (destNode != sourceNode) {
                                    StopNode startNode = tripGraph.getStopFromList(tripGraph.stops, sourceNode);

                                    StopNode endNode = tripGraph.getStopFromList(tripGraph.stops, destNode);

                                    tripGraph = Graph.calculateDijkstra(tripGraph, startNode);

                                    if ( tripGraph.getCost(endNode) == Double.POSITIVE_INFINITY) {
                                        System.out.println("No path exists between these stops");
                                    }
                                    else {
                                        tripGraph.printPath(endNode);
                                    }
                                }
                                else {
                                    System.out.println("Error - please give two different stops");
                                }
                            } else {
                                System.out.println("Error - please give a postive integer value");
                                scanner.nextLine();
                            }
                        }


                        boolean continueQuery = true;
                        scanner.nextLine();
                        while(continueQuery) {
                            System.out.println("Would you like to query again? Yes/No");
                            input = scanner.nextLine();
                            if (input.equalsIgnoreCase("no")) {
                                checkingNodes = false;
                                continueQuery = false;
                            } else if (input.equalsIgnoreCase("yes")) {
                                System.out.println("Glad to hear!");
                                continueQuery = false;
                            } else {
                                System.out.println("Error - please type yes or no");
                            }
                        }
                    } else {
                        System.out.println("Error - please give a positive integer value");
                        scanner.nextLine();
                    }
                }
            }
            else if(input.equalsIgnoreCase("2")) {
                if (!stopSearchMade) {
                    stopSearchMade = true;

                    busStopSearch = new TST<>();
                }

                boolean searchingStops = true;

                while(searchingStops) {
                    System.out.println("Which stop(s) are you looking for?");

                    if (scanner.hasNext()) {
                        input = scanner.nextLine();

                        Iterable<String> collection = busStopSearch.keyWithGivenPrefix(input.toUpperCase(Locale.ROOT));
                        int counter = 0;
                        for (String s : collection) {
                            System.out.println(s);
                            counter++;
                        }
                        if (counter == 0) {
                            System.out.println("There are no stops with that name");
                        }

                    }
                    else {
                        System.out.println("Error - please give a string value name");
                    }

                    boolean continueQuery = true;

                    while(continueQuery) {
                        System.out.println("Would you like to query again? Yes/No");
                        input = scanner.nextLine();
                        if (input.equalsIgnoreCase("no")) {
                            searchingStops = false;
                            continueQuery = false;
                        } else if (input.equalsIgnoreCase("yes")) {
                            System.out.println("Glad to hear!");
                            continueQuery = false;
                        } else {
                            System.out.println("Error - please type yes or no");
                        }
                    }
                }
            }
            else if (input.equalsIgnoreCase("3")) {
                if(!timeSearchMade) {
                    timeSearchMade = true;

                    timeSearch = new SearchByTime<>();
                }

                boolean searchingTimes = true;

                while (searchingTimes) {
                    System.out.println("Which arrival times are you looking for? (please give in hh:mm:ss format)");

                    input = scanner.nextLine();

                    if (timeSearch.isValidTime(input)) {
                        Iterable<String> collection = timeSearch.keyWithGivenPrefix(input);

                        System.out.println("All trips with arrival time " + input);

                        ArrayList<String[]> unsortedString = new ArrayList<>();


                        for (String s : collection) {
                            unsortedString.add(s.split(" "));
                        }

                        if(unsortedString.size() > 0) {
                            ArrayList<String> sortedString = timeSearch.getSortedStrings(unsortedString, collection);

                            for (String sort : sortedString) {
                                String[] arr = sort.split(" ", 2);
                                System.out.println(arr[1]);
                            }
                        }
                        else {
                            System.out.println("There are no trip with that arrival time");
                        }


                        boolean continueQuery = true;
                        while(continueQuery) {
                            System.out.println("Would you like to query again? Yes/No");
                            input = scanner.nextLine();
                            if (input.equalsIgnoreCase("no")) {
                                searchingTimes = false;
                                continueQuery = false;
                            } else if (input.equalsIgnoreCase("yes")) {
                                System.out.println("Glad to hear!");
                                continueQuery = false;
                            } else {
                                System.out.println("Error - please type yes or no");
                            }
                        }
                    }
                    else {
                        System.out.println("Error - please give a valid time format");
                    }
                }
            }
            else {
                System.out.println("Error - Please give a valid input.");
            }
        }
        scanner.close();

    }
}
