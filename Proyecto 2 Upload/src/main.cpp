
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>

const char* ssid = "SSID";
const char* password = "PASSWORD";

const char* mqtt_server = "MQTT SERVER";
const int mqtt_port = 8883;
const char* mqtt_user = "Device01";
const char* mqtt_password = "Device01";
const char* topic_publish = "Sensor";


WiFiClientSecure esp_client;
PubSubClient mqttClient(esp_client);


const int analogPin = A0;
const int threshold = 330;


void setup_wifi() {
  delay(10);
  Serial.println("");
  Serial.print("Conectando a ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("");
  Serial.println("WiFi conectado");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}


void reconnect() {
  while (!mqttClient.connected()) {
    Serial.print("Intentando conexion MQTT...");
    if (mqttClient.connect("ESP8266Client", mqtt_user, mqtt_password)) {
      Serial.println("conectado");
    } else {
      Serial.print("fallo, error=");
      Serial.print(mqttClient.state());
      Serial.println(" intentando en 5 segundos");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  setup_wifi();


  esp_client.setInsecure();

  mqttClient.setServer(mqtt_server, mqtt_port);
  pinMode(analogPin, INPUT);
}

void loop() {
  if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop();

  int alcoholLevel = analogRead(analogPin); 
  Serial.print("Nivel de alcohol: ");
  Serial.println(alcoholLevel);

  String alcoholStr = String(alcoholLevel);
  mqttClient.publish(topic_publish, alcoholStr.c_str());
  

  if (alcoholLevel > threshold) {
    Serial.println("¡Alerta! Nivel alto de alcohol detectado.");
  } else {
    Serial.println("Nivel de alcohol en rango seguro.");
  }

  delay(1000);
}