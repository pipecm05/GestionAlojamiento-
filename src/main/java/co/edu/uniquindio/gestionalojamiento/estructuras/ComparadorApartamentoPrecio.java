
package co.edu.uniquindio.gestionalojamiento.estructuras;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import java.util.Comparator;

public class ComparadorApartamentoPrecio
        implements Comparator<Apartamento> {

    @Override
    public int compare(Apartamento a1, Apartamento a2) {
        int resultado = Double.compare(
                a1.obtenerTarifa(
                        co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo.BAJA
                ),
                a2.obtenerTarifa(
                        co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo.BAJA
                )
        );

        if (resultado == 0) {
            return a1.compareTo(a2);
        }

        return resultado;
    }
}
