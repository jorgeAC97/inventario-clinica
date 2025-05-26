package com.clinica.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static final int PUERTO = 50005;
    private ServerSocket serverSocket;
    private boolean ejecutando;
    private ExecutorService poolHilos;
    private List<GestionadorCliente> clientesConectados;

    public Servidor() {
        this.poolHilos = Executors.newCachedThreadPool();
        this.clientesConectados = new ArrayList<>();
    }

    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            ejecutando = true;
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            System.out.println("Esperando conexiones de clientes...");

            while (ejecutando) {
                try {
                    Socket socketCliente = serverSocket.accept();
                    System.out.println("Cliente conectado desde: " + socketCliente.getInetAddress());
                    
                    GestionadorCliente gestor = new GestionadorCliente(socketCliente);
                    clientesConectados.add(gestor);
                    poolHilos.execute(gestor);
                    
                } catch (IOException e) {
                    if (ejecutando) {
                        System.err.println("Error al aceptar conexión de cliente: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public void detener() {
        ejecutando = false;
        try {
            // Cerrar todas las conexiones de clientes
            for (GestionadorCliente cliente : clientesConectados) {
                cliente.desconectar();
            }
            clientesConectados.clear();
            
            // Cerrar el pool de hilos
            poolHilos.shutdown();
            
            // Cerrar el socket del servidor
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println("Servidor detenido");
        } catch (IOException e) {
            System.err.println("Error al detener el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        
        // Agregar hook para cerrar el servidor correctamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nDeteniendo servidor...");
            servidor.detener();
        }));
        
        servidor.iniciar();
    }
} 