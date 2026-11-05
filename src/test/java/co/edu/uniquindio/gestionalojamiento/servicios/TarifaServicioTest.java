package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TarifaServicioTest {

    private TarifaServicio servicio;
    private Apartamento apartamento;

    @BeforeEach
    void preparar() {
        servicio = new TarifaServicio();

        apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        servicio.registrarTemporada(new Temporada(
                "Baja", TemporadaTipo.BAJA,
                LocalDate.of(2027, 3, 1),
                LocalDate.of(2027, 3, 10),
                50000
        ));

        servicio.registrarTemporada(new Temporada(
                "Media", TemporadaTipo.MEDIA,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 20),
                70000
        ));

        servicio.registrarTemporada(new Temporada(
                "Alta", TemporadaTipo.ALTA,
                LocalDate.of(2027, 3, 20),
                LocalDate.of(2027, 4, 1),
                90000
        ));
    }

    private Huesped crearHuesped(
            String documento, LocalDate nacimiento) {

        return new Huesped(
                documento,
                "Nombre",
                "Apellido",
                "3001234567",
                documento + "@correo.com",
                nacimiento
        );
    }

    private Reserva crearReserva(
            LocalDate entrada,
            LocalDate salida,
            Huesped... huespedes) {

        ListaDoble<Huesped> ocupantes = new ListaDoble<>();

        for (Huesped huesped : huespedes) {
            ocupantes.agregar(huesped);
        }

        return new Reserva(
                "R-TEST",
                apartamento,
                ocupantes,
                entrada,
                salida,
                CanalReserva.DIRECTA
        );
    }

    @Test
    void debeCalcularCambioDeTemporada() {

        double total = servicio.calcularTotal(
                apartamento,
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                2
        );

        assertEquals(380000.0, total, 0.0001);
    }

    @Test
    void debeExonerarMenorDeSeisAnios() {

        Reserva reserva = crearReserva(
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                crearHuesped("1001", LocalDate.of(1995, 5, 10)),
                crearHuesped("1002", LocalDate.of(2023, 5, 15))
        );

        assertEquals(
                190000.0,
                servicio.calcularTotal(reserva),
                0.0001
        );
    }

    @Test
    void debeCobrarDesdeCumpleaniosSeis() {

        Reserva reserva = crearReserva(
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                crearHuesped("1003", LocalDate.of(1995, 5, 10)),
                crearHuesped("1004", LocalDate.of(2021, 3, 10))
        );

        assertEquals(
                330000.0,
                servicio.calcularTotal(reserva),
                0.0001
        );
    }

    @Test
    void debeRechazarCapacidadExcedida() {
        // A101 tiene capacidad máxima de 4 personas.
        // Incluimos cinco huéspedes, uno de ellos menor de 6 años.

        assertThrows(
                IllegalArgumentException.class,
                () -> crearReserva(
                        LocalDate.of(2027, 3, 9),
                        LocalDate.of(2027, 3, 12),
                        crearHuesped("1005", LocalDate.of(1990, 1, 1)),
                        crearHuesped("1006", LocalDate.of(1991, 1, 1)),
                        crearHuesped("1007", LocalDate.of(1992, 1, 1)),
                        crearHuesped("1008", LocalDate.of(1993, 1, 1)),
                        crearHuesped("1009", LocalDate.of(2023, 1, 1))
                )
        );
    }

    @Test
    void debeRechazarHuespedSinNacimiento() {

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3001234567", "ana@correo.com"
        );

        Reserva reserva = crearReserva(
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                huesped
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularTotal(reserva)
        );
    }

    @Test
    void debeRechazarTemporadasSolapadas() {

        Temporada repetida = new Temporada(
                "Otra media", TemporadaTipo.MEDIA,
                LocalDate.of(2027, 3, 8),
                LocalDate.of(2027, 3, 15),
                70000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarTemporada(repetida)
        );
    }

    @Test
    void debeCotizarReservaRegistrada() {

        Huesped adulto = crearHuesped(
                "2001", LocalDate.of(1995, 5, 10));

        Huesped menor = crearHuesped(
                "2002", LocalDate.of(2021, 3, 10));

        ListaDoble<Huesped> ocupantes = new ListaDoble<>();
        ocupantes.agregar(adulto);
        ocupantes.agregar(menor);

        ReservaServicio reservaServicio = new ReservaServicio();

        AsignacionGrupo grupo = new AsignacionGrupo("G-TARIFA");
        grupo.agregarHuesped(adulto);
        grupo.agregarHuesped(menor);
        grupo.agregarApartamento(apartamento);
        grupo.asignarHuesped(adulto, apartamento);
        grupo.asignarHuesped(menor, apartamento);

        reservaServicio.registrarReservaGrupal(
                "RG-TARIFA",
                grupo,
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        double total = reservaServicio.cotizarReserva(
                "RG-TARIFA-A101", servicio);

        assertEquals(330000.0, total, 0.0001);
    }

    @Test
    void reservaPortalDebeSeguirPendienteSinPago() {

        Huesped huesped = crearHuesped(
                "3001",
                LocalDate.of(1995, 5, 10)
        );

        ReservaServicio reservaServicio = new ReservaServicio();

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-01",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        // La reserva se crea pendiente.
        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );

        // Cotizar no significa pagar.
        double total = reservaServicio.cotizarReserva(
                "R-PORTAL-01", servicio
        );

        assertEquals(140000.0, total, 0.001);

        // Después de cotizar, debe continuar pendiente.
        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );

        // El anticipo todavía no se ha registrado.
        FolioServicio folio = new FolioServicio(reserva);

        assertEquals(
                0,
                folio.cantidadMovimientos()
        );
    }


    @Test
    void noDebeConfirmarReservaPortalSinAnticipo() {

        Huesped huesped = crearHuesped(
                "4001",
                LocalDate.of(1995, 5, 10)
        );

        ReservaServicio reservaServicio = new ReservaServicio();

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-02",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );

        assertThrows(
                IllegalStateException.class,
                () -> reservaServicio.confirmarReserva(
                        "R-PORTAL-02"
                )
        );

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );
    }


    @Test
    void debeConservarElMismoFolioPorReserva() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "5001",
                LocalDate.of(1995, 5, 10)
        );

        reservaServicio.registrarReserva(
                "R-FOLIO-01",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio primerFolio =
                reservaServicio.obtenerFolio("R-FOLIO-01");

        primerFolio.registrarPago(
                "P001",
                "Anticipo recibido",
                42000
        );

        FolioServicio segundoFolio =
                reservaServicio.obtenerFolio("R-FOLIO-01");

        assertSame(primerFolio, segundoFolio);

        assertEquals(
                1,
                segundoFolio.cantidadMovimientos()
        );

        assertEquals(
                -42000.0,
                segundoFolio.obtenerSaldo(),
                0.001
        );
    }


    @Test
    void debeConfirmarReservaPortalConAnticipo() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "6001",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-03",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-03");

        double anticipo =
                folio.calcularAnticipoPortal(servicio);

        assertEquals(42000.0, anticipo, 0.001);

        folio.registrarPagoAnticipoPortal(
                "P-PORTAL-03",
                servicio
        );

        reservaServicio.confirmarReserva(
                "R-PORTAL-03",
                servicio
        );

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );
    }

    @Test
    void noDebeConfirmarPortalConAnticipoRevertido() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "6002",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-04",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-04");

        folio.registrarPagoAnticipoPortal(
                "P-PORTAL-04",
                servicio
        );

        folio.deshacerUltimaOperacion("REV-PORTAL-04");

        assertEquals(
                0.0,
                folio.obtenerPagosNetos(),
                0.001
        );

        assertThrows(
                IllegalStateException.class,
                () -> reservaServicio.confirmarReserva(
                        "R-PORTAL-04",
                        servicio
                )
        );

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );
    }


    @Test
    void noDebeConfirmarPortalConPagoInsuficiente() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "7001",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-05",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-05");

        // El anticipo requerido es de 42000.
        folio.registrarPago(
                "P-PORTAL-05",
                "Pago parcial",
                20000
        );

        assertThrows(
                IllegalStateException.class,
                () -> reservaServicio.confirmarReserva(
                        "R-PORTAL-05",
                        servicio
                )
        );

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );
    }

    @Test
    void debeConfirmarPortalConPagoSuperiorAlAnticipo() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "7002",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-06",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-06");

        // Paga 50000, más del anticipo requerido.
        folio.registrarPago(
                "P-PORTAL-06",
                "Pago anticipado",
                50000
        );

        reservaServicio.confirmarReserva(
                "R-PORTAL-06",
                servicio
        );

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );
    }


    @Test
    void debeCrearReservaDirectaConfirmadaSinAnticipo() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "7003",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-DIRECTA-01",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.DIRECTA
        );

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-DIRECTA-01");

        assertEquals(
                0,
                folio.cantidadMovimientos()
        );

        assertEquals(
                0.0,
                folio.obtenerPagosNetos(),
                0.001
        );
    }


    @Test
    void debeVencerReservaPendienteDespuesDe24Horas() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "8001",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-VENCE-01",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        LocalDateTime fechaSimulada =
                reserva.getFechaCreacion().plusHours(25);

        assertFalse(
                reservaServicio.estaDisponible(
                        apartamento,
                        reserva.getFechaEntrada(),
                        reserva.getFechaSalida())
        );

        int vencidas =
                reservaServicio.vencerReservasPendientes(
                        fechaSimulada);

        assertEquals(1, vencidas);

        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstado()
        );

        assertTrue(
                reservaServicio.estaDisponible(
                        apartamento,
                        reserva.getFechaEntrada(),
                        reserva.getFechaSalida())
        );

        assertNotNull(
                reservaServicio.buscarPorCodigo("R-VENCE-01")
        );
    }

    @Test
    void noDebeVencerReservaAntesDe24Horas() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "8002",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-VENCE-02",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        LocalDateTime fechaSimulada =
                reserva.getFechaCreacion().plusHours(23);

        int vencidas =
                reservaServicio.vencerReservasPendientes(
                        fechaSimulada);

        assertEquals(0, vencidas);

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );

        assertFalse(
                reservaServicio.estaDisponible(
                        apartamento,
                        reserva.getFechaEntrada(),
                        reserva.getFechaSalida())
        );
    }


    @Test
    void noDebeConfirmarPortalDespuesDe24Horas() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "9001",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-VENCIDA",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-VENCIDA");

        // Simulamos que el anticipo está registrado.
        folio.registrarPagoAnticipoPortal(
                "P-VENCIDA",
                servicio
        );

        // La confirmación se intenta 25 horas después.
        LocalDateTime fechaSimulada =
                reserva.getFechaCreacion().plusHours(25);

        assertThrows(
                IllegalStateException.class,
                () -> reservaServicio.confirmarReserva(
                        "R-PORTAL-VENCIDA",
                        servicio,
                        fechaSimulada
                )
        );

        assertEquals(
                EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );
    }


    @Test
    void debeConfirmarPortalAntesDe24Horas() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Huesped huesped = crearHuesped(
                "9002",
                LocalDate.of(1995, 5, 10)
        );

        Reserva reserva = reservaServicio.registrarReserva(
                "R-PORTAL-VIGENTE",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-PORTAL-VIGENTE");

        folio.registrarPagoAnticipoPortal(
                "P-VIGENTE",
                servicio
        );

        // La confirmación ocurre 23 horas después.
        LocalDateTime fechaSimulada =
                reserva.getFechaCreacion().plusHours(23);

        reservaServicio.confirmarReserva(
                "R-PORTAL-VIGENTE",
                servicio,
                fechaSimulada
        );

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );
    }

}
