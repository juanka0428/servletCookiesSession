package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listeners.SesionListener;
import util.Html;
import util.Validador;

/**
 * SessionServlet1: valida usuario y rol y crea una SESION nueva (datos en el servidor).
 * Mapeado en web.xml como /SessionServlet1.
 */
public class SessionServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SessionServlet1() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String usuario = Validador.limpiar(request.getParameter("usuario"));
		String rol     = Validador.limpiar(request.getParameter("rol"));

		// 1. Validar en el servidor
		List<String> errores = new ArrayList<>();
		Validador.agregar(errores, Validador.usuario(usuario));
		Validador.agregar(errores, Validador.rol(rol));

		if (!errores.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
			out.println(Html.inicio("Datos inválidos"));
			out.println("<h2>No se creó la sesión</h2>");
			out.println(Html.errores(errores));
			out.println("<p><a href='javascript:history.back()'>Volver al formulario</a></p>");
			out.println(Html.fin());
			out.close();
			return;
		}

		// 2. Si ya habia una sesion se invalida y se crea una nueva
		//    (evita reutilizar el mismo ID: "session fixation")
		HttpSession anterior = request.getSession(false);
		if (anterior != null) {
			anterior.invalidate();
		}
		HttpSession session = request.getSession(true);   // crea la sesion
		session.setAttribute("usuario", usuario);
		session.setAttribute("rol", rol);
		session.setAttribute("horaIngreso", LocalDateTime.now().format(CookieServlet1.FORMATO));
		session.setAttribute("visitas", 0);

		// 3. Respuesta
		out.println(Html.inicio("Sesión creada"));
		out.println("<h2>Bienvenido " + Html.esc(usuario) + "</h2>");
		out.println("<p class='ok'>Sesión creada con rol <b>" + Html.esc(rol) + "</b>.</p>");
		out.println("<p>ID de sesión: <b>" + session.getId() + "</b><br>");
		out.println("Expira tras <b>" + (session.getMaxInactiveInterval() / 60) + "</b> minutos sin actividad.<br>");
		out.println("Sesiones activas en el servidor: <b>" + SesionListener.total() + "</b></p>");
		out.println("<a href='SessionServlet2'><button>Ir a Servlet 2 (ver sesión)</button></a>");
		out.println(Html.fin());
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("index.html");
	}
}
