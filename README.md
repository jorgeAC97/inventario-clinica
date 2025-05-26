# Sistema de Inventario Clínica

Este es un sistema de inventario para clínica desarrollado en JavaFX con arquitectura cliente-servidor.

## Componentes del Sistema

### 1. Servidor
- **Ubicación**: `src/main/java/com/clinica/servidor/Servidor.java`
- **Puerto**: 50005
- **Función**: Maneja las conexiones de múltiples clientes y procesa las peticiones

### 2. Cliente (Aplicación JavaFX)
- **Ubicación**: `src/main/java/com/clinica/App.java`
- **Función**: Interfaz gráfica para gestionar el inventario de farmacia

### 3. Base de Datos
- **Tipo**: MongoDB
- **Puerto**: 27017
- **Bases de datos**: 
  - `Empresa` (usuarios)
  - `Inventario` (farmacia)

## Cómo Ejecutar el Sistema

### Opción 1: Con Servidor (Recomendado para múltiples usuarios)

1. **Iniciar el servidor** (en una terminal):
   ```bash
   # Usando el script batch (Windows)
   iniciar-servidor.bat
   
   # O manualmente
   mvn compile
   mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"
   ```

2. **Iniciar la aplicación cliente** (en otra terminal):
   ```bash
   mvn clean javafx:run
   ```

### Opción 2: Solo Cliente (Modo local)

Si no necesitas funcionalidades de red, puedes ejecutar solo el cliente:

```bash
mvn clean javafx:run
```

La aplicación detectará automáticamente que no hay servidor y funcionará en modo local.

## Estados de Conexión

- **Con servidor**: Permite funcionalidades de red y múltiples usuarios
- **Sin servidor**: Funciona en modo local, acceso directo a MongoDB

## Requisitos

- Java 11 o superior
- Maven
- MongoDB ejecutándose en localhost:27017
- JavaFX 13

## Estructura del Proyecto

```
src/main/java/com/clinica/
├── App.java                    # Aplicación principal JavaFX
├── ConstantesVentana.java      # Constantes de la interfaz
├── Controladores/              # Controladores JavaFX
├── servidor/                   # Componentes del servidor
│   ├── Servidor.java          # Servidor principal
│   └── GestionadorCliente.java # Manejo de clientes individuales
├── protocolo/                  # Protocolo de comunicación
├── db/                        # Conexión a base de datos
└── data/                      # Modelos de datos
```

## Notas Importantes

- El servidor debe iniciarse **antes** que los clientes si quieres usar funcionalidades de red
- La aplicación puede funcionar sin servidor para uso local
- Asegúrate de que MongoDB esté ejecutándose antes de iniciar cualquier componente 