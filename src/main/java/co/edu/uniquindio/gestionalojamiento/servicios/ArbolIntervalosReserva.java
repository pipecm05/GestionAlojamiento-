package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class ArbolIntervalosReserva {

    private NodoIntervaloReserva raiz;
    private int tamanio;

    public boolean insertar(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria.");
        }

        if (buscar(reserva) != null) {
            return false;
        }

        raiz = insertarRecursivo(raiz, reserva);
        tamanio++;
        return true;
    }

    private NodoIntervaloReserva insertarRecursivo(
            NodoIntervaloReserva actual,
            Reserva reserva) {

        if (actual == null) {
            return new NodoIntervaloReserva(reserva);
        }

        int comparacion = reserva.compareTo(actual.getReserva());

        if (comparacion < 0) {
            actual.setIzquierdo(
                    insertarRecursivo(actual.getIzquierdo(), reserva));
        } else if (comparacion > 0) {
            actual.setDerecho(
                    insertarRecursivo(actual.getDerecho(), reserva));
        }

        actual.actualizarMaximaFechaSalida();

        return actual;
    }

    public Reserva buscar(Reserva reserva) {

        if (reserva == null) {
            return null;
        }

        return buscarRecursivo(raiz, reserva);
    }

    private Reserva buscarRecursivo(
            NodoIntervaloReserva actual,
            Reserva reserva) {

        if (actual == null) {
            return null;
        }

        int comparacion = reserva.compareTo(actual.getReserva());

        if (comparacion == 0) {
            return actual.getReserva();
        }

        if (comparacion < 0) {
            return buscarRecursivo(actual.getIzquierdo(), reserva);
        }

        return buscarRecursivo(actual.getDerecho(), reserva);
    }

    public Reserva buscarConflicto(
            LocalDate entrada, LocalDate salida) {

        validarFechas(entrada, salida);
        return buscarConflictoRecursivo(raiz, entrada, salida);
    }

    private Reserva buscarConflictoRecursivo(
            NodoIntervaloReserva actual,
            LocalDate entrada,
            LocalDate salida) {

        if (actual == null) {
            return null;
        }

        // Si todas las reservas del subárbol terminaron
        // antes de la entrada, descartamos toda la rama.
        if (!actual.getMaximaFechaSalida().isAfter(entrada)) {
            return null;
        }

        Reserva reserva = actual.getReserva();

        if (bloqueaNoches(reserva)
                && entrada.isBefore(reserva.getFechaSalida())
                && salida.isAfter(reserva.getFechaEntrada())) {
            return reserva;
        }

        // Primero buscar en el subárbol izquierdo.
        Reserva conflicto = buscarConflictoRecursivo(
                actual.getIzquierdo(), entrada, salida);

        if (conflicto != null) {
            return conflicto;
        }

        // El subárbol derecho contiene entradas posteriores.
        // Solo se visita si todavía pueden comenzar
        // antes de la salida consultada.
        if (reserva.getFechaEntrada().isBefore(salida)) {
            return buscarConflictoRecursivo(
                    actual.getDerecho(), entrada, salida);
        }

        return null;
    }

    private boolean bloqueaNoches(Reserva reserva) {
        return switch (reserva.getEstado()) {
            case PENDIENTE, CONFIRMADA, EN_CURSO, FINALIZADA -> true;
            default -> false;
        };
    }

    public int getTamanio() {
        return tamanio;
    }

    private void validarFechas(LocalDate entrada, LocalDate salida) {
        if (entrada == null || salida == null
                || !entrada.isBefore(salida)) {
            throw new IllegalArgumentException(
                    "Las fechas no son válidas.");
        }
    }


    public boolean eliminar(Reserva reserva) {

        if (reserva == null || buscar(reserva) == null) {
            return false;
        }

        raiz = eliminarRecursivo(raiz, reserva);
        tamanio--;
        return true;
    }

    private NodoIntervaloReserva eliminarRecursivo(
            NodoIntervaloReserva actual,
            Reserva reserva) {

        if (actual == null) {
            return null;
        }

        int comparacion = reserva.compareTo(actual.getReserva());

        if (comparacion < 0) {

            actual.setIzquierdo(
                    eliminarRecursivo(actual.getIzquierdo(), reserva));

        } else if (comparacion > 0) {

            actual.setDerecho(
                    eliminarRecursivo(actual.getDerecho(), reserva));

        } else {

            // Caso 1: nodo sin hijo izquierdo.
            if (actual.getIzquierdo() == null) {
                return actual.getDerecho();
            }

            // Caso 2: nodo sin hijo derecho.
            if (actual.getDerecho() == null) {
                return actual.getIzquierdo();
            }

            // Caso 3: nodo con dos hijos.
            NodoIntervaloReserva sucesor =
                    encontrarMinimo(actual.getDerecho());

            actual.setReserva(sucesor.getReserva());

            actual.setDerecho(
                    eliminarRecursivo(
                            actual.getDerecho(),
                            sucesor.getReserva()));
        }

        // Recalcular la fecha máxima después de eliminar.
        actual.actualizarMaximaFechaSalida();

        return actual;
    }

    private NodoIntervaloReserva encontrarMinimo(
            NodoIntervaloReserva actual) {

        while (actual.getIzquierdo() != null) {
            actual = actual.getIzquierdo();
        }

        return actual;
    }

    LocalDate obtenerMaximaFechaSalida() {

        if (raiz == null) {
            return null;
        }

        return raiz.getMaximaFechaSalida();
    }

}
