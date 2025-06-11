# 🏥 Sistema de Inventario Clínica

Sistema de gestión de inventario farmacéutico con arquitectura cliente-servidor.

## 🚀 Inicio Rápido

### Requisitos
- **Java 11+** instalado
- **Maven** instalado y en PATH
- **MongoDB** ejecutándose (puerto 27017)

### Ejecutar la Aplicación

#### Opción 1: Solo Aplicación (Modo Local)
```bash
aplicacion.bat
```
- Funciona sin servidor
- Conecta directamente a MongoDB local

#### Opción 2: Con Servidor (Recomendado)
```bash
# Terminal 1: Iniciar servidor
servidor.bat

# Terminal 2: Iniciar aplicación
aplicacion.bat
```
- Soporte para múltiples clientes
- Comunicación en red
- Puerto servidor: 50005

## 📁 Estructura del Proyecto

```
📦 inventario-clinica
├── 🎯 servidor.bat          # Inicia el servidor
├── 🖥️ aplicacion.bat        # Inicia la aplicación JavaFX
├── 📊 src/main/java/
│   ├── com/clinica/
│   │   ├── App.java          # Aplicación principal
│   │   ├── servidor/         # Código del servidor
│   │   ├── cliente/          # Cliente de comunicación
│   │   ├── db/              # Conexión MongoDB
│   │   └── Controladores/   # Controladores JavaFX
└── 📋 README.md
```

## 🔧 Características

- ✅ **Auto-importación de datos**: La app importa datos automáticamente si no existen
- ✅ **Modo híbrido**: Funciona con o sin servidor
- ✅ **Interfaz moderna**: JavaFX con controles intuitivos
- ✅ **Base de datos**: MongoDB para persistencia
- ✅ **Comunicación**: Protocolo TCP personalizado

## 📝 Uso de la Aplicación

### 1. Inicio de Sesión
- **Usuario**: Configurable en MongoDB (`Empresa.usuarios`)
- **Contraseña**: Encriptada en base de datos

### 2. Gestión de Inventario
- **Buscar medicamentos**
- **Actualizar cantidades**
- **Gestionar stock**
- **Reportes en tiempo real**

### 3. Modos de Operación
- **🌐 Servidor**: Múltiples usuarios simultáneos
- **💻 Local**: Un usuario, acceso directo a BD

## 🛠️ Configuración

### Base de Datos
```javascript
// MongoDB - Base de datos requeridas:
- Empresa.usuarios     // Usuarios del sistema
- Inventario.farmacia  // Productos farmacéuticos
```

### Puertos
- **MongoDB**: 27017 (predeterminado)
- **Servidor TCP**: 50005

## 🐛 Solución de Problemas

### Error de Compilación
```bash
mvn clean compile
```

### MongoDB no conecta
- Verificar que MongoDB esté ejecutándose
- Puerto 27017 disponible

### JavaFX no inicia
- Verificar Java 11+ instalado
- Revisar variables de entorno

### Servidor no responde
- Verificar puerto 50005 libre
- Revisar firewall

## 🎯 Comandos Útiles

```bash
# Limpiar y compilar
mvn clean compile

# Solo servidor
mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"

# Solo aplicación
mvn javafx:run

# Detener procesos Java
taskkill /F /IM java.exe
```

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs en la consola
2. Verifica que MongoDB esté ejecutándose
3. Asegúrate de tener Java 11+ y Maven instalados
4. Comprueba que los puertos estén disponibles

---

**¡Listo para usar! 🎉**

*Sistema desarrollado con Java, JavaFX, Maven y MongoDB* 