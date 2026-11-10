package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uniquindio.gestionalojamiento.modelo.DetalleTarifaNoche;
import java.util.Iterator;

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


    @Test
    void debeProcesarVencimientosConColaPrioridad() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Reserva primera = reservaServicio.registrarReserva(
                "R-HEAP-01",
                apartamento,
                crearHuesped("9101", LocalDate.of(1990, 1, 1)),
                LocalDate.of(2027, 5, 1),
                LocalDate.of(2027, 5, 3),
                CanalReserva.PORTAL
        );

        Reserva segunda = reservaServicio.registrarReserva(
                "R-HEAP-02",
                apartamento,
                crearHuesped("9102", LocalDate.of(1992, 1, 1)),
                LocalDate.of(2027, 5, 5),
                LocalDate.of(2027, 5, 7),
                CanalReserva.PORTAL
        );

        LocalDateTime fechaSimulada =
                segunda.getFechaVencimiento().plusMinutes(1);

        int vencidas = reservaServicio.vencerReservasPendientes(
                fechaSimulada
        );

        assertEquals(2, vencidas);

        assertEquals(
                EstadoReserva.CANCELADA,
                primera.getEstado()
        );

        assertEquals(
                EstadoReserva.CANCELADA,
                segunda.getEstado()
        );

        assertTrue(reservaServicio.estaDisponible(
                apartamento,
                primera.getFechaEntrada(),
                primera.getFechaSalida()
        ));

        assertTrue(reservaServicio.estaDisponible(
                apartamento,
                segunda.getFechaEntrada(),
                segunda.getFechaSalida()
        ));

        assertNotNull(
                reservaServicio.buscarPorCodigo("R-HEAP-01")
        );

        assertNotNull(
                reservaServicio.buscarPorCodigo("R-HEAP-02")
        );

        assertEquals(
                0,
                reservaServicio.vencerReservasPendientes(
                        fechaSimulada
                )
        );
    }

    @Test
    void colaPrioridadDebeIgnorarReservaConfirmada() {

        ReservaServicio reservaServicio = new ReservaServicio();

        Reserva reserva = reservaServicio.registrarReserva(
                "R-HEAP-03",
                apartamento,
                crearHuesped("9103", LocalDate.of(1995, 1, 1)),
                LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 12),
                CanalReserva.PORTAL
        );

        FolioServicio folio =
                reservaServicio.obtenerFolio("R-HEAP-03");

        folio.registrarPagoAnticipoPortal(
                "P-HEAP-03",
                servicio
        );

        reservaServicio.confirmarReserva(
                "R-HEAP-03",
                servicio,
                reserva.getFechaCreacion().plusHours(1)
        );

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );

        int vencidas = reservaServicio.vencerReservasPendientes(
                reserva.getFechaVencimiento().plusHours(1)
        );

        assertEquals(0, vencidas);

        assertEquals(
                EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );

        assertFalse(reservaServicio.estaDisponible(
                apartamento,
                reserva.getFechaEntrada(),
                reserva.getFechaSalida()
        ));
    }


    @Test
    void debeGenerarDesglosePorNoche() {

        Reserva reserva = crearReserva(
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 12),
                crearHuesped("9201", LocalDate.of(1995, 5, 10)),
                crearHuesped("9202", LocalDate.of(2021, 3, 10))
        );

        ListaDoble<DetalleTarifaNoche> desglose =
                servicio.generarDesglose(reserva);

        assertEquals(3, desglose.getTamanio());

        Iterator<DetalleTarifaNoche> iterador =
                desglose.iterator();

        DetalleTarifaNoche primera = iterador.next();

        assertEquals(
                LocalDate.of(2027, 3, 9),
                primera.getFecha()
        );
        assertEquals(TemporadaTipo.BAJA, primera.getTemporada());
        assertEquals(2, primera.getTotalOcupantes());
        assertEquals(1, primera.getPersonasQuePagan());
        assertEquals(1, primera.getPersonasExentas());
        assertEquals(50000.0, primera.getSubtotal(), 0.001);

        DetalleTarifaNoche segunda = iterador.next();

        assertEquals(
                LocalDate.of(2027, 3, 10),
                segunda.getFecha()
        );
        assertEquals(TemporadaTipo.MEDIA, segunda.getTemporada());
        assertEquals(2, segunda.getPersonasQuePagan());
        assertEquals(0, segunda.getPersonasExentas());
        assertEquals(140000.0, segunda.getSubtotal(), 0.001);

        DetalleTarifaNoche tercera = iterador.next();

        assertEquals(
                LocalDate.of(2027, 3, 11),
                tercera.getFecha()
        );
        assertEquals(140000.0, tercera.getSubtotal(), 0.001);

        assertFalse(iterador.hasNext());

        assertEquals(
                330000.0,
                servicio.sumarDesglose(desglose),
                0.001
        );
    }


    @Test
    void desgloseDebeCoincidirConCalculoRecursivo() {

        Reserva reserva = crearReserva(
                LocalDate.of(2027, 3, 9),
                LocalDate.of(2027, 3, 22),
                crearHuesped("9301", LocalDate.of(1988, 4, 10)),
                crearHuesped("9302", LocalDate.of(2023, 6, 15))
        );

        double totalRecursivo =
                servicio.calcularTotal(reserva);

        ListaDoble<DetalleTarifaNoche> desglose =
                servicio.generarDesglose(reserva);

        double totalDesglose =
                servicio.sumarDesglose(desglose);

        assertEquals(13, desglose.getTamanio());

        assertEquals(
                totalRecursivo,
                totalDesglose,
                0.001
        );

        assertEquals(
                930000.0,
                totalDesglose,
                0.001
        );
    }


    @Test
    void debeAcumularAbonosParcialesDelPortal() {

        ReservaServicio reservas = new ReservaServicio();

        Reserva reserva = reservas.registrarReserva(
                "R-ABONO-01",
                apartamento,
                crearHuesped("9501", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarAbonoPortal(
                "R-ABONO-01", "P-01", 20000);

        assertThrows(
                IllegalStateException.class,
                () -> reservas.confirmarReserva(
                        "R-ABONO-01", servicio)
        );

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());

        reservas.registrarAbonoPortal(
                "R-ABONO-01", "P-02", 22000);

        assertEquals(
                42000.0,
                reservas.obtenerFolio("R-ABONO-01")
                        .obtenerPagosNetos(),
                0.001
        );

        reservas.confirmarReserva("R-ABONO-01", servicio);

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void noDebeRegistrarAbonoEnReservaCancelada() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-ABONO-02",
                apartamento,
                crearHuesped("9502", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.cancelarReserva("R-ABONO-02");

        assertThrows(
                IllegalStateException.class,
                () -> reservas.registrarAbonoPortal(
                        "R-ABONO-02", "P-03", 10000)
        );
    }

    @Test
    void noDebeRegistrarAbonoDuplicado() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-ABONO-03",
                apartamento,
                crearHuesped("9503", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarAbonoPortal(
                "R-ABONO-03", "P-04", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> reservas.registrarAbonoPortal(
                        "R-ABONO-03", "P-04", 10000)
        );

        assertEquals(
                10000.0,
                reservas.obtenerFolio("R-ABONO-03")
                        .obtenerPagosNetos(),
                0.001
        );
    }


    @Test
    void debeRegistrarCargoYCalcularSaldoDelFolio() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-FOLIO-10",
                apartamento,
                crearHuesped("9601", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarCargoAlojamiento(
                "R-FOLIO-10",
                servicio
        );

        reservas.registrarAbonoPortal(
                "R-FOLIO-10", "P-10", 20000
        );

        reservas.registrarAbonoPortal(
                "R-FOLIO-10", "P-11", 22000
        );

        FolioServicio folio =
                reservas.obtenerFolio("R-FOLIO-10");

        assertEquals(42000.0, folio.obtenerPagosNetos(), 0.001);
        assertEquals(3, folio.cantidadMovimientos());

        // El saldo pendiente es el cargo menos los pagos.
        assertEquals(
                98000.0,
                folio.obtenerSaldo(),
                0.001
        );
    }

    @Test
    void noDebeRegistrarDosVecesElCargoAlojamiento() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-FOLIO-11",
                apartamento,
                crearHuesped("9602", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarCargoAlojamiento(
                "R-FOLIO-11", servicio
        );

        assertThrows(
                IllegalStateException.class,
                () -> reservas.registrarCargoAlojamiento(
                        "R-FOLIO-11", servicio
                )
        );

        FolioServicio folio =
                reservas.obtenerFolio("R-FOLIO-11");

        assertEquals(1, folio.cantidadMovimientos());
        assertEquals(140000.0, folio.obtenerSaldo(), 0.001);
    }


    @Test
    void debePermitirPagarSaldoDespuesDeConfirmar() {

        ReservaServicio reservas = new ReservaServicio();

        Reserva reserva = reservas.registrarReserva(
                "R-SALDO-01",
                apartamento,
                crearHuesped("9701", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarCargoAlojamiento(
                "R-SALDO-01", servicio
        );

        reservas.registrarAbonoPortal(
                "R-SALDO-01", "ANT-01", 20000
        );

        reservas.registrarAbonoPortal(
                "R-SALDO-01", "ANT-02", 22000
        );

        reservas.confirmarReserva("R-SALDO-01", servicio);

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        reservas.registrarPagoSaldo(
                "R-SALDO-01", "SALDO-01", 40000
        );

        reservas.registrarPagoSaldo(
                "R-SALDO-01", "SALDO-02", 58000
        );

        FolioServicio folio =
                reservas.obtenerFolio("R-SALDO-01");

        assertEquals(0.0, folio.obtenerSaldo(), 0.001);
        assertEquals(140000.0, folio.obtenerPagosNetos(), 0.001);
        assertEquals(5, folio.cantidadMovimientos());
    }

    @Test
    void noDebePermitirPagoMayorAlSaldo() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-SALDO-02",
                apartamento,
                crearHuesped("9702", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarCargoAlojamiento(
                "R-SALDO-02", servicio
        );

        reservas.registrarAbonoPortal(
                "R-SALDO-02", "ANT-03", 42000
        );

        reservas.confirmarReserva("R-SALDO-02", servicio);

        FolioServicio folio =
                reservas.obtenerFolio("R-SALDO-02");

        assertThrows(
                IllegalArgumentException.class,
                () -> reservas.registrarPagoSaldo(
                        "R-SALDO-02", "SALDO-03", 100000
                )
        );

        assertEquals(98000.0, folio.obtenerSaldo(), 0.001);
        assertEquals(2, folio.cantidadMovimientos());
    }

    @Test
    void noDebePagarSaldoSinCargoRegistrado() {

        ReservaServicio reservas = new ReservaServicio();

        reservas.registrarReserva(
                "R-SALDO-03",
                apartamento,
                crearHuesped("9703", LocalDate.of(1995, 5, 10)),
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        reservas.registrarAbonoPortal(
                "R-SALDO-03", "ANT-04", 42000
        );

        reservas.confirmarReserva("R-SALDO-03", servicio);

        assertThrows(
                IllegalStateException.class,
                () -> reservas.registrarPagoSaldo(
                        "R-SALDO-03", "SALDO-04", 10000
                )
        );
    }

}
