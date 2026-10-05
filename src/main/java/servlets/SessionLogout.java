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
 * SessionLogout: invalida (cierra) SOLO la sesion actual.
 * Mapeado en web.xml como /SessionLogout.
 */
public class SessionLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SessionLogout() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(false);
		out.println(Html.inicio("Cerrar sesión"));
		if (session != null) {
			session.invalidate();   // borra la sesion y todos sus atributos
			out.println("<h2>Sesión cerrada</h2>");
			out.println("<p class='ok'>Tu sesión fue invalidada.</p>");
		} else {
			out.println("<h2>No había sesión</h2>");
			out.println("<p>No tenías una sesión activa.</p>");
		}
		out.println("<p>Sesiones activas en el servidor: <b>" + SesionListener.total() + "</b></p>");
		out.println("<a href='SessionServlet2'><button>Comprobar en Servlet 2</button></a>");
		out.println(Html.fin());
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("SessionServlet2");
	}
}
