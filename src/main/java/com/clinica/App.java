package com.clinica;

import java.io.IOException;

import com.clinica.cliente.ClienteComunicacion;
import com.clinica.db.ConexionMongo;
import com.clinica.db.ImportarDatos;

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

    private void verificarYCargarDatos() {
        System.out.println("Verificando datos de la base de datos...");
        
        try {
            if (!ConexionMongo.existeColeccionFarmacia()) {
                System.out.println("La colección 'farmacia' no existe o está vacía.");
                System.out.println("Iniciando importación de datos...");
                
                // Ejecutar ImportarDatos
                ImportarDatos.main(new String[]{});
                System.out.println("Datos importados correctamente.");
            } else {
                System.out.println("La colección 'farmacia' ya existe con datos.");
            }
        } catch (Exception e) {
            System.err.println("Error al verificar/importar datos: " + e.getMessage());
            System.out.println("La aplicación continuará sin datos iniciales.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Verificar y cargar datos si es necesario
        verificarYCargarDatos();
        
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