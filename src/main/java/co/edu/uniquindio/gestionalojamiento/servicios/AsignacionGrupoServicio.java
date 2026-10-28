
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.excepciones.FechasInvalidasException;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class AsignacionGrupoServicio {

    private final DisponibilidadServicio disponibilidadServicio;

    public AsignacionGrupoServicio() {
        disponibilidadServicio = new DisponibilidadServicio();
    }

    public ListaDoble<Apartamento> asignarApartamentos(
            int cantidadPersonas,
            ListaDoble<Apartamento> disponibles) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero.");
        }

        if (disponibles == null) {
            throw new IllegalArgumentException(
                    "La lista de apartamentos es obligatoria.");
        }

        ListaDoble<Apartamento> mejor = new ListaDoble<>();
        ListaDoble<Apartamento> actual = new ListaDoble<>();

        buscarMejorAsignacion(
                disponibles, 0, cantidadPersonas, 0, actual, mejor);

        if (mejor.estaVacia()) {
            throw new IllegalArgumentException(
                    "No hay suficientes apartamentos para alojar al grupo.");
        }

        return mejor;
    }

    public ListaDoble<Apartamento> asignarApartamentosDisponibles(
            int cantidadPersonas,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            ListaDoble<Apartamento> apartamentos,
            ArbolBinarioBusqueda<Reserva> reservas) {

        validarDatosBusqueda(
                cantidadPersonas, fechaEntrada, fechaSalida,
                apartamentos, reservas);

        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getEstado() == EstadoApartamento.PREPARADO
                    && disponibilidadServicio.estaDisponible(
                    apartamento, fechaEntrada, fechaSalida, reservas)) {
                disponibles.agregar(apartamento);
            }
        }

        return asignarApartamentos(cantidadPersonas, disponibles);
    }

    public ListaDoble<Apartamento> asignarApartamentosDisponibles(
            int cantidadPersonas,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            ListaDoble<Apartamento> apartamentos,
            Iterable<Reserva> reservas) {

        validarDatosBusqueda(
                cantidadPersonas, fechaEntrada, fechaSalida,
                apartamentos, reservas);

        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getEstado() == EstadoApartamento.PREPARADO
                    && disponibilidadServicio.estaDisponible(
                    apartamento, fechaEntrada, fechaSalida, reservas)) {
                disponibles.agregar(apartamento);
            }
        }

        return asignarApartamentos(cantidadPersonas, disponibles);
    }

    public AsignacionGrupo crearAsignacionGrupo(
            String codigo,
            ListaDoble<Huesped> huespedes,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            ListaDoble<Apartamento> apartamentos,
            ArbolBinarioBusqueda<Reserva> reservas) {

        validarHuespedes(huespedes);

        ListaDoble<Apartamento> seleccionados =
                asignarApartamentosDisponibles(
                        huespedes.getTamanio(),
                        fechaEntrada,
                        fechaSalida,
                        apartamentos,
                        reservas);

        AsignacionGrupo grupo = new AsignacionGrupo(codigo);

        for (Huesped huesped : huespedes) {
            grupo.agregarHuesped(huesped);
        }

        for (Apartamento apartamento : seleccionados) {
            grupo.agregarApartamento(apartamento);
        }

        distribuirHuespedes(grupo, huespedes, seleccionados);

        return grupo;
    }

    private void distribuirHuespedes(
            AsignacionGrupo grupo,
            ListaDoble<Huesped> huespedes,
            ListaDoble<Apartamento> apartamentos) {

        int indiceApartamento = 0;
        int ocupacionApartamento = 0;

        for (Huesped huesped : huespedes) {
            while (indiceApartamento < apartamentos.getTamanio()) {
                Apartamento apartamento =
                        obtenerPorIndice(apartamentos, indiceApartamento);

                if (ocupacionApartamento
                        < apartamento.getCapacidadMaxima()) {
                    grupo.asignarHuesped(huesped, apartamento);
                    ocupacionApartamento++;
                    break;
                }

                indiceApartamento++;
                ocupacionApartamento = 0;
            }

            if (indiceApartamento >= apartamentos.getTamanio()) {
                throw new IllegalStateException(
                        "No hay capacidad suficiente para asignar a todos los huéspedes.");
            }
        }
    }

    private void buscarMejorAsignacion(
            ListaDoble<Apartamento> disponibles,
            int indice,
            int personasNecesarias,
            int capacidadAcumulada,
            ListaDoble<Apartamento> actual,
            ListaDoble<Apartamento> mejor) {

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

        actual.agregar(apartamento);

        buscarMejorAsignacion(
                disponibles,
                indice + 1,
                personasNecesarias,
                capacidadAcumulada + apartamento.getCapacidadMaxima(),
                actual,
                mejor);

        actual.eliminar(apartamento);

        buscarMejorAsignacion(
                disponibles,
                indice + 1,
                personasNecesarias,
                capacidadAcumulada,
                actual,
                mejor);
    }

    private void validarHuespedes(ListaDoble<Huesped> huespedes) {
        if (huespedes == null || huespedes.estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener al menos un huésped.");
        }

        for (Huesped huesped : huespedes) {
            if (huesped == null) {
                throw new IllegalArgumentException(
                        "La lista contiene un huésped nulo.");
            }
        }
    }

    private void validarDatosBusqueda(
            int cantidadPersonas,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            ListaDoble<Apartamento> apartamentos,
            Object reservas) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero.");
        }

        if (fechaEntrada == null || fechaSalida == null
                || !fechaSalida.isAfter(fechaEntrada)) {
            throw new FechasInvalidasException(
                    "La fecha de salida debe ser posterior a la entrada.");
        }

        if (apartamentos == null || reservas == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos y las reservas son obligatorios.");
        }
    }

    private Apartamento obtenerPorIndice(
            ListaDoble<Apartamento> lista,
            int indice) {

        int posicion = 0;

        for (Apartamento apartamento : lista) {
            if (posicion == indice) {
                return apartamento;
            }

            posicion++;
        }

        throw new IndexOutOfBoundsException(
                "No existe un apartamento en esa posición.");
    }
}
