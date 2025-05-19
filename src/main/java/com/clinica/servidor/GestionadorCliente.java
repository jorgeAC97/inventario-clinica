package com.clinica.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestionadorCliente implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean conectado;

    public GestionadorCliente(Socket socket) {
        this.socket = socket;
        this.conectado = true;
        try {
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.salida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error al inicializar el gestor de cliente: " + e.getMessage());
            this.conectado = false;
        }
    }

    @Override
    public void run() {
        try {
            while (conectado) {
                String mensaje = entrada.readLine();
                if (mensaje == null) {
                    desconectar();
                    break;
                }
                procesarMensaje(mensaje);
            }
        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
            desconectar();
        }
    }

    private void procesarMensaje(String mensaje) {
        // Aquí se implementará la lógica para procesar los mensajes del cliente
        System.out.println("Mensaje recibido: " + mensaje);
        // Por ahora, solo enviamos una respuesta de prueba
        enviarMensaje("Mensaje recibido correctamente");
    }

    public void enviarMensaje(String mensaje) {
        if (conectado) {
            salida.println(mensaje);
        }
    }

    public void desconectar() {
        conectado = false;
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        return conectado;
    }
} 