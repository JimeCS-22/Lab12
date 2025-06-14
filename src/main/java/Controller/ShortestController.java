package Controller;

import domain.list.SinglyLinkedList;
import graph.*;
import domain.list.ListException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.*;
import javafx.scene.input.ScrollEvent;

public class ShortestController {
    @FXML private RadioButton AdjacencyList;
    @FXML private RadioButton AdjacencyMatrix;
    @FXML private RadioButton LinkedList;
    @FXML private Pane pane31;
    @FXML private TableView<DijkstraResult> dijkstraTableView;
    @FXML private TableColumn<DijkstraResult, Integer> positionColumn;
    @FXML private TableColumn<DijkstraResult, Object> vertexColumn;
    @FXML private TableColumn<DijkstraResult, Integer> distanceColumn;

    private AdjacencyListGraph adjacencyListGraph;
    private SinglyLinkedListGraph singlyLinkedListGraph;
    private AdjacencyMatrixGraph adjacencyMatrixGraph;
    private Graph currentActiveGraph;
    private ToggleGroup graphTypeGroup;
    private final int MAX_VERTICES = 10;

    @FXML
    public void initialize() {
        adjacencyListGraph = new AdjacencyListGraph(100);
        singlyLinkedListGraph = new SinglyLinkedListGraph();
        adjacencyMatrixGraph = new AdjacencyMatrixGraph(MAX_VERTICES);

        graphTypeGroup = new ToggleGroup();
        AdjacencyList.setToggleGroup(graphTypeGroup);
        LinkedList.setToggleGroup(graphTypeGroup);
        AdjacencyMatrix.setToggleGroup(graphTypeGroup);
        AdjacencyList.setSelected(true);
        currentActiveGraph = adjacencyListGraph;

        // Configurar las columnas de la tabla
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        vertexColumn.setCellValueFactory(new PropertyValueFactory<>("vertex"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Configurar listeners para los RadioButtons
        AdjacencyList.setOnAction(event -> {
            currentActiveGraph = adjacencyListGraph;
            randomizeOnAction(null);
        });
        LinkedList.setOnAction(event -> {
            currentActiveGraph = singlyLinkedListGraph;
            randomizeOnAction(null);
        });
        AdjacencyMatrix.setOnAction(event -> {
            currentActiveGraph = adjacencyMatrixGraph;
            randomizeOnAction(null);
        });

        randomizeOnAction(null); // Generar grafo inicial y actualizar tabla
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        generateRandomGraphForActiveType();
        drawGraph(currentActiveGraph, pane31);
        updateDijkstraResults();
    }

    private void generateRandomGraphForActiveType() {
        try {
            int numVertices = MAX_VERTICES;
            double edgeDensity = 0.4;
            currentActiveGraph.clear();

            if (currentActiveGraph instanceof AdjacencyListGraph) {
                ((AdjacencyListGraph) currentActiveGraph).generateRandomGraph(numVertices, edgeDensity);
            } else if (currentActiveGraph instanceof SinglyLinkedListGraph) {
                ((SinglyLinkedListGraph) currentActiveGraph).generateRandomGraph(numVertices, edgeDensity);
            } else if (currentActiveGraph instanceof AdjacencyMatrixGraph) {
                ((AdjacencyMatrixGraph) currentActiveGraph).generateRandomGraph(numVertices, edgeDensity);
            } else {
                showAlert("Error de Implementación", "Tipo de grafo no soportado para generación aleatoria.");
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo aleatorio", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado", "Un error inesperado ocurrió al generar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawGraph(Graph graphToDraw, Pane targetPane) {
        try {
            targetPane.getChildren().clear();
            if (targetPane.getProperties().get("listenersAdded") == null) {
                Runnable redrawAction = () -> {
                    try {
                        redrawGraphContent(graphToDraw, targetPane);
                        updateDijkstraResults(); // Actualizar tabla al redimensionar
                    } catch (ListException | GraphException e) {
                        showAlert("Error al redibujar el grafo al redimensionar", e.getMessage());
                    }
                };
                targetPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
                targetPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
                targetPane.getProperties().put("listenersAdded", true);
            }
            redrawGraphContent(graphToDraw, targetPane);
            updateDijkstraResults(); // Actualizar tabla al dibujar inicialmente
        } catch (Exception e) {
            showAlert("Error al dibujar el grafo", e.getMessage());
            e.printStackTrace();
        }
    }

    private void redrawGraphContent(Graph currentGraph, Pane targetPane) throws GraphException, ListException {
        targetPane.getChildren().clear();

        Map<Object, NodePosition<?>> positionsMap = new HashMap<>();
        List<Object> verticesData = new ArrayList<>();

        if (currentGraph instanceof AdjacencyListGraph) {
            AdjacencyListGraph adjGraph = (AdjacencyListGraph) currentGraph;
            for (int i = 0; i < adjGraph.size(); i++) {
                if (adjGraph.vertexList[i] != null) {
                    verticesData.add(adjGraph.vertexList[i].data);
                }
            }
        } else if (currentGraph instanceof SinglyLinkedListGraph) {
            SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) currentGraph;
            for (int i = 1; i <= sllGraph.size(); i++) {
                verticesData.add(sllGraph.getVertexDataByIndex(i));
            }
        } else if (currentGraph instanceof AdjacencyMatrixGraph) {
            AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) currentGraph;
            for (int i = 0; i < amGraph.size(); i++) {
                if (amGraph.vertexList[i] != null) {
                    verticesData.add(amGraph.getVertexData(i));
                }
            }
        }

        int numNodes = verticesData.size();
        double centerX = targetPane.getWidth() / 2;
        double centerY = targetPane.getHeight() / 2;
        double radius = Math.min(centerX, centerY) * 0.8;
        if (numNodes == 0) radius = 0;

        for (int i = 0; i < numNodes; i++) {
            Object vertexData = verticesData.get(i);

            double angle = 2 * Math.PI * i / numNodes;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            NodePosition<?> nodePos = new NodePosition<>(vertexData, x, y);
            positionsMap.put(vertexData, nodePos);

            Circle circle = new Circle(x, y, 20, Color.AQUAMARINE);
            circle.setStroke(Color.BLACK);
            targetPane.getChildren().add(circle);

            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
            text.setFill(Color.BLACK);
            text.setFont(new Font(12));
            targetPane.getChildren().add(text);
        }

        Set<String> drawnEdgesCanonical = new HashSet<>();

        if (currentGraph instanceof AdjacencyListGraph) {
            AdjacencyListGraph adjGraph = (AdjacencyListGraph) currentGraph;
            for (int i = 0; i < adjGraph.size(); i++) {
                Vertex currentVertex = adjGraph.vertexList[i];
                if (currentVertex == null) continue;

                Object node1Data = currentVertex.data;
                NodePosition<?> node1Pos = positionsMap.get(node1Data);

                if (node1Pos == null) continue;

                SinglyLinkedList edgesOfNode1 = currentVertex.edgesList;
                for (int k = 0; k < edgesOfNode1.size(); k++) {
                    EdgeWeight ew = (EdgeWeight) edgesOfNode1.get(k);
                    Edge currentEdge = (Edge) ew.getEdge();

                    String canonicalEdgeString;
                    if (util.Utility.compare(currentEdge.getElementA(), currentEdge.getElementB()) < 0) {
                        canonicalEdgeString = currentEdge.getElementA().toString() + "-" + currentEdge.getElementB().toString();
                    } else {
                        canonicalEdgeString = currentEdge.getElementB().toString() + "-" + currentEdge.getElementA().toString();
                    }

                    if (drawnEdgesCanonical.contains(canonicalEdgeString)) {
                        continue;
                    }

                    Object targetNodeData = null;
                    if (util.Utility.compare(currentEdge.getElementA(), node1Data) == 0) {
                        targetNodeData = currentEdge.getElementB();
                    } else {
                        targetNodeData = currentEdge.getElementA();
                    }

                    NodePosition<?> node2Pos = positionsMap.get(targetNodeData);

                    drawSingleEdge(node1Pos, node2Pos, (Integer) ew.getWeight(), targetPane, drawnEdgesCanonical);
                }
            }
        } else if (currentGraph instanceof SinglyLinkedListGraph) {
            SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) currentGraph;
            SinglyLinkedList graphVertexList = sllGraph.getVertexList();

            for (int i = 0; i < graphVertexList.size(); i++) {
                Vertex currentVertex = (Vertex) graphVertexList.getNode(i).data;
                Object node1Data = currentVertex.data;
                NodePosition<?> node1Pos = positionsMap.get(node1Data);

                if (node1Pos == null) continue;

                SinglyLinkedList edgesOfNode1 = currentVertex.edgesList;
                for (int k = 0; k < edgesOfNode1.size(); k++) {
                    EdgeWeight ew = (EdgeWeight) edgesOfNode1.get(k);
                    Object targetNodeData = ew.getEdge();

                    drawSingleEdge(node1Pos, positionsMap.get(targetNodeData), (Integer) ew.getWeight(), targetPane, drawnEdgesCanonical);
                }
            }
        } else if (currentGraph instanceof AdjacencyMatrixGraph) {
            AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) currentGraph;
            for (int i = 0; i < amGraph.size(); i++) {
                Object node1Data = amGraph.getVertexData(i);
                NodePosition<?> node1Pos = positionsMap.get(node1Data);

                if (node1Pos == null) continue;

                for (int j = i + 1; j < amGraph.size(); j++) {
                    Object node2Data = amGraph.getVertexData(j);
                    if (amGraph.containsEdge(node1Data, node2Data)) {
                        NodePosition<?> node2Pos = positionsMap.get(node2Data);
                        Integer weight = (Integer) amGraph.getEdgeWeight(node1Data, node2Data);

                        drawSingleEdge(node1Pos, node2Pos, weight, targetPane, drawnEdgesCanonical);
                    }
                }
            }
        }
    }

    private void drawSingleEdge(NodePosition<?> node1Pos, NodePosition<?> node2Pos, Integer weight, Pane targetPane, Set<String> drawnEdgesCanonical) {
        if (node1Pos == null || node2Pos == null) return;

        String canonicalEdgeString;
        if (util.Utility.compare(node1Pos.value, node2Pos.value) < 0) {
            canonicalEdgeString = node1Pos.value.toString() + "-" + node2Pos.value.toString();
        } else {
            canonicalEdgeString = node2Pos.value.toString() + "-" + node1Pos.value.toString();
        }

        if (drawnEdgesCanonical.contains(canonicalEdgeString)) {
            return;
        }

        Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
        line.setStrokeWidth(2);
        line.setStroke(Color.DARKSEAGREEN);

        targetPane.getChildren().add(line);
        line.toBack();
        Text weightText = new Text((node1Pos.x + node2Pos.x) / 2 + 5, (node1Pos.y + node2Pos.y) / 2 - 5, String.valueOf(weight));
        weightText.setFill(Color.BLACK);
        weightText.setFont(new Font(10));
        targetPane.getChildren().add(weightText);

        drawnEdgesCanonical.add(canonicalEdgeString);
    }

    private void updateDijkstraResults() {
        try {
            if (currentActiveGraph == null || currentActiveGraph.isEmpty()) {
                dijkstraTableView.getItems().clear();
                return;
            }

            Object startVertex = getFirstVertex(currentActiveGraph);
            if (startVertex == null) {
                dijkstraTableView.getItems().clear();
                return;
            }

            Map<Object, Integer> distances = dijkstra(currentActiveGraph, startVertex);
            displayDijkstraResults(distances);

        } catch (Exception e) {
            showAlert("Error en Dijkstra", "Ocurrió un error al ejecutar Dijkstra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Object getFirstVertex(Graph graph) throws GraphException, ListException {
        if (graph instanceof AdjacencyListGraph) {
            AdjacencyListGraph adjGraph = (AdjacencyListGraph) graph;
            for (int i = 0; i < adjGraph.size(); i++) {
                if (adjGraph.vertexList[i] != null) {
                    return adjGraph.vertexList[i].data;
                }
            }
        } else if (graph instanceof SinglyLinkedListGraph) {
            SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) graph;
            if (sllGraph.size() > 0) {
                return sllGraph.getVertexDataByIndex(1);
            }
        } else if (graph instanceof AdjacencyMatrixGraph) {
            AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) graph;
            for (int i = 0; i < amGraph.size(); i++) {
                if (amGraph.vertexList[i] != null) {
                    return amGraph.getVertexData(i);
                }
            }
        }
        return null;
    }

    private Map<Object, Integer> dijkstra(Graph graph, Object startVertex) throws GraphException {
        Map<Object, Integer> distances = new HashMap<>();
        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        Set<Object> visited = new HashSet<>();

        List<Object> vertices = getAllVertices(graph);
        for (Object vertex : vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(startVertex, 0);
        priorityQueue.add(new NodeDistance(startVertex, 0));

        while (!priorityQueue.isEmpty()) {
            NodeDistance current = priorityQueue.poll();
            Object currentVertex = current.vertex;

            if (visited.contains(currentVertex)) {
                continue;
            }
            visited.add(currentVertex);

            List<Object> neighbors = getNeighbors(graph, currentVertex);
            for (Object neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    int edgeWeight = getEdgeWeight(graph, currentVertex, neighbor);
                    int newDistance = distances.get(currentVertex) + edgeWeight;

                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        priorityQueue.add(new NodeDistance(neighbor, newDistance));
                    }
                }
            }
        }

        return distances;
    }

    private List<Object> getAllVertices(Graph graph) throws GraphException {
        try {
            List<Object> vertices = new ArrayList<>();

            if (graph instanceof AdjacencyListGraph) {
                AdjacencyListGraph adjGraph = (AdjacencyListGraph) graph;
                for (int i = 0; i < adjGraph.size(); i++) {
                    if (adjGraph.vertexList[i] != null) {
                        vertices.add(adjGraph.vertexList[i].data);
                    }
                }
            } else if (graph instanceof SinglyLinkedListGraph) {
                SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) graph;
                for (int i = 1; i <= sllGraph.size(); i++) {
                    vertices.add(sllGraph.getVertexDataByIndex(i));
                }
            } else if (graph instanceof AdjacencyMatrixGraph) {
                AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) graph;
                for (int i = 0; i < amGraph.size(); i++) {
                    if (amGraph.vertexList[i] != null) {
                        vertices.add(amGraph.getVertexData(i));
                    }
                }
            }

            return vertices;
        } catch (ListException e) {
            throw new GraphException("Error getting all vertices: " + e.getMessage());
        }
    }

    private List<Object> getNeighbors(Graph graph, Object vertex) throws GraphException {
        List<Object> neighbors = new ArrayList<>();

        try {
            if (graph instanceof AdjacencyListGraph) {
                AdjacencyListGraph adjGraph = (AdjacencyListGraph) graph;
                Vertex v = adjGraph.getVertex(vertex);
                if (v != null) {
                    SinglyLinkedList edges = v.edgesList;
                    for (int i = 0; i < edges.size(); i++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(i);
                        Edge edge = (Edge) ew.getEdge();
                        Object neighbor = edge.getElementA().equals(vertex) ? edge.getElementB() : edge.getElementA();
                        neighbors.add(neighbor);
                    }
                }
            } else if (graph instanceof SinglyLinkedListGraph) {
                SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) graph;
                Vertex v = sllGraph.getVertex(vertex);
                if (v != null) {
                    SinglyLinkedList edges = v.edgesList;
                    for (int i = 0; i < edges.size(); i++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(i);
                        neighbors.add(ew.getEdge());
                    }
                }
            } else if (graph instanceof AdjacencyMatrixGraph) {
                AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) graph;
                for (Object otherVertex : getAllVertices(graph)) {
                    if (!otherVertex.equals(vertex) && amGraph.containsEdge(vertex, otherVertex)) {
                        neighbors.add(otherVertex);
                    }
                }
            }
        } catch (ListException e) {
            throw new GraphException("Error getting neighbors: " + e.getMessage());
        }

        return neighbors;
    }

    private int getEdgeWeight(Graph graph, Object vertex1, Object vertex2) throws GraphException {
        try {
            if (graph instanceof AdjacencyListGraph) {
                AdjacencyListGraph adjGraph = (AdjacencyListGraph) graph;
                Vertex v = adjGraph.getVertex(vertex1);
                if (v != null) {
                    SinglyLinkedList edges = v.edgesList;
                    for (int i = 0; i < edges.size(); i++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(i);
                        Edge edge = (Edge) ew.getEdge();
                        if ((edge.getElementA().equals(vertex1) && edge.getElementB().equals(vertex2)) ||
                                (edge.getElementA().equals(vertex2) && edge.getElementB().equals(vertex1))) {
                            return (Integer) ew.getWeight();
                        }
                    }
                }
            } else if (graph instanceof SinglyLinkedListGraph) {
                SinglyLinkedListGraph sllGraph = (SinglyLinkedListGraph) graph;
                Vertex v = sllGraph.getVertex(vertex1);
                if (v != null) {
                    SinglyLinkedList edges = v.edgesList;
                    for (int i = 0; i < edges.size(); i++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(i);
                        if (ew.getEdge().equals(vertex2)) {
                            return (Integer) ew.getWeight();
                        }
                    }
                }
            } else if (graph instanceof AdjacencyMatrixGraph) {
                AdjacencyMatrixGraph amGraph = (AdjacencyMatrixGraph) graph;
                return (Integer) amGraph.getEdgeWeight(vertex1, vertex2);
            }
        } catch (ListException e) {
            throw new GraphException("Error getting edge weight: " + e.getMessage());
        }

        throw new GraphException("Arista no encontrada entre " + vertex1 + " y " + vertex2);
    }

    private void displayDijkstraResults(Map<Object, Integer> distances) {
        List<Map.Entry<Object, Integer>> sortedEntries = new ArrayList<>(distances.entrySet());
        sortedEntries.sort(Comparator.comparingInt(Map.Entry::getValue));

        ObservableList<DijkstraResult> data = FXCollections.observableArrayList();
        int position = 0;
        for (Map.Entry<Object, Integer> entry : sortedEntries) {
            data.add(new DijkstraResult(position++, entry.getKey(), entry.getValue()));
        }

        dijkstraTableView.setItems(data);
    }

    @FXML
    private void handleScrollZoom(ScrollEvent event) {
        double zoomFactor = 1.1;
        if (event.getDeltaY() < 0) {
            zoomFactor = 1 / zoomFactor;
        }
        pane31.setScaleX(pane31.getScaleX() * zoomFactor);
        pane31.setScaleY(pane31.getScaleY() * zoomFactor);
        event.consume();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clases internas para manejar los resultados y la cola de prioridad
    private static class NodeDistance {
        Object vertex;
        int distance;

        public NodeDistance(Object vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    public static class DijkstraResult {
        private final SimpleIntegerProperty position;
        private final SimpleObjectProperty<Object> vertex;
        private final SimpleIntegerProperty distance;

        public DijkstraResult(int position, Object vertex, int distance) {
            this.position = new SimpleIntegerProperty(position);
            this.vertex = new SimpleObjectProperty<>(vertex);
            this.distance = new SimpleIntegerProperty(distance);
        }

        public int getPosition() { return position.get(); }
        public Object getVertex() { return vertex.get(); }
        public int getDistance() { return distance.get(); }
    }

    // Métodos de los RadioButtons (pueden mantenerse vacíos si el comportamiento ya está en initialize)
    @FXML
    public void AdjacencyListOnAction(ActionEvent actionEvent) {}
    @FXML
    public void adjacencyMatrizOnAction(ActionEvent actionEvent) {}
    @FXML
    public void LinkedListOnAction(ActionEvent actionEvent) {}
}