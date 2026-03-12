package PracticaServiciosJSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PracticaServiciosJSON {

    public static void main(String[] args) {
        try {
            // 1) Recuperar JSON
            String json = fetchJsonFromWeb();
            System.out.println("📥 JSON descargado:");
            System.out.println(json);

            // 2) Encriptar (Base64)
            String encrypted = encryptData(json);
            System.out.println("\n🔐 ENCRIPTADO (Base64):");
            System.out.println(encrypted);

            // 3) Desencriptar
            String decrypted = decryptData(encrypted);
            System.out.println("\n📱 DESENCRIPTADO (JSON original):");
            System.out.println(decrypted);

            // 4) Mostrar datos extraídos
            showParsedData(decrypted);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    // Obtener JSON desde la web (compatible Java 8)
    static String fetchJsonFromWeb() throws Exception {
        URL url = new URL("https://jsonplaceholder.typicode.com/users/1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    // Encriptar con Base64
    static String encryptData(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    // Desencriptar Base64
    static String decryptData(String encrypted) {
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    // Parseo manual (sin librerías)
    static void showParsedData(String json) {
        System.out.println("\n👤 DATOS EXTRAÍDOS:");

        String name = extractValue(json, "name");
        String email = extractValue(json, "email");
        String city = extractValue(json, "city");

        System.out.println("Nombre: " + name);
        System.out.println("Email : " + email);
        System.out.println("Ciudad: " + city);
    }

    // Extraer valores tipo "key": "value" (admite espacios)
    static String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return "(no encontrado)";

        start += pattern.length();

        // Saltar espacios
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        // Debe empezar por comillas
        if (start >= json.length() || json.charAt(start) != '"') {
            return "(no encontrado)";
        }
        start++; // saltar comilla inicial

        int end = json.indexOf("\"", start);
        if (end == -1) return "(no encontrado)";

        return json.substring(start, end);
    }
}
