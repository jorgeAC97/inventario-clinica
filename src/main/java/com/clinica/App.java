package com.clinica;

import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene VentanaPrincipal;
    private static final int PUERTO_SERVIDOR = 50005;
    private static Socket socketCliente;
    
    private void conectarServidor() {
        try {
            socketCliente = new Socket("localhost", PUERTO_SERVIDOR);
            System.out.println("Conectado al servidor en el puerto " + PUERTO_SERVIDOR);
        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Conectar al servidor antes de iniciar la interfaz
        conectarServidor();
        
        // Cargar el FXML de inicio de sesión
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/InicioSesion.fxml"));
        Parent root = loader.load();
        
        // Crear la escena con el contenido del FXML usando las constantes
        VentanaPrincipal = new Scene(root, ConstantesVentana.ANCHO_VENTANA, ConstantesVentana.ALTO_VENTANA);
        
        // Configurar y mostrar la ventana
        stage.setScene(VentanaPrincipal);
        stage.setTitle(ConstantesVentana.TITULO_INICIO_SESION);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
                System.out.println("Conexión con el servidor cerrada");
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión con el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}