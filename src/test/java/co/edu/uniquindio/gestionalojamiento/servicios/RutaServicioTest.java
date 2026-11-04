package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutaServicioTest {

    private RutaServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new RutaServicio();

        servicio.agregarUbicacion("Recepcion");
        servicio.agregarUbicacion("Escaleras");
        servicio.agregarUbicacion("Aseo");
        servicio.agregarUbicacion("A101");
        servicio.agregarUbicacion("A102");

        servicio.conectarUbicaciones(
                "Recepcion", "Escaleras", 5);
        servicio.conectarUbicaciones(
                "Escaleras", "A101", 4);
        servicio.conectarUbicaciones(
                "Escaleras", "A102", 7);
        servicio.conectarUbicaciones(
                "Recepcion", "Aseo", 3);
        servicio.conectarUbicaciones(
                "Aseo", "A102", 6);
    }

    @Test
    void debeCalcularRutaMasCorta() {
        ListaDoble<String> ruta =
                servicio.obtenerRutaMasCorta(
                        "Recepcion", "A102");

        assertEquals(3, ruta.getTamanio());

        String[] esperada = {
                "Recepcion", "Aseo", "A102"
        };

        int i = 0;
        for (String ubicacion : ruta) {
            assertEquals(esperada[i++], ubicacion);
        }

        assertEquals(
                9.0,
                servicio.obtenerDistanciaMinima(
                        "Recepcion", "A102"),
                0.0001);
    }

    @Test
    void debeRechazarUbicacionesDuplicadas() {
        assertFalse(
                servicio.agregarUbicacion("Recepcion"));
        assertEquals(5, servicio.cantidadUbicaciones());
    }

    @Test
    void debeRechazarDistanciasInvalidas() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.conectarUbicaciones(
                        "Recepcion", "A101", -5));
    }

    @Test
    void debeRechazarUbicacionInexistente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.obtenerRutaMasCorta(
                        "Recepcion", "A999"));
    }

    @Test
    void debeManejarUbicacionDesconectada() {
        servicio.agregarUbicacion("Bodega");

        assertTrue(
                servicio.obtenerRutaMasCorta(
                        "Recepcion", "Bodega").estaVacia());

        assertTrue(Double.isInfinite(
                servicio.obtenerDistanciaMinima(
                        "Recepcion", "Bodega")));
    }

    @Test
    void debeRecorrerUbicacionesConBfsYDfs() {
        assertEquals(
                5,
                servicio.recorrerBfs(
                        "Recepcion").getTamanio());

        assertEquals(
                5,
                servicio.recorrerDfs(
                        "Recepcion").getTamanio());
    }
}
