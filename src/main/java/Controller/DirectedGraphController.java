package Controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

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

    @FXML
    public void ContainsEdgeOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void ToStrinOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void DFSOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void BFSOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void handleScrollZoom(Event event) {
    }

    @FXML
    public void containsVertexOnAction(ActionEvent actionEvent) {
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
//    @FXML
//    private Pane mainPain;
//    @FXML
//    private Pane pane2;
//    @FXML
//    private AnchorPane AP;
//    @FXML
//    private Pane pane3;
//    private AdjacencyMatrixGraph graph;
//    private Map<Object, Circle> nodeCircles = new HashMap<>();
//    @FXML
//    private TextArea TextResult;
//
//    @FXML
//    public void initialize() {
//
//            int maxVertices = 10;
//            graph = new AdjacencyMatrixGraph(maxVertices); // Instanciar AdjacencyMatrixGraph
//
//            // Generar valores aleatorios para vértices y aristas con pesos
//            generateRandomGraph();
//
//            // 4. Dibujar el grafo
//            drawGraph();
//
//
//    }
//
//    //Metodo para crear los numeros aleatorios
//    private void generateRandomGraph(){
//        try {
//            graph.clear(); // Limpiamos el grafo actual antes de generar uno nuevo
//
//            Random rand = new Random();
//            int numVerticesToGenerate = 10;
//            int verticesAddedCount = 0; // Para controlar cuántos vértices únicos hemos añadido
//
//            // Bucle para añadir vértices únicos
//            while (verticesAddedCount < numVerticesToGenerate && graph.size() < graph.vertexList.length) { // Añadimos hasta 'numVerticesToGenerate' o hasta que el grafo esté lleno
//                int randomVertexValue = util.Utility.random(99);
//                try {
//                    graph.addVertex(randomVertexValue); // Intentamos agregar el vértice
//                    verticesAddedCount++; // Solo incrementamos si se agregó exitosamente
//                } catch (GraphException ge) {
//                    // Si la excepción es porque el vértice ya existe, simplemente lo ignoramos
//                    // y el bucle while intentará con otro número aleatorio.
//                    // Si la excepción es "Graph is Full", el bucle while se detendrá.
//                }
//            }
//
//            // Si no se pudieron añadir todos los vértices deseados
//            if (verticesAddedCount < numVerticesToGenerate) {
//                System.err.println("Advertencia: No se pudieron añadir " + numVerticesToGenerate + " vértices únicos. Se añadieron " + verticesAddedCount + " vértices.");
//            }
//
//
//            int numEdgesToAdd = 15;
//            for (int k = 0; k < numEdgesToAdd; k++) {
//                if (graph.size() < 2) break; // Necesitamos al menos 2 vértices para una arista
//
//                // Seleccionamos dos vértices aleatorios de los que YA ESTÁN en el grafo
//                Object v1 = graph.getVertexData(rand.nextInt(graph.size()));
//                Object v2 = graph.getVertexData(rand.nextInt(graph.size()));
//
//                // Aseguramos que no sea el mismo vértice y que la arista no exista ya
//                if (!v1.equals(v2) && !graph.containsEdge(v1, v2)) {
//                    int weight = util.Utility.random(50) + 1; // Pesos entre 1 y 50
//                    graph.addEdgeWeight(v1, v2, weight); // Añadimos la arista con el peso
//                }
//            }
//
//        } catch (GraphException | ListException e) {
//            showAlert("Error de generación", "Ocurrió un error al generar el grafo aleatorio: " + e.getMessage());
//        }
//    }
//
//
//
//    private void drawGraph() {
//
//        if (graph == null || graph.isEmpty()) {
//            showAlert("Grafo Vacío", "No hay vértices para dibujar.");
//            return;
//        }
//        Pane drawingPane = pane3;
//
//        double initialCenterX = drawingPane.getWidth() / 2;
//        double initialCenterY = drawingPane.getHeight() / 2;
//        double initialRadius = Math.min(initialCenterX, initialCenterY) * 0.8;
//
//        drawingPane.getChildren().clear();
//        nodeCircles.clear();
//
//        // Escuchadores de cambio de tamaño para redibujar el grafo
//        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> {
//            try {
//                double currentCenterX = newVal.doubleValue() / 2;
//                double currentCenterY = drawingPane.getHeight() / 2;
//                double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
//                redrawGraphContent(currentCenterX, currentCenterY, currentRadius, drawingPane);
//            } catch (ListException | GraphException e) {
//                showAlert("Error al redibujar el grafo (width)", e.getMessage());
//            }
//        });
//
//        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> {
//            try {
//                double currentCenterX = drawingPane.getWidth() / 2;
//                double currentCenterY = newVal.doubleValue() / 2;
//                double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
//                redrawGraphContent(currentCenterX, currentCenterY, currentRadius, drawingPane);
//            } catch (ListException | GraphException e) {
//                showAlert("Error al redibujar el grafo (height)", e.getMessage());
//            }
//        });
//
//        try {
//            redrawGraphContent(initialCenterX, initialCenterY, initialRadius, drawingPane);
//        } catch (ListException | GraphException e) {
//            showAlert("Error al dibujar el grafo inicial", e.getMessage());
//        }
//
//
//    }
//
//    private void redrawGraphContent(double centerX, double centerY, double radius, Pane targetPane) throws ListException, GraphException {
//
//        targetPane.getChildren().clear();
//        nodeCircles.clear();
//
//        List<NodePosition> positions = new ArrayList<>();
//        int numNodes = graph.size();
//
//        // Dibuja los vértices y sus etiquetas
//        for (int i = 0; i < numNodes; i++) {
//            Object vertexData = graph.getVertexData(i); // Obtener el dato del vértice
//            double angle = 2 * Math.PI * i / numNodes;
//            double x = centerX + radius * Math.cos(angle);
//            double y = centerY + radius * Math.sin(angle);
//            positions.add(new NodePosition(vertexData, x, y));
//
//            Circle circle = new Circle(x, y, 20, Color.DEEPSKYBLUE);
//            circle.setStroke(Color.BLACK);
//            targetPane.getChildren().add(circle);
//            nodeCircles.put(vertexData, circle);
//
//            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
//            text.setFill(Color.RED);
//            text.setFont(new Font(12));
//            targetPane.getChildren().add(text);
//        }
//
//        // Dibuja las aristas y sus pesos
//        for (int i = 0; i < numNodes; i++) {
//            NodePosition node1Pos = positions.get(i);
//            Object node1Data = node1Pos.value;
//
//            for (int j = 0; j < numNodes; j++) {
//                // if (i == j) continue; // Si es un grafo no dirigido y no se permiten bucles
//
//                NodePosition node2Pos = positions.get(j);
//                Object node2Data = node2Pos.value;
//
//                // Solo dibuja una vez por par y verifica si existe la arista
//                // y si es un grafo no dirigido, dibuja solo para i < j
//                if (i < j && graph.containsEdge(node1Data, node2Data)) {
//                    Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
//                    line.setStroke(Color.BLACK); // Color inicial de la línea
//                    line.setStrokeWidth(2); // Grosor inicial de la línea
//
//                    // *** Añadir los manejadores de eventos ***
//                    line.setOnMouseEntered(event -> {
//                        line.setStroke(Color.RED); // Se pone roja al pasar el mouse
//                        line.setStrokeWidth(3); // Un poco más gruesa para destacar
//                    });
//
//                    line.setOnMouseExited(event -> {
//                        line.setStroke(Color.LIMEGREEN); // Se pone verde al salir el mouse
//                        line.setStrokeWidth(2); // Vuelve a su grosor original
//                    });
//
//                    targetPane.getChildren().add(line);
//                    line.toBack();
//
//                    // Dibuja el peso de la arista
//                    try {
//                        Object weight = graph.getEdgeWeight(node1Data, node2Data);
//                        if (weight != null && !weight.equals(0) && !weight.equals(1)) { // Mostrar peso si no es 0 o 1
//                            double midX = (node1Pos.x + node2Pos.x) / 2;
//                            double midY = (node1Pos.y + node2Pos.y) / 2;
//
//                            Text weightText = new Text(midX + 5, midY - 5, String.valueOf(weight));
//                            weightText.setFill(Color.BLACK);
//                            weightText.setFont(new Font(10));
//                            targetPane.getChildren().add(weightText);
//                        }
//                    } catch (GraphException e) {
//                        // Manejar la excepción si no se encuentra el peso (aunque containsEdge ya lo verifica)
//                        System.err.println("Error al obtener el peso de la arista: " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }
//
//
//
//    @FXML
//    public void randomizeOnAction(ActionEvent actionEvent) {
//
//        try {
//            generateRandomGraph(); // Vuelve a generar un grafo aleatorio
//            drawGraph(); // Redibuja el grafo
//            TextResult.setText(""); // Limpia el área de texto de resultados
//        } catch (Exception e) {
//            showAlert("Error", "No se pudo generar un nuevo grafo aleatorio: " + e.getMessage());
//        }
//
//    }
//
//
//    @FXML
//    public void handleScrollZoom(Event event) {
//    }
//
//    @FXML
//    public void ContainsEdgeOnAction(ActionEvent actionEvent) {
//
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Verificar Arista");
//        dialog.setHeaderText("Verificar si existe una arista entre dos vértices");
//        dialog.setContentText("Ingrese el primer vértice:");
//
//        String v1Str = dialog.showAndWait().orElse(null);
//        if (v1Str == null || v1Str.isEmpty()) return;
//
//        dialog.setContentText("Ingrese el segundo vértice:");
//        String v2Str = dialog.showAndWait().orElse(null);
//        if (v2Str == null || v2Str.isEmpty()) return;
//
//        try {
//            Object v1 = Integer.parseInt(v1Str);
//            Object v2 = Integer.parseInt(v2Str);
//
//            boolean contains = graph.containsEdge(v1, v2);
//            String result = "La arista entre " + v1 + " y " + v2 + " ";
//            if (contains) {
//                Object weight = graph.getEdgeWeight(v1, v2); // Ahora podemos obtener el peso
//                result += "EXISTE. Peso: " + weight;
//            } else {
//                result += "NO EXISTE.";
//            }
//            TextResult.setText(result); // Mostrar resultado en el TextArea
//
//        } catch (NumberFormatException e) {
//            showAlert("Error de Entrada", "Los vértices deben ser números enteros.");
//        } catch (GraphException | ListException e) {
//            showAlert("Error del Grafo", e.getMessage());
//        }
//
//    }
//
//    @FXML
//    public void ToStrinOnAction(ActionEvent actionEvent) {
//
//            TextResult.setText(graph.toString()); // Llamada al método público toString() del grafo
//    }
//
//    @FXML
//    public void DFSOnAction(ActionEvent actionEvent) {
//
//        try {
//            if (graph.isEmpty()) {
//                TextResult.setText("El grafo está vacío, no se puede realizar el DFS.");
//                return;
//            }
//            TextResult.setText("DFS Tour: " + graph.dfs()); // Llamada al método público dfs() del grafo
//        } catch (GraphException | ListException | domain.stack.StackException e) {
//            showAlert("Error en DFS", e.getMessage());
//        }
//
//    }
//
//    @FXML
//    public void BFSOnAction(ActionEvent actionEvent) {
//
//        try {
//            if (graph.isEmpty()) {
//                TextResult.setText("El grafo está vacío, no se puede realizar el BFS.");
//                return;
//            }
//            TextResult.setText("BFS Tour: " + graph.bfs()); // Llamada al método público bfs() del grafo
//        } catch (GraphException | ListException | domain.queue.QueueException e) {
//            showAlert("Error en BFS", e.getMessage());
//        }
//    }
//
//    @FXML
//    public void containsVertexOnAction(ActionEvent actionEvent) {
//
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Verificar Vértice");
//        dialog.setHeaderText("Verificar si un vértice existe en el grafo");
//        dialog.setContentText("Ingrese el valor del vértice:");
//
//        String vertexStr = dialog.showAndWait().orElse(null);
//        if (vertexStr == null || vertexStr.isEmpty()) return;
//
//        try {
//            Object vertex = Integer.parseInt(vertexStr); // Asumiendo que los vértices son Integer
//
//            boolean contains = graph.containsVertex(vertex); // Llamada al método público del grafo
//            if (contains) {
//                TextResult.setText("El vértice " + vertex + " EXISTE en el grafo."); // Mostrar resultado
//            } else {
//                TextResult.setText("El vértice " + vertex + " NO EXISTE en el grafo."); // Mostrar resultado
//            }
//        } catch (NumberFormatException e) {
//            showAlert("Error de Entrada", "El vértice debe ser un número entero.");
//        } catch (GraphException | ListException e) {
//            showAlert("Error del Grafo", e.getMessage());
//        }
//
//    }
//
//    private void showAlert(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}
