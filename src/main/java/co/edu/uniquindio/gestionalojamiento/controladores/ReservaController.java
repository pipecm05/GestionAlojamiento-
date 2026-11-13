
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.modelo.SolicitudCancelacion;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudCancelacion;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;
import co.edu.uniquindio.gestionalojamiento.modelo.DetalleTarifaNoche;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.servicios.FolioServicio;
import java.time.LocalDateTime;
import co.edu.uniquindio.gestionalojamiento.modelo.MovimientoFolio;
import java.util.Locale;
import java.util.UUID;
import java.util.List;
import java.time.format.DateTimeFormatterBuilder;

public class ReservaController {

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;
    private TarifaServicio tarifaServicio;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtCodigo;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbHuesped;
    @FXML private ComboBox<String> cbApartamento;
    @FXML private ComboBox<CanalReserva> cbCanal;
    @FXML private DatePicker dpEntrada;
    @FXML private DatePicker dpSalida;
    @FXML private Label lblEstado;

    // Controles para reservas familiares y grupales.
    @FXML private TextField txtCodigoGrupo;
    @FXML private ComboBox<CanalReserva> cbCanalGrupo;
    @FXML private DatePicker dpEntradaGrupo;
    @FXML private DatePicker dpSalidaGrupo;
    @FXML private ListView<String> lvHuespedesDisponibles;
    @FXML private ListView<String> lvApartamentosDisponibles;
    @FXML private ComboBox<String> cbHuespedGrupo;
    @FXML private ComboBox<String> cbApartamentoGrupo;
    @FXML private ListView<String> lvAsignacionesGrupo;
    @FXML private Label lblEstadoGrupo;

    private AsignacionGrupo grupoActual;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colCodigo;
    @FXML private TableColumn<Reserva, String> colHuesped;
    @FXML private TableColumn<Reserva, String> colApartamento;
    @FXML private TableColumn<Reserva, String> colEntrada;
    @FXML private TableColumn<Reserva, String> colSalida;
    @FXML private TableColumn<Reserva, String> colCanal;
    @FXML private TableColumn<Reserva, String> colEstado;

    @FXML private TableView<SolicitudCancelacion> tablaSolicitudesCancelacion;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudCodigo;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudReserva;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudHuesped;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudMotivo;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudEstado;
    @FXML private TableColumn<SolicitudCancelacion, String> colSolicitudFecha;

    @FXML
    private void initialize() {

        cbCanal.getItems().addAll(
                CanalReserva.DIRECTA,
                CanalReserva.PORTAL
        );

        cbCanal.setValue(CanalReserva.DIRECTA);

        cbCanalGrupo.getItems().setAll(
                CanalReserva.DIRECTA,
                CanalReserva.PORTAL
        );
        cbCanalGrupo.setValue(CanalReserva.DIRECTA);

        lvHuespedesDisponibles.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        lvApartamentosDisponibles.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        colCodigo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getCodigo()));

        colHuesped.setCellValueFactory(d -> {
            StringBuilder nombres = new StringBuilder();
            for (Huesped huesped : d.getValue().getHuespedes()) {
                if (nombres.length() > 0) {
                    nombres.append(", ");
                }
                nombres.append(huesped.getNombreCompleto());
            }
            return new ReadOnlyStringWrapper(nombres.toString());
        });

        colApartamento.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getApartamento()
                                .getCodigo()));

        colEntrada.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getFechaEntrada()
                                .format(FORMATO)));

        colSalida.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getFechaSalida()
                                .format(FORMATO)));

        colCanal.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getCanal().name()));

        colEstado.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getEstado().name()));

        tablaReservas.setPlaceholder(
                new Label("No hay reservas para mostrar.")
        );

        colSolicitudCodigo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getCodigo()));
        colSolicitudReserva.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getReserva().getCodigo()));
        colSolicitudHuesped.setCellValueFactory(d -> {
            StringBuilder nombres = new StringBuilder();
            for (Huesped huesped : d.getValue().getReserva().getHuespedes()) {
                if (nombres.length() > 0) nombres.append(", ");
                nombres.append(huesped.getNombreCompleto());
            }
            return new ReadOnlyStringWrapper(nombres.toString());
        });
        colSolicitudMotivo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getMotivo()));
        colSolicitudEstado.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getEstado().name()));
        colSolicitudFecha.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getFechaSolicitud()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        tablaSolicitudesCancelacion.setPlaceholder(
                new Label("No hay solicitudes de cancelación."));
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            TarifaServicio tarifaServicio) {

        if (catalogo == null
                || reservaServicio == null
                || tarifaServicio == null) {

            throw new IllegalArgumentException(
                    "Los servicios son obligatorios.");
        }

        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;
        this.tarifaServicio = tarifaServicio;

        actualizarDatos();
    }

    @FXML
    private void actualizarDatos() {

        if (catalogo == null || reservaServicio == null) {
            return;
        }

        String huespedSeleccionado = cbHuesped.getValue();
        String apartamentoSeleccionado = cbApartamento.getValue();

        cbHuesped.getItems().clear();
        cbApartamento.getItems().clear();

        for (Huesped huesped : catalogo.listarHuespedes()) {
            cbHuesped.getItems().add(huesped.getDocumento());
        }

        for (Apartamento apartamento :
                catalogo.listarApartamentos()) {
            cbApartamento.getItems().add(apartamento.getCodigo());
        }

        if (cbHuesped.getItems().contains(huespedSeleccionado)) {
            cbHuesped.setValue(huespedSeleccionado);
        }

        if (cbApartamento.getItems().contains(
                apartamentoSeleccionado)) {
            cbApartamento.setValue(apartamentoSeleccionado);
        }

        // Actualizar las listas de selección para reservas grupales.
        lvHuespedesDisponibles.getItems().clear();
        for (Huesped huesped : catalogo.listarHuespedes()) {
            lvHuespedesDisponibles.getItems().add(
                    huesped.getDocumento() + " | "
                            + huesped.getNombreCompleto()
            );
        }

        lvApartamentosDisponibles.getItems().clear();
        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            lvApartamentosDisponibles.getItems().add(
                    apartamento.getCodigo() + " | capacidad: "
                            + apartamento.getCapacidadMaxima()
            );
        }

        mostrarTodas();
    }

    private Huesped obtenerHuesped() {

        String documento = cbHuesped.getValue();

        if (documento == null) {
            throw new IllegalArgumentException(
                    "Selecciona un huésped.");
        }

        Huesped huesped = catalogo.buscarHuesped(documento);

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped seleccionado ya no existe.");
        }

        return huesped;
    }

    private Apartamento obtenerApartamento() {

        String codigo = cbApartamento.getValue();

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "Selecciona un apartamento.");
        }

        Apartamento apartamento =
                catalogo.buscarApartamento(codigo);

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento seleccionado ya no existe.");
        }

        return apartamento;
    }

    private void validarFechas() {

        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();

        if (entrada == null || salida == null
                || !entrada.isBefore(salida)) {
            throw new IllegalArgumentException(
                    "La entrada debe ser anterior a la salida.");
        }
    }

    @FXML
    private void consultarDisponibilidad() {

        try {
            Apartamento apartamento = obtenerApartamento();
            validarFechas();

            boolean disponible = reservaServicio.estaDisponible(
                    apartamento,
                    dpEntrada.getValue(),
                    dpSalida.getValue()
            );

            lblEstado.setText(disponible
                    ? "Apartamento disponible para esas fechas."
                    : "Apartamento no disponible para esas fechas.");

            informar(
                    disponible ? "Disponible" : "No disponible",
                    lblEstado.getText()
            );

        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void registrarReserva() {

        if (catalogo == null || reservaServicio == null) {
            error("Los servicios no están configurados.");
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();

            Huesped huesped = obtenerHuesped();
            Apartamento apartamento = obtenerApartamento();

            validarFechas();

            CanalReserva canal = cbCanal.getValue();

            if (canal == null) {
                throw new IllegalArgumentException(
                        "Selecciona el canal de reserva.");
            }

            Reserva reserva = reservaServicio.registrarReserva(
                    codigo,
                    apartamento,
                    huesped,
                    dpEntrada.getValue(),
                    dpSalida.getValue(),
                    canal
            );

            mostrarTodas();
            txtCodigo.clear();

            lblEstado.setText(
                    "Reserva " + reserva.getCodigo()
                            + " registrada: " + reserva.getEstado());

            informar(
                    "Reserva registrada",
                    lblEstado.getText()
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void buscarReserva() {

        if (reservaServicio == null) {
            return;
        }

        String codigo = txtBuscar.getText().trim();

        if (codigo.isBlank()) {
            error("Ingresa el código de la reserva.");
            return;
        }

        Reserva reserva = reservaServicio.buscarPorCodigo(codigo);

        tablaReservas.getItems().clear();

        if (reserva == null) {
            lblEstado.setText(
                    "No se encontró la reserva: " + codigo);
            return;
        }

        tablaReservas.getItems().add(reserva);
        lblEstado.setText("Reserva encontrada: " + codigo);
    }

    @FXML
    private void mostrarTodas() {

        if (reservaServicio == null) {
            return;
        }

        tablaReservas.getItems().clear();

        for (Reserva reserva : reservaServicio.listarReservas()) {
            tablaReservas.getItems().add(reserva);
        }

        lblEstado.setText(
                "Total de reservas: "
                        + tablaReservas.getItems().size()
        );
        actualizarSolicitudesCancelacion();
    }

    @FXML
    private void actualizarSolicitudesCancelacion() {
        if (reservaServicio == null || tablaSolicitudesCancelacion == null) return;
        tablaSolicitudesCancelacion.getItems().setAll(
                reservaServicio.listarSolicitudesCancelacion());
    }

    @FXML
    private void aprobarSolicitudCancelacion() {
        SolicitudCancelacion solicitud = tablaSolicitudesCancelacion
                .getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            error("Selecciona una solicitud de cancelación.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Aprobar la cancelación de la reserva "
                        + solicitud.getReserva().getCodigo() + "?\n"
                        + "Al aprobar se liberarán las fechas.",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Aprobar cancelación");
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            reservaServicio.aprobarSolicitudCancelacion(
                    solicitud.getCodigo(), "Solicitud aprobada por el administrador.");
            mostrarTodas();
            informar("Cancelación aprobada",
                    "La reserva quedó CANCELADA y sus fechas fueron liberadas.");
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void rechazarSolicitudCancelacion() {
        SolicitudCancelacion solicitud = tablaSolicitudesCancelacion
                .getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            error("Selecciona una solicitud de cancelación.");
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("La reserva se mantiene activa.");
        dialogo.setTitle("Rechazar cancelación");
        dialogo.setHeaderText("Solicitud " + solicitud.getCodigo());
        dialogo.setContentText("Motivo del rechazo:");
        dialogo.showAndWait().ifPresent(respuesta -> {
            try {
                reservaServicio.rechazarSolicitudCancelacion(
                        solicitud.getCodigo(), respuesta);
                actualizarSolicitudesCancelacion();
                informar("Solicitud rechazada",
                        "La solicitud fue RECHAZADA. La reserva conserva su estado y fechas.");
            } catch (RuntimeException ex) {
                error(ex.getMessage());
            }
        });
    }

    // -----------------------------------------------------------------
    // REGISTRO DE RESERVAS FAMILIARES Y GRUPALES
    // -----------------------------------------------------------------

    private AsignacionGrupo obtenerOCrearGrupo() {
        String codigo = txtCodigoGrupo.getText() == null
                ? "" : txtCodigoGrupo.getText().trim();

        if (codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingresa primero el código base del grupo.");
        }

        if (grupoActual == null) {
            grupoActual = new AsignacionGrupo(codigo);
        } else if (!grupoActual.getCodigo().equals(codigo)) {
            throw new IllegalStateException(
                    "Ya comenzaste a construir un grupo con el código "
                            + grupoActual.getCodigo()
                            + ". Limpia el grupo antes de cambiar el código.");
        }

        return grupoActual;
    }

    private String extraerIdentificador(String elemento) {
        if (elemento == null || elemento.isBlank()) {
            throw new IllegalArgumentException(
                    "Selecciona un elemento de la lista.");
        }
        int separador = elemento.indexOf(" | ");
        return separador >= 0
                ? elemento.substring(0, separador).trim()
                : elemento.trim();
    }

    @FXML
    private void agregarHuespedesGrupo() {
        try {
            AsignacionGrupo grupo = obtenerOCrearGrupo();
            var seleccionados =
                    lvHuespedesDisponibles.getSelectionModel()
                            .getSelectedItems();

            if (seleccionados.isEmpty()) {
                throw new IllegalArgumentException(
                        "Selecciona uno o varios huéspedes.");
            }

            int agregados = 0;
            for (String elemento : seleccionados) {
                String documento = extraerIdentificador(elemento);
                Huesped huesped = catalogo.buscarHuesped(documento);
                if (huesped == null) {
                    throw new IllegalArgumentException(
                            "No se encontró el huésped " + documento + ".");
                }
                grupo.agregarHuesped(huesped);
                agregados++;
            }

            actualizarSeleccionGrupo();
            lblEstadoGrupo.setText(
                    agregados + " huésped(es) agregado(s). Total del grupo: "
                            + grupo.getHuespedes().getTamanio());

        } catch (IllegalArgumentException | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void agregarApartamentosGrupo() {
        try {
            AsignacionGrupo grupo = obtenerOCrearGrupo();
            var seleccionados =
                    lvApartamentosDisponibles.getSelectionModel()
                            .getSelectedItems();

            if (seleccionados.isEmpty()) {
                throw new IllegalArgumentException(
                        "Selecciona uno o varios apartamentos.");
            }

            int agregados = 0;
            for (String elemento : seleccionados) {
                String codigo = extraerIdentificador(elemento);
                Apartamento apartamento =
                        catalogo.buscarApartamento(codigo);
                if (apartamento == null) {
                    throw new IllegalArgumentException(
                            "No se encontró el apartamento " + codigo + ".");
                }
                grupo.agregarApartamento(apartamento);
                agregados++;
            }

            actualizarSeleccionGrupo();
            lblEstadoGrupo.setText(
                    agregados + " apartamento(s) agregado(s). Total: "
                            + grupo.getApartamentos().getTamanio());

        } catch (IllegalArgumentException | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

    private void actualizarSeleccionGrupo() {
        cbHuespedGrupo.getItems().clear();
        cbApartamentoGrupo.getItems().clear();
        lvAsignacionesGrupo.getItems().clear();

        if (grupoActual == null) {
            return;
        }

        for (Huesped huesped : grupoActual.getHuespedes()) {
            cbHuespedGrupo.getItems().add(huesped.getDocumento());
        }

        for (Apartamento apartamento : grupoActual.getApartamentos()) {
            cbApartamentoGrupo.getItems().add(apartamento.getCodigo());
        }

        for (var asignacion : grupoActual.getAsignaciones()) {
            lvAsignacionesGrupo.getItems().add(
                    asignacion.getHuesped().getDocumento()
                            + " - "
                            + asignacion.getHuesped().getNombreCompleto()
                            + " → "
                            + asignacion.getApartamento().getCodigo()
            );
        }

        if (!cbHuespedGrupo.getItems().isEmpty()) {
            cbHuespedGrupo.getSelectionModel().selectFirst();
        }
        if (!cbApartamentoGrupo.getItems().isEmpty()) {
            cbApartamentoGrupo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void asignarHuespedGrupo() {
        try {
            if (grupoActual == null) {
                throw new IllegalArgumentException(
                        "Primero agrega huéspedes y apartamentos al grupo.");
            }

            String documento = cbHuespedGrupo.getValue();
            String codigoApartamento = cbApartamentoGrupo.getValue();

            if (documento == null || codigoApartamento == null) {
                throw new IllegalArgumentException(
                        "Selecciona un huésped y un apartamento.");
            }

            Huesped huesped = catalogo.buscarHuesped(documento);
            Apartamento apartamento =
                    catalogo.buscarApartamento(codigoApartamento);

            if (huesped == null || apartamento == null) {
                throw new IllegalArgumentException(
                        "El huésped o apartamento seleccionado no existe.");
            }

            grupoActual.asignarHuesped(huesped, apartamento);
            actualizarSeleccionGrupo();

            lblEstadoGrupo.setText(
                    "Huésped " + huesped.getNombreCompleto()
                            + " asignado a " + apartamento.getCodigo() + ".");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void registrarReservaGrupal() {
        if (catalogo == null || reservaServicio == null) {
            error("Los servicios no están configurados.");
            return;
        }

        try {
            if (grupoActual == null) {
                throw new IllegalArgumentException(
                        "Primero agrega los huéspedes y apartamentos.");
            }

            String codigoBase = txtCodigoGrupo.getText().trim();
            if (codigoBase.isBlank()) {
                throw new IllegalArgumentException(
                        "Ingresa el código base de la reserva grupal.");
            }

            LocalDate entrada = dpEntradaGrupo.getValue();
            LocalDate salida = dpSalidaGrupo.getValue();
            if (entrada == null || salida == null
                    || !entrada.isBefore(salida)) {
                throw new IllegalArgumentException(
                        "La fecha de entrada debe ser anterior a la salida.");
            }

            CanalReserva canal = cbCanalGrupo.getValue();
            if (canal == null) {
                throw new IllegalArgumentException(
                        "Selecciona el canal de la reserva grupal.");
            }

            if (!grupoActual.estanTodosLosHuespedesAsignados()) {
                throw new IllegalArgumentException(
                        "Debes asignar todos los huéspedes a un apartamento.");
            }

            ListaDoble<Reserva> reservasCreadas =
                    reservaServicio.registrarReservaGrupal(
                            codigoBase,
                            grupoActual,
                            entrada,
                            salida,
                            canal
                    );

            StringBuilder codigos = new StringBuilder();
            for (Reserva reserva : reservasCreadas) {
                if (codigos.length() > 0) {
                    codigos.append("\n");
                }
                codigos.append(reserva.getCodigo());
            }

            int cantidad = reservasCreadas.getTamanio();
            limpiarGrupo();
            mostrarTodas();

            lblEstadoGrupo.setText(
                    "Reserva grupal registrada. Reservas creadas: "
                            + cantidad);

            informar(
                    "Reserva familiar o grupal registrada",
                    "Se registraron " + cantidad
                            + " reserva(s), una por apartamento:\n\n"
                            + codigos
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

    @FXML
    private void limpiarGrupo() {
        grupoActual = null;
        txtCodigoGrupo.clear();
        dpEntradaGrupo.setValue(null);
        dpSalidaGrupo.setValue(null);
        cbCanalGrupo.setValue(CanalReserva.DIRECTA);
        cbHuespedGrupo.getItems().clear();
        cbApartamentoGrupo.getItems().clear();
        lvAsignacionesGrupo.getItems().clear();
        lvHuespedesDisponibles.getSelectionModel().clearSelection();
        lvApartamentosDisponibles.getSelectionModel().clearSelection();
        lblEstadoGrupo.setText(
                "Grupo limpio. Puedes comenzar una nueva reserva.");
    }

    // -----------------------------------------------------------------
    // CHECK-IN
    // -----------------------------------------------------------------

    @FXML
    private void checkInSeleccionada() {

        if (reservaServicio == null) {
            error("El servicio de reservas no está configurado.");
            return;
        }

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {
            if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
                throw new IllegalStateException(
                        "Solo se puede realizar el check-in "
                                + "de una reserva CONFIRMADA.");
            }

            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Deseas realizar el check-in de la reserva "
                            + reserva.getCodigo() + "?\n"
                            + "Apartamento: "
                            + reserva.getApartamento().getCodigo(),
                    ButtonType.YES,
                    ButtonType.NO
            );

            confirmacion.setTitle("Confirmar check-in");
            confirmacion.setHeaderText("Entrada del huésped");

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }

            String codigo = reserva.getCodigo();

            reservaServicio.registrarCheckIn(codigo);

            mostrarTodas();

            lblEstado.setText(
                    "Check-in realizado correctamente: " + codigo
            );

            informar(
                    "Check-in realizado",
                    "La reserva " + codigo
                            + " pasó a estado EN_CURSO.\n"
                            + "El apartamento pasó a estado OCUPADO."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // CHECK-OUT
    // -----------------------------------------------------------------

    @FXML
    private void checkOutSeleccionada() {

        if (reservaServicio == null) {
            error("El servicio de reservas no está configurado.");
            return;
        }

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {
            if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
                throw new IllegalStateException(
                        "Solo se puede realizar el check-out "
                                + "de una reserva EN_CURSO.");
            }

            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Deseas realizar el check-out de la reserva "
                            + reserva.getCodigo() + "?\n"
                            + "Apartamento: "
                            + reserva.getApartamento().getCodigo(),
                    ButtonType.YES,
                    ButtonType.NO
            );

            confirmacion.setTitle("Confirmar check-out");
            confirmacion.setHeaderText("Salida del huésped");

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }

            String codigo = reserva.getCodigo();

            reservaServicio.registrarCheckOut(codigo);

            mostrarTodas();

            lblEstado.setText(
                    "Check-out realizado correctamente: " + codigo
            );

            informar(
                    "Check-out realizado",
                    "La reserva " + codigo
                            + " pasó a estado FINALIZADA.\n"
                            + "El apartamento quedó PENDIENTE_PREPARACION "
                            + "y fue agregado a la cola de preparación."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // MENSAJES
    // -----------------------------------------------------------------

    private void error(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void informar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cotizarSeleccionada() {

        if (reservaServicio == null || tarifaServicio == null) {
            error("Los servicios no están configurados.");
            return;
        }

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {

            double total = reservaServicio.cotizarReserva(
                    reserva.getCodigo(),
                    tarifaServicio
            );

            ListaDoble<DetalleTarifaNoche> desglose =
                    tarifaServicio.generarDesglose(reserva);

            StringBuilder mensaje = new StringBuilder();

            mensaje.append("Reserva: ")
                    .append(reserva.getCodigo())
                    .append("\nApartamento: ")
                    .append(reserva.getApartamento().getCodigo())
                    .append("\n\nDESGLOSE POR NOCHE\n\n");

            for (DetalleTarifaNoche detalle : desglose) {

                mensaje.append(detalle.getFecha())
                        .append(" | ")
                        .append(detalle.getTemporada())
                        .append("\nPersonas que pagan: ")
                        .append(detalle.getPersonasQuePagan())
                        .append("\nSubtotal: $")
                        .append(String.format("%.2f",
                                detalle.getSubtotal()))
                        .append("\n\n");
            }

            mensaje.append("TOTAL: $")
                    .append(String.format("%.2f", total));

            if (reserva.getCanal() == CanalReserva.PORTAL) {

                double anticipo =
                        reservaServicio.obtenerFolio(
                                reserva.getCodigo()
                        ).calcularAnticipoPortal(tarifaServicio);

                mensaje.append("\nANTICIPO REQUERIDO (30 %): $")
                        .append(String.format("%.2f", anticipo));
            }

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Cotización de alojamiento");
            alerta.setHeaderText(
                    "Detalle económico de " + reserva.getCodigo()
            );

            TextArea contenido = new TextArea(mensaje.toString());
            contenido.setEditable(false);
            contenido.setWrapText(true);
            contenido.setPrefSize(480, 400);

            alerta.getDialogPane().setContent(contenido);
            alerta.showAndWait();

            lblEstado.setText(
                    "Cotización consultada: " + reserva.getCodigo()
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    @FXML
    private void registrarPagoSeleccionada() {

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva en la tabla.");
            return;
        }

        try {

            if (reserva.getCanal() != CanalReserva.PORTAL) {
                throw new IllegalStateException(
                        "Esta operación es exclusiva de reservas del portal.");
            }

            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
                throw new IllegalStateException(
                        "La reserva debe estar pendiente.");
            }

            if (reserva.estaVencidaEn(java.time.LocalDateTime.now())) {
                throw new IllegalStateException(
                        "La reserva superó el plazo de 24 horas.");
            }

            FolioServicio folio =
                    reservaServicio.obtenerFolio(reserva.getCodigo());

            double anticipo =
                    folio.calcularAnticipoPortal(tarifaServicio);

            double pagado = folio.obtenerPagosNetos();

            double faltante = Math.max(0, anticipo - pagado);

            if (faltante <= 0.001) {
                informar(
                        "Anticipo cubierto",
                        "Los pagos registrados ya cubren el 30 % requerido."
                );
                return;
            }

            TextInputDialog dialogo = new TextInputDialog(
                    String.format(java.util.Locale.US, "%.2f", faltante)
            );

            dialogo.setTitle("Registrar pago");
            dialogo.setHeaderText(
                    "Registrar abono de la reserva "
                            + reserva.getCodigo()
            );

            dialogo.setContentText(
                    "Anticipo requerido: $" + anticipo
                            + "\nPagado: $" + pagado
                            + "\nFaltante: $" + faltante
                            + "\n\nValor recibido:"
            );

            java.util.Optional<String> respuesta =
                    dialogo.showAndWait();

            if (respuesta.isEmpty()) {
                return;
            }

            double valor = Double.parseDouble(
                    respuesta.get().trim().replace(",", ".")
            );

            if (!Double.isFinite(valor) || valor <= 0) {
                throw new IllegalArgumentException(
                        "El valor debe ser positivo.");
            }

            Alert verificacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Confirmas que recibiste y verificaste un pago de $"
                            + String.format("%.2f", valor) + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            verificacion.setTitle("Verificación de pago");
            verificacion.setHeaderText(
                    "Esta acción registrará un movimiento económico."
            );

            if (verificacion.showAndWait()
                    .orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }

            String codigoPago = "P-" + UUID.randomUUID();

            reservaServicio.registrarAbonoPortal(
                    reserva.getCodigo(),
                    codigoPago,
                    valor
            );

            double nuevoTotal =
                    folio.obtenerPagosNetos();

            lblEstado.setText(
                    "Pago registrado. Total abonado: $"
                            + String.format("%.2f", nuevoTotal)
            );

            informar(
                    "Pago registrado",
                    "Abono registrado correctamente.\n"
                            + "Total pagado: $"
                            + String.format("%.2f", nuevoTotal)
                            + "\nAnticipo requerido: $"
                            + String.format("%.2f", anticipo)
            );

        } catch (NumberFormatException ex) {

            error("Ingresa un valor numérico válido.");

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    @FXML
    private void confirmarSeleccionada() {

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {

            if (reserva.getCanal() != CanalReserva.PORTAL) {
                throw new IllegalStateException(
                        "Esta confirmación de pagos solo aplica al portal.");
            }

            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
                throw new IllegalStateException(
                        "La reserva debe estar pendiente.");
            }

            FolioServicio folio =
                    reservaServicio.obtenerFolio(reserva.getCodigo());

            double anticipo =
                    folio.calcularAnticipoPortal(tarifaServicio);

            double pagado =
                    folio.obtenerPagosNetos();

            if (pagado + 0.001 < anticipo) {
                throw new IllegalStateException(
                        "Anticipo insuficiente.\n"
                                + "Requerido: $" + anticipo
                                + "\nRegistrado: $" + pagado
                                + "\nFaltante: $"
                                + (anticipo - pagado)
                );
            }

            reservaServicio.confirmarReserva(
                    reserva.getCodigo(),
                    tarifaServicio
            );

            mostrarTodas();

            lblEstado.setText(
                    "Reserva confirmada: " + reserva.getCodigo()
            );

            informar(
                    "Reserva confirmada",
                    "El anticipo mínimo fue cubierto.\n"
                            + "La reserva quedó CONFIRMADA."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    @FXML
    private void consultarFolioSeleccionada() {

        if (reservaServicio == null || tarifaServicio == null) {
            error("Los servicios no están configurados.");
            return;
        }

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {

            FolioServicio folio =
                    reservaServicio.obtenerFolio(reserva.getCodigo());

            StringBuilder mensaje = new StringBuilder();

            mensaje.append("Reserva: ")
                    .append(reserva.getCodigo())
                    .append("\nApartamento: ")
                    .append(reserva.getApartamento().getCodigo())
                    .append("\nEstado: ")
                    .append(reserva.getEstado())
                    .append("\n\nHISTORIAL DE MOVIMIENTOS\n\n");

            if (folio.cantidadMovimientos() == 0) {
                mensaje.append("No hay movimientos registrados.\n");
            } else {

                for (MovimientoFolio movimiento :
                        folio.listarMovimientos()) {

                    mensaje.append("Código: ")
                            .append(movimiento.getCodigo())
                            .append("\nValor: $")
                            .append(String.format(
                                    Locale.US,
                                    "%.2f",
                                    movimiento.getValor()))
                            .append("\n\n");
                }
            }

            mensaje.append("\nTotal de movimientos: ")
                    .append(folio.cantidadMovimientos());

            mensaje.append("\nPagos netos: $")
                    .append(String.format(
                            Locale.US,
                            "%.2f",
                            folio.obtenerPagosNetos()));

            mensaje.append("\nSaldo del folio: $")
                    .append(String.format(
                            Locale.US,
                            "%.2f",
                            folio.obtenerSaldo()));

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);

            alerta.setTitle("Folio económico");
            alerta.setHeaderText(
                    "Movimientos de la reserva "
                            + reserva.getCodigo());

            TextArea contenido = new TextArea(mensaje.toString());
            contenido.setEditable(false);
            contenido.setWrapText(true);
            contenido.setPrefSize(480, 400);

            alerta.getDialogPane().setContent(contenido);

            alerta.showAndWait();

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    @FXML
    private void registrarCargoSeleccionada() {

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {

            reservaServicio.registrarCargoAlojamiento(
                    reserva.getCodigo(),
                    tarifaServicio
            );

            informar(
                    "Cargo registrado",
                    "Se registró el cargo total del alojamiento "
                            + "en el folio de la reserva."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }

    @FXML
    private void pagarSaldoSeleccionada() {

        if (reservaServicio == null) {
            error("El servicio de reservas no está configurado.");
            return;
        }

        Reserva reserva =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (reserva == null) {
            error("Selecciona una reserva de la tabla.");
            return;
        }

        try {

            FolioServicio folio =
                    reservaServicio.obtenerFolio(reserva.getCodigo());

            double saldo = folio.obtenerSaldo();

            if (saldo <= 0.001) {
                throw new IllegalStateException(
                        "No existe saldo pendiente por pagar.");
            }

            TextInputDialog dialogo = new TextInputDialog(
                    String.format(Locale.US, "%.2f", saldo)
            );

            dialogo.setTitle("Pago del saldo");
            dialogo.setHeaderText(
                    "Reserva " + reserva.getCodigo());

            dialogo.setContentText(
                    "Saldo pendiente: $"
                            + String.format(Locale.US, "%.2f", saldo)
                            + "\nValor recibido:"
            );

            java.util.Optional<String> respuesta =
                    dialogo.showAndWait();

            if (respuesta.isEmpty()) {
                return;
            }

            double valor = Double.parseDouble(
                    respuesta.get().trim().replace(",", ".")
            );

            if (!Double.isFinite(valor) || valor <= 0) {
                throw new IllegalArgumentException(
                        "El pago debe ser positivo y válido.");
            }

            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Confirmas que recibiste y verificaste $"
                            + String.format(Locale.US, "%.2f", valor)
                            + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            confirmacion.setTitle("Verificar pago");
            confirmacion.setHeaderText(
                    "Registro de un movimiento económico");

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }

            reservaServicio.registrarPagoSaldo(
                    reserva.getCodigo(),
                    "SALDO-" + UUID.randomUUID(),
                    valor
            );

            double nuevoSaldo = folio.obtenerSaldo();

            lblEstado.setText(
                    "Saldo pendiente de " + reserva.getCodigo()
                            + ": $"
                            + String.format(Locale.US, "%.2f", nuevoSaldo)
            );

            informar(
                    "Pago registrado",
                    "El pago se registró correctamente.\n"
                            + "Saldo pendiente: $"
                            + String.format(Locale.US, "%.2f", nuevoSaldo)
            );

        } catch (NumberFormatException ex) {

            error("Ingresa un valor numérico válido.");

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            error(ex.getMessage());
        }
    }
}
