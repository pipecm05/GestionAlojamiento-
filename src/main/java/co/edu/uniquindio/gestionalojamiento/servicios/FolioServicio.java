package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.estructuras.Pila;
import co.edu.uniquindio.gestionalojamiento.modelo.MovimientoFolio;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;

import java.time.LocalDateTime;

public class FolioServicio {

    private final Reserva reserva;
    private final ListaDoble<MovimientoFolio> movimientos;
    private final Pila<MovimientoFolio> operaciones;
    private double pagosNetos = 0;

    public FolioServicio(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria.");
        }

        this.reserva = reserva;
        this.movimientos = new ListaDoble<>();
        this.operaciones = new Pila<>();
    }

    public MovimientoFolio registrarCargo(
            String codigo, String descripcion, double valor) {

        validarMovimiento(codigo, valor);

        MovimientoFolio movimiento = new MovimientoFolio(
                codigo,
                descripcion,
                valor,
                LocalDateTime.now(),
                reserva
        );

        movimientos.agregar(movimiento);
        operaciones.apilar(movimiento);

        return movimiento;
    }

    public MovimientoFolio registrarPago(
            String codigo, String descripcion, double valor) {

        validarMovimiento(codigo, valor);

        MovimientoFolio movimiento = new MovimientoFolio(
                codigo,
                descripcion,
                -valor,
                LocalDateTime.now(),
                reserva
        );

        movimientos.agregar(movimiento);
        operaciones.apilar(movimiento);
        pagosNetos += valor;

        return movimiento;
    }

    public MovimientoFolio deshacerUltimaOperacion(
            String codigoReversion) {

        if (operaciones.estaVacia()) {
            throw new IllegalStateException(
                    "No existen operaciones para deshacer.");
        }

        if (codigoReversion == null
                || codigoReversion.isBlank()
                || existeCodigo(codigoReversion)) {
            throw new IllegalArgumentException(
                    "El código de reversión no es válido.");
        }

        MovimientoFolio anterior = operaciones.consultarCima();

        if (anterior.getValor() < 0) {
            pagosNetos += anterior.getValor();
        }

        MovimientoFolio reversion = new MovimientoFolio(
                codigoReversion,
                "Reversión de " + anterior.getCodigo(),
                -anterior.getValor(),
                LocalDateTime.now(),
                reserva
        );

        movimientos.agregar(reversion);
        operaciones.desapilar();

        return reversion;
    }

    public double obtenerSaldo() {

        double saldo = 0;

        for (MovimientoFolio movimiento : movimientos) {
            saldo += movimiento.getValor();
        }

        return saldo;
    }

    public int cantidadMovimientos() {
        return movimientos.getTamanio();
    }

    public ListaDoble<MovimientoFolio> listarMovimientos() {

        ListaDoble<MovimientoFolio> copia = new ListaDoble<>();

        for (MovimientoFolio movimiento : movimientos) {
            copia.agregar(movimiento);
        }

        return copia;
    }

    private void validarMovimiento(String codigo, double valor) {

        if (codigo == null || codigo.isBlank()
                || existeCodigo(codigo)) {
            throw new IllegalArgumentException(
                    "El código del movimiento no es válido.");
        }

        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    "El valor debe ser positivo y finito.");
        }
    }

    private boolean existeCodigo(String codigo) {

        for (MovimientoFolio movimiento : movimientos) {
            if (movimiento.getCodigo().equals(codigo)) {
                return true;
            }
        }

        return false;
    }


    public double calcularAnticipoPortal(
            TarifaServicio tarifaServicio) {

        if (tarifaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de tarifas es obligatorio.");
        }

        if (reserva.getCanal() != CanalReserva.PORTAL) {
            throw new IllegalStateException(
                    "El anticipo del 30 % aplica a reservas del portal.");
        }

        double totalReserva =
                tarifaServicio.calcularTotal(reserva);

        return Math.round(totalReserva * 0.30 * 100.0) / 100.0;
    }


    public MovimientoFolio registrarPagoAnticipoPortal(
            String codigo,
            TarifaServicio tarifaServicio) {

        double anticipo =
                calcularAnticipoPortal(tarifaServicio);

        if (anticipo <= 0) {
            throw new IllegalStateException(
                    "No existe un anticipo positivo por registrar.");
        }

        if (pagosNetos > 0) {
            throw new IllegalStateException(
                    "Ya existen pagos registrados en el folio. "
                            + "Verifique el anticipo pendiente.");
        }

        return registrarPago(
                codigo,
                "Anticipo del 30 % de la reserva",
                anticipo);
    }

    public double obtenerPagosNetos() {
        return pagosNetos;
    }

}
