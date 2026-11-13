package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.MainApp;
import co.edu.uniquindio.gestionalojamiento.ContextoAplicacion;
import co.edu.uniquindio.gestionalojamiento.modelo.CuentaUsuario;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudCancelacion;
import co.edu.uniquindio.gestionalojamiento.enums.RolUsuario;
import co.edu.uniquindio.gestionalojamiento.servicios.Sesion;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.CanalExternoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.RutaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TurnoServicio;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Pos;
import java.util.List;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrincipalController {

    private final CatalogoAlojamiento catalogo =
            ContextoAplicacion.getInstancia().getCatalogo();

    private final ReservaServicio reservaServicio =
            ContextoAplicacion.getInstancia().getReservaServicio();

    private final CanalExternoServicio canalExternoServicio =
            ContextoAplicacion.getInstancia().getCanalExternoServicio();

    private final TarifaServicio tarifaServicio =
            ContextoAplicacion.getInstancia().getTarifaServicio();

    private final EmpleadoServicio empleadoServicio =
            ContextoAplicacion.getInstancia().getEmpleadoServicio();

    private final TurnoServicio turnoServicio =
            ContextoAplicacion.getInstancia().getTurnoServicio();

    private final FavoritoServicio favoritoServicio =
            ContextoAplicacion.getInstancia().getFavoritoServicio();

    private final RutaServicio rutaServicio =
            ContextoAplicacion.getInstancia().getRutaServicio();

    private Timeline verificadorVencimientos;

    @FXML
    private BorderPane panelPrincipal;

    @FXML
    private VBox contenidoCentral;

    @FXML
    private VBox menuLateral;

    private CuentaUsuario cuentaUsuario;

    public void configurarCuenta(CuentaUsuario cuentaUsuario) {

        this.cuentaUsuario = cuentaUsuario;

        if (cuentaUsuario != null
                && cuentaUsuario.getHuesped() != null) {

            String documento =
                    cuentaUsuario.getHuesped().getDocumento();

            if (catalogo.buscarHuesped(documento) == null) {
                catalogo.registrarHuesped(
                        cuentaUsuario.getHuesped()
                );
            }
        }

        aplicarPermisos();

        mostrarInicio();
    }

    private void aplicarPermisos() {

        boolean administrador =
                cuentaUsuario != null
                        && cuentaUsuario.esAdministrador();

        if (menuLateral == null) {
            return;
        }

        for (Node nodo : menuLateral.getChildren()) {

            if (nodo instanceof Button boton) {

                String texto = boton.getText();
                boolean esPortalHuesped = texto.contains("Portal del huésped");
                boolean permitido = administrador
                        ? !esPortalHuesped
                        : texto.contains("Inicio") || esPortalHuesped;

                boton.setVisible(permitido);
                boton.setManaged(permitido);

            } else if (nodo instanceof Label etiqueta
                    && etiqueta.getText() != null
                    && etiqueta.getText().equals("ADMINISTRACIÓN")) {

                etiqueta.setVisible(administrador);
                etiqueta.setManaged(administrador);
            }
        }
    }

    private boolean requiereAdministrador() {

        if (cuentaUsuario != null
                && cuentaUsuario.getRol()
                == RolUsuario.ADMINISTRADOR) {

            return true;
        }

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Acceso restringido");
        alerta.setHeaderText(
                "Esta función es exclusiva del administrador."
        );
        alerta.setContentText(
                "Inicia sesión con una cuenta administrativa para continuar."
        );

        alerta.showAndWait();

        return false;
    }

    @FXML
    private void cerrarSesion() {

        if (verificadorVencimientos != null) {
            verificadorVencimientos.stop();
        }

        Sesion.cerrar();
        MainApp.mostrarLogin();
    }

    @FXML
    public void initialize() {

        // Los servicios y los datos de prueba se inicializan una sola vez
        // en ContextoAplicacion y se comparten entre todas las sesiones.
        mostrarInicio();
        iniciarVerificacionVencimientos();
    }

    private void iniciarVerificacionVencimientos() {

        verificarReservasVencidas();

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
        if (cuentaUsuario != null && !cuentaUsuario.esAdministrador()) {
            mostrarInicioHuesped();
        } else {
            mostrarInicioAdministrador();
        }
    }

    private void mostrarInicioAdministrador() {
        VBox pagina = crearPaginaDashboard();

        Label etiqueta = crearEtiqueta("PANEL DE CONTROL", 12, "#6B8298", true);
        Label titulo = crearEtiqueta("Bienvenido al centro de gestión", 29, "#1E304B", true);
        Label descripcion = crearEtiqueta(
                "Administra alojamientos, reservas y huéspedes desde un solo lugar.",
                14, "#68798B", false);
        VBox encabezado = new VBox(7, etiqueta, titulo, descripcion);
        encabezado.setPadding(new Insets(4, 0, 8, 0));

        int totalAlojamientos = catalogo.cantidadApartamentos();
        int totalHuespedes = catalogo.cantidadHuespedes();
        int totalReservas = 0;
        for (Reserva reserva : reservaServicio.listarReservas()) {
            totalReservas++;
        }
        int solicitudesPendientes = 0;
        for (var solicitud : reservaServicio.listarSolicitudesCancelacion()) {
            if (solicitud.getEstado() == EstadoSolicitudCancelacion.PENDIENTE) {
                solicitudesPendientes++;
            }
        }

        FlowPane metricas = new FlowPane();
        metricas.setHgap(15);
        metricas.setVgap(15);
        metricas.getChildren().addAll(
                crearTarjetaMetrica("ALOJAMIENTOS", String.valueOf(totalAlojamientos), "Unidades registradas", "⌂", "#E8F0FA", "#315A83"),
                crearTarjetaMetrica("HUÉSPEDES", String.valueOf(totalHuespedes), "Clientes registrados", "♙", "#E8F5F0", "#28785B"),
                crearTarjetaMetrica("RESERVAS", String.valueOf(totalReservas), "Reservas en el sistema", "▣", "#FFF3E5", "#B66A25"),
                crearTarjetaMetrica("CANCELACIONES", String.valueOf(solicitudesPendientes), "Solicitudes por revisar", "↺", "#FCEBEC", "#B5474D")
        );

        Label accesosTitulo = crearEtiqueta("Accesos rápidos", 20, "#1E304B", true);
        Label accesosSubtitulo = crearEtiqueta("Continúa con las tareas principales del alojamiento.", 13, "#718096", false);
        FlowPane accesos = new FlowPane();
        accesos.setHgap(15);
        accesos.setVgap(15);
        accesos.getChildren().addAll(
                crearTarjetaAcceso("Gestionar alojamientos", "Registra y actualiza los apartamentos.", "⌂", this::mostrarAlojamientos),
                crearTarjetaAcceso("Gestionar reservas", "Consulta reservas y solicitudes de cancelación.", "▣", this::mostrarReservas),
                crearTarjetaAcceso("Gestionar huéspedes", "Consulta la información de tus huéspedes.", "♙", this::mostrarHuespedes),
                crearTarjetaAcceso("Consultar reportes", "Revisa los indicadores de operación.", "▤", this::mostrarReportes)
        );

        VBox actividad = crearPanelBlanco();
        Label actividadTitulo = crearEtiqueta("Reservas recientes", 18, "#1E304B", true);
        actividad.getChildren().add(actividadTitulo);
        int mostradas = 0;
        for (Reserva reserva : reservaServicio.listarReservas()) {
            if (mostradas >= 5) break;
            HBox fila = new HBox(12);
            fila.setAlignment(Pos.CENTER_LEFT);
            Label codigo = crearEtiqueta(reserva.getCodigo(), 13, "#243B53", true);
            Label apartamento = crearEtiqueta("Apartamento " + reserva.getApartamento().getCodigo(), 12, "#718096", false);
            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            Label estado = crearEtiqueta(reserva.getEstado().toString(), 11, "#315A83", true);
            fila.getChildren().addAll(codigo, apartamento, espacio, estado);
            fila.setPadding(new Insets(10, 0, 10, 0));
            actividad.getChildren().addAll(new Separator(), fila);
            mostradas++;
        }
        if (mostradas == 0) {
            actividad.getChildren().add(crearEtiqueta("Todavía no hay reservas registradas.", 13, "#718096", false));
        }

        pagina.getChildren().addAll(encabezado, metricas, accesosTitulo, accesosSubtitulo, accesos, actividad);
        panelPrincipal.setCenter(crearContenedorDesplazable(pagina));
    }

    private void mostrarInicioHuesped() {
        VBox pagina = crearPaginaDashboard();
        String nombre = "huésped";
        if (cuentaUsuario != null && cuentaUsuario.getHuesped() != null) {
            nombre = cuentaUsuario.getHuesped().getNombres();
        }

        StackPane banner = new StackPane();
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(30));
        banner.setMinHeight(205);
        banner.setStyle("-fx-background-color: linear-gradient(to right, #1E304B, #355B78); -fx-background-radius: 18;");
        VBox textosBanner = new VBox(10);
        Label bienvenida = crearEtiqueta("BIENVENIDO A APARTA-SUITES UQ", 11, "#D7E5F1", true);
        Label titulo = crearEtiqueta("Hola, " + nombre + " 👋", 30, "#FFFFFF", true);
        Label mensaje = crearEtiqueta("Tu próxima estadía comienza aquí. Encuentra un espacio\npara descansar, disfrutar y sentirte como en casa.", 14, "#E5EDF4", false);
        Button explorar = crearBotonPrincipal("Explorar alojamientos", this::mostrarPortalHuesped);
        textosBanner.getChildren().addAll(bienvenida, titulo, mensaje, explorar);
        banner.getChildren().add(textosBanner);

        int misReservas = 0;
        int misSolicitudes = 0;
        String documento = cuentaUsuario != null && cuentaUsuario.getHuesped() != null
                ? cuentaUsuario.getHuesped().getDocumento() : null;
        if (documento != null) {
            for (Reserva reserva : reservaServicio.listarReservas()) {
                if (reserva.getHuesped() != null && documento.equals(reserva.getHuesped().getDocumento())) {
                    misReservas++;
                }
            }
            for (var solicitud : reservaServicio.listarSolicitudesCancelacion()) {
                if (solicitud.getReserva() != null && solicitud.getReserva().getHuesped() != null
                        && documento.equals(solicitud.getReserva().getHuesped().getDocumento())) {
                    misSolicitudes++;
                }
            }
        }

        FlowPane metricas = new FlowPane();
        metricas.setHgap(15);
        metricas.setVgap(15);
        metricas.getChildren().addAll(
                crearTarjetaMetrica("MIS RESERVAS", String.valueOf(misReservas), "Reservas registradas", "▣", "#E8F0FA", "#315A83"),
                crearTarjetaMetrica("MIS SOLICITUDES", String.valueOf(misSolicitudes), "Solicitudes de cancelación", "↺", "#FFF3E5", "#B66A25"),
                crearTarjetaMetrica("ALOJAMIENTOS", String.valueOf(catalogo.cantidadApartamentos()), "Opciones disponibles en el catálogo", "⌂", "#E8F5F0", "#28785B")
        );

        Label tituloAccesos = crearEtiqueta("¿Qué deseas hacer hoy?", 20, "#1E304B", true);
        Label subtituloAccesos = crearEtiqueta("Accede rápidamente a los servicios de tu portal.", 13, "#718096", false);
        FlowPane accesos = new FlowPane();
        accesos.setHgap(15);
        accesos.setVgap(15);
        accesos.getChildren().addAll(
                crearTarjetaAcceso("Buscar alojamiento", "Consulta disponibilidad y encuentra tu espacio ideal.", "⌂", this::mostrarPortalHuesped),
                crearTarjetaAcceso("Mis reservas", "Consulta tus reservas, estados y pagos.", "▣", this::mostrarPortalHuesped),
                crearTarjetaAcceso("Mis favoritos", "Revisa los alojamientos que guardaste.", "♡", this::mostrarPortalHuesped)
        );

        pagina.getChildren().addAll(banner, metricas, tituloAccesos, subtituloAccesos, accesos);
        panelPrincipal.setCenter(crearContenedorDesplazable(pagina));
    }

    private VBox crearPaginaDashboard() {
        VBox pagina = new VBox(22);
        pagina.setPadding(new Insets(30, 34, 36, 34));
        pagina.setStyle("-fx-background-color: #F3F6FA;");
        return pagina;
    }

    private ScrollPane crearContenedorDesplazable(VBox contenido) {
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #F3F6FA; -fx-background-color: #F3F6FA; -fx-border-color: transparent;");
        return scroll;
    }

    private VBox crearTarjetaMetrica(String titulo, String valor, String detalle,
                                     String simbolo, String fondoIcono, String colorIcono) {
        VBox tarjeta = crearPanelBlanco();
        tarjeta.setSpacing(8);
        tarjeta.setPrefWidth(205);
        tarjeta.setMinWidth(175);
        HBox superior = new HBox();
        superior.setAlignment(Pos.CENTER_LEFT);
        Label icono = crearEtiqueta(simbolo, 22, colorIcono, true);
        StackPane circulo = new StackPane(icono);
        circulo.setMinSize(42, 42);
        circulo.setMaxSize(42, 42);
        circulo.setStyle("-fx-background-color: " + fondoIcono + "; -fx-background-radius: 12;");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        superior.getChildren().addAll(circulo, espacio);
        tarjeta.getChildren().addAll(superior,
                crearEtiqueta(titulo, 11, "#718096", true),
                crearEtiqueta(valor, 27, "#1E304B", true),
                crearEtiqueta(detalle, 11, "#8795A5", false));
        return tarjeta;
    }

    private VBox crearTarjetaAcceso(String titulo, String descripcion, String simbolo, Runnable accion) {
        VBox tarjeta = crearPanelBlanco();
        tarjeta.setSpacing(10);
        tarjeta.setPrefWidth(245);
        tarjeta.setMinHeight(145);
        Label icono = crearEtiqueta(simbolo, 23, "#315A83", true);
        StackPane iconoFondo = new StackPane(icono);
        iconoFondo.setAlignment(Pos.CENTER);
        iconoFondo.setMinSize(44, 44);
        iconoFondo.setMaxSize(44, 44);
        iconoFondo.setStyle("-fx-background-color: #E8F0FA; -fx-background-radius: 12;");
        tarjeta.getChildren().addAll(iconoFondo,
                crearEtiqueta(titulo, 14, "#243B53", true),
                crearEtiqueta(descripcion, 11, "#718096", false));
        Button abrir = new Button("Abrir →");
        abrir.setOnAction(e -> accion.run());
        abrir.setStyle("-fx-background-color: transparent; -fx-text-fill: #315A83; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 2 0 0 0;");
        tarjeta.getChildren().add(abrir);
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #B9CADD; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(30,48,75,0.12), 12, 0.15, 0, 4); -fx-cursor: hand; -fx-padding: 20;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E3EAF1; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(30,48,75,0.06), 10, 0.10, 0, 2); -fx-padding: 20;"));
        return tarjeta;
    }

    private VBox crearPanelBlanco() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E3EAF1; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(30,48,75,0.06), 10, 0.10, 0, 2);");
        return panel;
    }

    private Label crearEtiqueta(String texto, int tamano, String color, boolean negrita) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: " + tamano + "px; -fx-text-fill: " + color + ";"
                + (negrita ? " -fx-font-weight: bold;" : ""));
        return label;
    }

    private Button crearBotonPrincipal(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setOnAction(e -> accion.run());
        boton.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #1E304B; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 9; -fx-padding: 11 18; -fx-cursor: hand;");
        return boton;
    }

    @FXML
    private void mostrarAlojamientos() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/alojamientos.fxml"
                    )
            );

            Parent vista = loader.load();

            AlojamientoController controlador =
                    loader.getController();

            controlador.configurar(catalogo, reservaServicio);

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

        if (!requiereAdministrador()) return;

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
    private void mostrarEmpleados() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/empleados.fxml"
                    )
            );

            Parent vista = loader.load();

            EmpleadoController controlador =
                    loader.getController();

            controlador.configurar(empleadoServicio);

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de empleados.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarTurnos() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/turnos.fxml"
                    )
            );

            Parent vista = loader.load();

            TurnoController controlador =
                    loader.getController();

            controlador.configurar(
                    empleadoServicio,
                    turnoServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de turnos.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarFavoritos() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/favoritos.fxml"
                    )
            );

            Parent vista = loader.load();

            FavoritosController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    favoritoServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException ex) {

            mostrarError(
                    "No fue posible abrir la gestión de favoritos.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarReservas() {

        if (!requiereAdministrador()) return;

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
    private void mostrarCanalExterno() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/canal-externo.fxml"
                    )
            );

            Parent vista = loader.load();

            CanalExternoController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    canalExternoServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir el procesamiento del canal externo.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarCalendario() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/calendario.fxml"
                    )
            );

            Parent vista = loader.load();

            CalendarioController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    reservaServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir el calendario de ocupación.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarFolio() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/folio.fxml"
                    )
            );

            Parent vista = loader.load();

            FolioController controlador =
                    loader.getController();

            controlador.configurar(reservaServicio);

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir la gestión del folio.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarPreparacion() {

        if (!requiereAdministrador()) return;

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
    private void mostrarTemporadas() {

        if (!requiereAdministrador()) return;

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

    @FXML
    private void mostrarReportes() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/reportes.fxml"
                    )
            );

            Parent vista = loader.load();

            ReportesController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    reservaServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir los reportes administrativos.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarMapa() {

        if (!requiereAdministrador()) return;

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/mapa.fxml"
                    )
            );

            Parent vista = loader.load();

            MapaController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    rutaServicio
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir el mapa y la gestión de rutas.",
                    ex
            );
        }
    }

    @FXML
    private void mostrarPortalHuesped() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/vistas/portal-huesped.fxml"
                    )
            );

            Parent vista = loader.load();

            PortalHuespedController controlador =
                    loader.getController();

            controlador.configurar(
                    catalogo,
                    reservaServicio,
                    favoritoServicio,
                    tarifaServicio,
                    cuentaUsuario != null
                            && cuentaUsuario.getHuesped() != null
                            ? cuentaUsuario.getHuesped()
                            : null
            );

            panelPrincipal.setCenter(vista);

        } catch (IOException | RuntimeException ex) {

            mostrarError(
                    "No fue posible abrir el portal del huésped.",
                    ex
            );
        }
    }

    private void mostrarError(
            String mensaje,
            Exception ex
    ) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Error de navegación");
        alerta.setHeaderText(mensaje);

        alerta.setContentText(
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "Ocurrió un error inesperado."
        );

        alerta.showAndWait();

        ex.printStackTrace();
    }
}
