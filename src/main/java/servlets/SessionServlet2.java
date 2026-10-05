package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listeners.SesionListener;
import util.Html;

/**
 * SessionServlet2: valida que exista una sesion, muestra sus datos
 * y lista TODAS las sesiones activas del servidor.
 * Mapeado en web.xml como /SessionServlet2.
 */
public class SessionServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SessionServlet2() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 1. Validar sesion: getSession(false) NO crea una nueva
		HttpSession session = request.getSession(false);
		String usuario = (session != null) ? (String) session.getAttribute("usuario") : null;

		if (usuario == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
			out.println(Html.inicio("Sin sesión"));
			out.println("<h2>No hay sesión activa</h2>");
			out.println("<p>Tu sesión no existe, expiró o fue invalidada. Inicia sesión de nuevo.</p>");
			out.println("<p>Sesiones activas en el servidor: <b>" + SesionListener.total() + "</b></p>");
			out.println(Html.fin());
			out.close();
			return;
		}

		// 2. Actualizar contador de la sesion
		Object v = session.getAttribute("visitas");
		int visitas = (v instanceof Integer) ? (Integer) v + 1 : 1;
		session.setAttribute("visitas", visitas);
		String rol = (String) session.getAttribute("rol");

		// 3. Datos de mi sesion
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		out.println(Html.inicio("Mi sesión"));
		out.println("<h2>Hola " + Html.esc(usuario) + "</h2>");
		out.println("<table><tr><th>Dato de la sesión</th><th>Valor</th></tr>");
		out.println(CookieServlet1.fila("ID", session.getId()));
		out.println(CookieServlet1.fila("usuario", usuario));
		out.println(CookieServlet1.fila("rol", rol));
		out.println(CookieServlet1.fila("horaIngreso", (String) session.getAttribute("horaIngreso")));
		out.println(CookieServlet1.fila("visitas a esta página", String.valueOf(visitas)));
		out.println(CookieServlet1.fila("creada", f.format(new Date(session.getCreationTime()))));
		out.println(CookieServlet1.fila("último acceso", f.format(new Date(session.getLastAccessedTime()))));
		out.println(CookieServlet1.fila("expira tras (min)", String.valueOf(session.getMaxInactiveInterval() / 60)));
		out.println("</table>");

		// 4. Todas las sesiones activas (las registra SesionListener)
		out.println("<h3>Sesiones activas en el servidor: " + SesionListener.total() + "</h3>");
		out.println("<table><tr><th>ID</th><th>Usuario</th><th>Rol</th><th>Creada</th></tr>");
		for (HttpSession s : SesionListener.activas()) {
			try {
				String id = s.getId();
				String marca = id.equals(session.getId()) ? " (tú)" : "";
				out.println("<tr><td>" + id.substring(0, 8) + "…" + marca + "</td><td>"
						+ Html.esc(String.valueOf(s.getAttribute("usuario"))) + "</td><td>"
						+ Html.esc(String.valueOf(s.getAttribute("rol"))) + "</td><td>"
						+ f.format(new Date(s.getCreationTime())) + "</td></tr>");
			} catch (IllegalStateException e) {
				// la sesion se invalido mientras se recorria la lista
			}
		}
		out.println("</table>");

		// 5. Acciones
		out.println("<a href='SessionServlet2'><button>Recargar</button></a>");
		out.println(Html.boton("SessionLogout", "Cerrar mi sesión"));
		if ("admin".equals(rol)) {
			out.println("<form action='InvalidarSesiones' method='post' style='display:inline'>"
					+ "<input class='peligro' type='submit' value='Invalidar TODAS las sesiones activas'></form>");
		} else {
			out.println("<p><i>Solo un usuario con rol <b>admin</b> puede invalidar todas las sesiones.</i></p>");
		}
		out.println(Html.fin());
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
