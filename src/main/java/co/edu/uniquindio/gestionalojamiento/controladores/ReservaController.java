package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
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

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colCodigo;
    @FXML private TableColumn<Reserva, String> colHuesped;
    @FXML private TableColumn<Reserva, String> colApartamento;
    @FXML private TableColumn<Reserva, String> colEntrada;
    @FXML private TableColumn<Reserva, String> colSalida;
    @FXML private TableColumn<Reserva, String> colCanal;
    @FXML private TableColumn<Reserva, String> colEstado;

    @FXML
    private void initialize() {

        cbCanal.getItems().addAll(
                CanalReserva.DIRECTA,
                CanalReserva.PORTAL
        );

        cbCanal.setValue(CanalReserva.DIRECTA);

        colCodigo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getCodigo()));

        colHuesped.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(
                        d.getValue().getHuesped()
                                .getNombreCompleto()));

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
    }

    @FXML
    private void cancelarSeleccionada() {

        if (reservaServicio == null) {
            return;
        }

        Reserva seleccionada =
                tablaReservas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            error("Selecciona una reserva en la tabla.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Cancelar la reserva "
                        + seleccionada.getCodigo() + "?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText(null);

        if (confirmacion.showAndWait()
                .orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        try {
            reservaServicio.cancelarReserva(
                    seleccionada.getCodigo()
            );

            mostrarTodas();

            lblEstado.setText(
                    "Reserva cancelada: "
                            + seleccionada.getCodigo()
            );

        } catch (IllegalArgumentException
                 | IllegalStateException ex) {
            error(ex.getMessage());
        }
    }

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

}
