package co.edu.uniquindio.gestionalojamiento.modelo;

import java.time.LocalDate;
import java.time.Period;

public class Huesped implements Comparable<Huesped> {

    private String documento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private LocalDate fechaNacimiento;

    // Constructor original para mantener compatibilidad.
    public Huesped(
            String documento,
            String nombres,
            String apellidos,
            String telefono,
            String correo
    ) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio."
            );
        }

        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException(
                    "Los nombres son obligatorios."
            );
        }

        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios."
            );
        }

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio."
            );
        }

        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;

        // Validar el correo también desde el constructor.
        setCorreo(correo);
    }

    // Constructor con fecha de nacimiento.
    public Huesped(
            String documento,
            String nombres,
            String apellidos,
            String telefono,
            String correo,
            LocalDate fechaNacimiento
    ) {
        this(documento, nombres, apellidos, telefono, correo);
        setFechaNacimiento(fechaNacimiento);
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException(
                    "Los nombres son obligatorios."
            );
        }

        this.nombres = nombres;
    }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios."
            );
        }

        this.apellidos = apellidos;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio."
            );
        }

        this.telefono = telefono;
    }

    public void setCorreo(String correo) {

        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio."
            );
        }

        String correoLimpio = correo.trim();

        if (!correoLimpio.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no tiene un formato válido."
            );
        }

        this.correo = correoLimpio;
    }

    // Validar la fecha de nacimiento.
    public void setFechaNacimiento(LocalDate fechaNacimiento) {

        if (fechaNacimiento == null
                || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento no es válida."
            );
        }

        this.fechaNacimiento = fechaNacimiento;
    }

    // Calcular la edad en una fecha específica.
    public int obtenerEdadEn(LocalDate fecha) {

        if (fechaNacimiento == null) {
            throw new IllegalStateException(
                    "El huésped no tiene fecha de nacimiento registrada."
            );
        }

        if (fecha == null || fecha.isBefore(fechaNacimiento)) {
            throw new IllegalArgumentException(
                    "La fecha de consulta no es válida."
            );
        }

        return Period.between(
                fechaNacimiento, fecha
        ).getYears();
    }

    // Comprobar si el huésped es menor de seis años.
    public boolean esMenorDeSeisEn(LocalDate fecha) {
        return obtenerEdadEn(fecha) < 6;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public int compareTo(Huesped otro) {
        return documento.compareTo(otro.documento);
    }

    @Override
    public String toString() {
        return "Huesped{" +
                "documento='" + documento + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}
