
package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;

import java.time.LocalDate;

public class Temporada {

    private String nombre;
    private TemporadaTipo tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double tarifaPorPersonaNoche;

    public Temporada(
            String nombre,
            TemporadaTipo tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            double tarifaPorPersonaNoche
    ) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la temporada es obligatorio."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de temporada es obligatorio."
            );
        }

        if (fechaInicio == null || fechaFin == null
                || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "Las fechas de la temporada no son válidas."
            );
        }

        if (!Double.isFinite(tarifaPorPersonaNoche)
                || tarifaPorPersonaNoche < 0) {
            throw new IllegalArgumentException(
                    "La tarifa debe ser un valor válido y no negativo."
            );
        }

        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tarifaPorPersonaNoche = tarifaPorPersonaNoche;
    }

    public String getNombre() {
        return nombre;
    }

    public TemporadaTipo getTipo() {
        return tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public double getTarifaPorPersonaNoche() {
        return tarifaPorPersonaNoche;
    }

    public boolean contieneFecha(LocalDate fecha) {
        if (fecha == null) {
            return false;
        }

        return !fecha.isBefore(fechaInicio)
                && fecha.isBefore(fechaFin);
    }

    public void setTarifaPorPersonaNoche(double tarifa) {
        if (!Double.isFinite(tarifa) || tarifa < 0) {
            throw new IllegalArgumentException(
                    "La tarifa debe ser un valor válido y no negativo."
            );
        }

        this.tarifaPorPersonaNoche = tarifa;
    }

    @Override
    public String toString() {
        return "Temporada{" +
                "nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", tarifaPorPersonaNoche=" + tarifaPorPersonaNoche +
                '}';
    }
}
