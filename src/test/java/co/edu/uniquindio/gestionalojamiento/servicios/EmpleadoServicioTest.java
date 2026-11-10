package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoServicioTest {

    private EmpleadoServicio servicio;
    private Empleado empleado;
    private Empleado otroEmpleado;

    @BeforeEach
    void preparar() {
        servicio = new EmpleadoServicio();

        empleado = crearEmpleado(
                "123",
                "Carlos",
                "Gomez",
                TipoEmpleado.RECEPCION
        );

        otroEmpleado = crearEmpleado(
                "456",
                "Maria",
                "Lopez",
                TipoEmpleado.LIMPIEZA
        );
    }

    private Empleado crearEmpleado(
            String documento,
            String nombres,
            String apellidos,
            TipoEmpleado tipo
    ) {
        return new Empleado(
                documento,
                nombres,
                apellidos,
                "3001234567",
                documento + "@gmail.com",
                tipo
        );
    }

    @Test
    void debeRegistrarEmpleadoCorrectamente() {
        assertTrue(servicio.registrarEmpleado(empleado));

        assertEquals(1, servicio.cantidadEmpleados());
        assertTrue(servicio.existeEmpleado("123"));
    }

    @Test
    void debeRechazarEmpleadoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarEmpleado(null)
        );

        assertEquals(0, servicio.cantidadEmpleados());
    }

    @Test
    void noDebeRegistrarDocumentosDuplicados() {
        servicio.registrarEmpleado(empleado);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarEmpleado(
                        crearEmpleado(
                                "123",
                                "Pedro",
                                "Perez",
                                TipoEmpleado.MANTENIMIENTO
                        )
                )
        );

        assertEquals(1, servicio.cantidadEmpleados());
    }

    @Test
    void debeBuscarEmpleadoPorDocumento() {
        servicio.registrarEmpleado(empleado);

        assertEquals(
                empleado,
                servicio.buscarEmpleado("123")
        );
    }

    @Test
    void debeRetornarNullSiEmpleadoNoExiste() {
        assertNull(servicio.buscarEmpleado("999"));
    }

    @Test
    void debeRechazarDocumentoNuloAlBuscar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarEmpleado(null)
        );
    }

    @Test
    void debeRechazarDocumentoVacioAlBuscar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarEmpleado("")
        );
    }

    @Test
    void debeListarTodosLosEmpleados() {
        servicio.registrarEmpleado(empleado);
        servicio.registrarEmpleado(otroEmpleado);

        assertEquals(2, servicio.listarEmpleados().getTamanio());
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayEmpleados() {
        assertTrue(servicio.listarEmpleados().estaVacia());
    }

    @Test
    void debeListarSolamenteEmpleadosActivos() {
        servicio.registrarEmpleado(empleado);
        servicio.registrarEmpleado(otroEmpleado);

        servicio.desactivarEmpleado("123");

        assertEquals(
                1,
                servicio.listarEmpleadosActivos().getTamanio()
        );

        assertEquals(
                "456",
                servicio.listarEmpleadosActivos()
                        .obtenerPrimero()
                        .getDocumento()
        );
    }

    @Test
    void debeBuscarEmpleadosPorTipo() {
        servicio.registrarEmpleado(empleado);
        servicio.registrarEmpleado(otroEmpleado);

        Empleado mantenimiento = crearEmpleado(
                "789",
                "Andres",
                "Castano",
                TipoEmpleado.MANTENIMIENTO
        );

        servicio.registrarEmpleado(mantenimiento);

        assertEquals(
                1,
                servicio.buscarPorTipo(
                        TipoEmpleado.RECEPCION
                ).getTamanio()
        );

        assertEquals(
                1,
                servicio.buscarPorTipo(
                        TipoEmpleado.LIMPIEZA
                ).getTamanio()
        );

        assertEquals(
                1,
                servicio.buscarPorTipo(
                        TipoEmpleado.MANTENIMIENTO
                ).getTamanio()
        );
    }

    @Test
    void debeRetornarListaVaciaSiNoHayEmpleadosDelTipo() {
        servicio.registrarEmpleado(empleado);

        assertTrue(
                servicio.buscarPorTipo(
                        TipoEmpleado.ADMINISTRADOR
                ).estaVacia()
        );
    }

    @Test
    void debeRechazarTipoNuloAlBuscarPorTipo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarPorTipo(null)
        );
    }

    @Test
    void debeActualizarEmpleadoCorrectamente() {
        servicio.registrarEmpleado(empleado);

        assertTrue(
                servicio.actualizarEmpleado(
                        "123",
                        "Carlos Alberto",
                        "Gomez",
                        "3119876543",
                        "carlosnuevo@gmail.com",
                        TipoEmpleado.ADMINISTRADOR
                )
        );

        assertEquals("Carlos Alberto", empleado.getNombres());
        assertEquals("Gomez", empleado.getApellidos());
        assertEquals("3119876543", empleado.getTelefono());
        assertEquals("carlosnuevo@gmail.com", empleado.getCorreo());
        assertEquals(
                TipoEmpleado.ADMINISTRADOR,
                empleado.getTipo()
        );
    }

    @Test
    void debeRetornarFalseAlActualizarEmpleadoInexistente() {
        assertFalse(
                servicio.actualizarEmpleado(
                        "999",
                        "Carlos",
                        "Gomez",
                        "3001234567",
                        "carlos@gmail.com",
                        TipoEmpleado.RECEPCION
                )
        );
    }

    @Test
    void debeRechazarActualizacionConNombresVacios() {
        servicio.registrarEmpleado(empleado);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.actualizarEmpleado(
                        "123",
                        "",
                        "Gomez",
                        "3001234567",
                        "carlos@gmail.com",
                        TipoEmpleado.RECEPCION
                )
        );
    }

    @Test
    void debeRechazarActualizacionConCorreoInvalido() {
        servicio.registrarEmpleado(empleado);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.actualizarEmpleado(
                        "123",
                        "Carlos",
                        "Gomez",
                        "3001234567",
                        "correo-invalido",
                        TipoEmpleado.RECEPCION
                )
        );
    }

    @Test
    void debeDesactivarEmpleadoCorrectamente() {
        servicio.registrarEmpleado(empleado);

        assertTrue(servicio.desactivarEmpleado("123"));
        assertFalse(empleado.isActivo());
    }

    @Test
    void noDebeDesactivarDosVecesElMismoEmpleado() {
        servicio.registrarEmpleado(empleado);

        assertTrue(servicio.desactivarEmpleado("123"));
        assertFalse(servicio.desactivarEmpleado("123"));
    }

    @Test
    void debeRetornarFalseAlDesactivarEmpleadoInexistente() {
        assertFalse(servicio.desactivarEmpleado("999"));
    }

    @Test
    void debeActivarEmpleadoCorrectamente() {
        servicio.registrarEmpleado(empleado);
        servicio.desactivarEmpleado("123");

        assertTrue(servicio.activarEmpleado("123"));
        assertTrue(empleado.isActivo());
    }

    @Test
    void noDebeActivarDosVecesElMismoEmpleado() {
        servicio.registrarEmpleado(empleado);

        assertFalse(servicio.activarEmpleado("123"));
    }

    @Test
    void debeRetornarFalseAlActivarEmpleadoInexistente() {
        assertFalse(servicio.activarEmpleado("999"));
    }

    @Test
    void debeContarEmpleadosCorrectamente() {
        assertEquals(0, servicio.cantidadEmpleados());

        servicio.registrarEmpleado(empleado);
        servicio.registrarEmpleado(otroEmpleado);

        assertEquals(2, servicio.cantidadEmpleados());
    }

    @Test
    void debeContarSolamenteEmpleadosActivos() {
        servicio.registrarEmpleado(empleado);
        servicio.registrarEmpleado(otroEmpleado);

        servicio.desactivarEmpleado("123");

        assertEquals(1, servicio.cantidadEmpleadosActivos());
    }

    @Test
    void debeIndicarSiExisteUnEmpleado() {
        servicio.registrarEmpleado(empleado);

        assertTrue(servicio.existeEmpleado("123"));
        assertFalse(servicio.existeEmpleado("999"));
    }
}
