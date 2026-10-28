
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;

import java.time.LocalDate;

public class AsignacionGrupoServicio {

    private final DisponibilidadServicio disponibilidadServicio;

    public AsignacionGrupoServicio() {
        disponibilidadServicio = new DisponibilidadServicio();
    }

    public ListaDoble<Apartamento> asignarApartamentos(
            int cantidadPersonas,
            ListaDoble<Apartamento> disponibles
    ) {
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (disponibles == null) {
            throw new IllegalArgumentException(
                    "La lista de apartamentos es obligatoria."
            );
        }

        ListaDoble<Apartamento> mejorAsignacion = new ListaDoble<>();
        ListaDoble<Apartamento> asignacionActual = new ListaDoble<>();

        buscarMejorAsignacion(
                disponibles,
                0,
                cantidadPersonas,
                0,
                asignacionActual,
                mejorAsignacion
        );

        if (mejorAsignacion.estaVacia()) {
            throw new IllegalArgumentException(
                    "No hay suficientes apartamentos para alojar al grupo."
            );
        }

        return mejorAsignacion;
    }

    public ListaDoble<Apartamento> asignarApartamentosDisponibles(
            int cantidadPersonas,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            ListaDoble<Apartamento> apartamentos,
            Iterable<Reserva> reservas
    ) {
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (fechaEntrada == null || fechaSalida == null
                || !fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                    "Las fechas de la estancia no son válidas."
            );
        }

        if (apartamentos == null || reservas == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos y las reservas son obligatorios."
            );
        }

        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getEstado() == EstadoApartamento.PREPARADO
                    && disponibilidadServicio.estaDisponible(
                    apartamento,
                    fechaEntrada,
                    fechaSalida,
                    reservas
            )) {
                disponibles.agregar(apartamento);
            }
        }

        return asignarApartamentos(cantidadPersonas, disponibles);
    }

    private void buscarMejorAsignacion(
            ListaDoble<Apartamento> disponibles,
            int indice,
            int personasNecesarias,
            int capacidadAcumulada,
            ListaDoble<Apartamento> actual,
            ListaDoble<Apartamento> mejor
    ) {
        if (capacidadAcumulada >= personasNecesarias) {
            if (mejor.estaVacia()
                    || actual.getTamanio() < mejor.getTamanio()) {
                mejor.vaciar();

                for (Apartamento apartamento : actual) {
                    mejor.agregar(apartamento);
                }
            }

            return;
        }

        if (indice >= disponibles.getTamanio()) {
            return;
        }

        if (!mejor.estaVacia()
                && actual.getTamanio() >= mejor.getTamanio()) {
            return;
        }

        Apartamento apartamento = obtenerPorIndice(disponibles, indice);

        // Opción 1: incluir el apartamento.
        actual.agregar(apartamento);

        buscarMejorAsignacion(
                disponibles,
                indice + 1,
                personasNecesarias,
                capacidadAcumulada + apartamento.getCapacidadMaxima(),
                actual,
                mejor
        );

        // Backtracking: deshacer la elección.
        actual.eliminar(apartamento);

        // Opción 2: excluir el apartamento.
        buscarMejorAsignacion(
                disponibles,
                indice + 1,
                personasNecesarias,
                capacidadAcumulada,
                actual,
                mejor
        );
    }

    private Apartamento obtenerPorIndice(
            ListaDoble<Apartamento> lista,
            int indice
    ) {
        int posicion = 0;

        for (Apartamento apartamento : lista) {
            if (posicion == indice) {
                return apartamento;
            }

            posicion++;
        }

        throw new IndexOutOfBoundsException(
                "No existe un apartamento en esa posición."
        );
    }
}
