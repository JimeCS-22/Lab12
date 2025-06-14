package Controller;

import domain.list.ListException;
import domain.list.SinglyLinkedList;
import graph.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.TextInputDialog;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DirectedGraphController {
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

    @FXML private RadioButton DFS;
    @FXML private RadioButton BFS;
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

        if (DFS != null && BFS != null) {
            tourTypeGroup = new ToggleGroup();
            DFS.setToggleGroup(tourTypeGroup);
            BFS.setToggleGroup(tourTypeGroup);
            DFS.setSelected(true);
        }

        AdjacencyList.setOnAction(this::AdjacencyListOnAction);
        AdjacencyMatrix.setOnAction(this::adjacencyMatrizOnAction);
        LinkedList.setOnAction(this::LinkedListOnAction);

        initializeVertexDataPools();
        randomizeOnAction(null);

        if (edgeInfoLabel != null) {
            edgeInfoLabel.setText("");
        }
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
        TextResult.setText("");
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
            if (radius == 0) radius = 100;

            for (int i = 0; i < numNodes; i++) {
                Object vertexData = verticesData.get(i);
                double angle = 2 * Math.PI * i / numNodes;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);

                NodePosition<?> nodePos = new NodePosition<>(vertexData, x, y);
                positionsMap.put(vertexData, nodePos);

                Circle circle = new Circle(x, y, 20, Color.LIGHTBLUE);
                circle.setStroke(Color.DARKBLUE);
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
        line.setStroke(Color.GRAY);

        double arrowSize = 10;
        double angle = Math.atan2(endY - startY, endX - startX);

        Line arrow1 = new Line();
        arrow1.setStartX(endX);
        arrow1.setStartY(endY);
        arrow1.setEndX(endX - arrowSize * Math.cos(angle - Math.PI / 6));
        arrow1.setEndY(endY - arrowSize * Math.sin(angle - Math.PI / 6));
        arrow1.setStroke(Color.GRAY);
        arrow1.setStrokeWidth(2);

        Line arrow2 = new Line();
        arrow2.setStartX(endX);
        arrow2.setStartY(endY);
        arrow2.setEndX(endX - arrowSize * Math.cos(angle + Math.PI / 6));
        arrow2.setEndY(endY - arrowSize * Math.sin(angle + Math.PI / 6));
        arrow2.setStroke(Color.GRAY);
        arrow2.setStrokeWidth(2);

        javafx.scene.Group edgeGroup = new javafx.scene.Group(line, arrow1, arrow2);
        targetPane.getChildren().add(edgeGroup);
        edgeGroup.toBack();

        edgeGroup.setOnMouseEntered(event -> {
            line.setStroke(Color.ORANGE);
            line.setStrokeWidth(4);
            arrow1.setStroke(Color.ORANGE);
            arrow1.setStrokeWidth(4);
            arrow2.setStroke(Color.ORANGE);
            arrow2.setStrokeWidth(4);

            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText(String.format("Edge: %s -> %s | Weight: %s", sourceData, targetData, weight));
            }
        });

        edgeGroup.setOnMouseExited(event -> {
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(2);
            arrow1.setStroke(Color.GRAY);
            arrow1.setStrokeWidth(2);
            arrow2.setStroke(Color.GRAY);
            arrow2.setStrokeWidth(2);

            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText("");
            }
        });
    }

    @FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Vértice");
        dialog.setHeaderText("Verificar existencia de un vértice");
        dialog.setContentText("Introduce el valor del vértice:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(vertexStr -> {
            try {
                Object vertex = parseVertexInput(vertexStr);
                if (currentActiveGraph.containsVertex(vertex)) {
                    showAlert("Vértice Encontrado", "El vértice '" + vertexStr + "' existe en el grafo.");
                } else {
                    showAlert("Vértice No Encontrado", "El vértice '" + vertexStr + "' NO existe en el grafo.");
                }
            } catch (Exception e) {
                showAlert("Error de Entrada", "Valor de vértice inválido para el tipo de grafo actual: " + e.getMessage());
            }
        });
    }

    @FXML
    public void ContainsEdgeOnAction(ActionEvent actionEvent) {
        TextInputDialog dialogSource = new TextInputDialog();
        dialogSource.setTitle("Buscar Arista");
        dialogSource.setHeaderText("Verificar existencia de una arista dirigida");
        dialogSource.setContentText("Introduce el vértice de ORIGEN:");
        Optional<String> resultSource = dialogSource.showAndWait();

        resultSource.ifPresent(sourceStr -> {
            TextInputDialog dialogDest = new TextInputDialog();
            dialogDest.setTitle("Buscar Arista");
            dialogDest.setHeaderText("Verificar existencia de una arista dirigida");
            dialogDest.setContentText("Introduce el vértice de DESTINO:");
            Optional<String> resultDest = dialogDest.showAndWait();

            resultDest.ifPresent(destStr -> {
                try {
                    Object source = parseVertexInput(sourceStr);
                    Object dest = parseVertexInput(destStr);

                    if (currentActiveGraph.containsEdge(source, dest)) {
                        showAlert("Arista Encontrada", "La arista dirigida '" + sourceStr + " -> " + destStr + "' existe en el grafo.");
                    } else {
                        showAlert("Arista No Encontrada", "La arista dirigida '" + sourceStr + " -> " + destStr + "' NO existe en el grafo.");
                    }
                } catch (Exception e) {
                    showAlert("Error de Entrada", "Valor de vértice inválido para el tipo de grafo actual: " + e.getMessage());
                }
            });
        });
    }

    private Object parseVertexInput(String input) {
        if (currentActiveGraph instanceof DirectedAdjacencyMatrixGraph) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Se espera un número entero (0-99) para Matriz de Adyacencia.");
            }
        } else if (currentActiveGraph instanceof DirectedAdjacencyListGraph) {
            if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                return Character.toUpperCase(input.charAt(0));
            }
            throw new IllegalArgumentException("Se espera una sola letra mayúscula para Lista de Adyacencia.");
        } else if (currentActiveGraph instanceof DirectedSinglyLinkedListGraph) {
            return input;
        }
        return input;
    }

    @FXML
    public void ToStrinOnAction(ActionEvent actionEvent) {
        if (currentActiveGraph != null) {
            TextResult.setText(currentActiveGraph.toString());
        } else {
            TextResult.setText("No hay grafo seleccionado.");
        }
    }

    @FXML
    public void DFSOnAction(ActionEvent actionEvent) {
        if (currentActiveGraph.isEmpty()) {
            showAlert("Grafo Vacío", "No se puede realizar un tour en un grafo vacío.");
            return;
        }
        try {
            TextResult.setText("Tour DFS: " + currentActiveGraph.dfs());
        } catch (Exception e) {
            showAlert("Error en el Tour DFS", "No se pudo realizar el tour DFS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void BFSOnAction(ActionEvent actionEvent) {
        if (currentActiveGraph.isEmpty()) {
            showAlert("Grafo Vacío", "No se puede realizar un tour en un grafo vacío.");
            return;
        }
        try {
            TextResult.setText("Tour BFS: " + currentActiveGraph.bfs());
        } catch (Exception e) {
            showAlert("Error en el Tour BFS", "No se pudo realizar el tour BFS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void AdjacencyListOnAction(ActionEvent actionEvent) {
        currentActiveGraph = adjacencyListGraph;
        randomizeOnAction(null);
        showAlert("Tipo de Grafo", "Se ha seleccionado Lista de Adyacencia.");
    }

    @FXML
    public void adjacencyMatrizOnAction(ActionEvent actionEvent) {
        currentActiveGraph = adjacencyMatrixGraph;
        randomizeOnAction(null);
        showAlert("Tipo de Grafo", "Se ha seleccionado Matriz de Adyacencia.");
    }

    @FXML
    public void LinkedListOnAction(ActionEvent actionEvent) {
        currentActiveGraph = singlyLinkedListGraph;
        randomizeOnAction(null);
        showAlert("Tipo de Grafo", "Se ha seleccionado Lista Enlazada.");
    }

    @FXML
    public void handleScrollZoom(Event event) {

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class NodePosition<T> {
        T data;
        double x;
        double y;

        public NodePosition(T data, double x, double y) {
            this.data = data;
            this.x = x;
            this.y = y;
        }
    }
}