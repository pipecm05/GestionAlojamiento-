package co.edu.uniquindio.gestionalojamiento;

import co.edu.uniquindio.gestionalojamiento.modelo.CuentaUsuario;
import co.edu.uniquindio.gestionalojamiento.servicios.Sesion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage escenarioPrincipal;

    @Override
    public void start(Stage stage) {
        escenarioPrincipal = stage;

        // Inicializa los servicios y carga los datos una sola vez.
        ContextoAplicacion.getInstancia();

        mostrarLogin();
    }

    public static void mostrarLogin() {
        try {
            Sesion.cerrar();

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/vistas/login.fxml")
            );

            Scene scene = new Scene(loader.load());

            escenarioPrincipal.setTitle(
                    "Aparta-Suites UQ - Inicio de sesión"
            );

            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.setWidth(520);
            escenarioPrincipal.setHeight(620);
            escenarioPrincipal.centerOnScreen();
            escenarioPrincipal.show();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No fue posible abrir el inicio de sesión.",
                    ex
            );
        }
    }

    public static void mostrarPrincipal(CuentaUsuario cuenta) {
        try {
            // Garantiza que el contexto exista antes de crear el controlador.
            ContextoAplicacion.getInstancia();

            Sesion.iniciar(cuenta);

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/vistas/principal.fxml")
            );

            Scene scene = new Scene(loader.load());

            co.edu.uniquindio.gestionalojamiento.controladores.PrincipalController
                    controlador = loader.getController();

            controlador.configurarCuenta(cuenta);

            escenarioPrincipal.setTitle(
                    "Aparta-Suites UQ - " + cuenta.getRol()
            );

            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.setWidth(1200);
            escenarioPrincipal.setHeight(750);
            escenarioPrincipal.centerOnScreen();
            escenarioPrincipal.show();

        } catch (Exception ex) {
            Sesion.cerrar();

            throw new IllegalStateException(
                    "No fue posible abrir el sistema.",
                    ex
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
