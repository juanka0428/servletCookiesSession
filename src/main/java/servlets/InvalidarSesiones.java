package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listeners.SesionListener;
import util.Html;

/**
 * InvalidarSesiones: invalida TODAS las sesiones activas del servidor.
 * Validacion: solo lo puede hacer un usuario con sesion y rol "admin".
 * Mapeado en web.xml como /InvalidarSesiones.
 */
public class InvalidarSesiones extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public InvalidarSesiones() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 1. Validar permisos
		HttpSession session = request.getSession(false);
		String rol = (session != null) ? (String) session.getAttribute("rol") : null;

		if (!"admin".equals(rol)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
			out.println(Html.inicio("Acceso denegado"));
			out.println("<h2>Acceso denegado</h2>");
			out.println("<p class='error'>Solo un usuario con sesión activa y rol <b>admin</b> "
					+ "puede invalidar todas las sesiones.</p>");
			out.println(Html.fin());
			out.close();
			return;
		}

		// 2. Invalidar todas
		int antes = SesionListener.total();
		int invalidadas = SesionListener.invalidarTodas();

		out.println(Html.inicio("Sesiones invalidadas"));
		out.println("<h2>Todas las sesiones fueron invalidadas</h2>");
		out.println("<p class='ok'>Sesiones activas antes: <b>" + antes + "</b><br>"
				+ "Sesiones invalidadas: <b>" + invalidadas + "</b> (incluida la tuya)<br>"
				+ "Sesiones activas ahora: <b>" + SesionListener.total() + "</b></p>");
		out.println("<a href='SessionServlet2'><button>Comprobar en Servlet 2</button></a>");
		out.println(Html.fin());
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Accion peligrosa: solo por POST (boton), nunca por un enlace
		response.sendRedirect("SessionServlet2");
	}
}
