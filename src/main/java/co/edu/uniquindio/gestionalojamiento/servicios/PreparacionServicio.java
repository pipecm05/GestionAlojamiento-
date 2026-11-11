
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.estructuras.ColaPrioridad;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;

import java.util.Comparator;

public class PreparacionServicio {

    private final ColaPrioridad<Apartamento> colaPreparacion;

    // El servicio conserva el apartamento que está en preparación.
    private Apartamento apartamentoEnPreparacion;

    public PreparacionServicio(Comparator<Apartamento> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El comparador de preparación es obligatorio."
            );
        }

        colaPreparacion = new ColaPrioridad<>(10, comparador);
        apartamentoEnPreparacion = null;
    }

    public void agregarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio."
            );
        }

        if (apartamento.getEstado()
                != EstadoApartamento.PENDIENTE_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento debe estar pendiente de preparación."
            );
        }

        if (apartamentoEnPreparacion != null
                && apartamentoEnPreparacion.getCodigo()
                .equals(apartamento.getCodigo())) {
            throw new IllegalStateException(
                    "El apartamento ya está en preparación."
            );
        }

        colaPreparacion.insertar(apartamento);
    }

    public Apartamento consultarSiguiente() {
        if (colaPreparacion.estaVacia()) {
            return null;
        }

        return colaPreparacion.consultarPrioridad();
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

    /**
     * Inicia la preparación del apartamento que tiene
     * la mayor prioridad en la cola.
     */
    public Apartamento iniciarSiguientePreparacion() {
        if (apartamentoEnPreparacion != null) {
            throw new IllegalStateException(
                    "Ya existe un apartamento en preparación."
            );
        }

        Apartamento siguiente = consultarSiguiente();

        if (siguiente == null) {
            return null;
        }

        iniciarPreparacion(siguiente);

        return apartamentoEnPreparacion;
    }

    /**
     * Inicia un apartamento únicamente si es el siguiente
     * de la cola de prioridad.
     */
    public void iniciarPreparacion(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio."
            );
        }

        if (apartamentoEnPreparacion != null) {
            throw new IllegalStateException(
                    "Ya existe un apartamento en preparación."
            );
        }

        if (colaPreparacion.estaVacia()) {
            throw new IllegalStateException(
                    "No hay apartamentos pendientes de preparación."
            );
        }

        Apartamento siguiente = colaPreparacion.consultarPrioridad();

        if (!siguiente.getCodigo().equals(apartamento.getCodigo())) {
            throw new IllegalStateException(
                    "Debe iniciarse primero el apartamento con mayor prioridad: "
                            + siguiente.getCodigo() + "."
            );
        }

        if (apartamento.getEstado()
                != EstadoApartamento.PENDIENTE_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento no está pendiente de preparación."
            );
        }

        // Primero se cambia el estado.
        // Solo después se retira de la cola.
        apartamento.cambiarEstado(
                EstadoApartamento.EN_PREPARACION,
                "Preparación iniciada."
        );

        Apartamento extraido = colaPreparacion.extraerPrioridad();

        if (!extraido.getCodigo().equals(apartamento.getCodigo())) {
            // Protección ante una extracción inesperada.
            apartamento.cambiarEstado(
                    EstadoApartamento.PENDIENTE_PREPARACION,
                    "Se revirtió el inicio de preparación."
            );

            colaPreparacion.insertar(extraido);

            throw new IllegalStateException(
                    "No se pudo retirar correctamente el apartamento de la cola."
            );
        }

        apartamentoEnPreparacion = apartamento;
    }

    /**
     * Finaliza la preparación del apartamento activo.
     */
    public void finalizarPreparacion() {
        if (apartamentoEnPreparacion == null) {
            throw new IllegalStateException(
                    "No hay ningún apartamento en preparación."
            );
        }

        apartamentoEnPreparacion.cambiarEstado(
                EstadoApartamento.PREPARADO,
                "Preparación finalizada."
        );

        apartamentoEnPreparacion = null;
    }

    /**
     * Finaliza la preparación verificando que sea
     * el mismo apartamento que está activo.
     */
    public void finalizarPreparacion(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio."
            );
        }

        if (apartamentoEnPreparacion == null
                || !apartamentoEnPreparacion.getCodigo()
                .equals(apartamento.getCodigo())) {
            throw new IllegalStateException(
                    "El apartamento indicado no es el que está en preparación."
            );
        }

        finalizarPreparacion();
    }

    public Apartamento getApartamentoEnPreparacion() {
        return apartamentoEnPreparacion;
    }
}
