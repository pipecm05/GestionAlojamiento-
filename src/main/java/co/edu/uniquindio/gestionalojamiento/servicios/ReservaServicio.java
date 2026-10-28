package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoPreparacion;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.excepciones.FechasInvalidasException;
import co.edu.uniquindio.gestionalojamiento.excepciones.ReservaSolapadaException;
import co.edu.uniquindio.gestionalojamiento.excepciones.EstadoReservaInvalidoException;

import java.time.LocalDate;

public class ReservaServicio {

    private final ArbolBinarioBusqueda<Reserva> reservas;
    private final DisponibilidadServicio disponibilidadServicio;
    private final PreparacionServicio preparacionServicio;

    public ReservaServicio() {
        reservas = new ArbolBinarioBusqueda<>();
        disponibilidadServicio = new DisponibilidadServicio();

        preparacionServicio = new PreparacionServicio(
                new ComparadorApartamentoPreparacion(reservas));
    }

    public Reserva registrarReserva(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        if (fechaEntrada == null || fechaSalida == null
                || !fechaSalida.isAfter(fechaEntrada)) {
            throw new FechasInvalidasException(
                    "La salida debe ser posterior a la entrada.");
        }

        if (apartamento == null || huesped == null || canal == null) {
            throw new IllegalArgumentException(
                    "El apartamento, el huésped y el canal son obligatorios.");
        }

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de reserva es obligatorio.");
        }

        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva con ese código.");
        }

        if (!disponibilidadServicio.estaDisponible(
                apartamento, fechaEntrada, fechaSalida, reservas)) {
            throw new ReservaSolapadaException(
                    "El apartamento ya tiene una reserva en esas fechas.");
        }

        Reserva nuevaReserva = new Reserva(
                codigo,
                apartamento,
                huesped,
                fechaEntrada,
                fechaSalida,
                canal);

        reservas.insertar(nuevaReserva);
        return nuevaReserva;
    }

    public Reserva buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equals(codigo)) {
                return reserva;
            }
        }

        return null;
    }

    public ListaDoble<Reserva> listarReservas() {
        return reservas.inorden();
    }

    public int getCantidadReservas() {
        return reservas.getTamanio();
    }

    public boolean estaDisponible(
            Apartamento apartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        return disponibilidadServicio.estaDisponible(
                apartamento, fechaEntrada, fechaSalida, reservas);
    }

    public void confirmarReserva(String codigo) {
        Reserva reserva = obtenerReserva(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new EstadoReservaInvalidoException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }

    public void cancelarReserva(String codigo) {
        Reserva reserva = obtenerReserva(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new EstadoReservaInvalidoException(
                    "La reserva no se puede cancelar en su estado actual.");
        }

        reserva.cambiarEstado(EstadoReserva.CANCELADA);
    }

    public void registrarCheckIn(String codigo) {
        Reserva reserva = obtenerReserva(codigo);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new EstadoReservaInvalidoException(
                    "Solo se puede hacer check-in de una reserva confirmada.");
        }

        Apartamento apartamento = reserva.getApartamento();

        if (apartamento.getEstado() != EstadoApartamento.PREPARADO) {
            throw new EstadoReservaInvalidoException(
                    "El apartamento debe estar preparado para el check-in.");
        }

        reserva.cambiarEstado(EstadoReserva.EN_CURSO);

        apartamento.cambiarEstado(
                EstadoApartamento.OCUPADO, null);
    }

    public void registrarCheckOut(String codigo) {
        Reserva reserva = obtenerReserva(codigo);

        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new EstadoReservaInvalidoException(
                    "Solo se puede hacer check-out de una reserva en curso.");
        }

        Apartamento apartamento = reserva.getApartamento();

        reserva.cambiarEstado(EstadoReserva.FINALIZADA);

        apartamento.cambiarEstado(
                EstadoApartamento.PENDIENTE_PREPARACION, null);

        preparacionServicio.agregarApartamento(apartamento);
    }

    public PreparacionServicio getPreparacionServicio() {
        return preparacionServicio;
    }

    private Reserva obtenerReserva(String codigo) {
        Reserva reserva = buscarPorCodigo(codigo);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "No existe una reserva con el código indicado.");
        }

        return reserva;
    }
}
