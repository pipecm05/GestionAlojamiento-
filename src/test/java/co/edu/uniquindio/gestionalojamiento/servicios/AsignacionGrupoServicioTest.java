package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionGrupoServicioTest {

    private AsignacionGrupoServicio servicio;
    private LocalDate entrada;
    private LocalDate salida;

    @BeforeEach
    void setUp() {
        servicio = new AsignacionGrupoServicio();
        entrada = LocalDate.of(2026, 12, 10);
        salida = LocalDate.of(2026, 12, 15);
    }

    @Test
    void debeSeleccionarUnSoloApartamentoCuandoTieneCapacidadSuficiente() {
        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        Apartamento pequeno = apartamento("A1", 2);
        Apartamento grande = apartamento("A2", 5);

        disponibles.agregar(pequeno);
        disponibles.agregar(grande);

        ListaDoble<Apartamento> resultado =
                servicio.asignarApartamentos(4, disponibles);

        assertEquals(1, resultado.getTamanio());
        assertTrue(contieneApartamento(resultado, grande));
    }

    @Test
    void debeSeleccionarLaMenorCantidadDeApartamentosNecesaria() {
        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        disponibles.agregar(apartamento("A1", 2));
        disponibles.agregar(apartamento("A2", 2));
        disponibles.agregar(apartamento("A3", 3));

        ListaDoble<Apartamento> resultado =
                servicio.asignarApartamentos(4, disponibles);

        assertEquals(2, resultado.getTamanio());
    }

    @Test
    void debeLanzarExcepcionCuandoNoHayCapacidadSuficiente() {
        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        disponibles.agregar(apartamento("A1", 2));
        disponibles.agregar(apartamento("A2", 1));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.asignarApartamentos(5, disponibles));
    }

    @Test
    void noDebeAceptarCantidadDePersonasCeroONegativa() {
        ListaDoble<Apartamento> disponibles = new ListaDoble<>();

        assertThrows(IllegalArgumentException.class,
                () -> servicio.asignarApartamentos(0, disponibles));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.asignarApartamentos(-2, disponibles));
    }

    @Test
    void noDebeAceptarListaDeApartamentosNula() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.asignarApartamentos(2, null));
    }

    @Test
    void debeFiltrarApartamentosQueNoEstanPreparados() {
        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        Apartamento preparado = apartamento("A1", 4);
        Apartamento ocupado = apartamento("A2", 4);

        ocupado.cambiarEstado(EstadoApartamento.OCUPADO, null);

        apartamentos.agregar(preparado);
        apartamentos.agregar(ocupado);

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        ListaDoble<Apartamento> resultado =
                servicio.asignarApartamentosDisponibles(
                        3, entrada, salida, apartamentos, reservas);

        assertEquals(1, resultado.getTamanio());
        assertTrue(contieneApartamento(resultado, preparado));
        assertFalse(contieneApartamento(resultado, ocupado));
    }

    @Test
    void debeExcluirApartamentoFueraDeServicio() {
        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        Apartamento disponible = apartamento("A1", 4);
        Apartamento fueraServicio = apartamento("A2", 4);

        fueraServicio.cambiarEstado(
                EstadoApartamento.FUERA_DE_SERVICIO,
                "Mantenimiento");

        apartamentos.agregar(disponible);
        apartamentos.agregar(fueraServicio);

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        ListaDoble<Apartamento> resultado =
                servicio.asignarApartamentosDisponibles(
                        2, entrada, salida, apartamentos, reservas);

        assertEquals(1, resultado.getTamanio());
        assertTrue(contieneApartamento(resultado, disponible));
    }

    @Test
    void debeExcluirApartamentoConReservaEnLasMismasFechas() {
        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        Apartamento reservado = apartamento("A1", 4);
        Apartamento libre = apartamento("A2", 4);

        apartamentos.agregar(reservado);
        apartamentos.agregar(libre);

        Huesped huesped = huesped("1001");

        Reserva reserva = new Reserva(
                "R1",
                reservado,
                huesped,
                entrada,
                salida,
                CanalReserva.DIRECTA
        );

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        reservas.insertar(reserva);

        ListaDoble<Apartamento> resultado =
                servicio.asignarApartamentosDisponibles(
                        2, entrada, salida, apartamentos, reservas);

        assertEquals(1, resultado.getTamanio());
        assertTrue(contieneApartamento(resultado, libre));
        assertFalse(contieneApartamento(resultado, reservado));
    }

    @Test
    void debeCrearGrupoYAsignarTodosLosHuespedes() {
        ListaDoble<Huesped> huespedes = new ListaDoble<>();

        huespedes.agregar(huesped("1001"));
        huespedes.agregar(huesped("1002"));
        huespedes.agregar(huesped("1003"));

        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        apartamentos.agregar(apartamento("A1", 3));

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        AsignacionGrupo grupo = servicio.crearAsignacionGrupo(
                "G1",
                huespedes,
                entrada,
                salida,
                apartamentos,
                reservas
        );

        assertEquals("G1", grupo.getCodigo());
        assertEquals(3, grupo.getHuespedes().getTamanio());
        assertEquals(1, grupo.getApartamentos().getTamanio());
        assertEquals(3, grupo.getAsignaciones().getTamanio());
        assertTrue(grupo.estanTodosLosHuespedesAsignados());
    }

    @Test
    void debeDistribuirHuespedesEnVariosApartamentosCuandoEsNecesario() {
        ListaDoble<Huesped> huespedes = new ListaDoble<>();

        huespedes.agregar(huesped("1001"));
        huespedes.agregar(huesped("1002"));
        huespedes.agregar(huesped("1003"));
        huespedes.agregar(huesped("1004"));

        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        apartamentos.agregar(apartamento("A1", 2));
        apartamentos.agregar(apartamento("A2", 2));

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        AsignacionGrupo grupo = servicio.crearAsignacionGrupo(
                "G2",
                huespedes,
                entrada,
                salida,
                apartamentos,
                reservas
        );

        assertEquals(2, grupo.getApartamentos().getTamanio());
        assertEquals(4, grupo.getAsignaciones().getTamanio());
        assertTrue(grupo.estanTodosLosHuespedesAsignados());
    }

    @Test
    void noDebeCrearGrupoSinHuespedes() {
        ListaDoble<Huesped> huespedes = new ListaDoble<>();
        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        apartamentos.agregar(apartamento("A1", 3));

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        assertThrows(IllegalArgumentException.class,
                () -> servicio.crearAsignacionGrupo(
                        "G1",
                        huespedes,
                        entrada,
                        salida,
                        apartamentos,
                        reservas));
    }

    @Test
    void debeRechazarFechasInvalidas() {
        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        apartamentos.agregar(apartamento("A1", 3));

        ArbolBinarioBusqueda<Reserva> reservas =
                new ArbolBinarioBusqueda<>();

        assertThrows(RuntimeException.class,
                () -> servicio.asignarApartamentosDisponibles(
                        2,
                        salida,
                        entrada,
                        apartamentos,
                        reservas));
    }

    @Test
    void debeRechazarApartamentosNulosEnBusqueda() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.asignarApartamentosDisponibles(
                        2,
                        entrada,
                        salida,
                        null,
                        new ArbolBinarioBusqueda<Reserva>()));
    }

    private Apartamento apartamento(String codigo, int capacidad) {
        return new Apartamento(
                codigo,
                "Apartamento " + codigo,
                capacidad,
                2,
                100000,
                150000,
                200000
        );
    }

    private Huesped huesped(String documento) {
        return new Huesped(
                documento,
                "Nombre",
                "Apellido",
                "3001234567",
                documento + "@correo.com"
        );
    }

    private boolean contieneApartamento(
            Iterable<Apartamento> apartamentos,
            Apartamento buscado) {

        for (Apartamento apartamento : apartamentos) {
            if (apartamento == buscado) {
                return true;
            }
        }

        return false;
    }
}
