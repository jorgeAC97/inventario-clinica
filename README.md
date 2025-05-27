# Sistema de Inventario Clínica

Este es un sistema de inventario para clínica desarrollado en JavaFX con arquitectura cliente-servidor y protocolo de comunicación personalizado.

## 🏗️ Arquitectura del Sistema

### Componentes Principales

1. **Servidor** (`src/main/java/com/clinica/servidor/`)
   - `Servidor.java`: Servidor principal que escucha en puerto 50005
   - `GestionadorCliente.java`: Maneja cada cliente conectado individualmente

2. **Cliente** (`src/main/java/com/clinica/cliente/`)
   - `ClienteComunicacion.java`: Maneja la comunicación con el servidor usando el protocolo

3. **Protocolo** (`src/main/java/com/clinica/protocolo/`)
   - `Protocolo.java`: Define códigos de operación, respuesta y utilidades de mensaje

4. **Servicios** (`src/main/java/com/clinica/servicios/`)
   - `ServicioInventario.java`: Capa de abstracción que decide usar servidor o base de datos local

5. **Aplicación JavaFX** (`src/main/java/com/clinica/`)
   - `App.java`: Aplicación principal
   - `Controladores/`: Controladores de la interfaz gráfica

## 📡 Protocolo de Comunicación

### Formato de Mensajes
```
CODIGO|PARAMETRO1:PARAMETRO2:...:ID_MENSAJE
```

### Códigos de Operación (Cliente → Servidor)
- `1` - LOGIN_REQUEST
- `2` - BUSCAR_FARMACIA_REQUEST  
- `3` - OBTENER_FARMACIA_REQUEST
- `4` - ACTUALIZAR_UNIDADES_REQUEST

### Códigos de Respuesta (Servidor → Cliente)
- `100` - LOGIN_RESPONSE
- `101` - BUSCAR_FARMACIA_RESPONSE
- `102` - OBTENER_FARMACIA_RESPONSE
- `103` - ACTUALIZAR_UNIDADES_RESPONSE

### Códigos de Estado
- `200` - SUCCESS
- `201` - LOGIN_SUCCESS
- `401` - LOGIN_FAILED
- `500` - SERVER_ERROR
- `501` - DATABASE_ERROR

### Ejemplos de Mensajes
```
# Login
Cliente: 1|usuario:password:123
Servidor: 100|123|201|Login exitoso

# Búsqueda
Cliente: 2|aspirina:456
Servidor: 101|456|200|[{"codigo":"ASP001","nombre":"Aspirina",...}]

# Actualizar unidades
Cliente: 4|ASP001:50:789
Servidor: 103|789|200|Unidades actualizadas
```

## 🚀 Cómo Ejecutar el Sistema

### Opción 1: Con Protocolo Cliente-Servidor

1. **Iniciar el servidor**:
   ```bash
   # Usando script de prueba
   probar-protocolo.bat
   # Opción 2: Iniciar servidor
   
   # O manualmente
   mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"
   ```

2. **Iniciar cliente**:
   ```bash
   mvn clean javafx:run
   ```

### Opción 2: Modo Local (Sin Servidor)

```bash
# No iniciar servidor, solo ejecutar:
mvn clean javafx:run
```

### Opción 3: Scripts de Prueba

- `probar-protocolo.bat`: Prueba específica del protocolo
- `probar-sistema.bat`: Prueba general del sistema
- `iniciar-servidor.bat`: Solo inicia el servidor

## 🔄 Flujo de Datos

### Con Servidor (Modo Protocolo)
```
Cliente → ServicioInventario → ClienteComunicacion → Protocolo → Servidor → GestionadorCliente → ConexionMongo → MongoDB
```

### Sin Servidor (Modo Local)
```
Cliente → ServicioInventario → ConexionMongo → MongoDB
```

## ✅ Funcionalidades Implementadas

### 🔐 Autenticación
- ✅ Login a través del protocolo (servidor)
- ✅ Login directo a MongoDB (local)
- ✅ Validación asíncrona de credenciales

### 🔍 Búsqueda de Productos
- ✅ Búsqueda por nombre via protocolo
- ✅ Búsqueda directa en MongoDB
- ✅ Resultados en tiempo real

### 📦 Gestión de Inventario
- ✅ Actualización de unidades via protocolo
- ✅ Actualización directa en MongoDB
- ✅ Sincronización en tiempo real

### 🌐 Comunicación
- ✅ Protocolo personalizado con códigos de operación
- ✅ Manejo asíncrono de mensajes
- ✅ Gestión de errores y timeouts
- ✅ Fallback automático a modo local

## 🛠️ Mejoras Técnicas

### Protocolo Robusto
- ✅ Identificación única de mensajes
- ✅ Manejo de respuestas asíncronas
- ✅ Códigos de error específicos
- ✅ Serialización JSON para datos complejos

### Arquitectura Limpia
- ✅ Separación de responsabilidades
- ✅ Capa de servicios para abstracción
- ✅ Patrón cliente-servidor bien definido
- ✅ Manejo de concurrencia thread-safe

### Experiencia de Usuario
- ✅ Indicador de modo de operación (SERVIDOR/LOCAL)
- ✅ Feedback visual durante operaciones
- ✅ Manejo elegante de errores
- ✅ Funcionamiento transparente en ambos modos

## 📋 Requisitos

- Java 11 o superior
- Maven
- MongoDB ejecutándose en localhost:27017
- JavaFX 13

## 🗂️ Estructura del Proyecto

```
src/main/java/com/clinica/
├── App.java                    # Aplicación principal JavaFX
├── ConstantesVentana.java      # Constantes de la interfaz
├── Controladores/              # Controladores JavaFX
├── cliente/                    # Cliente de comunicación
│   └── ClienteComunicacion.java
├── servidor/                   # Componentes del servidor
│   ├── Servidor.java          # Servidor principal
│   └── GestionadorCliente.java # Manejo de clientes individuales
├── protocolo/                  # Protocolo de comunicación
│   └── Protocolo.java         # Definición del protocolo
├── servicios/                  # Capa de servicios
│   └── ServicioInventario.java # Abstracción servidor/local
├── db/                        # Conexión a base de datos
└── data/                      # Modelos de datos
```

## 🧪 Cómo Probar el Protocolo

1. **Ejecutar `probar-protocolo.bat`**
2. **Compilar el proyecto** (opción 1)
3. **Iniciar servidor** (opción 2) - dejar abierto
4. **En otra terminal, probar cliente** (opción 3)
5. **Observar logs del protocolo** en ambas consolas

### Qué Observar
- Mensajes del protocolo en formato `CODIGO|PARAMETROS`
- Respuestas del servidor con códigos de estado
- Fallback automático a modo local si no hay servidor
- Indicador de modo en la interfaz

## 🎯 Beneficios de la Implementación

- **Escalabilidad**: Múltiples clientes pueden conectarse al servidor
- **Flexibilidad**: Funciona con o sin servidor
- **Robustez**: Protocolo bien definido con manejo de errores
- **Mantenibilidad**: Arquitectura limpia y separada
- **Transparencia**: El usuario no nota la diferencia entre modos

¡Ahora tienes un sistema completo con protocolo cliente-servidor funcional! 🎉 