package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;
import com.clinica.db.ConexionMongo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
    private void iniciarSesion() throws IOException {
        String nombre = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (ConexionMongo.validarCredenciales(nombre, contrasena)) {
            // Acceso permitido: mostrar siguiente vista
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/PanelInventario.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
            stage.setScene(scene);
            stage.setTitle(ConstantesVentana.TITULO_PRINCIPAL);
        } else {
            // Mostrar mensaje de error: usuario o contraseña incorrectos
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos");
            alert.showAndWait();
        }
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