package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class IndiceReservasApartamento {

    private final String codigoApartamento;
    private final ArbolIntervalosReserva reservas;;

    public IndiceReservasApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        this.codigoApartamento = apartamento.getCodigo();
        this.reservas = new ArbolIntervalosReserva();
    }

    public boolean registrar(Reserva reserva) {
        validarReserva(reserva);

        if (reservas.buscar(reserva) != null) {
            return false;
        }

        if (bloqueaNoches(reserva.getEstado())
                && buscarConflicto(
                reserva.getFechaEntrada(),
                reserva.getFechaSalida()) != null) {
            throw new IllegalArgumentException(
                    "La reserva se solapa con otra reserva vigente.");
        }

        return reservas.insertar(reserva);
    }

    public Reserva buscarConflicto(
            LocalDate entrada, LocalDate salida) {

        validarFechas(entrada, salida);

        return reservas.buscarConflicto(entrada, salida);
    }

    public boolean estaLibre(
            LocalDate entrada, LocalDate salida) {

        return buscarConflicto(entrada, salida) == null;
    }

    public int cantidadReservas() {
        return reservas.getTamanio();
    }

    private boolean bloqueaNoches(EstadoReserva estado) {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO
                || estado == EstadoReserva.FINALIZADA;
    }


    private void validarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria.");
        }

        if (!codigoApartamento.equals(
                reserva.getApartamento().getCodigo())) {
            throw new IllegalArgumentException(
                    "La reserva pertenece a otro apartamento.");
        }
    }

    private void validarFechas(
            LocalDate entrada, LocalDate salida) {

        if (entrada == null || salida == null
                || !entrada.isBefore(salida)) {
            throw new IllegalArgumentException(
                    "Las fechas no son válidas.");
        }
    }

    public String getCodigoApartamento() {
        return codigoApartamento;
    }

    public boolean eliminar(Reserva reserva) {
        validarReserva(reserva);
        return reservas.eliminar(reserva);
    }

}
