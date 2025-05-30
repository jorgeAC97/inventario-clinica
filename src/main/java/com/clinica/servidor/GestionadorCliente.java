package com.clinica.servidor;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;
import java.util.List;
 
import org.bson.Document;
 
import com.clinica.db.ConexionMongo;
import com.clinica.protocolo.Protocolo;
import com.google.gson.Gson;
 
public class GestionadorCliente implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean conectado;
    private String clienteInfo;
    private Gson gson;
 
    public GestionadorCliente(Socket socket) {
        this.socket = socket;
        this.conectado = true;
        this.clienteInfo = socket.getInetAddress().toString();
        this.gson = new Gson();
        try {
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.salida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error al inicializar el gestor de cliente " + clienteInfo + ": " + e.getMessage());
            this.conectado = false;
        }
    }
 
    @Override
    public void run() {
        try {
            while (conectado && !socket.isClosed()) {
                String mensaje = entrada.readLine();
                if (mensaje == null) {
                    // Cliente se desconectó normalmente
                    System.out.println("Cliente " + clienteInfo + " se desconectó");
                    desconectar();
                    break;
                }
                procesarMensaje(mensaje);
            }
        } catch (SocketException e) {
            // Conexión cerrada por el cliente - esto es normal
            System.out.println("Cliente " + clienteInfo + " cerró la conexión");
        } catch (IOException e) {
            // Solo mostrar error si no es una desconexión normal
            if (conectado && !socket.isClosed()) {
                System.err.println("Error en la comunicación con el cliente " + clienteInfo + ": " + e.getMessage());
            }
        } finally {
            desconectar();
        }
    }
 
    private void procesarMensaje(String mensaje) {
        System.out.println("Mensaje recibido de " + clienteInfo + ": " + mensaje);
       
        String[] partes = Protocolo.parsearMensaje(mensaje);
        if (partes.length < 2) {
            enviarError("Mensaje malformado", "");
            return;
        }
 
        try {
            int codigo = Integer.parseInt(partes[0]);
            String idMensaje = partes[partes.length - 1]; // El ID siempre es el último parámetro
 
            switch (codigo) {
                case Protocolo.LOGIN_REQUEST:
                    procesarLogin(partes, idMensaje);
                    break;
                case Protocolo.BUSCAR_FARMACIA_REQUEST:
                    procesarBuscarFarmacia(partes, idMensaje);
                    break;
                case Protocolo.OBTENER_FARMACIA_REQUEST:
                    procesarObtenerFarmacia(partes, idMensaje);
                    break;
                case Protocolo.ACTUALIZAR_UNIDADES_REQUEST:
                    procesarActualizarUnidades(partes, idMensaje);
                    break;
                case Protocolo.OBTENER_MEDICAMENTOS_INVENTARIO:
                    procesarObtenerMedicamentosInventario(partes, idMensaje);
                    break;
                case Protocolo.BUSCAR_MEDICAMENTOS_INVENTARIO:
                    procesarBuscarMedicamentosInventario(partes, idMensaje);
                    break;
                case Protocolo.REDUCIR_INVENTARIO:
                    procesarReducirInventario(partes, idMensaje);
                    break;
                case Protocolo.RESTABLECER_INVENTARIO:
                    procesarRestablecerInventario(partes, idMensaje);
                    break;
                default:
                    enviarError("Código de operación no reconocido: " + codigo, idMensaje);
            }
        } catch (NumberFormatException e) {
            enviarError("Código de operación inválido", "");
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            enviarError("Error interno del servidor", "");
        }
    }
 
    private void procesarLogin(String[] partes, String idMensaje) {
        if (partes.length < 4) {
            enviarRespuesta(Protocolo.LOGIN_RESPONSE, idMensaje, String.valueOf(Protocolo.INVALID_CREDENTIALS), "Parámetros insuficientes");
            return;
        }
 
        String usuario = partes[1];
        String contrasena = partes[2];
 
        try {
            boolean valido = ConexionMongo.validarCredenciales(usuario, contrasena);
            if (valido) {
                enviarRespuesta(Protocolo.LOGIN_RESPONSE, idMensaje, String.valueOf(Protocolo.LOGIN_SUCCESS), "Login exitoso");
                System.out.println("Login exitoso para usuario: " + usuario + " desde " + clienteInfo);
            } else {
                enviarRespuesta(Protocolo.LOGIN_RESPONSE, idMensaje, String.valueOf(Protocolo.LOGIN_FAILED), "Credenciales inválidas");
                System.out.println("Login fallido para usuario: " + usuario + " desde " + clienteInfo);
            }
        } catch (Exception e) {
            System.err.println("Error en validación de credenciales: " + e.getMessage());
            enviarRespuesta(Protocolo.LOGIN_RESPONSE, idMensaje, String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos");
        }
    }
 
    private void procesarBuscarFarmacia(String[] partes, String idMensaje) {
        if (partes.length < 3) {
            enviarRespuesta(Protocolo.BUSCAR_FARMACIA_RESPONSE, idMensaje, String.valueOf(Protocolo.SERVER_ERROR), "Parámetros insuficientes");
            return;
        }
 
        String nombre = partes[1];
 
        try {
            List<Document> resultados = ConexionMongo.buscarFarmaciaPorNombre(nombre);
            String jsonData = gson.toJson(resultados);
            String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes());
            enviarRespuesta(Protocolo.BUSCAR_FARMACIA_RESPONSE, idMensaje, String.valueOf(Protocolo.SUCCESS), base64Data);
            System.out.println("Búsqueda realizada para '" + nombre + "' desde " + clienteInfo + " - " + resultados.size() + " resultados");
        } catch (Exception e) {
            System.err.println("Error en búsqueda de farmacia: " + e.getMessage());
            enviarRespuesta(Protocolo.BUSCAR_FARMACIA_RESPONSE, idMensaje, String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos");
        }
    }
 
    private void procesarObtenerFarmacia(String[] partes, String idMensaje) {
        try {
            List<Document> resultados = ConexionMongo.obtenerFarmacia();
            String jsonData = gson.toJson(resultados);
            String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes());
            enviarRespuesta(Protocolo.OBTENER_FARMACIA_RESPONSE, idMensaje, String.valueOf(Protocolo.SUCCESS), base64Data);
            System.out.println("Farmacia completa enviada a " + clienteInfo + " - " + resultados.size() + " productos");
        } catch (Exception e) {
            System.err.println("Error al obtener farmacia: " + e.getMessage());
            enviarRespuesta(Protocolo.OBTENER_FARMACIA_RESPONSE, idMensaje, String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos");
        }
    }
 
    private void procesarActualizarUnidades(String[] partes, String idMensaje) {
        if (partes.length < 4) {
            enviarRespuesta(Protocolo.ACTUALIZAR_UNIDADES_RESPONSE, idMensaje, String.valueOf(Protocolo.SERVER_ERROR), "Parámetros insuficientes");
            return;
        }
 
        String codigo = partes[1];
        String unidadesStr = partes[2];
 
        try {
            int unidades = Integer.parseInt(unidadesStr);
            ConexionMongo.actualizarUnidadesProducto(codigo, unidades);
            enviarRespuesta(Protocolo.ACTUALIZAR_UNIDADES_RESPONSE, idMensaje, String.valueOf(Protocolo.SUCCESS), "Unidades actualizadas");
            System.out.println("Unidades actualizadas para " + codigo + " a " + unidades + " desde " + clienteInfo);
        } catch (NumberFormatException e) {
            enviarRespuesta(Protocolo.ACTUALIZAR_UNIDADES_RESPONSE, idMensaje, String.valueOf(Protocolo.SERVER_ERROR), "Número de unidades inválido");
        } catch (Exception e) {
            System.err.println("Error al actualizar unidades: " + e.getMessage());
            enviarRespuesta(Protocolo.ACTUALIZAR_UNIDADES_RESPONSE, idMensaje, String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos");
        }
    }
 
    private void procesarObtenerMedicamentosInventario(String[] partes, String idMensaje) {
        try {
            // Obtener todos los medicamentos con stock > 0 desde la base de datos
            List<Document> medicamentos = ConexionMongo.obtenerMedicamentosConStock();
           
            // Convertir a JSON y codificar en Base64
            String jsonData = gson.toJson(medicamentos);
            String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes());
           
            // Enviar respuesta exitosa
            enviarRespuesta(Protocolo.OBTENER_MEDICAMENTOS_INVENTARIO_RESPONSE, idMensaje,
                           String.valueOf(Protocolo.SUCCESS), base64Data);
           
            System.out.println("Enviados " + medicamentos.size() + " medicamentos al cliente " + clienteInfo);
           
        } catch (Exception e) {
            System.err.println("Error al obtener medicamentos: " + e.getMessage());
            enviarRespuesta(Protocolo.ERROR_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.DATABASE_ERROR), "Error al obtener medicamentos: " + e.getMessage());
        }
    }
 
    private void procesarBuscarMedicamentosInventario(String[] partes, String idMensaje) {
        if (partes.length < 3) {
            enviarRespuesta(Protocolo.BUSCAR_MEDICAMENTOS_INVENTARIO_RESPONSE, idMensaje,
                           String.valueOf(Protocolo.SERVER_ERROR), "Parámetros insuficientes");
            return;
        }
 
        String terminoBusqueda = partes[1];
 
        try {
            // Buscar medicamentos por nombre/código/laboratorio
            List<Document> medicamentosEncontrados = ConexionMongo.buscarMedicamentos(terminoBusqueda);
 
            // Convertir a JSON y codificar en Base64
            String jsonData = gson.toJson(medicamentosEncontrados);
            String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes());
 
            // Enviar respuesta exitosa
            enviarRespuesta(Protocolo.BUSCAR_MEDICAMENTOS_INVENTARIO_RESPONSE, idMensaje,
                           String.valueOf(Protocolo.SUCCESS), base64Data);
 
            System.out.println("Enviados " + medicamentosEncontrados.size() +
                             " medicamentos encontrados para: " + terminoBusqueda + " al cliente " + clienteInfo);
 
        } catch (Exception e) {
            System.err.println("Error al buscar medicamentos: " + e.getMessage());
            enviarRespuesta(Protocolo.ERROR_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.DATABASE_ERROR), "Error al buscar medicamentos: " + e.getMessage());
        }
    }
 
    private void procesarReducirInventario(String[] partes, String idMensaje) {
        if (partes.length < 4) {
            enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.SERVER_ERROR), "Parámetros insuficientes");
            return;
        }

        String codigo = partes[1];
        String cantidadStr = partes[2];

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            
            if (cantidad <= 0) {
                enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                               String.valueOf(Protocolo.SERVER_ERROR), "La cantidad debe ser mayor a 0");
                return;
            }
            
            // Intentar reducir unidades usando el método que valida stock
            boolean exito = ConexionMongo.reducirUnidadesProducto(codigo, cantidad);
            
            if (exito) {
                enviarRespuesta(Protocolo.REDUCIR_INVENTARIO_RESPONSE, idMensaje, 
                               String.valueOf(Protocolo.SUCCESS), "Inventario reducido exitosamente");
                System.out.println("Inventario reducido para " + codigo + " - " + cantidad + " unidades desde " + clienteInfo);
            } else {
                // Verificar si el producto existe para dar un mensaje más específico
                int unidadesActuales = ConexionMongo.obtenerUnidadesProducto(codigo);
                if (unidadesActuales == -1) {
                    enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                                   String.valueOf(Protocolo.NOT_FOUND), "Producto no encontrado");
                } else {
                    enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                                   String.valueOf(Protocolo.SERVER_ERROR), 
                                   "Stock insuficiente. Stock actual: " + unidadesActuales + ", solicitado: " + cantidad);
                }
            }
        } catch (NumberFormatException e) {
            enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.SERVER_ERROR), "Número de unidades inválido");
        } catch (Exception e) {
            System.err.println("Error al reducir inventario: " + e.getMessage());
            enviarRespuesta(Protocolo.ERROR_REDUCIR_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos: " + e.getMessage());
        }
    }
 
    private void procesarRestablecerInventario(String[] partes, String idMensaje) {
        if (partes.length < 5) {
            enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.SERVER_ERROR), "Parámetros insuficientes: se requiere idFactura, codigoFarmaco y unidades");
            return;
        }

        String idFactura = partes[1];
        String codigoFarmaco = partes[2];
        String unidadesStr = partes[3];

        try {
            int unidades = Integer.parseInt(unidadesStr);
            
            if (unidades <= 0) {
                enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                               String.valueOf(Protocolo.SERVER_ERROR), "Las unidades deben ser mayor a 0");
                return;
            }
            
            // Restaurar las unidades al inventario
            boolean exito = ConexionMongo.restaurarInventarioBorrador(idFactura, codigoFarmaco, unidades);
            
            if (exito) {
                enviarRespuesta(Protocolo.RESTABLECER_INVENTARIO_RESPONSE, idMensaje, 
                               String.valueOf(Protocolo.SUCCESS), "Inventario restaurado exitosamente");
                System.out.println("Inventario restaurado - Factura: " + idFactura + 
                                 ", Producto: " + codigoFarmaco + 
                                 ", Unidades: " + unidades + 
                                 " desde " + clienteInfo);
            } else {
                // Verificar si el producto existe para dar un mensaje más específico
                int unidadesActuales = ConexionMongo.obtenerUnidadesProducto(codigoFarmaco);
                if (unidadesActuales == -1) {
                    enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                                   String.valueOf(Protocolo.NOT_FOUND), "Producto no encontrado: " + codigoFarmaco);
                } else {
                    enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                                   String.valueOf(Protocolo.SERVER_ERROR), "Error al restaurar inventario");
                }
            }
        } catch (NumberFormatException e) {
            enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.SERVER_ERROR), "Número de unidades inválido");
        } catch (Exception e) {
            System.err.println("Error al restaurar inventario: " + e.getMessage());
            enviarRespuesta(Protocolo.ERROR_RESTABLECER_INVENTARIO, idMensaje,
                           String.valueOf(Protocolo.DATABASE_ERROR), "Error de base de datos: " + e.getMessage());
        }
    }
 
    private void enviarRespuesta(int codigoRespuesta, String idMensaje, String estado, String datos) {
        String respuesta = Protocolo.construirMensaje(codigoRespuesta, idMensaje, estado, datos);
        enviarMensaje(respuesta);
    }
 
    private void enviarError(String mensaje, String idMensaje) {
        String respuesta = Protocolo.construirMensaje(Protocolo.SERVER_ERROR, idMensaje, String.valueOf(Protocolo.SERVER_ERROR), mensaje);
        enviarMensaje(respuesta);
    }
 
    public void enviarMensaje(String mensaje) {
        if (conectado && salida != null) {
            salida.println(mensaje);
        }
    }
 
    public void desconectar() {
        if (conectado) {
            conectado = false;
            try {
                if (entrada != null) entrada.close();
                if (salida != null) salida.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar la conexión con " + clienteInfo + ": " + e.getMessage());
            }
        }
    }
 
    public boolean estaConectado() {
        return conectado && !socket.isClosed();
    }
}
 