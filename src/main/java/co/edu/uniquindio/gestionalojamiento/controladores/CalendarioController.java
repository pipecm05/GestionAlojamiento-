
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarioController {

    @FXML
    private Label lblMes;

    @FXML
    private Label lblFechaSeleccionada;

    @FXML
    private Label lblResumen;

    @FXML
    private GridPane gridCalendario;

    @FXML
    private VBox contenedorDetalle;

    private CatalogoAlojamiento catalogo;
    private ReservaServicio reservaServicio;

    private YearMonth mesActual;
    private LocalDate fechaSeleccionada;

    private final Locale locale = new Locale("es", "CO");

    private final DateTimeFormatter formatoMes =
            DateTimeFormatter.ofPattern("MMMM yyyy", locale);

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern(
                    "EEEE dd 'de' MMMM 'de' yyyy",
                    locale
            );

    @FXML
    public void initialize() {
        mesActual = YearMonth.now();
        fechaSeleccionada = LocalDate.now();
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            ReservaServicio reservaServicio
    ) {
        if (catalogo == null || reservaServicio == null) {
            throw new IllegalArgumentException(
                    "Los servicios del calendario son obligatorios."
            );
        }

        this.catalogo = catalogo;
        this.reservaServicio = reservaServicio;

        actualizarVista();
    }

    @FXML
    private void mesAnterior() {
        mesActual = mesActual.minusMonths(1);
        actualizarVista();
    }

    @FXML
    private void mesSiguiente() {
        mesActual = mesActual.plusMonths(1);
        actualizarVista();
    }

    @FXML
    private void irAHoy() {
        fechaSeleccionada = LocalDate.now();
        mesActual = YearMonth.from(fechaSeleccionada);
        actualizarVista();
    }

    private void actualizarVista() {
        if (catalogo == null || reservaServicio == null) {
            return;
        }

        actualizarTituloMes();
        dibujarDias();
        mostrarDetalleFecha();
    }

    private void actualizarTituloMes() {
        lblMes.setText(
                mesActual.format(formatoMes).toUpperCase(locale)
        );
    }

    private void dibujarDias() {
        gridCalendario.getChildren().clear();

        LocalDate primerDia = mesActual.atDay(1);

        int columnaInicial =
                primerDia.getDayOfWeek().getValue() - 1;

        int cantidadDias = mesActual.lengthOfMonth();

        for (int numeroDia = 1;
             numeroDia <= cantidadDias;
             numeroDia++) {

            LocalDate fecha = mesActual.atDay(numeroDia);

            int posicion = columnaInicial + numeroDia - 1;
            int columna = posicion % 7;
            int fila = posicion / 7;

            Button botonDia = crearBotonDia(fecha);

            gridCalendario.add(
                    botonDia,
                    columna,
                    fila
            );
        }
    }

    private Button crearBotonDia(LocalDate fecha) {
        Button boton = new Button();

        int ocupados = contarEstado(fecha, EstadoVisual.OCUPADO);
        int reservados = contarEstado(fecha, EstadoVisual.RESERVADO);
        int preparacion = contarEstado(fecha, EstadoVisual.PREPARACION);

        boton.setText(
                fecha.getDayOfMonth()
                        + "\n"
                        + "Ocupados: " + ocupados
                        + "\n"
                        + "Reservas: " + reservados
        );

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setPrefHeight(78);
        boton.setMinHeight(70);

        String estilo =
                "-fx-background-color: " + colorDia(fecha) + ";"
                        + "-fx-text-fill: #243447;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-radius: 8;"
                        + "-fx-border-color: #DCE3EA;"
                        + "-fx-cursor: hand;";

        if (fecha.equals(fechaSeleccionada)) {
            estilo += "-fx-border-color: #243447;"
                    + "-fx-border-width: 2;";
        }

        if (fecha.equals(LocalDate.now())) {
            estilo += "-fx-font-weight: bold;";
        }

        boton.setStyle(estilo);

        boton.setTooltip(
                new Tooltip(
                        fecha.format(formatoFecha)
                                + "\nOcupados: " + ocupados
                                + "\nReservados: " + reservados
                                + "\nEn preparación: " + preparacion
                )
        );

        boton.setOnAction(evento -> {
            fechaSeleccionada = fecha;
            actualizarVista();
        });

        return boton;
    }

    private String colorDia(LocalDate fecha) {
        int fueraServicio =
                contarEstado(fecha, EstadoVisual.FUERA_SERVICIO);

        int ocupados =
                contarEstado(fecha, EstadoVisual.OCUPADO);

        int reservados =
                contarEstado(fecha, EstadoVisual.RESERVADO);

        int preparacion =
                contarEstado(fecha, EstadoVisual.PREPARACION);

        if (fueraServicio > 0) {
            return "#E0E3E8";
        }

        if (ocupados > 0) {
            return "#F8D7DA";
        }

        if (reservados > 0) {
            return "#FFF0B3";
        }

        if (preparacion > 0) {
            return "#FFE0B2";
        }

        return "#D8F3DC";
    }

    private int contarEstado(
            LocalDate fecha,
            EstadoVisual estadoBuscado
    ) {
        int cantidad = 0;

        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            if (consultarEstado(apartamento, fecha) == estadoBuscado) {
                cantidad++;
            }
        }

        return cantidad;
    }

    private EstadoVisual consultarEstado(
            Apartamento apartamento,
            LocalDate fecha
    ) {
        if (apartamento.getEstado()
                == EstadoApartamento.FUERA_DE_SERVICIO) {
            return EstadoVisual.FUERA_SERVICIO;
        }

        for (Reserva reserva : reservaServicio.listarReservas()) {

            if (!reserva.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {
                continue;
            }

            /*
             * La fecha de entrada se incluye.
             * La fecha de salida no se incluye.
             */
            boolean fechaDentroDeReserva =
                    !fecha.isBefore(reserva.getFechaEntrada())
                            && fecha.isBefore(reserva.getFechaSalida());

            if (!fechaDentroDeReserva) {
                continue;
            }

            EstadoReserva estado = reserva.getEstado();

            if (estado == EstadoReserva.CANCELADA
                    || estado == EstadoReserva.NO_SHOW) {
                continue;
            }

            if (estado == EstadoReserva.EN_CURSO) {
                return EstadoVisual.OCUPADO;
            }

            if (estado == EstadoReserva.CONFIRMADA
                    || estado == EstadoReserva.PENDIENTE) {
                return EstadoVisual.RESERVADO;
            }

            if (estado == EstadoReserva.FINALIZADA) {
                return EstadoVisual.FINALIZADO;
            }
        }

        /*
         * El estado operativo del apartamento se utiliza para
         * representar su situación actual, no para alterar
         * las fechas históricas o futuras.
         */
        if (fecha.equals(LocalDate.now())) {

            EstadoApartamento estado = apartamento.getEstado();

            if (estado == EstadoApartamento.PENDIENTE_PREPARACION
                    || estado == EstadoApartamento.EN_PREPARACION) {
                return EstadoVisual.PREPARACION;
            }

            if (estado == EstadoApartamento.OCUPADO) {
                return EstadoVisual.OCUPADO;
            }
        }

        return EstadoVisual.DISPONIBLE;
    }

    private void mostrarDetalleFecha() {
        contenedorDetalle.getChildren().clear();

        lblFechaSeleccionada.setText(
                fechaSeleccionada.format(formatoFecha)
        );

        int disponibles = 0;
        int ocupados = 0;
        int reservados = 0;
        int preparacion = 0;
        int fueraServicio = 0;
        int finalizados = 0;

        for (Apartamento apartamento : catalogo.listarApartamentos()) {

            EstadoVisual estado =
                    consultarEstado(apartamento, fechaSeleccionada);

            Label etiqueta = new Label(
                    apartamento.getCodigo()
                            + " - "
                            + apartamento.getNombre()
                            + "     "
                            + textoEstado(estado)
            );

            etiqueta.setMaxWidth(Double.MAX_VALUE);
            etiqueta.setWrapText(true);
            etiqueta.setStyle(
                    "-fx-background-color: " + colorEstado(estado) + ";"
                            + "-fx-text-fill: #243447;"
                            + "-fx-font-size: 13px;"
                            + "-fx-padding: 10;"
                            + "-fx-background-radius: 7;"
            );

            contenedorDetalle.getChildren().add(etiqueta);

            switch (estado) {
                case DISPONIBLE:
                    disponibles++;
                    break;
                case OCUPADO:
                    ocupados++;
                    break;
                case RESERVADO:
                    reservados++;
                    break;
                case PREPARACION:
                    preparacion++;
                    break;
                case FUERA_SERVICIO:
                    fueraServicio++;
                    break;
                case FINALIZADO:
                    finalizados++;
                    break;
            }
        }

        lblResumen.setText(
                "Disponibles: " + disponibles
                        + " | Ocupados: " + ocupados
                        + " | Reservados: " + reservados
                        + " | Preparación: " + preparacion
                        + " | Fuera de servicio: " + fueraServicio
                        + " | Estancias finalizadas: " + finalizados
        );
    }

    private String textoEstado(EstadoVisual estado) {
        switch (estado) {
            case DISPONIBLE:
                return "DISPONIBLE";
            case OCUPADO:
                return "OCUPADO";
            case RESERVADO:
                return "RESERVADO";
            case PREPARACION:
                return "PENDIENTE / EN PREPARACIÓN";
            case FUERA_SERVICIO:
                return "FUERA DE SERVICIO";
            case FINALIZADO:
                return "ESTANCIA FINALIZADA";
            default:
                return "SIN ESTADO";
        }
    }

    private String colorEstado(EstadoVisual estado) {
        switch (estado) {
            case DISPONIBLE:
                return "#D8F3DC";
            case OCUPADO:
                return "#F8D7DA";
            case RESERVADO:
                return "#FFF0B3";
            case PREPARACION:
                return "#FFE0B2";
            case FUERA_SERVICIO:
                return "#E0E3E8";
            case FINALIZADO:
                return "#DDE5F0";
            default:
                return "#FFFFFF";
        }
    }

    private enum EstadoVisual {
        DISPONIBLE,
        OCUPADO,
        RESERVADO,
        PREPARACION,
        FUERA_SERVICIO,
        FINALIZADO
    }
}
