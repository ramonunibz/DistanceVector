import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    Set<String> stringNodeSet = new HashSet<>();
    Set<Node> nodeSet = new HashSet<>();
    List<Edge> edges = new ArrayList<>();

    public void createGraph(String topology) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(topology));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String node1;
        String node2;
        while (true) {
            assert scanner != null;
            if (!scanner.hasNext()) break;
            node1 = scanner.next();
            stringNodeSet.add(node1);
            node2 = scanner.next();
            stringNodeSet.add(node2);
            int cost = scanner.nextInt();
            edges.add(new Edge(node1, node2, cost));
            edges.add(new Edge(node2, node1, cost));
        }
    }

    public void initialize() {
        for (String node : stringNodeSet) {
            nodeSet.add(new Node(node));
        }
        for (Node node : nodeSet) {
            node.neighbours = getNeighbours(node.name);
        }
        for (Node node : nodeSet) {
            node.destinations = node.neighbours.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        for (Node source : nodeSet) {
            for (Node destination : nodeSet) {
                source.pathToNode.put(destination.name, new ArrayList<>());
                if (source.name.equals(destination.name)) {
                    source.destinations.put(source.name, 0);
                } else source.destinations.putIfAbsent(destination.name, Integer.MAX_VALUE - 100);
            }
        }
        for (Node source : nodeSet) {
            source.pathToNode.put(source.name, Collections.singletonList(source.name));
            source.neighbours.forEach((x, y) -> source.pathToNode.put(x, Collections.singletonList(x)));
        }
    }

    public Map<String, Integer> getNeighbours(String node) {
        Map<String, Integer> neighbours = new HashMap<>();
        for (Edge edge : edges) {
            if (edge.fromNode.equals(node)) {
                neighbours.put(edge.toNode, edge.cost);
            }
        }
        return neighbours;
    }

    public void sendInfo(String source) {
        for (Node node : nodeSet) {
            if (node.name.equals(source)) {
                for (String neighbour : node.neighbours.keySet()) {
                    for (Node neighbourNode : nodeSet) {
                        if (neighbourNode.name.equals(neighbour)) {
                            for (String destination : node.destinations.keySet()) {
                                if (node.destinations.get(destination) + node.neighbours.get(neighbour) < neighbourNode.destinations.get(destination)) {
                                    neighbourNode.destinations.put(destination, node.destinations.get(destination) + node.neighbours.get(neighbour));
                                    List<String> path = new ArrayList<>(neighbourNode.pathToNode.get(destination));
                                    path.add(source);
                                    neighbourNode.pathToNode.put(destination, path);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodeSet=" + nodeSet +
                ", edges=" + edges +
                '}';
    }
}
