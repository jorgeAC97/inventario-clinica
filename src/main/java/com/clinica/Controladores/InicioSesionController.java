package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;
import com.clinica.servicios.ServicioInventario;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InicioSesionController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Label lblModoOperacion;

    @FXML
    private void initialize() {
        // Mostrar el modo de operación
        if (lblModoOperacion != null) {
            String modo = ServicioInventario.getModoOperacion();
            lblModoOperacion.setText("Modo: " + modo);
            lblModoOperacion.setStyle(modo.equals("SERVIDOR") ? 
                "-fx-text-fill: green;" : "-fx-text-fill: orange;");
        }
    }

    @FXML
    private void iniciarSesion() throws IOException {
        String nombre = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (nombre.trim().isEmpty() || contrasena.trim().isEmpty()) {
            mostrarError("Error de validación", "Por favor ingrese usuario y contraseña");
            return;
        }

        // Deshabilitar botón mientras se procesa
        btnIniciarSesion.setDisable(true);
        btnIniciarSesion.setText("Validando...");

        // Usar el servicio para validar credenciales
        ServicioInventario.validarCredenciales(nombre, contrasena)
            .thenAccept(valido -> {
                Platform.runLater(() -> {
                    btnIniciarSesion.setDisable(false);
                    btnIniciarSesion.setText("Iniciar Sesión");
                    
                    if (valido) {
                        try {
                            // Acceso permitido: mostrar siguiente vista
                            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/PanelInventario.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
                            stage.setScene(scene);
                            stage.setTitle(ConstantesVentana.TITULO_PRINCIPAL);
                            
                            System.out.println("Login exitoso para usuario: " + nombre + " en modo " + ServicioInventario.getModoOperacion());
                        } catch (IOException e) {
                            mostrarError("Error de aplicación", "No se pudo cargar la pantalla principal");
                        }
                    } else {
                        // Mostrar mensaje de error: usuario o contraseña incorrectos
                        mostrarError("Error de inicio de sesión", "Usuario o contraseña incorrectos");
                    }
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    btnIniciarSesion.setDisable(false);
                    btnIniciarSesion.setText("Iniciar Sesión");
                    mostrarError("Error de conexión", "Error al validar credenciales: " + throwable.getMessage());
                });
                return null;
            });
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleRegister() {
        // Aquí irá la lógica para abrir el panel de registro
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registro");
        alert.setHeaderText(null);
        alert.setContentText("Abriendo panel de registro...");
        alert.showAndWait();
    }
    
    @FXML
    private void handleExit() {
        // Cerrar la ventana actual
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
} 