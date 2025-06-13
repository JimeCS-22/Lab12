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
    private RadioButton AdjacencyList;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Pane pane2;
    @javafx.fxml.FXML
    private Pane pane3;
    @javafx.fxml.FXML
    private RadioButton AdjacencyMatrix;
    @javafx.fxml.FXML
    private RadioButton LinkedList;
    @javafx.fxml.FXML
    private AnchorPane AP;
    @javafx.fxml.FXML
    private Pane pane31;

    private AdjacencyListGraph graph; // El grafo principal
    private AdjacencyListGraph mstGraph;

    private final int MAX_VERTICES = 10;
    private final int NUM_EDGES_TO_ADD = 15;
    @FXML
    private RadioButton Prim;
    @FXML
    private RadioButton Kruskal;

    @FXML
    public void initialize() {
        graph = new AdjacencyListGraph(MAX_VERTICES);
        mstGraph = new AdjacencyListGraph(MAX_VERTICES); // Inicializa el grafo MST

        AdjacencyList.setSelected(true); // Selecciona la lista de adyacencia por defecto

        if (Prim != null) {
            Prim.setSelected(true); // Selecciona Prim por defecto para el MST
        }
        if (Kruskal != null && !Prim.isSelected()) { // Si Prim no está seleccionado, selecciona Kruskal
            Kruskal.setSelected(true);
        }

        randomizeOnAction(null); // Genera y dibuja al iniciar la aplicación
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        generateRandomGraph(); // Genera el grafo original aleatorio
        drawGraph(graph, pane3); // Dibuja el grafo original en pane3

        generateAndDrawMST(); // Calcula y dibuja el MST en pane31
    }

    private void generateRandomGraph() {
        try {
            Random rand = new Random();
            graph.clear(); // Limpia el grafo antes de generar uno nuevo

            // Usa SinglyLinkedList para manejar los caracteres disponibles
            SinglyLinkedList availableCharacters = new SinglyLinkedList();
            for (char c = 'A'; c <= 'Z'; c++) {
                availableCharacters.add(c);
            }

            // Añadir vértices aleatorios
            for (int i = 0; i < MAX_VERTICES; i++) {
                if (availableCharacters.isEmpty()) break;
                int randomIndex = rand.nextInt(availableCharacters.size());
                Character charToRemove = (Character) availableCharacters.get(randomIndex);
                graph.addVertex(charToRemove);
                availableCharacters.remove(charToRemove);
            }

            // Obtener los vértices actualmente en el grafo para crear aristas
            SinglyLinkedList currentVertices = new SinglyLinkedList();
            for (int i = 0; i < graph.size(); i++) {
                currentVertices.add(graph.vertexList[i].data);
            }

            int edgesAdded = 0;
            long startTime = System.nanoTime();
            final long TIMEOUT = 5_000_000_000L; // 5 segundos para evitar bucles infinitos

            // Añadir aristas aleatorias
            while (edgesAdded < NUM_EDGES_TO_ADD && (System.nanoTime() - startTime < TIMEOUT)) {
                if (currentVertices.size() < 2) break;

                Object v1 = currentVertices.get(rand.nextInt(currentVertices.size()));
                Object v2 = currentVertices.get(rand.nextInt(currentVertices.size()));

                // Asegurarse de que no sea el mismo vértice y que la arista no exista ya
                if (util.Utility.compare(v1, v2) != 0 && !graph.containsEdge(v1, v2)) {
                    int weight = rand.nextInt(50) + 1; // Peso entre 1 y 50
                    Edge newEdge = new Edge(v1, v2);
                    graph.addEdgeWeightTwo(v1, v2, newEdge, weight); // Añade la arista con peso
                    edgesAdded++;
                }
            }

        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo aleatorio", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado", "Un error inesperado ocurrió al generar el grafo: " + e.getMessage());
        }
    }

    private void generateAndDrawMST() {
        try {
            mstGraph.clear(); // Limpia el grafo MST anterior para el nuevo cálculo

            // Verifica si el grafo original existe y no está vacío
            if (graph == null || graph.isEmpty()) {
                showAlert("Grafo vacío", "El grafo original está vacío, no se puede generar un MST.");
                pane31.getChildren().clear(); // Asegúrate de que el panel del MST también se limpie
                return;
            }

            // Lógica para seleccionar y ejecutar el algoritmo de MST
            if (Prim.isSelected()) {
                // Para Prim, necesitamos un vértice inicial. Tomamos el primero del grafo.
                // Asegúrate de que el grafo tenga al menos un vértice antes de intentar esto
                if (graph.size() > 0 && graph.vertexList[0] != null) {
                    Object startVertex = graph.vertexList[0].data; // Tomar el primer vértice como inicio para Prim
                    mstGraph = graph.primMST(startVertex); // Llama a tu método primMST()
                } else {
                    showAlert("Grafo Invalido", "El grafo no tiene suficientes vértices para calcular un MST con Prim.");
                    pane31.getChildren().clear();
                    return;
                }
            } else if (Kruskal.isSelected()) {
                // --- ESPACIO PARA EL ALGORITMO DE KRUSKAL ---
                // Aquí iría la llamada a tu algoritmo de Kruskal si lo implementas.
                // Por ejemplo: mstGraph = graph.kruskalMST();
                // Si aún no lo has implementado, puedes mostrar una alerta:
                showAlert("Algoritmo Kruskal", "El algoritmo de Kruskal no está implementado en este ejemplo.");
                pane31.getChildren().clear(); // Limpia el panel si no hay implementación
                return; // Salir del método si Kruskal no está implementado
            } else {
                // Si ningún algoritmo de MST está seleccionado (esto no debería pasar con ToggleGroup)
                showAlert("Selección de algoritmo", "Por favor, selecciona Kruskal o Prim para el MST.");
                pane31.getChildren().clear();
                return;
            }

            // Dibuja el MST calculado en pane31
            drawGraph(mstGraph, pane31);

        } catch (GraphException | ListException e) {
            showAlert("Error al generar o dibujar el MST", e.getMessage());
        } catch (Exception e) {
            showAlert("Error Inesperado al generar el MST", "Un error inesperado ocurrió: " + e.getMessage());
        }
    }

    private void drawGraph(AdjacencyListGraph currentGraph, Pane targetPane) {
        try {
            // Importante: limpiar el panel antes de dibujar el nuevo grafo
            targetPane.getChildren().clear();

            // Llama al método principal de dibujo que maneja el posicionamiento y los elementos gráficos
            drawGraphContent(currentGraph, targetPane);
        } catch (Exception e) {
            showAlert("Error al dibujar el grafo", e.getMessage());
        }
    }

    private void drawGraphContent(AdjacencyListGraph currentGraph, Pane drawingPane) {
        if (currentGraph == null || currentGraph.isEmpty()) {
            drawingPane.getChildren().clear();
            return;
        }

        // Este bloque es para añadir listeners de redimensionamiento una sola vez por panel.
        // Asegura que el grafo se redibuje si el tamaño del panel cambia.
        if (drawingPane.getProperties().get("listenersAdded") == null) {
            Runnable redrawAction = () -> {
                try {
                    // Calcula el centro y el radio para dibujar el grafo en forma circular
                    double currentCenterX = drawingPane.getWidth() / 2;
                    double currentCenterY = drawingPane.getHeight() / 2;
                    double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;

                    // Llama a redrawGraphContent de nuevo para redibujar
                    // (el clear() se maneja dentro de redrawGraphContent para este caso)
                    redrawGraphContent(currentGraph, currentCenterX, currentCenterY, currentRadius, drawingPane);
                } catch (Exception e) {
                    showAlert("Error al redibujar el grafo al redimensionar", e.getMessage());
                }
            };
            // Añade listeners a las propiedades de ancho y alto del panel
            drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
            drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawAction.run());
            // Marca que los listeners ya fueron añadidos para este panel
            drawingPane.getProperties().put("listenersAdded", true);
        }

        // Obtener las dimensiones iniciales o actuales del panel para el primer dibujo
        double initialCenterX = drawingPane.getWidth() / 2;
        double initialCenterY = drawingPane.getHeight() / 2;
        double initialRadius = Math.min(initialCenterX, initialCenterY) * 0.8;

        try {
            // Llama a la lógica de dibujo que realmente crea los nodos y líneas
            redrawGraphContent(currentGraph, initialCenterX, initialCenterY, initialRadius, drawingPane);
        } catch (GraphException | ListException e) {
            showAlert("Error al dibujar el grafo inicial", e.getMessage());
        }
    }

    private void redrawGraphContent(AdjacencyListGraph currentGraph, double centerX, double centerY, double radius, Pane targetPane) throws GraphException, ListException {
        // Limpia el panel antes de dibujar. Esto es esencial para el redibujado.
        targetPane.getChildren().clear();

        SinglyLinkedList positions = new SinglyLinkedList(); // Almacena las posiciones (x,y) de los nodos
        SinglyLinkedList verticesData = new SinglyLinkedList(); // Almacena solo la data de los vértices

        // Recolectar datos de los vértices que existen en el grafo actual
        for (int i = 0; i < currentGraph.size(); i++) {
            if (currentGraph.vertexList[i] != null) { // Asegurarse de que el slot no sea nulo
                if (currentGraph.vertexList[i].data instanceof Character) {
                    verticesData.add(currentGraph.vertexList[i].data);
                } else {
                    // Manejar casos donde el dato no es un Character, si es posible
                    System.err.println("Advertencia: Dato de vértice no es Character: " + currentGraph.vertexList[i].data);
                    // Intenta convertir a Character o manejar de otra forma
                    // Esto es una solución de respaldo, considera qué tipo de datos usarás.
                    verticesData.add(String.valueOf(currentGraph.vertexList[i].data).charAt(0));
                }
            }
        }
        int numNodes = verticesData.size();

        for (int i = 0; i < numNodes; i++) {
            Character vertexData = (Character) verticesData.get(i);

            // Calcular posición del nodo en un círculo (disposición circular)
            double angle = 2 * Math.PI * i / numNodes;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.add(new NodePosition<>(vertexData, x, y)); // Guardar posición del nodo

            // Dibujar círculo que representa el nodo
            Circle circle = new Circle(x, y, 20, Color.DEEPSKYBLUE);
            circle.setStroke(Color.BLACK);
            targetPane.getChildren().add(circle);

            // Dibujar texto del vértice (la letra del vértice)
            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
            text.setFill(Color.RED);
            text.setFont(new Font(12));
            targetPane.getChildren().add(text);
        }

        // 2. Dibujar aristas y sus pesos
        for (int i = 0; i < numNodes; i++) {
            NodePosition<Character> node1Pos = (NodePosition<Character>) positions.get(i);
            Object node1Data = node1Pos.value;

            try {
                int node1Index = currentGraph.indexOf(node1Data);
                if (node1Index != -1 && currentGraph.vertexList[node1Index].edgesList != null) {
                    SinglyLinkedList edgesOfNode1 = currentGraph.vertexList[node1Index].edgesList;

                    for (int k = 0; k < edgesOfNode1.size(); k++) {
                        EdgeWeight ew = (EdgeWeight) edgesOfNode1.getNode(k).getData();
                        Edge currentEdge = (Edge) ew.getEdge();
                        Object targetNodeData = null;

                        if (util.Utility.compare(currentEdge.getElementA(), node1Data) == 0) {
                            targetNodeData = currentEdge.getElementB();
                        } else if (util.Utility.compare(currentEdge.getElementB(), node1Data) == 0) {
                            targetNodeData = currentEdge.getElementA();
                        }

                        if (targetNodeData != null) {
                            NodePosition<Character> node2Pos = null;
                            for (int p = 0; p < positions.size(); p++) {
                                NodePosition<Character> pos = (NodePosition<Character>) positions.get(p);
                                if (util.Utility.compare(pos.value, targetNodeData) == 0) {
                                    node2Pos = pos;
                                    break;
                                }
                            }

                            if (node2Pos != null) {
                                if (util.Utility.compare(node1Data, targetNodeData) < 0) {
                                    Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
                                    line.setStrokeWidth(2);
                                    line.setStroke(Color.BLACK);

                                    // Interacciones básicas con el mouse para las líneas (opcional)
                                    line.setOnMouseEntered(event -> {
                                        line.setStroke(Color.RED);
                                        line.setStrokeWidth(3);
                                    });
                                    line.setOnMouseExited(event -> {
                                        line.setStroke(Color.BLACK);
                                        line.setStrokeWidth(2);
                                    });

                                    targetPane.getChildren().add(line);
                                    line.toBack(); // Mueve la línea al fondo para que los nodos estén encima

                                    // Dibujar el peso de la arista (el valor numérico)
                                    int weight = (Integer) ew.getWeight();
                                    Text weightText = new Text((node1Pos.x + node2Pos.x) / 2 + 5, (node1Pos.y + node2Pos.y) / 2 - 5, String.valueOf(weight));
                                    weightText.setFill(Color.BLUE);
                                    weightText.setFont(new Font(10));
                                    targetPane.getChildren().add(weightText);
                                }
                            }
                        }
                    }
                }
            } catch (ListException e) {
                showAlert("Error al iterar aristas para dibujar", e.getMessage());
            }
        }
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

    @FXML
    public void KrusalOnAction(ActionEvent actionEvent) {

        if (Kruskal.isSelected()) {
            Prim.setSelected(false);
            generateAndDrawMST();
        }

    }

    @FXML
    public void PrimOnAction(ActionEvent actionEvent) {

        if (Prim.isSelected()) {
            Kruskal.setSelected(false);
            generateAndDrawMST();
        }
    }
    }

