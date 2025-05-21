package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PanelInventarioController {
    @FXML
    private Button btnRegresar;
    @FXML
    private StackPane root;

    private double mouseAnchorX;
    private double mouseAnchorY;

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

            // Limpiar y añadir los nodos al root (StackPane)
            root.getChildren().clear();
            root.getChildren().addAll(resultadosNode, localizadorNode); // resultados al fondo, localizador encima

            // Quitar alineación automática para permitir movimiento libre
            StackPane.setAlignment(localizadorNode, null);

            // Forzar que el panel localizador solo ocupe el tamaño de su contenido
            ((Region) localizadorNode).setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            ((Region) localizadorNode).setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            ((Region) localizadorNode).setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            // Posicionar el panel localizador en una posición inicial (por ejemplo, arriba a la izquierda)
            localizadorNode.setLayoutX(30);
            localizadorNode.setLayoutY(30);

            // Obtener el botón mover del controlador localizador
            Button btnMover = localizadorController.getBtnMover();

            // Lógica de arrastre SOLO en el botón mover
            btnMover.setOnMousePressed(event -> {
                mouseAnchorX = event.getSceneX() - localizadorNode.getLayoutX();
                mouseAnchorY = event.getSceneY() - localizadorNode.getLayoutY();
            });
            btnMover.setOnMouseDragged(event -> {
                double newX = event.getSceneX() - mouseAnchorX;
                double newY = event.getSceneY() - mouseAnchorY;
                localizadorNode.setLayoutX(newX);
                localizadorNode.setLayoutY(newY);
            });

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