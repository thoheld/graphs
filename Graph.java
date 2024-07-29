import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Graph.java

class Graph<Type> {
    // the matrix stores the edge information
    private boolean[][] matrix;

    // this stores the values being stored by this graph
    private Type[] values;

    // the size of the graph
    private int size;

    // set the graph as empty
    public Graph(int size) {
        matrix = new boolean[size][size];
        this.size = size;

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matrix[i][j] = false;
            }
        }

        // make space for the values (and ignore the cast warning)
        @SuppressWarnings("unchecked")
        Type[] values = (Type[]) new Object[size];
        this.values = values;
    }

    // lookup a node number by value
    public int lookup(Type value) {
        for (int i = 0; i < size; i++) {
            if (values[i] != null && values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    // insert an edge by index
    public void insertEdge(int from, int to) {
        matrix[from][to] = true;
    }

    // insert an edge by value
    public void insertEdge(Type from, Type to) {
        int fromIndex = lookup(from);
        int toIndex = lookup(to);
        insertEdge(fromIndex, toIndex);
    }

    // remove an edge
    public void removeEdge(int from, int to) {
        matrix[from][to] = false;
    }

    // return whether these are connected
    public boolean isEdge(int from, int to) {
        return matrix[from][to];
    }

    // add a node's data to the graph
    public void setValue(int node, Type value) {
        values[node] = value;
    }

    // return the size of the graph
    public int getSize() {
        return size;
    }

    // get the value of a node
    public Type getValue(int index) {
        return values[index];
    }
    
    
    
    // Thomas's Project Work
    
    /*
     * This function attempts to traverse the graph using topological sort. If it can be fully traversed,
     * the full path is printed. If it cannot be fully travered, it will print the path of the nodes that
     * can be reached, as well as specify which nodes are left over.
     * Returns true if the graph is fully traversed, and false otherwise.
     */
    public boolean DAG_tester() {
    	Type[] ordering = (Type[]) new Object[size];
    	Type[] incomplete = values.clone();
    	boolean independent = true;
    	boolean already = false;
    	
    	for (int i = 0; i < size; i++) { // loop for every node in graph
    		
    		for (int to = 0; to < size; to++) { // look for next available node
    			// skip nodes already in ordering or active
    			already = false;
    			for (Type node : ordering) {
    				if (node == values[to]) {
    					already = true;
    					break;
    				}
    			}
    			if (already) {
    				continue;
    			}
    			
    			// check if current node is independent
    			independent = true;
    			for (int from = 0; from < size; from++) {
    				if (matrix[from][to] == true) { // dependency found
    					independent = false;
    					break;
    				}
    			}
    			if (independent) { // independent node found
    				ordering[i] = values[to];
    				incomplete[to] = null;
    				for (int toto = 0; toto < size; toto++) {
        				matrix[to][toto] = false;
    				}
    				break;
    			}
    		}
    	}
    	
    	// check if graph was traversable (DAG)
    	boolean DAG = true;
    	if (size > 0) {
    		if (ordering[size-1] == null) { DAG = false; } // if some nodes not reached
    	}
    	
    	// print and return
    	if (DAG && size == 0) {
    		System.out.println("This graph had no tasks to complete.");
    		return true;
    	} else if (DAG) { // DAG
    		System.out.println("The tasks can be completed in the following order:");
    		for (int i = 0; i < size; i++) {
    			System.out.println(i+1 + ". " + ordering[i]);
    		}
    		return true;
    	} else { // not DAG
    		System.out.println("The tasks can be completed:");
    		int i = 0;
    		while (ordering[i] != null) {
    			System.out.println(i+1 + ". " + ordering[i]);
    			i++;
    		}
    		System.out.println("The following tasks cannot be completed:");
    		boolean first = true;
    		for (int j = 0; j < size; j++) {
    			if (incomplete[j] != null) {
    				if (!first) {
    					System.out.print(", ");
    				}
    				first = false;
    				System.out.print(incomplete[j]);
    			}
    		}
			System.out.println(".");
    		return false;
    	}
    }
    
    
    
    /*
     * This function takes a file name as its paramater and generates the graph described
     * in said file. It then returns the generated graph. Returns null if the file cannot be
     * found or if the file's format is incorrect.
     */
    public Graph<String> loadGraph(String fileName) {

    	try { // open file
			
		FileInputStream file = new FileInputStream(fileName); // open file
		Scanner s = new Scanner(file); // open scanner on file
		
		Graph<String> graph = new Graph<String>(Integer.parseInt(s.nextLine())); // create graph of specified size
		String relationship;
		String[] nodes;
		int nodeCount = 0;
		
		while (s.hasNextLine()) { // add specified relationships to graph
			
			relationship = s.nextLine();
			nodes = relationship.split(" -> ");
			String node;
			
			if (nodes.length == 2) {
				// add nodes if not already added
				for (int i = 0; i < 2; i++) {
					if (graph.lookup(nodes[i]) == -1) { // node not yet added
						graph.setValue(nodeCount, nodes[i]);
						nodeCount++;
					}
				}
				graph.insertEdge(nodes[1], nodes[0]);
				
			} else if (relationship.substring(0, 3).equals("-> ")){ // handle relationships with only one specified node
				node = relationship.substring(3, relationship.length());
				// add node if not already added
				if (graph.lookup(node) == -1) { // node not yet added
					graph.setValue(nodeCount, node);
					nodeCount++;
				}
			}
			
		}
		
		s.close(); // close scanner
		return graph;
		 
		} catch (FileNotFoundException e) { // file not found
			System.out.println("Something went wrong!");
			return null;
		}
    }
    
    
    
    /*
     * getter for matrix (for testing purposes)
     */
    public boolean[][] getMatrix() {
    	return matrix;
    }
}