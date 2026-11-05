package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Temporada;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import java.time.LocalDate;

public class TarifaServicio {

    private final ListaDoble<Temporada> temporadas;

    public TarifaServicio() {
        temporadas = new ListaDoble<>();
    }

    public boolean registrarTemporada(Temporada nueva) {

        if (nueva == null) {
            throw new IllegalArgumentException(
                    "La temporada es obligatoria.");
        }

        if (!nueva.getFechaInicio()
                .isBefore(nueva.getFechaFin())) {
            throw new IllegalArgumentException(
                    "La temporada debe tener al menos un día.");
        }

        // Impedir temporadas con fechas superpuestas.
        for (Temporada existente : temporadas) {

            boolean hayCruce =
                    nueva.getFechaInicio()
                            .isBefore(existente.getFechaFin())
                            && nueva.getFechaFin()
                            .isAfter(existente.getFechaInicio());

            if (hayCruce) {
                throw new IllegalArgumentException(
                        "Las fechas se cruzan con otra temporada.");
            }
        }

        temporadas.agregar(nueva);
        return true;
    }

    public Temporada buscarTemporada(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        for (Temporada temporada : temporadas) {
            if (temporada.contieneFecha(fecha)) {
                return temporada;
            }
        }

        return null;
    }

    public int cantidadTemporadas() {
        return temporadas.getTamanio();
    }

    public double calcularTotal(
            Apartamento apartamento,
            LocalDate entrada,
            LocalDate salida,
            int personasQuePagan) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        if (entrada == null || salida == null
                || !entrada.isBefore(salida)) {
            throw new IllegalArgumentException(
                    "Las fechas no son válidas.");
        }

        if (personasQuePagan < 0
                || personasQuePagan > apartamento.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                    "La cantidad de personas que pagan no es válida.");
        }

        return calcularTotalRecursivo(
                apartamento, entrada, salida, personasQuePagan);
    }

    private double calcularTotalRecursivo(
            Apartamento apartamento,
            LocalDate fechaActual,
            LocalDate salida,
            int personasQuePagan) {

        // Caso base: la fecha de salida no se cobra.
        if (!fechaActual.isBefore(salida)) {
            return 0;
        }

        Temporada temporada = buscarTemporada(fechaActual);

        // BAJA es la temporada base.
        TemporadaTipo tipo = temporada == null
                ? TemporadaTipo.BAJA
                : temporada.getTipo();

        double tarifaNoche = apartamento.obtenerTarifa(tipo);

        double subtotalNoche = tarifaNoche * personasQuePagan;

        // Avanzar una noche mediante recursividad.
        return subtotalNoche + calcularTotalRecursivo(
                apartamento,
                fechaActual.plusDays(1),
                salida,
                personasQuePagan);
    }


    public double calcularTotal(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria.");
        }

        Apartamento apartamento = reserva.getApartamento();
        ListaDoble<Huesped> huespedes = reserva.getHuespedes();

        if (huespedes.estaVacia()
                || huespedes.getTamanio()
                > apartamento.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                    "La cantidad de ocupantes no es válida.");
        }

        // Todos los huéspedes deben tener fecha de nacimiento.
        for (Huesped huesped : huespedes) {
            if (huesped.getFechaNacimiento() == null) {
                throw new IllegalArgumentException(
                        "Falta la fecha de nacimiento de "
                                + huesped.getNombreCompleto());
            }
        }

        return calcularReservaRecursivo(
                apartamento,
                huespedes,
                reserva.getFechaEntrada(),
                reserva.getFechaSalida());
    }

    private double calcularReservaRecursivo(
            Apartamento apartamento,
            ListaDoble<Huesped> huespedes,
            LocalDate fechaActual,
            LocalDate fechaSalida) {

        // Caso base: la fecha de salida no se cobra.
        if (!fechaActual.isBefore(fechaSalida)) {
            return 0;
        }

        Temporada temporada = buscarTemporada(fechaActual);

        TemporadaTipo tipo = temporada == null
                ? TemporadaTipo.BAJA
                : temporada.getTipo();

        double tarifaPersona =
                apartamento.obtenerTarifa(tipo);

        int personasQuePagan =
                contarPersonasQuePagan(huespedes, fechaActual);

        double subtotalNoche =
                tarifaPersona * personasQuePagan;

        return subtotalNoche + calcularReservaRecursivo(
                apartamento,
                huespedes,
                fechaActual.plusDays(1),
                fechaSalida);
    }

    private int contarPersonasQuePagan(
            ListaDoble<Huesped> huespedes,
            LocalDate fecha) {

        int cantidad = 0;

        for (Huesped huesped : huespedes) {
            if (!huesped.esMenorDeSeisEn(fecha)) {
                cantidad++;
            }
        }

        return cantidad;
    }


}
