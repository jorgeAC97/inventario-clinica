package com.clinica.db;

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

    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017"; // Conexión al contenedor expuesto en tu máquina

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Cambia "Clinica" por la base de datos que quieras usar
            MongoDatabase database = mongoClient.getDatabase("Clinica");
            System.out.println("Conexión exitosa a la base de datos Clinica");
        } catch (Exception e) {
            System.out.println("Error al conectar a MongoDB: " + e.getMessage());
        }
    }
} 