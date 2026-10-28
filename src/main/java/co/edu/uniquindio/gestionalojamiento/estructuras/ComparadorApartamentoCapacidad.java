
package co.edu.uniquindio.gestionalojamiento.estructuras;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import java.util.Comparator;

public class ComparadorApartamentoCapacidad
        implements Comparator<Apartamento> {

    @Override
    public int compare(Apartamento a1, Apartamento a2) {
        int resultado = Integer.compare(
                a1.getCapacidadMaxima(),
                a2.getCapacidadMaxima()
        );

        if (resultado == 0) {
            return a1.compareTo(a2);
        }

        return resultado;
    }
}
