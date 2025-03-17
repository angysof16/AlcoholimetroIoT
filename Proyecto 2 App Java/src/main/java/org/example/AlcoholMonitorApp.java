package org.example;

import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

public class AlcoholMonitorApp {
    private static final String BROKER_URL = "BROKERURL";
    private static final String TOPIC_RECEIVE = "Sensor";
    private static final String TOPIC_BUZZER = "Buzzer";
    private static final String CLIENT_ID = "JavaClient_" + System.currentTimeMillis();

    private static MqttClient client;
    private static TelegramNotifier telegramNotifier;

    private static boolean buzzerActive = false;
    private static Instant lastTelegramSent = Instant.EPOCH;
    private static final int TELEGRAM_COOLDOWN_SECONDS = 10;

    private static boolean buzzerOverride = false;


    public static void main(String[] args) {
        JFrame frame = new JFrame("Monitor de Alcohol");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton connectButton = new JButton("Conectar");
        JButton disconnectButton = new JButton("Desconectar");
        JButton deactivateBuzzerButton = new JButton("Desactivar Buzzer");
        //JButton sendTelegramButton = new JButton("Enviar a Telegram");
        deactivateBuzzerButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        //sendTelegramButton.setEnabled(false);

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(deactivateBuzzerButton);
        //buttonPanel.add(sendTelegramButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        telegramNotifier = new TelegramNotifier("BOTTOKEN", "CHATID");

        connectButton.addActionListener(e -> {
            try {
                client = new MqttClient(BROKER_URL, CLIENT_ID);

                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setUserName("JavaClient");
                options.setPassword("JavaClient".toCharArray());

                client.connect(options);
                messageArea.append("Conectado al broker MQTT en " + BROKER_URL + "\n");


                client.subscribe(TOPIC_RECEIVE, (topic, msg) -> {
                    String message = new String(msg.getPayload());
                    SwingUtilities.invokeLater(() -> {
                        messageArea.append("Nivel de alcohol recibido: " + message + "\n");
                        int alcoholLevel = Integer.parseInt(message);

                        if (alcoholLevel > 330 && !buzzerActive && !buzzerOverride) {
                            activateBuzzer(messageArea);
                        }

                        if (alcoholLevel <= 330 && buzzerActive) {
                            deactivateBuzzerAutomatically(messageArea);
                        }

                        if (alcoholLevel > 330) {
                            Instant now = Instant.now();
                            if (lastTelegramSent.plusSeconds(TELEGRAM_COOLDOWN_SECONDS).isBefore(now)) {
                                sendAutomaticTelegram("¡Alerta! Nivel de alcohol elevado detectado: " + alcoholLevel, messageArea);
                                lastTelegramSent = now;
                            }
                        }
                    });
                });


                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                deactivateBuzzerButton.setEnabled(true);
                //sendTelegramButton.setEnabled(true);
            } catch (MqttException ex) {
                JOptionPane.showMessageDialog(frame, "Error al conectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });


        disconnectButton.addActionListener(e -> {
            try {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                    messageArea.append("Desconectado del broker MQTT\n");
                    buzzerOverride = false;
                }
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                deactivateBuzzerButton.setEnabled(false);
                //sendTelegramButton.setEnabled(false);
            } catch (MqttException ex) {
                JOptionPane.showMessageDialog(frame, "Error al desconectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });


        deactivateBuzzerButton.addActionListener(e -> {
            try {
                if (client != null && client.isConnected()) {
                    client.publish(TOPIC_BUZZER, new MqttMessage("DEACTIVATE".getBytes()));
                    messageArea.append("Mensaje enviado para desactivar el buzzer.\n");
                    buzzerActive = false;
                    buzzerOverride = true;
                }
            } catch (MqttException ex) {
                JOptionPane.showMessageDialog(frame, "Error al desactivar buzzer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    private static void activateBuzzer(JTextArea messageArea) {
        try {
            if (client != null && client.isConnected() && !buzzerOverride) {
                client.publish(TOPIC_BUZZER, new MqttMessage("ACTIVATE".getBytes()));
                messageArea.append("Buzzer activado automáticamente.\n");
                buzzerActive = true;
            }
        } catch (MqttException ex) {
            messageArea.append("Error al activar buzzer: " + ex.getMessage() + "\n");
        }
    }

    private static void deactivateBuzzerAutomatically(JTextArea messageArea) {
        try {
            if (client != null && client.isConnected()) {
                client.publish(TOPIC_BUZZER, new MqttMessage("DEACTIVATE".getBytes()));
                messageArea.append("Buzzer desactivado automáticamente (nivel de alcohol bajo).\n");
                buzzerActive = false;
                buzzerOverride = false;
            }
        } catch (MqttException ex) {
            messageArea.append("Error al desactivar buzzer automáticamente: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void sendAutomaticTelegram(String message, JTextArea messageArea) {
        try {
            telegramNotifier.sendMessage(message);
            messageArea.append("Mensaje enviado automáticamente a Telegram: " + message + "\n");
        } catch (Exception ex) {
            messageArea.append("Error al enviar mensaje a Telegram: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }
}