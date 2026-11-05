
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.MovimientoFolio;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class PruebaFolioServicio {

    public static void main(String[] args) {

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3001234567", "ana@correo.com",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = new Reserva(
                "R001",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio = new FolioServicio(reserva);

        System.out.println("--- MOVIMIENTOS DEL FOLIO ---");

        folio.registrarCargo(
                "M001", "Valor de alojamiento", 140000);

        System.out.println("Saldo después del cargo: "
                + folio.obtenerSaldo());

        folio.registrarPago(
                "M002", "Anticipo del huésped", 42000);

        System.out.println("Saldo después del pago: "
                + folio.obtenerSaldo());

        folio.deshacerUltimaOperacion("M003");

        System.out.println("Saldo después de deshacer el pago: "
                + folio.obtenerSaldo());

        System.out.println("Total movimientos: "
                + folio.cantidadMovimientos());

        System.out.println("\n--- HISTORIAL ---");

        for (MovimientoFolio movimiento :
                folio.listarMovimientos()) {

            System.out.println(
                    movimiento.getCodigo()
                            + ": " + movimiento.getValor());
        }
    }
}
