package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoritoServicioTest {

    private FavoritoServicio servicio;
    private Huesped huesped;
    private Huesped otroHuesped;
    private Apartamento apartamento;
    private Apartamento otroApartamento;

    @BeforeEach
    void preparar() {
        servicio = new FavoritoServicio();

        huesped = new Huesped(
                "123",
                "Carlos",
                "Gomez",
                "3001234567",
                "carlos@gmail.com"
        );

        otroHuesped = new Huesped(
                "456",
                "Maria",
                "Lopez",
                "3101234567",
                "maria@gmail.com"
        );

        apartamento = new Apartamento(
                "A001",
                "Apartamento Centro",
                4,
                2,
                100000,
                150000,
                200000
        );

        otroApartamento = new Apartamento(
                "A002",
                "Apartamento Norte",
                6,
                3,
                120000,
                170000,
                220000
        );
    }

    @Test
    void debeAgregarFavoritoCorrectamente() {
        assertTrue(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertEquals(1, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debeRechazarHuespedNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.agregarFavorito(null, apartamento)
        );

        assertEquals(0, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debeRechazarAlojamientoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.agregarFavorito(huesped, null)
        );

        assertEquals(0, servicio.cantidadTotalFavoritos());
    }

    @Test
    void noDebeAgregarDosVecesElMismoAlojamiento() {
        assertTrue(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertFalse(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertEquals(1, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debePermitirElMismoAlojamientoParaHuespedesDiferentes() {
        assertTrue(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertTrue(
                servicio.agregarFavorito(otroHuesped, apartamento)
        );

        assertEquals(2, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debePermitirVariosFavoritosParaUnHuesped() {
        assertTrue(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertTrue(
                servicio.agregarFavorito(huesped, otroApartamento)
        );

        assertEquals(2, servicio.contarFavoritos("123"));
    }

    @Test
    void debeConsultarFavoritosDeUnHuesped() {
        servicio.agregarFavorito(huesped, apartamento);
        servicio.agregarFavorito(huesped, otroApartamento);
        servicio.agregarFavorito(otroHuesped, apartamento);

        assertEquals(
                2,
                servicio.obtenerFavoritos("123").getTamanio()
        );
    }

    @Test
    void debeRetornarListaVaciaSiNoTieneFavoritos() {
        assertTrue(
                servicio.obtenerFavoritos("123").estaVacia()
        );
    }

    @Test
    void debeRechazarDocumentoNuloAlConsultarFavoritos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.obtenerFavoritos(null)
        );
    }

    @Test
    void debeRechazarDocumentoVacioAlConsultarFavoritos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.obtenerFavoritos("")
        );
    }

    @Test
    void debeIdentificarUnAlojamientoComoFavorito() {
        servicio.agregarFavorito(huesped, apartamento);

        assertTrue(
                servicio.esFavorito("123", "A001")
        );
    }

    @Test
    void debeIndicarCuandoUnAlojamientoNoEsFavorito() {
        assertFalse(
                servicio.esFavorito("123", "A001")
        );
    }

    @Test
    void debeRechazarCodigoAlojamientoNuloAlConsultar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.esFavorito("123", null)
        );
    }

    @Test
    void debeEliminarFavoritoCorrectamente() {
        servicio.agregarFavorito(huesped, apartamento);

        assertTrue(
                servicio.eliminarFavorito("123", "A001")
        );

        assertEquals(0, servicio.contarFavoritos("123"));
    }

    @Test
    void debeRetornarFalseAlEliminarFavoritoInexistente() {
        assertFalse(
                servicio.eliminarFavorito("123", "A001")
        );
    }

    @Test
    void debeRechazarDocumentoNuloAlEliminar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.eliminarFavorito(null, "A001")
        );
    }

    @Test
    void debeRechazarCodigoAlojamientoVacioAlEliminar() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.eliminarFavorito("123", "")
        );
    }

    @Test
    void debeContarFavoritosCorrectamente() {
        servicio.agregarFavorito(huesped, apartamento);
        servicio.agregarFavorito(huesped, otroApartamento);
        servicio.agregarFavorito(otroHuesped, apartamento);

        assertEquals(2, servicio.contarFavoritos("123"));
        assertEquals(1, servicio.contarFavoritos("456"));
    }

    @Test
    void debeIndicarSiUnHuespedTieneFavoritos() {
        assertFalse(servicio.tieneFavoritos("123"));

        servicio.agregarFavorito(huesped, apartamento);

        assertTrue(servicio.tieneFavoritos("123"));
    }

    @Test
    void debeConsultarCantidadTotalDeFavoritos() {
        assertEquals(0, servicio.cantidadTotalFavoritos());

        servicio.agregarFavorito(huesped, apartamento);
        servicio.agregarFavorito(otroHuesped, otroApartamento);

        assertEquals(2, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debeConservarFavoritosDeOtrosHuespedesAlEliminar() {
        servicio.agregarFavorito(huesped, apartamento);
        servicio.agregarFavorito(otroHuesped, apartamento);

        assertTrue(
                servicio.eliminarFavorito("123", "A001")
        );

        assertFalse(
                servicio.esFavorito("123", "A001")
        );

        assertTrue(
                servicio.esFavorito("456", "A001")
        );

        assertEquals(1, servicio.cantidadTotalFavoritos());
    }

    @Test
    void debePermitirAgregarNuevamenteDespuesDeEliminar() {
        servicio.agregarFavorito(huesped, apartamento);

        assertTrue(
                servicio.eliminarFavorito("123", "A001")
        );

        assertTrue(
                servicio.agregarFavorito(huesped, apartamento)
        );

        assertEquals(1, servicio.contarFavoritos("123"));
    }
}
