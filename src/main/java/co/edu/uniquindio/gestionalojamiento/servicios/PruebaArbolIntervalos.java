package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class PruebaArbolIntervalos {

    public static void main(String[] args) {

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        ArbolIntervalosReserva arbol = new ArbolIntervalosReserva();

        Reserva r1 = new Reserva(
                "R001", apartamento, huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        Reserva r2 = new Reserva(
                "R002", apartamento, huesped,
                LocalDate.of(2027, 3, 20),
                LocalDate.of(2027, 3, 25),
                CanalReserva.DIRECTA
        );

        System.out.println("Insertar R001: " + arbol.insertar(r1));
        System.out.println("Insertar R002: " + arbol.insertar(r2));

        System.out.println("Conflicto 11-14 marzo: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 11),
                LocalDate.of(2027, 3, 14)) != null));

        System.out.println("Conflicto 12-15 marzo: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 12),
                LocalDate.of(2027, 3, 15)) != null));

        System.out.println("Conflicto 21-24 marzo: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 21),
                LocalDate.of(2027, 3, 24)) != null));

        System.out.println("Total reservas: " + arbol.getTamanio());


        System.out.println("\n--- PRUEBA DE ACTUALIZACIÓN DE FECHA MÁXIMA ---");

        Reserva r3 = new Reserva(
                "R003", apartamento, huesped,
                LocalDate.of(2027, 3, 28),
                LocalDate.of(2027, 4, 2),
                CanalReserva.DIRECTA
        );

        System.out.println("Insertar R003: "
                + arbol.insertar(r3));

        // La nueva reserva termina después
        // de las fechas máximas anteriores.
        System.out.println("Conflicto 30 marzo - 1 abril: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 30),
                LocalDate.of(2027, 4, 1)) != null));

        // Estas fechas están entre dos reservas.
        System.out.println("Conflicto 26-27 marzo: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 26),
                LocalDate.of(2027, 3, 27)) != null));

        System.out.println("Total reservas: "
                + arbol.getTamanio());


        System.out.println("\n--- PRUEBA DE ELIMINACIÓN ---");

        System.out.println("Eliminar R003: " + arbol.eliminar(r3));

        System.out.println("Conflicto 30 marzo - 1 abril: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 30),
                LocalDate.of(2027, 4, 1)) != null));

        System.out.println("Conflicto 21-24 marzo: "
                + (arbol.buscarConflicto(
                LocalDate.of(2027, 3, 21),
                LocalDate.of(2027, 3, 24)) != null));

        System.out.println("Total después de eliminar: "
                + arbol.getTamanio());


        System.out.println("\n--- ELIMINACIÓN DE NODO CON DOS HIJOS ---");

        ArbolIntervalosReserva arbolDosHijos =
                new ArbolIntervalosReserva();

        Reserva central = new Reserva(
                "R010", apartamento, huesped,
                LocalDate.of(2027, 5, 20),
                LocalDate.of(2027, 5, 22),
                CanalReserva.DIRECTA
        );

        Reserva izquierda = new Reserva(
                "R011", apartamento, huesped,
                LocalDate.of(2027, 5, 10),
                LocalDate.of(2027, 5, 12),
                CanalReserva.DIRECTA
        );

        Reserva derecha = new Reserva(
                "R012", apartamento, huesped,
                LocalDate.of(2027, 5, 30),
                LocalDate.of(2027, 6, 2),
                CanalReserva.DIRECTA
        );

        arbolDosHijos.insertar(central);
        arbolDosHijos.insertar(izquierda);
        arbolDosHijos.insertar(derecha);

        System.out.println("Eliminar nodo central: "
                + arbolDosHijos.eliminar(central));

        System.out.println("Nodo central encontrado: "
                + (arbolDosHijos.buscar(central) != null));

        System.out.println("Conflicto izquierda: "
                + (arbolDosHijos.buscarConflicto(
                LocalDate.of(2027, 5, 11),
                LocalDate.of(2027, 5, 12)) != null));

        System.out.println("Conflicto derecha: "
                + (arbolDosHijos.buscarConflicto(
                LocalDate.of(2027, 5, 31),
                LocalDate.of(2027, 6, 1)) != null));

        System.out.println("Total final: "
                + arbolDosHijos.getTamanio());


        System.out.println(
                "\n--- VERIFICACIÓN DE FECHA MÁXIMA ---"
        );

        // Después de eliminar la reserva central,
        // la fecha máxima corresponde a R012.
        System.out.println("Máxima después de eliminar R010: "
                + arbolDosHijos.obtenerMaximaFechaSalida());

        // Ahora eliminamos R012.
        System.out.println("Eliminar R012: "
                + arbolDosHijos.eliminar(derecha));

        // Solo debe quedar R011.
        System.out.println("Nueva fecha máxima: "
                + arbolDosHijos.obtenerMaximaFechaSalida());

        System.out.println("Conflicto 31 mayo - 1 junio: "
                + (arbolDosHijos.buscarConflicto(
                LocalDate.of(2027, 5, 31),
                LocalDate.of(2027, 6, 1)) != null));

        System.out.println("Conflicto 11-12 mayo: "
                + (arbolDosHijos.buscarConflicto(
                LocalDate.of(2027, 5, 11),
                LocalDate.of(2027, 5, 12)) != null));

        System.out.println("Total reservas restantes: "
                + arbolDosHijos.getTamanio());


    }
}
