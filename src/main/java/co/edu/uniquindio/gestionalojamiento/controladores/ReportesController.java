package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class ReportesController {

    @FXML private Label lblTotalReservas;
    @FXML private Label lblTotalHuespedes;
    @FXML private Label lblTotalApartamentos;

    @FXML private Label lblPendientes;
    @FXML private Label lblConfirmadas;
    @FXML private Label lblEnCurso;
    @FXML private Label lblFinalizadas;
    @FXML private Label lblCanceladas;
    @FXML private Label lblNoShow;

    @FXML private Label lblPreparados;
    @FXML private Label lblOcupados;
    @FXML private Label lblPendientesPreparacion;
    @FXML private Label lblEnPreparacion;
    @FXML private Label lblFueraServicio;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colCodigo;
    @FXML private TableColumn<Reserva, String> colApartamento;
    @FXML private TableColumn<Reserva, String> colCantidadHuespedes;
    @FXML private TableColumn<Reserva, String> colEntrada;
    @FXML private TableColumn<Reserva, String> colSalida;
    @FXML private TableColumn<Reserva, String> colEstado;

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        configurarColumnas();
    }

    public void configurar(CatalogoAlojamiento catalogo,
                           ReservaServicio reservaServicio) {
        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;
        actualizarReportes();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getCodigo()));

        colApartamento.setCellValueFactory(dato -> {
            Apartamento apartamento = dato.getValue().getApartamento();

            String nombre = apartamento == null
                    ? "Sin apartamento"
                    : apartamento.getCodigo() + " - " + apartamento.getNombre();

            return new SimpleStringProperty(nombre);
        });

        colCantidadHuespedes.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        String.valueOf(dato.getValue().getCantidadHuespedes())));

        colEntrada.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getFechaEntrada() == null ? ""
                                : dato.getValue().getFechaEntrada().format(FORMATO_FECHA)));

        colSalida.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getFechaSalida() == null ? ""
                                : dato.getValue().getFechaSalida().format(FORMATO_FECHA)));

        colEstado.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getEstado() == null ? ""
                                : dato.getValue().getEstado().toString()));
    }

    @FXML
    private void actualizarReportes() {
        if (catalogo == null || reservaServicio == null) {
            return;
        }

        int pendientes = 0;
        int confirmadas = 0;
        int enCurso = 0;
        int finalizadas = 0;
        int canceladas = 0;
        int noShow = 0;
        int totalReservas = 0;

        tablaReservas.getItems().clear();

        for (Reserva reserva : reservaServicio.listarReservas()) {
            totalReservas++;
            tablaReservas.getItems().add(reserva);

            EstadoReserva estado = reserva.getEstado();

            if (estado == EstadoReserva.PENDIENTE) pendientes++;
            else if (estado == EstadoReserva.CONFIRMADA) confirmadas++;
            else if (estado == EstadoReserva.EN_CURSO) enCurso++;
            else if (estado == EstadoReserva.FINALIZADA) finalizadas++;
            else if (estado == EstadoReserva.CANCELADA) canceladas++;
            else if (estado == EstadoReserva.NO_SHOW) noShow++;
        }

        lblTotalReservas.setText(String.valueOf(totalReservas));
        lblTotalHuespedes.setText(String.valueOf(catalogo.cantidadHuespedes()));
        lblTotalApartamentos.setText(String.valueOf(catalogo.cantidadApartamentos()));

        lblPendientes.setText(String.valueOf(pendientes));
        lblConfirmadas.setText(String.valueOf(confirmadas));
        lblEnCurso.setText(String.valueOf(enCurso));
        lblFinalizadas.setText(String.valueOf(finalizadas));
        lblCanceladas.setText(String.valueOf(canceladas));
        lblNoShow.setText(String.valueOf(noShow));

        int preparados = 0;
        int ocupados = 0;
        int pendientesPreparacion = 0;
        int enPreparacion = 0;
        int fueraServicio = 0;

        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            EstadoApartamento estado = apartamento.getEstado();

            if (estado == EstadoApartamento.PREPARADO) preparados++;
            else if (estado == EstadoApartamento.OCUPADO) ocupados++;
            else if (estado == EstadoApartamento.PENDIENTE_PREPARACION) pendientesPreparacion++;
            else if (estado == EstadoApartamento.EN_PREPARACION) enPreparacion++;
            else if (estado == EstadoApartamento.FUERA_DE_SERVICIO) fueraServicio++;
        }

        lblPreparados.setText(String.valueOf(preparados));
        lblOcupados.setText(String.valueOf(ocupados));
        lblPendientesPreparacion.setText(String.valueOf(pendientesPreparacion));
        lblEnPreparacion.setText(String.valueOf(enPreparacion));
        lblFueraServicio.setText(String.valueOf(fueraServicio));
    }
}
