
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.modelo.MovimientoFolio;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.servicios.FolioServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FolioController {

    @FXML
    private ComboBox<String> cbReserva;

    @FXML
    private ComboBox<String> cbTipoMovimiento;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtValor;

    @FXML
    private Label lblReserva;

    @FXML
    private Label lblSaldo;

    @FXML
    private Label lblCantidadMovimientos;

    @FXML
    private TableView<MovimientoFolio> tablaMovimientos;

    @FXML
    private TableColumn<MovimientoFolio, String> colCodigo;

    @FXML
    private TableColumn<MovimientoFolio, String> colDescripcion;

    @FXML
    private TableColumn<MovimientoFolio, String> colTipo;

    @FXML
    private TableColumn<MovimientoFolio, String> colValor;

    @FXML
    private TableColumn<MovimientoFolio, String> colFecha;

    private ReservaServicio reservaServicio;
    private FolioServicio folioActual;

    private final ObservableList<MovimientoFolio> movimientos =
            FXCollections.observableArrayList();

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {

        configurarTabla();

        cbTipoMovimiento.setItems(
                FXCollections.observableArrayList(
                        "Cargo",
                        "Pago"
                )
        );

        cbTipoMovimiento.setValue("Cargo");

        cbReserva.valueProperty().addListener(
                (observable, anterior, seleccionado) ->
                        cargarFolioSeleccionado()
        );
    }

    public void configurar(ReservaServicio reservaServicio) {

        if (reservaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de reservas es obligatorio."
            );
        }

        this.reservaServicio = reservaServicio;

        cargarReservas();
    }

    private void configurarTabla() {

        colCodigo.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().getCodigo()
                )
        );

        colDescripcion.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().getDescripcion()
                )
        );

        colTipo.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().esCargo()
                                ? "Cargo"
                                : "Pago"
                )
        );

        colValor.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        formatoMoneda.format(
                                dato.getValue().getValorAbsoluto()
                        )
                )
        );

        colFecha.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue()
                                .getFecha()
                                .format(formatoFecha)
                )
        );

        tablaMovimientos.setItems(movimientos);
    }

    private void cargarReservas() {

        cbReserva.getItems().clear();

        for (Reserva reserva : reservaServicio.listarReservas()) {
            cbReserva.getItems().add(reserva.getCodigo());
        }

        if (!cbReserva.getItems().isEmpty()) {

            cbReserva.getSelectionModel().selectFirst();

        } else {

            folioActual = null;

            movimientos.clear();

            lblReserva.setText(
                    "No hay reservas registradas."
            );

            lblSaldo.setText(
                    formatoMoneda.format(0)
            );

            lblCantidadMovimientos.setText(
                    "0 movimientos"
            );
        }
    }

    private void cargarFolioSeleccionado() {

        if (reservaServicio == null) {
            return;
        }

        String codigoReserva = cbReserva.getValue();

        if (codigoReserva == null || codigoReserva.isBlank()) {

            folioActual = null;

            movimientos.clear();

            return;
        }

        try {

            Reserva reserva =
                    reservaServicio.buscarPorCodigo(codigoReserva);

            if (reserva == null) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Reserva no encontrada",
                        "No fue posible encontrar la reserva seleccionada."
                );

                return;
            }

            folioActual =
                    reservaServicio.obtenerFolio(codigoReserva);

            lblReserva.setText(
                    "Reserva " + reserva.getCodigo()
                            + " | Apartamento: "
                            + reserva.getApartamento().getCodigo()
                            + " | Estado: "
                            + reserva.getEstado()
            );

            actualizarTablaYResumen();

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible cargar el folio",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void registrarMovimiento() {

        if (!validarFolioSeleccionado()) {
            return;
        }

        String codigo = txtCodigo.getText() == null
                ? ""
                : txtCodigo.getText().trim();

        String descripcion = txtDescripcion.getText() == null
                ? ""
                : txtDescripcion.getText().trim();

        String tipo = cbTipoMovimiento.getValue();

        if (codigo.isBlank()
                || descripcion.isBlank()
                || txtValor.getText() == null
                || txtValor.getText().isBlank()
                || tipo == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Completa el código, la descripción, el tipo y el valor."
            );

            return;
        }

        try {

            double valor = Double.parseDouble(
                    txtValor.getText()
                            .trim()
                            .replace(',', '.')
            );

            if ("Cargo".equals(tipo)) {

                folioActual.registrarCargo(
                        codigo,
                        descripcion,
                        valor
                );

            } else {

                folioActual.registrarPago(
                        codigo,
                        descripcion,
                        valor
                );
            }

            limpiarFormulario();

            actualizarTablaYResumen();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Movimiento registrado",
                    "El movimiento se agregó correctamente al folio."
            );

        } catch (NumberFormatException ex) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Valor inválido",
                    "Ingresa un valor numérico. Puedes usar punto o coma decimal."
            );

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible registrar el movimiento",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void deshacerUltimoMovimiento() {

        if (!validarFolioSeleccionado()) {
            return;
        }

        try {

            String codigoReversion =
                    "REV-" + System.currentTimeMillis();

            folioActual.deshacerUltimaOperacion(
                    codigoReversion
            );

            actualizarTablaYResumen();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Operación revertida",
                    "Se registró un movimiento contrario con código: "
                            + codigoReversion
                            + ". El movimiento original se conservó en el historial."
            );

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible deshacer la operación",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void actualizarReservas() {

        if (reservaServicio == null) {
            return;
        }

        String seleccionAnterior = cbReserva.getValue();

        cargarReservas();

        if (seleccionAnterior != null
                && cbReserva.getItems().contains(seleccionAnterior)) {

            cbReserva.setValue(seleccionAnterior);
        }
    }

    private void actualizarTablaYResumen() {

        movimientos.clear();

        if (folioActual == null) {

            lblSaldo.setText(
                    formatoMoneda.format(0)
            );

            lblCantidadMovimientos.setText(
                    "0 movimientos"
            );

            return;
        }

        for (MovimientoFolio movimiento
                : folioActual.listarMovimientos()) {

            movimientos.add(movimiento);
        }

        lblSaldo.setText(
                formatoMoneda.format(
                        folioActual.obtenerSaldo()
                )
        );

        lblCantidadMovimientos.setText(
                folioActual.cantidadMovimientos()
                        + " movimientos"
        );
    }

    private boolean validarFolioSeleccionado() {

        if (reservaServicio == null || folioActual == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona una reserva",
                    "Debes seleccionar una reserva para consultar o modificar su folio."
            );

            return false;
        }

        return true;
    }

    private void limpiarFormulario() {

        txtCodigo.clear();

        txtDescripcion.clear();

        txtValor.clear();

        cbTipoMovimiento.setValue("Cargo");
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(
                mensaje == null
                        ? "Ocurrió un error."
                        : mensaje
        );

        alerta.showAndWait();
    }
}
