package Controller;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import graph.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class MSTController {
    @FXML
    private RadioButton AdjacencyList;
    @FXML
    private Pane mainPain;
    @FXML
    private Pane pane2;
    @FXML
    private Pane pane3;
    @FXML
    private RadioButton AdjacencyMatrix;
    @FXML
    private RadioButton LinkedList;
    @FXML
    private AnchorPane AP;
    @FXML
    private Pane pane31;

    private AdjacencyListGraph adjacencyListGraph;
    private SinglyLinkedListGraph singlyLinkedListGraph;
    private AdjacencyMatrixGraph adjacencyMatrixGraph;
    private Graph currentActiveGraph;
    private ToggleGroup graphTypeGroup;
    private final int MAX_VERTICES = 10;
    @FXML
    private RadioButton Prim;
    @FXML
    private RadioButton Kruskal;
    private ToggleGroup mstAlgorithmGroup;

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

        mstAlgorithmGroup = new ToggleGroup();
        Prim.setToggleGroup(mstAlgorithmGroup);
        Kruskal.setToggleGroup(mstAlgorithmGroup);
        if (Prim != null) {
            Prim.setSelected(true);
        } else if (Kruskal != null) {
            Kruskal.setSelected(true);
        }

        // Action handlers for graph type selection
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
            showAlert("Tipo de Grafo", "Se ha seleccionado Matriz de Adyacencia.");
        });

        randomizeOnAction(null);
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        generateRandomGraphForActiveType();
        drawGraph(currentActiveGraph, pane31);
        generateAndDrawMST();
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
            } else if (currentActiveGraph instanceof AdjacencyMatrixGraph) { // NEW: Handle AdjacencyMatrixGraph
                ((AdjacencyMatrixGraph) currentActiveGraph).generateRandomGraph(numVertices, edgeDensity);
            } else {
                showAlert("Error de Implementación", "Tipo de grafo no soportado para generación aleatoria.");
                return;
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo aleatorio", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado", "Un error inesperado ocurrió al generar el grafo: " + e.getMessage());
            e.printStackTrace(); // Added for more detailed stack trace
        }
    }

    private void generateAndDrawMST() {
        try {
            pane3.getChildren().clear();

            if (currentActiveGraph == null || currentActiveGraph.isEmpty()) {
                showAlert("Grafo vacío", "El grafo original está vacío, no se puede generar un MST.");
                return;
            }

            Graph resultMST = null;
            Object startVertex = null; // Declare startVertex here for wider scope

            // Get a start vertex for Prim's algorithm. For Kruskal, it's not needed.
            if (currentActiveGraph.size() > 0) {
                if (currentActiveGraph instanceof AdjacencyListGraph) {
                    startVertex = ((AdjacencyListGraph) currentActiveGraph).getVertexDataByIndex(0);
                } else if (currentActiveGraph instanceof SinglyLinkedListGraph) {
                    startVertex = ((SinglyLinkedListGraph) currentActiveGraph).getVertexDataByIndex(1); // 1-based index
                } else if (currentActiveGraph instanceof AdjacencyMatrixGraph) { // NEW: Get start vertex for AdjacencyMatrixGraph
                    startVertex = ((AdjacencyMatrixGraph) currentActiveGraph).getVertexData(0); // 0-based index
                }
            }

            if (startVertex == null && Prim.isSelected()) { // Only check if Prim is selected
                showAlert("Grafo Invalido", "El grafo no tiene suficientes vértices para calcular un MST con Prim.");
                return;
            }

            if (Prim.isSelected()) {

                if (currentActiveGraph instanceof AdjacencyListGraph) {
                    resultMST = ((AdjacencyListGraph) currentActiveGraph).primMST(startVertex);
                } else if (currentActiveGraph instanceof SinglyLinkedListGraph) {
                    resultMST = ((SinglyLinkedListGraph) currentActiveGraph).primMST(startVertex);
                } else if (currentActiveGraph instanceof AdjacencyMatrixGraph) { // NEW: Prim for AdjacencyMatrixGraph
                    resultMST = ((AdjacencyMatrixGraph) currentActiveGraph).primMST(startVertex);
                }

            } else if (Kruskal.isSelected()) {
                if (currentActiveGraph instanceof AdjacencyListGraph) {
                    resultMST = ((AdjacencyListGraph) currentActiveGraph).kruskalMST();
                } else if (currentActiveGraph instanceof SinglyLinkedListGraph) {
                    resultMST = ((SinglyLinkedListGraph) currentActiveGraph).kruskalMST();
                } else if (currentActiveGraph instanceof AdjacencyMatrixGraph) { // NEW: Kruskal for AdjacencyMatrixGraph
                    resultMST = ((AdjacencyMatrixGraph) currentActiveGraph).kruskalMST();
                }
            } else {
                showAlert("Selección de algoritmo", "Por favor, selecciona Kruskal o Prim para el MST.");
                return;
            }

            if (resultMST != null) {
                drawGraph(resultMST, pane3);
            } else {
                pane3.getChildren().clear();
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al generar o dibujar el MST", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado al generar el MST", "Un error inesperado ocurrió: " + e.getMessage());
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
                    } catch (ListException | GraphException e) {
                        showAlert("Error al redibujar el grafo al redimensionar", e.getMessage());
                    }
                };
                targetPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
                targetPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
                targetPane.getProperties().put("listenersAdded", true);
            }
            redrawGraphContent(graphToDraw, targetPane);
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

            Circle circle = new Circle(x, y, 20, Color.DEEPSKYBLUE);
            circle.setStroke(Color.BLACK);
            targetPane.getChildren().add(circle);

            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
            text.setFill(Color.RED);
            text.setFont(new Font(12));
            targetPane.getChildren().add(text);
        }


        Set<String> drawnEdgesCanonical = new HashSet<>();


        if (currentGraph instanceof AdjacencyListGraph) {
            AdjacencyListGraph adjGraph = (AdjacencyListGraph) currentGraph;
            for (int i = 0; i < adjGraph.size(); i++) {
                Vertex currentVertex = adjGraph.vertexList[i];
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
        Text weightText = new Text((node1Pos.x + node2Pos.x) / 2 + 5, (node1Pos.y + node2Pos.y) / 2 - 5, String.valueOf(weight));
        weightText.setFill(Color.BLUE);
        weightText.setFont(new Font(10));
        targetPane.getChildren().add(weightText);

        drawnEdgesCanonical.add(canonicalEdgeString); // Mark as drawn
    }


    @FXML
    public void AdjacencyListOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void adjacencyMatrizOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void LinkedListOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void handleScrollZoom(javafx.event.Event event) {
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void KrusalOnAction(ActionEvent actionEvent) {
        if (Kruskal.isSelected()) {
            Prim.setSelected(false);
            generateAndDrawMST();
        } else {
            if (!Prim.isSelected()) {
                Prim.setSelected(true);
                generateAndDrawMST();
            }
        }
    }

    @FXML
    public void PrimOnAction(ActionEvent actionEvent) {
        if (Prim.isSelected()) {
            Kruskal.setSelected(false);
            generateAndDrawMST();
        } else {
            // Ensure at least one algorithm is always selected
            if (!Kruskal.isSelected()) {
                Kruskal.setSelected(true);
                generateAndDrawMST();
            }
        }
    }
}