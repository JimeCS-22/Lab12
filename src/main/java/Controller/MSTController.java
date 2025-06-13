package Controller;

import domain.*;
import domain.list.ListException;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class MSTController {
    @javafx.fxml.FXML
    private RadioButton AdjacencyMatrix1;
    @javafx.fxml.FXML
    private RadioButton AdjacencyList;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Pane pane2;
    @javafx.fxml.FXML
    private Pane pane3;
    @javafx.fxml.FXML
    private RadioButton AdjacencyList1;
    @javafx.fxml.FXML
    private RadioButton AdjacencyMatrix;
    @javafx.fxml.FXML
    private RadioButton LinkedList;
    @javafx.fxml.FXML
    private AnchorPane AP;
    @javafx.fxml.FXML
    private Pane pane31;

    private AdjacencyListGraph graph; // El grafo principal

    private final int MAX_VERTICES = 10;
    private final int NUM_EDGES_TO_ADD = 15;

    @FXML
    public void initialize() {

        graph = new AdjacencyListGraph(MAX_VERTICES);

        AdjacencyList.setSelected(true);

        randomizeOnAction(null);
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {

        generateRandomGraph();
        drawGraph();

    }

    private void generateRandomGraph() {
        try {
            Random rand = new Random();
            graph.clear();

            // Usando SinglyLinkedList (NO GENÉRICA)
            SinglyLinkedList availableCharacters = new SinglyLinkedList();
            for (char c = 'A'; c <= 'Z'; c++) {
                availableCharacters.add(c);
            }

            for (int i = 0; i < MAX_VERTICES; i++) {
                if (availableCharacters.isEmpty()) break;
                int randomIndex = rand.nextInt(availableCharacters.size());
                Character charToRemove = (Character) availableCharacters.get(randomIndex);
                graph.addVertex(charToRemove);
                availableCharacters.remove(charToRemove);
            }

            SinglyLinkedList currentVertices = new SinglyLinkedList();
            for (int i = 0; i < graph.size(); i++) {
                currentVertices.add(graph.vertexList[i].data);
            }

            int edgesAdded = 0;
            long startTime = System.nanoTime();
            final long TIMEOUT = 5_000_000_000L; // 5 segundos de timeout

            while (edgesAdded < NUM_EDGES_TO_ADD && (System.nanoTime() - startTime < TIMEOUT)) {
                if (currentVertices.size() < 2) break;

                Object v1 = currentVertices.get(rand.nextInt(currentVertices.size()));
                Object v2 = currentVertices.get(rand.nextInt(currentVertices.size()));

                if (util.Utility.compare(v1, v2) != 0 && !graph.containsEdge(v1, v2)) {
                    int weight = rand.nextInt(50) + 1;
                    // Aquí es donde se crea la arista Edge, y se encapsula en EdgeWeight
                    Edge newEdge = new Edge(v1, v2); // Crea una instancia de tu clase Edge
                    graph.addEdgeWeightTwo(v1, v2, newEdge, weight); // NECESITAS ESTE MÉTODO EN TU GRAFO
                    edgesAdded++;
                }
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo aleatorio", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado", "Un error inesperado ocurrió: " + e.getMessage());
        }
    }

    private void drawGraph() {
        try {
            drawGraphContent(graph, pane3);
            pane31.getChildren().clear();
        } catch (Exception e) {
            showAlert("Error al dibujar el grafo", e.getMessage());
        }
    }

    private void drawGraphContent(AdjacencyListGraph currentGraph, Pane drawingPane) {
        if (currentGraph == null || currentGraph.isEmpty()) {
            drawingPane.getChildren().clear();
            return;
        }

        if (drawingPane.getProperties().get("listenersAdded") == null) {
            Runnable redrawAction = () -> {
                try {
                    double currentCenterX = drawingPane.getWidth() / 2;
                    double currentCenterY = drawingPane.getHeight() / 2;
                    double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
                    redrawGraphContent(currentGraph, currentCenterX, currentCenterY, currentRadius, drawingPane);
                } catch (Exception e) {
                    showAlert("Error al redibujar el grafo", e.getMessage());
                }
            };
            drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
            drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
            drawingPane.getProperties().put("listenersAdded", true);
        }

        double initialCenterX = drawingPane.getWidth() / 2;
        double initialCenterY = drawingPane.getHeight() / 2;
        double initialRadius = Math.min(initialCenterX, initialCenterY) * 0.8;

        try {
            redrawGraphContent(currentGraph, initialCenterX, initialCenterY, initialRadius, drawingPane);
        } catch (GraphException | ListException e) {
            showAlert("Error al dibujar el grafo inicial", e.getMessage());
        }
    }

    private void redrawGraphContent(AdjacencyListGraph currentGraph, double centerX, double centerY, double radius, Pane targetPane) throws GraphException, ListException {
        targetPane.getChildren().clear();

        SinglyLinkedList positions = new SinglyLinkedList();
        SinglyLinkedList verticesData = new SinglyLinkedList();

        for (int i = 0; i < currentGraph.size(); i++) {
            if (currentGraph.vertexList[i].data instanceof Character) {
                verticesData.add(currentGraph.vertexList[i].data);
            } else {
                System.err.println("Advertencia: Dato de vértice no es Character: " + currentGraph.vertexList[i].data);
                verticesData.add(String.valueOf(currentGraph.vertexList[i].data).charAt(0));
            }
        }
        int numNodes = verticesData.size();

        // 1. Dibujar nodos
        Map<Character, Circle> nodeCirclesForThisPane = new HashMap<>();
        for (int i = 0; i < numNodes; i++) {
            Character vertexData = (Character) verticesData.get(i);

            double angle = 2 * Math.PI * i / numNodes;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.add(new NodePosition<>(vertexData, x, y));

            Circle circle = new Circle(x, y, 20, Color.DEEPSKYBLUE);
            circle.setStroke(Color.BLACK);
            targetPane.getChildren().add(circle);
            nodeCirclesForThisPane.put(vertexData, circle);

            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
            text.setFill(Color.RED);
            text.setFont(new Font(12));
            targetPane.getChildren().add(text);
        }

        // 2. Dibujar aristas y pesos
        for (int i = 0; i < numNodes; i++) {
            NodePosition<Character> node1Pos = (NodePosition<Character>) positions.get(i);
            Object node1Data = node1Pos.value;

            for (int j = 0; j < numNodes; j++) {
                if (i == j) continue;

                NodePosition<Character> node2Pos = (NodePosition<Character>) positions.get(j);
                Object node2Data = node2Pos.value;

                // Solo dibuja una vez por par y verifica si existe la arista
                if (i < j && currentGraph.containsEdge(node1Data, node2Data)) {
                    Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
                    line.setStrokeWidth(2);
                    line.setStroke(Color.BLACK);

                    line.setOnMouseEntered(event -> {
                        line.setStroke(Color.RED);
                        line.setStrokeWidth(3);
                    });

                    line.setOnMouseExited(event -> {
                        line.setStroke(Color.BLACK);
                        line.setStrokeWidth(2);
                    });

                    targetPane.getChildren().add(line);
                    line.toBack();

                    // Obtener el peso usando la nueva lógica
                    int weight = getEdgeWeightFromAdjacencyListGraph(currentGraph, node1Data, node2Data);
                    Text weightText = new Text((node1Pos.x + node2Pos.x) / 2 + 5, (node1Pos.y + node2Pos.y) / 2 - 5, String.valueOf(weight));
                    weightText.setFill(Color.BLUE);
                    weightText.setFont(new Font(10));
                    targetPane.getChildren().add(weightText);
                }
            }
        }
    }

    // Método modificado para obtener el peso de la arista
    private int getEdgeWeightFromAdjacencyListGraph(AdjacencyListGraph graph, Object source, Object destination) throws GraphException, ListException {
        int sourceIndex = graph.indexOf(source);
        if (sourceIndex == -1) throw new GraphException("Source vertex not found: " + source);

        SinglyLinkedList edges = graph.vertexList[sourceIndex].edgesList;
        for (int i = 0; i < edges.size(); i++) {
            EdgeWeight ew = (EdgeWeight) edges.getNode(i).getData(); // Obtiene el EdgeWeight

            // Aquí es donde obtenemos el objeto Edge del EdgeWeight
            Object edgeObject = ew.getEdge();
            if (!(edgeObject instanceof Edge)) {
                // Esto es una validación de seguridad. Debería ser un Edge si tu grafo lo crea bien.
                throw new GraphException("EdgeWeight contains an object that is not of type domain.Edge.");
            }
            Edge edge = (Edge) edgeObject; // Realiza el cast a domain.Edge

            // Comparamos los elementos de la arista 'edge' con 'source' y 'destination'
            // Para grafos no dirigidos, el orden de source/destination puede estar invertido en el Edge
            if ((util.Utility.compare(edge.getElementA(), source) == 0 && util.Utility.compare(edge.getElementB(), destination) == 0) ||
                    (util.Utility.compare(edge.getElementA(), destination) == 0 && util.Utility.compare(edge.getElementB(), source) == 0)) {

                // Una vez que encontramos la arista correcta, devolvemos su peso
                return (Integer) ew.getWeight();
            }
        }
        throw new GraphException("Edge not found between " + source + " and " + destination);
    }




    @javafx.fxml.FXML
    public void AdjacencyListOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void adjacencyMatrizOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void LinkedListOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void handleScrollZoom(Event event) {
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
