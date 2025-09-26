package lalala;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static lalala.Request.sendError;

public class Points {
    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    public static boolean validateQueryString(Map<String, String> params) {
        if (!params.containsKey("x") || !params.containsKey("y") || !params.containsKey("r")) {
            sendError("Отсутствуют обязательные параметры (x, y, r)");
            return false;
        }

        try {
            float x = Float.parseFloat(params.get("x"));
            float y = Float.parseFloat(params.get("y"));
            float r = Float.parseFloat(params.get("r"));

            if (x < -5 || x > 3) {
                sendError("x должен быть в диапазоне [-5, 3]");
                return false;
            }
            if (y < -3 || y > 5) {
                sendError("y должен быть в диапазоне [-3, 5]");
                return false;
            }
            if (r <= 0) {
                sendError("r должен быть положительным числом");
                return false;
            }
        } catch (NumberFormatException e) {
            sendError("x, y и r должны быть числами");
            return false;
        }

        return true;
    }

    public static boolean calculate(float x, float y, float r) {
        // четверть круга
        if (x >= 0 && y >= 0 && (x * x + y * y <= r * r)) return true;
        // прямоугольник
        if (x <= 0 && y <= 0 && x >= -r && y >= -r/2) return true;
        // треугольник
        return x <= 0 && y >= 0 && y <= r && x >= -r && y <= x + r;
    }
}
