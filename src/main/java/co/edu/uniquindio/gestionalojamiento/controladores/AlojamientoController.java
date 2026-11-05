package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AlojamientoController {

    private CatalogoAlojamiento catalogo;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCapacidad;

    @FXML
    private TextField txtHabitaciones;

    @FXML
    private TextField txtTarifaBaja;

    @FXML
    private TextField txtTarifaMedia;

    @FXML
    private TextField txtTarifaAlta;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Apartamento> tablaApartamentos;

    @FXML
    private TableColumn<Apartamento, String> colCodigo;

    @FXML
    private TableColumn<Apartamento, String> colNombre;

    @FXML
    private TableColumn<Apartamento, String> colCapacidad;

    @FXML
    private TableColumn<Apartamento, String> colHabitaciones;

    @FXML
    private TableColumn<Apartamento, String> colEstado;

    @FXML
    private TableColumn<Apartamento, String> colTarifaBaja;

    @FXML
    private TableColumn<Apartamento, String> colTarifaMedia;

    @FXML
    private TableColumn<Apartamento, String> colTarifaAlta;

    @FXML
    private Label lblEstado;

    @FXML
    private void initialize() {

        colCodigo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCodigo())
        );

        colNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getNombre())
        );

        colCapacidad.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.valueOf(
                                datos.getValue().getCapacidadMaxima()))
        );

        colHabitaciones.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.valueOf(
                                datos.getValue().getNumeroHabitaciones()))
        );

        colEstado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getEstado().name())
        );

        colTarifaBaja.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.format("%.0f",
                                datos.getValue().getTarifaBaja()))
        );

        colTarifaMedia.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.format("%.0f",
                                datos.getValue().getTarifaMedia()))
        );

        colTarifaAlta.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        String.format("%.0f",
                                datos.getValue().getTarifaAlta()))
        );

        tablaApartamentos.setPlaceholder(
                new Label("No hay apartamentos para mostrar.")
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
    private void registrarApartamento() {

        if (catalogo == null) {
            mostrarError("El catálogo no está configurado.");
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();

            int capacidad = Integer.parseInt(
                    txtCapacidad.getText().trim()
            );

            int habitaciones = Integer.parseInt(
                    txtHabitaciones.getText().trim()
            );

            double tarifaBaja = Double.parseDouble(
                    txtTarifaBaja.getText().trim()
            );

            double tarifaMedia = Double.parseDouble(
                    txtTarifaMedia.getText().trim()
            );

            double tarifaAlta = Double.parseDouble(
                    txtTarifaAlta.getText().trim()
            );

            if (!Double.isFinite(tarifaBaja)
                    || !Double.isFinite(tarifaMedia)
                    || !Double.isFinite(tarifaAlta)) {
                throw new IllegalArgumentException(
                        "Las tarifas deben ser números finitos.");
            }

            Apartamento apartamento = new Apartamento(
                    codigo,
                    nombre,
                    capacidad,
                    habitaciones,
                    tarifaBaja,
                    tarifaMedia,
                    tarifaAlta
            );

            boolean registrado =
                    catalogo.registrarApartamento(apartamento);

            if (!registrado) {
                mostrarError(
                        "Ya existe un apartamento con ese código.");
                return;
            }

            mostrarTodos();
            limpiarFormulario();

            lblEstado.setText(
                    "Apartamento registrado: "
                            + apartamento.getCodigo()
            );

            mostrarInformacion(
                    "El apartamento se registró correctamente.");

        } catch (NumberFormatException ex) {

            mostrarError(
                    "Capacidad y habitaciones deben ser enteros. "
                            + "Las tarifas deben ser números válidos.");

        } catch (IllegalArgumentException ex) {

            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void buscarApartamento() {

        if (catalogo == null) {
            mostrarError("El catálogo no está configurado.");
            return;
        }

        String codigo = txtBuscar.getText().trim();

        if (codigo.isBlank()) {
            mostrarError(
                    "Ingresa un código para buscar.");
            return;
        }

        Apartamento apartamento =
                catalogo.buscarApartamento(codigo);

        tablaApartamentos.getItems().clear();

        if (apartamento == null) {

            lblEstado.setText(
                    "No se encontró el apartamento: " + codigo
            );
            return;
        }

        tablaApartamentos.getItems().add(apartamento);

        lblEstado.setText(
                "Apartamento encontrado: "
                        + apartamento.getNombre()
        );
    }

    @FXML
    private void mostrarTodos() {

        if (catalogo == null) {
            return;
        }

        tablaApartamentos.getItems().clear();

        for (Apartamento apartamento :
                catalogo.listarApartamentos()) {

            tablaApartamentos.getItems().add(apartamento);
        }

        lblEstado.setText(
                "Total de apartamentos registrados: "
                        + catalogo.cantidadApartamentos()
        );
    }

    @FXML
    private void limpiarFormulario() {

        txtCodigo.clear();
        txtNombre.clear();
        txtCapacidad.clear();
        txtHabitaciones.clear();
        txtTarifaBaja.clear();
        txtTarifaMedia.clear();
        txtTarifaAlta.clear();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No se pudo completar la operación");
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
