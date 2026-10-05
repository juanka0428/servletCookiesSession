package listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Listener registrado en web.xml (etiqueta <listener>).
 * Tomcat lo llama cada vez que se crea o se destruye una sesion,
 * asi la aplicacion conoce TODAS las sesiones activas y puede invalidarlas.
 */
public class SesionListener implements HttpSessionListener, HttpSessionIdListener {

	/** Sesiones activas: ID de sesion -> sesion. */
	private static final Map<String, HttpSession> ACTIVAS = new ConcurrentHashMap<>();

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		ACTIVAS.put(se.getSession().getId(), se.getSession());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		// Se llama al invalidar la sesion o cuando expira (session-timeout)
		ACTIVAS.remove(se.getSession().getId());
	}

	@Override
	public void sessionIdChanged(HttpSessionEvent se, String idAnterior) {
		ACTIVAS.remove(idAnterior);
		ACTIVAS.put(se.getSession().getId(), se.getSession());
	}

	/** Copia de la lista de sesiones activas. */
	public static List<HttpSession> activas() {
		return new ArrayList<>(ACTIVAS.values());
	}

	public static int total() {
		return ACTIVAS.size();
	}

	/**
	 * Invalida TODAS las sesiones activas.
	 * @return cuantas sesiones se invalidaron
	 */
	public static int invalidarTodas() {
		int contador = 0;
		for (Map.Entry<String, HttpSession> e : new ArrayList<>(ACTIVAS.entrySet())) { // se recorre una copia
			try {
				e.getValue().invalidate();     // dispara sessionDestroyed()
				contador++;
			} catch (IllegalStateException ex) {
				ACTIVAS.remove(e.getKey());    // ya estaba invalidada
			}
		}
		return contador;
	}
}
