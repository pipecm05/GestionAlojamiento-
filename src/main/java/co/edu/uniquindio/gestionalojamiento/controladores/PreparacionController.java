
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.PreparacionServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PreparacionController {

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;
    private PreparacionServicio preparacionServicio;

    @FXML
    private TableView<Apartamento> tablaApartamentos;

    @FXML
    private TableColumn<Apartamento, String> colCodigo;

    @FXML
    private TableColumn<Apartamento, String> colNombre;

    @FXML
    private TableColumn<Apartamento, String> colEstado;

    @FXML
    private Label lblPendientes;

    @FXML
    private Label lblSiguiente;

    @FXML
    private Label lblEnPreparacion;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnFinalizar;

    @FXML
    private void initialize() {

        colCodigo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getNombre()
                )
        );

        colEstado.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(
                        datos.getValue().getEstado().name()
                )
        );

        tablaApartamentos.setPlaceholder(
                new Label("No hay apartamentos registrados.")
        );

        btnFinalizar.setDisable(true);
        btnIniciar.setDisable(true);
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio
    ) {

        if (catalogo == null || reservaServicio == null) {
            throw new IllegalArgumentException(
                    "El catálogo y el servicio de reservas son obligatorios."
            );
        }

        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;

        this.preparacionServicio =
                reservaServicio.getPreparacionServicio();

        actualizarTabla();
    }

    @FXML
    private void actualizarTabla() {

        if (catalogo == null || preparacionServicio == null) {
            return;
        }

        ObservableList<Apartamento> apartamentos =
                FXCollections.observableArrayList();

        for (Apartamento apartamento :
                catalogo.listarApartamentos()) {

            apartamentos.add(apartamento);
        }

        tablaApartamentos.setItems(apartamentos);

        actualizarInformacion();
    }

    private void actualizarInformacion() {

        int pendientes = preparacionServicio.cantidadPendientes();

        lblPendientes.setText(
                "Apartamentos pendientes: " + pendientes
        );

        Apartamento siguiente =
                preparacionServicio.consultarSiguiente();

        if (siguiente == null) {
            lblSiguiente.setText(
                    "Siguiente apartamento: ninguno"
            );
        } else {
            lblSiguiente.setText(
                    "Siguiente apartamento: "
                            + siguiente.getCodigo()
                            + " - "
                            + siguiente.getNombre()
            );
        }

        Apartamento activo =
                preparacionServicio.getApartamentoEnPreparacion();

        if (activo == null) {
            lblEnPreparacion.setText(
                    "En preparación: ninguno"
            );
        } else {
            lblEnPreparacion.setText(
                    "En preparación: "
                            + activo.getCodigo()
                            + " - "
                            + activo.getNombre()
            );
        }

        btnIniciar.setDisable(
                activo != null
                        || !preparacionServicio.hayApartamentosPendientes()
        );

        btnFinalizar.setDisable(activo == null);
    }

    @FXML
    private void iniciarPreparacion() {

        try {

            Apartamento siguiente =
                    preparacionServicio.iniciarSiguientePreparacion();

            if (siguiente == null) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Cola vacía",
                        "No hay apartamentos pendientes de preparación."
                );

                actualizarTabla();
                return;
            }

            actualizarTabla();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Preparación iniciada",
                    "Se inició la preparación del apartamento "
                            + siguiente.getCodigo() + "."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo iniciar la preparación",
                    ex.getMessage()
            );

            actualizarTabla();
        }
    }

    @FXML
    private void finalizarPreparacion() {

        try {

            Apartamento activo =
                    preparacionServicio.getApartamentoEnPreparacion();

            if (activo == null) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Sin preparación activa",
                        "No hay ningún apartamento en preparación."
                );

                actualizarTabla();
                return;
            }

            String codigo = activo.getCodigo();

            preparacionServicio.finalizarPreparacion();

            actualizarTabla();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Preparación finalizada",
                    "El apartamento " + codigo
                            + " quedó preparado correctamente."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo finalizar la preparación",
                    ex.getMessage()
            );

            actualizarTabla();
        }
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
