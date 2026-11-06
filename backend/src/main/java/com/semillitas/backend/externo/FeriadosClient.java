package com.semillitas.backend.externo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class FeriadosClient {

    private final HttpClient http;
    private final String baseUrl;
    private final Duration timeout;

    // Constructor normal
    public FeriadosClient(String baseUrl) {
        this(baseUrl, Duration.ofSeconds(20));
    }

    // Constructor para configurar timeout en pruebas
    public FeriadosClient(
            String baseUrl,
            Duration timeout
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                "La URL del servicio es obligatoria"
            );
        }

        if (timeout == null ||
                timeout.isZero() ||
                timeout.isNegative()) {
            throw new IllegalArgumentException(
                "El timeout debe ser positivo"
            );
        }

        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.timeout = timeout;

        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public String consultar(int anio)
            throws IOException, InterruptedException {

        if (anio < 2000 || anio > 2100) {
            throw new IllegalArgumentException(
                "Anio fuera de rango permitido"
            );
        }

        URI uri = URI.create(
            baseUrl + "/api/v3/PublicHolidays/" + anio + "/PE"
        );

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = http.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                "Servicio externo: HTTP " + response.statusCode()
            );
        }

        String json = response.body().trim();

        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IOException(
                "Formato de respuesta no esperado"
            );
        }

        return json;
    }
}
