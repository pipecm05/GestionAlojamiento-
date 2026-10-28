package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;

import java.time.LocalDate;

public class Reserva implements Comparable<Reserva> {

    private final String codigo;
    private final Apartamento apartamento;
    private final ListaDoble<Huesped> huespedes;
    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;
    private final CanalReserva canal;
    private EstadoReserva estado;

    // Constructor compatible con las reservas individuales existentes.
    public Reserva(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        this(codigo, apartamento, crearListaHuespedes(huesped),
                fechaEntrada, fechaSalida, canal);
    }

    // Constructor para reservas grupales.
    public Reserva(
            String codigo,
            Apartamento apartamento,
            ListaDoble<Huesped> huespedes,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la reserva es obligatorio.");
        }

        if (apartamento == null || huespedes == null
                || huespedes.estaVacia()) {
            throw new IllegalArgumentException(
                    "El apartamento y los huéspedes son obligatorios.");
        }

        if (fechaEntrada == null || fechaSalida == null
                || !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "Las fechas de la reserva no son válidas.");
        }

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva es obligatorio.");
        }

        if (huespedes.getTamanio() > apartamento.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                    "La cantidad de huéspedes supera la capacidad del apartamento.");
        }

        this.codigo = codigo;
        this.apartamento = apartamento;
        this.huespedes = copiarHuespedes(huespedes);
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.canal = canal;

        this.estado = canal == CanalReserva.PORTAL
                ? EstadoReserva.PENDIENTE
                : EstadoReserva.CONFIRMADA;
    }

    private static ListaDoble<Huesped> crearListaHuespedes(
            Huesped huesped) {

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped es obligatorio.");
        }

        ListaDoble<Huesped> lista = new ListaDoble<>();
        lista.agregar(huesped);
        return lista;
    }

    private static ListaDoble<Huesped> copiarHuespedes(
            ListaDoble<Huesped> origen) {

        ListaDoble<Huesped> copia = new ListaDoble<>();

        for (Huesped huesped : origen) {
            if (huesped == null) {
                throw new IllegalArgumentException(
                        "La lista no puede contener huéspedes nulos.");
            }

            copia.agregar(huesped);
        }

        return copia;
    }

    public String getCodigo() {
        return codigo;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    /**
     * Compatibilidad con el código anterior:
     * devuelve el primer huésped registrado.
     */
    public Huesped getHuesped() {
        return huespedes.estaVacia()
                ? null
                : huespedes.obtenerPrimero();
    }

    /**
     * Devuelve una copia de la lista para evitar modificar
     * directamente los huéspedes de la reserva.
     */
    public ListaDoble<Huesped> getHuespedes() {
        return copiarHuespedes(huespedes);
    }

    public int getCantidadHuespedes() {
        return huespedes.getTamanio();
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public CanalReserva getCanal() {
        return canal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void cambiarEstado(EstadoReserva nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado de la reserva es obligatorio.");
        }

        this.estado = nuevoEstado;
    }

    public long calcularNumeroNoches() {
        return fechaEntrada.until(fechaSalida).getDays()
                + java.time.temporal.ChronoUnit.DAYS.between(
                fechaEntrada, fechaSalida)
                - fechaEntrada.until(fechaSalida).getDays();
    }

    @Override
    public int compareTo(Reserva otra) {
        int comparacionFecha =
                this.fechaEntrada.compareTo(otra.fechaEntrada);

        if (comparacionFecha != 0) {
            return comparacionFecha;
        }

        return this.codigo.compareTo(otra.codigo);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "codigo='" + codigo + '\'' +
                ", apartamento=" + apartamento.getCodigo() +
                ", cantidadHuespedes=" + huespedes.getTamanio() +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", canal=" + canal +
                ", estado=" + estado +
                '}';
    }
}
