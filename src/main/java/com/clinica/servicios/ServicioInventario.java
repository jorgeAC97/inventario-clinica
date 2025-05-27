package com.clinica.servicios;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;

import com.clinica.App;
import com.clinica.cliente.ClienteComunicacion;
import com.clinica.db.ConexionMongo;

public class ServicioInventario {
    
    public static CompletableFuture<Boolean> validarCredenciales(String usuario, String contrasena) {
        if (App.isServidorConectado()) {
            // Usar servidor
            ClienteComunicacion cliente = App.getClienteComunicacion();
            return cliente.login(usuario, contrasena);
        } else {
            // Usar base de datos local
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ConexionMongo.validarCredenciales(usuario, contrasena);
                } catch (Exception e) {
                    System.err.println("Error en validación local: " + e.getMessage());
                    return false;
                }
            });
        }
    }
    
    public static CompletableFuture<List<Document>> buscarFarmacia(String nombre) {
        if (App.isServidorConectado()) {
            // Usar servidor
            ClienteComunicacion cliente = App.getClienteComunicacion();
            return cliente.buscarFarmacia(nombre);
        } else {
            // Usar base de datos local
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ConexionMongo.buscarFarmaciaPorNombre(nombre);
                } catch (Exception e) {
                    System.err.println("Error en búsqueda local: " + e.getMessage());
                    return List.of();
                }
            });
        }
    }
    
    public static CompletableFuture<List<Document>> obtenerFarmacia() {
        if (App.isServidorConectado()) {
            // Usar servidor
            ClienteComunicacion cliente = App.getClienteComunicacion();
            return cliente.obtenerFarmacia();
        } else {
            // Usar base de datos local
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ConexionMongo.obtenerFarmacia();
                } catch (Exception e) {
                    System.err.println("Error al obtener farmacia local: " + e.getMessage());
                    return List.of();
                }
            });
        }
    }
    
    public static CompletableFuture<Boolean> actualizarUnidades(String codigo, int unidades) {
        if (App.isServidorConectado()) {
            // Usar servidor
            ClienteComunicacion cliente = App.getClienteComunicacion();
            return cliente.actualizarUnidades(codigo, unidades);
        } else {
            // Usar base de datos local
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ConexionMongo.actualizarUnidadesProducto(codigo, unidades);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error al actualizar unidades local: " + e.getMessage());
                    return false;
                }
            });
        }
    }
    
    public static String getModoOperacion() {
        return App.isServidorConectado() ? "SERVIDOR" : "LOCAL";
    }
} 