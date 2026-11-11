package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudExterna;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.SolicitudExterna;
import co.edu.uniquindio.gestionalojamiento.servicios.CanalExternoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CanalExternoController {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CatalogoAlojamiento catalogo;
    private CanalExternoServicio canalExternoServicio;

    @FXML private TextField txtCodigo;
    @FXML private ComboBox<Huesped> cbHuesped;
    @FXML private ComboBox<Apartamento> cbApartamento;
    @FXML private DatePicker dpEntrada;
    @FXML private DatePicker dpSalida;

    @FXML private TableView<SolicitudExterna> tablaPendientes;
    @FXML private TableColumn<SolicitudExterna, String> colCodigoPendiente;
    @FXML private TableColumn<SolicitudExterna, String> colHuespedPendiente;
    @FXML private TableColumn<SolicitudExterna, String> colApartamentoPendiente;
    @FXML private TableColumn<SolicitudExterna, String> colFechasPendiente;

    @FXML private TableView<SolicitudExterna> tablaHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colCodigoHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colHuespedHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colApartamentoHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colFechasHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colEstadoHistorial;
    @FXML private TableColumn<SolicitudExterna, String> colMotivoHistorial;

    @FXML private Label lblPendientes;
    @FXML private Label lblProcesadas;
    @FXML private Label lblConflictos;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSelectores();

        dpEntrada.setValue(LocalDate.now().plusDays(1));
        dpSalida.setValue(LocalDate.now().plusDays(2));
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            CanalExternoServicio canalExternoServicio) {

        if (catalogo == null || canalExternoServicio == null) {
            throw new IllegalArgumentException(
                    "El catálogo y el servicio del canal externo son obligatorios.");
        }

        this.catalogo = catalogo;
        this.canalExternoServicio = canalExternoServicio;

        cargarSelectores();
        actualizarTablas();
    }

    private void configurarColumnas() {

        colCodigoPendiente.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getCodigo()));

        colHuespedPendiente.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getHuesped().getNombreCompleto()));

        colApartamentoPendiente.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getApartamento().getCodigo()));

        colFechasPendiente.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(formatearFechas(d.getValue())));

        colCodigoHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(d.getValue().getCodigo()));

        colHuespedHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getHuesped().getNombreCompleto()));

        colApartamentoHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getApartamento().getCodigo()));

        colFechasHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(formatearFechas(d.getValue())));

        colEstadoHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        textoEstado(d.getValue().getEstado())));

        colMotivoHistorial.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getMotivoRechazo() == null
                                ? "—"
                                : d.getValue().getMotivoRechazo()));
    }

    private void configurarSelectores() {

        cbHuesped.setConverter(new StringConverter<>() {

            @Override
            public String toString(Huesped huesped) {
                return huesped == null
                        ? ""
                        : huesped.getDocumento()
                        + " - "
                        + huesped.getNombreCompleto();
            }

            @Override
            public Huesped fromString(String texto) {
                return null;
            }
        });

        cbApartamento.setConverter(new StringConverter<>() {

            @Override
            public String toString(Apartamento apartamento) {
                return apartamento == null
                        ? ""
                        : apartamento.getCodigo()
                        + " - "
                        + apartamento.getNombre()
                        + " (capacidad: "
                        + apartamento.getCapacidadMaxima()
                        + ")";
            }

            @Override
            public Apartamento fromString(String texto) {
                return null;
            }
        });
    }

    private void cargarSelectores() {

        ObservableList<Huesped> huespedes =
                FXCollections.observableArrayList();

        for (Huesped huesped : catalogo.listarHuespedes()) {
            huespedes.add(huesped);
        }

        cbHuesped.setItems(huespedes);

        ObservableList<Apartamento> apartamentos =
                FXCollections.observableArrayList();

        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            apartamentos.add(apartamento);
        }

        cbApartamento.setItems(apartamentos);
    }

    @FXML
    private void registrarSolicitud() {

        try {

            if (txtCodigo.getText() == null
                    || txtCodigo.getText().isBlank()) {
                throw new IllegalArgumentException(
                        "Ingresa el código de la solicitud.");
            }

            if (cbHuesped.getValue() == null
                    || cbApartamento.getValue() == null) {
                throw new IllegalArgumentException(
                        "Selecciona un huésped y un apartamento.");
            }

            if (dpEntrada.getValue() == null
                    || dpSalida.getValue() == null) {
                throw new IllegalArgumentException(
                        "Selecciona las fechas de entrada y salida.");
            }

            SolicitudExterna solicitud = new SolicitudExterna(
                    txtCodigo.getText().trim(),
                    cbApartamento.getValue(),
                    cbHuesped.getValue(),
                    dpEntrada.getValue(),
                    dpSalida.getValue()
            );

            canalExternoServicio.recibirSolicitud(solicitud);

            txtCodigo.clear();
            cbHuesped.getSelectionModel().clearSelection();
            cbApartamento.getSelectionModel().clearSelection();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Solicitud recibida",
                    "La solicitud fue agregada al final de la cola FIFO."
            );

            actualizarTablas();

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No fue posible registrar la solicitud",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void procesarSiguiente() {

        try {

            SolicitudExterna procesada =
                    canalExternoServicio.procesarSiguiente();

            String mensaje =
                    procesada.getEstado() == EstadoSolicitudExterna.ACEPTADA
                            ? "La solicitud " + procesada.getCodigo()
                            + " fue aceptada y se creó la reserva."
                            : "La solicitud " + procesada.getCodigo()
                            + " fue rechazada. Motivo: "
                            + procesada.getMotivoRechazo();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Resultado del procesamiento",
                    mensaje
            );

            actualizarTablas();

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No hay solicitud para procesar",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void procesarTodas() {

        try {

            int cantidad = canalExternoServicio.procesarTodas();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Procesamiento finalizado",
                    "Se procesaron " + cantidad
                            + " solicitudes en orden de llegada."
            );

            actualizarTablas();

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al procesar solicitudes",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void actualizarVista() {
        actualizarTablas();
    }

    private void actualizarTablas() {

        if (canalExternoServicio == null) {
            return;
        }

        ObservableList<SolicitudExterna> pendientes =
                FXCollections.observableArrayList();

        for (SolicitudExterna solicitud :
                canalExternoServicio.obtenerPendientes()) {
            pendientes.add(solicitud);
        }

        tablaPendientes.setItems(pendientes);

        ObservableList<SolicitudExterna> historial =
                FXCollections.observableArrayList();

        for (SolicitudExterna solicitud :
                canalExternoServicio.obtenerHistorial()) {
            historial.add(solicitud);
        }

        tablaHistorial.setItems(historial);

        lblPendientes.setText(
                "Pendientes: "
                        + canalExternoServicio.cantidadPendientes());

        lblProcesadas.setText(
                "Procesadas: "
                        + canalExternoServicio.cantidadProcesadas());

        lblConflictos.setText(
                "Conflictos: "
                        + canalExternoServicio.contarConflictos());
    }

    private String formatearFechas(SolicitudExterna solicitud) {

        return solicitud.getFechaEntrada().format(FORMATO_FECHA)
                + " - "
                + solicitud.getFechaSalida().format(FORMATO_FECHA);
    }

    private String textoEstado(EstadoSolicitudExterna estado) {

        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case ACEPTADA -> "Aceptada";
            case RECHAZADA -> "Rechazada";
        };
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(
                mensaje == null ? "Ocurrió un error." : mensaje
        );
        alerta.showAndWait();
    }
}
