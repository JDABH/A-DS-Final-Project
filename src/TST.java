import com.sun.jdi.Value;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class TST<Value> {

    int n;  // size of the ternary search tree
    TSTNode root; // root node of the TST

    private static class TSTNode<Value> {
        private char character;
        private TSTNode<Value> left, middle, right;
        private Value value;
    }


    TST() {

        try {
            FileReader reader = new FileReader("stops.txt");

            Scanner scanner = new Scanner(reader).useDelimiter(",");

            // Skipping over the first line as that is just the headers for the data
            scanner.nextLine();

            // starting at key 2, as this can be used later on to print specific lines from the stops.txt file
            Integer value = 2;
            while(scanner.hasNextLine()) {
                // skip the first two values in every line as we are only looking for the stop name
                scanner.next();
                scanner.next();
                String stop = scanner.next();
                String[] nameSplit = stop.split(" ", 2);
                if ( nameSplit[0].equalsIgnoreCase("FLAGSTOP") || nameSplit[0].equalsIgnoreCase("WB")
                        || nameSplit[0].equalsIgnoreCase("NB") || nameSplit[0].equalsIgnoreCase("SB")
                            || nameSplit[0].equalsIgnoreCase("EB")) {
                    stop = nameSplit[1] + " " + nameSplit[0];
                }
                this.put(stop, (Value)value);
                scanner.nextLine();
                value++;
            }


        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        if ( key.length() == 0) {
            throw new IllegalArgumentException("key must have a length greater than one");
        }
        TSTNode<Value>x = get(root, key, 0);
        if (x == null) {
            return null;
        }
        return x.value;
    }

    // return the subtrie of the given key, if any
    private TSTNode<Value> get(TSTNode<Value> node, String key, int d){
        if ( node == null) {
            return null;
        }
        char c = key.charAt(d);
        if (c < node.character) {
            return get(node.left, key, d);
        }
        else if (c > node.character) {
            return get(node.right, key, d);
        }
        else if (d < key.length() - 1) {
            return get(node.middle,   key, d+1);
        }
        else {
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
        if ( c < node.character) {
            node.left = put(node.left, key, value, d);
        }
        else if ( c > node.character) {
            node.right = put(node.right, key, value, d);
        }
        else if (d < key.length() - 1) {
            node.middle = put(node.middle, key, value, d + 1);
        }
        else {
            node.value = value;
        }
        return node;

    }


    public Iterable<String> keyWithGivenPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("can't give a null argument to keysWithGivenPrefix()");
        }
        Queue<String> queue = new LinkedList<>();
        TSTNode node = get(root, prefix, 0);
        if ( node == null ) {
            return queue;
        }
        if ( node.value != null) {
            queue.add(prefix);
        }
        collect(node.middle, new StringBuilder(prefix), queue);
        return queue;
    }

    private void collect(TSTNode node, StringBuilder prefix, Queue<String> queue) {
        if ( node == null ) {
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
        TST<Integer> newTST = new TST<>();

        Scanner scanboy = new Scanner(System.in);

        System.out.println("What's the stop name?");

        String input = scanboy.next();

        System.out.println(newTST.get(input));

        scanboy.nextLine();
        System.out.println("Get that iterable baby");
        input = scanboy.next();

        //System.out.println(newTST.keyWithGivenPrefix(input));
        Iterable<String> collection = newTST.keyWithGivenPrefix(input);
        for(String s : collection) {
            System.out.println(s);
        }
    }


}
