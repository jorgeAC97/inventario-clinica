package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private Button btnAnadirInventario;

    @FXML
    private void abrirAnadirInventario() throws IOException {
        Stage stage = (Stage) btnAnadirInventario.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/PanelInventario.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
        stage.setScene(scene);
        stage.setTitle("Inventario");
    }
} 