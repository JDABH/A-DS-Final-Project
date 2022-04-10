import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph {

    ArrayList<ArrayList<IntersectionNode>> graph = new ArrayList<>();


    // Got this number from the number if lines in the stops.txt file excluding the first one with the headers
    int numberOfVertices = 8757;


    Graph() {

        try {
            FileReader reader = new FileReader("stops.txt");

            Scanner scanner = new Scanner(reader).useDelimiter(",");

            // skipping the first line as it just has the header values for the data
            scanner.nextLine();




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Graph grpah = new Graph();
    }
}