// src/main/java/graph/AdjacencyListGraph.java
package graph;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;
import util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
// No necesitamos java.util.HashSet ni java.util.Set

public class AdjacencyListGraph implements Graph {
    public Vertex[] vertexList; // Arreglo de objetos tipo vértice
    private int n; // Máximo de elementos
    private int counter; // Contador de vértices

    // Para los recorridos DFS y BFS
    private LinkedStack stack;
    private LinkedQueue queue;

    // Constructor
    public AdjacencyListGraph(int n) {
        if (n <= 0) System.exit(1);
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n];
        this.counter = 0;
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Grafo de Lista de Adyacencia está vacío");
        return indexOf(element) != -1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Grafo de Lista de Adyacencia está vacío");
        int indexA = indexOf(a);
        if (indexA == -1) return false;

        SinglyLinkedList edgesOfA = vertexList[indexA].edgesList;
        if (edgesOfA.isEmpty()) return false;

        // Itera a través de las aristas del vértice A para encontrar una que conecte con B
        for (int i = 0; i < edgesOfA.size(); i++) {
            EdgeWeight ew = (EdgeWeight) edgesOfA.get(i);
            Edge edge = (Edge) ew.getEdge();
            // Edge.equals maneja (A,B) == (B,A) para no dirigidos
            if (edge.equals(new Edge(a, b))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter >= vertexList.length)
            throw new GraphException("Grafo de Lista de Adyacencia está lleno");
        // La validación de unicidad se hará en generateRandomGraph, o se asume fuera de este método para otros usos
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("No se puede agregar arista entre vértices [" + a + "] y [" + b + "]. Uno o ambos no existen.");
        if (containsEdge(a, b))
            throw new GraphException("La arista [" + a + "-" + b + "] ya existe.");

        Edge newEdge = new Edge(a, b);
        vertexList[indexOf(a)].edgesList.add(new EdgeWeight(newEdge, null));
        vertexList[indexOf(b)].edgesList.add(new EdgeWeight(newEdge, null)); // Grafo no dirigido
    }

    public int indexOf(Object element) {
        for (int i = 0; i < counter; i++) {
            if (vertexList[i]!=null && Utility.compare(vertexList[i].data, element) == 0) // Añadido null check
                return i;
        }
        return -1;
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b))
            throw new GraphException("No existe arista entre los vértices[" + a + "] y [" + b + "] para agregar peso.");

        updateEdgeWeight(a, b, weight);
        updateEdgeWeight(b, a, weight); // Grafo no dirigido
    }

    // Método auxiliar para actualizar el peso de una arista existente
    private void updateEdgeWeight(Object source, Object destination, Object weight) throws ListException {
        int sourceIndex = indexOf(source);
        SinglyLinkedList edgesList = vertexList[sourceIndex].edgesList;

        for (int i = 0; i < edgesList.size(); i++) {
            EdgeWeight ew = (EdgeWeight) edgesList.get(i);
            Edge edge = (Edge) ew.getEdge();
            // Verifica si es la arista que queremos actualizar (para no dirigido)
            if (edge.equals(new Edge(source, destination))) {
                ew.setWeight(weight);
                return;
            }
        }
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("No se puede agregar arista con peso entre vértices [" + a + "] y [" + b + "]. Uno o ambos no existen.");

        Edge newEdge = new Edge(a, b);
        if (!containsEdge(a, b)) { // Si la arista no existe, la agrega
            vertexList[indexOf(a)].edgesList.add(new EdgeWeight(newEdge, weight));
            vertexList[indexOf(b)].edgesList.add(new EdgeWeight(newEdge, weight)); // Grafo no dirigido
        } else { // Si la arista ya existe, actualiza su peso
            addWeight(a, b, weight);
        }
    }

    // Añade una arista usando un objeto Edge explícito y un peso
    public void addEdgeWithSpecificEdgeObject(Object source, Object destination, Edge edgeObject, Object weight) throws GraphException, ListException {
        int sourceIndex = indexOf(source);
        int destIndex = indexOf(destination);

        if (sourceIndex == -1 || destIndex == -1) {
            throw new GraphException("addEdgeWithSpecificEdgeObject: Uno o ambos vértices no existen en el grafo. Origen: " + source + ", Destino: " + destination);
        }

        if (!(weight instanceof Integer)) {
            throw new GraphException("addEdgeWithSpecificEdgeObject: El peso proporcionado no es un Integer.");
        }

        vertexList[sourceIndex].edgesList.add(new EdgeWeight(edgeObject, weight));
        vertexList[destIndex].edgesList.add(new EdgeWeight(edgeObject, weight)); // Para grafo no dirigido
    }


    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Grafo de Lista de Adyacencia está vacío");
        int indexToRemove = indexOf(element);
        if (indexToRemove == -1)
            throw new GraphException("Vértice [" + element + "] no encontrado.");

        // Elimina todas las aristas conectadas al elemento a eliminar de las listas de otros vértices
        for (int i = 0; i < counter; i++) {
            Object currentVertexData = vertexList[i].data;
            if (Utility.compare(currentVertexData, element) != 0) {
                SinglyLinkedList edgesOfCurrent = vertexList[i].edgesList;
                for (int j = edgesOfCurrent.size() - 1; j >= 0; j--) {
                    EdgeWeight ew = (EdgeWeight) edgesOfCurrent.get(j);
                    Edge edge = (Edge) ew.getEdge();
                    if (edge.equals(new Edge(currentVertexData, element))) {
                        edgesOfCurrent.remove(ew);
                    }
                }
            }
        }

        // Desplaza los vértices restantes en el arreglo
        for (int j = indexToRemove; j < counter - 1; j++) {
            vertexList[j] = vertexList[j + 1];
        }
        vertexList[--counter] = null; // Limpia la última referencia duplicada
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("No se puede remover la arista: uno o ambos vértices [" + a + "] y [" + b + "] no existen.");
        if (!containsEdge(a, b))
            throw new GraphException("No se puede remover la arista: no existe arista entre los vértices [" + a + "] y [" + b + "].");

        removeSpecificEdge(a, b);
        removeSpecificEdge(b, a); // Para grafo no dirigido
    }

    // Método auxiliar para remover una arista específica usando la igualdad de objetos Edge
    private void removeSpecificEdge(Object source, Object destination) throws ListException {
        int sourceIndex = indexOf(source);
        SinglyLinkedList edgesList = vertexList[sourceIndex].edgesList;

        for (int i = 0; i < edgesList.size(); i++) {
            EdgeWeight ew = (EdgeWeight) edgesList.get(i);
            Edge edge = (Edge) ew.getEdge();
            if (edge.equals(new Edge(source, destination))) { // Usa Edge.equals para comparación no dirigida
                edgesList.remove(ew);
                return;
            }
        }
    }

    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);
        if (isEmpty()) throw new GraphException("Grafo de Lista de Adyacencia está vacío para DFS");

        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true);
        stack.clear();
        stack.push(0);

        while (!stack.isEmpty()) {
            int currentVertexIndex = (int) stack.top();
            int nextAdjacentIndex = getUnvisitedAdjacentVertex(currentVertexIndex);
            if (nextAdjacentIndex == -1) {
                stack.pop();
            } else {
                vertexList[nextAdjacentIndex].setVisited(true);
                info += vertexList[nextAdjacentIndex].data + ", ";
                stack.push(nextAdjacentIndex);
            }
        }
        return info;
    }

    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);
        if (isEmpty()) throw new GraphException("Grafo de Lista de Adyacencia está vacío para BFS");

        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true);
        queue.clear();
        queue.enQueue(0);

        while (!queue.isEmpty()) {
            int v1 = (int) queue.deQueue();
            int v2;
            while ((v2 = getUnvisitedAdjacentVertex(v1)) != -1) {
                vertexList[v2].setVisited(true);
                info += vertexList[v2].data + ", ";
                queue.enQueue(v2);
            }
        }
        return info;
    }

    private void setVisited(boolean value) {
        for (int i = 0; i < counter; i++) {
            if(vertexList[i]!=null)
                vertexList[i].setVisited(value);
        }
    }

    // Obtiene el índice de un vértice adyacente no visitado
    private int getUnvisitedAdjacentVertex(int index) throws ListException {
        if (vertexList[index] == null) return -1;
        SinglyLinkedList edgesOfCurrent = vertexList[index].edgesList;

        for (int i = 0; i < edgesOfCurrent.size(); i++) {
            EdgeWeight ew = (EdgeWeight) edgesOfCurrent.get(i);
            Edge edge = (Edge) ew.getEdge();

            Object adjacentVertexData = null;
            // Determina el otro extremo de la arista
            if (Utility.compare(edge.getElementA(), vertexList[index].data) == 0) {
                adjacentVertexData = edge.getElementB();
            } else {
                adjacentVertexData = edge.getElementA();
            }

            int adjacentIndex = indexOf(adjacentVertexData);
            if (adjacentIndex != -1 && !vertexList[adjacentIndex].isVisited()) {
                return adjacentIndex;
            }
        }
        return -1;
    }

    // Obtiene la data del vértice por su índice
    public Object getVertexDataByIndex(int index) {
        if (index >= 0 && index < counter && vertexList[index] != null) {
            return vertexList[index].data;
        }
        return null;
    }

    @Override
    public String toString() {
        String result = "Contenido del Grafo de Lista de Adyacencia...";
        for (int i = 0; i < counter; i++) {
            if(vertexList[i] != null) {
                result += "\nEl vértice en la posición: " + i + " es: " + vertexList[i].data;
                if (!vertexList[i].edgesList.isEmpty())
                    result += "\n......ARISTAS Y PESOS: " + vertexList[i].edgesList.toString();
            }
        }
        return result;
    }

    // --- ALGORITMO DE PRIM ---
    public AdjacencyListGraph primMST(Object startVertexData) throws GraphException, ListException {
        if (isEmpty() || !containsVertex(startVertexData)) {
            throw new GraphException("Grafo vacío o vértice inicial para Prim no existe.");
        }

        AdjacencyListGraph mst = new AdjacencyListGraph(this.n);
        // Añadir todos los vértices del grafo original al MST
        for (int i = 0; i < counter; i++) {
            if (vertexList[i] != null) {
                mst.addVertex(vertexList[i].data);
            }
        }

        Map<Object, Integer> key = new HashMap<>(); // Almacena el peso mínimo para conectar al MST
        Map<Object, Object> parent = new HashMap<>(); // Almacena el padre de cada vértice en el MST
        PriorityQueue<EdgeWeight> minHeap = new PriorityQueue<>(); // Cola de prioridad de EdgeWeight, ordenada por peso

        // Uso de un arreglo booleano para 'inMST' en lugar de HashSet
        boolean[] inMST = new boolean[counter]; // true si el vértice está en el MST, false en caso contrario
        Map<Object, Integer> dataToIndexMap = new HashMap<>(); // Para mapear data a índice del arreglo inMST

        for (int i = 0; i < counter; i++) {
            Object vertexData = vertexList[i].data;
            dataToIndexMap.put(vertexData, i); // Rellenar el mapa de data a índice
            key.put(vertexData, Integer.MAX_VALUE);
            parent.put(vertexData, null);
            inMST[i] = false; // Inicializar todos como no visitados
        }

        key.put(startVertexData, 0); // Clave para el vértice inicial es 0
        minHeap.add(new EdgeWeight(new Edge(startVertexData, startVertexData), 0)); // Añadir una arista "ficticia" al vértice inicial

        while (!minHeap.isEmpty()) { // Ya no es necesario 'inMST.size() < counter' porque se usa el array
            EdgeWeight currentEdgeWeight = minHeap.poll(); // Obtener la arista con el peso mínimo
            Edge currentEdge = (Edge) currentEdgeWeight.getEdge();

            Object uData = currentEdge.getElementB(); // Por convención, el segundo elemento es el vértice de destino
            if (Utility.compare(currentEdge.getElementA(), currentEdge.getElementB()) == 0) {
                uData = currentEdge.getElementA(); // Caso de arista ficticia (startVertex, startVertex)
            }


            int uIndex = dataToIndexMap.get(uData);

            if (inMST[uIndex]) { // Si el vértice ya está en el MST
                continue;
            }

            inMST[uIndex] = true; // Añadir vértice al conjunto MST

            // Si no es la arista ficticia del vértice de inicio, añadir la arista real al grafo MST
            Object p = parent.get(uData);
            if (p != null) {
                Edge edgeToAdd = new Edge(p, uData);
                mst.addEdgeWithSpecificEdgeObject(p, uData, edgeToAdd, (Integer) currentEdgeWeight.getWeight());
            }

            // Explorar vecinos de uData
            SinglyLinkedList edgesOfU = vertexList[uIndex].edgesList;
            for (int i = 0; i < edgesOfU.size(); i++) {
                EdgeWeight neighborEW = (EdgeWeight) edgesOfU.get(i);
                Edge neighborEdge = (Edge) neighborEW.getEdge();
                Object vNeighborData = null;

                // Determinar el vértice adyacente a 'uData' de 'neighborEdge'
                if (Utility.compare(neighborEdge.getElementA(), uData) == 0) {
                    vNeighborData = neighborEdge.getElementB();
                } else if (Utility.compare(neighborEdge.getElementB(), uData) == 0) {
                    vNeighborData = neighborEdge.getElementA();
                }

                int vNeighborIndex = dataToIndexMap.get(vNeighborData);

                if (!inMST[vNeighborIndex]) { // Si el vecino NO está en el MST
                    int weight = (Integer) neighborEW.getWeight();
                    if (weight < key.get(vNeighborData)) {
                        key.put(vNeighborData, weight);
                        parent.put(vNeighborData, uData);
                        minHeap.add(new EdgeWeight(neighborEdge, weight));
                    }
                }
            }
        }
        // Verificar si todos los vértices fueron incluidos, si el grafo es conectado
        for(int i=0; i<counter; i++) {
            if(!inMST[i]) {
                System.err.println("Advertencia: el MST de Prim no incluyó todos los vértices. El grafo podría estar desconectado.");
                break;
            }
        }
        return mst;
    }


    // --- ALGORITMO DE KRUSKAL ---
    public AdjacencyListGraph kruskalMST() throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("Grafo vacío, no se puede calcular MST con Kruskal.");
        }

        AdjacencyListGraph mst = new AdjacencyListGraph(this.n);
        // Añadir todos los vértices al grafo MST (sin aristas)
        for (int i = 0; i < counter; i++) {
            if (vertexList[i] != null) {
                mst.addVertex(vertexList[i].data);
            }
        }

        // 1. Obtener todas las aristas únicas del grafo y ordenarlas por peso
        ArrayList<EdgeWeight> allEdges = getSortedEdges();

        // 2. Inicializar la estructura de datos Union-Find (Disjoint Set Union)
        Map<Object, Integer> vertexToIndexMap = new HashMap<>();
        for (int i = 0; i < counter; i++) {
            vertexToIndexMap.put(vertexList[i].data, i);
        }

        Subset[] subsets = new Subset[counter];
        for (int i = 0; i < counter; i++) {
            subsets[i] = new Subset(i, 0); // Cada vértice está inicialmente en su propio conjunto
        }

        int edgesInMST = 0; // Contador de aristas añadidas al MST
        int i = 0; // Índice para la lista de aristas ordenadas

        // Iterar a través de las aristas ordenadas
        while (edgesInMST < counter - 1 && i < allEdges.size()) {
            EdgeWeight currentEW = allEdges.get(i++); // Obtener la arista más pequeña
            Edge currentEdge = (Edge) currentEW.getEdge();

            Object u = currentEdge.getElementA();
            Object v = currentEdge.getElementB();

            int rootU = find(subsets, vertexToIndexMap.get(u));
            int rootV = find(subsets, vertexToIndexMap.get(v));

            // Si incluir esta arista no forma un ciclo (las raíces son diferentes)
            if (rootU != rootV) {
                edgesInMST++;
                // Añadir la arista al grafo MST
                mst.addEdgeWithSpecificEdgeObject(u, v, currentEdge, (Integer) currentEW.getWeight());
                union(subsets, rootU, rootV); // Unir los conjuntos
            }
        }

        // Opcional: Verificar si todos los vértices están conectados (si el grafo está desconectado, el MST no abarcará todos)
        if (edgesInMST != counter - 1 && counter > 1) {
            System.err.println("Advertencia: el MST de Kruskal no incluyó todos los vértices. El grafo podría estar desconectado.");
        }
        return mst;
    }

    // Método auxiliar para Kruskal: Recolecta todas las aristas únicas con pesos y las ordena
    private ArrayList<EdgeWeight> getSortedEdges() throws ListException {
        ArrayList<EdgeWeight> allEdges = new ArrayList<>();
        // Uso de un arreglo booleano para 'addedEdges' en lugar de HashSet
        // Esto es más complejo para aristas, por lo que usaremos una pequeña clase auxiliar si no se quiere HashSet.
        // Sin embargo, para la unicidad de aristas, HashSet es el enfoque más directo y eficiente.
        // Si el requisito es NO USAR HASHSET EN NINGÚN LUGAR, entonces se necesitaría una lista y búsqueda lineal/ordenación.
        // Para este caso, dado que ya tienes Edge.equals, es viable.

        // Una alternativa (menos eficiente que HashSet) para evitar duplicados en getSortedEdges:
        // Crear una lista temporal de Strings que representen las aristas (ej. "A-B" o "B-A")
        ArrayList<String> addedEdgeStrings = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            SinglyLinkedList edgesOfVertex = vertexList[i].edgesList;
            for (int j = 0; j < edgesOfVertex.size(); j++) {
                EdgeWeight ew = (EdgeWeight) edgesOfVertex.get(j);
                Edge edge = (Edge) ew.getEdge();

                // Construir una representación canónica de la arista (ej. "min-max" de elementos)
                String edgeString;
                if (Utility.compare(edge.getElementA(), edge.getElementB()) < 0) {
                    edgeString = edge.getElementA().toString() + "-" + edge.getElementB().toString();
                } else {
                    edgeString = edge.getElementB().toString() + "-" + edge.getElementA().toString();
                }

                boolean found = false;
                for (String existingEdgeString : addedEdgeStrings) {
                    if (existingEdgeString.equals(edgeString)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    allEdges.add(ew);
                    addedEdgeStrings.add(edgeString);
                }
            }
        }
        Collections.sort(allEdges); // Ordena usando el compareTo de EdgeWeight (por peso)
        return allEdges;
    }


    // --- Clases auxiliares para Union-Find (para Kruskal) ---

    // Representa un subconjunto para Union-Find
    static class Subset {
        int parent;
        int rank; // Para optimización de unión por rango

        public Subset(int parent, int rank) {
            this.parent = parent;
            this.rank = rank;
        }
    }

    // Encuentra la raíz del conjunto que contiene el elemento 'i' con compresión de ruta
    private int find(Subset[] subsets, int i) {
        if (subsets[i].parent != i) {
            subsets[i].parent = find(subsets, subsets[i].parent);
        }
        return subsets[i].parent;
    }

    // Une el conjunto que contiene x y el conjunto que contiene y por rango
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


    public void generateRandomGraph(int numVertices, double edgeDensity) throws GraphException, ListException {
        if (numVertices <= 0) {
            throw new IllegalArgumentException("El número de vértices debe ser mayor que 0.");
        }
        if (numVertices > 100) { // Los vértices son de 0 a 99 (100 posibles valores)
            throw new IllegalArgumentException("El número de vértices no puede exceder 100 (rango 0-99).");
        }
        if (edgeDensity < 0.0 || edgeDensity > 1.0) {
            throw new IllegalArgumentException("La densidad de aristas debe estar entre 0.0 y 1.0.");
        }

        // Limpiar el grafo actual
        this.clear();
        this.n = numVertices; // Ajustar el tamaño máximo si es diferente
        this.vertexList = new Vertex[numVertices]; // Reiniciar el arreglo de vértices

        // 1. Añadir vértices aleatorios (valores numéricos entre 0 y 99)
        for (int i = 0; i < numVertices; i++) {
            Integer vertexValue;
            boolean unique;
            do {
                vertexValue = Utility.random(100); // Genera un número entre 0 y 99
                unique = true;
                // Validar si el valor ya existe en los vértices agregados hasta ahora
                for (int k = 0; k < i; k++) { // Solo iteramos sobre los vértices ya añadidos
                    if (Utility.compare(vertexList[k].data, vertexValue) == 0) {
                        unique = false;
                        break;
                    }
                }
            } while (!unique);

            addVertex(vertexValue);
        }

        // 2. Añadir aristas aleatorias con pesos aleatorios (entre 10 y 100)
        // Iterar sobre todas las posibles parejas de vértices
        for (int i = 0; i < counter; i++) {
            for (int j = i + 1; j < counter; j++) {
                // Usar Random de java.util.Random para la densidad, no es un problema
                if (new Random().nextDouble() < edgeDensity) {
                    Object v1 = getVertexDataByIndex(i);
                    Object v2 = getVertexByIndex(j).data; // Obtener data directamente
                    // Peso aleatorio entre 10 y 100
                    int weight = Utility.random(91) + 10; // (max - min + 1) + min = (100 - 10 + 1) + 10 = 91 + 10

                    try {
                        addEdgeWeight(v1, v2, weight);
                    } catch (GraphException e) {
                        // Podrías registrar el error o simplemente ignorarlo si es un caso esperado
                        // System.err.println("Error al añadir arista aleatoria entre " + v1 + " y " + v2 + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    public Vertex getVertexByIndex(int index) {
        if (index >= 0 && index < counter) {
            return vertexList[index];
        }
        return null;
    }

    public Vertex getVertex(Object data) throws GraphException, ListException {
        for (int i = 0; i < size(); i++) {
            if (vertexList[i] != null && vertexList[i].data.equals(data)) {
                return vertexList[i];
            }
        }
        throw new GraphException("Vertex not found: " + data);
    }
}