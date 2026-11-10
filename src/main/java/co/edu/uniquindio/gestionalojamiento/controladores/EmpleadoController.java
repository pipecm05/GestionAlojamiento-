
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EmpleadoController {

    private EmpleadoServicio empleadoServicio;

    private final ObservableList<Empleado> empleadosTabla =
            FXCollections.observableArrayList();

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<TipoEmpleado> cmbTipo;
    @FXML private ComboBox<TipoEmpleado> cmbFiltroTipo;

    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private TableColumn<Empleado, String> colDocumento;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colTipo;
    @FXML private TableColumn<Empleado, String> colEstado;

    @FXML private Label lblEstado;

    @FXML
    private void initialize() {

        cmbTipo.setItems(
                FXCollections.observableArrayList(TipoEmpleado.values())
        );

        cmbFiltroTipo.setItems(
                FXCollections.observableArrayList(TipoEmpleado.values())
        );

        colDocumento.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getDocumento()
                )
        );

        colNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getNombreCompleto()
                )
        );

        colTelefono.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getTelefono()
                )
        );

        colCorreo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCorreo()
                )
        );

        colTipo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        formatearTipo(datos.getValue().getTipo())
                )
        );

        colEstado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().isActivo()
                                ? "Activo"
                                : "Inactivo"
                )
        );

        tablaEmpleados.setItems(empleadosTabla);

        tablaEmpleados.setPlaceholder(
                new Label("No hay empleados para mostrar.")
        );

        tablaEmpleados.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    }
                });
    }

    public void configurar(EmpleadoServicio empleadoServicio) {

        if (empleadoServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de empleados es obligatorio."
            );
        }

        this.empleadoServicio = empleadoServicio;

        mostrarTodos();
    }

    @FXML
    private void registrarEmpleado() {

        if (!servicioConfigurado()) return;

        try {

            Empleado empleado = new Empleado(
                    obtenerTexto(txtDocumento),
                    obtenerTexto(txtNombres),
                    obtenerTexto(txtApellidos),
                    obtenerTexto(txtTelefono),
                    obtenerTexto(txtCorreo),
                    cmbTipo.getValue()
            );

            empleadoServicio.registrarEmpleado(empleado);

            mostrarTodos();
            limpiarFormulario();

            lblEstado.setText(
                    "Empleado registrado correctamente."
            );

            mostrarInformacion(
                    "El empleado se registró correctamente."
            );

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void actualizarEmpleado() {

        if (!servicioConfigurado()) return;

        try {

            boolean actualizado =
                    empleadoServicio.actualizarEmpleado(
                            obtenerTexto(txtDocumento),
                            obtenerTexto(txtNombres),
                            obtenerTexto(txtApellidos),
                            obtenerTexto(txtTelefono),
                            obtenerTexto(txtCorreo),
                            cmbTipo.getValue()
                    );

            if (!actualizado) {
                mostrarError(
                        "No existe un empleado con ese documento."
                );
                return;
            }

            mostrarTodos();

            lblEstado.setText(
                    "Empleado actualizado correctamente."
            );

            mostrarInformacion(
                    "Los datos del empleado se actualizaron correctamente."
            );

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void desactivarEmpleado() {
        cambiarEstadoEmpleado(false);
    }

    @FXML
    private void activarEmpleado() {
        cambiarEstadoEmpleado(true);
    }

    private void cambiarEstadoEmpleado(boolean activar) {

        if (!servicioConfigurado()) return;

        String documento = obtenerTexto(txtDocumento);

        if (documento.isBlank()) {
            mostrarError(
                    "Selecciona un empleado o escribe su documento."
            );
            return;
        }

        try {

            boolean cambio = activar
                    ? empleadoServicio.activarEmpleado(documento)
                    : empleadoServicio.desactivarEmpleado(documento);

            if (!cambio) {

                mostrarError(
                        activar
                                ? "No se encontró el empleado o ya está activo."
                                : "No se encontró el empleado o ya está inactivo."
                );

                return;
            }

            mostrarTodos();

            lblEstado.setText(
                    activar
                            ? "Empleado activado correctamente."
                            : "Empleado desactivado correctamente."
            );

            mostrarInformacion(lblEstado.getText());

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void buscarEmpleado() {

        if (!servicioConfigurado()) return;

        String documento = obtenerTexto(txtBuscar);

        if (documento.isBlank()) {
            mostrarError(
                    "Ingresa el documento que deseas buscar."
            );
            return;
        }

        try {

            Empleado empleado =
                    empleadoServicio.buscarEmpleado(documento);

            empleadosTabla.clear();

            if (empleado == null) {

                lblEstado.setText(
                        "No se encontró un empleado con ese documento."
                );

                return;
            }

            empleadosTabla.add(empleado);

            tablaEmpleados.getSelectionModel().select(empleado);

            lblEstado.setText(
                    "Empleado encontrado: "
                            + empleado.getNombreCompleto()
            );

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void mostrarTodos() {

        if (!servicioConfigurado()) return;

        cargarLista(empleadoServicio.listarEmpleados());

        lblEstado.setText(
                "Empleados registrados: " + empleadosTabla.size()
        );
    }

    @FXML
    private void mostrarActivos() {

        if (!servicioConfigurado()) return;

        cargarLista(
                empleadoServicio.listarEmpleadosActivos()
        );

        lblEstado.setText(
                "Empleados activos: " + empleadosTabla.size()
        );
    }

    @FXML
    private void filtrarPorTipo() {

        if (!servicioConfigurado()) return;

        TipoEmpleado tipo = cmbFiltroTipo.getValue();

        if (tipo == null) {
            mostrarError(
                    "Selecciona un tipo de empleado para filtrar."
            );
            return;
        }

        cargarLista(
                empleadoServicio.buscarPorTipo(tipo)
        );

        lblEstado.setText(
                "Empleados del tipo "
                        + formatearTipo(tipo)
                        + ": "
                        + empleadosTabla.size()
        );
    }

    @FXML
    private void limpiarFiltro() {

        cmbFiltroTipo.setValue(null);
        txtBuscar.clear();

        mostrarTodos();
    }

    @FXML
    private void limpiarFormulario() {

        txtDocumento.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();

        cmbTipo.setValue(null);

        tablaEmpleados.getSelectionModel().clearSelection();
    }

    private void cargarFormulario(Empleado empleado) {

        txtDocumento.setText(empleado.getDocumento());
        txtNombres.setText(empleado.getNombres());
        txtApellidos.setText(empleado.getApellidos());
        txtTelefono.setText(empleado.getTelefono());
        txtCorreo.setText(empleado.getCorreo());

        cmbTipo.setValue(empleado.getTipo());
    }

    private void cargarLista(ListaDoble<Empleado> lista) {

        empleadosTabla.clear();

        for (Empleado empleado : lista) {
            empleadosTabla.add(empleado);
        }
    }

    private boolean servicioConfigurado() {

        if (empleadoServicio == null) {

            mostrarError(
                    "El servicio de empleados no está configurado."
            );

            return false;
        }

        return true;
    }

    private String obtenerTexto(TextField campo) {

        return campo.getText() == null
                ? ""
                : campo.getText().trim();
    }

    private String formatearTipo(TipoEmpleado tipo) {

        String texto = tipo.name()
                .toLowerCase()
                .replace('_', ' ');

        return texto.substring(0, 1).toUpperCase()
                + texto.substring(1);
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Gestión de empleados");
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

        alerta.setTitle("Gestión de empleados");
        alerta.setHeaderText("Operación exitosa");
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
