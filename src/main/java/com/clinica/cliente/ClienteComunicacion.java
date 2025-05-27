package com.clinica.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;

import com.clinica.protocolo.Protocolo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClienteComunicacion {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean conectado;
    private AtomicInteger contadorMensajes;
    private ConcurrentHashMap<Integer, CompletableFuture<String>> respuestasPendientes;
    private Thread hiloEscucha;
    private Gson gson;

    public ClienteComunicacion() {
        this.contadorMensajes = new AtomicInteger(0);
        this.respuestasPendientes = new ConcurrentHashMap<>();
        this.gson = new Gson();
    }

    public boolean conectar(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            conectado = true;

            // Iniciar hilo para escuchar respuestas
            hiloEscucha = new Thread(this::escucharRespuestas);
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();

            return true;
        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
            return false;
        }
    }

    public void desconectar() {
        conectado = false;
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    private void escucharRespuestas() {
        try {
            while (conectado) {
                String respuesta = entrada.readLine();
                if (respuesta == null) break;

                // Procesar respuesta
                procesarRespuesta(respuesta);
            }
        } catch (IOException e) {
            if (conectado) {
                System.err.println("Error al escuchar respuestas: " + e.getMessage());
            }
        }
    }

    private void procesarRespuesta(String respuesta) {
        String[] partes = Protocolo.parsearMensaje(respuesta);
        if (partes.length < 2) return;

        try {
            int codigoRespuesta = Integer.parseInt(partes[0]);
            int idMensaje = Integer.parseInt(partes[1]);

            CompletableFuture<String> futuro = respuestasPendientes.remove(idMensaje);
            if (futuro != null) {
                futuro.complete(respuesta);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error al procesar respuesta: " + e.getMessage());
        }
    }

    private CompletableFuture<String> enviarMensaje(String mensaje) {
        if (!conectado) {
            CompletableFuture<String> futuro = new CompletableFuture<>();
            futuro.completeExceptionally(new RuntimeException("No conectado al servidor"));
            return futuro;
        }

        int idMensaje = contadorMensajes.incrementAndGet();
        String mensajeCompleto = mensaje + Protocolo.SEPARADOR_PARAMETROS + idMensaje;

        CompletableFuture<String> futuro = new CompletableFuture<>();
        respuestasPendientes.put(idMensaje, futuro);

        salida.println(mensajeCompleto);
        return futuro;
    }

    // Métodos específicos del protocolo

    public CompletableFuture<Boolean> login(String usuario, String contrasena) {
        String mensaje = Protocolo.construirMensaje(Protocolo.LOGIN_REQUEST, usuario, contrasena);
        return enviarMensaje(mensaje).thenApply(respuesta -> {
            String[] partes = Protocolo.parsearMensaje(respuesta);
            if (partes.length >= 3) {
                try {
                    int estado = Integer.parseInt(partes[2]);
                    return estado == Protocolo.LOGIN_SUCCESS;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        });
    }

    public CompletableFuture<List<Document>> buscarFarmacia(String nombre) {
        String mensaje = Protocolo.construirMensaje(Protocolo.BUSCAR_FARMACIA_REQUEST, nombre);
        return enviarMensaje(mensaje).thenApply(respuesta -> {
            String[] partes = Protocolo.parsearMensaje(respuesta);
            if (partes.length >= 4) {
                try {
                    int estado = Integer.parseInt(partes[2]);
                    if (estado == Protocolo.SUCCESS) {
                        String base64Data = partes[3];
                        String jsonData = new String(Base64.getDecoder().decode(base64Data));
                        return gson.fromJson(jsonData, new TypeToken<List<Document>>(){}.getType());
                    }
                } catch (Exception e) {
                    System.err.println("Error al parsear respuesta de búsqueda: " + e.getMessage());
                }
            }
            return new ArrayList<>();
        });
    }

    public CompletableFuture<List<Document>> obtenerFarmacia() {
        String mensaje = Protocolo.construirMensaje(Protocolo.OBTENER_FARMACIA_REQUEST);
        return enviarMensaje(mensaje).thenApply(respuesta -> {
            String[] partes = Protocolo.parsearMensaje(respuesta);
            if (partes.length >= 4) {
                try {
                    int estado = Integer.parseInt(partes[2]);
                    if (estado == Protocolo.SUCCESS) {
                        String base64Data = partes[3];
                        String jsonData = new String(Base64.getDecoder().decode(base64Data));
                        return gson.fromJson(jsonData, new TypeToken<List<Document>>(){}.getType());
                    }
                } catch (Exception e) {
                    System.err.println("Error al parsear respuesta de farmacia: " + e.getMessage());
                }
            }
            return new ArrayList<>();
        });
    }

    public CompletableFuture<Boolean> actualizarUnidades(String codigo, int unidades) {
        String mensaje = Protocolo.construirMensaje(Protocolo.ACTUALIZAR_UNIDADES_REQUEST, codigo, String.valueOf(unidades));
        return enviarMensaje(mensaje).thenApply(respuesta -> {
            String[] partes = Protocolo.parsearMensaje(respuesta);
            if (partes.length >= 3) {
                try {
                    int estado = Integer.parseInt(partes[2]);
                    return estado == Protocolo.SUCCESS;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        });
    }

    public boolean isConectado() {
        return conectado && socket != null && !socket.isClosed();
    }
} 