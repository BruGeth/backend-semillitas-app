package com.semillitas.backend.usabilidad;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private final String frontendUrl = System.getenv().getOrDefault(
            "SEMILLITAS_FRONTEND_URL",
            "http://localhost:5173/login");

    @BeforeEach
    void iniciar() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(frontendUrl);
    }

    @AfterEach
    void cerrar() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void captura(String nombre) throws Exception {
        Path carpeta = Path.of("target", "selenium-evidencias");
        Files.createDirectories(carpeta);

        Path origen = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE).toPath();

        Files.copy(
                origen,
                carpeta.resolve(nombre + ".png"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    @DisplayName("CP-S01: El formulario de login se visualiza")
    void visualizarLogin() throws Exception {
        WebElement password = wait.until(d -> d.findElement(By.cssSelector("input[type='password']")));

        assertTrue(password.isDisplayed());
        captura("EV-S-01-login");
    }

    @Test
    @DisplayName("CP-S02: El campo de contraseña permite ingresar datos")
    void ingresarContrasena() throws Exception {

        WebElement password = wait.until(d -> d.findElement(By.cssSelector("input[type='password']")));

        // Pausa para observar el formulario
        Thread.sleep(1500);

        // Escribir contraseña de prueba
        password.sendKeys("PruebaSelenium123");

        assertEquals(
                "PruebaSelenium123",
                password.getDomProperty("value"));

        // Mantener el campo escrito para observarlo
        Thread.sleep(3000);

        captura("EV-S-02-contrasena-ingresada");

        // Limpiar el campo
        password.clear();

        assertEquals("", password.getDomProperty("value"));

        Thread.sleep(1500);

        captura("EV-S-02-interaccion");
    }

}
