package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
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
}

