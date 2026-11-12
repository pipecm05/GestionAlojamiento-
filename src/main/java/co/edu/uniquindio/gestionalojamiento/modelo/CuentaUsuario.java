
package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.RolUsuario;

public class CuentaUsuario {

    private final String correo;
    private final String hashContrasena;
    private final String salt;
    private final RolUsuario rol;
    private final Huesped huesped;

    public CuentaUsuario(String correo, String hashContrasena,
                         String salt, RolUsuario rol, Huesped huesped) {
        this.correo = correo;
        this.hashContrasena = hashContrasena;
        this.salt = salt;
        this.rol = rol;
        this.huesped = huesped;
    }

    public String getCorreo() {
        return correo;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public String getSalt() {
        return salt;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public boolean esAdministrador() {
        return rol == RolUsuario.ADMINISTRADOR;
    }
}
