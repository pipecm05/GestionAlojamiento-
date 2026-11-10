package co.edu.uniquindio.gestionalojamiento.estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConjuntoTest {

    private Conjunto<Integer> conjunto;

    @BeforeEach
    void configurar() {
        conjunto = new Conjunto<>();
    }

    @Test
    void debeIniciarVacio() {
        assertTrue(conjunto.estaVacio());
        assertEquals(0, conjunto.getTamanio());
    }

    @Test
    void debeAgregarElemento() {
        assertTrue(conjunto.agregar(10));

        assertTrue(conjunto.contiene(10));
        assertEquals(1, conjunto.getTamanio());
    }

    @Test
    void noDebePermitirElementosDuplicados() {
        conjunto.agregar(10);

        assertFalse(conjunto.agregar(10));

        assertEquals(1, conjunto.getTamanio());
    }

    @Test
    void debeEliminarElemento() {
        conjunto.agregar(10);
        conjunto.agregar(20);

        assertTrue(conjunto.eliminar(10));

        assertFalse(conjunto.contiene(10));
        assertEquals(1, conjunto.getTamanio());
    }

    @Test
    void debeRetornarFalseAlEliminarElementoInexistente() {
        assertFalse(conjunto.eliminar(50));
    }

    @Test
    void debeRealizarLaUnionDeDosConjuntos() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);

        otro.agregar(2);
        otro.agregar(3);

        Conjunto<Integer> resultado = conjunto.union(otro);

        assertEquals(3, resultado.getTamanio());
        assertTrue(resultado.contiene(1));
        assertTrue(resultado.contiene(2));
        assertTrue(resultado.contiene(3));
    }

    @Test
    void debeRealizarLaInterseccionDeDosConjuntos() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);
        conjunto.agregar(3);

        otro.agregar(2);
        otro.agregar(3);
        otro.agregar(4);

        Conjunto<Integer> resultado = conjunto.interseccion(otro);

        assertEquals(2, resultado.getTamanio());
        assertTrue(resultado.contiene(2));
        assertTrue(resultado.contiene(3));
        assertFalse(resultado.contiene(1));
    }

    @Test
    void debeRealizarLaDiferenciaDeDosConjuntos() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);
        conjunto.agregar(3);

        otro.agregar(2);
        otro.agregar(4);

        Conjunto<Integer> resultado = conjunto.diferencia(otro);

        assertEquals(2, resultado.getTamanio());
        assertTrue(resultado.contiene(1));
        assertTrue(resultado.contiene(3));
        assertFalse(resultado.contiene(2));
    }

    @Test
    void debeIdentificarUnSubconjunto() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);

        otro.agregar(1);
        otro.agregar(2);
        otro.agregar(3);

        assertTrue(conjunto.esSubconjuntoDe(otro));
    }

    @Test
    void noDebeIdentificarComoSubconjuntoSiFaltaUnElemento() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);

        otro.agregar(1);

        assertFalse(conjunto.esSubconjuntoDe(otro));
    }

    @Test
    void debeVaciarElConjunto() {
        conjunto.agregar(1);
        conjunto.agregar(2);

        conjunto.vaciar();

        assertTrue(conjunto.estaVacio());
        assertEquals(0, conjunto.getTamanio());
    }

    @Test
    void noDebePermitirAgregarNulos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> conjunto.agregar(null)
        );
    }

    @Test
    void debeLanzarExcepcionSiElOtroConjuntoEsNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> conjunto.union(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> conjunto.interseccion(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> conjunto.diferencia(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> conjunto.esSubconjuntoDe(null)
        );
    }

    @Test
    void dosConjuntosConLosMismosElementosDebenSerIguales() {
        Conjunto<Integer> otro = new Conjunto<>();

        conjunto.agregar(1);
        conjunto.agregar(2);

        otro.agregar(2);
        otro.agregar(1);

        assertEquals(conjunto, otro);
        assertEquals(conjunto.hashCode(), otro.hashCode());
    }

    @Test
    void debeMostrarLosElementosEnFormatoDeConjunto() {
        conjunto.agregar(1);
        conjunto.agregar(2);
        conjunto.agregar(3);

        assertEquals("{1, 2, 3}", conjunto.toString());
    }
}
