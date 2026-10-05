package util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Metodos para crear, leer y borrar cookies.
 */
public final class CookieUtil {

	/** Cookies que crea la aplicacion (5 del formulario + 2 automaticas = 7). */
	public static final String[] DEL_FORMULARIO = { "nombre", "email", "edad", "ciudad", "lenguaje" };
	public static final String VISITAS = "visitas";
	public static final String ULTIMA_VISITA = "ultimaVisita";
	public static final String[] TODAS = { "nombre", "email", "edad", "ciudad", "lenguaje", VISITAS, ULTIMA_VISITA };

	/** Duracion de las cookies: 1 hora (en segundos). */
	public static final int DURACION = 60 * 60;

	private CookieUtil() {
	}

	/** Crea una cookie y la agrega a la respuesta. */
	public static void crear(HttpServletRequest req, HttpServletResponse resp, String nombre, String valor) {
		// Una cookie no admite espacios, tildes ni comas: se codifica el valor
		Cookie c = new Cookie(nombre, URLEncoder.encode(valor, StandardCharsets.UTF_8));
		c.setMaxAge(DURACION);                 // tiempo de vida
		c.setPath(ruta(req));                  // valida solo para esta aplicacion
		c.setHttpOnly(true);                   // JavaScript no puede leerla
		resp.addCookie(c);
	}

	/** Devuelve el valor de la cookie, o null si el navegador no la envio. */
	public static String leer(HttpServletRequest req, String nombre) {
		Cookie[] cookies = req.getCookies();
		if (cookies == null) return null;
		for (Cookie c : cookies) {
			if (c.getName().equals(nombre)) {
				try {
					return URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
				} catch (IllegalArgumentException e) {
					return null; // valor alterado / mal codificado
				}
			}
		}
		return null;
	}

	/** Borra una cookie enviando otra con el mismo nombre y ruta y MaxAge = 0. */
	public static void borrar(HttpServletRequest req, HttpServletResponse resp, String nombre) {
		Cookie c = new Cookie(nombre, "");
		c.setMaxAge(0);
		c.setPath(ruta(req));
		resp.addCookie(c);
	}

	private static String ruta(HttpServletRequest req) {
		String ctx = req.getContextPath();
		return ctx.isEmpty() ? "/" : ctx;
	}
}
