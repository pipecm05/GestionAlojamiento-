package co.edu.uniquindio.gestionalojamiento.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionGrupoTest {

    private AsignacionGrupo grupo;
    private Huesped huesped1;
    private Huesped huesped2;
    private Apartamento apartamento1;
    private Apartamento apartamento2;

    @BeforeEach
    void setUp() {
        grupo = new AsignacionGrupo("GRUPO-001");

        huesped1 = new Huesped(
                "123",
                "Juan",
                "Perez",
                "3001234567",
                "juan@gmail.com"
        );

        huesped2 = new Huesped(
                "456",
                "Maria",
                "Lopez",
                "3007654321",
                "maria@gmail.com"
        );

        apartamento1 = new Apartamento(
                "A-101",
                "Apartamento Norte",
                2,
                2,
                100000,
                150000,
                200000
        );

        apartamento2 = new Apartamento(
                "A-102",
                "Apartamento Sur",
                3,
                2,
                120000,
                160000,
                220000
        );
    }

    @Test
    void crearGrupoCorrectamente() {
        assertEquals("GRUPO-001", grupo.getCodigo());
        assertTrue(grupo.getHuespedes().estaVacia());
        assertTrue(grupo.getApartamentos().estaVacia());
        assertTrue(grupo.getAsignaciones().estaVacia());
    }

    @Test
    void noPermitirCodigoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionGrupo(null)
        );
    }

    @Test
    void noPermitirCodigoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionGrupo(" ")
        );
    }

    @Test
    void agregarHuespedCorrectamente() {
        grupo.agregarHuesped(huesped1);

        assertEquals(1, grupo.getHuespedes().getTamanio());
        assertTrue(grupo.getHuespedes().contiene(huesped1));
    }

    @Test
    void noPermitirHuespedNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.agregarHuesped(null)
        );
    }

    @Test
    void noPermitirHuespedDuplicado() {
        grupo.agregarHuesped(huesped1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.agregarHuesped(huesped1)
        );
    }

    @Test
    void agregarApartamentoCorrectamente() {
        grupo.agregarApartamento(apartamento1);

        assertEquals(1, grupo.getApartamentos().getTamanio());
        assertTrue(grupo.getApartamentos().contiene(apartamento1));
    }

    @Test
    void noPermitirApartamentoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.agregarApartamento(null)
        );
    }

    @Test
    void noPermitirApartamentoDuplicado() {
        grupo.agregarApartamento(apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.agregarApartamento(apartamento1)
        );
    }

    @Test
    void asignarHuespedCorrectamente() {
        grupo.agregarHuesped(huesped1);
        grupo.agregarApartamento(apartamento1);

        grupo.asignarHuesped(huesped1, apartamento1);

        assertEquals(1, grupo.getAsignaciones().getTamanio());

        AsignacionHuespedApartamento asignacion =
                grupo.getAsignaciones().obtenerPrimero();

        assertSame(huesped1, asignacion.getHuesped());
        assertSame(apartamento1, asignacion.getApartamento());
    }

    @Test
    void noPermitirAsignacionConHuespedNulo() {
        grupo.agregarApartamento(apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(null, apartamento1)
        );
    }

    @Test
    void noPermitirAsignacionConApartamentoNulo() {
        grupo.agregarHuesped(huesped1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(huesped1, null)
        );
    }

    @Test
    void noPermitirHuespedQueNoPerteneceAlGrupo() {
        grupo.agregarApartamento(apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(huesped1, apartamento1)
        );
    }

    @Test
    void noPermitirApartamentoQueNoPerteneceAlGrupo() {
        grupo.agregarHuesped(huesped1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(huesped1, apartamento1)
        );
    }

    @Test
    void noPermitirAsignarDosApartamentosAlMismoHuesped() {
        grupo.agregarHuesped(huesped1);
        grupo.agregarApartamento(apartamento1);
        grupo.agregarApartamento(apartamento2);

        grupo.asignarHuesped(huesped1, apartamento1);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(huesped1, apartamento2)
        );

        assertEquals(1, grupo.getAsignaciones().getTamanio());
    }

    @Test
    void noPermitirSuperarCapacidadDelApartamento() {
        grupo.agregarHuesped(huesped1);
        grupo.agregarHuesped(huesped2);
        grupo.agregarApartamento(apartamento1);

        grupo.asignarHuesped(huesped1, apartamento1);
        grupo.asignarHuesped(huesped2, apartamento1);

        Huesped huesped3 = new Huesped(
                "789",
                "Carlos",
                "Gomez",
                "3001112233",
                "carlos@gmail.com"
        );

        grupo.agregarHuesped(huesped3);

        assertThrows(
                IllegalArgumentException.class,
                () -> grupo.asignarHuesped(huesped3, apartamento1)
        );

        assertEquals(2, grupo.getAsignaciones().getTamanio());
    }

    @Test
    void permitirAsignarHuespedesAdiferentesApartamentos() {
        grupo.agregarHuesped(huesped1);
        grupo.agregarHuesped(huesped2);

        grupo.agregarApartamento(apartamento1);
        grupo.agregarApartamento(apartamento2);

        grupo.asignarHuesped(huesped1, apartamento1);
        grupo.asignarHuesped(huesped2, apartamento2);

        assertEquals(2, grupo.getAsignaciones().getTamanio());
    }
}
