package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoTurno;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTurno;

import java.time.LocalDateTime;

public class Turno implements Comparable<Turno> {

    private final String codigo;
    private final Empleado empleado;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private TipoTurno tipo;
    private EstadoTurno estado;

    public Turno(
            String codigo,
            Empleado empleado,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            TipoTurno tipo
    ) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del turno es obligatorio."
            );
        }

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado es obligatorio."
            );
        }

        if (!empleado.isActivo()) {
            throw new IllegalArgumentException(
                    "No se puede asignar un turno a un empleado inactivo."
            );
        }

        validarFechas(fechaHoraInicio, fechaHoraFin);

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de turno es obligatorio."
            );
        }

        this.codigo = codigo;
        this.empleado = empleado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.tipo = tipo;
        this.estado = EstadoTurno.PROGRAMADO;
    }

    private void validarFechas(
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException(
                    "Las fechas del turno son obligatorias."
            );
        }

        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio debe ser anterior a la fecha de finalización."
            );
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public TipoTurno getTipo() {
        return tipo;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void cambiarEstado(EstadoTurno estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del turno es obligatorio."
            );
        }

        this.estado = estado;
    }

    public boolean seCruzaCon(Turno otro) {
        if (otro == null) {
            throw new IllegalArgumentException(
                    "El turno a comparar es obligatorio."
            );
        }

        if (!empleado.getDocumento().equals(
                otro.empleado.getDocumento()
        )) {
            return false;
        }

        if (estado == EstadoTurno.CANCELADO
                || otro.estado == EstadoTurno.CANCELADO) {
            return false;
        }

        return fechaHoraInicio.isBefore(otro.fechaHoraFin)
                && fechaHoraFin.isAfter(otro.fechaHoraInicio);
    }

    @Override
    public int compareTo(Turno otro) {
        int comparacion = fechaHoraInicio.compareTo(
                otro.fechaHoraInicio
        );

        if (comparacion != 0) {
            return comparacion;
        }

        return codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        return "Turno{" +
                "codigo='" + codigo + '\'' +
                ", empleado=" + empleado.getNombreCompleto() +
                ", inicio=" + fechaHoraInicio +
                ", fin=" + fechaHoraFin +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}