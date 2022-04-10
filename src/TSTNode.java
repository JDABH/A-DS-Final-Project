import com.sun.jdi.Value;

public class TSTNode<Value>{

    char character;    // character of the node
    Value value;         // value associated with the string

    TSTNode left;      // left subtrie
    TSTNode middle;    // middle subtrie
    TSTNode right;     // right subtrie
}