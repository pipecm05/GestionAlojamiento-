package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrincipalController {

    private final CatalogoAlojamiento catalogo =
            new CatalogoAlojamiento();

    private final ReservaServicio reservaServicio =
            new ReservaServicio();

    private final TarifaServicio tarifaServicio =
            new TarifaServicio();

    private Timeline verificadorVencimientos;

    @FXML
    private BorderPane panelPrincipal;

    @FXML
    private VBox contenidoCentral;

    @FXML
    public void initialize() {
        mostrarInicio();
        iniciarVerificacionVencimientos();
    }

    /**
     * Inicia la revisión automática de las reservas vencidas.
     */
    private void iniciarVerificacionVencimientos() {

        // Revisar inmediatamente al iniciar la aplicación.
        verificarReservasVencidas();

        // Revisar nuevamente cada minuto.
        verificadorVencimientos = new Timeline(
                new KeyFrame(
                        Duration.minutes(1),
                        evento -> verificarReservasVencidas()
                )
        );

        verificadorVencimientos.setCycleCount(
                Animation.INDEFINITE
        );

        verificadorVencimientos.play();
    }

    /**
     * Comprueba si existen reservas pendientes vencidas.
     */
    private void verificarReservasVencidas() {

        try {

            int cantidadVencidas =
                    reservaServicio.vencerReservasPendientes(
                            LocalDateTime.now()
                    );

            if (cantidadVencidas > 0) {

                Alert alerta = new Alert(
                        Alert.AlertType.INFORMATION
                );

                alerta.setTitle("Reservas vencidas");
                alerta.setHeaderText(
                        "Se actualizaron las reservas automáticamente."
                );

                alerta.setContentText(
                        "Cantidad de reservas vencidas: "
                                + cantidadVencidas
                                + "\nLas fechas de los apartamentos "
                                + "fueron liberadas."
                );

                alerta.show();
            }

        } catch (RuntimeException ex) {

            System.err.println(
                    "Error al verificar las reservas vencidas: "
                            + ex.getMessage()
            );

            ex.printStackTrace();
        }
    }

    @FXML
    private void mostrarInicio() {
        cambiarContenido(
                "Panel principal",
                "Sistema de Gestión de Alojamiento",
                "Bienvenido. Selecciona una opción "
                        + "del menú para comenzar."
        );
    }

    @FXML
    private void mostrarAlojamientos() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/alojamientos.fxml"
                    )
            );

            Parent vista = loader.load();

            AlojamientoController controlador =
                    loader.getController();

            controlador.setCatalogo(catalogo);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de alojamientos.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarHuespedes() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/huespedes.fxml"
                    )
            );

            Parent vista = loader.load();

            HuespedController controlador =
                    loader.getController();

            controlador.setCatalogo(catalogo);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de huéspedes.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarReservas() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/reservas.fxml"
                    )
            );

            Parent vista = loader.load();

            ReservaController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    reservaServicio,
                    tarifaServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de reservas.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarPreparacion() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/preparacion.fxml"
                    )
            );

            Parent vista = loader.load();

            PreparacionController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    reservaServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la preparación de apartamentos.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarReportes() {

        cambiarContenido(
                "Reportes administrativos",
                "Indicadores y estadísticas",
                "Desde este módulo podrás consultar "
                        + "los reportes del sistema."
        );
    }

    private void cambiarContenido(
            String titulo,
            String subtitulo,
            String descripcion) {

        VBox contenido = new VBox(20);

        contenido.setPadding(
                new Insets(35, 35, 35, 35)
        );

        contenido.setStyle(
                "-fx-background-color: #F4F6F9;"
        );

        Label lblTitulo = new Label(titulo);

        lblTitulo.setStyle(
                "-fx-font-size: 28px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #243447;"
        );

        Label lblSubtitulo = new Label(subtitulo);

        lblSubtitulo.setStyle(
                "-fx-font-size: 17px;"
                        + "-fx-text-fill: #455A64;"
        );

        Label lblDescripcion = new Label(descripcion);

        lblDescripcion.setWrapText(true);

        lblDescripcion.setStyle(
                "-fx-font-size: 14px;"
        );

        contenido.getChildren().addAll(
                lblTitulo,
                lblSubtitulo,
                lblDescripcion
        );

        panelPrincipal.setCenter(contenido);
    }

    @FXML
    private void mostrarTemporadas() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/temporadas.fxml"
                    )
            );

            Parent vista = loader.load();

            TemporadasController controlador =
                    loader.getController();

            controlador.configurar(tarifaServicio);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la configuración de temporadas.",
                    ex
            );
        }
    }

    private void mostrarError(
            String mensaje,
            Exception ex) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Error de navegación");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(ex.getMessage());
        alerta.showAndWait();

        ex.printStackTrace();
    }
}
