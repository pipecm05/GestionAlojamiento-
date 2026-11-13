package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudCancelacion;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoPreparacion;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionHuespedApartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.modelo.RegistroFolio;
import co.edu.uniquindio.gestionalojamiento.modelo.SolicitudCancelacion;
import java.time.LocalDateTime;
import co.edu.uniquindio.gestionalojamiento.estructuras.ColaPrioridad;
import java.util.Comparator;
import co.edu.uniquindio.gestionalojamiento.modelo.MovimientoFolio;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class ReservaServicio {

    private final ArbolBinarioBusqueda<Reserva> reservas;
    private final DisponibilidadServicio disponibilidadServicio;
    private final PreparacionServicio preparacionServicio;

    private final ListaDoble<IndiceReservasApartamento> indicesPorApartamento;
    private final ListaDoble<RegistroFolio> foliosPorReserva;
    private final ColaPrioridad<Reserva> colaVencimientos;
    private final List<SolicitudCancelacion> solicitudesCancelacion;

    public ReservaServicio() {
        reservas = new ArbolBinarioBusqueda<>();
        disponibilidadServicio = new DisponibilidadServicio();
        indicesPorApartamento = new ListaDoble<>();
        foliosPorReserva = new ListaDoble<>();
        preparacionServicio = new PreparacionServicio(
                new ComparadorApartamentoPreparacion(reservas));

        colaVencimientos = new ColaPrioridad<>(10,
                Comparator.comparing(Reserva::getFechaVencimiento).thenComparing(Reserva::getCodigo));
        solicitudesCancelacion = new ArrayList<>();

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

        // Comprobar que el apartamento esté habilitado
        // para aceptar reservas.
        if (apartamento.getEstado()
                == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalArgumentException(
                    "El apartamento está fuera de servicio.");
        }

        // Obtener el índice propio del apartamento.
        IndiceReservasApartamento indice =
                obtenerOCrearIndice(apartamento);

        // Comprobar las fechas usando su ABB.
        if (!indice.estaLibre(fechaEntrada, fechaSalida)) {
            throw new IllegalArgumentException(
                    "El apartamento no está disponible para esas fechas.");
        }

        Reserva reserva = new Reserva(
                codigo, apartamento, huesped,
                fechaEntrada, fechaSalida, canal);

        // Registrar primero en el árbol general.
        if (!reservas.insertar(reserva)) {
            throw new IllegalStateException(
                    "No fue posible registrar la reserva.");
        }

        // Registrar también en el índice del apartamento.
        // Si falla, revertir la inserción anterior.
        try {
            if (!indice.registrar(reserva)) {
                throw new IllegalStateException(
                        "No fue posible actualizar el índice.");
            }
        } catch (RuntimeException e) {
            reservas.eliminar(reserva);
            throw e;
        }


        if (reserva.getCanal() == CanalReserva.PORTAL) {
            colaVencimientos.insertar(reserva);
        }

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
            throw new IllegalArgumentException("El grupo es obligatorio.");
        }

        if (grupo.getHuespedes().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener huéspedes.");
        }

        if (grupo.getApartamentos().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener apartamentos asignados.");
        }

        if (grupo.getAsignaciones().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener huéspedes asignados a sus apartamentos.");
        }

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva es obligatorio.");
        }

        validarFechas(fechaEntrada, fechaSalida);
        validarAsignacionesGrupo(grupo);

        // Primera fase: validar todos los apartamentos sin crear índices.
        ListaDoble<String> codigosRevisados = new ListaDoble<>();

        for (Apartamento apartamento : grupo.getApartamentos()) {
            if (apartamento == null) {
                throw new IllegalArgumentException(
                        "El grupo contiene un apartamento nulo.");
            }

            for (String codigoRevisado : codigosRevisados) {
                if (codigoRevisado.equals(apartamento.getCodigo())) {
                    throw new IllegalArgumentException(
                            "El apartamento está repetido en el grupo.");
                }
            }
            codigosRevisados.agregar(apartamento.getCodigo());

            String codigoReserva = codigoBase + "-" + apartamento.getCodigo();
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

            if (apartamento.getEstado() == EstadoApartamento.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " está fuera de servicio.");
            }

            IndiceReservasApartamento indice =
                    buscarIndice(apartamento.getCodigo());

            if (indice != null
                    && !indice.estaLibre(fechaEntrada, fechaSalida)) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " no está disponible para esas fechas.");
            }
        }

        // Segunda fase: construir todas las reservas antes de registrarlas.
        ListaDoble<Reserva> nuevasReservas = new ListaDoble<>();

        for (Apartamento apartamento : grupo.getApartamentos()) {
            String codigoReserva = codigoBase + "-" + apartamento.getCodigo();
            ListaDoble<Huesped> huespedesApartamento =
                    obtenerHuespedesDelApartamento(grupo, apartamento);

            nuevasReservas.agregar(new Reserva(
                    codigoReserva,
                    apartamento,
                    huespedesApartamento,
                    fechaEntrada,
                    fechaSalida,
                    canal));
        }

        // Tercera fase: registrar y revertir todo si ocurre un error.
        ListaDoble<Reserva> registradas = new ListaDoble<>();

        for (Reserva reserva : nuevasReservas) {
            IndiceReservasApartamento indice =
                    obtenerOCrearIndice(reserva.getApartamento());

            boolean insertadaGeneral = false;
            boolean insertadaIndice = false;

            try {
                insertadaGeneral = insertarEnArbolGeneral(reserva);
                if (!insertadaGeneral) {
                    throw new IllegalStateException(
                            "No se pudo registrar " + reserva.getCodigo());
                }

                insertadaIndice = indice.registrar(reserva);
                if (!insertadaIndice) {
                    throw new IllegalStateException(
                            "No se pudo actualizar el índice de "
                                    + reserva.getCodigo());
                }

                registradas.agregar(reserva);

            } catch (RuntimeException e) {
                if (insertadaIndice) {
                    indice.eliminar(reserva);
                }
                if (insertadaGeneral) {
                    reservas.eliminar(reserva);
                }

                for (Reserva anterior : registradas) {
                    reservas.eliminar(anterior);
                    IndiceReservasApartamento indiceAnterior =
                            buscarIndice(anterior.getApartamento().getCodigo());
                    if (indiceAnterior != null) {
                        indiceAnterior.eliminar(anterior);
                    }
                }

                throw e;
            }
        }

        // Las reservas del portal se agregan a la cola al finalizar el registro.
        for (Reserva reserva : nuevasReservas) {
            if (reserva.getCanal() == CanalReserva.PORTAL) {
                colaVencimientos.insertar(reserva);
            }
        }

        return nuevasReservas;
    }

    private void validarAsignacionesGrupo(AsignacionGrupo grupo) {
        if (!grupo.estanTodosLosHuespedesAsignados()) {
            throw new IllegalArgumentException(
                    "Todos los huéspedes del grupo deben tener un apartamento asignado.");
        }

        ListaDoble<String> documentosAsignados = new ListaDoble<>();

        for (AsignacionHuespedApartamento asignacion : grupo.getAsignaciones()) {
            if (asignacion == null
                    || asignacion.getHuesped() == null
                    || asignacion.getApartamento() == null) {
                throw new IllegalArgumentException(
                        "El grupo contiene una asignación inválida.");
            }

            Huesped huesped = asignacion.getHuesped();
            Apartamento apartamento = asignacion.getApartamento();

            if (!grupo.perteneceHuesped(huesped)) {
                throw new IllegalArgumentException(
                        "Una asignación contiene un huésped que no pertenece al grupo.");
            }

            if (!grupo.perteneceApartamento(apartamento)) {
                throw new IllegalArgumentException(
                        "Una asignación contiene un apartamento que no pertenece al grupo.");
            }

            if (documentosAsignados.contiene(huesped.getDocumento())) {
                throw new IllegalArgumentException(
                        "Un huésped tiene más de una asignación.");
            }

            documentosAsignados.agregar(huesped.getDocumento());
        }
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

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        validarFechas(fechaEntrada, fechaSalida);

        // Un apartamento fuera de servicio
        // no acepta nuevas reservas.
        if (apartamento.getEstado()
                == EstadoApartamento.FUERA_DE_SERVICIO) {
            return false;
        }

        // Buscar el índice del apartamento.
        IndiceReservasApartamento indice =
                buscarIndice(apartamento.getCodigo());

        // Si todavía no tiene índice,
        // no hay reservas registradas en él.
        if (indice == null) {
            return true;
        }

        // Consultar su árbol de reservas.
        return indice.estaLibre(fechaEntrada, fechaSalida);
    }



    public void confirmarReserva(String codigo) {

        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        if (reserva.getCanal() == CanalReserva.PORTAL) {
            throw new IllegalStateException(
                    "Las reservas del portal requieren verificar "
                            + "el anticipo con el servicio de tarifas.");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }



    public void confirmarReserva(
            String codigo,
            TarifaServicio tarifaServicio) {

        confirmarReserva(
                codigo,
                tarifaServicio,
                LocalDateTime.now()
        );
    }


    void confirmarReserva(
            String codigo,
            TarifaServicio tarifaServicio,
            LocalDateTime fechaActual) {

        Reserva reserva = buscarReservaObligatoria(codigo);

        if (fechaActual == null) {
            throw new IllegalArgumentException(
                    "La fecha actual es obligatoria.");
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        if (reserva.getCanal() != CanalReserva.PORTAL) {
            confirmarReserva(codigo);
            return;
        }

        if (reserva.estaVencidaEn(fechaActual)) {
            throw new IllegalStateException(
                    "La reserva del portal superó las 24 horas "
                            + "y ya no puede confirmarse.");
        }

        if (tarifaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de tarifas es obligatorio.");
        }

        FolioServicio folio = obtenerFolio(codigo);

        double anticipoRequerido =
                folio.calcularAnticipoPortal(tarifaServicio);

        if (folio.obtenerPagosNetos() + 0.001
                < anticipoRequerido) {
            throw new IllegalStateException(
                    "No se ha registrado el anticipo mínimo "
                            + "del 30 % para confirmar la reserva.");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }


    // -----------------------------------------------------------------
    // SOLICITUDES DE CANCELACIÓN DEL HUÉSPED
    // -----------------------------------------------------------------

    public SolicitudCancelacion solicitarCancelacion(String codigoReserva, String motivo) {
        Reserva reserva = buscarReservaObligatoria(codigoReserva);

        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.FINALIZADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW
                || reserva.getEstado() == EstadoReserva.EN_CURSO) {
            throw new IllegalStateException(
                    "La reserva no admite solicitudes de cancelación en su estado actual.");
        }

        for (SolicitudCancelacion solicitud : solicitudesCancelacion) {
            if (solicitud.getReserva().getCodigo().equals(codigoReserva)
                    && solicitud.getEstado() == EstadoSolicitudCancelacion.PENDIENTE) {
                throw new IllegalStateException(
                        "Esta reserva ya tiene una solicitud de cancelación pendiente.");
            }
        }

        SolicitudCancelacion solicitud = new SolicitudCancelacion(
                "CAN-" + System.currentTimeMillis(), reserva, motivo);
        solicitudesCancelacion.add(solicitud);
        return solicitud;
    }

    public List<SolicitudCancelacion> listarSolicitudesCancelacion() {
        return new ArrayList<>(solicitudesCancelacion);
    }

    public void aprobarSolicitudCancelacion(String codigoSolicitud, String respuesta) {
        SolicitudCancelacion solicitud = buscarSolicitudCancelacion(codigoSolicitud);
        if (solicitud.getEstado() != EstadoSolicitudCancelacion.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue atendida.");
        }
        // La cancelación libera las fechas del apartamento en el índice de reservas.
        cancelarReserva(solicitud.getReserva().getCodigo());
        solicitud.responder(EstadoSolicitudCancelacion.APROBADA, respuesta);
    }

    public void rechazarSolicitudCancelacion(String codigoSolicitud, String respuesta) {
        SolicitudCancelacion solicitud = buscarSolicitudCancelacion(codigoSolicitud);
        solicitud.responder(EstadoSolicitudCancelacion.RECHAZADA, respuesta);
    }

    private SolicitudCancelacion buscarSolicitudCancelacion(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Selecciona una solicitud válida.");
        }
        for (SolicitudCancelacion solicitud : solicitudesCancelacion) {
            if (solicitud.getCodigo().equals(codigo)) {
                return solicitud;
            }
        }
        throw new IllegalArgumentException("No se encontró la solicitud seleccionada.");
    }

    public void cancelarReserva(String codigo) {

        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() == EstadoReserva.FINALIZADA
                || reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar en su estado actual.");
        }

        IndiceReservasApartamento indice =
                buscarIndice(reserva.getApartamento().getCodigo());

        if (indice == null) {
            throw new IllegalStateException(
                    "No existe el índice del apartamento.");
        }

        if (!indice.eliminar(reserva)) {
            throw new IllegalStateException(
                    "No fue posible liberar las fechas de la reserva.");
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


    private IndiceReservasApartamento buscarIndice(
            String codigoApartamento) {

        for (IndiceReservasApartamento indice
                : indicesPorApartamento) {

            if (indice.getCodigoApartamento()
                    .equals(codigoApartamento)) {
                return indice;
            }
        }

        return null;
    }


    private IndiceReservasApartamento obtenerOCrearIndice(
            Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        IndiceReservasApartamento indice =
                buscarIndice(apartamento.getCodigo());

        if (indice != null) {
            return indice;
        }

        indice = new IndiceReservasApartamento(apartamento);
        indicesPorApartamento.agregar(indice);

        return indice;
    }

    protected boolean insertarEnArbolGeneral(Reserva reserva) {
        return reservas.insertar(reserva);
    }


    public double cotizarReserva(
            String codigo,
            TarifaServicio tarifaServicio) {

        if (tarifaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de tarifas es obligatorio.");
        }

        Reserva reserva = buscarReservaObligatoria(codigo);

        return tarifaServicio.calcularTotal(reserva);
    }

    public FolioServicio obtenerFolio(String codigoReserva) {

        Reserva reserva = buscarReservaObligatoria(codigoReserva);

        for (RegistroFolio registro : foliosPorReserva) {

            if (registro.getCodigoReserva().equals(codigoReserva)) {
                return registro.getFolio();
            }
        }

        FolioServicio nuevoFolio = new FolioServicio(reserva);

        foliosPorReserva.agregar(
                new RegistroFolio(codigoReserva, nuevoFolio)
        );

        return nuevoFolio;
    }



    public int vencerReservasPendientes(LocalDateTime fechaActual) {

        if (fechaActual == null) {
            throw new IllegalArgumentException(
                    "La fecha actual es obligatoria.");
        }

        int cantidadVencidas = 0;

        while (!colaVencimientos.estaVacia()) {

            Reserva reserva = colaVencimientos.consultarPrioridad();

            // Una reserva confirmada o cancelada ya no debe vencer.
            // Se descarta de la cola cuando llega a la prioridad.
            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
                colaVencimientos.extraerPrioridad();
                continue;
            }

            // La reserva más próxima todavía no ha vencido.
            // Por el orden del heap, ninguna posterior ha vencido.
            if (!reserva.estaVencidaEn(fechaActual)) {
                break;
            }

            // Reutilizar la cancelación y la liberación del índice.
            cancelarReserva(reserva.getCodigo());

            // Solo retirar de la cola después de cancelar.
            colaVencimientos.extraerPrioridad();

            cantidadVencidas++;
        }

        return cantidadVencidas;
    }


    public void registrarAbonoPortal(
            String codigoReserva,
            String codigoPago,
            double valor) {

        Reserva reserva = buscarReservaObligatoria(codigoReserva);

        if (reserva.getCanal() != CanalReserva.PORTAL) {
            throw new IllegalStateException(
                    "Esta operación solo aplica a reservas del portal.");
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se admiten anticipos en reservas pendientes.");
        }

        if (reserva.estaVencidaEn(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "La reserva superó las 24 horas permitidas.");
        }

        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    "El valor del abono debe ser positivo y válido.");
        }

        FolioServicio folio = obtenerFolio(codigoReserva);

        folio.registrarPago(
                codigoPago,
                "Abono recibido para reserva del portal",
                valor
        );
    }


    public void registrarCargoAlojamiento(
            String codigoReserva,
            TarifaServicio tarifaServicio) {

        Reserva reserva = buscarReservaObligatoria(codigoReserva);

        if (tarifaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de tarifas es obligatorio.");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new IllegalStateException(
                    "No se puede generar el cargo en una reserva cancelada.");
        }

        FolioServicio folio = obtenerFolio(codigoReserva);

        String codigoCargo = "ALOJ-" + codigoReserva;

        for (var movimiento : folio.listarMovimientos()) {
            if (movimiento.getCodigo().equals(codigoCargo)) {
                throw new IllegalStateException(
                        "El cargo de alojamiento ya está registrado.");
            }
        }

        double total = tarifaServicio.calcularTotal(reserva);

        if (total <= 0) {
            throw new IllegalStateException(
                    "La reserva no tiene un cargo positivo de alojamiento.");
        }

        folio.registrarCargo(
                codigoCargo,
                "Cargo total del alojamiento",
                total
        );
    }


    public void registrarPagoSaldo(
            String codigoReserva,
            String codigoPago,
            double valor) {

        Reserva reserva = buscarReservaObligatoria(codigoReserva);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA
                && reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se admiten pagos de saldo en reservas "
                            + "confirmadas o en curso.");
        }

        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    "El pago debe tener un valor positivo y finito.");
        }

        FolioServicio folio = obtenerFolio(codigoReserva);

        String codigoCargo = "ALOJ-" + codigoReserva;
        boolean cargoRegistrado = false;

        for (var movimiento : folio.listarMovimientos()) {
            if (movimiento.getCodigo().equals(codigoCargo)) {
                cargoRegistrado = true;
                break;
            }
        }

        if (!cargoRegistrado) {
            throw new IllegalStateException(
                    "Primero debe registrarse el cargo del alojamiento.");
        }

        double saldo = folio.obtenerSaldo();

        if (saldo <= 0.001) {
            throw new IllegalStateException(
                    "La reserva no tiene saldo pendiente.");
        }

        if (valor > saldo + 0.001) {
            throw new IllegalArgumentException(
                    "El pago supera el saldo pendiente de la reserva.");
        }

        folio.registrarPago(
                codigoPago,
                "Pago del saldo de alojamiento",
                valor
        );
    }

}
