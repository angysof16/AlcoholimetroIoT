package org.example;

import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;

public class MqttManager {
    private final String brokerUrl;
    private final String username;
    private final String password;
    private MqttClient client;
    private final JTextArea messageArea;

    public MqttManager(String brokerUrl, String username, String password, JTextArea messageArea) {
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
        this.messageArea = messageArea;
    }

    public boolean connect() {
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            client.connect(options);
            messageArea.append("Connected to broker: " + brokerUrl + "\n");


            return true;
        } catch (MqttException ex) {
            JOptionPane.showMessageDialog(null, "Error al conectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
            return true;
        } catch (MqttException ex) {
            JOptionPane.showMessageDialog(null, "Error al desconectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void subscribe(String topic, MqttMessageListener listener) {
        try {
            client.subscribe(topic, (t, msg) -> listener.onMessage(new String(msg.getPayload())));
        } catch (MqttException ex) {
            JOptionPane.showMessageDialog(null, "Error al suscribirse: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException ex) {
            JOptionPane.showMessageDialog(null, "Error al publicar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public interface MqttMessageListener {
        void onMessage(String message);
    }
}
