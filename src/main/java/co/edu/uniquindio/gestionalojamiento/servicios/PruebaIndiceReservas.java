package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class PruebaIndiceReservas {

    public static void main(String[] args) {

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        IndiceReservasApartamento indice =
                new IndiceReservasApartamento(apartamento);

        Reserva reserva1 = new Reserva(
                "R001", apartamento, huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        System.out.println("Registrar R001: "
                + indice.registrar(reserva1));

        System.out.println("Fechas solapadas libres: "
                + indice.estaLibre(
                LocalDate.of(2027, 3, 11),
                LocalDate.of(2027, 3, 14)));

        System.out.println("Fechas consecutivas libres: "
                + indice.estaLibre(
                LocalDate.of(2027, 3, 12),
                LocalDate.of(2027, 3, 14)));

        Reserva reserva2 = new Reserva(
                "R002", apartamento, huesped,
                LocalDate.of(2027, 3, 11),
                LocalDate.of(2027, 3, 14),
                CanalReserva.DIRECTA
        );

        try {
            indice.registrar(reserva2);
            System.out.println("R002 registrada sin conflicto");
        } catch (IllegalArgumentException e) {
            System.out.println("R002 rechazada por solapamiento");
        }

        System.out.println("Total reservas: "
                + indice.cantidadReservas());


        Reserva reserva3 = new Reserva(
                "R003", apartamento, huesped,
                LocalDate.of(2027, 4, 20),
                LocalDate.of(2027, 4, 23),
                CanalReserva.DIRECTA
        );

        indice.registrar(reserva3);

        System.out.println("\n--- PRUEBA DE PODA ---");

        System.out.println("Consulta sin conflicto: "
                + indice.estaLibre(
                LocalDate.of(2027, 3, 15),
                LocalDate.of(2027, 3, 18)));

        System.out.println("Conflicto con R003: "
                + indice.estaLibre(
                LocalDate.of(2027, 4, 21),
                LocalDate.of(2027, 4, 24)));

        System.out.println("Total reservas: "
                + indice.cantidadReservas());

    }
}
