package co.edu.uniquindio.gestionalojamiento.modelo;

import java.time.LocalDateTime;

public class Favorito {

    private final Huesped huesped;
    private final Alojamiento alojamiento;
    private final LocalDateTime fechaAgregado;

    public Favorito(Huesped huesped, Alojamiento alojamiento) {

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped es obligatorio."
            );
        }

        if (alojamiento == null) {
            throw new IllegalArgumentException(
                    "El alojamiento es obligatorio."
            );
        }

        this.huesped = huesped;
        this.alojamiento = alojamiento;
        this.fechaAgregado = LocalDateTime.now();
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Alojamiento getAlojamiento() {
        return alojamiento;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    @Override
    public String toString() {
        return "Favorito{" +
                "huesped=" + huesped.getNombreCompleto() +
                ", alojamiento=" + alojamiento.getNombre() +
                ", fechaAgregado=" + fechaAgregado +
                '}';
    }
}
