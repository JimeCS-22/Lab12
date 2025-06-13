package graph;

import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;
import util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet; // Necesario para Kruskal
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue; // Necesario para Prim
import java.util.Set; // Necesario para Kruskal

public class AdjacencyMatrixGraph implements Graph {

    public Vertex[] vertexList; //arreglo de objetos tupo vértice
    private Object[][] adjacencyMatrix; //arreglo bidimensional
    private int n; //max de elementos
    private int counter; //contador de vertices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public AdjacencyMatrixGraph(int n) {
        if (n <= 0) {
            System.err.println("Error: El tamaño del grafo debe ser mayor que 0.");
            // Considera lanzar una IllegalArgumentException en lugar de System.exit(1)
            // para que la aplicación pueda manejar el error de forma más elegante.
            System.exit(1);
        }
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.adjacencyMatrix = new Object[n][n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
        initMatrix(); //inicializa matriz de objetos con cero
    }

    private void initMatrix() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                this.adjacencyMatrix[i][j] = 0; //init con ceros
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n]; // Reinicializa el arreglo de vértices
        this.adjacencyMatrix = new Object[n][n]; // Reinicializa la matriz
        this.counter = 0; //inicializo contador de vértices
        this.initMatrix();
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if(isEmpty())
            // No lanzar excepción aquí, ya que se usa para verificar existencia
            // throw new GraphException("Adjacency Matrix Graph is Empty");
            return false; // Si está vacío, definitivamente no contiene el vértice
        return indexOf(element)!=-1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if(isEmpty())
            throw new GraphException("Adjacency Matrix Graph is Empty");
        int indexA = indexOf(a);
        int indexB = indexOf(b);
        if (indexA == -1 || indexB == -1) {
            return false; // Uno o ambos vértices no existen
        }
        return !(util.Utility.compare(adjacencyMatrix[indexA][indexB], 0)==0);
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if(counter>=vertexList.length)
            throw new GraphException("Adjacency Matrix Graph is Full");
        if(containsVertex(element)) // Verificar si ya existe antes de añadir
            throw new GraphException("El vértice [" + element + "] ya existe.");
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes ["+a+"] y ["+b+"]");
        if(containsEdge(a, b)) // Verificar si la arista ya existe
            throw new GraphException("La arista [" + a + "-" + b + "] ya existe.");

        int indexA = indexOf(a);
        int indexB = indexOf(b);
        adjacencyMatrix[indexA][indexB] = 1; //hay una arista (peso por defecto 1)
        //grafo no dirigido
        adjacencyMatrix[indexB][indexA] = 1; //hay una arista
    }

    private int indexOf(Object element){
        for (int i = 0; i < counter; i++) {
            if(util.Utility.compare(vertexList[i].data, element)==0)
                return i; //retorna la pos en el arreglo de objectos vertexList (0-based)
        }
        return -1; //significa q la data de todos los vertices no coinciden con element
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if(!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes["+a+"] y ["+b+"]");
        int indexA = indexOf(a);
        int indexB = indexOf(b);
        adjacencyMatrix[indexA][indexB] = weight; //hay una arista
        //grafo no dirigido
        adjacencyMatrix[indexB][indexA] = weight; //hay una arista
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot add edge with weight between vertexes ["+a+"] y ["+b+"]");
        int indexA = indexOf(a);
        int indexB = indexOf(b);
        adjacencyMatrix[indexA][indexB] = weight; //hay una arista
        //grafo no dirigido
        adjacencyMatrix[indexB][indexA] = weight; //hay una arista
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if(isEmpty())
            throw new GraphException("Adjacency Matrix Graph is Empty");
        int index = indexOf(element);
        if(index == -1)
            throw new GraphException("Vértice [" + element + "] no encontrado.");

        // Mover los vértices posteriores hacia adelante en vertexList
        for (int i = index; i < counter - 1; i++) {
            vertexList[i] = vertexList[i + 1];
        }

        // Eliminar las filas y columnas asociadas al vértice
        // Mover filas hacia arriba
        for (int i = index; i < counter - 1; i++) {
            for (int j = 0; j < counter; j++) {
                adjacencyMatrix[i][j] = adjacencyMatrix[i + 1][j];
            }
        }
        // Mover columnas hacia la izquierda
        for (int i = 0; i < counter; i++) {
            for (int j = index; j < counter - 1; j++) {
                adjacencyMatrix[i][j] = adjacencyMatrix[i][j + 1];
            }
        }

        // Limpiar la última fila y columna (que ahora tienen datos duplicados o no deseados)
        for (int j = 0; j < counter; j++) {
            adjacencyMatrix[counter - 1][j] = 0;
        }
        for (int i = 0; i < counter; i++) {
            adjacencyMatrix[i][counter - 1] = 0;
        }

        counter--; // Decrementar el contador de vértices

        // Si no quedan vértices, reinicializar la matriz
        if(counter == 0) initMatrix();
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("There's no some of the vertexes");
        int i = indexOf(a);
        int j = indexOf(b);
        if(i!=-1 && j!=-1){
            adjacencyMatrix[i][j] = 0;
            adjacencyMatrix[j][i] = 0; //grafo no dirigido
        }
    }

    // Recorrido en profundidad
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);//marca todos los vertices como no vistados
        if(isEmpty()) throw new GraphException("Adjacency Matrix Graph is empty for DFS");
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        stack.clear();
        stack.push(0); //lo apila (índice 0-based)
        while (!stack.isEmpty()) {
            // obtiene un vertice adyacente no visitado,
            //el que esta en el tope de la pila
            int index = adjacentVertexNotVisited((int) stack.top());
            if (index == -1) // no lo encontro
                stack.pop();
            else {
                vertexList[index].setVisited(true); // lo marca
                info += vertexList[index].data + ", "; //lo muestra
                stack.push(index); //inserta la posicion
            }
        }
        return info;
    }

    //Recorrido en amplitud
    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);//marca todos los vertices como no visitados
        if(isEmpty()) throw new GraphException("Adjacency Matrix Graph is empty for BFS");
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        queue.clear();
        queue.enQueue(0); // encola el elemento (índice 0-based)
        int v2;
        while (!queue.isEmpty()) {
            int v1 = (int) queue.deQueue(); // remueve el vertice de la cola
            // hasta que no tenga vecinos sin visitar
            while ((v2 = adjacentVertexNotVisited(v1)) != -1) {
                // obtiene uno
                vertexList[v2].setVisited(true); // lo marca
                info += vertexList[v2].data + ", "; //lo muestra
                queue.enQueue(v2); // lo encola
            }
        }
        return info;
    }

    //setteamos el atributo visitado del vertice respectivo
    private void setVisited(boolean value) {
        for (int i = 0; i < counter; i++) {
            if (vertexList[i] != null) // Asegurarse de que el vértice no sea nulo
                vertexList[i].setVisited(value); //value==true o false
        }//for
    }

    private int adjacentVertexNotVisited(int index) {
        if (index < 0 || index >= counter) return -1; // Validar índice

        for (int i = 0; i < counter; i++) {
            // Asegurarse de que no sea el mismo vértice y que exista una arista y el vecino no haya sido visitado
            if (i != index && util.Utility.compare(adjacencyMatrix[index][i], 0) != 0
                    && vertexList[i] != null && !vertexList[i].isVisited())
                return i;//retorna la posicion del vertice adyacente no visitado
        }//for i
        return -1;
    }

    @Override
    public String toString() {
        String result = "Adjacency Matrix Graph Content...";
        if (isEmpty()) return result + "\nGraph is empty.";

        //se muestran todos los vértices del grafo
        for (int i = 0; i < counter; i++) {
            result+="\nThe vertex in the position: "+i+" is: "+vertexList[i].data;
        }
        //agregamos la info de las aristas y pesos
        for (int i = 0; i < counter; i++) {
            for (int j = 0; j < counter; j++) {
                if(util.Utility.compare(adjacencyMatrix[i][j], 0)!=0) {//si existe arista
                    //si existe una arista
                    result+="\nThere is edge between the vertexes: "+vertexList[i].data+"...."
                            +vertexList[j].data;
                    //si existe peso que lo muestre
                    if(util.Utility.compare(adjacencyMatrix[i][j], 1)!=0){
                        //si matriz[fila][col] !=1 existe un peso agregado
                        result+="_____WEIGHT: "+adjacencyMatrix[i][j];
                    }
                }
            }
        }

        return result;
    }

    public Object getEdgeWeight(Object a, Object b) throws GraphException, ListException {
        if(!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes[" + a + "] and [" + b + "].");
        int indexA = indexOf(a);
        int indexB = indexOf(b);
        return adjacencyMatrix[indexA][indexB];
    }

    public Object getVertexData(int index) throws ListException {
        if (index < 0 || index >= counter) {
            throw new ListException("Vertex index out of bounds.");
        }
        return vertexList[index].data;
    }

    /**
     * Genera un grafo aleatorio con un número específico de vértices y una densidad de aristas.
     * Los vértices son números enteros aleatorios.
     * @param numVertices El número de vértices a generar.
     * @param edgeDensity La probabilidad (0.0 a 1.0) de que exista una arista entre dos vértices.
     * @throws GraphException Si ocurre un error al añadir vértices o aristas.
     * @throws ListException Si ocurre un error interno en las operaciones de lista.
     */
    public void generateRandomGraph(int numVertices, double edgeDensity) throws GraphException, ListException {
        if (numVertices <= 0) {
            throw new IllegalArgumentException("El número de vértices debe ser mayor que 0.");
        }
        if (numVertices > n) { // n es la capacidad máxima del grafo de matriz de adyacencia
            throw new IllegalArgumentException("El número de vértices no puede exceder la capacidad máxima del grafo (" + n + ").");
        }
        if (edgeDensity < 0.0 || edgeDensity > 1.0) {
            throw new IllegalArgumentException("La densidad de aristas debe estar entre 0.0 y 1.0.");
        }

        this.clear(); // Limpiar el grafo existente

        java.util.Random rand = new java.util.Random();
        Set<Integer> addedVertexValues = new HashSet<>(); // Para asegurar vértices únicos

        // 1. Añadir vértices únicos
        for (int i = 0; i < numVertices; i++) {
            Integer vertexValue;
            do {
                vertexValue = rand.nextInt(100); // Genera un número entre 0 y 99
            } while (addedVertexValues.contains(vertexValue)); // Asegura unicidad

            addVertex(vertexValue); // Añade el vértice al grafo
            addedVertexValues.add(vertexValue); // Añade el valor al set de seguimiento
        }

        // 2. Añadir aristas con una cierta densidad
        // Solo para los vértices que realmente se agregaron (counter)
        for (int i = 0; i < counter; i++) {
            for (int j = i + 1; j < counter; j++) { // Evitar auto-bucles y aristas duplicadas (grafo no dirigido)
                if (rand.nextDouble() < edgeDensity) {
                    Object v1 = getVertexData(i);
                    Object v2 = getVertexData(j);
                    int weight = rand.nextInt(91) + 10; // Peso entre 10 y 100

                    try {
                        addEdgeWeight(v1, v2, weight);
                    } catch (GraphException e) {
                        System.err.println("Error al añadir arista [" + v1 + "-" + v2 + "]: " + e.getMessage());
                    }
                }
            }
        }
    }



    public AdjacencyMatrixGraph primMST(Object startVertexData) throws GraphException, ListException {
        if (isEmpty() || indexOf(startVertexData) == -1) {
            throw new GraphException("Grafo vacío o vértice inicial para Prim no existe.");
        }

        // Crear un nuevo grafo para el MST
        AdjacencyMatrixGraph mst = new AdjacencyMatrixGraph(this.n); // Usar la misma capacidad
        for (int i = 0; i < counter; i++) {
            mst.addVertex(getVertexData(i)); // Añadir todos los vértices al MST
        }

        // Inicialización para Prim
        Map<Object, Integer> key = new HashMap<>(); // Almacena el peso mínimo para conectar el vértice al MST
        Map<Object, Object> parent = new HashMap<>(); // Almacena el padre en el MST
        // PriorityQueue para seleccionar la arista de menor peso
        PriorityQueue<EdgeWeight> minHeap = new PriorityQueue<>((ew1, ew2) ->
                Integer.compare((Integer) ew1.getWeight(), (Integer) ew2.getWeight()));

        boolean[] inMST = new boolean[counter]; // Para rastrear vértices incluidos en el MST
        Map<Object, Integer> dataToIndexMap = new HashMap<>(); // Mapeo de datos de vértice a índice (0-based)

        // Inicializar todas las claves a infinito, padres a nulo
        for (int i = 0; i < counter; i++) {
            Object vertexData = getVertexData(i);
            dataToIndexMap.put(vertexData, i);
            key.put(vertexData, Integer.MAX_VALUE);
            parent.put(vertexData, null);
            inMST[i] = false;
        }

        // La clave del vértice inicial es 0
        key.put(startVertexData, 0);
        minHeap.add(new EdgeWeight(startVertexData, 0));

        while (!minHeap.isEmpty()) {
            // Extraer el vértice con la clave mínima
            EdgeWeight currentEW = minHeap.poll();
            Object uData = currentEW.getEdge(); // Los datos del vértice actual

            int uIndex0Based = dataToIndexMap.get(uData);

            // Si ya está en el MST, ignorar (puede haber entradas duplicadas en la cola de prioridad)
            if (inMST[uIndex0Based]) {
                continue;
            }

            // Marcar el vértice como parte del MST
            inMST[uIndex0Based] = true;

            // Si tiene un padre, añadir la arista al MST
            Object p = parent.get(uData);
            if (p != null) {
                mst.addEdgeWeight(p, uData, (Integer) currentEW.getWeight());
            }

            // Recorrer todos los vecinos de u
            for (int vIndex0Based = 0; vIndex0Based < counter; vIndex0Based++) {
                if (uIndex0Based == vIndex0Based) continue; // Saltar el propio vértice

                // Obtener el peso de la arista u-v
                Object weightObj = adjacencyMatrix[uIndex0Based][vIndex0Based];
                if (util.Utility.compare(weightObj, 0) != 0) { // Si existe una arista
                    int weight = (Integer) weightObj;
                    Object vNeighborData = getVertexData(vIndex0Based);

                    // Si v no está en el MST y el peso de la arista (u,v) es menor que la clave actual de v
                    if (!inMST[vIndex0Based] && weight < key.get(vNeighborData)) {
                        key.put(vNeighborData, weight);
                        parent.put(vNeighborData, uData);
                        minHeap.add(new EdgeWeight(vNeighborData, weight));
                    }
                }
            }
        }

        // Comprobar si todos los vértices fueron incluidos (grafo conectado)
        for (int i = 0; i < counter; i++) {
            if (!inMST[i]) {
                System.err.println("Advertencia: el MST de Prim no incluyó todos los vértices. El grafo podría estar desconectado.");
                break;
            }
        }
        return mst;
    }


    public AdjacencyMatrixGraph kruskalMST() throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("Grafo vacío, no se puede calcular MST con Kruskal.");
        }

        // Crear un nuevo grafo para el MST
        AdjacencyMatrixGraph mst = new AdjacencyMatrixGraph(this.n);
        for (int i = 0; i < counter; i++) {
            mst.addVertex(getVertexData(i)); // Añadir todos los vértices al MST
        }

        // 1. Obtener todas las aristas y ordenarlas por peso
        ArrayList<KruskalEdge> allKruskalEdges = getSortedKruskalEdges();

        // 2. Inicializar la estructura de Disjoint Set Union (DSU)
        // Cada vértice es inicialmente su propio conjunto
        Subset[] subsets = new Subset[counter];
        Map<Object, Integer> vertexDataToIndexMap = new HashMap<>(); // Para mapear Object a índice DSU
        for (int i = 0; i < counter; i++) {
            subsets[i] = new Subset(i, 0);
            vertexDataToIndexMap.put(getVertexData(i), i);
        }

        // 3. Iterar a través de las aristas ordenadas
        int edgesInMST = 0;
        int edgeIndex = 0;

        // El MST tendrá (V-1) aristas para un grafo conectado
        while (edgesInMST < counter - 1 && edgeIndex < allKruskalEdges.size()) {
            KruskalEdge currentKEdge = allKruskalEdges.get(edgeIndex++);

            Object uData = currentKEdge.source;
            Object vData = currentKEdge.destination;

            // Obtener los índices de los vértices en la estructura DSU
            int uIndex = vertexDataToIndexMap.get(uData);
            int vIndex = vertexDataToIndexMap.get(vData);

            // Encontrar los representantes (raíces) de los conjuntos de u y v
            int rootU = find(subsets, uIndex);
            int rootV = find(subsets, vIndex);

            // Si los vértices no están en el mismo componente conectado (no forman un ciclo)
            if (rootU != rootV) {
                edgesInMST++;
                // Añadir la arista al MST
                mst.addEdgeWeight(uData, vData, currentKEdge.weight);
                // Unir los conjuntos de u y v
                union(subsets, rootU, rootV);
            }
        }

        // Comprobar si el MST incluyó todos los vértices (solo si el grafo original tenía más de 1 vértice)
        if (counter > 1 && edgesInMST != counter - 1) {
            System.err.println("Advertencia: el MST de Kruskal no incluyó todos los vértices. El grafo podría estar desconectado.");
        }
        return mst;
    }

    // Clase auxiliar para las aristas en Kruskal (misma que en SinglyLinkedListGraph)
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
            // Las aristas son no dirigidas, así que (A,B) es igual a (B,A)
            return (Utility.compare(source, that.source) == 0 && Utility.compare(destination, that.destination) == 0) ||
                    (Utility.compare(source, that.destination) == 0 && Utility.compare(destination, that.source) == 0);
        }

        @Override
        public int hashCode() {
            // Genera un hash consistente para aristas no dirigidas
            // Combinar los hashes de source y destination de manera conmutativa
            return Objects.hash(source) + Objects.hash(destination);
            // Una forma más robusta pero más compleja si los objetos no tienen un hashCode consistente con compare:
            // List<Object> elements = new ArrayList<>();
            // elements.add(source);
            // elements.add(destination);
            // Collections.sort(elements, (o1, o2) -> Utility.compare(o1, o2));
            // return Objects.hash(elements.get(0), elements.get(1));
        }
    }

    // Método auxiliar para obtener todas las aristas para Kruskal
    private ArrayList<KruskalEdge> getSortedKruskalEdges() throws ListException {
        ArrayList<KruskalEdge> allKruskalEdges = new ArrayList<>();
        Set<KruskalEdge> uniqueEdgesTracker = new HashSet<>(); // Para evitar aristas duplicadas (por ser no dirigido)

        for (int i = 0; i < counter; i++) {
            Object currentVertexData = getVertexData(i);
            for (int j = i + 1; j < counter; j++) { // Recorre solo la mitad superior de la matriz para evitar duplicados
                Object neighborVertexData = getVertexData(j);
                Object weightObj = adjacencyMatrix[i][j];

                if (util.Utility.compare(weightObj, 0) != 0) { // Si existe una arista
                    int weight = (Integer) weightObj;
                    KruskalEdge newKEdge = new KruskalEdge(currentVertexData, neighborVertexData, weight);
                    if (uniqueEdgesTracker.add(newKEdge)) { // add retorna true si el elemento fue añadido (es único)
                        allKruskalEdges.add(newKEdge);
                    }
                }
            }
        }
        Collections.sort(allKruskalEdges); // Ordenar por peso ascendente
        return allKruskalEdges;
    }

    // Clases auxiliares para Disjoint Set Union (DSU) (misma que en SinglyLinkedListGraph)
    static class Subset {
        int parent;
        int rank; // Para optimización de unión por rango

        public Subset(int parent, int rank) {
            this.parent = parent;
            this.rank = rank;
        }
    }

    // Operación find con compresión de ruta (misma que en SinglyLinkedListGraph)
    private int find(Subset[] subsets, int i) {
        if (subsets[i].parent != i) {
            subsets[i].parent = find(subsets, subsets[i].parent);
        }
        return subsets[i].parent;
    }

    // Operación union por rango (misma que en SinglyLinkedListGraph)
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
}