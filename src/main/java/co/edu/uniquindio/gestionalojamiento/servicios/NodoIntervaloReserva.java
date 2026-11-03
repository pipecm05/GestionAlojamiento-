package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

class NodoIntervaloReserva {

    private Reserva reserva;
    private NodoIntervaloReserva izquierdo;
    private NodoIntervaloReserva derecho;
    private LocalDate maximaFechaSalida;

    public NodoIntervaloReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria.");
        }

        this.reserva = reserva;
        this.maximaFechaSalida = reserva.getFechaSalida();
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public NodoIntervaloReserva getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoIntervaloReserva izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoIntervaloReserva getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoIntervaloReserva derecho) {
        this.derecho = derecho;
    }

    public LocalDate getMaximaFechaSalida() {
        return maximaFechaSalida;
    }

    public void actualizarMaximaFechaSalida() {

        LocalDate maxima = reserva.getFechaSalida();

        if (izquierdo != null
                && izquierdo.getMaximaFechaSalida().isAfter(maxima)) {
            maxima = izquierdo.getMaximaFechaSalida();
        }

        if (derecho != null
                && derecho.getMaximaFechaSalida().isAfter(maxima)) {
            maxima = derecho.getMaximaFechaSalida();
        }

        maximaFechaSalida = maxima;
    }
}
