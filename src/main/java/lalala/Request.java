package lalala;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Request {
    private static final String RESULT_JSON = """
            {
                "x": %f,
                "y": %f,
                "r": %f,
                "result": %b,
                "duration_ns": %d,
                "now": "%s"
            }
            """;

    private static final String ERROR_JSON = """
            {
                "now": "%s",
                "reason": "%s"
            }
            """;

    public static void SendMessage(Instant startTime, Instant endTime, boolean result,
                                   float x, float y, float r) {
        String json = String.format(
                RESULT_JSON,
                x, y, r,
                result,
                ChronoUnit.NANOS.between(startTime, endTime),
                LocalDateTime.now()
        );

        String response = String.format("""
                        Status: 200 OK
                        Content-Type: application/json

                        %s
                        """, json);

        System.out.println(response);
    }

    public static void sendError(String reason) {
        String json = String.format(ERROR_JSON, LocalDateTime.now(), reason);
        String response = String.format("""
                Status: 400 Bad Request
                Content-Type: application/json

                %s
                """, json);
        System.out.println(response);
    }
}
