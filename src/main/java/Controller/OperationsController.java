package Controller;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import graph.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.TextInputDialog;
import util.FXUtility;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OperationsController {
    @FXML
    private TextArea TextResult;
    @FXML
    private Pane mainPain;
    @FXML
    private Pane pane2;
    @FXML
    private Pane pane3;
    @FXML
    private AnchorPane AP;
    @FXML
    private RadioButton AdjacencyList;
    @FXML
    private RadioButton AdjacencyMatrix;
    @FXML
    private RadioButton LinkedList;

    private ToggleGroup tourTypeGroup;

    @FXML private Label edgeInfoLabel;

    private DirectedAdjacencyListGraph adjacencyListGraph;
    private DirectedAdjacencyMatrixGraph adjacencyMatrixGraph;
    private DirectedSinglyLinkedListGraph singlyLinkedListGraph;

    private Graph currentActiveGraph;
    private ToggleGroup graphTypeGroup;

    private final int NUM_VERTICES = 10;

    private List<Integer> matrixVertexDataPool;
    private List<Character> listVertexDataPool;
    private List<String> linkedListVertexDataPool;

    @FXML
    public void initialize() {
        adjacencyListGraph = new DirectedAdjacencyListGraph(NUM_VERTICES);
        adjacencyMatrixGraph = new DirectedAdjacencyMatrixGraph(NUM_VERTICES);
        singlyLinkedListGraph = new DirectedSinglyLinkedListGraph();

        graphTypeGroup = new ToggleGroup();
        AdjacencyList.setToggleGroup(graphTypeGroup);
        AdjacencyMatrix.setToggleGroup(graphTypeGroup);
        LinkedList.setToggleGroup(graphTypeGroup);

        AdjacencyList.setSelected(true);
        currentActiveGraph = adjacencyListGraph;

        AdjacencyList.setOnAction(this::AdjacencyListOnAction);
        AdjacencyMatrix.setOnAction(this::adjacencyMatrizOnAction);
        LinkedList.setOnAction(this::LinkedListOnAction);

        initializeVertexDataPools();

        TextResult.setText(currentActiveGraph.toString());

        Platform.runLater(() -> {
            randomizeOnAction(null); // Esto generará y dibujará el grafo
            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText(""); // Limpiar el label de info de arista al inicio
            }
        });
    }

    private void initializeVertexDataPools() {
        matrixVertexDataPool = new ArrayList<>();
        Set<Integer> uniqueNumbers = new HashSet<>();
        while (uniqueNumbers.size() < NUM_VERTICES) {
            uniqueNumbers.add(ThreadLocalRandom.current().nextInt(0, 100));
        }
        matrixVertexDataPool.addAll(uniqueNumbers);

        listVertexDataPool = new ArrayList<>();
        Set<Character> uniqueChars = new HashSet<>();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (uniqueChars.size() < NUM_VERTICES) {
            uniqueChars.add(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        listVertexDataPool.addAll(uniqueChars);

        List<String> monuments = Arrays.asList(
                "Eiffel", "Colosseum", "Pyramids", "Machu Picchu", "Taj Mahal",
                "Great Wall", "Statue Liberty", "Christ Redeemer", "Petra", "Acropolis",
                "Stonehenge", "Chichen Itza", "Big Ben", "Sagrada Familia", "Kremlin",
                "Louvre", "Forbidden City", "Sydney Opera", "Golden Gate", "Burj Khalifa"
        );
        Collections.shuffle(monuments);
        linkedListVertexDataPool = monuments.subList(0, NUM_VERTICES);
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        generateRandomGraph();
        drawGraph(currentActiveGraph, pane3);
        TextResult.setText(currentActiveGraph.toString());
        if (edgeInfoLabel != null) {
            edgeInfoLabel.setText("");
        }
    }

    private void generateRandomGraph() {
        try {
            currentActiveGraph.clear();

            List<?> currentDataPool;
            int minWeight, maxWeight;

            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                currentDataPool = matrixVertexDataPool;
                minWeight = 1; maxWeight = 50;
            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                currentDataPool = listVertexDataPool;
                minWeight = 51; maxWeight = 100;
            } else if (currentActiveGraph instanceof DirectedSinglyLinkedListGraph) {
                currentDataPool = linkedListVertexDataPool;
                minWeight = 101; maxWeight = 150;
            } else {
                currentDataPool = listVertexDataPool;
                minWeight = 51; maxWeight = 100;
            }

            Collections.shuffle(currentDataPool);
            for (int i = 0; i < NUM_VERTICES; i++) {
                currentActiveGraph.addVertex(currentDataPool.get(i));
            }

            for (int i = 0; i < NUM_VERTICES * 2; i++) {
                Object source = currentDataPool.get(ThreadLocalRandom.current().nextInt(NUM_VERTICES));
                Object dest = currentDataPool.get(ThreadLocalRandom.current().nextInt(NUM_VERTICES));
                int weight = ThreadLocalRandom.current().nextInt(minWeight, maxWeight + 1);
                if (!source.equals(dest) && !currentActiveGraph.containsEdge(source, dest)) {
                    currentActiveGraph.addEdgeWeight(source, dest, weight);
                }
            }
        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo", e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawGraph(Graph graphToDraw, Pane targetPane) {
        targetPane.getChildren().clear();
        Map<Object, NodePosition<?>> positionsMap = new HashMap<>();
        List<Object> verticesData = new ArrayList<>();

        try {
            if (graphToDraw instanceof DirectedAdjacencyListGraph) {
                DirectedAdjacencyListGraph adjGraph = (DirectedAdjacencyListGraph) graphToDraw;
                for (int i = 0; i < adjGraph.size(); i++) {
                    if (adjGraph.getVertexData(i) != null) {
                        verticesData.add(adjGraph.getVertexData(i));
                    }
                }
            } else if (graphToDraw instanceof DirectedAdjacencyMatrixGraph) {
                DirectedAdjacencyMatrixGraph amGraph = (DirectedAdjacencyMatrixGraph) graphToDraw;
                for (int i = 0; i < amGraph.size(); i++) {
                    if (amGraph.getVertexData(i) != null) {
                        verticesData.add(amGraph.getVertexData(i));
                    }
                }
            } else if (graphToDraw instanceof DirectedSinglyLinkedListGraph) {
                DirectedSinglyLinkedListGraph sllGraph = (DirectedSinglyLinkedListGraph) graphToDraw;
                SinglyLinkedList sllVertexList = sllGraph.getVertexList();
                for (int i = 0; i < sllVertexList.size(); i++) {
                    Vertex v = (Vertex) sllVertexList.getNode(i).data;
                    if (v != null) {
                        verticesData.add(v.data);
                    }
                }
            }

            int numNodes = verticesData.size();
            if (numNodes == 0) return;

            double centerX = targetPane.getWidth() / 2;
            double centerY = targetPane.getHeight() / 2;
            double radius = Math.min(centerX, centerY) * 0.8;
            if (radius <= 0) radius = 100;
            
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

            for (Object sourceData : verticesData) {
                NodePosition<?> sourcePos = positionsMap.get(sourceData);
                if (sourcePos == null) continue;

                for (Object targetData : verticesData) {
                    if (sourceData.equals(targetData)) continue;

                    if (graphToDraw.containsEdge(sourceData, targetData)) {
                        NodePosition<?> targetPos = positionsMap.get(targetData);
                        Object weight = null;

                        if (graphToDraw instanceof DirectedAdjacencyListGraph) {
                            Vertex currentVertex = ((DirectedAdjacencyListGraph) graphToDraw).getVertexObject(
                                    ((DirectedAdjacencyListGraph) graphToDraw).indexOf(sourceData));
                            if (currentVertex != null) {
                                SinglyLinkedList edgesOfSource = currentVertex.edgesList;
                                for (int k = 0; k < edgesOfSource.size(); k++) {
                                    EdgeWeight ew = (EdgeWeight) edgesOfSource.get(k);
                                    if (util.Utility.compare(ew.getEdge(), targetData) == 0) {
                                        weight = ew.getWeight();
                                        break;
                                    }
                                }
                            }
                        } else if (graphToDraw instanceof DirectedAdjacencyMatrixGraph) {
                            weight = ((DirectedAdjacencyMatrixGraph) graphToDraw).getEdgeWeight(sourceData, targetData);
                        } else if (graphToDraw instanceof DirectedSinglyLinkedListGraph) {
                            Vertex currentVertex = null;
                            SinglyLinkedList sllVertexList = ((DirectedSinglyLinkedListGraph) graphToDraw).getVertexList();
                            for (int i = 0; i < sllVertexList.size(); i++) {
                                Vertex v = (Vertex) sllVertexList.getNode(i).data;
                                if (util.Utility.compare(v.data, sourceData) == 0) {
                                    currentVertex = v;
                                    break;
                                }
                            }

                            if (currentVertex != null) {
                                SinglyLinkedList edgesOfSource = currentVertex.edgesList;
                                for (int k = 0; k < edgesOfSource.size(); k++) {
                                    EdgeWeight ew = (EdgeWeight) edgesOfSource.get(k);
                                    if (util.Utility.compare(ew.getEdge(), targetData) == 0) {
                                        weight = ew.getWeight();
                                        break;
                                    }
                                }
                            }
                        }

                        if (targetPos != null && weight instanceof Integer) {
                            drawDirectedEdge(sourcePos, targetPos, (Integer) weight, targetPane, sourceData, targetData);
                        }
                    }
                }
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al dibujar el grafo", e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawDirectedEdge(NodePosition<?> sourcePos, NodePosition<?> targetPos, int weight, Pane targetPane, Object sourceData, Object targetData) {
        double deltaX = targetPos.x - sourcePos.x;
        double deltaY = targetPos.y - sourcePos.y;
        double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double unitDx = deltaX / dist;
        double unitDy = deltaY / dist;

        double startX = sourcePos.x + unitDx * 20;
        double startY = sourcePos.y + unitDy * 20;
        double endX = targetPos.x - unitDx * 20;
        double endY = targetPos.y - unitDy * 20;

        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(2);
        line.setStroke(Color.DARKSEAGREEN);

        double arrowSize = 10;
        double angle = Math.atan2(endY - startY, endX - startX);

        Line arrow1 = new Line();
        arrow1.setStartX(endX);
        arrow1.setStartY(endY);
        arrow1.setEndX(endX - arrowSize * Math.cos(angle - Math.PI / 6));
        arrow1.setEndY(endY - arrowSize * Math.sin(angle - Math.PI / 6));
        arrow1.setStroke(Color.DARKSEAGREEN);
        arrow1.setStrokeWidth(2);

        Line arrow2 = new Line();
        arrow2.setStartX(endX);
        arrow2.setStartY(endY);
        arrow2.setEndX(endX - arrowSize * Math.cos(angle + Math.PI / 6));
        arrow2.setEndY(endY - arrowSize * Math.sin(angle + Math.PI / 6));
        arrow2.setStroke(Color.DARKSEAGREEN);
        arrow2.setStrokeWidth(2);

        javafx.scene.Group edgeGroup = new javafx.scene.Group(line, arrow1, arrow2);
        targetPane.getChildren().add(edgeGroup);
        edgeGroup.toBack();

        edgeGroup.setOnMouseEntered(event -> {
            line.setStroke(Color.DARKTURQUOISE);
            line.setStrokeWidth(4);
            arrow1.setStroke(Color.DARKTURQUOISE);
            arrow1.setStrokeWidth(4);
            arrow2.setStroke(Color.DARKTURQUOISE);
            arrow2.setStrokeWidth(4);

            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText(String.format("Edge: %s -> %s | Weight: %s", sourceData, targetData, weight));
            }
        });

        edgeGroup.setOnMouseExited(event -> {
            line.setStroke(Color.ALICEBLUE);
            line.setStrokeWidth(2);
            arrow1.setStroke(Color.ALICEBLUE);
            arrow1.setStrokeWidth(2);
            arrow2.setStroke(Color.ALICEBLUE);
            arrow2.setStrokeWidth(2);

            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText("");
            }
        });
    }

    @FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
            Object newVertex;

            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                // Para matriz de adyacencia, usar números
                int newVertexValue;
                boolean vertexExists;
                int attempts = 0;
                final int MAX_ATTEMPTS = 100;

                do {
                    newVertexValue = util.Utility.random(100);
                    vertexExists = currentActiveGraph.containsVertex(newVertexValue);
                    attempts++;
                } while (vertexExists && attempts < MAX_ATTEMPTS);

                if (vertexExists) {
                    FXUtility.showAlert("Error", "No se pudo generar un vértice único",
                            Alert.AlertType.ERROR);
                    return;
                }
                newVertex = newVertexValue;

            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                // Para lista de adyacencia, usar letras
                char newVertexChar;
                boolean vertexExists;
                int attempts = 0;
                final int MAX_ATTEMPTS = 100;
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                do {
                    newVertexChar = alphabet.charAt(util.Utility.random(alphabet.length()));
                    vertexExists = currentActiveGraph.containsVertex(newVertexChar);
                    attempts++;
                } while (vertexExists && attempts < MAX_ATTEMPTS);

                if (vertexExists) {
                    FXUtility.showAlert("Error", "No se pudo generar un vértice único",
                            Alert.AlertType.ERROR);
                    return;
                }
                newVertex = newVertexChar;

            } else {
                // Para lista enlazada, usar nombres de monumentos
                List<String> monuments = Arrays.asList(
                        "Eiffel", "Colosseum", "Pyramids", "MachuPicchu", "TajMahal",
                        "GreatWall", "StatueLiberty", "ChristRedeemer", "Petra", "Acropolis",
                        "Stonehenge", "ChichenItza", "BigBen", "SagradaFamilia", "Kremlin",
                        "Louvre", "ForbiddenCity", "SydneyOpera", "GoldenGate", "BurjKhalifa"
                );
                String newVertexStr;
                boolean vertexExists;
                int attempts = 0;
                final int MAX_ATTEMPTS = 100;

                do {
                    newVertexStr = monuments.get(util.Utility.random(monuments.size()));
                    vertexExists = currentActiveGraph.containsVertex(newVertexStr);
                    attempts++;
                } while (vertexExists && attempts < MAX_ATTEMPTS);

                if (vertexExists) {
                    FXUtility.showAlert("Error", "No se pudo generar un vértice único",
                            Alert.AlertType.ERROR);
                    return;
                }
                newVertex = newVertexStr;
            }

            currentActiveGraph.addVertex(newVertex);
            drawGraph(currentActiveGraph, pane3);
            TextResult.setText(currentActiveGraph.toString());

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "Error al añadir vértice: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            if (currentActiveGraph.isEmpty()) {
                FXUtility.showAlert("Grafo Vacío", "No hay vértices para eliminar",
                        Alert.AlertType.WARNING);
                return;
            }

            // Obtener todos los vértices disponibles
            List<Object> vertices = new ArrayList<>();
            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                DirectedAdjacencyMatrixGraph amGraph = (DirectedAdjacencyMatrixGraph) currentActiveGraph;
                for (int i = 0; i < amGraph.size(); i++) {
                    if (amGraph.getVertexData(i) != null) {
                        vertices.add(amGraph.getVertexData(i));
                    }
                }
            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                DirectedAdjacencyListGraph alGraph = (DirectedAdjacencyListGraph) currentActiveGraph;
                for (int i = 0; i < alGraph.size(); i++) {
                    if (alGraph.getVertexData(i) != null) {
                        vertices.add(alGraph.getVertexData(i));
                    }
                }
            } else if (currentActiveGraph instanceof DirectedSinglyLinkedListGraph) {
                DirectedSinglyLinkedListGraph sllGraph = (DirectedSinglyLinkedListGraph) currentActiveGraph;
                SinglyLinkedList vertexList = sllGraph.getVertexList();
                for (int i = 0; i < vertexList.size(); i++) {
                    Vertex v = (Vertex) vertexList.getNode(i).data;
                    vertices.add(v.data);
                }
            }

            if (vertices.isEmpty()) {
                FXUtility.showAlert("Error", "No se encontraron vértices para eliminar",
                        Alert.AlertType.WARNING);
                return;
            }

            // Seleccionar un vértice aleatorio para eliminar
            Object vertexToRemove = vertices.get(new Random().nextInt(vertices.size()));

            currentActiveGraph.removeVertex(vertexToRemove);
            drawGraph(currentActiveGraph, pane3);
            TextResult.setText(currentActiveGraph.toString());

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "Error al eliminar vértice: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleScrollZoomForPane3(ScrollEvent event) {
        double zoomFactor = 1.1;
        if (event.getDeltaY() < 0) {
            zoomFactor = 1 / zoomFactor;
        }
        pane3.setScaleX(pane3.getScaleX() * zoomFactor);
        pane3.setScaleY(pane3.getScaleY() * zoomFactor);
        event.consume(); // Opcional: evita que el evento se propague
    }

    @FXML
    public void clearOnAction(ActionEvent actionEvent) {
        TextResult.clear();
        pane3.getChildren().clear();
        edgeInfoLabel.setText("");
        for (Node node : pane3.getChildren()) {
            if (node instanceof Line) {
                Line line = (Line) node;

                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1);
            }
        }

    }

    @FXML
    public void addEdgeWeOnAction(ActionEvent actionEvent) {
        try {
            // Verificar que hay al menos 2 vértices
            if (currentActiveGraph.size() < 2) {
                FXUtility.showAlert("Error", "Se necesitan al menos 2 vértices para crear una arista",
                        Alert.AlertType.WARNING);
                return;
            }

            // Obtener todos los vértices disponibles
            List<Object> vertices = new ArrayList<>();
            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                DirectedAdjacencyMatrixGraph amGraph = (DirectedAdjacencyMatrixGraph) currentActiveGraph;
                for (int i = 0; i < amGraph.size(); i++) {
                    if (amGraph.getVertexData(i) != null) {
                        vertices.add(amGraph.getVertexData(i));
                    }
                }
            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                DirectedAdjacencyListGraph alGraph = (DirectedAdjacencyListGraph) currentActiveGraph;
                for (int i = 0; i < alGraph.size(); i++) {
                    if (alGraph.getVertexData(i) != null) {
                        vertices.add(alGraph.getVertexData(i));
                    }
                }
            } else if (currentActiveGraph instanceof DirectedSinglyLinkedListGraph) {
                DirectedSinglyLinkedListGraph sllGraph = (DirectedSinglyLinkedListGraph) currentActiveGraph;
                SinglyLinkedList vertexList = sllGraph.getVertexList();
                for (int i = 0; i < vertexList.size(); i++) {
                    Vertex v = (Vertex) vertexList.getNode(i).data;
                    vertices.add(v.data);
                }
            }

            if (vertices.size() < 2) {
                FXUtility.showAlert("Error", "No hay suficientes vértices para crear una arista",
                        Alert.AlertType.WARNING);
                return;
            }

            // Seleccionar dos vértices aleatorios distintos
            Random rand = new Random();
            Object vertex1, vertex2;
            do {
                vertex1 = vertices.get(rand.nextInt(vertices.size()));
                vertex2 = vertices.get(rand.nextInt(vertices.size()));
            } while (vertex1.equals(vertex2));

            // Generar peso aleatorio según el tipo de grafo
            int weight;
            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                weight = rand.nextInt(50) + 1; // 1-50 para matriz
            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                weight = rand.nextInt(50) + 51; // 51-100 para lista
            } else {
                weight = rand.nextInt(50) + 101; // 101-150 para lista enlazada
            }

            // Añadir la arista con peso
            currentActiveGraph.addEdgeWeight(vertex1, vertex2, weight);

            // Actualizar la visualización
            drawGraph(currentActiveGraph, pane3);
            TextResult.setText(currentActiveGraph.toString());

        } catch (Exception e) {
            FXUtility.showAlert("Error", "Error al añadir arista: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void removeEdgeWeOnAction(ActionEvent actionEvent) {
        try {
            // Obtener todas las aristas existentes
            List<Object[]> existingEdges = new ArrayList<>();

            if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
                DirectedAdjacencyMatrixGraph amGraph = (DirectedAdjacencyMatrixGraph) currentActiveGraph;
                for (int i = 0; i < amGraph.size(); i++) {
                    Object v1 = amGraph.getVertexData(i);
                    if (v1 == null) continue;

                    for (int j = 0; j < amGraph.size(); j++) {
                        Object v2 = amGraph.getVertexData(j);
                        if (v2 != null && amGraph.containsEdge(v1, v2)) {
                            existingEdges.add(new Object[]{v1, v2});
                        }
                    }
                }
            } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
                DirectedAdjacencyListGraph alGraph = (DirectedAdjacencyListGraph) currentActiveGraph;
                for (int i = 0; i < alGraph.size(); i++) {
                    Vertex v = alGraph.getVertexObject(i);
                    if (v == null) continue;

                    Object v1 = v.data;
                    SinglyLinkedList edges = v.edgesList;
                    for (int j = 0; j < edges.size(); j++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(j);
                        Object v2 = ew.getEdge();
                        existingEdges.add(new Object[]{v1, v2});
                    }
                }
            } else if (currentActiveGraph instanceof DirectedSinglyLinkedListGraph) {
                DirectedSinglyLinkedListGraph sllGraph = (DirectedSinglyLinkedListGraph) currentActiveGraph;
                SinglyLinkedList vertexList = sllGraph.getVertexList();
                for (int i = 0; i < vertexList.size(); i++) {
                    Vertex v = (Vertex) vertexList.getNode(i).data;
                    Object v1 = v.data;
                    SinglyLinkedList edges = v.edgesList;
                    for (int j = 0; j < edges.size(); j++) {
                        EdgeWeight ew = (EdgeWeight) edges.get(j);
                        Object v2 = ew.getEdge();
                        existingEdges.add(new Object[]{v1, v2});
                    }
                }
            }

            // Verificar si hay aristas
            if (existingEdges.isEmpty()) {
                FXUtility.showAlert("Información", "El grafo no contiene aristas para eliminar",
                        Alert.AlertType.INFORMATION);
                return;
            }

            // Seleccionar una arista aleatoria para eliminar
            Object[] edgeToRemove = existingEdges.get(new Random().nextInt(existingEdges.size()));
            Object vertex1 = edgeToRemove[0];
            Object vertex2 = edgeToRemove[1];

            // Eliminar la arista
            currentActiveGraph.removeEdge(vertex1, vertex2);

            // Actualizar la visualización
            drawGraph(currentActiveGraph, pane3);
            TextResult.setText(currentActiveGraph.toString());

        } catch (Exception e) {
            FXUtility.showAlert("Error", "Error al eliminar arista: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void AdjacencyListOnAction(ActionEvent actionEvent) {
        currentActiveGraph = adjacencyListGraph;
        randomizeOnAction(null);
        TextResult.setText(currentActiveGraph.toString());
    }

    @FXML
    public void adjacencyMatrizOnAction(ActionEvent actionEvent) {
        currentActiveGraph = adjacencyMatrixGraph;
        randomizeOnAction(null);
        TextResult.setText(currentActiveGraph.toString());
    }

    @FXML
    public void LinkedListOnAction(ActionEvent actionEvent) {
        currentActiveGraph = singlyLinkedListGraph;
        randomizeOnAction(null);
        TextResult.setText(currentActiveGraph.toString());
    }
    
}
