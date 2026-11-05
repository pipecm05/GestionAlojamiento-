
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HuespedController {

    private CatalogoAlojamiento catalogo;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TextField txtDocumento;

    @FXML
    private TextField txtNombres;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Huesped> tablaHuespedes;

    @FXML
    private TableColumn<Huesped, String> colDocumento;

    @FXML
    private TableColumn<Huesped, String> colNombre;

    @FXML
    private TableColumn<Huesped, String> colTelefono;

    @FXML
    private TableColumn<Huesped, String> colCorreo;

    @FXML
    private TableColumn<Huesped, String> colNacimiento;

    @FXML
    private Label lblEstado;

    @FXML
    private void initialize() {

        colDocumento.setCellValueFactory(
                datos -> new ReadOnlyStringWrapper(
                        datos.getValue().getDocumento())
        );

        colNombre.setCellValueFactory(
                datos -> new ReadOnlyStringWrapper(
                        datos.getValue().getNombreCompleto())
        );

        colTelefono.setCellValueFactory(
                datos -> new ReadOnlyStringWrapper(
                        datos.getValue().getTelefono())
        );

        colCorreo.setCellValueFactory(
                datos -> new ReadOnlyStringWrapper(
                        datos.getValue().getCorreo())
        );

        colNacimiento.setCellValueFactory(datos -> {

            LocalDate fecha =
                    datos.getValue().getFechaNacimiento();

            String texto = fecha == null
                    ? "Sin registrar"
                    : fecha.format(FORMATO_FECHA);

            return new ReadOnlyStringWrapper(texto);
        });

        tablaHuespedes.setPlaceholder(
                new Label("No hay huéspedes para mostrar.")
        );
    }

    public void setCatalogo(CatalogoAlojamiento catalogo) {

        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "El catálogo es obligatorio.");
        }

        this.catalogo = catalogo;

        mostrarTodos();
    }

    @FXML
    private void registrarHuesped() {

        if (catalogo == null) {
            mostrarError("El catálogo no está configurado.");
            return;
        }

        try {

            String documento =
                    txtDocumento.getText().trim();

            String nombres =
                    txtNombres.getText().trim();

            String apellidos =
                    txtApellidos.getText().trim();

            String telefono =
                    txtTelefono.getText().trim();

            String correo =
                    txtCorreo.getText().trim();

            LocalDate nacimiento =
                    dpFechaNacimiento.getValue();

            Huesped huesped = new Huesped(
                    documento,
                    nombres,
                    apellidos,
                    telefono,
                    correo,
                    nacimiento
            );

            boolean registrado =
                    catalogo.registrarHuesped(huesped);

            if (!registrado) {
                mostrarError(
                        "Ya existe un huésped con ese documento.");
                return;
            }

            mostrarTodos();
            limpiarFormulario();

            lblEstado.setText(
                    "Huésped registrado: "
                            + huesped.getNombreCompleto()
            );

            mostrarInformacion(
                    "El huésped se registró correctamente.");

        } catch (IllegalArgumentException ex) {

            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void buscarHuesped() {

        if (catalogo == null) {
            mostrarError("El catálogo no está configurado.");
            return;
        }

        String documento = txtBuscar.getText().trim();

        if (documento.isBlank()) {
            mostrarError(
                    "Ingresa un documento para buscar.");
            return;
        }

        Huesped huesped =
                catalogo.buscarHuesped(documento);

        if (huesped == null) {

            tablaHuespedes.getItems().clear();

            lblEstado.setText(
                    "No se encontró el documento: "
                            + documento
            );

            return;
        }

        tablaHuespedes.getItems().setAll(huesped);

        lblEstado.setText(
                "Huésped encontrado: "
                        + huesped.getNombreCompleto()
        );
    }

    @FXML
    private void mostrarTodos() {

        if (catalogo == null) {
            return;
        }

        tablaHuespedes.getItems().clear();

        for (Huesped huesped : catalogo.listarHuespedes()) {
            tablaHuespedes.getItems().add(huesped);
        }

        lblEstado.setText(
                "Total de huéspedes registrados: "
                        + catalogo.cantidadHuespedes()
        );
    }

    @FXML
    private void limpiarFormulario() {

        txtDocumento.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        dpFechaNacimiento.setValue(null);
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Operación exitosa");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
