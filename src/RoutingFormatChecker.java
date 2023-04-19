import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class RoutingFormatChecker {

    public final static String topologyLineRegex = "[A-Z] [A-Z] [1-9][0-9]*";
    public final static String routingLineRegex = "[A-Z] ([A-Z]|direct)";
    public final static String messagesLineRegex = "^$|[A-Z]( [A-Z])*";

    private static String getRoutingTableFile(Character router) {
        return router + ".txt";
    }

    private static void lineError(int line, String filename) {
        System.out.println("Line " + line + " in " + filename + " doesn't match the proper format");
    }

    private static void checkTopologyFile(String filename, Set<Character> nodes) {
        Pattern pattern = Pattern.compile(topologyLineRegex);
        Matcher matcher;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            try {
                int lineNumber = 1;
                line = br.readLine();
                while (line != null) {
                    matcher = pattern.matcher(line);
                    if (!matcher.matches()) {
                        lineError(lineNumber, filename);
                        exit(1);
                    }
                    String[] parts = line.split(" ");
                    char node1, node2;
                    node1 = parts[0].charAt(0);
                    node2 = parts[1].charAt(0);
                    nodes.add(node1);
                    nodes.add(node2);
                    line = br.readLine();
                    lineNumber++;
                }
            } finally {
                br.close();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found: " + filename);
            exit(1);
        } catch (IOException ioex) {
            System.err.println(ioex);
            exit(1);
        }
    }

    private static void checkRoutingTables(Set<Character> nodes) {
        Pattern pattern = Pattern.compile(routingLineRegex);
        Matcher matcher;
        Iterator<Character> i = nodes.iterator();
        while (i.hasNext()) {
            char router = i.next();
            String filename = getRoutingTableFile(router);
            System.out.println("Searching for routing table file of router " + router + " (" + filename + ")");
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;
                Set<Character> destinations = new HashSet<Character>();
                System.out.println("Checking routing table file content");
                try {
                    int lineNumber = 1;
                    while ((line = br.readLine()) != null) {
                        matcher = pattern.matcher(line);
                        if (!matcher.matches()) {
                            lineError(lineNumber, filename);
                            exit(1);
                        }
                        String[] parts = line.split(" ");
                        char destination;
                        String nextHop;
                        destination = parts[0].charAt(0);
                        nextHop = parts[1];
                        destinations.add(destination);
                        if (!nextHop.equals("direct"))
                            destinations.add(nextHop.charAt(0));
                        lineNumber++;
                    }
                    if (!nodes.containsAll(destinations)) {
                        System.out.println("Some destinations or next hops in the routing table don't exist in the topology");
                        exit(1);
                    }
                } catch (IOException e) {
                    System.out.println("Error while reading " + filename);
                } finally {
                    br.close();
                }
            } catch (FileNotFoundException e) {
                System.err.println("Impossible to find routing table file: " + filename);
                exit(1);
            } catch (IOException ioe) {
                System.err.println("Impossible to read routing table file: " + filename);
                exit(1);
            }
        }
    }

    private static void checkMessagesFile(String filename, Set<Character> nodes) {

        Pattern pattern = Pattern.compile(messagesLineRegex);
        Matcher matcher;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            try {
                line = br.readLine();
                matcher = pattern.matcher(line);
                if (!matcher.matches()) {
                    System.out.println("The format of the messages file is wrong");
                    exit(1);
                }
                if (!line.isEmpty()) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].length() != 1) {
                            System.out.println("Invalid router name: " + parts[i]);
                            exit(1);
                        }
                        char router = parts[i].charAt(0);
                        if (!nodes.contains(router)) {
                            System.out.println("Router " + router + " does not exist in topology file");
                            exit(1);
                        }
                    }
                }
                if (br.readLine() != null) {
                    System.out.println("This file should contain one line only");
                    exit(1);
                }
            } catch (IOException e) {
                System.out.println("Error while reading " + filename);
            } finally {
                br.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find messages file: " + filename);
            exit(1);
        } catch (IOException ioe) {
            System.err.println("Impossible to read messages file: " + filename);
            exit(1);
        }

    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java RoutingFormatChecker <topology file> [messages file]");
            exit(1);
        }

        String topology = args[0];
        String messages = null;
        if (args.length == 2)
            messages = args[1];
        Set<Character> nodes = new HashSet<Character>();

        System.out.println("Checking topology file " + topology);
        checkTopologyFile(topology, nodes);
        System.out.println();

        System.out.println("Checking routing tables");
        checkRoutingTables(nodes);
        System.out.println();

        if (messages != null) {
            System.out.println("Checking messages file " + messages);
            checkMessagesFile(messages, nodes);
            System.out.println();
        }

        System.out.println("All checks passed!");

    }
}