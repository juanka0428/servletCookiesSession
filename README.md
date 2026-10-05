# servletCookiesSession

Aplicación web en **Java (Jakarta Servlets)** que muestra cómo mantener el estado entre peticiones HTTP con **Cookies** y **HttpSession**.
Todos los servlets y el listener se configuran en **`web.xml`** (no se usa `@WebServlet` ni `@WebListener`).

- **IDE:** Eclipse IDE for Enterprise Java and Web Developers
- **Servidor:** Apache Tomcat 10.0
- **Java:** 21 · **Servlet:** Jakarta Servlet 5.0

## Cómo ejecutarlo

1. En Eclipse: *File → Import → General → Existing Projects into Workspace* y seleccionar esta carpeta.
2. Clic derecho en el proyecto → *Run As → Run on Server* → *Tomcat v10.0*.
3. Abrir `http://localhost:8080/servletCookiesSession/`

## Estructura

```
src/main/java/
├── servlets/
│   ├── CookieServlet1.java     Valida el formulario y crea las cookies
│   ├── CookieServlet2.java     Lee y valida las cookies, cuenta visitas
│   ├── CookieLogout.java       Borra todas las cookies
│   ├── SessionServlet1.java    Valida usuario/rol y crea la sesión
│   ├── SessionServlet2.java    Muestra la sesión y TODAS las sesiones activas
│   ├── SessionLogout.java      Invalida solo la sesión actual
│   └── InvalidarSesiones.java  Invalida TODAS las sesiones activas (solo admin)
├── listeners/
│   └── SesionListener.java     Registra las sesiones al crearse/destruirse
└── util/
    ├── Validador.java          Validaciones del lado del servidor
    ├── CookieUtil.java         Crear, leer y borrar cookies
    └── Html.java               Plantilla HTML y escape de caracteres (XSS)
src/main/webapp/
├── index.html                  Formularios con validación HTML5
├── WEB-INF/web.xml             Declaración de servlets, listener y sesión
└── META-INF/context.xml        Desactiva la persistencia de sesiones de Tomcat
```

## Parte 1: Cookies

Los datos se guardan en el **navegador** del cliente. Se crean **7 cookies**:

| Cookie | Origen | Validación |
|---|---|---|
| `nombre` | formulario | obligatorio, solo letras y espacios, 3–30 caracteres |
| `email` | formulario | obligatorio, formato de correo válido, máx. 60 |
| `edad` | formulario | obligatorio, número entero entre 1 y 120 |
| `ciudad` | formulario | obligatorio, solo letras y espacios, 2–40 caracteres |
| `lenguaje` | formulario | obligatorio, una de: Java, Python, JavaScript, C# |
| `visitas` | automática | contador; si fue alterada (no numérica) se reinicia en 0 |
| `ultimaVisita` | automática | fecha y hora de la última visita |

- Las cookies duran 1 hora (`setMaxAge`), son `HttpOnly` y su valor se codifica con `URLEncoder` (las cookies no admiten espacios ni tildes).
- **CookieServlet2** valida que las cookies existan; si no hay, muestra "No hay cookies".
- **CookieLogout** borra las 7 cookies enviándolas con `MaxAge = 0`.

## Parte 2: HttpSession

Los datos se guardan en el **servidor**; el navegador solo recibe el ID de sesión (cookie `JSESSIONID`).

- **SessionServlet1** valida el usuario (letras, números y `_`, 3–20, sin espacios) y el rol (`usuario` o `admin`).
  Si ya existía una sesión la invalida y crea una nueva (evita *session fixation*).
  Guarda en la sesión: `usuario`, `rol`, `horaIngreso` y `visitas`.
- **SessionServlet2** usa `getSession(false)` para no crear sesiones vacías. Muestra los datos de la sesión y la **lista de todas las sesiones activas** del servidor.
- **SessionLogout** invalida solo la sesión actual.
- **InvalidarSesiones** invalida **todas las sesiones activas**:
  - `SesionListener` (declarado en `web.xml` con `<listener>`) implementa `HttpSessionListener` y guarda cada sesión en un mapa al crearse y la quita al destruirse.
  - Solo un usuario con rol **admin** puede hacerlo; si no, responde **403 Acceso denegado**.
  - Solo acepta POST (un botón, no un enlace).
- La sesión expira tras **30 minutos** sin actividad (`<session-timeout>` en `web.xml`).

## Validaciones

Se valida en dos capas:

1. **Navegador:** atributos HTML5 (`required`, `pattern`, `type="email"`, `min`/`max`).
2. **Servidor:** clase `Validador`. Siempre se vuelve a validar porque la validación del navegador se puede saltar. Si hay errores, responde **400** con la lista de errores.

Además, todo dato del usuario se escapa con `Html.esc()` antes de mostrarlo, para evitar inyección de HTML/JavaScript (XSS).

## Configuración en web.xml

```xml
<listener>
  <listener-class>listeners.SesionListener</listener-class>
</listener>

<servlet>
  <servlet-name>InvalidarSesiones</servlet-name>
  <servlet-class>servlets.InvalidarSesiones</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>InvalidarSesiones</servlet-name>
  <url-pattern>/InvalidarSesiones</url-pattern>
</servlet-mapping>

<session-config>
  <session-timeout>30</session-timeout>
</session-config>
```

(Los demás servlets se declaran igual; ver `src/main/webapp/WEB-INF/web.xml`.)
