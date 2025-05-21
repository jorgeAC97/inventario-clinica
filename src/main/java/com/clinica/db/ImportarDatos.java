package com.clinica.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ImportarDatos {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_INVENTARIO = "Inventario";
    private static final String COLLECTION_FARMACIA = "farmacia";

    public static void main(String[] args) throws Exception {
        // Conexión a MongoDB
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_INVENTARIO);
            MongoCollection<Document> coleccion = database.getCollection(COLLECTION_FARMACIA);

            // Leer el archivo JSON
            String rutaArchivo = "src/main/java/com/clinica/colecionJSON/farmacia.json";
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            JsonArray array = JsonParser.parseString(contenido).getAsJsonArray();

            // Insertar cada documento en la colección
            List<Document> documentos = new ArrayList<>();
            for (JsonElement elem : array) {
                documentos.add(Document.parse(elem.toString()));
            }
            coleccion.insertMany(documentos);

            System.out.println("Datos importados correctamente a la base de datos " + DB_INVENTARIO);
            System.out.println("Colección: " + COLLECTION_FARMACIA);
        } catch (Exception e) {
            System.err.println("Error al importar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
