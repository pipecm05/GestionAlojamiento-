
package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;

public class Apartamento extends Alojamiento implements Comparable<Apartamento> {

    private int capacidadMaxima;
    private int numeroHabitaciones;
    private EstadoApartamento estado;
    private String motivoFueraServicio;

    private double tarifaBaja;
    private double tarifaMedia;
    private double tarifaAlta;

    public Apartamento(
            String codigo,
            String nombre,
            int capacidadMaxima,
            int numeroHabitaciones,
            double tarifaBaja,
            double tarifaMedia,
            double tarifaAlta
    ) {
        super(codigo, nombre);

        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad máxima debe ser mayor que cero."
            );
        }

        if (numeroHabitaciones <= 0) {
            throw new IllegalArgumentException(
                    "El número de habitaciones debe ser mayor que cero."
            );
        }

        if (tarifaBaja < 0 || tarifaMedia < 0 || tarifaAlta < 0) {
            throw new IllegalArgumentException(
                    "Las tarifas no pueden ser negativas."
            );
        }

        this.capacidadMaxima = capacidadMaxima;
        this.numeroHabitaciones = numeroHabitaciones;
        this.tarifaBaja = tarifaBaja;
        this.tarifaMedia = tarifaMedia;
        this.tarifaAlta = tarifaAlta;

        this.estado = EstadoApartamento.PREPARADO;
        this.motivoFueraServicio = null;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getNumeroHabitaciones() {
        return numeroHabitaciones;
    }

    public EstadoApartamento getEstado() {
        return estado;
    }

    public String getMotivoFueraServicio() {
        return motivoFueraServicio;
    }

    public double getTarifaBaja() {
        return tarifaBaja;
    }

    public double getTarifaMedia() {
        return tarifaMedia;
    }

    public double getTarifaAlta() {
        return tarifaAlta;
    }

    public double obtenerTarifa(TemporadaTipo temporada) {
        if (temporada == null) {
            throw new IllegalArgumentException(
                    "La temporada es obligatoria."
            );
        }

        switch (temporada) {
            case BAJA:
                return tarifaBaja;
            case MEDIA:
                return tarifaMedia;
            case ALTA:
                return tarifaAlta;
            default:
                throw new IllegalArgumentException(
                        "La temporada no es válida."
                );
        }
    }

    public void cambiarEstado(
            EstadoApartamento nuevoEstado,
            String motivo
    ) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio."
            );
        }

        if (nuevoEstado == EstadoApartamento.FUERA_DE_SERVICIO
                && (motivo == null || motivo.isBlank())) {
            throw new IllegalArgumentException(
                    "Debes indicar el motivo de fuera de servicio."
            );
        }

        this.estado = nuevoEstado;

        if (nuevoEstado == EstadoApartamento.FUERA_DE_SERVICIO) {
            this.motivoFueraServicio = motivo;
        } else {
            this.motivoFueraServicio = null;
        }
    }

    @Override
    public int compareTo(Apartamento otro) {
        return getCodigo().compareTo(otro.getCodigo());
    }

    @Override
    public String toString() {
        return "Apartamento{" +
                "codigo='" + getCodigo() + '\'' +
                ", nombre='" + getNombre() + '\'' +
                ", capacidadMaxima=" + capacidadMaxima +
                ", numeroHabitaciones=" + numeroHabitaciones +
                ", estado=" + estado +
                '}';
    }
}
