
# Informe de Evaluación Lighthouse — Semillitas

## 1. Objetivo

Evaluar la accesibilidad, el rendimiento y las buenas
prácticas de la interfaz de inicio de sesión del sistema
Semillitas mediante Google Lighthouse, como evidencia
complementaria de evaluación de usabilidad para el APF3.

## 2. Herramientas y entorno

- Herramienta: Google Lighthouse
- Navegador: Google Chrome
- Frontend: Vue 3 y Vite
- Entorno: compilación de producción local
- URL evaluada: http://localhost:4173/login
- Modalidad: Desktop
- Fecha: 05/11/2026

## 3. Resultados obtenidos

| Categoría | Puntuación |
|---|---:|
| Rendimiento | 99/100 |
| Accesibilidad | 81/100 |
| Buenas prácticas | 100/100 |
| SEO | 82/100 |

## 4. Métricas de rendimiento

| Métrica | Resultado |
|---|---:|
| First Contentful Paint (FCP) | 0.6 s |
| Largest Contentful Paint (LCP) | 0.8 s |
| Total Blocking Time (TBT) | 0 ms |
| Cumulative Layout Shift (CLS) | 0.003 |
| Speed Index | 0.7 s |

Los resultados evidencian un buen rendimiento de la
interfaz durante la evaluación automatizada local.

## 5. Hallazgos de accesibilidad

### H-L03: Contraste insuficiente

Lighthouse identificó elementos cuyo contraste entre
el texto y el fondo no cumple con los niveles mínimos
evaluados por la herramienta.

**Recomendación:**
Revisar los colores utilizados en los componentes
señalados y aumentar el contraste para mejorar
la legibilidad.

### H-L04: Ausencia del atributo lang

El elemento HTML principal no declara el idioma
de la página.

**Recomendación:**
Agregar el atributo lang="es" al elemento html
principal del documento.

### H-L05: Tamaño o separación insuficiente de controles

Lighthouse identificó elementos táctiles cuyo tamaño
o separación podría dificultar la interacción.

**Recomendación:**
Revisar los controles señalados y ampliar sus áreas
de interacción o la separación entre elementos.

## 6. Hallazgos de SEO

### H-L01: Ausencia de meta description

El documento no contiene una metadescripción
que explique el contenido de la página.

**Recomendación:**
Agregar una etiqueta meta description adecuada
al documento HTML.

### H-L02: Archivo robots.txt no válido

Lighthouse identificó 15 errores relacionados
con la validación de robots.txt.

**Recomendación:**
Revisar la sintaxis y las directivas del archivo
robots.txt para corregir los errores detectados.

## 7. Relación con ISO 25010

La evaluación aporta evidencia técnica relacionada
con la accesibilidad y la capacidad de interacción
del sistema.

Los hallazgos de contraste, idioma y tamaño de
controles permiten identificar oportunidades de
mejora que pueden facilitar el uso de la plataforma.

La evaluación automatizada complementa las pruebas
Selenium, pero no reemplaza las pruebas de usabilidad
con usuarios reales.

## 8. Conclusiones

La interfaz de inicio de sesión de Semillitas
obtuvo resultados favorables en rendimiento
(99/100) y buenas prácticas (100/100).

Sin embargo, la accesibilidad obtuvo 81/100,
identificándose tres oportunidades de mejora
relacionadas con contraste, idioma y controles táctiles.

Asimismo, la evaluación SEO obtuvo 82/100,
presentando observaciones sobre la metadescripción
y el archivo robots.txt.

Los hallazgos permiten establecer recomendaciones
para futuras mejoras del frontend sin modificar
la funcionalidad actual del sistema.

## 9. Evidencias

- EV-L-01: reporte Lighthouse HTML.
- EV-L-02: reporte Lighthouse JSON.
- EV-L-03: captura de las cuatro puntuaciones.
- EV-L-04: capturas de hallazgos de accesibilidad y SEO.
- EV-L-05: informe de evaluación Lighthouse.
