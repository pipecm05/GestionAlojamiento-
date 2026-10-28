package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.estructuras.ColaPrioridad;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;

import java.util.Comparator;

public class PreparacionServicio {

    private final ColaPrioridad<Apartamento> colaPreparacion;

    public PreparacionServicio(Comparator<Apartamento> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El comparador de preparación es obligatorio.");
        }

        colaPreparacion = new ColaPrioridad<>(10, comparador);
    }

    public void agregarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        if (apartamento.getEstado()
                != EstadoApartamento.PENDIENTE_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento debe estar pendiente de preparación.");
        }

        colaPreparacion.insertar(apartamento);
    }

    public Apartamento obtenerSiguiente() {
        if (colaPreparacion.estaVacia()) {
            return null;
        }

        return colaPreparacion.extraerPrioridad();
    }

    public boolean hayApartamentosPendientes() {
        return !colaPreparacion.estaVacia();
    }

    public int cantidadPendientes() {
        return colaPreparacion.getTamanio();
    }

    public void iniciarPreparacion(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        if (apartamento.getEstado()
                != EstadoApartamento.PENDIENTE_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento no está pendiente de preparación.");
        }

        apartamento.cambiarEstado(
                EstadoApartamento.EN_PREPARACION,
                "Preparación iniciada.");
    }

    public void finalizarPreparacion(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        if (apartamento.getEstado()
                != EstadoApartamento.EN_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento no está en preparación.");
        }

        apartamento.cambiarEstado(
                EstadoApartamento.PREPARADO,
                "Preparación finalizada.");
    }
}
