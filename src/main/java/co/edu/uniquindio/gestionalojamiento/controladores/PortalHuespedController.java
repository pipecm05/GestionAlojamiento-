package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.DetalleTarifaNoche;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.FolioServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.Comparator;

public class PortalHuespedController {

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;
    private FavoritoServicio favoritoServicio;
    private TarifaServicio tarifaServicio;

    private Huesped huespedAutenticado;

    private final ObservableList<Apartamento> alojamientos =
            FXCollections.observableArrayList();

    private final ObservableList<Reserva> reservas =
            FXCollections.observableArrayList();

    private final ObservableList<Apartamento> favoritos =
            FXCollections.observableArrayList();

    private final ObservableList<DetalleTarifaNoche> detallesTarifa =
            FXCollections.observableArrayList();

    @FXML
    private ComboBox<Huesped> cmbHuesped;

    @FXML
    private DatePicker dpEntrada;

    @FXML
    private DatePicker dpSalida;

    @FXML
    private Spinner<Integer> spPersonas;

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblResultado;

    @FXML
    private Label lblTotalReserva;

    @FXML
    private Label lblAnticipo;

    @FXML
    private Label lblSaldoPendiente;

    @FXML
    private Label lblEstadoPago;

    @FXML
    private TableView<Apartamento> tablaAlojamientos;

    @FXML
    private TableColumn<Apartamento, String> colCodigoAlojamiento;

    @FXML
    private TableColumn<Apartamento, String> colNombreAlojamiento;

    @FXML
    private TableColumn<Apartamento, String> colCapacidad;

    @FXML
    private TableColumn<Apartamento, String> colHabitaciones;

    @FXML
    private TableColumn<Apartamento, String> colTarifa;

    @FXML
    private TableView<Reserva> tablaReservas;

    @FXML
    private TableColumn<Reserva, String> colCodigoReserva;

    @FXML
    private TableColumn<Reserva, String> colApartamentoReserva;

    @FXML
    private TableColumn<Reserva, String> colFechasReserva;

    @FXML
    private TableColumn<Reserva, String> colEstadoReserva;

    @FXML
    private TableView<DetalleTarifaNoche> tablaDesglose;

    @FXML
    private TableColumn<DetalleTarifaNoche, String> colFechaDesglose;

    @FXML
    private TableColumn<DetalleTarifaNoche, String> colTemporadaDesglose;

    @FXML
    private TableColumn<DetalleTarifaNoche, String> colPersonasDesglose;

    @FXML
    private TableColumn<DetalleTarifaNoche, String> colTarifaDesglose;

    @FXML
    private TableColumn<DetalleTarifaNoche, String> colSubtotalDesglose;

    @FXML
    private TableView<Apartamento> tablaFavoritos;

    @FXML
    private TableColumn<Apartamento, String> colCodigoFavorito;

    @FXML
    private TableColumn<Apartamento, String> colNombreFavorito;

    @FXML
    private TableColumn<Apartamento, String> colCapacidadFavorito;

    @FXML
    public void initialize() {

        spPersonas.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, 20, 1
                )
        );

        configurarColumnas();

        tablaReservas.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        mostrarDesglose(seleccionada)
                );

        limpiarResumenTarifa();
    }

    /*
     * Configuración utilizada cuando no se recibe
     * un huésped autenticado.
     */
    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            FavoritoServicio favoritoServicio,
            TarifaServicio tarifaServicio
    ) {

        configurar(
                catalogo,
                reservaServicio,
                favoritoServicio,
                tarifaServicio,
                null
        );
    }

    /*
     * Configuración utilizada desde PrincipalController.
     * Recibe el huésped que inició sesión.
     */
    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            FavoritoServicio favoritoServicio,
            TarifaServicio tarifaServicio,
            Huesped huespedAutenticado
    ) {

        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;
        this.favoritoServicio = favoritoServicio;
        this.tarifaServicio = tarifaServicio;
        this.huespedAutenticado = huespedAutenticado;

        cargarHuespedes(huespedAutenticado);
    }

    private void configurarColumnas() {

        colCodigoAlojamiento.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getCodigo()
                )
        );

        colNombreAlojamiento.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getNombre()
                )
        );

        colCapacidad.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        String.valueOf(
                                dato.getValue().getCapacidadMaxima()
                        )
                )
        );

        colHabitaciones.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        String.valueOf(
                                dato.getValue().getNumeroHabitaciones()
                        )
                )
        );

        colTarifa.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        formatoMoneda(
                                dato.getValue().getTarifaBaja()
                        )
                )
        );

        colCodigoReserva.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getCodigo()
                )
        );

        colApartamentoReserva.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getApartamento().getNombre()
                )
        );

        colFechasReserva.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getFechaEntrada()
                                + " al "
                                + dato.getValue().getFechaSalida()
                )
        );

        colEstadoReserva.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getEstado().toString()
                )
        );

        colFechaDesglose.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getFecha().toString()
                )
        );

        colTemporadaDesglose.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getTemporada().toString()
                )
        );

        colPersonasDesglose.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getPersonasQuePagan()
                                + " de "
                                + dato.getValue().getTotalOcupantes()
                )
        );

        colTarifaDesglose.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        formatoMoneda(
                                dato.getValue().getTarifaPorPersona()
                        )
                )
        );

        colSubtotalDesglose.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        formatoMoneda(
                                dato.getValue().getSubtotal()
                        )
                )
        );

        colCodigoFavorito.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getCodigo()
                )
        );

        colNombreFavorito.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getNombre()
                )
        );

        colCapacidadFavorito.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        String.valueOf(
                                dato.getValue().getCapacidadMaxima()
                        )
                )
        );

        tablaAlojamientos.setItems(alojamientos);
        tablaReservas.setItems(reservas);
        tablaDesglose.setItems(detallesTarifa);
        tablaFavoritos.setItems(favoritos);
    }

    private void cargarHuespedes(Huesped huespedAutenticado) {

        cmbHuesped.getItems().clear();

        if (huespedAutenticado != null) {

            cmbHuesped.getItems().add(huespedAutenticado);

            cmbHuesped.setVisible(false);
            cmbHuesped.setManaged(false);

        } else {

            cmbHuesped.setVisible(true);
            cmbHuesped.setManaged(true);

            for (Huesped huesped : catalogo.listarHuespedes()) {
                cmbHuesped.getItems().add(huesped);
            }
        }

        cmbHuesped.setConverter(
                new StringConverter<Huesped>() {

                    @Override
                    public String toString(Huesped huesped) {

                        if (huesped == null) {
                            return "";
                        }

                        return huesped.getNombreCompleto()
                                + " - "
                                + huesped.getDocumento();
                    }

                    @Override
                    public Huesped fromString(String texto) {
                        return null;
                    }
                }
        );

        cmbHuesped.setOnAction(event -> actualizarPerfil());

        if (!cmbHuesped.getItems().isEmpty()) {

            cmbHuesped.getSelectionModel().selectFirst();

            actualizarPerfil();

        } else {

            actualizarPerfil();
        }
    }

    private void actualizarPerfil() {

        Huesped huesped = cmbHuesped.getValue();

        if (huesped == null) {

            lblBienvenida.setText(
                    "Selecciona un huésped para comenzar."
            );

            reservas.clear();
            favoritos.clear();
            detallesTarifa.clear();

            limpiarResumenTarifa();

            return;
        }

        lblBienvenida.setText(
                "Bienvenido/a, " + huesped.getNombreCompleto()
        );

        cargarReservas();
        cargarFavoritos();

        detallesTarifa.clear();

        limpiarResumenTarifa();
    }

    @FXML
    private void buscarAlojamientos() {

        try {

            if (cmbHuesped.getValue() == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "No se encontró un huésped asociado a la sesión."
                );

                return;
            }

            LocalDate entrada = dpEntrada.getValue();
            LocalDate salida = dpSalida.getValue();

            if (entrada == null || salida == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Selecciona las fechas de entrada y salida."
                );

                return;
            }

            if (!entrada.isBefore(salida)) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "La salida debe ser posterior a la entrada."
                );

                return;
            }

            if (entrada.isBefore(LocalDate.now())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "La fecha de entrada no puede estar en el pasado."
                );

                return;
            }

            int cantidadPersonas = spPersonas.getValue();

            alojamientos.clear();

            for (Apartamento apartamento : catalogo.listarApartamentos()) {

                boolean tieneCapacidad =
                        apartamento.getCapacidadMaxima()
                                >= cantidadPersonas;

                boolean disponible = reservaServicio.estaDisponible(
                        apartamento,
                        entrada,
                        salida
                );

                if (tieneCapacidad && disponible) {
                    alojamientos.add(apartamento);
                }
            }

            alojamientos.sort(
                    Comparator.comparingDouble(
                            Apartamento::getTarifaBaja
                    )
            );

            lblResultado.setText(
                    "Apartamentos disponibles encontrados: "
                            + alojamientos.size()
            );

            if (alojamientos.isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "No se encontraron apartamentos disponibles "
                                + "para las fechas seleccionadas."
                );
            }

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible consultar la disponibilidad: "
                            + ex.getMessage()
            );
        }
    }

    @FXML
    private void solicitarReserva() {

        Huesped huesped = cmbHuesped.getValue();

        Apartamento apartamento =
                tablaAlojamientos.getSelectionModel().getSelectedItem();

        if (huesped == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se encontró un huésped asociado a la sesión."
            );

            return;
        }

        if (apartamento == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un apartamento disponible."
            );

            return;
        }

        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();

        if (entrada == null || salida == null
                || !entrada.isBefore(salida)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un rango de fechas válido."
            );

            return;
        }

        if (spPersonas.getValue() > apartamento.getCapacidadMaxima()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "La cantidad de personas supera la capacidad "
                            + "del apartamento."
            );

            return;
        }

        try {

            String codigo = "PORT-" + System.currentTimeMillis();

            Reserva reserva = reservaServicio.registrarReserva(
                    codigo,
                    apartamento,
                    huesped,
                    entrada,
                    salida,
                    CanalReserva.PORTAL
            );

            cargarReservas();

            tablaReservas.getSelectionModel().select(reserva);
            tablaReservas.scrollTo(reserva);

            mostrarDesglose(reserva);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "La solicitud fue registrada correctamente.\n\n"
                            + "Código: " + reserva.getCodigo()
                            + "\nApartamento: " + apartamento.getNombre()
                            + "\nEstado: " + reserva.getEstado()
                            + "\n\nEl administrador debe verificar y "
                            + "registrar el anticipo del 30 % para "
                            + "confirmar la reserva."
            );

            buscarAlojamientos();

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible registrar la solicitud: "
                            + ex.getMessage()
            );
        }
    }

    private void mostrarDesglose(Reserva reserva) {

        detallesTarifa.clear();

        limpiarResumenTarifa();

        if (reserva == null || tarifaServicio == null) {
            return;
        }

        try {

            ListaDoble<DetalleTarifaNoche> desglose =
                    tarifaServicio.generarDesglose(reserva);

            for (DetalleTarifaNoche detalle : desglose) {
                detallesTarifa.add(detalle);
            }

            double total = tarifaServicio.calcularTotal(reserva);

            FolioServicio folio =
                    reservaServicio.obtenerFolio(reserva.getCodigo());

            double anticipoRequerido =
                    folio.calcularAnticipoPortal(tarifaServicio);

            double pagosRegistrados = folio.obtenerPagosNetos();

            double saldo = Math.max(0, total - pagosRegistrados);

            lblTotalReserva.setText(formatoMoneda(total));

            lblAnticipo.setText(
                    formatoMoneda(anticipoRequerido)
            );

            lblSaldoPendiente.setText(
                    formatoMoneda(saldo)
            );

            if (reserva.getEstado().toString().equals("CONFIRMADA")) {

                lblEstadoPago.setText(
                        "Reserva confirmada. Pagos registrados: "
                                + formatoMoneda(pagosRegistrados)
                );

            } else {

                lblEstadoPago.setText(
                        "Anticipo registrado: "
                                + formatoMoneda(pagosRegistrados)
                                + ". Se requiere el 30 % para confirmar."
                );
            }

        } catch (RuntimeException ex) {

            lblEstadoPago.setText(
                    "No fue posible calcular el desglose: "
                            + ex.getMessage()
            );
        }
    }

    /*
     * El huésped no puede registrar pagos ni confirmar
     * reservas desde el portal.
     *
     * Se conserva este método para evitar errores si el FXML
     * todavía tiene conectado el botón anterior.
     */
    @FXML
    private void pagarAnticipoYConfirmar() {

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "El pago del anticipo no se realiza desde el portal.\n\n"
                        + "Tu solicitud permanece pendiente hasta que "
                        + "el administrador verifique y registre "
                        + "el anticipo correspondiente."
        );
    }

    @FXML
    private void agregarFavorito() {

        Huesped huesped = cmbHuesped.getValue();

        Apartamento apartamento =
                tablaAlojamientos.getSelectionModel().getSelectedItem();

        if (huesped == null || apartamento == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un apartamento para agregarlo "
                            + "a favoritos."
            );

            return;
        }

        try {

            boolean agregado = favoritoServicio.agregarFavorito(
                    huesped,
                    apartamento
            );

            if (agregado) {

                cargarFavoritos();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "El apartamento se agregó a favoritos."
                );

            } else {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Este apartamento ya está en tus favoritos."
                );
            }

        } catch (RuntimeException ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No fue posible agregar el favorito: "
                            + ex.getMessage()
            );
        }
    }

    @FXML
    private void eliminarFavorito() {

        Huesped huesped = cmbHuesped.getValue();

        Apartamento apartamento =
                tablaFavoritos.getSelectionModel().getSelectedItem();

        if (huesped == null || apartamento == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un favorito para eliminar."
            );

            return;
        }

        boolean eliminado = favoritoServicio.eliminarFavorito(
                huesped.getDocumento(),
                apartamento.getCodigo()
        );

        if (eliminado) {

            cargarFavoritos();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "El apartamento se eliminó de favoritos."
            );
        }
    }

    private void cargarReservas() {

        reservas.clear();

        Huesped huesped = cmbHuesped.getValue();

        if (huesped == null) {
            return;
        }

        for (Reserva reserva : reservaServicio.listarReservas()) {

            for (Huesped huespedReserva : reserva.getHuespedes()) {

                if (huespedReserva.getDocumento()
                        .equals(huesped.getDocumento())) {

                    reservas.add(reserva);

                    break;
                }
            }
        }
    }

    private void cargarFavoritos() {

        favoritos.clear();

        Huesped huesped = cmbHuesped.getValue();

        if (huesped == null) {
            return;
        }

        ListaDoble<Favorito> lista =
                favoritoServicio.obtenerFavoritos(
                        huesped.getDocumento()
                );

        for (Favorito favorito : lista) {

            if (favorito.getAlojamiento() instanceof Apartamento) {

                favoritos.add(
                        (Apartamento) favorito.getAlojamiento()
                );
            }
        }
    }

    private void limpiarResumenTarifa() {

        if (lblTotalReserva != null) {
            lblTotalReserva.setText("$ 0");
        }

        if (lblAnticipo != null) {
            lblAnticipo.setText("$ 0");
        }

        if (lblSaldoPendiente != null) {
            lblSaldoPendiente.setText("$ 0");
        }

        if (lblEstadoPago != null) {

            lblEstadoPago.setText(
                    "Selecciona una reserva para consultar sus valores."
            );
        }
    }

    private String formatoMoneda(double valor) {

        return String.format(
                java.util.Locale.forLanguageTag("es-CO"),
                "$ %,.0f",
                valor
        );
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Portal del huésped");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
