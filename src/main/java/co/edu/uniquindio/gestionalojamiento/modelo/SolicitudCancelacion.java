package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudCancelacion;

import java.time.LocalDateTime;

public class SolicitudCancelacion {

    private final String codigo;
    private final Reserva reserva;
    private final String motivo;
    private final LocalDateTime fechaSolicitud;

    private EstadoSolicitudCancelacion estado;
    private String observacionAdministrador;
    private LocalDateTime fechaRespuesta;

    public SolicitudCancelacion(
            String codigo,
            Reserva reserva,
            String motivo) {

        if (codigo == null || codigo.isBlank()
                || reserva == null
                || motivo == null || motivo.isBlank()) {

            throw new IllegalArgumentException(
                    "El código, la reserva y el motivo son obligatorios."
            );
        }

        this.codigo = codigo;
        this.reserva = reserva;
        this.motivo = motivo.trim();
        this.fechaSolicitud = LocalDateTime.now();

        this.estado = EstadoSolicitudCancelacion.PENDIENTE;
    }

    public void responder(
            EstadoSolicitudCancelacion nuevoEstado,
            String observacion) {

        if (estado != EstadoSolicitudCancelacion.PENDIENTE) {
            throw new IllegalStateException(
                    "La solicitud ya fue respondida."
            );
        }

        if (nuevoEstado != EstadoSolicitudCancelacion.APROBADA
                && nuevoEstado != EstadoSolicitudCancelacion.RECHAZADA) {

            throw new IllegalArgumentException(
                    "La respuesta debe ser aprobada o rechazada."
            );
        }

        this.estado = nuevoEstado;

        this.observacionAdministrador =
                observacion == null ? "" : observacion.trim();

        this.fechaRespuesta = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public EstadoSolicitudCancelacion getEstado() {
        return estado;
    }

    public String getObservacionAdministrador() {
        return observacionAdministrador;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    @Override
    public String toString() {
        return codigo + " - "
                + reserva.getCodigo() + " - "
                + estado;
    }
}
