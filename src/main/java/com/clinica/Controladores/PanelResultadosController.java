package com.clinica.Controladores;

import org.bson.Document;

import com.clinica.servicios.ServicioInventario;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PanelResultadosController {
    @FXML
    private ListView<Document> listaResultados;

    private static PanelResultadosController instance;
    public PanelResultadosController() { instance = this; }
    public static PanelResultadosController getInstance() { return instance; }

    @FXML
    private void initialize() {
        listaResultados.setCellFactory(lv -> new ListCell<Document>() {
            private TextFlow textFlow = new TextFlow();
            private Spinner<Integer> spinnerUnidades = new Spinner<>();
            private Document currentItem;

            {
                spinnerUnidades.setPrefWidth(80);
                spinnerUnidades.setEditable(true);
                spinnerUnidades.valueProperty().addListener((obs, oldValue, newValue) -> {
                    // Validaciones para evitar NullPointerException
                    if (currentItem != null && newValue != null && oldValue != null && 
                        !newValue.equals(oldValue) && currentItem.containsKey("codigo")) {
                        
                        String codigo = currentItem.getString("codigo");
                        if (codigo != null && !codigo.trim().isEmpty()) {
                            // Actualizar en la base de datos
                            new Thread(() -> {
                                try {
                                    ServicioInventario.actualizarUnidades(codigo, newValue)
                                        .thenAccept(exito -> {
                                            if (exito) {
                                                // Actualizar el documento localmente
                                                Platform.runLater(() -> {
                                                    currentItem.put("unidades", newValue);
                                                });
                                                System.out.println("Unidades actualizadas para " + codigo + ": " + newValue + " (Modo: " + ServicioInventario.getModoOperacion() + ")");
                                            } else {
                                                System.err.println("Error al actualizar unidades para " + codigo);
                                                // Revertir el valor en caso de error
                                                Platform.runLater(() -> {
                                                    if (oldValue != null) {
                                                        spinnerUnidades.getValueFactory().setValue(oldValue);
                                                    }
                                                });
                                            }
                                        })
                                        .exceptionally(throwable -> {
                                            System.err.println("Error al actualizar unidades para " + codigo + ": " + throwable.getMessage());
                                            // Revertir el valor en caso de error
                                            Platform.runLater(() -> {
                                                if (oldValue != null) {
                                                    spinnerUnidades.getValueFactory().setValue(oldValue);
                                                }
                                            });
                                            return null;
                                        });
                                } catch (Exception e) {
                                    System.err.println("Error al actualizar unidades para " + codigo + ": " + e.getMessage());
                                    // Revertir el valor en caso de error
                                    Platform.runLater(() -> {
                                        if (oldValue != null) {
                                            spinnerUnidades.getValueFactory().setValue(oldValue);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Document item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    currentItem = null;
                } else {
                    currentItem = item;
                    textFlow.getChildren().clear();
                    
                    try {
                        addFormattedText("-Código: ", Color.RED);
                        addFormattedText(getStringValue(item, "codigo") + "\n", Color.WHITE);
                        addFormattedText("-Nombre: ", Color.RED);
                        addFormattedText(getStringValue(item, "nombre") + "\n", Color.WHITE);
                        addFormattedText("-Dimensión: ", Color.RED);
                        addFormattedText(getStringValue(item, "dimension") + "\n", Color.WHITE);
                        addFormattedText("-Via de administración: ", Color.RED);
                        addFormattedText(getStringValue(item, "ViaAdmin") + "\n", Color.WHITE);
                        addFormattedText("-Precio: ", Color.RED);
                        // Manejo seguro del precio
                        double precio = 0.0;
                        Object precioObj = item.get("precio");
                        if (precioObj instanceof Number) {
                            precio = ((Number) precioObj).doubleValue();
                        }
                        addFormattedText(String.format("%.2f€", precio) + "\n", Color.WHITE);
                        addFormattedText("-unidades: ", Color.RED);

                        // Manejo seguro de unidades (puede ser Double o Integer)
                        int unidades = 0;
                        Object unidadesObj = item.get("unidades");
                        if (unidadesObj instanceof Number) {
                            unidades = ((Number) unidadesObj).intValue();
                        }
                        
                        spinnerUnidades.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, unidades));
                        textFlow.getChildren().add(spinnerUnidades);
                        addFormattedText("\n", Color.WHITE);

                        textFlow.getStyleClass().add("resultado-panel");
                        setGraphic(textFlow);
                    } catch (Exception e) {
                        System.err.println("Error al mostrar item: " + e.getMessage());
                        // Mostrar un texto de error en lugar de fallar
                        textFlow.getChildren().clear();
                        addFormattedText("Error al cargar producto", Color.RED);
                        setGraphic(textFlow);
                    }
                }
            }

            private String getStringValue(Document doc, String key) {
                try {
                    String value = doc.getString(key);
                    return value != null ? value : "N/A";
                } catch (Exception e) {
                    return "N/A";
                }
            }

            private void addFormattedText(String content, Color color) {
                Text text = new Text(content);
                text.setFill(color);
                text.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
                textFlow.getChildren().add(text);
            }
        });
    }

    public void mostrarResultados(java.util.List<Document> resultados) {
        System.out.println("Mostrando en lista: " + resultados.size());
        ObservableList<Document> lista = FXCollections.observableArrayList(resultados);
        listaResultados.setItems(lista);
    }

    public void limpiarResultados() {
        listaResultados.getItems().clear();
    }
} 