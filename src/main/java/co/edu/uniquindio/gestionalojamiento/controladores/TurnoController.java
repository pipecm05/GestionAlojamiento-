package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoTurno;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTurno;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Turno;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TurnoServicio;

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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TurnoController {

    private EmpleadoServicio empleadoServicio;
    private TurnoServicio turnoServicio;

    private final ObservableList<Turno> turnosTabla =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TextField txtCodigo;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML private TextField txtBuscarCodigo;
    @FXML private TextField txtBuscarDocumento;

    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<TipoTurno> cmbTipo;

    @FXML private DatePicker dpFecha;

    @FXML private TableView<Turno> tablaTurnos;

    @FXML private TableColumn<Turno, String> colCodigo;
    @FXML private TableColumn<Turno, String> colEmpleado;
    @FXML private TableColumn<Turno, String> colFechaInicio;
    @FXML private TableColumn<Turno, String> colFechaFin;
    @FXML private TableColumn<Turno, String> colTipo;
    @FXML private TableColumn<Turno, String> colEstado;

    @FXML private Label lblEstado;

    @FXML
    private void initialize() {

        cmbTipo.setItems(
                FXCollections.observableArrayList(
                        TipoTurno.values()
                )
        );

        colCodigo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCodigo()
                )
        );

        colEmpleado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getEmpleado().getNombreCompleto()
                                + " - "
                                + datos.getValue().getEmpleado().getDocumento()
                )
        );

        colFechaInicio.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue()
                                .getFechaHoraInicio()
                                .format(formatoFecha)
                )
        );

        colFechaFin.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue()
                                .getFechaHoraFin()
                                .format(formatoFecha)
                )
        );

        colTipo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        formatearTipo(datos.getValue().getTipo())
                )
        );

        colEstado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        formatearEstado(datos.getValue().getEstado())
                )
        );

        tablaTurnos.setItems(turnosTabla);

        tablaTurnos.setPlaceholder(
                new Label("No hay turnos registrados.")
        );

        tablaTurnos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    }
                });
    }

    public void configurar(
            EmpleadoServicio empleadoServicio,
            TurnoServicio turnoServicio) {

        if (empleadoServicio == null || turnoServicio == null) {
            throw new IllegalArgumentException(
                    "Los servicios de empleados y turnos son obligatorios."
            );
        }

        this.empleadoServicio = empleadoServicio;
        this.turnoServicio = turnoServicio;

        cargarEmpleadosActivos();
        mostrarTodos();
    }

    private void cargarEmpleadosActivos() {

        cmbEmpleado.getItems().clear();

        ListaDoble<Empleado> empleados =
                empleadoServicio.listarEmpleadosActivos();

        for (Empleado empleado : empleados) {
            cmbEmpleado.getItems().add(
                    empleado.getDocumento()
            );
        }
    }

    @FXML
    private void registrarTurno() {

        if (!serviciosConfigurados()) return;

        try {

            String codigo = txtCodigo.getText().trim();

            String documentoEmpleado = cmbEmpleado.getValue();

            if (documentoEmpleado == null
                    || documentoEmpleado.isBlank()) {

                mostrarError(
                        "Selecciona un empleado para asignarle el turno."
                );

                return;
            }

            if (dpFecha.getValue() == null) {
                mostrarError("Selecciona la fecha del turno.");
                return;
            }

            if (cmbTipo.getValue() == null) {
                mostrarError("Selecciona el tipo de turno.");
                return;
            }

            LocalTime horaInicio =
                    LocalTime.parse(txtHoraInicio.getText().trim());

            LocalTime horaFin =
                    LocalTime.parse(txtHoraFin.getText().trim());

            LocalDateTime fechaHoraInicio =
                    LocalDateTime.of(
                            dpFecha.getValue(),
                            horaInicio
                    );

            LocalDateTime fechaHoraFin =
                    LocalDateTime.of(
                            dpFecha.getValue(),
                            horaFin
                    );

            Empleado empleado =
                    empleadoServicio.buscarEmpleado(documentoEmpleado);

            if (empleado == null) {
                mostrarError(
                        "No se encontró el empleado seleccionado."
                );
                return;
            }

            Turno turno = new Turno(
                    codigo,
                    empleado,
                    fechaHoraInicio,
                    fechaHoraFin,
                    cmbTipo.getValue()
            );

            turnoServicio.registrarTurno(turno);

            mostrarTodos();
            limpiarFormulario();

            lblEstado.setText(
                    "Turno registrado correctamente."
            );

            mostrarInformacion(
                    "El turno se programó correctamente."
            );

        } catch (IllegalArgumentException ex) {

            mostrarError(ex.getMessage());

        } catch (Exception ex) {

            mostrarError(
                    "Verifica que las horas estén en formato HH:mm. "
                            + "Ejemplo: 08:00 o 16:30."
            );
        }
    }

    @FXML
    private void buscarTurno() {

        if (!serviciosConfigurados()) return;

        String codigo = txtBuscarCodigo.getText().trim();

        if (codigo.isBlank()) {
            mostrarError("Escribe el código del turno.");
            return;
        }

        try {

            Turno turno = turnoServicio.buscarTurno(codigo);

            turnosTabla.clear();

            if (turno == null) {
                lblEstado.setText(
                        "No se encontró un turno con ese código."
                );
                return;
            }

            turnosTabla.add(turno);

            tablaTurnos.getSelectionModel().select(turno);

            lblEstado.setText(
                    "Turno encontrado: " + turno.getCodigo()
            );

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void buscarTurnosEmpleado() {

        if (!serviciosConfigurados()) return;

        String documento =
                txtBuscarDocumento.getText().trim();

        if (documento.isBlank()) {
            mostrarError(
                    "Escribe el documento del empleado."
            );
            return;
        }

        try {

            ListaDoble<Turno> resultado =
                    turnoServicio.obtenerTurnosEmpleado(documento);

            cargarLista(resultado);

            lblEstado.setText(
                    "Turnos encontrados para el empleado: "
                            + turnosTabla.size()
            );

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void mostrarTodos() {

        if (!serviciosConfigurados()) return;

        cargarLista(turnoServicio.listarTurnos());

        lblEstado.setText(
                "Turnos registrados: " + turnosTabla.size()
        );
    }

    @FXML
    private void cancelarTurno() {

        cambiarEstadoTurno(false);
    }

    @FXML
    private void completarTurno() {

        cambiarEstadoTurno(true);
    }

    private void cambiarEstadoTurno(boolean completar) {

        if (!serviciosConfigurados()) return;

        String codigo = txtCodigo.getText().trim();

        if (codigo.isBlank()) {
            mostrarError(
                    "Selecciona un turno de la tabla o escribe su código."
            );
            return;
        }

        try {

            boolean resultado = completar
                    ? turnoServicio.completarTurno(codigo)
                    : turnoServicio.cancelarTurno(codigo);

            if (!resultado) {

                mostrarError(
                        completar
                                ? "El turno no existe o ya está completado."
                                : "El turno no existe o ya está cancelado."
                );

                return;
            }

            mostrarTodos();

            lblEstado.setText(
                    completar
                            ? "Turno completado correctamente."
                            : "Turno cancelado correctamente."
            );

            mostrarInformacion(lblEstado.getText());

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {

        txtCodigo.clear();
        txtHoraInicio.clear();
        txtHoraFin.clear();

        cmbEmpleado.setValue(null);
        cmbTipo.setValue(null);

        dpFecha.setValue(null);

        tablaTurnos.getSelectionModel().clearSelection();
    }

    @FXML
    private void limpiarBusqueda() {

        txtBuscarCodigo.clear();
        txtBuscarDocumento.clear();

        mostrarTodos();
    }

    private void cargarFormulario(Turno turno) {

        txtCodigo.setText(turno.getCodigo());

        cmbEmpleado.setValue(
                turno.getEmpleado().getDocumento()
        );

        dpFecha.setValue(
                turno.getFechaHoraInicio().toLocalDate()
        );

        txtHoraInicio.setText(
                turno.getFechaHoraInicio()
                        .toLocalTime()
                        .toString()
        );

        txtHoraFin.setText(
                turno.getFechaHoraFin()
                        .toLocalTime()
                        .toString()
        );

        cmbTipo.setValue(turno.getTipo());
    }

    private void cargarLista(ListaDoble<Turno> lista) {

        turnosTabla.clear();

        for (Turno turno : lista) {
            turnosTabla.add(turno);
        }
    }

    private boolean serviciosConfigurados() {

        if (empleadoServicio == null || turnoServicio == null) {

            mostrarError(
                    "Los servicios de empleados y turnos "
                            + "no están configurados."
            );

            return false;
        }

        return true;
    }

    private String formatearTipo(TipoTurno tipo) {

        String texto = tipo.name()
                .toLowerCase()
                .replace('_', ' ');

        return texto.substring(0, 1).toUpperCase()
                + texto.substring(1);
    }

    private String formatearEstado(EstadoTurno estado) {

        String texto = estado.name()
                .toLowerCase()
                .replace('_', ' ');

        return texto.substring(0, 1).toUpperCase()
                + texto.substring(1);
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Gestión de turnos");
        alerta.setHeaderText(
                "No fue posible realizar la operación"
        );
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Gestión de turnos");
        alerta.setHeaderText("Operación exitosa");
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
