# Proyecto IoT: Alcoholímetro con MQTT y Notificaciones en Telegram

Este proyecto implementa un sistema IoT que monitorea los niveles de alcohol en tiempo real utilizando un sensor MQ-3 y envía alertas a través de Telegram cuando se supera un umbral predefinido. El sistema utiliza el protocolo MQTT para la comunicación entre los dispositivos y una aplicación Java para la gestión de notificaciones.

## Tabla de Contenidos
- [Descripción del Proyecto](#descripción-del-proyecto)
- [Componentes](#componentes)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso](#uso)
- [Autores](#autores)

## Descripción del Proyecto

El objetivo de este proyecto es crear un prototipo de alcoholímetro que permita monitorear los niveles de alcohol en tiempo real y enviar alertas cuando se supera un umbral recomendado. El sistema está diseñado para prevenir accidentes viales y muertes relacionadas con el consumo de alcohol en Colombia.

El proyecto utiliza:
- Un sensor MQ-3 para detectar los niveles de alcohol.
- Dos módulos NodeMCU ESP8266 para la comunicación y control.
- El protocolo MQTT para la transmisión de datos.
- Una aplicación Java para gestionar las notificaciones y el control del sistema.
- Telegram para enviar alertas a los contactos de emergencia.

## Componentes

- **Sensor MQ-3**: Sensor de alcohol y etanol.
- **NodeMCU ESP8266**: Módulo Wi-Fi para la comunicación IoT.
- **Buzzer**: Para emitir alertas sonoras.
- **Protoboard**: Para realizar las conexiones.
- **Cables Jumper**: Para conectar los componentes.
- **Aplicación Java**: Para gestionar las notificaciones y el control del sistema.

## Instalación y Configuración

### Requisitos Previos
- Circuito montado.
- Arduino IDE o PlatformIO instalado.
- Cuenta en un broker MQTT (por ejemplo, Mosquitto).
- Bot de Telegram configurado para enviar notificaciones.

### Pasos para la Configuración

1. **Configuración del ESP8266**:
   - Conecta el sensor MQ-3 al primer ESP8266.
   - Conecta el buzzer al segundo ESP8266.
   - Carga el código en los ESP8266 utilizando el Arduino IDE.

2. **Configuración del Broker MQTT**:
   - Configura un broker MQTT (por ejemplo, Mosquitto) y asegúrate de que los ESP8266 y la aplicación Java puedan conectarse a él.

3. **Configuración de la Aplicación Java**:
   - Clona el repositorio de la aplicación Java.
   - Configura las credenciales del broker MQTT y del bot de Telegram en el código.
   - Ejecuta la aplicación Java.

4. **Configuración de Telegram**:
   - Crea un bot en Telegram y obtén el token.
   - Configura los contactos de emergencia en la aplicación Java.

## Uso

1. **Encender el sistema**: Conecta los ESP8266 a la red Wi-Fi y enciende la aplicación Java.
2. **Monitoreo**: El sensor MQ-3 comenzará a medir los niveles de alcohol y enviará los datos al broker MQTT.
3. **Alertas**: Si el nivel de alcohol supera el umbral predefinido, el buzzer se activará y se enviará una notificación a los contactos de emergencia en Telegram.


## Autores

- **Sofia Guerra Jimenez**
- **Alexander Aponte Largacha**
- **Viviana Gomez Leon**

---

Este proyecto fue desarrollado como parte del curso de Comunicaciones y Redes de la carrera de Ingeniería de Sistemas en la Pontificia Universidad Javeriana, Bogotá, Colombia.
