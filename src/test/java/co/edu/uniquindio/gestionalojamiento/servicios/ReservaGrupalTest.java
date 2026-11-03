package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservaGrupalTest {

    private ReservaServicio reservaServicio;
    private Apartamento apartamento1;
    private Apartamento apartamento2;
    private Huesped huesped1;
    private Huesped huesped2;
    private Huesped huesped3;

    @BeforeEach
    void prepararDatos() {
        reservaServicio = new ReservaServicio();

        apartamento1 = new Apartamento(
                "A01", "Apartamento uno",
                2, 1, 100000, 150000, 200000);

        apartamento2 = new Apartamento(
                "A02", "Apartamento dos",
                3, 2, 120000, 180000, 250000);

        huesped1 = new Huesped(
                "1001", "Ana", "Gómez",
                "3001111111", "ana@correo.com");

        huesped2 = new Huesped(
                "1002", "Luis", "Pérez",
                "3002222222", "luis@correo.com");

        huesped3 = new Huesped(
                "1003", "María", "López",
                "3003333333", "maria@correo.com");
    }

    @Test
    void debeCrearUnaReservaConVariosHuespedes() {
        AsignacionGrupo grupo = new AsignacionGrupo("G01");

        grupo.agregarHuesped(huesped1);
        grupo.agregarHuesped(huesped2);
        grupo.agregarApartamento(apartamento1);

        grupo.asignarHuesped(huesped1, apartamento1);
        grupo.asignarHuesped(huesped2, apartamento1);

        ListaDoble<Reserva> resultado =
                reservaServicio.registrarReservaGrupal(
                        "RG01",
                        grupo,
                        LocalDate.of(2026, 11, 10),
                        LocalDate.of(2026, 11, 12),
                        CanalReserva.DIRECTA);

        assertEquals(1, resultado.getTamanio());

        Reserva reserva = resultado.obtenerPrimero();

        assertEquals("A01", reserva.getApartamento().getCodigo());
        assertEquals(2, reserva.getCantidadHuespedes());
        assertEquals("RG01-A01", reserva.getCodigo());
    }

    @Test
    void debeCrearUnaReservaPorCadaApartamento() {
        AsignacionGrupo grupo = new AsignacionGrupo("G02");

        grupo.agregarHuesped(huesped1);
        grupo.agregarHuesped(huesped2);
        grupo.agregarHuesped(huesped3);

        grupo.agregarApartamento(apartamento1);
        grupo.agregarApartamento(apartamento2);

        grupo.asignarHuesped(huesped1, apartamento1);
        grupo.asignarHuesped(huesped2, apartamento1);
        grupo.asignarHuesped(huesped3, apartamento2);

        ListaDoble<Reserva> resultado =
                reservaServicio.registrarReservaGrupal(
                        "RG02",
                        grupo,
                        LocalDate.of(2026, 11, 15),
                        LocalDate.of(2026, 11, 18),
                        CanalReserva.DIRECTA);

        assertEquals(2, resultado.getTamanio());
        assertEquals(2, reservaServicio.listarReservas().getTamanio());

        Reserva primera = resultado.obtenerPrimero();
        Reserva segunda = resultado.obtenerUltimo();

        assertEquals(2, primera.getCantidadHuespedes());
        assertEquals(1, segunda.getCantidadHuespedes());
    }

    @Test
    void noDebeRegistrarElGrupoSiUnApartamentoNoEstaDisponible() {
        reservaServicio.registrarReserva(
                "R001",
                apartamento1,
                huesped1,
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 15),
                CanalReserva.DIRECTA);

        AsignacionGrupo grupo = new AsignacionGrupo("G03");

        grupo.agregarHuesped(huesped2);
        grupo.agregarHuesped(huesped3);
        grupo.agregarApartamento(apartamento2);
        grupo.agregarApartamento(apartamento1);

        grupo.asignarHuesped(huesped2, apartamento2);
        grupo.asignarHuesped(huesped3, apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> reservaServicio.registrarReservaGrupal(
                        "RG03",
                        grupo,
                        LocalDate.of(2026, 11, 12),
                        LocalDate.of(2026, 11, 14),
                        CanalReserva.DIRECTA)
        );

        assertEquals(1, reservaServicio.listarReservas().getTamanio());
    }

    @Test
    void noDebePermitirFechasInvalidas() {
        AsignacionGrupo grupo = new AsignacionGrupo("G04");

        grupo.agregarHuesped(huesped1);
        grupo.agregarApartamento(apartamento1);
        grupo.asignarHuesped(huesped1, apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> reservaServicio.registrarReservaGrupal(
                        "RG04",
                        grupo,
                        LocalDate.of(2026, 11, 20),
                        LocalDate.of(2026, 11, 20),
                        CanalReserva.DIRECTA)
        );

        assertEquals(0, reservaServicio.listarReservas().getTamanio());
    }


    @Test
    void reservaGrupalDebeBloquearReservaIndividual() {

        AsignacionGrupo grupo = new AsignacionGrupo("G05");

        grupo.agregarHuesped(huesped1);
        grupo.agregarApartamento(apartamento1);
        grupo.asignarHuesped(huesped1, apartamento1);

        reservaServicio.registrarReservaGrupal(
                "RG05",
                grupo,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 15),
                CanalReserva.DIRECTA
        );

        // Una reserva individual no puede ocupar
        // las mismas noches del apartamento.
        assertThrows(
                IllegalArgumentException.class,
                () -> reservaServicio.registrarReserva(
                        "R005",
                        apartamento1,
                        huesped2,
                        LocalDate.of(2027, 3, 12),
                        LocalDate.of(2027, 3, 16),
                        CanalReserva.DIRECTA
                )
        );

        assertEquals(
                1,
                reservaServicio.listarReservas().getTamanio()
        );
    }

    @Test
    void cancelarReservaGrupalDebeLiberarFechas() {

        AsignacionGrupo grupo = new AsignacionGrupo("G06");

        grupo.agregarHuesped(huesped1);
        grupo.agregarApartamento(apartamento1);
        grupo.asignarHuesped(huesped1, apartamento1);

        reservaServicio.registrarReservaGrupal(
                "RG06",
                grupo,
                LocalDate.of(2027, 4, 10),
                LocalDate.of(2027, 4, 15),
                CanalReserva.DIRECTA
        );

        // Cancelamos la reserva de ese apartamento.
        reservaServicio.cancelarReserva("RG06-A01");

        // Las mismas noches pueden reservarse nuevamente.
        assertDoesNotThrow(
                () -> reservaServicio.registrarReserva(
                        "R006",
                        apartamento1,
                        huesped2,
                        LocalDate.of(2027, 4, 10),
                        LocalDate.of(2027, 4, 15),
                        CanalReserva.DIRECTA
                )
        );

        // Se conserva el historial de ambas reservas.
        assertEquals(
                2,
                reservaServicio.listarReservas().getTamanio()
        );
    }


    @Test
    void debeRechazarApartamentosRepetidosEnGrupo() {

        AsignacionGrupo grupo = new AsignacionGrupo("G07");

        grupo.agregarApartamento(apartamento1);

        // Intentar agregar nuevamente el mismo apartamento.
        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.agregarApartamento(apartamento1)
        );

        // Verificar que el duplicado no fue agregado.
        assertEquals(
                1,
                grupo.getApartamentos().getTamanio()
        );

        // No se ha registrado ninguna reserva.
        assertEquals(
                0,
                reservaServicio.listarReservas().getTamanio()
        );
    }

    @Test
    void reservaFinalizadaDebeConservarHistorial() {

        reservaServicio.registrarReserva(
                "R-FIN",
                apartamento1,
                huesped1,
                LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 12),
                CanalReserva.DIRECTA
        );

        // La reserva directa inicia confirmada.
        reservaServicio.registrarCheckIn("R-FIN");
        reservaServicio.registrarCheckOut("R-FIN");

        Reserva reserva =
                reservaServicio.buscarPorCodigo("R-FIN");

        assertNotNull(reserva);

        assertEquals(
                EstadoReserva.FINALIZADA,
                reserva.getEstado()
        );

        assertEquals(
                EstadoApartamento.PENDIENTE_PREPARACION,
                apartamento1.getEstado()
        );

        // Las fechas posteriores están libres de reservas.
        assertTrue(
                reservaServicio.estaDisponible(
                        apartamento1,
                        LocalDate.of(2027, 6, 15),
                        LocalDate.of(2027, 6, 17)
                )
        );

        // La reserva finalizada sigue registrada.
        assertEquals(
                1,
                reservaServicio.listarReservas().getTamanio()
        );
    }


    @Test
    void reservaFinalizadaDebeBloquearFechasHistoricas() {

        reservaServicio.registrarReserva(
                "R-HIST-01",
                apartamento1,
                huesped1,
                LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 12),
                CanalReserva.DIRECTA
        );

        reservaServicio.registrarCheckIn("R-HIST-01");
        reservaServicio.registrarCheckOut("R-HIST-01");

        // Intentar registrar otra reserva sobre
        // una noche que ya fue ocupada.
        assertThrows(
                IllegalArgumentException.class,
                () -> reservaServicio.registrarReserva(
                        "R-HIST-02",
                        apartamento1,
                        huesped2,
                        LocalDate.of(2027, 6, 11),
                        LocalDate.of(2027, 6, 13),
                        CanalReserva.DIRECTA
                )
        );

        // La reserva original sigue en el historial.
        assertEquals(
                1,
                reservaServicio.listarReservas().getTamanio()
        );
    }


    @Test
    void noDebeRegistrarGrupoSiUnApartamentoEstaOcupado() {

        // Primero registramos una reserva existente en A02.
        reservaServicio.registrarReserva(
                "R-EXISTENTE",
                apartamento2,
                huesped2,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 15),
                CanalReserva.DIRECTA
        );

        // El grupo necesita dos apartamentos.
        AsignacionGrupo grupo = new AsignacionGrupo("G08");

        grupo.agregarHuesped(huesped1);
        grupo.agregarHuesped(huesped2);

        grupo.agregarApartamento(apartamento1);
        grupo.agregarApartamento(apartamento2);

        grupo.asignarHuesped(huesped1, apartamento1);
        grupo.asignarHuesped(huesped2, apartamento2);

        // A01 está libre, pero A02 tiene un conflicto.
        assertThrows(
                IllegalArgumentException.class,
                () -> reservaServicio.registrarReservaGrupal(
                        "RG08",
                        grupo,
                        LocalDate.of(2027, 7, 12),
                        LocalDate.of(2027, 7, 17),
                        CanalReserva.DIRECTA
                )
        );

        // No debe existir ninguna reserva del grupo.
        assertNull(reservaServicio.buscarPorCodigo("RG08-A01"));
        assertNull(reservaServicio.buscarPorCodigo("RG08-A02"));

        // Solo permanece la reserva que ya existía.
        assertEquals(
                1,
                reservaServicio.listarReservas().getTamanio()
        );

        // A01 debe continuar disponible porque el grupo fue rechazado.
        assertTrue(
                reservaServicio.estaDisponible(
                        apartamento1,
                        LocalDate.of(2027, 7, 12),
                        LocalDate.of(2027, 7, 17)
                )
        );

        // A02 debe seguir ocupado por la reserva original.
        assertFalse(
                reservaServicio.estaDisponible(
                        apartamento2,
                        LocalDate.of(2027, 7, 12),
                        LocalDate.of(2027, 7, 17)
                )
        );

        // Verificar que la reserva original permanece.
        assertNotNull(
                reservaServicio.buscarPorCodigo("R-EXISTENTE")
        );
    }

}

