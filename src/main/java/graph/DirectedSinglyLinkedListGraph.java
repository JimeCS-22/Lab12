package graph;

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

        if (!vertexList.contains(new Vertex(element)))
            vertexList.add(new Vertex(element)); // agrego un nuevo objeto vertice
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes [" + a + "] and [" + b + "] because one or both do not exist.");
        addRemoveVertexEdgeWeight(a, b, null, "addEdge"); // agrego la arista
    }

    private int indexOf(Object element) throws ListException {
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            if (util.Utility.compare(vertex.data, element) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes[" + a + "] and [" + b + "] to add weight.");
        addRemoveVertexEdgeWeight(a, b, weight, "addWeight");
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge with weight between vertexes [" + a + "] and [" + b + "] because one or both do not exist.");
        if (!containsEdge(a, b)) {
            addRemoveVertexEdgeWeight(a, b, weight, "addEdge");
        } else {
            addRemoveVertexEdgeWeight(a, b, weight, "addWeight");
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


        vertexList.remove(new Vertex(element));

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex otherVertex = (Vertex) vertexList.getNode(i).data;
            if (otherVertex != null && !otherVertex.edgesList.isEmpty()) {

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
        addRemoveVertexEdgeWeight(a, b, null, "remove");
    }

    private void addRemoveVertexEdgeWeight(Object a, Object b, Object weight, String action) throws ListException {
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            if (util.Utility.compare(vertex.data, a) == 0) {
                switch (action) {
                    case "addEdge":
                        vertex.edgesList.add(new EdgeWeight(b, weight));
                        break;
                    case "addWeight":
                        Node edgeNode = vertex.edgesList.getNode(new EdgeWeight(b, null));
                        if (edgeNode != null) {
                            ((EdgeWeight) edgeNode.getData()).setWeight(weight);
                        } else {

                            throw new ListException("Edge not found when trying to add/update weight.");
                        }
                        break;
                    case "remove":
                        if (!vertex.edgesList.isEmpty())
                            vertex.edgesList.remove(new EdgeWeight(b, weight));
                        break;
                }
                return;
            }
        }
    }

    // Recorrido en profundidad (DFS)
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);
        if (isEmpty()) {
            throw new GraphException("Directed Linked List Graph is Empty for DFS");
        }


        Vertex startVertex = (Vertex) vertexList.getNode(0).data;
        String info = startVertex.data + ", ";
        startVertex.setVisited(true);
        stack.clear();
        stack.push(0);

        while (!stack.isEmpty()) {
            int currentVertexIndex = (int) stack.top();
            int nextAdjacentIndex = getUnvisitedAdjacentVertex(currentVertexIndex);

            if (nextAdjacentIndex == -1) {
                stack.pop();
            } else {
                Vertex nextVertex = (Vertex) vertexList.getNode(nextAdjacentIndex).data;
                nextVertex.setVisited(true);
                info += nextVertex.data + ", ";
                stack.push(nextAdjacentIndex);
            }
        }
        return info;
    }

    // Recorrido en amplitud (BFS)
    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);
        if (isEmpty()) {
            throw new GraphException("Directed Linked List Graph is Empty for BFS");
        }


        Vertex startVertex = (Vertex) vertexList.getNode(0).data;
        String info = startVertex.data + ", ";
        startVertex.setVisited(true);
        queue.clear();
        queue.enQueue(0);

        while (!queue.isEmpty()) {
            int currentVertexIndex = (int) queue.deQueue();
            int nextAdjacentIndex;
            while ((nextAdjacentIndex = getUnvisitedAdjacentVertex(currentVertexIndex)) != -1) {
                Vertex nextVertex = (Vertex) vertexList.getNode(nextAdjacentIndex).data;
                nextVertex.setVisited(true);
                info += nextVertex.data + ", ";
                queue.enQueue(nextAdjacentIndex);
            }
        }
        return info;
    }


    private void setVisited(boolean value) throws ListException {
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = (Vertex) vertexList.getNode(i).data;
            vertex.setVisited(value);
        }
    }

    private int getUnvisitedAdjacentVertex(int currentVertexIndex) throws ListException {
        Vertex currentVertex = (Vertex) vertexList.getNode(currentVertexIndex).data;


        for (int i = 0; i < vertexList.size(); i++) {
            Vertex possibleAdjacentVertex = (Vertex) vertexList.getNode(i).data;


            if (util.Utility.compare(currentVertex.data, possibleAdjacentVertex.data) != 0 &&
                    currentVertex.edgesList.contains(new EdgeWeight(possibleAdjacentVertex.data, null)) &&
                    !possibleAdjacentVertex.isVisited()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        String result = "\n";
        try {
            if (isEmpty()) return result + "Graph is empty.";
            for (int i = 0; i < vertexList.size(); i++) {
                Vertex vertex = (Vertex) vertexList.getNode(i).data;
                result += "\nEl vertice en la posición " + i + " es: " + vertex.data + "\n";
                if (!vertex.edgesList.isEmpty()) {
                    result += "EDGES AND WEIGHTS: " + vertex.edgesList.toString() + "\n";
                }
            }
        } catch (ListException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}