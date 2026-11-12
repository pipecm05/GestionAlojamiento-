package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.modelo.CuentaUsuario;

public final class Sesion {

    private static CuentaUsuario cuentaActual;

    private Sesion() {
    }

    public static void iniciar(CuentaUsuario cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "La cuenta es obligatoria."
            );
        }

        cuentaActual = cuenta;
    }

    public static CuentaUsuario getCuentaActual() {
        return cuentaActual;
    }

    public static boolean estaIniciada() {
        return cuentaActual != null;
    }

    public static boolean esAdministrador() {
        return cuentaActual != null
                && cuentaActual.esAdministrador();
    }

    public static boolean esHuesped() {
        return cuentaActual != null
                && cuentaActual.getHuesped() != null;
    }

    public static void cerrar() {
        cuentaActual = null;
    }
}
