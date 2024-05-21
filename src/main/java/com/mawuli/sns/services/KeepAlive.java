package com.mawuli.sns.services;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KeepAlive {

    private static final String APP_URL = "https://courserec-flu8.onrender.com";
    private static final int KEEP_ALIVE_INTERVAL = 14 * 60; // in seconds

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(KeepAlive::sendKeepAliveRequest, 0, KEEP_ALIVE_INTERVAL, TimeUnit.SECONDS);
    }

    private static void sendKeepAliveRequest() {
        try {
            URL url = new URL(APP_URL + "/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                log.error("Keep-alive request sent successfully");
            } else {
                log.error("Failed to send keep-alive request. Response code: {}", responseCode);
            }
        } catch (Exception e) {
            log.error("Failed to send keep-alive request. Error: {}", e.getMessage());
        }
    }
}