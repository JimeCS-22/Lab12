package Controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab12.HelloApplication;

import java.io.IOException;

public class HelloController {

    @FXML
    private Text txtMessage;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;


    @Deprecated
    private void load(String form) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
        this.bp.setCenter(fxmlLoader.load());
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
        this.txtMessage.setText("Laboratory No. 12");
    }


    @FXML
    public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void exampleOnMousePressed(Event event)  {
        this.txtMessage.setText("Loading Example. Please wait!!!");
    }

    @FXML
    public void KruskalPrimOnAction(ActionEvent actionEvent) throws IOException {

        load("/ucr/lab12/MSTGraph.fxml");

    }

    @FXML
    public void OperationsOnAction(ActionEvent actionEvent) throws IOException {

        load("/ucr/lab12/Operations.fxml");
    }

    @FXML
    public void DijkstraOnAction(ActionEvent actionEvent) throws IOException {

        load("/ucr/lab12/ShortestPath.fxml");
    }

    @FXML
    public void DirectedGraphOnAction(ActionEvent actionEvent) throws IOException {

        load("/ucr/lab12/DirectedGraph.fxml");
    }
}


