package co.edu.uniquindio.gestionalojamiento.controladores;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.io.IOException;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;

public class PrincipalController {

    private final CatalogoAlojamiento catalogo =
            new CatalogoAlojamiento();

    private final ReservaServicio reservaServicio =
            new ReservaServicio();

    private final TarifaServicio tarifaServicio =
            new TarifaServicio();

    @FXML
    private BorderPane panelPrincipal;

    @FXML
    private VBox contenidoCentral;

    @FXML
    public void initialize() {
        mostrarInicio();
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
                            "/vistas/alojamientos.fxml")
            );

            Parent vista = loader.load();

            AlojamientoController controlador =
                    loader.getController();

            controlador.setCatalogo(catalogo);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de navegación");
            alerta.setHeaderText(
                    "No fue posible abrir la gestión de alojamientos.");

            alerta.setContentText(ex.getMessage());
            alerta.showAndWait();

            ex.printStackTrace();
        }
    }

    @FXML
    private void mostrarHuespedes() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/huespedes.fxml")
            );

            Parent vista = loader.load();

            HuespedController controlador =
                    loader.getController();

            controlador.setCatalogo(catalogo);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de navegación");
            alerta.setHeaderText(
                    "No fue posible abrir la gestión de huéspedes.");

            alerta.setContentText(ex.getMessage());
            alerta.showAndWait();

            ex.printStackTrace();
        }
    }

    @FXML
    private void mostrarReservas() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/reservas.fxml")
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

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de navegación");
            alerta.setHeaderText(
                    "No fue posible abrir la gestión de reservas.");

            alerta.setContentText(ex.getMessage());
            alerta.showAndWait();

            ex.printStackTrace();
        }
    }

    @FXML
    private void mostrarPreparacion() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/preparacion.fxml")
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

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de navegación");
            alerta.setHeaderText(
                    "No fue posible abrir la preparación de apartamentos."
            );

            alerta.setContentText(ex.getMessage());
            alerta.showAndWait();

            ex.printStackTrace();
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
                    getClass().getResource("/vistas/temporadas.fxml")
            );

            Parent vista = loader.load();

            TemporadasController controlador =
                    loader.getController();

            controlador.configurar(tarifaServicio);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de navegación");
            alerta.setHeaderText(
                    "No fue posible abrir la configuración de temporadas."
            );

            alerta.setContentText(ex.getMessage());
            alerta.showAndWait();

            ex.printStackTrace();
        }
    }
}
