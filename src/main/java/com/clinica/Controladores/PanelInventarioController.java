package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PanelInventarioController {
    @FXML
    private Button btnRegresar;
    @FXML
    private HBox root;

    @FXML
    private void initialize() {
        try {
            // Cargar los FXML de forma manual para tener acceso a los controladores
            FXMLLoader localizadorLoader = new FXMLLoader(getClass().getResource("/com/clinica/PanelLocalizador.fxml"));
            Parent localizadorNode = localizadorLoader.load();
            PanelLocalizadorController localizadorController = localizadorLoader.getController();

            FXMLLoader resultadosLoader = new FXMLLoader(getClass().getResource("/com/clinica/PanelResultados.fxml"));
            Parent resultadosNode = resultadosLoader.load();
            PanelResultadosController resultadosController = resultadosLoader.getController();

            // Limpiar y añadir los nodos al root
            root.getChildren().clear();
            root.getChildren().addAll(localizadorNode, resultadosNode);

            // Conectar los controladores
            localizadorController.setResultadosController(resultadosController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() throws IOException {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/Menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
        stage.setScene(scene);
        stage.setTitle("Menú Principal");
    }
} 