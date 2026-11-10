
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoTurno;
import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTurno;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Turno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TurnoServicioTest {

    private TurnoServicio servicio;
    private Empleado empleado;
    private Empleado otroEmpleado;

    @BeforeEach
    void preparar() {
        servicio = new TurnoServicio();

        empleado = new Empleado(
                "123",
                "Carlos",
                "Gomez",
                "3001234567",
                "carlos@gmail.com",
                TipoEmpleado.RECEPCION
        );

        otroEmpleado = new Empleado(
                "456",
                "Maria",
                "Lopez",
                "3101234567",
                "maria@gmail.com",
                TipoEmpleado.LIMPIEZA
        );
    }

    private Turno crearTurno(
            String codigo,
            Empleado empleado,
            int horaInicio,
            int horaFin
    ) {
        return new Turno(
                codigo,
                empleado,
                LocalDateTime.of(2026, 12, 10, horaInicio, 0),
                LocalDateTime.of(2026, 12, 10, horaFin, 0),
                TipoTurno.MANANA
        );
    }

    @Test
    void debeRegistrarTurnoCorrectamente() {
        Turno turno = crearTurno("T001", empleado, 7, 15);

        assertTrue(servicio.registrarTurno(turno));
        assertEquals(1, servicio.cantidadTurnos());
        assertTrue(servicio.existeTurno("T001"));
    }

    @Test
    void debeRechazarTurnoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarTurno(null)
        );

        assertEquals(0, servicio.cantidadTurnos());
    }

    @Test
    void debeRechazarTurnosConCodigoDuplicado() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 12)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarTurno(
                        crearTurno("T001", otroEmpleado, 13, 18)
                )
        );

        assertEquals(1, servicio.cantidadTurnos());
    }

    @Test
    void debeRechazarTurnosQueSeCruzanParaElMismoEmpleado() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 15)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarTurno(
                        crearTurno("T002", empleado, 10, 18)
                )
        );

        assertEquals(1, servicio.cantidadTurnos());
    }

    @Test
    void debePermitirTurnosEnHorariosDiferentes() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 12)
        );

        assertTrue(
                servicio.registrarTurno(
                        crearTurno("T002", empleado, 12, 18)
                )
        );

        assertEquals(2, servicio.cantidadTurnos());
    }

    @Test
    void debePermitirTurnosSimultaneosDeEmpleadosDiferentes() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 15)
        );

        assertTrue(
                servicio.registrarTurno(
                        crearTurno("T002", otroEmpleado, 7, 15)
                )
        );

        assertEquals(2, servicio.cantidadTurnos());
    }

    @Test
    void debeBuscarTurnoPorCodigo() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);

        assertEquals(turno, servicio.buscarTurno("T001"));
    }

    @Test
    void debeRetornarNullCuandoElTurnoNoExiste() {
        assertNull(servicio.buscarTurno("T999"));
    }

    @Test
    void debeRechazarCodigoDeBusquedaInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarTurno(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarTurno("")
        );
    }

    @Test
    void debeConsultarTurnosDeUnEmpleado() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 12)
        );

        servicio.registrarTurno(
                crearTurno("T002", empleado, 13, 18)
        );

        servicio.registrarTurno(
                crearTurno("T003", otroEmpleado, 7, 15)
        );

        assertEquals(
                2,
                servicio.obtenerTurnosEmpleado("123").getTamanio()
        );

        assertEquals(
                1,
                servicio.obtenerTurnosEmpleado("456").getTamanio()
        );
    }

    @Test
    void debeRetornarListaVaciaSiEmpleadoNoTieneTurnos() {
        assertTrue(
                servicio.obtenerTurnosEmpleado("123").estaVacia()
        );
    }

    @Test
    void debeListarTodosLosTurnos() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 12)
        );

        servicio.registrarTurno(
                crearTurno("T002", otroEmpleado, 7, 15)
        );

        assertEquals(2, servicio.listarTurnos().getTamanio());
    }

    @Test
    void debeCancelarTurnoCorrectamente() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);

        assertTrue(servicio.cancelarTurno("T001"));

        assertEquals(
                EstadoTurno.CANCELADO,
                turno.getEstado()
        );
    }

    @Test
    void debeRetornarFalseAlCancelarTurnoInexistente() {
        assertFalse(servicio.cancelarTurno("T999"));
    }

    @Test
    void noDebeCancelarDosVecesElMismoTurno() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);

        assertTrue(servicio.cancelarTurno("T001"));
        assertFalse(servicio.cancelarTurno("T001"));
    }

    @Test
    void debeCompletarTurnoCorrectamente() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);

        assertTrue(servicio.completarTurno("T001"));

        assertEquals(
                EstadoTurno.COMPLETADO,
                turno.getEstado()
        );
    }

    @Test
    void noDebeCompletarTurnoCancelado() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);
        servicio.cancelarTurno("T001");

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.completarTurno("T001")
        );
    }

    @Test
    void noDebeCancelarTurnoCompletado() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);
        servicio.completarTurno("T001");

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.cancelarTurno("T001")
        );
    }

    @Test
    void noDebeCompletarDosVecesElMismoTurno() {
        Turno turno = crearTurno("T001", empleado, 7, 15);
        servicio.registrarTurno(turno);

        assertTrue(servicio.completarTurno("T001"));
        assertFalse(servicio.completarTurno("T001"));
    }

    @Test
    void debePermitirNuevoTurnoCuandoElAnteriorFueCancelado() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 15)
        );

        servicio.cancelarTurno("T001");

        assertTrue(
                servicio.registrarTurno(
                        crearTurno("T002", empleado, 7, 15)
                )
        );

        assertEquals(2, servicio.cantidadTurnos());
    }

    @Test
    void debeContarCorrectamenteLosTurnos() {
        assertEquals(0, servicio.cantidadTurnos());

        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 12)
        );

        servicio.registrarTurno(
                crearTurno("T002", otroEmpleado, 7, 15)
        );

        assertEquals(2, servicio.cantidadTurnos());
    }

    @Test
    void debeIndicarSiExisteUnTurno() {
        servicio.registrarTurno(
                crearTurno("T001", empleado, 7, 15)
        );

        assertTrue(servicio.existeTurno("T001"));
        assertFalse(servicio.existeTurno("T999"));
    }
}
