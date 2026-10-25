
package co.edu.uniquindio.gestionalojamiento.modelo;

public class Huesped implements Comparable<Huesped> {

    private String documento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;

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
                '}';
    }
}

