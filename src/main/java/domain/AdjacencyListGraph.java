package domain;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;

import java.util.*;

public class AdjacencyListGraph implements Graph {
    public Vertex[] vertexList; //arreglo de objetos tupo vértice
    private int n; //max de elementos
    private int counter; //contador de vertices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public AdjacencyListGraph(int n) {
        if (n <= 0) System.exit(1); //sale con status==1 (error)
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
        this.counter = 0; //inicializo contador de vértices
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if(isEmpty())
            throw new GraphException("Adjacency List Graph is Empty");
        //opcion-1
       /* for (int i = 0; i < counter; i++) {
            if(util.Utility.compare(vertexList[i].data, element)==0)
                return true;
        }*/
        //opcion-2
        return indexOf(element)!=-1;
        //return false;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if(isEmpty())
            throw new GraphException("Adjacency List Graph is Empty");
        return !vertexList[indexOf(a)].edgesList.isEmpty()
                && vertexList[indexOf(a)].edgesList.contains(new EdgeWeight(b, null));
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if(counter>=vertexList.length)
            throw new GraphException("Adjacency List Graph is Full");
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes ["+a+"] y ["+b+"]");
        vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b, null));
        //grafo no dirigido
        vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a, null));

    }

    public int indexOf(Object element){
        for (int i = 0; i < counter; i++) {
            if(util.Utility.compare(vertexList[i].data, element)==0)
                return i; //retorna la pos en el arreglo de objectos vertexList
        }
        return -1; //significa q la data de todos los vertices no coinciden con element
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if(!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes["+a+"] y ["+b+"]");
        updateEdgesListEdgeWeight(a, b, weight);
        //GRAFO NO DIRIGIDO
        updateEdgesListEdgeWeight(b, a, weight);
    }

    private void updateEdgesListEdgeWeight(Object a, Object b, Object weight) throws ListException {
        EdgeWeight ew = (EdgeWeight) vertexList[indexOf(a)].edgesList
                .getNode(new EdgeWeight(b, null)).getData();
        //setteo el peso en el campo respectivo
        ew.setWeight(weight);
        //ahora actualizo la info en la lista de aristas correspondiente
        vertexList[indexOf(a)].edgesList.getNode(new EdgeWeight(b, null))
                .setData(ew);
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes ["+a+"] y ["+b+"]");
        if(!containsEdge(a, b)){ //si no existe la arista
            vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b, weight));
            //grafo no dirigido
            vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a, weight));
        }
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if(isEmpty())
            throw new GraphException("Adjacency List Graph is Empty");
        if(containsVertex(element)){
            for (int i = 0; i < counter; i++) {
                if(util.Utility.compare(vertexList[i].data, element)==0) {
                    //ya lo encontro, ahora
                    //se debe suprimir el vertice a eliminar de todas las listas
                    //enlazadas de los otros vértices
                    for (int j = 0; j < counter; j++) {
                        if(containsEdge(vertexList[j].data, element))
                            removeEdge(vertexList[j].data, element);
                    }

                    //ahora, debemos suprimir el vértice
                    for (int j = i; j < counter-1; j++) {
                        vertexList[j] = vertexList[j+1];
                    }
                    counter--; //decrementamos el contador de vértices
                }
            }
        }
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if(!containsVertex(a)||!containsVertex(b))
            throw new GraphException("There's no some of the vertexes");
        if(!vertexList[indexOf(a)].edgesList.isEmpty()) {
            vertexList[indexOf(a)].edgesList.remove(new EdgeWeight(b, null));
        }
        //grafo no dirigido
        if(!vertexList[indexOf(b)].edgesList.isEmpty()){
            vertexList[indexOf(b)].edgesList.remove(new EdgeWeight(a, null));
        }
    }

    // Recorrido en profundidad
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);//marca todos los vertices como no vistados
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        stack.clear();
        stack.push(0); //lo apila
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
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        queue.clear();
        queue.enQueue(0); // encola el elemento
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
            vertexList[i].setVisited(value); //value==true o false
        }//for
    }

    private int adjacentVertexNotVisited(int index) throws ListException {
        Object vertexData = vertexList[index].data;
        for (int i = 0; i < counter; i++) {
            if(!vertexList[index].edgesList.isEmpty()
                    && vertexList[i].edgesList.contains(new EdgeWeight(vertexData, null))
                    && !vertexList[i].isVisited())
                return i;//retorna la posicion del vertice adyacente no visitado
        }//for i
        return -1;
    }

    @Override
    public String toString() {
        String result = "Adjacency List Graph Content...";
        //se muestran todos los vértices del grafo
        for (int i = 0; i < counter; i++) {
            result+="\nThe vextex in the position: "+i+" is: "+vertexList[i].data;
            if(!vertexList[i].edgesList.isEmpty())
                result+="\n......EDGES AND WEIGHTS: "+vertexList[i].edgesList.toString();
        }
        return result;
    }

    public void addEdgeWeightTwo(Object source, Object destination, Object edgeObject, Object weight) throws GraphException, ListException {
        // 1. Validación de que los vértices existen en el grafo
        int sourceIndex = indexOf(source);
        int destIndex = indexOf(destination);

        if (sourceIndex == -1 || destIndex == -1) {
            throw new GraphException("addEdgeWeight: One or both vertices do not exist in the graph. Source: " + source + ", Destination: " + destination);
        }

        // 2. Validación y caste de los objetos Edge y Weight
        if (!(edgeObject instanceof Edge)) {
            throw new GraphException("addEdgeWeight: The provided edgeObject is not an instance of domain.Edge.");
        }
        Edge edge = (Edge) edgeObject; // Cast a tu clase Edge

        if (!(weight instanceof Integer)) {
            throw new GraphException("addEdgeWeight: The provided weight is not an Integer.");
        }
        Integer edgeWeightValue = (Integer) weight; // Cast a Integer

        // 3. Comprobar si la arista ya existe para evitar duplicados
        // Esta comprobación debe ser robusta para grafos no dirigidos.
        // Si tu containsEdge ya maneja esto, excelente.
        if (containsEdge(source, destination)) {
            throw new GraphException("addEdgeWeight: Edge (" + source + ", " + destination + ") already exists in the graph.");
        }

        // 4. Crear el objeto EdgeWeight que encapsula la arista y su peso
        // Usamos el constructor de tu clase EdgeWeight (Object edge, Object weight)
        EdgeWeight newEdgeWeight = new EdgeWeight(edge, edgeWeightValue);

        // 5. Añadir el EdgeWeight a la lista de adyacencia del vértice de origen
        vertexList[sourceIndex].edgesList.add(newEdgeWeight);

        // 6. Para GRAFOS NO DIRIGIDOS: Añadir la arista recíproca al vértice de destino
        // Si tu grafo es dirigido, SIMPLEMENTE OMITE EL CÓDIGO A PARTIR DE AQUÍ (HASTA EL FINAL DEL MÉTODO).
        // Crear un nuevo Edge con los elementos invertidos para la representación en la lista de adyacencia del destino.
        // Esto es importante para que al recorrer desde el destino, se vea la arista hacia el origen.
        Edge reverseEdge = new Edge(destination, source);
        EdgeWeight reverseEdgeWeight = new EdgeWeight(reverseEdge, edgeWeightValue); // Mismo peso

        try {
            // Comprobar si la arista inversa ya existe (solo para no dirigidos)
            if (!containsEdge(destination, source)) {
                vertexList[destIndex].edgesList.add(reverseEdgeWeight);
            }
        } catch (ListException e) {
            throw new GraphException("Error adding reverse edge to destination vertex list: " + e.getMessage());
        }
    }

    public AdjacencyListGraph primMST(Object startVertexData) throws GraphException, ListException {
        if (isEmpty() || !containsVertex(startVertexData)) {
            throw new GraphException("Grafo vacío o vértice inicial no existe.");
        }

        AdjacencyListGraph mst = new AdjacencyListGraph(this.n);
        // 1. Añadir todos los vértices del grafo original al MST
        for (int i = 0; i < counter; i++) {
            if (vertexList[i] != null) {
                mst.addVertex(vertexList[i].data);
            }
        }

        Map<Object, Integer> key = new HashMap<>();
        Map<Object, Object> parent = new HashMap<>();
        PriorityQueue<EdgeWeight> minHeap = new PriorityQueue<>();
        Set<Object> visited = new HashSet<>();

        // Inicializar
        for (int i = 0; i < counter; i++) {
            if (vertexList[i] != null) {
                Object vertexData = vertexList[i].data;
                key.put(vertexData, Integer.MAX_VALUE);
                parent.put(vertexData, null);
            }
        }

        key.put(startVertexData, 0);
        minHeap.add(new EdgeWeight(new Edge(startVertexData, startVertexData), 0));


        while (!minHeap.isEmpty() && visited.size() < counter) { // Usar 'counter'
            EdgeWeight currentEdgeWeight = minHeap.poll();
            Edge currentEdge = (Edge) currentEdgeWeight.getEdge();
            Object u = null;

            if (visited.contains(currentEdge.getElementA()) && !visited.contains(currentEdge.getElementB())) {
                u = currentEdge.getElementB();
            } else if (!visited.contains(currentEdge.getElementA()) && visited.contains(currentEdge.getElementB())) {
                u = currentEdge.getElementA();
            } else if (!visited.contains(currentEdge.getElementA()) && !visited.contains(currentEdge.getElementB())) {
                // Esto ocurre para el primer vértice (startVertexData, startVertexData) o si el grafo es disconexo
                // Si la arista ficticia es (X,X) y X no ha sido visitado:
                if (util.Utility.compare(currentEdge.getElementA(), currentEdge.getElementB()) == 0 && util.Utility.compare(currentEdge.getElementA(), startVertexData) == 0) {
                    u = startVertexData;
                } else { // Caso de grafo disconexo o error lógico, saltar
                    continue;
                }
            } else { // Ambos ya visitados, saltar
                continue;
            }


            if (visited.contains(u)) { // Si ya fue añadido al MST, ignorar
                continue;
            }

            visited.add(u); // Marcar el vértice como visitado

            // Si u no es el vértice inicial (la arista ficticia), añade la arista real al mstGraph
            Object p = parent.get(u); // El padre de 'u' en el MST
            if (p != null) { // Si tiene un padre (no es el primer nodo)
                // La arista que conecta p con u es parte del MST
                Edge edgeToMst = new Edge(p, u);
                // Usa el peso actual de la arista (currentEdgeWeight.getWeight())
                mst.addEdgeWeightTwo(p, u, edgeToMst, (Integer) currentEdgeWeight.getWeight());
            }


            // Recorrer todos los adyacentes de 'u'
            int uIndex = indexOf(u);
            if (uIndex != -1 && vertexList[uIndex].edgesList != null) {
                SinglyLinkedList edgesOfU = vertexList[uIndex].edgesList;
                for (int i = 0; i < edgesOfU.size(); i++) {
                    EdgeWeight neighborEW = (EdgeWeight) edgesOfU.get(i);
                    Edge neighborEdge = (Edge) neighborEW.getEdge();
                    Object vNeighbor = null; // Vértice adyacente a 'u'

                    // Determinar el vecino 'vNeighbor' de la arista 'neighborEdge' con respecto a 'u'
                    if (util.Utility.compare(neighborEdge.getElementA(), u) == 0) {
                        vNeighbor = neighborEdge.getElementB();
                    } else if (util.Utility.compare(neighborEdge.getElementB(), u) == 0) {
                        vNeighbor = neighborEdge.getElementA();
                    }

                    if (vNeighbor != null) {
                        int weight = (Integer) neighborEW.getWeight();

                        // Si 'vNeighbor' no ha sido visitado y la arista (u,vNeighbor) ofrece un peso menor
                        // que el que 'vNeighbor' ya tenía para conectarse al MST
                        if (!visited.contains(vNeighbor) && weight < key.get(vNeighbor)) {
                            key.put(vNeighbor, weight);
                            parent.put(vNeighbor, u); // 'u' es ahora el padre de 'vNeighbor' en el MST
                            // Añadir la arista al minHeap para consideración futura
                            minHeap.add(new EdgeWeight(new Edge(u, vNeighbor), weight));
                        }
                    }
                }
            }
        }
        return mst;
    }

}