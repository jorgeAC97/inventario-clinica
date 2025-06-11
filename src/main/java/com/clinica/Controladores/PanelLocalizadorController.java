package com.clinica.Controladores;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.clinica.servicios.ServicioInventario;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PanelLocalizadorController {
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnMover;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnSalir;
    @FXML
    private VBox rootVBox;

    private PanelResultadosController resultadosController;
    private ExecutorService executorService;
    private AtomicInteger searchCounter;
    private static final int SEARCH_DELAY = 300; // milisegundos
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void mostrarVentana() {
        try {
            FXMLLoader loader = new FXMLLoader(PanelLocalizadorController.class.getResource("/com/clinica/PanelLocalizador.fxml"));
            VBox root = loader.load();
            
            Stage stage = new Stage(StageStyle.TRANSPARENT); // Cambiado a TRANSPARENT para permitir bordes redondeados
            Scene scene = new Scene(root);
            scene.setFill(null); // Hacer el fondo de la escena transparente
            
            // Aplicar los estilos CSS
            scene.getStylesheets().addAll(
                PanelLocalizadorController.class.getResource("/css/PanelesStyle.css").toExternalForm()
            );
            
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.show();

            PanelLocalizadorController controller = loader.getController();
            controller.setStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setResultadosController(PanelResultadosController controller) {
        this.resultadosController = controller;
    }

    @FXML
    private void initialize() {
        resultadosController = PanelResultadosController.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        searchCounter = new AtomicInteger(0);
        
        btnBuscar.setOnAction(e -> buscar());
        
        // Búsqueda en tiempo real al escribir con debounce
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> {
            int currentCount = searchCounter.incrementAndGet();
            executorService.submit(() -> {
                try {
                    Thread.sleep(SEARCH_DELAY);
                    if (currentCount == searchCounter.get()) {
                        Platform.runLater(() -> buscar());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        // Configurar el botón mover para arrastrar la ventana
        btnMover.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        btnMover.setOnMouseDragged(event -> {
            if (stage != null) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        // Acción Limpiar
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            if (resultadosController != null) {
                resultadosController.limpiarResultados();
            }
        });

        // Acción Salir
        btnSalir.setOnAction(e -> {
            shutdown();
            Platform.exit();
            System.exit(0);
        });
    }

    private void buscar() {
        String nombre = txtBuscar.getText();
        executorService.submit(() -> {
            ServicioInventario.buscarFarmacia(nombre)
                .thenAccept(resultados -> {
                    System.out.println("Buscando: " + nombre + " - Resultados: " + resultados.size() + " (Modo: " + ServicioInventario.getModoOperacion() + ")");
                    if (resultadosController != null) {
                        Platform.runLater(() -> resultadosController.mostrarResultados(resultados));
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Error en búsqueda: " + throwable.getMessage());
                    if (resultadosController != null) {
                        Platform.runLater(() -> resultadosController.limpiarResultados());
                    }
                    return null;
                });
        });
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if (stage != null) {
            stage.close();
        }
    }

    public Button getBtnMover() {
        return btnMover;
    }
} 