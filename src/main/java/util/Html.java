package util;

import java.util.List;

/**
 * Ayudas para generar el HTML de las respuestas de los servlets.
 */
public final class Html {

	private Html() {
	}

	/** Escapa caracteres especiales para que un dato del usuario no se ejecute como HTML/JS (XSS). */
	public static String esc(String s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder(s.length());
		for (char c : s.toCharArray()) {
			switch (c) {
			case '&': sb.append("&amp;"); break;
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '"': sb.append("&quot;"); break;
			case '\'': sb.append("&#39;"); break;
			default: sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String inicio(String titulo) {
		return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + esc(titulo) + "</title>"
				+ "<style>"
				+ "body{font-family:Arial,sans-serif;margin:40px;background:#f4f6f8}"
				+ ".caja{background:#fff;padding:20px 26px;border-radius:8px;box-shadow:0 1px 4px rgba(0,0,0,.15);max-width:720px}"
				+ "h2{color:#2c3e50;margin-top:0}"
				+ "table{border-collapse:collapse;margin:10px 0}"
				+ "th,td{border:1px solid #ccc;padding:6px 10px;text-align:left;font-size:14px}"
				+ "th{background:#2980b9;color:#fff}"
				+ ".error{background:#fdecea;border:1px solid #e74c3c;color:#a93226;padding:10px 26px;border-radius:6px}"
				+ ".ok{background:#e9f7ef;border:1px solid #27ae60;color:#1e8449;padding:10px 14px;border-radius:6px}"
				+ "input[type=submit],button{padding:6px 14px;margin:4px 4px 4px 0;cursor:pointer}"
				+ ".peligro{background:#c0392b;color:#fff;border:none;border-radius:4px}"
				+ "</style></head><body><div class='caja'>";
	}

	public static String fin() {
		return "<p><a href='index.html'>Volver al inicio</a></p></div></body></html>";
	}

	/** Lista de errores de validacion. */
	public static String errores(List<String> errores) {
		StringBuilder sb = new StringBuilder("<ul class='error'>");
		for (String e : errores) {
			sb.append("<li>").append(esc(e)).append("</li>");
		}
		return sb.append("</ul>").toString();
	}

	/** Boton que envia un formulario POST a una URL. */
	public static String boton(String accion, String texto) {
		return "<form action='" + accion + "' method='post' style='display:inline'>"
				+ "<input type='submit' value='" + esc(texto) + "'></form>";
	}
}
