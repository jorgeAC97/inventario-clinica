package com.clinica.db;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
 
import org.bson.Document;
 
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
 
public class ConexionMongo {
    // Conexión para empleados (usuarios)
    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_EMPRESA = "Empresa";
    private static final String COLLECTION_USUARIOS = "usuarios";
 
    // Conexión para farmacia
    private static final String DB_INVENTARIO = "Inventario";
    private static final String COLLECTION_FARMACIA = "farmacia";
 
    // Método para validar empleados
    public static boolean validarCredenciales(String usuario, String contrasena) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_EMPRESA);
            MongoCollection<Document> usuarios = database.getCollection(COLLECTION_USUARIOS);
 
            Document query = new Document("usuario", usuario)
                    .append("password", contrasena);
            Document resultado = usuarios.find(query).first();
 
            return resultado != null;
        }
    }
 
    // Método para obtener productos de farmacia
    public static List<Document> obtenerFarmacia() {
        List<Document> lista = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            FindIterable<Document> resultados = farmacia.find()
                .projection(new Document("codigo", 1)
                            .append("nombre", 1)
                            .append("dimension", 1)
                            .append("unidades", 1)
                            .append("ViaAdmin", 1)
                            .append("precio", 1)
                            .append("_id", 0));
 
            for (Document doc : resultados) {
                lista.add(doc);
            }
        }
        return lista;
    }
 
    public static List<Document> buscarFarmaciaPorNombre(String nombre) {
        List<Document> lista = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            FindIterable<Document> resultados = farmacia.find(
                new Document("nombre", new Document("$regex", nombre).append("$options", "i"))
            ).projection(new Document("codigo", 1)
                        .append("nombre", 1)
                        .append("dimension", 1)
                        .append("unidades", 1)
                        .append("ViaAdmin", 1)
                        .append("precio", 1)
                        .append("_id", 0));
 
            for (Document doc : resultados) {
                lista.add(doc);
            }
        }
        return lista;
    }
 
    public static List<Document> obtenerTodosLosProductos() {
        List<Document> lista = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            FindIterable<Document> resultados = farmacia.find()
                .projection(new Document("codigo", 1)
                            .append("nombre", 1)
                            .append("cantidad", 1)
                            .append("precio", 1)
                            .append("_id", 0));
 
            for (Document doc : resultados) {
                lista.add(doc);
            }
        }
        return lista;
    }
 
    public static void eliminarProducto(String codigo) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            Document query = new Document("codigo", codigo);
            farmacia.deleteOne(query);
        }
    }
 
    public static void actualizarUnidadesProducto(String codigo, int nuevasUnidades) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            Document query = new Document("codigo", codigo);
            Document update = new Document("$set", new Document("unidades", nuevasUnidades));
            farmacia.updateOne(query, update);
        }
    }
 
    // Método para obtener medicamentos con stock > 0
    public static List<Document> obtenerMedicamentosConStock() {
        List<Document> lista = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            // Query para obtener medicamentos donde unidades > 0
            Document query = new Document("unidades", new Document("$gt", 0));
            FindIterable<Document> resultados = farmacia.find(query)
                .projection(new Document("codigo", 1)
                            .append("nombre", 1)
                            .append("dimension", 1)
                            .append("unidades", 1)
                            .append("ViaAdmin", 1)
                            .append("precio", 1)
                            .append("laboratorio", 1)
                            .append("_id", 0));
 
            for (Document doc : resultados) {
                lista.add(doc);
            }
        }
        return lista;
    }
 
    // Método para buscar medicamentos por término (nombre, código, laboratorio)
    public static List<Document> buscarMedicamentos(String termino) {
        List<Document> lista = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
 
            // Query para buscar por nombre, código o laboratorio con stock > 0
            Document query = new Document("$and", List.of(
                new Document("unidades", new Document("$gt", 0)),
                new Document("$or", List.of(
                    new Document("nombre", new Document("$regex", termino).append("$options", "i")),
                    new Document("codigo", new Document("$regex", termino).append("$options", "i")),
                    new Document("laboratorio", new Document("$regex", termino).append("$options", "i"))
                ))
            ));
 
            FindIterable<Document> resultados = farmacia.find(query)
                .projection(new Document("codigo", 1)
                            .append("nombre", 1)
                            .append("dimension", 1)
                            .append("unidades", 1)
                            .append("ViaAdmin", 1)
                            .append("precio", 1)
                            .append("laboratorio", 1)
                            .append("_id", 0));
 
            for (Document doc : resultados) {
                lista.add(doc);
            }
        }
        return lista;
    }
 
    // Método para obtener las unidades actuales de un producto específico
    public static int obtenerUnidadesProducto(String codigo) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
            
            Document query = new Document("codigo", codigo);
            Document resultado = farmacia.find(query).first();
            
            if (resultado != null) {
                return resultado.getInteger("unidades", 0);
            }
            return -1; // Producto no encontrado
        }
    }
    
    // Método para reducir unidades de un producto de forma segura
    public static boolean reducirUnidadesProducto(String codigo, int unidadesAReducir) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
            
            // Primero obtener las unidades actuales
            Document query = new Document("codigo", codigo);
            Document producto = farmacia.find(query).first();
            
            if (producto == null) {
                return false; // Producto no encontrado
            }
            
            int unidadesActuales = producto.getInteger("unidades", 0);
            int nuevasUnidades = unidadesActuales - unidadesAReducir;
            
            // Verificar que no baje de 0
            if (nuevasUnidades < 0) {
                return false; // No hay suficiente stock
            }
            
            // Actualizar las unidades
            Document update = new Document("$set", new Document("unidades", nuevasUnidades));
            farmacia.updateOne(query, update);
            
            return true;
        }
    }
    
    // Método para restaurar unidades al inventario cuando se cancela una factura en borrador
    public static boolean restaurarInventarioBorrador(String idFactura, String codigoFarmaco, int unidadesARestaurar) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
            
            // Verificar que el producto existe
            Document query = new Document("codigo", codigoFarmaco);
            Document producto = farmacia.find(query).first();
            
            if (producto == null) {
                System.err.println("Producto no encontrado al restaurar inventario: " + codigoFarmaco);
                return false; // Producto no encontrado
            }
            
            // Validar que las unidades a restaurar son positivas
            if (unidadesARestaurar <= 0) {
                System.err.println("Las unidades a restaurar deben ser positivas: " + unidadesARestaurar);
                return false;
            }
            
            // Obtener unidades actuales y sumar las unidades restauradas
            int unidadesActuales = producto.getInteger("unidades", 0);
            int nuevasUnidades = unidadesActuales + unidadesARestaurar;
            
            // Actualizar las unidades
            Document update = new Document("$set", new Document("unidades", nuevasUnidades));
            farmacia.updateOne(query, update);
            
            System.out.println("Inventario restaurado - Factura: " + idFactura + 
                             ", Producto: " + codigoFarmaco + 
                             ", Unidades restauradas: " + unidadesARestaurar +
                             ", Stock anterior: " + unidadesActuales + 
                             ", Stock actual: " + nuevasUnidades);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error al restaurar inventario: " + e.getMessage());
            return false;
        }
    }
 
    // Método para verificar si existe la colección farmacia
    public static boolean existeColeccionFarmacia() {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            
            // Verificar si la colección existe y tiene datos
            boolean existe = false;
            for (String nombreColeccion : database.listCollectionNames()) {
                if (COLLECTION_FARMACIA.equals(nombreColeccion)) {
                    // Verificar que tenga al menos un documento
                    MongoCollection<Document> farmacia = database.getCollection(COLLECTION_FARMACIA);
                    existe = farmacia.countDocuments() > 0;
                    break;
                }
            }
            
            return existe;
        } catch (Exception e) {
            System.err.println("Error al verificar la colección farmacia: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        String uri = "mongodb://localhost:27017"; // Conexión al contenedor expuesto en tu máquina
 
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Cambia "Clinica" por la base de datos que quieras usar
            MongoDatabase database = mongoClient.getDatabase("Clinica");
            System.out.println("Conexión exitosa a la base de datos Clinica");
        } catch (Exception e) {
            System.out.println("Error al conectar a MongoDB: " + e.getMessage());
        }
 
        String contenido = new String(Files.readAllBytes(Paths.get("src/main/java/com/clinica/colecionJSON/Inventario.farmacia.json")));
    }
}
 