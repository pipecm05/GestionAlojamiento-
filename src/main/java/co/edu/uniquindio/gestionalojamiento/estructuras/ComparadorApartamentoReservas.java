
package co.edu.uniquindio.gestionalojamiento.estructuras;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import java.util.Comparator;

public class ComparadorApartamentoReservas
        implements Comparator<Apartamento> {

    private final Iterable<Reserva> reservas;

    public ComparadorApartamentoReservas(Iterable<Reserva> reservas) {
        if (reservas == null) {
            throw new IllegalArgumentException(
                    "La lista de reservas es obligatoria."
            );
        }

        this.reservas = reservas;
    }

    @Override
    public int compare(Apartamento a1, Apartamento a2) {
        int cantidad1 = contarReservas(a1);
        int cantidad2 = contarReservas(a2);

        int resultado = Integer.compare(cantidad1, cantidad2);

        if (resultado == 0) {
            return a1.compareTo(a2);
        }

        return resultado;
    }

    private int contarReservas(Apartamento apartamento) {
        int cantidad = 0;

        for (Reserva reserva : reservas) {
            if (reserva.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {
                cantidad++;
            }
        }

        return cantidad;
    }
}
