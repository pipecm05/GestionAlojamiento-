
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import java.time.LocalDate;

public class PruebaIntegracionReservas {

    public static void main(String[] args) {

        ReservaServicio servicio = new ReservaServicio();

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        servicio.registrarReserva(
                "R001", apartamento, huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        System.out.println("R001 encontrada: "
                + (servicio.buscarPorCodigo("R001") != null));

        System.out.println("Fechas solapadas disponibles: "
                + servicio.estaDisponible(
                apartamento,
                LocalDate.of(2027, 3, 11),
                LocalDate.of(2027, 3, 14)));

        try {
            servicio.registrarReserva(
                    "R002", apartamento, huesped,
                    LocalDate.of(2027, 3, 11),
                    LocalDate.of(2027, 3, 14),
                    CanalReserva.DIRECTA
            );

            System.out.println("R002 registrada");
        } catch (IllegalArgumentException e) {
            System.out.println("R002 rechazada");
        }

        System.out.println("Total en árbol general: "
                + servicio.listarReservas().getTamanio());


        System.out.println("\n--- PRUEBA DE CANCELACIÓN ---");

        // Cancelar la reserva existente.
        servicio.cancelarReserva("R001");

        // Las fechas deben quedar disponibles.
        System.out.println("Fechas libres después de cancelar: "
                + servicio.estaDisponible(
                apartamento,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12)));

        // Registrar otra reserva en las mismas fechas.
        servicio.registrarReserva(
                "R003", apartamento, huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        System.out.println("Nueva reserva encontrada: "
                + (servicio.buscarPorCodigo("R003") != null));

        System.out.println("Reserva cancelada conservada: "
                + (servicio.buscarPorCodigo("R001") != null));

        System.out.println("Total en historial: "
                + servicio.listarReservas().getTamanio());

    }
}
