
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class DisponibilidadServicio {

    public boolean estaDisponible(
            Apartamento apartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Iterable<Reserva> reservas
    ) {
        validarDatos(apartamento, fechaEntrada, fechaSalida, reservas);

        for (Reserva reserva : reservas) {
            if (reserva.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())
                    && estaActiva(reserva.getEstado())
                    && seSolapan(
                    fechaEntrada,
                    fechaSalida,
                    reserva.getFechaEntrada(),
                    reserva.getFechaSalida()
            )) {
                return false;
            }
        }

        return true;
    }

    public boolean seSolapan(
            LocalDate entradaNueva,
            LocalDate salidaNueva,
            LocalDate entradaExistente,
            LocalDate salidaExistente
    ) {
        return entradaNueva.isBefore(salidaExistente)
                && salidaNueva.isAfter(entradaExistente);
    }

    private boolean estaActiva(EstadoReserva estado) {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO;
    }

    private void validarDatos(
            Apartamento apartamento,
            LocalDate entrada,
            LocalDate salida,
            Iterable<Reserva> reservas
    ) {
        if (apartamento == null || reservas == null
                || entrada == null || salida == null) {
            throw new IllegalArgumentException(
                    "El apartamento, las fechas y las reservas son obligatorios."
            );
        }

        if (!salida.isAfter(entrada)) {
            throw new IllegalArgumentException(
                    "La salida debe ser posterior a la entrada."
            );
        }
    }
}
