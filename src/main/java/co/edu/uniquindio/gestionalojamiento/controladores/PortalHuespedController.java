package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.Comparator;

public class PortalHuespedController {

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;
    private FavoritoServicio favoritoServicio;

    private final ObservableList<Apartamento> alojamientos =
            FXCollections.observableArrayList();

    private final ObservableList<Reserva> reservas =
            FXCollections.observableArrayList();

    private final ObservableList<Apartamento> favoritos =
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
                new SpinnerValueFactoryFactory().crear(1, 20, 1)
        );

        configurarColumnas();
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio,
            FavoritoServicio favoritoServicio
    ) {

        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;
        this.favoritoServicio = favoritoServicio;

        cargarHuespedes();
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
        tablaFavoritos.setItems(favoritos);
    }

    private void cargarHuespedes() {

        cmbHuesped.getItems().clear();

        for (Huesped huesped : catalogo.listarHuespedes()) {
            cmbHuesped.getItems().add(huesped);
        }

        cmbHuesped.setConverter(
                new javafx.util.StringConverter<Huesped>() {

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
            return;
        }

        lblBienvenida.setText(
                "Bienvenido/a, " + huesped.getNombreCompleto()
        );

        cargarReservas();
        cargarFavoritos();
    }

    @FXML
    private void buscarAlojamientos() {

        try {

            if (cmbHuesped.getValue() == null) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Selecciona un huésped."
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
                    "Selecciona un huésped."
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

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "La solicitud fue registrada correctamente.\n\n"
                            + "Código: " + reserva.getCodigo()
                            + "\nApartamento: " + apartamento.getNombre()
                            + "\nEstado: " + reserva.getEstado()
                            + "\n\nLa reserva queda pendiente de confirmación."
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

    @FXML
    private void agregarFavorito() {

        Huesped huesped = cmbHuesped.getValue();

        Apartamento apartamento =
                tablaAlojamientos.getSelectionModel().getSelectedItem();

        if (huesped == null || apartamento == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un huésped y un apartamento."
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

    private static class SpinnerValueFactoryFactory {

        private javafx.scene.control.SpinnerValueFactory<Integer> crear(
                int minimo,
                int maximo,
                int inicial
        ) {

            return new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(
                    minimo,
                    maximo,
                    inicial
            );
        }
    }
}
