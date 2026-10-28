package co.edu.uniquindio.gestionalojamiento.estructuras;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;

import java.time.LocalDate;
import java.util.Comparator;

public class ComparadorApartamentoPreparacion
        implements Comparator<Apartamento> {

    private final Iterable<Reserva> reservas;
    private final LocalDate fechaActual;

    public ComparadorApartamentoPreparacion(
            Iterable<Reserva> reservas) {

        if (reservas == null) {
            throw new IllegalArgumentException(
                    "Las reservas son obligatorias.");
        }

        this.reservas = reservas;
        this.fechaActual = LocalDate.now();
    }

    @Override
    public int compare(Apartamento a1, Apartamento a2) {
        int prioridad1 = obtenerPrioridad(a1);
        int prioridad2 = obtenerPrioridad(a2);

        int comparacion = Integer.compare(prioridad1, prioridad2);

        if (comparacion != 0) {
            return comparacion;
        }

        return a1.compareTo(a2);
    }

    private int obtenerPrioridad(Apartamento apartamento) {
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())
                    && reserva.getFechaEntrada().equals(fechaActual)
                    && (reserva.getEstado() == EstadoReserva.PENDIENTE
                    || reserva.getEstado() == EstadoReserva.CONFIRMADA)) {

                return 0;
            }
        }

        return 1;
    }
}
