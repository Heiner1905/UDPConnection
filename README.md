# Taller UDP Connection – Computación en Internet I

**Universidad Icesi**  
**Curso:** Computación en Internet I  
**Profesor:** Ing. Nicolás Javier Salazar Echeverry  
**Fecha de entrega:** 13 de mayo de 2025

## Descripción del proyecto

Este proyecto consiste en una implementación de comunicación entre múltiples peers utilizando el protocolo UDP en Java. Cada peer está representado por una clase que hace uso de una clase común `UDPConnection` encargada de manejar el envío y la recepción de mensajes.

El sistema permite que los peers se comuniquen entre sí dentro de una misma red local. Además, se desarrolló una interfaz gráfica en JavaFX (BONUS) que simula un pequeño cliente de chat UDP.

## Estructura del repositorio

```plaintext
UDPConnection_WithJavaFX/
├── src/
│ └── main/
│ ├── java/
│ │ └── com.example.javafxudpconnection/
│ │   ├── HelloApplication.java # Clase principal de JavaFX
│ │   ├── HelloController.java # Controlador de interfaz
│ │ └── ui/
│ │   ├── PeerA.java # Peer A
│ │   ├── PeerB.java # Peer B
│ │ └── util/
│ │   └── UDPConnection.java # Clase común para la conexión UDP
│ └── resources/
│ └── message.fxml # Diseño de la interfaz JavaFX
├── pom.xml # Configuración Maven
├── .gitignore
```

## Resumen técnico
**Protocolo usado:** UDP

**Puerto Peer A:** 5000

**Puerto Peer B:** 5001

**Lógica de envío:** Implementada en hilos separados.

**Lógica de recepción:** Persistente mediante un bucle en un hilo.

**Seguridad sugerida:** Cifrado simétrico (AES), HMAC, validación de mensajes.

**Diseño sugerido:** Singleton para manejo de conexión, Observer para mensajes, posibilidad de aplicar MVC con la interfaz JavaFX.

## Autores
David Vergara Laverde – A00402237

Heiner Danit Rincon Carrillo - A00402510
