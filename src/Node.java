import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    String name;
    Map<String, Integer> neighbours = new HashMap<>();
    Map<String, Integer> destinations = new HashMap<>();
    Map<String, List<String>> pathToNode = new HashMap<>();
    Map<String, String> nextHop = new HashMap<>();

    public Node(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", neighbours=" + neighbours +
                ", destinations=" + destinations +
                ", pathToNode=" + pathToNode +
                ", nextHop=" + nextHop +
                '}';
    }
}