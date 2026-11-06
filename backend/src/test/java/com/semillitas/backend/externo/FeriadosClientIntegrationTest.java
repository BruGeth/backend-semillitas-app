package com.semillitas.backend.externo;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class FeriadosClientIntegrationTest {

    private WireMockServer servidor;
    private FeriadosClient cliente;

    private static final String RUTA = "/api/v3/PublicHolidays/2026/PE";

    @BeforeEach
    void preparar() {

        servidor = new WireMockServer(
                com.github.tomakehurst.wiremock.core.WireMockConfiguration.options()
                        .dynamicPort());

        servidor.start();

        // Timeout amplio para las pruebas normales
        cliente = new FeriadosClient(
                servidor.baseUrl(),
                Duration.ofSeconds(20));
    }

    @AfterEach
    void cerrar() {
        if (servidor != null) {
            servidor.stop();
        }
    }

    @Test
    @DisplayName("CP-I01: consulta de feriados 200 OK")
    void consultaExitosa() throws Exception {

        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(
                                okJson(
                                        "[{\"date\":\"2026-01-01\","
                                                + "\"localName\":\"Año Nuevo\","
                                                + "\"countryCode\":\"PE\"}]")));

        String respuesta = cliente.consultar(2026);

        assertNotNull(respuesta);
        assertTrue(respuesta.contains("2026-01-01"));
        assertTrue(respuesta.contains("PE"));
    }

    @Test
    @DisplayName("CP-I02: ruta y metodo GET correctos")
    void metodoYRutaCorrectos() throws Exception {

        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(okJson("[]")));

        String respuesta = cliente.consultar(2026);

        assertEquals("[]", respuesta);

        servidor.verify(
                1,
                getRequestedFor(urlEqualTo(RUTA)));
    }

    @Test
    @DisplayName("CP-I03: error HTTP 404 controlado")
    void error404() {

        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(
                                aResponse().withStatus(404)));

        IOException ex = assertThrows(
                IOException.class,
                () -> cliente.consultar(2026));

        assertTrue(
                ex.getMessage().contains("404"));
    }

    @Test
    @DisplayName("CP-I04: error HTTP 503 controlado")
    void error503() {

        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(
                                aResponse().withStatus(503)));

        IOException ex = assertThrows(
                IOException.class,
                () -> cliente.consultar(2026));

        assertTrue(
                ex.getMessage().contains("503"),
                "Mensaje recibido: " + ex.getMessage());
    }

    @Test
    @DisplayName("CP-I05: timeout del servicio externo")
    void timeout() {

        // Solo esta prueba utiliza timeout corto
        FeriadosClient clienteLento = new FeriadosClient(
                servidor.baseUrl(),
                Duration.ofSeconds(1));

        // WireMock demora 5 segundos en responder
        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withFixedDelay(5000)
                                        .withBody("[]")));

        assertThrows(
                HttpTimeoutException.class,
                () -> clienteLento.consultar(2026));
    }

    @Test
    @DisplayName("CP-I06: respuesta invalida rechazada")
    void respuestaInvalida() {

        servidor.stubFor(
                get(urlEqualTo(RUTA))
                        .willReturn(
                                ok("texto inesperado")));

        assertThrows(
                IOException.class,
                () -> cliente.consultar(2026));
    }

    @Test
    @DisplayName("CP-I07: anio invalido no se envia")
    void anioInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> cliente.consultar(1990));

        servidor.verify(
                0,
                getRequestedFor(urlMatching(".*")));
    }
}
