package com.clinica;

import java.io.IOException;

import com.clinica.cliente.ClienteComunicacion;

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
    private static ClienteComunicacion clienteComunicacion;
    private static boolean servidorConectado = false;
    
    private void conectarServidor() {
        try {
            clienteComunicacion = new ClienteComunicacion();
            servidorConectado = clienteComunicacion.conectar("localhost", PUERTO_SERVIDOR);
            
            if (servidorConectado) {
                System.out.println("Conectado al servidor en el puerto " + PUERTO_SERVIDOR);
            } else {
                System.out.println("Servidor no disponible. La aplicación funcionará en modo local.");
                System.out.println("Para usar funcionalidades de red, inicie el servidor primero.");
            }
        } catch (Exception e) {
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
            if (clienteComunicacion != null && clienteComunicacion.isConectado()) {
                clienteComunicacion.desconectar();
                System.out.println("Conexión con el servidor cerrada");
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión con el servidor: " + e.getMessage());
        }
    }

    // Métodos estáticos para acceder al estado del servidor
    public static boolean isServidorConectado() {
        return servidorConectado && clienteComunicacion != null && clienteComunicacion.isConectado();
    }

    public static ClienteComunicacion getClienteComunicacion() {
        return clienteComunicacion;
    }

    public static void main(String[] args) {
        launch();
    }
}