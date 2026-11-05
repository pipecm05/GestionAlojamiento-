package co.edu.uniquindio.gestionalojamiento.modelo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HuespedTest {

    @Test
    void debeRegistrarHuespedConCorreoValido() {

        Huesped huesped = new Huesped(
                "1001",
                "Ana",
                "Lopez",
                "3001234567",
                "ana@correo.com",
                LocalDate.of(1995, 5, 10)
        );

        assertEquals("ana@correo.com", huesped.getCorreo());
    }

    @Test
    void debeRechazarCorreoInvalidoEnConstructor() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Huesped(
                        "1002",
                        "Luis",
                        "Perez",
                        "3001234567",
                        "correoinvalido",
                        LocalDate.of(1995, 5, 10)
                )
        );
    }

    @Test
    void debeRechazarCorreoInvalidoAlModificar() {

        Huesped huesped = new Huesped(
                "1003",
                "Carlos",
                "Gomez",
                "3001234567",
                "carlos@correo.com",
                LocalDate.of(1990, 3, 15)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> huesped.setCorreo("correo-sin-arroba")
        );

        assertEquals("carlos@correo.com", huesped.getCorreo());
    }
}
