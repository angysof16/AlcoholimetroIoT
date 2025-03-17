#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>


const char* ssid = "SSID";
const char* password = "PASSWORD";

const char *mqtt_server = "MQTTSERVER";
const int mqtt_port = 8883;
const char *mqtt_user = "Device02";
const char *mqtt_password = "Device02";
const char *topic_subscribe = "Buzzer";

WiFiClientSecure esp_client;
PubSubClient mqttClient(esp_client);

const int buzzerPin = D1;
bool buzzerState = false;

void setup_wifi() {
  delay(10);
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

void callback(char *topic, byte *payload, unsigned int length) {
  String message = "";
  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

  if (String(topic) == "Buzzer") {
    if (message == "ACTIVATE") {
      buzzerState = true;
    } else if (message == "DEACTIVATE") {
      buzzerState = false;
      digitalWrite(buzzerPin, LOW);
    }
  }
}

void reconnect() {
  while (!mqttClient.connected()) {
    Serial.print("Intentando conexión MQTT...");
    if (mqttClient.connect("ESP8266Client_Receiver", mqtt_user, mqtt_password)) {
      Serial.println("conectado");
      mqttClient.subscribe(topic_subscribe);
    } else {
      Serial.print("falló, rc=");
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
  mqttClient.setCallback(callback);

  pinMode(buzzerPin, OUTPUT);
}

void loop() {
  if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop();

  if (buzzerState) {
    digitalWrite(buzzerPin, HIGH);
  } else {
    digitalWrite(buzzerPin, LOW); 
  }
}
