package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.CookieUtil;
import util.Html;

/**
 * CookieLogout: borra TODAS las cookies de la aplicacion (MaxAge = 0).
 * Mapeado en web.xml como /CookieLogout.
 */
public class CookieLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CookieLogout() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		int borradas = 0;
		for (String nombre : CookieUtil.TODAS) {
			if (CookieUtil.leer(request, nombre) != null) {
				borradas++;
			}
			CookieUtil.borrar(request, response, nombre);
		}

		out.println(Html.inicio("Cookies borradas"));
		out.println("<h2>Cookies eliminadas</h2>");
		out.println("<p class='ok'>Se borraron <b>" + borradas + "</b> cookies.</p>");
		out.println("<a href='CookieServlet2'><button>Comprobar en Servlet 2</button></a>");
		out.println(Html.fin());
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Borrar es una accion: solo por POST (boton)
		response.sendRedirect("CookieServlet2");
	}
}
