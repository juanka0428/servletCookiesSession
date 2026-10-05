package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.CookieUtil;
import util.Html;
import util.Validador;

/**
 * CookieServlet1: valida el formulario y crea 7 COOKIES
 * (5 con los datos del formulario + visitas + ultimaVisita).
 * Mapeado en web.xml como /CookieServlet1 (sin @WebServlet).
 */
public class CookieServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public CookieServlet1() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 1. Leer parametros
		String nombre   = Validador.limpiar(request.getParameter("nombre"));
		String email    = Validador.limpiar(request.getParameter("email"));
		String edad     = Validador.limpiar(request.getParameter("edad"));
		String ciudad   = Validador.limpiar(request.getParameter("ciudad"));
		String lenguaje = Validador.limpiar(request.getParameter("lenguaje"));

		// 2. Validar en el servidor
		List<String> errores = new ArrayList<>();
		Validador.agregar(errores, Validador.nombre(nombre));
		Validador.agregar(errores, Validador.email(email));
		Validador.agregar(errores, Validador.edad(edad));
		Validador.agregar(errores, Validador.ciudad(ciudad));
		Validador.agregar(errores, Validador.lenguaje(lenguaje));

		if (!errores.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
			out.println(Html.inicio("Datos inválidos"));
			out.println("<h2>No se crearon las cookies</h2>");
			out.println("<p>Corrige estos errores:</p>");
			out.println(Html.errores(errores));
			out.println("<p><a href='javascript:history.back()'>Volver al formulario</a></p>");
			out.println(Html.fin());
			out.close();
			return;
		}

		// 3. Crear las cookies
		String ahora = LocalDateTime.now().format(FORMATO);
		CookieUtil.crear(request, response, "nombre", nombre);
		CookieUtil.crear(request, response, "email", email);
		CookieUtil.crear(request, response, "edad", edad);
		CookieUtil.crear(request, response, "ciudad", ciudad);
		CookieUtil.crear(request, response, "lenguaje", lenguaje);
		CookieUtil.crear(request, response, CookieUtil.VISITAS, "0");
		CookieUtil.crear(request, response, CookieUtil.ULTIMA_VISITA, ahora);

		// 4. Respuesta
		out.println(Html.inicio("Cookies creadas"));
		out.println("<h2>Bienvenido " + Html.esc(nombre) + "</h2>");
		out.println("<p class='ok'>Se crearon <b>" + CookieUtil.TODAS.length
				+ "</b> cookies en tu navegador (duran " + (CookieUtil.DURACION / 60) + " minutos).</p>");
		out.println("<table><tr><th>Cookie</th><th>Valor</th></tr>");
		out.println(fila("nombre", nombre));
		out.println(fila("email", email));
		out.println(fila("edad", edad));
		out.println(fila("ciudad", ciudad));
		out.println(fila("lenguaje", lenguaje));
		out.println(fila(CookieUtil.VISITAS, "0"));
		out.println(fila(CookieUtil.ULTIMA_VISITA, ahora));
		out.println("</table>");
		out.println(Html.boton("CookieServlet2", "Ir a Servlet 2 (leer cookies)"));
		out.println(Html.fin());
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Solo se permite POST desde el formulario
		response.sendRedirect("index.html");
	}

	static String fila(String nombre, String valor) {
		return "<tr><td>" + Html.esc(nombre) + "</td><td>" + Html.esc(valor) + "</td></tr>";
	}
}
