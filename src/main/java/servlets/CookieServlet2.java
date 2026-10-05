package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.CookieUtil;
import util.Html;

/**
 * CookieServlet2: lee y valida las cookies que envia el navegador,
 * aumenta el contador de visitas y actualiza la ultima visita.
 * Mapeado en web.xml como /CookieServlet2.
 */
public class CookieServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CookieServlet2() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 1. Validar que existan las 5 cookies del formulario
		List<String> faltantes = new ArrayList<>();
		for (String nombre : CookieUtil.DEL_FORMULARIO) {
			if (CookieUtil.leer(request, nombre) == null) {
				faltantes.add(nombre);
			}
		}

		if (faltantes.size() == CookieUtil.DEL_FORMULARIO.length) {
			out.println(Html.inicio("Sin cookies"));
			out.println("<h2>No hay cookies</h2>");
			out.println("<p>El navegador no envió ninguna cookie de la aplicación. "
					+ "Llena el formulario de cookies primero.</p>");
			out.println(Html.fin());
			out.close();
			return;
		}

		// 2. Validar y actualizar el contador de visitas
		int visitas;
		try {
			visitas = Integer.parseInt(CookieUtil.leer(request, CookieUtil.VISITAS));
			if (visitas < 0) visitas = 0;
		} catch (NumberFormatException e) {   // cookie ausente o alterada
			visitas = 0;
		}
		visitas++;
		String anterior = CookieUtil.leer(request, CookieUtil.ULTIMA_VISITA);
		String ahora = LocalDateTime.now().format(CookieServlet1.FORMATO);
		CookieUtil.crear(request, response, CookieUtil.VISITAS, String.valueOf(visitas));
		CookieUtil.crear(request, response, CookieUtil.ULTIMA_VISITA, ahora);

		// 3. Mostrar las cookies
		out.println(Html.inicio("Leer cookies"));
		out.println("<h2>Hola " + Html.esc(CookieUtil.leer(request, "nombre")) + "</h2>");
		out.println("<p>Has visitado esta página <b>" + visitas + "</b> vez/veces. "
				+ "Visita anterior: <b>" + Html.esc(anterior == null ? "—" : anterior) + "</b></p>");

		if (!faltantes.isEmpty()) {
			out.println("<p class='error'>Faltan cookies (se borraron o expiraron): "
					+ Html.esc(String.join(", ", faltantes)) + "</p>");
		}

		out.println("<table><tr><th>Cookie</th><th>Valor</th></tr>");
		for (String nombre : CookieUtil.TODAS) {
			String valor;
			if (nombre.equals(CookieUtil.VISITAS)) {
				valor = String.valueOf(visitas);          // valor actualizado
			} else if (nombre.equals(CookieUtil.ULTIMA_VISITA)) {
				valor = ahora;                            // valor actualizado
			} else {
				valor = CookieUtil.leer(request, nombre); // valor recibido
			}
			out.println(CookieServlet1.fila(nombre, valor == null ? "(no existe)" : valor));
		}
		out.println("</table>");

		Cookie[] todas = request.getCookies();
		out.println("<p>Total de cookies que envió el navegador: <b>" + (todas == null ? 0 : todas.length) + "</b></p>");

		out.println("<a href='CookieServlet2'><button>Recargar (sumar visita)</button></a>");
		out.println(Html.boton("CookieLogout", "Borrar todas las cookies"));
		out.println(Html.fin());
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
