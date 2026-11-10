package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;

public class Empleado implements Comparable<Empleado> {

    private final String documento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private TipoEmpleado tipo;
    private boolean activo;

    public Empleado(
            String documento,
            String nombres,
            String apellidos,
            String telefono,
            String correo,
            TipoEmpleado tipo
    ) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio."
            );
        }

        this.documento = documento;
        setNombres(nombres);
        setApellidos(apellidos);
        setTelefono(telefono);
        setCorreo(correo);
        setTipo(tipo);

        this.activo = true;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public TipoEmpleado getTipo() {
        return tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException(
                    "Los nombres son obligatorios."
            );
        }

        this.nombres = nombres.trim();
    }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios."
            );
        }

        this.apellidos = apellidos.trim();
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio."
            );
        }

        this.telefono = telefono.trim();
    }

    public void setCorreo(String correo) {
        if (correo == null || !correo.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido."
            );
        }

        this.correo = correo.trim();
    }

    public void setTipo(TipoEmpleado tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de empleado es obligatorio."
            );
        }

        this.tipo = tipo;
    }

    public void cambiarEstado(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public int compareTo(Empleado otro) {
        return documento.compareTo(otro.documento);
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "documento='" + documento + '\'' +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", tipo=" + tipo +
                ", activo=" + activo +
                '}';
    }
}