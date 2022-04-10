import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StopNode {

    int stopId;
    double cost = Double.POSITIVE_INFINITY;

    List<StopNode> shortest = new LinkedList<>();

    Map<StopNode, Double> adjacentStops = new HashMap<>();

    public void addDestination(StopNode newStop, double cost) {
        adjacentStops.put(newStop, cost);
    }

    public StopNode(int stopId){
        this.stopId = stopId;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

}
