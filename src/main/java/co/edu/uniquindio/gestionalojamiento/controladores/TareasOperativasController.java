package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoTareaOperativa;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTareaOperativa;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.TareaOperativa;
import co.edu.uniquindio.gestionalojamiento.servicios.TareaOperativaServicio;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;

public class TareasOperativasController {

    private TareaOperativaServicio tareaServicio;
    private final ObservableList<TareaOperativa> tareasTabla = FXCollections.observableArrayList();
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TableView<TareaOperativa> tablaTareas;
    @FXML private TableColumn<TareaOperativa, String> colCodigo;
    @FXML private TableColumn<TareaOperativa, String> colTipo;
    @FXML private TableColumn<TareaOperativa, String> colApartamento;
    @FXML private TableColumn<TareaOperativa, String> colMotivo;
    @FXML private TableColumn<TareaOperativa, String> colEmpleado;
    @FXML private TableColumn<TareaOperativa, String> colEstado;
    @FXML private TableColumn<TareaOperativa, String> colFecha;
    @FXML private TableColumn<TareaOperativa, String> colObservaciones;
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<Empleado> cmbEmpleado;
    @FXML private TextArea txtObservaciones;
    @FXML private TextField txtFiltroEmpleado;
    @FXML private TextField txtFiltroApartamento;
    @FXML private Label lblPendientes;
    @FXML private Label lblProceso;
    @FXML private Label lblFinalizadas;
    @FXML private Label lblDisponibles;
    @FXML private Label lblMensaje;

    @FXML
    private void initialize() {
        cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "LIMPIEZA", "MANTENIMIENTO"));
        cmbFiltroTipo.setValue("Todos");
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
                "Todos", "PENDIENTE", "ASIGNADA", "EN_PROCESO", "FINALIZADA", "CANCELADA"));
        cmbFiltroEstado.setValue("Todos");

        colCodigo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCodigo()));
        colTipo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getTipo().toString()));
        colApartamento.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getApartamento().getCodigo()));
        colMotivo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getMotivo()));
        colEmpleado.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getEmpleado() == null ? "Sin asignar" : d.getValue().getEmpleado().getNombreCompleto()));
        colEstado.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getEstado().toString()));
        colFecha.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getFechaCreacion() == null ? "-" : d.getValue().getFechaCreacion().format(formatoFecha)));
        colObservaciones.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getObservaciones() == null || d.getValue().getObservaciones().isBlank()
                        ? "-" : d.getValue().getObservaciones()));

        tablaTareas.setItems(tareasTabla);
        tablaTareas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            actualizarEmpleadosDisponibles(seleccionada);
            if (seleccionada != null) {
                txtObservaciones.setText(seleccionada.getObservaciones());
            }
        });

        cmbEmpleado.setConverter(new StringConverter<>() {
            @Override public String toString(Empleado empleado) {
                return empleado == null ? "" : empleado.getNombreCompleto() + " - " + empleado.getDocumento();
            }
            @Override public Empleado fromString(String texto) { return null; }
        });
    }

    public void configurar(TareaOperativaServicio servicio) {
        if (servicio == null) throw new IllegalArgumentException("El servicio de tareas es obligatorio.");
        tareaServicio = servicio;
        actualizarVista();
    }

    @FXML private void filtrar() { actualizarVista(); }

    @FXML
    private void limpiarFiltros() {
        cmbFiltroTipo.setValue("Todos");
        cmbFiltroEstado.setValue("Todos");
        txtFiltroEmpleado.clear();
        txtFiltroApartamento.clear();
        actualizarVista();
    }

    @FXML private void mostrarTodas() { limpiarFiltros(); }

    @FXML
    private void filtrarPorEmpleado() {
        if (tareaServicio == null) return;
        String documento = txtFiltroEmpleado.getText() == null ? "" : txtFiltroEmpleado.getText().trim();
        if (documento.isBlank()) {
            mostrarMensaje("Escribe el documento del empleado para consultar su historial.", true);
            return;
        }
        tareasTabla.clear();
        for (TareaOperativa tarea : tareaServicio.listarPorEmpleado(documento)) {
            tareasTabla.add(tarea);
        }
        mostrarMensaje("Historial del empleado " + documento + ".", false);
    }

    @FXML
    private void filtrarPorApartamento() {
        if (tareaServicio == null) return;
        String codigo = txtFiltroApartamento.getText() == null ? "" : txtFiltroApartamento.getText().trim();
        if (codigo.isBlank()) {
            mostrarMensaje("Escribe el código del apartamento para consultar su historial.", true);
            return;
        }
        tareasTabla.clear();
        for (TareaOperativa tarea : tareaServicio.listarPorApartamento(codigo)) {
            tareasTabla.add(tarea);
        }
        mostrarMensaje("Historial del apartamento " + codigo + ".", false);
    }

    private void actualizarVista() {
        if (tareaServicio == null) return;
        tareasTabla.clear();
        for (TareaOperativa tarea : tareaServicio.listarTareas()) {
            boolean coincideTipo = "Todos".equals(cmbFiltroTipo.getValue())
                    || tarea.getTipo().toString().equals(cmbFiltroTipo.getValue());
            boolean coincideEstado = "Todos".equals(cmbFiltroEstado.getValue())
                    || tarea.getEstado().toString().equals(cmbFiltroEstado.getValue());
            if (coincideTipo && coincideEstado) tareasTabla.add(tarea);
        }
        lblPendientes.setText(String.valueOf(tareaServicio.contarTareasPendientes()));
        lblProceso.setText(String.valueOf(tareaServicio.contarTareasEnProceso()));
        lblFinalizadas.setText(String.valueOf(tareaServicio.contarTareasFinalizadas()));
        int disponibles = 0;
        for (TipoTareaOperativa tipo : TipoTareaOperativa.values()) {
            for (Empleado empleado : tareaServicio.listarEmpleadosDisponibles(tipo)) disponibles++;
        }
        lblDisponibles.setText(String.valueOf(disponibles));
        actualizarEmpleadosDisponibles(tablaTareas.getSelectionModel().getSelectedItem());
    }

    private void actualizarEmpleadosDisponibles(TareaOperativa tarea) {
        cmbEmpleado.getItems().clear();
        if (tarea == null) return;

        if (tarea.getEstado() == EstadoTareaOperativa.PENDIENTE) {
            for (Empleado empleado : tareaServicio.listarEmpleadosDisponibles(tarea.getTipo())) {
                cmbEmpleado.getItems().add(empleado);
            }
        } else if (tarea.estaActiva()) {
            for (Empleado empleado : tareaServicio.listarEmpleadosParaReasignacion(tarea.getCodigo())) {
                cmbEmpleado.getItems().add(empleado);
            }
        }
    }

    @FXML
    private void asignarEmpleado() {
        TareaOperativa tarea = obtenerTareaSeleccionada();
        if (tarea == null) return;
        Empleado empleado = cmbEmpleado.getValue();
        if (empleado == null) {
            mostrarMensaje("Selecciona un empleado disponible.", true);
            return;
        }
        ejecutar(() -> tareaServicio.asignarEmpleado(tarea.getCodigo(), empleado.getDocumento()),
                "Empleado asignado correctamente.");
    }

    @FXML
    private void reasignarEmpleado() {
        TareaOperativa tarea = obtenerTareaSeleccionada();
        if (tarea == null) return;
        Empleado empleado = cmbEmpleado.getValue();
        if (empleado == null) {
            mostrarMensaje("Selecciona el nuevo empleado responsable.", true);
            return;
        }
        ejecutar(() -> tareaServicio.reasignarEmpleado(tarea.getCodigo(), empleado.getDocumento()),
                "Empleado reasignado correctamente.");
    }

    @FXML
    private void iniciarTarea() {
        TareaOperativa tarea = obtenerTareaSeleccionada();
        if (tarea == null) return;
        ejecutar(() -> tareaServicio.iniciarTarea(tarea.getCodigo()), "La tarea se marcó EN_PROCESO.");
    }

    @FXML
    private void finalizarTarea() {
        TareaOperativa tarea = obtenerTareaSeleccionada();
        if (tarea == null) return;
        ejecutar(() -> tareaServicio.finalizarTarea(tarea.getCodigo(), txtObservaciones.getText()),
                tarea.getTipo() == TipoTareaOperativa.LIMPIEZA
                        ? "Limpieza finalizada. El apartamento quedó PREPARADO y el empleado está disponible."
                        : "Tarea finalizada y empleado disponible.");
    }

    @FXML
    private void cancelarTarea() {
        TareaOperativa tarea = obtenerTareaSeleccionada();
        if (tarea == null) return;
        ejecutar(() -> tareaServicio.cancelarTarea(tarea.getCodigo(), txtObservaciones.getText()),
                "La tarea se canceló.");
    }

    /**
     * Obtiene la tarea seleccionada sin lanzar excepciones en el hilo de JavaFX.
     * Si no hay selección, informa al usuario y permite que la interfaz continúe.
     */
    private TareaOperativa obtenerTareaSeleccionada() {
        TareaOperativa tarea = tablaTareas.getSelectionModel().getSelectedItem();
        if (tarea == null) {
            mostrarMensaje("Selecciona una tarea de la tabla antes de realizar esta acción.", true);
            return null;
        }
        return tarea;
    }

    private void ejecutar(Runnable accion, String mensajeExito) {
        try {
            accion.run();
            actualizarVista();
            mostrarMensaje(mensajeExito, false);
        } catch (RuntimeException ex) {
            mostrarMensaje(ex.getMessage(), true);
        }
    }

    private void mostrarMensaje(String mensaje, boolean error) {
        lblMensaje.setText(mensaje == null ? "Ocurrió un error." : mensaje);
        lblMensaje.setStyle(error ? "-fx-text-fill: #B42318;" : "-fx-text-fill: #18794E;");
    }
}
