package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FolioServicioTest {

    private FolioServicio folio;
    private Reserva reserva;

    @BeforeEach
    void preparar() {

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3001234567", "ana@correo.com",
                LocalDate.of(1995, 5, 10)
        );

        reserva = new Reserva(
                "R001",
                apartamento,
                huesped,
                LocalDate.of(2027, 3, 10),
                LocalDate.of(2027, 3, 12),
                CanalReserva.PORTAL
        );

        folio = new FolioServicio(reserva);
    }

    @Test
    void debeRegistrarCargo() {

        folio.registrarCargo(
                "M001", "Alojamiento", 140000);

        assertEquals(140000, folio.obtenerSaldo(), 0.001);
        assertEquals(1, folio.cantidadMovimientos());
    }

    @Test
    void debeRegistrarPago() {

        folio.registrarCargo(
                "M001", "Alojamiento", 140000);

        folio.registrarPago(
                "M002", "Anticipo", 42000);

        assertEquals(98000, folio.obtenerSaldo(), 0.001);
        assertEquals(2, folio.cantidadMovimientos());
    }

    @Test
    void debeRevertirSinBorrarHistorial() {

        folio.registrarCargo(
                "M001", "Alojamiento", 140000);

        folio.registrarPago(
                "M002", "Anticipo", 42000);

        folio.deshacerUltimaOperacion("M003");

        assertEquals(140000, folio.obtenerSaldo(), 0.001);
        assertEquals(3, folio.cantidadMovimientos());
    }

    @Test
    void debeRechazarMovimientoDuplicado() {

        folio.registrarCargo(
                "M001", "Alojamiento", 140000);

        assertThrows(
                IllegalArgumentException.class,
                () -> folio.registrarPago(
                        "M001", "Pago repetido", 20000)
        );

        assertEquals(1, folio.cantidadMovimientos());
    }

    @Test
    void debeRechazarValoresInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> folio.registrarPago(
                        "M002", "Pago inválido", -5000)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> folio.registrarCargo(
                        "M003", "Cargo inválido", 0)
        );
    }

    @Test
    void noDebeRevertirSinOperaciones() {

        assertThrows(
                IllegalStateException.class,
                () -> folio.deshacerUltimaOperacion("M001")
        );
    }


    @Test
    void debeCalcularYRegistrarAnticipoPortal() {

        TarifaServicio tarifas = new TarifaServicio();

        // Un adulto, dos noches de temporada baja:
        // 2 x 50000 = 100000.
        double total = tarifas.calcularTotal(reserva);

        assertEquals(100000.0, total, 0.001);

        double anticipo =
                folio.calcularAnticipoPortal(tarifas);

        assertEquals(30000.0, anticipo, 0.001);

        // Registrar primero el cargo del alojamiento.
        folio.registrarCargo(
                "M001", "Alojamiento", total);

        // Simular que se verificó y recibió el anticipo.
        folio.registrarPagoAnticipoPortal(
                "M002", tarifas);

        assertEquals(
                70000.0,
                folio.obtenerSaldo(),
                0.001);

        assertEquals(2, folio.cantidadMovimientos());
    }


    @Test
    void noDebeRegistrarDosAnticiposAutomaticos() {

        TarifaServicio tarifas = new TarifaServicio();

        folio.registrarPagoAnticipoPortal(
                "P001",
                tarifas
        );

        assertThrows(
                IllegalStateException.class,
                () -> folio.registrarPagoAnticipoPortal(
                        "P002",
                        tarifas
                )
        );

        assertEquals(
                30000.0,
                folio.obtenerPagosNetos(),
                0.001
        );

        assertEquals(
                1,
                folio.cantidadMovimientos()
        );
    }

}
