package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PanelInventarioController {
    @FXML
    private Button btnRegresar;
    @FXML
    private StackPane root;

    @FXML
    private void initialize() {
        try {
            // Cargar solo el panel de resultados
            FXMLLoader resultadosLoader = new FXMLLoader(getClass().getResource("/com/clinica/PanelResultados.fxml"));
            Parent resultadosNode = resultadosLoader.load();

            // Limpiar y añadir el nodo al root
            root.getChildren().clear();
            root.getChildren().add(resultadosNode);

            // Mostrar la ventana del localizador
            PanelLocalizadorController.mostrarVentana();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() throws IOException {
        // Cerrar la ventana del localizador si está abierta
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/Menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
        stage.setScene(scene);
        stage.setTitle("Menú Principal");
    }
} 