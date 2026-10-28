package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoPrecio;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoCapacidad;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoReservas;

import java.time.LocalDate;
import java.util.Comparator;

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

    public ListaDoble<Apartamento> buscarDisponibles(
            ListaDoble<Apartamento> apartamentos,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            int cantidadPersonas,
            Iterable<Reserva> reservas,
            Comparator<Apartamento> comparador
    ) {
        if (apartamentos == null || reservas == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos y las reservas son obligatorios."
            );
        }

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El criterio de ordenamiento es obligatorio."
            );
        }

        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getEstado() == EstadoApartamento.PREPARADO
                    && apartamento.getCapacidadMaxima() >= cantidadPersonas
                    && estaDisponible(
                    apartamento,
                    fechaEntrada,
                    fechaSalida,
                    reservas
            )) {
                insertarOrdenado(resultado, apartamento, comparador);
            }
        }

        return resultado;
    }

    public Comparator<Apartamento> ordenarPorPrecio() {
        return new ComparadorApartamentoPrecio();
    }

    public Comparator<Apartamento> ordenarPorCapacidad() {
        return new ComparadorApartamentoCapacidad();
    }

    public Comparator<Apartamento> ordenarPorReservas(
            Iterable<Reserva> reservas
    ) {
        return new ComparadorApartamentoReservas(reservas);
    }

    private void insertarOrdenado(
            ListaDoble<Apartamento> lista,
            Apartamento apartamento,
            Comparator<Apartamento> comparador
    ) {
        ListaDoble<Apartamento> auxiliar = new ListaDoble<>();
        boolean insertado = false;

        for (Apartamento actual : lista) {
            if (!insertado && comparador.compare(apartamento, actual) < 0) {
                auxiliar.agregar(apartamento);
                insertado = true;
            }

            auxiliar.agregar(actual);
        }

        if (!insertado) {
            auxiliar.agregar(apartamento);
        }

        lista.vaciar();

        for (Apartamento actual : auxiliar) {
            lista.agregar(actual);
        }
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
