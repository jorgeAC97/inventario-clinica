package com.clinica.Controladores;

import java.io.IOException;

import com.clinica.ConstantesVentana;
import com.clinica.db.ConexionMongo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/Menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
            stage.setScene(scene);
            stage.setTitle(ConstantesVentana.TITULO_PRINCIPAL);
        } else {
            // Mostrar mensaje de error: usuario o contraseña incorrectos
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos");
            alert.showAndWait();
        }
    }
} 