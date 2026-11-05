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

    // Constructor original: conserva compatibilidad
    // con las clases y pruebas existentes.
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

        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio."
            );
        }

        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Nuevo constructor con fecha de nacimiento.
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

        this.correo = correo;
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

    // Obtener la edad en una fecha específica.
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

        return Period.between(fechaNacimiento, fecha).getYears();
    }

    // Determinar si el huésped tiene menos de 6 años.
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
