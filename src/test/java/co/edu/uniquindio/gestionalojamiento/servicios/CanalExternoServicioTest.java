package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudExterna;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.SolicitudExterna;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CanalExternoServicioTest {

    private ReservaServicio reservas;
    private CanalExternoServicio externos;
    private Apartamento apartamento;
    private Huesped huesped;

    @BeforeEach
    void preparar() {

        reservas = new ReservaServicio();
        externos = new CanalExternoServicio(reservas);

        apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        huesped = new Huesped(
                "1001", "Ana", "Lopez",
                "3001234567", "ana@correo.com",
                LocalDate.of(1995, 5, 10)
        );
    }

    private SolicitudExterna solicitud(
            String codigo,
            LocalDate entrada,
            LocalDate salida) {

        return new SolicitudExterna(
                codigo,
                apartamento,
                huesped,
                entrada,
                salida
        );
    }

    @Test
    void debeProcesarSolicitudesEnOrdenFIFO() {

        SolicitudExterna primera = solicitud(
                "EXT-01",
                LocalDate.of(2027, 5, 1),
                LocalDate.of(2027, 5, 3)
        );

        SolicitudExterna segunda = solicitud(
                "EXT-02",
                LocalDate.of(2027, 5, 4),
                LocalDate.of(2027, 5, 6)
        );

        externos.recibirSolicitud(primera);
        externos.recibirSolicitud(segunda);

        assertEquals(2, externos.cantidadPendientes());

        assertSame(primera, externos.procesarSiguiente());

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                primera.getEstado()
        );

        assertEquals(1, externos.cantidadPendientes());

        assertSame(segunda, externos.procesarSiguiente());

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                segunda.getEstado()
        );

        assertEquals(0, externos.cantidadPendientes());
        assertEquals(2, externos.cantidadProcesadas());
    }

    @Test
    void debeRechazarSolicitudPorConflicto() {

        reservas.registrarReserva(
                "R-DIRECTA",
                apartamento,
                huesped,
                LocalDate.of(2027, 6, 10),
                LocalDate.of(2027, 6, 15),
                CanalReserva.DIRECTA
        );

        SolicitudExterna externa = solicitud(
                "EXT-03",
                LocalDate.of(2027, 6, 12),
                LocalDate.of(2027, 6, 14)
        );

        externos.recibirSolicitud(externa);
        externos.procesarSiguiente();

        assertEquals(
                EstadoSolicitudExterna.RECHAZADA,
                externa.getEstado()
        );

        assertEquals(1, externos.contarConflictos());

        assertNull(reservas.buscarPorCodigo("EXT-03"));

        assertNotNull(externa.getMotivoRechazo());
    }

    @Test
    void solicitudExternaAceptadaDebeCrearReserva() {

        SolicitudExterna externa = solicitud(
                "EXT-04",
                LocalDate.of(2027, 7, 1),
                LocalDate.of(2027, 7, 4)
        );

        externos.recibirSolicitud(externa);
        externos.procesarSiguiente();

        assertNotNull(reservas.buscarPorCodigo("EXT-04"));

        assertEquals(
                CanalReserva.EXTERNA,
                reservas.buscarPorCodigo("EXT-04").getCanal()
        );
    }

    @Test
    void debeRechazarCodigoDuplicadoEnCola() {

        externos.recibirSolicitud(solicitud(
                "EXT-05",
                LocalDate.of(2027, 8, 1),
                LocalDate.of(2027, 8, 3)
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> externos.recibirSolicitud(solicitud(
                        "EXT-05",
                        LocalDate.of(2027, 8, 5),
                        LocalDate.of(2027, 8, 7)
                ))
        );
    }

    @Test
    void debeConservarRechazosYContinuar() {

        reservas.registrarReserva(
                "R-OCUPADA",
                apartamento,
                huesped,
                LocalDate.of(2027, 9, 1),
                LocalDate.of(2027, 9, 5),
                CanalReserva.DIRECTA
        );

        SolicitudExterna rechazada = solicitud(
                "EXT-06",
                LocalDate.of(2027, 9, 2),
                LocalDate.of(2027, 9, 4)
        );

        SolicitudExterna aceptada = solicitud(
                "EXT-07",
                LocalDate.of(2027, 9, 6),
                LocalDate.of(2027, 9, 8)
        );

        externos.recibirSolicitud(rechazada);
        externos.recibirSolicitud(aceptada);

        assertEquals(2, externos.procesarTodas());

        assertEquals(
                EstadoSolicitudExterna.RECHAZADA,
                rechazada.getEstado()
        );

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                aceptada.getEstado()
        );

        assertEquals(1, externos.contarConflictos());
        assertEquals(2, externos.obtenerHistorial().getTamanio());
        assertEquals(0, externos.cantidadPendientes());
    }

    @Test
    void noDebeProcesarColaVacia() {

        assertThrows(
                IllegalStateException.class,
                () -> externos.procesarSiguiente()
        );
    }


    @Test
    void debeAceptarSolicitudTrasCancelarReservaAnterior() {

        LocalDate entrada = LocalDate.of(2027, 10, 10);
        LocalDate salida = LocalDate.of(2027, 10, 15);

        reservas.registrarReserva(
                "R-ANTERIOR",
                apartamento,
                huesped,
                entrada,
                salida,
                CanalReserva.DIRECTA
        );

        SolicitudExterna externa = solicitud(
                "EXT-08",
                entrada,
                salida
        );

        externos.recibirSolicitud(externa);

        reservas.cancelarReserva("R-ANTERIOR");

        externos.procesarSiguiente();

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                externa.getEstado()
        );

        assertNotNull(
                reservas.buscarPorCodigo("EXT-08")
        );

        assertEquals(0, externos.contarConflictos());
    }

    @Test
    void debeRespetarFIFOCuandoDosSolicitudesCompiten() {

        LocalDate entrada = LocalDate.of(2027, 11, 10);
        LocalDate salida = LocalDate.of(2027, 11, 15);

        SolicitudExterna primera = solicitud(
                "EXT-09",
                entrada,
                salida
        );

        SolicitudExterna segunda = solicitud(
                "EXT-10",
                entrada,
                salida
        );

        externos.recibirSolicitud(primera);
        externos.recibirSolicitud(segunda);

        assertEquals(2, externos.procesarTodas());

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                primera.getEstado()
        );

        assertEquals(
                EstadoSolicitudExterna.RECHAZADA,
                segunda.getEstado()
        );

        assertNotNull(
                reservas.buscarPorCodigo("EXT-09")
        );

        assertNull(
                reservas.buscarPorCodigo("EXT-10")
        );

        assertEquals(1, externos.contarConflictos());
        assertEquals(0, externos.cantidadPendientes());
    }

    @Test
    void noDebeRecibirUnaSolicitudYaProcesada() {

        SolicitudExterna externa = solicitud(
                "EXT-11",
                LocalDate.of(2027, 12, 10),
                LocalDate.of(2027, 12, 12)
        );

        externos.recibirSolicitud(externa);
        externos.procesarSiguiente();

        assertEquals(
                EstadoSolicitudExterna.ACEPTADA,
                externa.getEstado()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> externos.recibirSolicitud(externa)
        );

        assertEquals(0, externos.cantidadPendientes());
        assertEquals(1, externos.cantidadProcesadas());
    }

}
