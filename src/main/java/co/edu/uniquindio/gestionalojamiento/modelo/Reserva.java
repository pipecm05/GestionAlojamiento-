
package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;

import java.time.LocalDate;

public class Reserva implements Comparable<Reserva> {

    private String codigo;
    private Apartamento apartamento;
    private Huesped huesped;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private CanalReserva canal;
    private EstadoReserva estado;

    public Reserva(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal
    ) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la reserva es obligatorio."
            );
        }

        if (apartamento == null || huesped == null) {
            throw new IllegalArgumentException(
                    "El apartamento y el huésped son obligatorios."
            );
        }

        if (fechaEntrada == null || fechaSalida == null
                || !fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la entrada."
            );
        }

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva es obligatorio."
            );
        }

        this.codigo = codigo;
        this.apartamento = apartamento;
        this.huesped = huesped;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.canal = canal;

        if (canal == CanalReserva.PORTAL) {
            this.estado = EstadoReserva.PENDIENTE;
        } else {
            this.estado = EstadoReserva.CONFIRMADA;
        }
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

    public CanalReserva getCanal() {
        return canal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void cambiarEstado(EstadoReserva nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado de la reserva es obligatorio."
            );
        }

        this.estado = nuevoEstado;
    }

    public long calcularNumeroNoches() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                fechaEntrada,
                fechaSalida
        );
    }

    @Override
    public int compareTo(Reserva otra) {
        int resultado = fechaEntrada.compareTo(otra.fechaEntrada);

        if (resultado == 0) {
            return codigo.compareTo(otra.codigo);
        }

        return resultado;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "codigo='" + codigo + '\'' +
                ", apartamento=" + apartamento.getCodigo() +
                ", huesped=" + huesped.getDocumento() +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", canal=" + canal +
                ", estado=" + estado +
                '}';
    }
}
