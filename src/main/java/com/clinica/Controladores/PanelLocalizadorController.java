package com.clinica.Controladores;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PanelLocalizadorController {
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;

    private PanelResultadosController resultadosController;
    private ExecutorService executorService;
    private AtomicInteger searchCounter;
    private static final int SEARCH_DELAY = 300; // milisegundos

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
    }

    private void buscar() {
        String nombre = txtBuscar.getText();
        executorService.submit(() -> {
            List<Document> resultados = com.clinica.db.ConexionMongo.buscarFarmaciaPorNombre(nombre);
            System.out.println("Buscando: " + nombre + " - Resultados: " + resultados.size());
            if (resultadosController != null) {
                Platform.runLater(() -> resultadosController.mostrarResultados(resultados));
            }
        });
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 