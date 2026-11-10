
package co.edu.uniquindio.gestionalojamiento.estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaCircularTest {

    private ListaCircular<Integer> lista;

    @BeforeEach
    void prepararDatos() {
        lista = new ListaCircular<>();
    }

    @Test
    void debeAgregarElementosAlFinal() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertEquals(3, lista.getTamanio());
        assertEquals(10, lista.obtenerPrimero());
        assertEquals(30, lista.obtenerUltimo());
    }

    @Test
    void debeAgregarElementosAlInicio() {
        lista.agregar(20);
        lista.agregarAlInicio(10);
        lista.agregarAlInicio(5);

        assertEquals(3, lista.getTamanio());
        assertEquals(5, lista.obtenerPrimero());
        assertEquals(20, lista.obtenerUltimo());
    }

    @Test
    void debeEncontrarElementos() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertTrue(lista.contiene(20));
        assertFalse(lista.contiene(50));
    }

    @Test
    void debeEliminarUnElemento() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertTrue(lista.eliminar(20));
        assertEquals(2, lista.getTamanio());
        assertFalse(lista.contiene(20));
    }

    @Test
    void debeEliminarElPrimerElemento() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        int eliminado = lista.eliminarPrimero();

        assertEquals(10, eliminado);
        assertEquals(20, lista.obtenerPrimero());
        assertEquals(2, lista.getTamanio());
    }

    @Test
    void debeEliminarElUltimoElemento() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        assertTrue(lista.eliminar(30));

        assertEquals(2, lista.getTamanio());
        assertEquals(20, lista.obtenerUltimo());
    }

    @Test
    void debeEliminarElUnicoElemento() {
        lista.agregar(10);

        assertTrue(lista.eliminar(10));
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.getTamanio());
    }

    @Test
    void debeRecorrerTodosLosElementos() {
        lista.agregar(10);
        lista.agregar(20);
        lista.agregar(30);

        int suma = 0;

        for (Integer numero : lista) {
            suma += numero;
        }

        assertEquals(60, suma);
    }

    @Test
    void noDebePermitirAgregarNulos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lista.agregar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlConsultarListaVacia() {
        assertThrows(
                NoSuchElementException.class,
                () -> lista.obtenerPrimero()
        );

        assertThrows(
                NoSuchElementException.class,
                () -> lista.obtenerUltimo()
        );
    }

    @Test
    void debeVaciarLaLista() {
        lista.agregar(10);
        lista.agregar(20);

        lista.vaciar();

        assertTrue(lista.estaVacia());
        assertEquals(0, lista.getTamanio());
    }
}
