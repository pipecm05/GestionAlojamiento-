package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoPreparacion;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionHuespedApartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class ReservaServicio {

    private final ArbolBinarioBusqueda<Reserva> reservas;
    private final DisponibilidadServicio disponibilidadServicio;
    private final PreparacionServicio preparacionServicio;

    public ReservaServicio() {
        reservas = new ArbolBinarioBusqueda<>();
        disponibilidadServicio = new DisponibilidadServicio();

        preparacionServicio = new PreparacionServicio(
                new ComparadorApartamentoPreparacion(reservas));
    }

    public Reserva registrarReserva(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        validarCodigoDisponible(codigo);
        validarFechas(fechaEntrada, fechaSalida);

        if (apartamento == null || huesped == null || canal == null) {
            throw new IllegalArgumentException(
                    "El apartamento, el huésped y el canal son obligatorios.");
        }

        if (!disponibilidadServicio.estaDisponible(
                apartamento, fechaEntrada, fechaSalida, reservas)) {
            throw new IllegalArgumentException(
                    "El apartamento no está disponible para esas fechas.");
        }

        Reserva reserva = new Reserva(
                codigo, apartamento, huesped,
                fechaEntrada, fechaSalida, canal);

        reservas.insertar(reserva);
        return reserva;
    }

    /**
     * Registra una reserva por cada apartamento del grupo.
     * Cada reserva contiene únicamente los huéspedes asignados
     * al apartamento correspondiente.
     */
    public ListaDoble<Reserva> registrarReservaGrupal(
            String codigoBase,
            AsignacionGrupo grupo,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        if (codigoBase == null || codigoBase.isBlank()) {
            throw new IllegalArgumentException(
                    "El código base de la reserva es obligatorio.");
        }

        if (grupo == null) {
            throw new IllegalArgumentException(
                    "El grupo es obligatorio.");
        }

        if (grupo.getApartamentos() == null
                || grupo.getApartamentos().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener apartamentos asignados.");
        }

        if (grupo.getAsignaciones() == null
                || grupo.getAsignaciones().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener huéspedes asignados a sus apartamentos.");
        }

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva es obligatorio.");
        }

        validarFechas(fechaEntrada, fechaSalida);

        // Primera fase: validar todo sin registrar ninguna reserva.
        for (Apartamento apartamento : grupo.getApartamentos()) {

            if (apartamento == null) {
                throw new IllegalArgumentException(
                        "El grupo contiene un apartamento nulo.");
            }

            String codigoReserva =
                    codigoBase + "-" + apartamento.getCodigo();

            validarCodigoDisponible(codigoReserva);

            ListaDoble<Huesped> huespedesApartamento =
                    obtenerHuespedesDelApartamento(grupo, apartamento);

            if (huespedesApartamento.estaVacia()) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " no tiene huéspedes asignados.");
            }

            if (huespedesApartamento.getTamanio()
                    > apartamento.getCapacidadMaxima()) {
                throw new IllegalArgumentException(
                        "Se supera la capacidad del apartamento "
                                + apartamento.getCodigo() + ".");
            }

            if (!disponibilidadServicio.estaDisponible(
                    apartamento, fechaEntrada, fechaSalida, reservas)) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " no está disponible para esas fechas.");
            }
        }

        // Segunda fase: construir todas las reservas.
        ListaDoble<Reserva> nuevasReservas = new ListaDoble<>();

        for (Apartamento apartamento : grupo.getApartamentos()) {

            String codigoReserva =
                    codigoBase + "-" + apartamento.getCodigo();

            ListaDoble<Huesped> huespedesApartamento =
                    obtenerHuespedesDelApartamento(grupo, apartamento);

            Reserva reserva = new Reserva(
                    codigoReserva,
                    apartamento,
                    huespedesApartamento,
                    fechaEntrada,
                    fechaSalida,
                    canal);

            nuevasReservas.agregar(reserva);
        }

        // Tercera fase: registrar las reservas en el árbol.
        for (Reserva reserva : nuevasReservas) {
            reservas.insertar(reserva);
        }

        return nuevasReservas;
    }

    private ListaDoble<Huesped> obtenerHuespedesDelApartamento(
            AsignacionGrupo grupo,
            Apartamento apartamento) {

        ListaDoble<Huesped> huespedes = new ListaDoble<>();

        for (AsignacionHuespedApartamento asignacion
                : grupo.getAsignaciones()) {

            if (asignacion.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {

                huespedes.agregar(asignacion.getHuesped());
            }
        }

        return huespedes;
    }

    private void validarCodigoDisponible(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de reserva es obligatorio.");
        }

        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva con el código " + codigo + ".");
        }
    }

    private void validarFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (fechaEntrada == null || fechaSalida == null
                || !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "La fecha de entrada debe ser anterior a la fecha de salida.");
        }
    }

    public Reserva buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equals(codigo)) {
                return reserva;
            }
        }

        return null;
    }

    public ListaDoble<Reserva> listarReservas() {
        return reservas.inorden();
    }

    public boolean estaDisponible(
            Apartamento apartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        return disponibilidadServicio.estaDisponible(
                apartamento, fechaEntrada, fechaSalida, reservas);
    }

    public void confirmarReserva(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }

    public void cancelarReserva(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() == EstadoReserva.FINALIZADA
                || reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar en su estado actual.");
        }

        reserva.cambiarEstado(EstadoReserva.CANCELADA);
    }

    public void registrarCheckIn(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "La reserva debe estar confirmada para realizar el check-in.");
        }

        if (reserva.getApartamento().getEstado()
                != EstadoApartamento.PREPARADO) {
            throw new IllegalStateException(
                    "El apartamento debe estar preparado.");
        }

        reserva.cambiarEstado(EstadoReserva.EN_CURSO);
        reserva.getApartamento().cambiarEstado(
                EstadoApartamento.OCUPADO, null);
    }

    public void registrarCheckOut(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede realizar check-out de una reserva en curso.");
        }

        Apartamento apartamento = reserva.getApartamento();

        reserva.cambiarEstado(EstadoReserva.FINALIZADA);

        apartamento.cambiarEstado(
                EstadoApartamento.PENDIENTE_PREPARACION,
                "Pendiente de limpieza después del check-out.");

        preparacionServicio.agregarApartamento(apartamento);
    }

    private Reserva buscarReservaObligatoria(String codigo) {
        Reserva reserva = buscarPorCodigo(codigo);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "No existe una reserva con el código indicado.");
        }

        return reserva;
    }

    public PreparacionServicio getPreparacionServicio() {
        return preparacionServicio;
    }
}
