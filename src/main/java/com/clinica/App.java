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
    private static boolean servidorConectado = false;
    
    private void conectarServidor() {
        try {
            socketCliente = new Socket("localhost", PUERTO_SERVIDOR);
            servidorConectado = true;
            System.out.println("Conectado al servidor en el puerto " + PUERTO_SERVIDOR);
        } catch (IOException e) {
            servidorConectado = false;
            System.out.println("Servidor no disponible. La aplicación funcionará en modo local.");
            System.out.println("Para usar funcionalidades de red, inicie el servidor primero.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Intentar conectar al servidor (opcional)
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

    // Métodos estáticos para acceder al estado del servidor
    public static boolean isServidorConectado() {
        return servidorConectado;
    }

    public static Socket getSocketCliente() {
        return socketCliente;
    }

    public static void main(String[] args) {
        launch();
    }
}