package util;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validaciones del lado del servidor.
 * Cada metodo devuelve un mensaje de error, o null si el dato es valido.
 * (Las validaciones del navegador en index.html se pueden saltar,
 *  por eso SIEMPRE se vuelve a validar aqui).
 */
public final class Validador {

	private static final Pattern NOMBRE  = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{3,30}$");
	private static final Pattern CIUDAD  = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{2,40}$");
	private static final Pattern EMAIL   = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
	private static final Pattern USUARIO = Pattern.compile("^[A-Za-z0-9_]{3,20}$");

	public static final Set<String> LENGUAJES = Set.of("Java", "Python", "JavaScript", "C#");
	public static final Set<String> ROLES     = Set.of("admin", "usuario");

	private Validador() {
	}

	/** Devuelve el texto sin espacios al inicio/fin, o "" si es null. */
	public static String limpiar(String s) {
		return (s == null) ? "" : s.trim();
	}

	/** Agrega el error a la lista solo si no es null. */
	public static void agregar(List<String> errores, String error) {
		if (error != null) {
			errores.add(error);
		}
	}

	public static String nombre(String v) {
		if (v.isEmpty()) return "El nombre es obligatorio.";
		if (!NOMBRE.matcher(v).matches())
			return "El nombre solo puede tener letras y espacios (entre 3 y 30 caracteres).";
		return null;
	}

	public static String email(String v) {
		if (v.isEmpty()) return "El correo es obligatorio.";
		if (v.length() > 60) return "El correo no puede tener más de 60 caracteres.";
		if (!EMAIL.matcher(v).matches()) return "El correo no tiene un formato válido (ej: juan@correo.com).";
		return null;
	}

	public static String edad(String v) {
		if (v.isEmpty()) return "La edad es obligatoria.";
		try {
			int edad = Integer.parseInt(v);
			if (edad < 1 || edad > 120) return "La edad debe estar entre 1 y 120.";
		} catch (NumberFormatException e) {
			return "La edad debe ser un número entero.";
		}
		return null;
	}

	public static String ciudad(String v) {
		if (v.isEmpty()) return "La ciudad es obligatoria.";
		if (!CIUDAD.matcher(v).matches())
			return "La ciudad solo puede tener letras y espacios (entre 2 y 40 caracteres).";
		return null;
	}

	public static String lenguaje(String v) {
		if (v.isEmpty()) return "Debe elegir un lenguaje favorito.";
		if (!LENGUAJES.contains(v)) return "El lenguaje elegido no es una opción válida.";
		return null;
	}

	public static String usuario(String v) {
		if (v.isEmpty()) return "El usuario es obligatorio.";
		if (!USUARIO.matcher(v).matches())
			return "El usuario solo puede tener letras, números y _ (entre 3 y 20 caracteres, sin espacios).";
		return null;
	}

	public static String rol(String v) {
		if (v.isEmpty()) return "Debe elegir un rol.";
		if (!ROLES.contains(v)) return "El rol elegido no es válido.";
		return null;
	}
}
