package com.example.dweb_App.utils;


import java.time.Duration;
import java.time.LocalDateTime;


public class TimeUtils {

    public static String formatTimeDifference(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "À l'instant";
        } else if (seconds < 3600) {
            long minutes = duration.toMinutes();
            return "il y a " + minutes + " min";
        } else if (seconds < 86400) {
            long hours = duration.toHours();
            return "il y a " + hours + " h";
        } else {
            long days = duration.toDays();
            return "il y a " + days + " j";
        }
    }
}
