package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.TemporadaTipo;
import co.edu.uniquindio.gestionalojamiento.modelo.Temporada;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TemporadasController {

    private TarifaServicio tarifaServicio;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtNombre;
    @FXML private ComboBox<TemporadaTipo> cbTipo;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private DatePicker dpConsulta;
    @FXML private Label lblEstado;

    @FXML private TableView<Temporada> tablaTemporadas;
    @FXML private TableColumn<Temporada, String> colNombre;
    @FXML private TableColumn<Temporada, String> colTipo;
    @FXML private TableColumn<Temporada, String> colInicio;
    @FXML private TableColumn<Temporada, String> colFin;

    @FXML
    private void initialize() {

        cbTipo.getItems().addAll(TemporadaTipo.values());

        colNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getNombre()));

        colTipo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getTipo().name()));

        colInicio.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getFechaInicio()
                                .format(FORMATO)));

        colFin.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getFechaFin()
                                .format(FORMATO)));

        tablaTemporadas.setPlaceholder(
                new Label("No hay temporadas registradas.")
        );
    }

    public void configurar(TarifaServicio tarifaServicio) {

        if (tarifaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de tarifas es obligatorio.");
        }

        this.tarifaServicio = tarifaServicio;
        mostrarTemporadas();
    }

    @FXML
    private void registrarTemporada() {

        if (tarifaServicio == null) {
            mostrarError("El servicio de tarifas no está configurado.");
            return;
        }

        try {

            String nombre = txtNombre.getText().trim();
            TemporadaTipo tipo = cbTipo.getValue();
            LocalDate inicio = dpInicio.getValue();
            LocalDate fin = dpFin.getValue();

            if (inicio == null || fin == null
                    || !inicio.isBefore(fin)) {
                throw new IllegalArgumentException(
                        "La fecha de inicio debe ser anterior a la fecha de fin.");
            }

            // Las tarifas monetarias se obtienen del apartamento.
            // El importe de Temporada es un campo heredado
            // del modelo que el cálculo actual no utiliza.
            Temporada temporada = new Temporada(
                    nombre, tipo, inicio, fin, 0
            );

            tarifaServicio.registrarTemporada(temporada);

            mostrarTemporadas();
            limpiarFormulario();

            lblEstado.setText(
                    "Temporada registrada: " + temporada.getNombre());

            mostrarInformacion(
                    "La temporada se registró correctamente.");

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void consultarTemporada() {

        if (tarifaServicio == null) {
            mostrarError("El servicio de tarifas no está configurado.");
            return;
        }

        LocalDate fecha = dpConsulta.getValue();

        if (fecha == null) {
            mostrarError("Selecciona una fecha para consultar.");
            return;
        }

        Temporada temporada = tarifaServicio.buscarTemporada(fecha);

        if (temporada == null) {
            lblEstado.setText(
                    "No hay temporada especial para esa fecha. "
                            + "Se aplica la tarifa BAJA del apartamento.");
        } else {
            lblEstado.setText(
                    "Temporada encontrada: "
                            + temporada.getNombre()
                            + " (" + temporada.getTipo() + ")");
        }
    }

    private void mostrarTemporadas() {

        tablaTemporadas.getItems().clear();

        for (Temporada temporada :
                tarifaServicio.listarTemporadas()) {
            tablaTemporadas.getItems().add(temporada);
        }

        lblEstado.setText(
                "Total de temporadas registradas: "
                        + tarifaServicio.cantidadTemporadas());
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        cbTipo.setValue(null);
        dpInicio.setValue(null);
        dpFin.setValue(null);
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
