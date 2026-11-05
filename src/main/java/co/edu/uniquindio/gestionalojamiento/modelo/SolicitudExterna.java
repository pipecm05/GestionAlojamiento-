package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudExterna;

import java.time.LocalDate;

public class SolicitudExterna {

    private final String codigo;
    private final Apartamento apartamento;
    private final Huesped huesped;
    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;

    private EstadoSolicitudExterna estado;
    private String motivoRechazo;

    public SolicitudExterna(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código es obligatorio.");
        }

        if (apartamento == null || huesped == null) {
            throw new IllegalArgumentException(
                    "Apartamento y huésped son obligatorios.");
        }

        if (fechaEntrada == null || fechaSalida == null
                || !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "Las fechas no son válidas.");
        }

        this.codigo = codigo;
        this.apartamento = apartamento;
        this.huesped = huesped;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = EstadoSolicitudExterna.PENDIENTE;
    }

    public String getCodigo() {
        return codigo;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public EstadoSolicitudExterna getEstado() {
        return estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void marcarAceptada() {
        if (estado != EstadoSolicitudExterna.PENDIENTE) {
            throw new IllegalStateException(
                    "La solicitud ya fue procesada.");
        }

        estado = EstadoSolicitudExterna.ACEPTADA;
    }

    public void marcarRechazada(String motivo) {
        if (estado != EstadoSolicitudExterna.PENDIENTE) {
            throw new IllegalStateException(
                    "La solicitud ya fue procesada.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo es obligatorio.");
        }

        motivoRechazo = motivo;
        estado = EstadoSolicitudExterna.RECHAZADA;
    }
}
