package graph;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;
import util.Utility;

import java.util.*;

public class SinglyLinkedListGraph implements Graph {
    private SinglyLinkedList vertexList; //lista enlazada de vértices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public SinglyLinkedListGraph() {
        this.vertexList = new SinglyLinkedList();
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
    }

    public SinglyLinkedList getVertexList() {
        return vertexList;
    }

    @Override
    public int size() throws ListException {
        int currentSize = vertexList.size();
        return currentSize;
    }

    @Override
    public void clear() {
        vertexList.clear();
    }

    @Override
    public boolean isEmpty() {
        boolean empty = vertexList.isEmpty();
        return empty;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if(isEmpty()) {

            return false;
        }
        boolean contains = indexOf(element)!=-1;
        return contains;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if(isEmpty()) {
            throw new GraphException("Singly Linked List Graph is Empty");
        }
        int indexA = indexOf(a);
        if(indexA == -1) {
            return false;
        }

        Vertex vertexA = (Vertex) vertexList.getNode(indexA - 1).data;
        if(vertexA.edgesList.isEmpty()) {
            return false;
        }

        for (int i = 0; i < vertexA.edgesList.size(); i++) {
            EdgeWeight ew = (EdgeWeight) vertexA.edgesList.get(i);
            if (Utility.compare(ew.getEdge(), b) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (containsVertex(element)) {
            throw new GraphException("El vértice [" + element + "] ya existe.");
        }
        vertexList.add(new Vertex(element));
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b)) {
            throw new GraphException("Cannot add edge between vertexes ["+a+"] y ["+b+"]");
        }
        if(containsEdge(a,b)) {
            System.err.println("[SinglyLinkedListGraph ERROR] addEdge(): Edge [" + a + "-" + b + "] already exists.");
            throw new GraphException("La arista [" + a + "-" + b + "] ya existe.");
        }

        addRemoveVertexEdgeWeight(a, b, null, "addEdge");
        addRemoveVertexEdgeWeight(b, a, null, "addEdge");
    }

    private int indexOf(Object element) throws ListException {
        if (vertexList == null || vertexList.isEmpty()) {
            return -1;
        }
        for(int i=0;i<vertexList.size();i++){
            Vertex vertex = (Vertex)vertexList.getNode(i).data;
            if(util.Utility.compare(vertex.data, element)==0){
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b)) {
            throw new GraphException("There is no edge between the vertexes[" + a + "] y [" + b + "]");
        }
        addRemoveVertexEdgeWeight(a, b, weight, "addWeight");
        addRemoveVertexEdgeWeight(b, a, weight, "addWeight");
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b)) {
            throw new GraphException("Cannot add edge with weight between vertexes ["+a+"] y ["+b+"]");
        }
        if(!containsEdge(a, b)) {
            addRemoveVertexEdgeWeight(a, b, weight, "addEdge");
            addRemoveVertexEdgeWeight(b, a, weight, "addEdge");
        } else {
            addWeight(a, b, weight);
        }
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if(isEmpty()) {
            throw new GraphException("Singly Linked List Graph is Empty");
        }
        int indexToRemove = indexOf(element);
        if(indexToRemove == -1) {
            throw new GraphException("Vértice [" + element + "] no encontrado.");
        }

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex currentVertex = (Vertex) vertexList.getNode(i).data;
            if(util.Utility.compare(currentVertex.data, element)!=0){
                SinglyLinkedList edgesList = currentVertex.edgesList;
                for (int j = edgesList.size() - 1; j >= 0; j--) {
                    EdgeWeight ew = (EdgeWeight) edgesList.get(j);
                    if (util.Utility.compare(ew.getEdge(), element) == 0) {
                        edgesList.remove(ew);
                        System.out.println("[SinglyLinkedListGraph DEBUG] removeVertex(): Removed edge " + currentVertex.data + "-" + element + " from " + currentVertex.data);
                    }
                }
            }
        }
        vertexList.remove(new Vertex(element));
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b)) {
            throw new GraphException("There's no some of the vertexes");
        }
        addRemoveVertexEdgeWeight(a, b, null, "remove");
        addRemoveVertexEdgeWeight(b, a, null, "remove");
    }

    private void addRemoveVertexEdgeWeight(Object a, Object b, Object weight, String action) throws ListException{
        int aIndex = indexOf(a);
        if (aIndex == -1) {
            return;
        }

        Vertex vertexA = (Vertex) vertexList.getNode(aIndex - 1).data;
        EdgeWeight targetEdgeWeight = new EdgeWeight(b, weight);

        switch(action){
            case "addEdge":
                if (!vertexA.edgesList.contains(targetEdgeWeight)) {
                    vertexA.edgesList.add(targetEdgeWeight);
                }
                break;
            case "addWeight":
                boolean weightUpdated = false;
                for(int i=0; i<vertexA.edgesList.size(); i++){
                    EdgeWeight ew = (EdgeWeight) vertexA.edgesList.get(i);
                    if(util.Utility.compare(ew.getEdge(), b) == 0){
                        ew.setWeight(weight);
                        weightUpdated = true;
                        return;
                    }
                }

                break;
            case "remove":
                boolean edgeRemoved = false;
                for (int i = vertexA.edgesList.size() - 1; i >= 0; i--) {
                    EdgeWeight ew = (EdgeWeight) vertexA.edgesList.get(i);
                    if (util.Utility.compare(ew.getEdge(), b) == 0) {
                        vertexA.edgesList.remove(ew); // Assuming remove by element works
                        edgeRemoved = true;
                        return;
                    }
                }

                break;
        }
    }

    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);
        if (isEmpty()) {
            throw new GraphException("Singly Linked List Graph is empty for DFS");
        }

        Vertex vertex = (Vertex)vertexList.getNode(0).data;
        String info =vertex.data+", ";
        vertex.setVisited(true);
        stack.clear();
        stack.push(1); // Push 1-based index

        while( !stack.isEmpty() ){
            int currentIndex = (int) stack.top();
            int nextAdjacentIndex = adjacentVertexNotVisited(currentIndex);
            if(nextAdjacentIndex==-1) {
                stack.pop();
            } else {
                vertex = (Vertex)vertexList.getNode(nextAdjacentIndex - 1).data;
                vertex.setVisited(true);
                info+=vertex.data+", ";
                stack.push(nextAdjacentIndex);
            }
        }
        return info;
    }

    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);
        if (isEmpty()) {
            throw new GraphException("Singly Linked List Graph is empty for BFS");
        }

        Vertex vertex = (Vertex)vertexList.getNode(0).data;
        String info =vertex.data+", ";
        vertex.setVisited(true);
        queue.clear();
        queue.enQueue(1); // Enqueue 1-based index

        int index2;
        while(!queue.isEmpty()){
            int index1 = (int) queue.deQueue();
            while((index2=adjacentVertexNotVisited(index1)) != -1 ){
                vertex = (Vertex)vertexList.getNode(index2 - 1).data;
                vertex.setVisited(true);
                info+=vertex.data+", ";
                queue.enQueue(index2);
            }
        }
        return info;
    }

    private void setVisited(boolean value) throws ListException {
        for (int i=0; i<vertexList.size(); i++) {
            Vertex vertex = (Vertex)vertexList.getNode(i).data;
            vertex.setVisited(value);
        }
    }

    private int adjacentVertexNotVisited(int index) throws ListException {
        if (index <= 0 || index > vertexList.size()) {
            return -1;
        }

        Vertex currentVertex = (Vertex) vertexList.getNode(index - 1).data;

        for (int i = 0; i < currentVertex.edgesList.size(); i++) {
            EdgeWeight ew = (EdgeWeight) currentVertex.edgesList.get(i);
            Object neighborData = ew.getEdge();

            int neighborIndex1Based = indexOf(neighborData);
            if (neighborIndex1Based != -1) {
                Vertex neighborVertex = (Vertex) vertexList.getNode(neighborIndex1Based - 1).data;
                if (!neighborVertex.isVisited()) {
                    return neighborIndex1Based;
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        String result = "Singly Linked List Graph Content...";
        try {
            if (isEmpty()) return result + "\nGraph is empty.";
            for(int i=0; i<vertexList.size(); i++){
                Vertex vertex = (Vertex)vertexList.getNode(i).data;
                result+="\nThe vertex in the position "+(i+1)+" is: "+vertex.data+"\n";
                if(!vertex.edgesList.isEmpty()){
                    result+="........EDGES AND WEIGHTS: ";
                    for(int j=0; j<vertex.edgesList.size(); j++){
                        EdgeWeight ew = (EdgeWeight) vertex.edgesList.get(j);
                        result += "(" + ew.getEdge() + ", " + ew.getWeight() + ") ";
                    }
                    result += "\n";
                }
            }
        } catch (ListException ex) {
            result += "\nError generating toString: " + ex.getMessage();
        }
        return result;
    }

    public void generateRandomGraph(int numVertices, double edgeDensity) throws GraphException, ListException {
        if (numVertices <= 0) {
            throw new IllegalArgumentException("El número de vértices debe ser mayor que 0.");
        }
        if (numVertices > 100) {
            // This restriction might be specific to your implementation (e.g., array-based)
            // For SinglyLinkedListGraph, it's less critical, but kept for consistency.
            throw new IllegalArgumentException("El número de vértices no puede exceder 100 (rango 0-99).");
        }
        if (edgeDensity < 0.0 || edgeDensity > 1.0) {
            throw new IllegalArgumentException("La densidad de aristas debe estar entre 0.0 y 1.0.");
        }

        this.clear();

        Random rand = new Random();
        SinglyLinkedList addedVertexValues = new SinglyLinkedList(); // To keep track of unique values

        // 1. Añadir vértices únicos
        for (int i = 0; i < numVertices; i++) {
            Integer vertexValue;
            boolean unique;
            do {
                vertexValue = rand.nextInt(100);
                unique = !addedVertexValues.contains(vertexValue);
            } while (!unique);


            addVertex(vertexValue);
            addedVertexValues.add(vertexValue);
        }


        // 2. Añadir aristas con una cierta densidad
        for (int i = 0; i < vertexList.size(); i++) {
            for (int j = i + 1; j < vertexList.size(); j++) {
                if (rand.nextDouble() < edgeDensity) {
                    Object v1 = getVertexDataByIndex(i + 1); // Get vertex data (1-based index)
                    Object v2 = getVertexDataByIndex(j + 1); // Get vertex data (1-based index)
                    int weight = rand.nextInt(91) + 10; // Weight between 10 and 100

                    try {
                        addEdgeWeight(v1, v2, weight); // Add edge with weight
                    } catch (GraphException e) {
                    }
                }
            }
        }
    }

    public Object getVertexDataByIndex(int index) throws ListException {
        if (index >= 1 && index <= vertexList.size()) {
            Vertex vertex = (Vertex) vertexList.getNode(index - 1).data;
            return vertex.data;
        }
        throw new ListException("Invalid index for getVertexDataByIndex: " + index + ". Size: " + vertexList.size());
    }

    public SinglyLinkedListGraph primMST(Object startVertexData) throws GraphException, ListException {
        if (isEmpty() || indexOf(startVertexData) == -1) {
            throw new GraphException("Grafo vacío o vértice inicial para Prim no existe.");
        }

        SinglyLinkedListGraph mst = new SinglyLinkedListGraph();
        for (int i = 0; i < vertexList.size(); i++) {
            mst.addVertex(getVertexDataByIndex(i + 1));
        }

        Map<Object, Integer> key = new HashMap<>();
        Map<Object, Object> parent = new HashMap<>();
        PriorityQueue<EdgeWeight> minHeap = new PriorityQueue<>((ew1, ew2) ->
                Integer.compare((Integer) ew1.getWeight(), (Integer) ew2.getWeight()));

        boolean[] inMST = new boolean[vertexList.size()];
        Map<Object, Integer> dataToIndexMap = new HashMap<>();

        for (int i = 0; i < vertexList.size(); i++) {
            Object vertexData = getVertexDataByIndex(i + 1);
            dataToIndexMap.put(vertexData, i);
            key.put(vertexData, Integer.MAX_VALUE);
            parent.put(vertexData, null);
            inMST[i] = false;
        }

        key.put(startVertexData, 0);
        minHeap.add(new EdgeWeight(startVertexData, 0));

        int mstVerticesCount = 0;
        while (!minHeap.isEmpty()) {
            EdgeWeight currentEW = minHeap.poll();
            Object uData = currentEW.getEdge();

            int uIndex0Based = dataToIndexMap.get(uData);

            if (inMST[uIndex0Based]) {
                continue; // Already processed
            }

            inMST[uIndex0Based] = true;
            mstVerticesCount++;

            Object p = parent.get(uData);
            if (p != null) { // If it's not the start vertex
                try {
                    mst.addEdgeWeight(p, uData, (Integer) currentEW.getWeight());
                } catch (GraphException e) {
                }
            }

            Vertex currentVertex = (Vertex) vertexList.getNode(uIndex0Based).data;
            SinglyLinkedList edgesOfU = currentVertex.edgesList;

            for (int i = 0; i < edgesOfU.size(); i++) {
                EdgeWeight neighborEW = (EdgeWeight) edgesOfU.get(i);
                Object vNeighborData = neighborEW.getEdge();
                int weight = (Integer) neighborEW.getWeight();

                if (dataToIndexMap.containsKey(vNeighborData)) {
                    int vNeighborIndex0Based = dataToIndexMap.get(vNeighborData);

                    if (!inMST[vNeighborIndex0Based] && weight < key.get(vNeighborData)) {
                        key.put(vNeighborData, weight);
                        parent.put(vNeighborData, uData);
                        minHeap.add(new EdgeWeight(vNeighborData, weight));
                    }
                }
            }
        }
        if (mstVerticesCount < vertexList.size()) {
            System.err.println("Advertencia: el MST de Prim no incluyó todos los vértices. El grafo podría estar desconectado. Vértices en MST: " + mstVerticesCount + ", Total vértices: " + vertexList.size());
        }
        return mst;
    }

    public SinglyLinkedListGraph kruskalMST() throws GraphException, ListException {
        if (isEmpty()) {
            System.err.println("[SinglyLinkedListGraph ERROR] kruskalMST(): Graph is empty.");
            throw new GraphException("Grafo vacío, no se puede calcular MST con Kruskal.");
        }

        SinglyLinkedListGraph mst = new SinglyLinkedListGraph();
        for (int i = 0; i < vertexList.size(); i++) {
            mst.addVertex(getVertexDataByIndex(i + 1));
        }

        ArrayList<KruskalEdge> allKruskalEdges = getSortedKruskalEdges();

        Map<Object, Integer> vertexDataToIndexMap = new HashMap<>();
        for (int i = 0; i < vertexList.size(); i++) {
            vertexDataToIndexMap.put(getVertexDataByIndex(i + 1), i);
        }

        Subset[] subsets = new Subset[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            subsets[i] = new Subset(i, 0); // Each vertex is initially its own set
        }

        int edgesInMST = 0;
        int i = 0;

        while (edgesInMST < vertexList.size() - 1 && i < allKruskalEdges.size()) {
            KruskalEdge currentKEdge = allKruskalEdges.get(i++);

            Object uData = currentKEdge.source;
            Object vData = currentKEdge.destination;

            if (!vertexDataToIndexMap.containsKey(uData) || !vertexDataToIndexMap.containsKey(vData)) {
                System.err.println("[SinglyLinkedListGraph WARNING] KruskalEdge contains vertex not existing in index map. Skipping edge.");
                continue;
            }

            int rootU = find(subsets, vertexDataToIndexMap.get(uData));
            int rootV = find(subsets, vertexDataToIndexMap.get(vData));

            if (rootU != rootV) {
                edgesInMST++;
                try {
                    mst.addEdgeWeight(uData, vData, currentKEdge.weight);
                } catch (GraphException e) {
                    System.err.println("[SinglyLinkedListGraph ERROR] kruskalMST(): Could not add edge to MST: " + uData + "-" + vData + " - " + e.getMessage());
                }
                union(subsets, rootU, rootV); // Union the sets
            } else {
            }
        }

        if (edgesInMST != vertexList.size() - 1 && vertexList.size() > 1) {
            System.err.println("Advertencia: el MST de Kruskal no incluyó todos los vértices. El grafo podría estar desconectado. Edges in MST: " + edgesInMST + ", Required edges: " + (vertexList.size() - 1));
        }
        return mst;
    }

    private static class KruskalEdge implements Comparable<KruskalEdge> {
        Object source;
        Object destination;
        int weight;

        public KruskalEdge(Object source, Object destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public int compareTo(KruskalEdge other) {
            return Integer.compare(this.weight, other.weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KruskalEdge that = (KruskalEdge) o;
            return (Utility.compare(source, that.source) == 0 && Utility.compare(destination, that.destination) == 0) ||
                    (Utility.compare(source, that.destination) == 0 && Utility.compare(destination, that.source) == 0);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.min(Utility.compare(source, destination), Utility.compare(destination, source) == 0 ? 0 : 1),
                    Math.max(Utility.compare(source, destination), Utility.compare(destination, source) == 0 ? 0 : 1));
        }
    }

    private ArrayList<KruskalEdge> getSortedKruskalEdges() throws ListException {
        ArrayList<KruskalEdge> allKruskalEdges = new ArrayList<>();
        Set<KruskalEdge> uniqueEdgesTracker = new HashSet<>();

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex currentVertex = (Vertex) vertexList.getNode(i).data;
            SinglyLinkedList edgesOfVertex = currentVertex.edgesList;

            for (int j = 0; j < edgesOfVertex.size(); j++) {
                EdgeWeight ew = (EdgeWeight) edgesOfVertex.get(j);
                Object sourceData = currentVertex.data;
                Object destinationData = ew.getEdge();
                int weight = (Integer) ew.getWeight();

                KruskalEdge newKEdge = new KruskalEdge(sourceData, destinationData, weight);

                if (uniqueEdgesTracker.add(newKEdge)) {
                    allKruskalEdges.add(newKEdge);
                }
            }
        }
        Collections.sort(allKruskalEdges);
        return allKruskalEdges;
    }

    static class Subset {
        int parent;
        int rank;

        public Subset(int parent, int rank) {
            this.parent = parent;
            this.rank = rank;
        }
    }

    private int find(Subset[] subsets, int i) {
        if (subsets[i].parent != i) {
            subsets[i].parent = find(subsets, subsets[i].parent);
        }
        return subsets[i].parent;
    }

    private void union(Subset[] subsets, int xRoot, int yRoot) {
        if (subsets[xRoot].rank < subsets[yRoot].rank) {
            subsets[xRoot].parent = yRoot;
        } else if (subsets[xRoot].rank > subsets[yRoot].rank) {
            subsets[yRoot].parent = xRoot;
        } else {
            subsets[yRoot].parent = xRoot;
            subsets[xRoot].rank++;
        }
    }

    public Vertex getVertex(Object data) throws GraphException, ListException {
        for (int i = 1; i <= size(); i++) {
            Object vertexData = getVertexDataByIndex(i);
            if (vertexData.equals(data)) {
                return (Vertex) getVertexList().getNode(i-1).data;
            }
        }
        throw new GraphException("Vertex not found: " + data);
    }
}