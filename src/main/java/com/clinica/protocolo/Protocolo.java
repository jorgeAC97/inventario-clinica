package com.clinica.protocolo;

public interface Protocolo {
    // Separadores para el protocolo
    String SEPARADOR_CODIGO = "|";  // Separa el código de operación de los parámetros
    String SEPARADOR_PARAMETROS = ":";  // Separa los parámetros entre sí
    
    // Códigos de operación del cliente al servidor
    int LOGIN_REQUEST = 1;
    int BUSCAR_FARMACIA_REQUEST = 2;
    int OBTENER_FARMACIA_REQUEST = 3;
    int ACTUALIZAR_UNIDADES_REQUEST = 4;
    int ELIMINAR_PRODUCTO_REQUEST = 5;
    
    // Códigos de respuesta del servidor al cliente
    int LOGIN_RESPONSE = 100;
    int BUSCAR_FARMACIA_RESPONSE = 101;
    int OBTENER_FARMACIA_RESPONSE = 102;
    int ACTUALIZAR_UNIDADES_RESPONSE = 103;
    int ELIMINAR_PRODUCTO_RESPONSE = 104;
    
    // Códigos de estado
    int SUCCESS = 200;
    int LOGIN_SUCCESS = 201;
    int LOGIN_FAILED = 401;
    int INVALID_CREDENTIALS = 402;
    int NOT_FOUND = 404;
    int SERVER_ERROR = 500;
    int DATABASE_ERROR = 501;

    // Códigos que debe manejar el servidor de inventario
    static final int OBTENER_MEDICAMENTOS_INVENTARIO = 9001;
    static final int OBTENER_MEDICAMENTOS_INVENTARIO_RESPONSE = 9002;
    static final int BUSCAR_MEDICAMENTOS_INVENTARIO = 9003;
    static final int BUSCAR_MEDICAMENTOS_INVENTARIO_RESPONSE = 9004;
    static final int ERROR_INVENTARIO = 9999;
    
    // Métodos de utilidad para construir mensajes
    static String construirMensaje(int codigo, String... parametros) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append(codigo).append(SEPARADOR_CODIGO);
        
        if (parametros != null && parametros.length > 0) {
            for (int i = 0; i < parametros.length; i++) {
                if (i > 0) {
                    mensaje.append(SEPARADOR_PARAMETROS);
                }
                mensaje.append(parametros[i] != null ? parametros[i] : "");
            }
        }
        
        return mensaje.toString();
    }
    
    static String[] parsearMensaje(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return new String[0];
        }
        
        String[] partes = mensaje.split("\\" + SEPARADOR_CODIGO, 2);
        if (partes.length < 2) {
            return new String[]{partes[0]};
        }
        
        String[] parametros = partes[1].split(SEPARADOR_PARAMETROS);
        String[] resultado = new String[parametros.length + 1];
        resultado[0] = partes[0]; // código
        System.arraycopy(parametros, 0, resultado, 1, parametros.length);
        
        return resultado;
    }
    
    static int obtenerCodigo(String mensaje) {
        try {
            String[] partes = parsearMensaje(mensaje);
            return partes.length > 0 ? Integer.parseInt(partes[0]) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 