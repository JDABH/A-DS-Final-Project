import javax.lang.model.type.ArrayType;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


// Very much just a redo of the TST for searching for stops, but this is for the arrival times of trips
// I figured while it'd be more effective and efficient to use the same TST code, this is cleaner and easier
public class SearchByTime<Value> {

    int n;  // size of the ternary search tree
    TSTNode root; // root node of the TST

    private static class TimeTSTNode<Value> {
        private char character;
        private TSTNode<Value> left, middle, right;
        private Value value;
    }


    SearchByTime() {

        try {
            FileReader reader = new FileReader("stop_times.txt");

            Scanner scanner = new Scanner(reader).useDelimiter(",|\\n");

            // Skipping over the first line as that is just the headers for the data
            scanner.nextLine();

            Integer value = 0;
            while (scanner.hasNextLine()) {
                // skip the first two values in every line as we are only looking for the stop name
                String tripID = ("Trip ID: " + scanner.next());
                String arrivalTime = (scanner.next().replace(" ", ""));
                // ignore any invalid times that are below 0 in any unit, or above 23 hours, 59 minutes or 59 seconds
                if ( isValidTime(arrivalTime)) {
                    String departureTime = (" Departure Time: " + scanner.next().replace(" ", "") + ", ");
                    String stopID = ("Stop ID: " + scanner.next() + ", ");
                    // skipping the stop headsign column as there doesn't seem to be any value
                    scanner.next();

                    String trip = arrivalTime + departureTime + stopID + tripID;
                    this.put(trip, (Value) value);
                }
                scanner.nextLine();
                value++;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidTime(String timeToCheck) {
        String[] intChecks = timeToCheck.split(":");
        // can't be more than three units of time in this format, hours:minutes:seconds
        if (intChecks.length != 3) {
            return false;
        }
        int[] stringToInt = new int[intChecks.length];
        for (int i = 0; i < stringToInt.length; i++) {
            try {
                stringToInt[i] = Integer.parseInt(intChecks[i]);
            }
            catch (NumberFormatException nfe) {
                return false;
            }

        }
        if ( stringToInt[0] > 23 || stringToInt[1] > 59 || stringToInt[2] > 59) {
            return false;
        }

        return true;
    }

    // get the size, fairly straightforward so far
    public int getSize() {
        return n;
    }

    public boolean containsKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }

    public Value get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("called get() with an invalid argument");
        }
        if (key.length() == 0) {
            throw new IllegalArgumentException("key must have a length greater than one");
        }
        TSTNode<Value> x = get(root, key, 0);
        if (x == null) {
            return null;
        }
        return x.value;
    }

    // return the subtrie of the given key, if any
    private TSTNode<Value> get(TSTNode<Value> node, String key, int d) {
        if (node == null) {
            return null;
        }
        char c = key.charAt(d);
        if (c < node.character) {
            return get(node.left, key, d);
        } else if (c > node.character) {
            return get(node.right, key, d);
        } else if (d < key.length() - 1) {
            return get(node.middle, key, d + 1);
        } else {
            return node;
        }
    }

    public void put(String key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("can't call put() with a null key");
        }
        if (!containsKey(key)) n++;
        else if (val == null) {
            n--;
        }
        root = put(root, key, val, 0);
    }

    private TSTNode<Value> put(TSTNode<Value> node, String key, Value value, int d) {
        char c = key.charAt(d);
        if (node == null) {
            node = new TSTNode<Value>();
            node.character = c;
        }
        if (c < node.character) {
            node.left = put(node.left, key, value, d);
        } else if (c > node.character) {
            node.right = put(node.right, key, value, d);
        } else if (d < key.length() - 1) {
            node.middle = put(node.middle, key, value, d + 1);
        } else {
            node.value = value;
        }
        return node;

    }

    public ArrayList<String> getSortedStrings(ArrayList<String[]> unsortedList, Iterable<String> collection) {
        ArrayList<String> sortedString = new ArrayList<>();
        for ( int i = 0; i < unsortedList.size(); i++) {
            int minTripID = Integer.MAX_VALUE;
            int minTripIndex = 0;
            for(int j = i; j < unsortedList.size(); j++) {
                if (minTripID > Integer.parseInt(unsortedList.get(j)[unsortedList.get(j).length-1])) {
                    minTripID = Integer.parseInt(unsortedList.get(j)[unsortedList.get(j).length-1]);
                    minTripIndex = j;
                }
            }
            sortedString.add(String.join(" ", unsortedList.get(minTripIndex)));
            unsortedList.remove(minTripIndex);
        }
        return sortedString;
    }

    public Iterable<String> keyWithGivenPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("can't give a null argument to keysWithGivenPrefix()");
        }
        Queue<String> queue = new LinkedList<>();
        TSTNode node = get(root, prefix, 0);
        if (node == null) {
            return queue;
        }
        if (node.value != null) {
            queue.add(prefix);
        }
        collect(node.middle, new StringBuilder(prefix), queue);
        return queue;
    }

    private void collect(TSTNode node, StringBuilder prefix, Queue<String> queue) {
        if (node == null) {
            return;
        }
        collect(node.left, prefix, queue);
        if (node.value != null) {
            queue.add(prefix.toString() + node.character);
        }
        collect(node.middle, prefix.append(node.character), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(node.right, prefix, queue);
    }

    public static void main(String[] args) {
        SearchByTime<Integer> timeSearch = new SearchByTime<>();

        Scanner scannable = new Scanner(System.in);

        System.out.println("What time is it?: ");

        String inputTime = scannable.next();

        Iterable<String> collection = timeSearch.keyWithGivenPrefix(inputTime);

        System.out.println("All trips with arrival time " + inputTime);

        ArrayList<String[]> unsortedString = new ArrayList<>();
        for(String s : collection) {
            unsortedString.add(s.split(" "));
        }

        ArrayList<String> sortedString = timeSearch.getSortedStrings(unsortedString, collection);

        for (String sort : sortedString) {
            String[] arr = sort.split(" ", 2);
            System.out.println(arr[1]);
        }
    }
}
