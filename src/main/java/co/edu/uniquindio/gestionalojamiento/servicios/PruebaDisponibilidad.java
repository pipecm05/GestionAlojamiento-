package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class PruebaDisponibilidad {

    public static void main(String[] args) {

        DisponibilidadServicio servicio =
                new DisponibilidadServicio();

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        Apartamento apto1 = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Apartamento apto2 = new Apartamento(
                "A102", "Suite Mirador",
                4, 2, 60000, 80000, 100000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        Reserva reserva = new Reserva(
                "R001", apto1, huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        reservas.insertar(reserva);

        System.out.println("--- PRUEBAS DE DISPONIBILIDAD ---");

        // Caso 1: fechas solapadas
        System.out.println("Caso 1 - Solapamiento: " +
                servicio.estaDisponible(
                        apto1,
                        LocalDate.of(2027, 3, 11),
                        LocalDate.of(2027, 3, 14),
                        reservas
                ));

        // Caso 2: entrada el mismo día de una salida
        System.out.println("Caso 2 - Fechas consecutivas: " +
                servicio.estaDisponible(
                        apto1,
                        LocalDate.of(2027, 3, 12),
                        LocalDate.of(2027, 3, 14),
                        reservas
                ));

        // Caso 3: otro apartamento en las mismas fechas
        System.out.println("Caso 3 - Otro apartamento: " +
                servicio.estaDisponible(
                        apto2,
                        LocalDate.of(2027, 3, 11),
                        LocalDate.of(2027, 3, 14),
                        reservas
                ));

        // Caso 4: apartamento ocupado actualmente,
        // pero sin reservas en las fechas consultadas
        apto1.cambiarEstado(EstadoApartamento.OCUPADO, null);

        System.out.println("Caso 4 - Ocupado, fechas futuras: " +
                servicio.estaDisponible(
                        apto1,
                        LocalDate.of(2027, 5, 10),
                        LocalDate.of(2027, 5, 12),
                        reservas
                ));

        // Caso 5: una reserva cancelada no ocupa noches
        apto1.cambiarEstado(EstadoApartamento.PREPARADO, null);
        reserva.cambiarEstado(EstadoReserva.CANCELADA);

        System.out.println("Caso 5 - Reserva cancelada: " +
                servicio.estaDisponible(
                        apto1,
                        LocalDate.of(2027, 3, 11),
                        LocalDate.of(2027, 3, 14),
                        reservas
                ));
    }
}
