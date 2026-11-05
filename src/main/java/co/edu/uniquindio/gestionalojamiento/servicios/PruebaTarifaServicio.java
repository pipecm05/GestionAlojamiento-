package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Temporada;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;

import java.time.LocalDate;

public class PruebaTarifaServicio {

    public static void main(String[] args) {

        TarifaServicio servicio = new TarifaServicio();

        Temporada baja = new Temporada(
                "Temporada baja",
                TemporadaTipo.BAJA,
                LocalDate.of(2027, 3, 1),
                LocalDate.of(2027, 3, 10),
                50000
        );

        Temporada media = new Temporada(
                "Temporada media",
                TemporadaTipo.MEDIA,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 20),
                70000
        );

        Temporada alta = new Temporada(
                "Temporada alta",
                TemporadaTipo.ALTA,
                LocalDate.of(2027, 3, 20),
                LocalDate.of(2027, 4, 1),
                90000
        );

        System.out.println("Registrar baja: "
                + servicio.registrarTemporada(baja));

        System.out.println("Registrar media: "
                + servicio.registrarTemporada(media));

        System.out.println("Registrar alta: "
                + servicio.registrarTemporada(alta));

        System.out.println("Temporada del 5 de marzo: "
                + servicio.buscarTemporada(
                LocalDate.of(2027, 3, 5)).getTipo());

        System.out.println("Temporada del 10 de marzo: "
                + servicio.buscarTemporada(
                LocalDate.of(2027, 3, 10)).getTipo());

        System.out.println("Temporada del 25 de marzo: "
                + servicio.buscarTemporada(
                LocalDate.of(2027, 3, 25)).getTipo());

        System.out.println("Total temporadas: "
                + servicio.cantidadTemporadas());

        try {
            Temporada repetida = new Temporada(
                    "Temporada superpuesta",
                    TemporadaTipo.MEDIA,
                    LocalDate.of(2027, 3, 8),
                    LocalDate.of(2027, 3, 15),
                    70000
            );

            servicio.registrarTemporada(repetida);
            System.out.println("Solapamiento aceptado");

        } catch (IllegalArgumentException e) {
            System.out.println("Solapamiento rechazado");
        }


        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        System.out.println("\n--- LIQUIDACIÓN RECURSIVA ---");

        double total = servicio.calcularTotal(
                apartamento,
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                2
        );

        System.out.println("Total de la estancia: " + total);


        Huesped nino = new Huesped(
                "1001",
                "Juan",
                "Perez",
                "3001234567",
                "juan@correo.com",
                LocalDate.of(2023, 5, 15)
        );

        Huesped adulto = new Huesped(
                "1002",
                "Carlos",
                "Perez",
                "3007654321",
                "carlos@correo.com",
                LocalDate.of(1995, 8, 10)
        );

        LocalDate fechaEstancia = LocalDate.of(2027, 3, 10);

        System.out.println("\n--- EDAD DE LOS HUÉSPEDES ---");

        System.out.println("Edad de Juan: "
                + nino.obtenerEdadEn(fechaEstancia));

        System.out.println("Juan es menor de 6: "
                + nino.esMenorDeSeisEn(fechaEstancia));

        System.out.println("Carlos es menor de 6: "
                + adulto.esMenorDeSeisEn(fechaEstancia));



        System.out.println("\n--- TARIFA AUTOMÁTICA POR HUÉSPEDES ---");

        Huesped adultoReserva = new Huesped(
                "2001",
                "Ana",
                "Lopez",
                "3001111111",
                "ana@correo.com",
                LocalDate.of(1995, 5, 10)
        );

        Huesped menorReserva = new Huesped(
                "2002",
                "Luis",
                "Lopez",
                "3002222222",
                "luis@correo.com",
                LocalDate.of(2021, 3, 10)
        );

        ListaDoble<Huesped> ocupantes = new ListaDoble<>();

        ocupantes.agregar(adultoReserva);
        ocupantes.agregar(menorReserva);

        Reserva reservaFamiliar = new Reserva(
                "R-TARIFA-01",
                apartamento,
                ocupantes,
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        System.out.println("Total de ocupantes: "
                + reservaFamiliar.getCantidadHuespedes());

        System.out.println("Luis es menor de 6 el 9 de marzo: "
                + menorReserva.esMenorDeSeisEn(
                LocalDate.of(2027, 3, 9)));

        System.out.println("Luis es menor de 6 el 10 de marzo: "
                + menorReserva.esMenorDeSeisEn(
                LocalDate.of(2027, 3, 10)));

        System.out.println("Total automático: "
                + servicio.calcularTotal(reservaFamiliar));

    }
}
