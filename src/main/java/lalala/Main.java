package lalala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fastcgi.FCGIInterface;

import static lalala.Points.calculate;

public class Main {

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            try {
                // Читаем тело POST-запроса полностью
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                StringBuilder bodyBuilder = new StringBuilder();
                String line;
                while (reader.ready() && (line = reader.readLine()) != null) {
                    bodyBuilder.append(line);
                }
                String body = bodyBuilder.toString();

                if (body.isBlank()) {
                    sendError("Пустое тело запроса");
                    continue;
                }

                Map<String, String> params = parseQueryString(body);

                if (!params.containsKey("x") || !params.containsKey("y") || !params.containsKey("r")) {
                    sendError("Отсутствуют параметры x, y или r");
                    continue;
                }

                float x = Float.parseFloat(params.get("x"));
                float y = Float.parseFloat(params.get("y"));
                float r = Float.parseFloat(params.get("r"));

                long startNs = System.nanoTime();
                boolean result = calculate(x, y, r);
                long durationNs = System.nanoTime() - startNs;

                sendMessage(result, x, y, r, durationNs);

            } catch (NumberFormatException e) {
                sendError("Ошибка формата числа: " + e.getMessage());
            } catch (IOException e) {
                sendError("Ошибка чтения тела запроса: " + e.toString());
            }
        }
    }

    private static void sendError(String msg) {
        System.out.println("Content-Type: application/json; charset=UTF-8\n");
        System.out.println("{\"reason\":\"" + escape(msg) + "\"}");
        System.out.flush();
    }

    private static void sendMessage(boolean result, float x, float y, float r, long durationNs) {
        String now = Instant.now().toString();
        System.out.println("Content-Type: application/json; charset=UTF-8\n");
        System.out.println("{");
        System.out.println("  \"x\": " + x + ",");
        System.out.println("  \"y\": " + y + ",");
        System.out.println("  \"r\": " + r + ",");
        System.out.println("  \"result\": " + result + ",");
        System.out.println("  \"now\": \"" + now + "\",");
        System.out.println("  \"duration_ns\": " + durationNs);
        System.out.println("}");
        System.out.flush();
    }

    private static String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    // Простейший парсер application/x-www-form-urlencoded
    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
