import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DistanceVector {
    public static void main(String[] args) {
        String topology = args[0];
        String messages = args[1];
        start(topology, messages);
    }

    public static void start(String topology, String messages) {
        Graph graph = new Graph();
        graph.createGraph(topology);
        graph.initialize();
        computeDistanceVector(messages, graph);
        for (Node node : graph.nodeSet) {
            writeInto(new File(node.name + ".txt"), node);
        }
    }

    public static void computeDistanceVector(String messages, Graph graph) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(messages));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            assert scanner != null;
            if (!scanner.hasNext()) break;
            graph.sendInfo(scanner.next());
            calculateNextHop(graph);
            System.out.println("\nInfo about the sent information: ");
            for (Node node : graph.nodeSet) {
                System.out.println("\nRouter " + node.name + ":");
                System.out.println("Dest , Next hop , Cost");
                node.nextHop.forEach((x, y) -> {
                    if (!y.equals("-")) {
                        System.out.println(x + "\t" + y + "\t" + node.destinations.get(x));
                    }
                });
                writeInto(new File(node.name + ".txt"), node);
            }
        }
    }

    public static void calculateNextHop(Graph graph) {
        for (Node node : graph.nodeSet) {
            for (String destination : node.destinations.keySet()) {
                node.nextHop.put(destination, calculateHop(node, destination));
            }
        }
    }

    public static String calculateHop(Node node, String destination) {
        if (node.pathToNode.get(destination).isEmpty()) {
            return "-";
        }
        if (node.pathToNode.get(destination).get(node.pathToNode.get(destination).size() - 1).equals(node.name)) {
            return "direct";
        }
        return node.pathToNode.get(destination).get(node.pathToNode.get(destination).size() - 1);
    }

    public static void writeInto(File file, Node node) {
        try (FileWriter writer = new FileWriter(file)) {
            node.nextHop.forEach((x, y) -> {
                try {
                    if (!y.equals("-")) {
                        writer.write(x + " " + y + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
