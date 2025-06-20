package com.kiwi.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}

