package co.edu.uniquindio.gestionalojamiento.datos;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTurno;
import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Temporada;
import co.edu.uniquindio.gestionalojamiento.modelo.Turno;

import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TurnoServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatosPrueba {

    private DatosPrueba() {
    }

    /**
     * Carga información ficticia para probar los módulos
     * del sistema de gestión de alojamiento.
     *
     * La carga se realiza únicamente cuando los catálogos
     * principales están vacíos.
     */
    public static void cargarDatos(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            TarifaServicio tarifaServicio,
            EmpleadoServicio empleadoServicio,
            TurnoServicio turnoServicio,
            FavoritoServicio favoritoServicio
    ) {

        if (catalogo == null
                || reservaServicio == null
                || tarifaServicio == null
                || empleadoServicio == null
                || turnoServicio == null
                || favoritoServicio == null) {

            throw new IllegalArgumentException(
                    "Los servicios para cargar los datos son obligatorios."
            );
        }

        // Evitar cargar los mismos registros más de una vez.
        if (catalogo.cantidadHuespedes() > 0
                || catalogo.cantidadApartamentos() > 0) {

            System.out.println(
                    "Los datos de prueba ya existen. "
                            + "No se realizará otra carga."
            );

            return;
        }

        LocalDate hoy = LocalDate.now();

        cargarHuespedes(catalogo, hoy);
        cargarApartamentos(catalogo);
        cargarTemporadas(tarifaServicio, hoy);

        cargarEmpleados(empleadoServicio);
        cargarTurnos(empleadoServicio, turnoServicio, hoy);

        cargarReservas(catalogo, reservaServicio, hoy);

        cargarFavoritos(
                catalogo,
                favoritoServicio
        );

        System.out.println("------------------------------------");
        System.out.println("DATOS DE PRUEBA CARGADOS");
        System.out.println("Huéspedes: "
                + catalogo.cantidadHuespedes());
        System.out.println("Apartamentos: "
                + catalogo.cantidadApartamentos());
        System.out.println("Reservas: "
                + reservaServicio.listarReservas().getTamanio());
        System.out.println("Temporadas: "
                + tarifaServicio.cantidadTemporadas());
        System.out.println("Empleados: "
                + empleadoServicio.cantidadEmpleados());
        System.out.println("Turnos: "
                + turnoServicio.cantidadTurnos());
        System.out.println("Favoritos: "
                + favoritoServicio.cantidadTotalFavoritos());
        System.out.println("------------------------------------");
    }

    private static void cargarHuespedes(
            CatalogoAlojamiento catalogo,
            LocalDate hoy
    ) {

        // Familia: dos adultos y dos menores.
        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H001",
                        "Carlos",
                        "Ramirez",
                        "3001112233",
                        "carlos.prueba@email.com",
                        hoy.minusYears(35)
                )
        );

        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H002",
                        "Laura",
                        "Gomez",
                        "3002223344",
                        "laura.prueba@email.com",
                        hoy.minusYears(32)
                )
        );

        // Menor de seis años.
        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H003",
                        "Sofia",
                        "Ramirez",
                        "3003334455",
                        "sofia.prueba@email.com",
                        hoy.minusYears(4)
                )
        );

        // Menor de seis años.
        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H004",
                        "Mateo",
                        "Ramirez",
                        "3004445566",
                        "mateo.prueba@email.com",
                        hoy.minusYears(2)
                )
        );

        // Pareja.
        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H005",
                        "Andres",
                        "Lopez",
                        "3005556677",
                        "andres.prueba@email.com",
                        hoy.minusYears(28)
                )
        );

        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H006",
                        "Daniela",
                        "Torres",
                        "3006667788",
                        "daniela.prueba@email.com",
                        hoy.minusYears(27)
                )
        );

        // Huésped individual.
        catalogo.registrarHuesped(
                new Huesped(
                        "PRU-H007",
                        "Juan",
                        "Martinez",
                        "3007778899",
                        "juan.prueba@email.com",
                        hoy.minusYears(24)
                )
        );

        System.out.println("Huéspedes de prueba registrados.");
    }

    private static void cargarApartamentos(
            CatalogoAlojamiento catalogo
    ) {

        catalogo.registrarApartamento(
                new Apartamento(
                        "PRU-A001",
                        "Apartamento Familiar",
                        4,
                        2,
                        180000,
                        220000,
                        280000
                )
        );

        catalogo.registrarApartamento(
                new Apartamento(
                        "PRU-A002",
                        "Apartamento Pareja",
                        2,
                        1,
                        140000,
                        170000,
                        210000
                )
        );

        catalogo.registrarApartamento(
                new Apartamento(
                        "PRU-A003",
                        "Apartamento Individual",
                        2,
                        1,
                        100000,
                        130000,
                        160000
                )
        );

        catalogo.registrarApartamento(
                new Apartamento(
                        "PRU-A004",
                        "Apartamento Familiar Grande",
                        6,
                        3,
                        250000,
                        300000,
                        380000
                )
        );

        System.out.println("Apartamentos de prueba registrados.");
    }

    private static void cargarTemporadas(
            TarifaServicio tarifaServicio,
            LocalDate hoy
    ) {

        tarifaServicio.registrarTemporada(
                new Temporada(
                        "Temporada Baja Prueba",
                        TemporadaTipo.BAJA,
                        hoy.minusDays(365),
                        hoy,
                        50000
                )
        );

        tarifaServicio.registrarTemporada(
                new Temporada(
                        "Temporada Media Prueba",
                        TemporadaTipo.MEDIA,
                        hoy,
                        hoy.plusDays(120),
                        70000
                )
        );

        tarifaServicio.registrarTemporada(
                new Temporada(
                        "Temporada Alta Prueba",
                        TemporadaTipo.ALTA,
                        hoy.plusDays(120),
                        hoy.plusDays(365),
                        100000
                )
        );

        System.out.println("Temporadas de prueba registradas.");
    }

    private static void cargarEmpleados(
            EmpleadoServicio empleadoServicio
    ) {

        empleadoServicio.registrarEmpleado(
                new Empleado(
                        "PRU-E001",
                        "Maria",
                        "Fernandez",
                        "3101112233",
                        "maria.empleada@email.com",
                        TipoEmpleado.RECEPCION
                )
        );

        empleadoServicio.registrarEmpleado(
                new Empleado(
                        "PRU-E002",
                        "Pedro",
                        "Sanchez",
                        "3102223344",
                        "pedro.empleado@email.com",
                        TipoEmpleado.LIMPIEZA
                )
        );

        empleadoServicio.registrarEmpleado(
                new Empleado(
                        "PRU-E003",
                        "Camila",
                        "Rodriguez",
                        "3103334455",
                        "camila.empleada@email.com",
                        TipoEmpleado.MANTENIMIENTO
                )
        );

        System.out.println("Empleados de prueba registrados.");
    }

    private static void cargarTurnos(
            EmpleadoServicio empleadoServicio,
            TurnoServicio turnoServicio,
            LocalDate hoy
    ) {

        Empleado recepcion =
                empleadoServicio.buscarEmpleado("PRU-E001");

        Empleado limpieza =
                empleadoServicio.buscarEmpleado("PRU-E002");

        Empleado mantenimiento =
                empleadoServicio.buscarEmpleado("PRU-E003");

        turnoServicio.registrarTurno(
                new Turno(
                        "PRU-T001",
                        recepcion,
                        hoy.plusDays(1).atTime(7, 0),
                        hoy.plusDays(1).atTime(15, 0),
                        TipoTurno.MANANA
                )
        );

        turnoServicio.registrarTurno(
                new Turno(
                        "PRU-T002",
                        limpieza,
                        hoy.plusDays(1).atTime(8, 0),
                        hoy.plusDays(1).atTime(16, 0),
                        TipoTurno.MANANA
                )
        );

        turnoServicio.registrarTurno(
                new Turno(
                        "PRU-T003",
                        mantenimiento,
                        hoy.plusDays(1).atTime(14, 0),
                        hoy.plusDays(1).atTime(22, 0),
                        TipoTurno.TARDE
                )
        );

        System.out.println("Turnos de prueba registrados.");
    }

    private static void cargarReservas(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            LocalDate hoy
    ) {

        Huesped carlos =
                catalogo.buscarHuesped("PRU-H001");

        Huesped laura =
                catalogo.buscarHuesped("PRU-H002");

        Huesped sofia =
                catalogo.buscarHuesped("PRU-H003");

        Huesped mateo =
                catalogo.buscarHuesped("PRU-H004");

        Huesped andres =
                catalogo.buscarHuesped("PRU-H005");

        Huesped daniela =
                catalogo.buscarHuesped("PRU-H006");

        Huesped juan =
                catalogo.buscarHuesped("PRU-H007");

        Apartamento familiar =
                catalogo.buscarApartamento("PRU-A001");

        Apartamento pareja =
                catalogo.buscarApartamento("PRU-A002");

        Apartamento individual =
                catalogo.buscarApartamento("PRU-A003");

        // Reserva familiar: dos adultos y dos menores.
        AsignacionGrupo grupoFamiliar =
                new AsignacionGrupo("PRU-G001");

        grupoFamiliar.agregarHuesped(carlos);
        grupoFamiliar.agregarHuesped(laura);
        grupoFamiliar.agregarHuesped(sofia);
        grupoFamiliar.agregarHuesped(mateo);

        grupoFamiliar.agregarApartamento(familiar);

        grupoFamiliar.asignarHuesped(carlos, familiar);
        grupoFamiliar.asignarHuesped(laura, familiar);
        grupoFamiliar.asignarHuesped(sofia, familiar);
        grupoFamiliar.asignarHuesped(mateo, familiar);

        ListaDoble<co.edu.uniquindio.gestionalojamiento.modelo.Reserva>
                reservasFamiliares =
                reservaServicio.registrarReservaGrupal(
                        "PRU-R001",
                        grupoFamiliar,
                        hoy.plusDays(10),
                        hoy.plusDays(13),
                        CanalReserva.DIRECTA
                );

        // Reserva para una pareja.
        reservaServicio.registrarReserva(
                "PRU-R002",
                pareja,
                andres,
                hoy.plusDays(15),
                hoy.plusDays(18),
                CanalReserva.DIRECTA
        );

        // Reserva individual.
        reservaServicio.registrarReserva(
                "PRU-R003",
                individual,
                juan,
                hoy.plusDays(20),
                hoy.plusDays(22),
                CanalReserva.DIRECTA
        );

        System.out.println(
                "Reservas de prueba registradas. "
                        + "Reservas familiares creadas: "
                        + reservasFamiliares.getTamanio()
        );
    }

    private static void cargarFavoritos(
            CatalogoAlojamiento catalogo,
            FavoritoServicio favoritoServicio
    ) {

        Huesped carlos =
                catalogo.buscarHuesped("PRU-H001");

        Huesped andres =
                catalogo.buscarHuesped("PRU-H005");

        Apartamento familiar =
                catalogo.buscarApartamento("PRU-A001");

        Apartamento pareja =
                catalogo.buscarApartamento("PRU-A002");

        Apartamento grande =
                catalogo.buscarApartamento("PRU-A004");

        favoritoServicio.agregarFavorito(
                carlos,
                familiar
        );

        favoritoServicio.agregarFavorito(
                carlos,
                grande
        );

        favoritoServicio.agregarFavorito(
                andres,
                pareja
        );

        System.out.println("Favoritos de prueba registrados.");
    }
}
