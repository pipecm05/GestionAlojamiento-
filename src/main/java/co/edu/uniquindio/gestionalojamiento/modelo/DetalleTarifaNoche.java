package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;

import java.time.LocalDate;

public class DetalleTarifaNoche {

    private final LocalDate fecha;
    private final TemporadaTipo temporada;
    private final int totalOcupantes;
    private final int personasQuePagan;
    private final double tarifaPorPersona;
    private final double subtotal;

    public DetalleTarifaNoche(
            LocalDate fecha,
            TemporadaTipo temporada,
            int totalOcupantes,
            int personasQuePagan,
            double tarifaPorPersona) {

        if (fecha == null || temporada == null) {
            throw new IllegalArgumentException(
                    "La fecha y la temporada son obligatorias.");
        }

        if (totalOcupantes <= 0
                || personasQuePagan < 0
                || personasQuePagan > totalOcupantes) {
            throw new IllegalArgumentException(
                    "La cantidad de ocupantes no es válida.");
        }

        if (!Double.isFinite(tarifaPorPersona)
                || tarifaPorPersona < 0) {
            throw new IllegalArgumentException(
                    "La tarifa por persona no es válida.");
        }

        this.fecha = fecha;
        this.temporada = temporada;
        this.totalOcupantes = totalOcupantes;
        this.personasQuePagan = personasQuePagan;
        this.tarifaPorPersona = tarifaPorPersona;
        this.subtotal = personasQuePagan * tarifaPorPersona;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public TemporadaTipo getTemporada() {
        return temporada;
    }

    public int getTotalOcupantes() {
        return totalOcupantes;
    }

    public int getPersonasQuePagan() {
        return personasQuePagan;
    }

    public int getPersonasExentas() {
        return totalOcupantes - personasQuePagan;
    }

    public double getTarifaPorPersona() {
        return tarifaPorPersona;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "DetalleTarifaNoche{" +
                "fecha=" + fecha +
                ", temporada=" + temporada +
                ", totalOcupantes=" + totalOcupantes +
                ", personasQuePagan=" + personasQuePagan +
                ", personasExentas=" + getPersonasExentas() +
                ", tarifaPorPersona=" + tarifaPorPersona +
                ", subtotal=" + subtotal +
                '}';
    }
}
