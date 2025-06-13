package domain;

import domain.list.ListException;
import domain.list.Node; // Ensure Node is imported, assuming it's the generic Node
import domain.list.SinglyLinkedList;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;

public class DirectedSinglyLinkedListGraph implements Graph {
    private SinglyLinkedList vertexList; // lista enlazada de vértices

    // para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    // Constructor
    public DirectedSinglyLinkedListGraph() {
        this.vertexList = new SinglyLinkedList();
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
    }

    @Override
    public int size() throws ListException {
        return vertexList.size();
    }

    @Override
    public void clear() {
        vertexList.clear();
    }

    @Override
    public boolean isEmpty() {
        return vertexList.isEmpty();
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Directed Linked List Graph is Empty");
        return indexOf(element) != -1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Directed Linked List Graph is Empty");
        int index = indexOf(a); // buscamos el índice del elemento en la lista enlazada
        if (index == -1) return false;
        Vertex vertex = (Vertex) vertexList.getNode(index).data;
        return vertex != null && !vertex.edgesList.isEmpty()
                && vertex.edgesList.contains(new EdgeWeight(b, null));
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        // Corrected logic: add a new Vertex object, and ensure uniqueness by checking for a Vertex containing the element.
        // It's crucial that SinglyLinkedList.contains and Vertex.equals handle comparing just the 'data' part.
        if (!vertexList.contains(new Vertex(element))) // Pass a Vertex object for correct comparison if Vertex.equals is based on data
            vertexList.add(new Vertex(element)); // agrego un nuevo objeto vertice
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes [" + a + "] and [" + b + "] because one or both do not exist.");
        addRemoveVertexEdgeWeight(a, b, null, "addEdge"); // agrego la arista
    }

    // *** CRITICAL FIX: INDEXING LOOP IS NOW CONSISTENTLY 0-BASED ***
    private int indexOf(Object element) throws ListException {
        // The loop must go from 0 up to, but not including, vertexList.size()
        for (int i = 0; i < vertexList.size(); i++) { // Corrected: i < vertexList.size()
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            if (util.Utility.compare(vertex.data, element) == 0) {
                return i; // Found the vertex at index 'i'
            }
        }
        return -1; // Element not found
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes[" + a + "] and [" + b + "] to add weight.");
        addRemoveVertexEdgeWeight(a, b, weight, "addWeight"); // actualiza el peso de la arista existente
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge with weight between vertexes [" + a + "] and [" + b + "] because one or both do not exist.");
        if (!containsEdge(a, b)) { // Only add the edge if it doesn't exist
            addRemoveVertexEdgeWeight(a, b, weight, "addEdge"); // adds the edge with weight
        } else {
            addRemoveVertexEdgeWeight(a, b, weight, "addWeight"); // updates the weight of existing edge
        }
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("Directed Linked List Graph is Empty");
        }

        int vertexToRemoveIndex = indexOf(element);
        if (vertexToRemoveIndex == -1) {
            throw new GraphException("Vertex [" + element + "] not found in the graph.");
        }

        // 1. Remove the vertex from the main vertex list
        // It's crucial that Vertex.equals() correctly identifies a Vertex object by its 'data'.
        vertexList.remove(new Vertex(element));

        // 2. Remove all edges that pointed TO the removed vertex
        // We must iterate over all remaining vertices in the graph's main list
        // and check their edgesList for any edge whose destination is 'element'.
        // *** CRITICAL FIX: LOOP IS NOW CONSISTENTLY 0-BASED ***
        for (int i = 0; i < vertexList.size(); i++) { // Loop over the updated (potentially smaller) vertex list
            Vertex otherVertex = (Vertex) vertexList.getNode(i).data;
            if (otherVertex != null && !otherVertex.edgesList.isEmpty()) {
                // Create an EdgeWeight with 'element' as destination for correct removal
                // (assuming EdgeWeight.equals() and Utility.compare() work based on 'edge' property).
                otherVertex.edgesList.remove(new EdgeWeight(element, null));
            }
        }
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot remove edge: one or both vertexes [" + a + "] and [" + b + "] do not exist.");
        if (!containsEdge(a, b))
            throw new GraphException("Cannot remove edge: there is no edge between vertexes [" + a + "] and [" + b + "].");
        addRemoveVertexEdgeWeight(a, b, null, "remove"); // suppresses the edge
    }

    // Helper method to add, update weight, or remove edges
    private void addRemoveVertexEdgeWeight(Object a, Object b, Object weight, String action) throws ListException {
        // *** CRITICAL FIX: LOOP IS NOW CONSISTENTLY 0-BASED ***
        for (int i = 0; i < vertexList.size(); i++) { // Loop condition: i < size()
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            if (util.Utility.compare(vertex.data, a) == 0) { // If we find vertex 'a'
                switch (action) {
                    case "addEdge":
                        vertex.edgesList.add(new EdgeWeight(b, weight));
                        break;
                    case "addWeight":
                        // Find the edge node that connects 'a' to 'b' and update its weight
                        Node edgeNode = vertex.edgesList.getNode(new EdgeWeight(b, null)); // Search for the edge by destination 'b'
                        if (edgeNode != null) {
                            // Assuming node's data is an EdgeWeight object, update its weight
                            ((EdgeWeight) edgeNode.getData()).setWeight(weight);
                        } else {
                            // This case indicates an internal logic error if containsEdge was true but getNode failed.
                            // Or, if EdgeWeight.equals requires weight for comparison and it was null.
                            // For robustness, consider how EdgeWeight.equals behaves with null weight.
                            throw new ListException("Edge not found when trying to add/update weight.");
                        }
                        break;
                    case "remove":
                        if (!vertex.edgesList.isEmpty())
                            vertex.edgesList.remove(new EdgeWeight(b, weight)); // Use an EdgeWeight for comparison during removal
                        break;
                }
                return; // Once action is performed on vertex 'a', we can exit.
            }
        }
    }

    // Recorrido en profundidad (DFS)
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false); // Mark all vertices as unvisited
        if (isEmpty()) {
            throw new GraphException("Directed Linked List Graph is Empty for DFS");
        }

        // *** CRITICAL FIX: STARTING NODE INDEX MUST BE 0 ***
        // Start traversal from the first vertex (index 0)
        Vertex startVertex = (Vertex) vertexList.getNode(0).data;
        String info = startVertex.data + ", "; // Only show vertex data for clarity
        startVertex.setVisited(true); // Mark as visited
        stack.clear();
        stack.push(0); // Push the index of the starting vertex (0)

        while (!stack.isEmpty()) {
            int currentVertexIndex = (int) stack.top(); // Get the index of the vertex at the stack top
            int nextAdjacentIndex = getUnvisitedAdjacentVertex(currentVertexIndex); // Find an unvisited adjacent vertex

            if (nextAdjacentIndex == -1) { // No unvisited adjacent found from current vertex
                stack.pop(); // Pop current vertex
            } else {
                Vertex nextVertex = (Vertex) vertexList.getNode(nextAdjacentIndex).data;
                nextVertex.setVisited(true); // Mark as visited
                info += nextVertex.data + ", "; // Add to info string
                stack.push(nextAdjacentIndex); // Push the index of the newly visited vertex
            }
        }
        return info;
    }

    // Recorrido en amplitud (BFS)
    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false); // Mark all vertices as unvisited
        if (isEmpty()) {
            throw new GraphException("Directed Linked List Graph is Empty for BFS");
        }

        // *** CRITICAL FIX: STARTING NODE INDEX MUST BE 0 ***
        // Start traversal from the first vertex (index 0)
        Vertex startVertex = (Vertex) vertexList.getNode(0).data;
        String info = startVertex.data + ", "; // Only show vertex data
        startVertex.setVisited(true); // Mark as visited
        queue.clear();
        queue.enQueue(0); // Enqueue the index of the starting vertex (0)

        while (!queue.isEmpty()) {
            int currentVertexIndex = (int) queue.deQueue(); // Dequeue the index of the current vertex
            int nextAdjacentIndex;
            // While there are unvisited neighbors from the current vertex
            while ((nextAdjacentIndex = getUnvisitedAdjacentVertex(currentVertexIndex)) != -1) {
                Vertex nextVertex = (Vertex) vertexList.getNode(nextAdjacentIndex).data;
                nextVertex.setVisited(true); // Mark as visited
                info += nextVertex.data + ", "; // Add to info string
                queue.enQueue(nextAdjacentIndex); // Enqueue the index of the newly visited vertex
            }
        }
        return info;
    }

    // Marks all vertices as visited or unvisited
    // *** CRITICAL FIX: LOOP IS NOW CONSISTENTLY 0-BASED ***
    private void setVisited(boolean value) throws ListException {
        for (int i = 0; i < vertexList.size(); i++) { // Loop condition: i < size()
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            vertex.setVisited(value);
        }
    }

    // Gets the index of an unvisited adjacent vertex from the vertex at 'currentVertexIndex'
    private int getUnvisitedAdjacentVertex(int currentVertexIndex) throws ListException {
        Vertex currentVertex = (Vertex) vertexList.getNode(currentVertexIndex).data;

        // Iterate over all vertices in the graph to find an unvisited adjacent one
        // *** CRITICAL FIX: LOOP IS NOW CONSISTENTLY 0-BASED ***
        for (int i = 0; i < vertexList.size(); i++) { // Loop condition: i < size()
            Vertex possibleAdjacentVertex = (Vertex) vertexList.getNode(i).data;

            // Conditions for an unvisited adjacent vertex:
            // 1. Not the same vertex (unless self-loops are explicitly desired for traversals, which is rare)
            // 2. The 'currentVertex' has an edge pointing to 'possibleAdjacentVertex'.
            // 3. 'possibleAdjacentVertex' has not been visited.
            if (util.Utility.compare(currentVertex.data, possibleAdjacentVertex.data) != 0 && // Not the same vertex
                    currentVertex.edgesList.contains(new EdgeWeight(possibleAdjacentVertex.data, null)) && // currentVertex points to possibleAdjacentVertex
                    !possibleAdjacentVertex.isVisited()) { // possibleAdjacentVertex has not been visited
                return i; // Returns the 0-based index of the unvisited neighbor
            }
        }
        return -1; // No unvisited neighbors found
    }

    @Override
    public String toString() {
        String result = "Directed Linked List Graph Content...\n";
        try {
            if (isEmpty()) return result + "Graph is empty.";
            // *** CRITICAL FIX: LOOP IS NOW CONSISTENTLY 0-BASED FOR PRINTING ***
            for (int i = 0; i < vertexList.size(); i++) { // Loop condition: i < size()
                Vertex vertex = (Vertex) vertexList.getNode(i).data;
                result += "\nThe vertex in the position " + i + " is: " + vertex.data + "\n"; // Show 0-based index 'i'
                if (!vertex.edgesList.isEmpty()) {
                    result += "........EDGES AND WEIGHTS: " + vertex.edgesList.toString() + "\n";
                }
            }
        } catch (ListException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}