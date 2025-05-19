package com.clinica.Controladores;

import org.bson.Document;

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
                    if (currentItem != null && newValue != null && !newValue.equals(oldValue)) {
                        // Actualizar en la base de datos
                        new Thread(() -> {
                            com.clinica.db.ConexionMongo.actualizarUnidadesProducto(currentItem.getString("codigo"), newValue);
                            // Actualizar el documento localmente
                            Platform.runLater(() -> {
                                currentItem.put("unidades", newValue);
                            });
                        }).start();
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
                    setStyle("-fx-background-color: #2b2b2b;");

                    addFormattedText("--------------------------------\n", Color.GRAY);
                    addFormattedText("-Código: ", Color.RED);
                    addFormattedText(item.getString("codigo") + "\n", Color.WHITE);
                    addFormattedText("-Nombre: ", Color.RED);
                    addFormattedText(item.getString("nombre") + "\n", Color.WHITE);
                    addFormattedText("-Dimensión: ", Color.RED);
                    addFormattedText(item.getString("dimension") + "\n", Color.WHITE);
                    addFormattedText("-Via de administración: ", Color.RED);
                    addFormattedText(item.getString("ViaAdmin") + "\n", Color.WHITE);
                    addFormattedText("-unidades: ", Color.RED);

                    int unidades = item.getInteger("unidades", 0);
                    spinnerUnidades.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, unidades));
                    textFlow.getChildren().add(spinnerUnidades);
                    addFormattedText("\n", Color.WHITE);

                    setGraphic(textFlow);
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
} 