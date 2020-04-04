package com.anti_captcha.Helper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

public class StringHelper {
    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCase = new StringBuilder("");

        Arrays.stream(parts).forEach(part -> {
            camelCase.append(part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase());
        });


        var camelCaseString = camelCase.toString();

        return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
    }

    public static String imageFileToBase64String(String path) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            return null;
        }
    }
}
