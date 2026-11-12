package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.MainApp;
import co.edu.uniquindio.gestionalojamiento.modelo.CuentaUsuario;
import co.edu.uniquindio.gestionalojamiento.servicios.AutenticacionServicio;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class LoginController {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblMensaje;

    @FXML
    private void iniciarSesion() {

        String correo = txtCorreo.getText() == null
                ? ""
                : txtCorreo.getText().trim();

        String contrasena = txtContrasena.getText();

        CuentaUsuario cuenta =
                AutenticacionServicio.getInstancia()
                        .autenticar(correo, contrasena);

        if (cuenta == null) {
            lblMensaje.setText(
                    "Correo o contraseña incorrectos."
            );
            return;
        }

        try {
            MainApp.mostrarPrincipal(cuenta);
        } catch (RuntimeException ex) {
            lblMensaje.setText(
                    "No fue posible ingresar al sistema."
            );
            ex.printStackTrace();
        }
    }

    @FXML
    private void registrarCuenta() {

        Dialog<ButtonType> dialogo = new Dialog<>();

        dialogo.setTitle("Registro de huésped");
        dialogo.setHeaderText(
                "Crea tu cuenta para ingresar al portal"
        );

        ButtonType registrar = new ButtonType(
                "Crear cuenta",
                ButtonBar.ButtonData.OK_DONE
        );

        dialogo.getDialogPane()
                .getButtonTypes()
                .addAll(registrar, ButtonType.CANCEL);

        TextField documento = new TextField();
        TextField nombres = new TextField();
        TextField apellidos = new TextField();
        TextField telefono = new TextField();
        TextField correo = new TextField();

        DatePicker nacimiento = new DatePicker();

        PasswordField clave = new PasswordField();
        PasswordField confirmar = new PasswordField();

        documento.setPromptText("Documento");
        nombres.setPromptText("Nombres");
        apellidos.setPromptText("Apellidos");
        telefono.setPromptText("Teléfono");
        correo.setPromptText("Correo electrónico");
        clave.setPromptText("Mínimo 3 caracteres");
        confirmar.setPromptText("Repite la contraseña");

        GridPane formulario = new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(9);

        formulario.addRow(0, new Label("Documento:"), documento);
        formulario.addRow(1, new Label("Nombres:"), nombres);
        formulario.addRow(2, new Label("Apellidos:"), apellidos);
        formulario.addRow(3, new Label("Teléfono:"), telefono);
        formulario.addRow(4, new Label("Correo:"), correo);
        formulario.addRow(5, new Label("Nacimiento:"), nacimiento);
        formulario.addRow(6, new Label("Contraseña:"), clave);
        formulario.addRow(7, new Label("Confirmar:"), confirmar);

        dialogo.getDialogPane().setContent(formulario);

        dialogo.setResultConverter(boton ->
                boton == registrar ? registrar : null
        );

        dialogo.showAndWait().ifPresent(resultado -> {

            if (resultado != registrar) {
                return;
            }

            try {

                if (!clave.getText().equals(confirmar.getText())) {
                    throw new IllegalArgumentException(
                            "Las contraseñas no coinciden."
                    );
                }

                AutenticacionServicio.getInstancia()
                        .registrarHuesped(
                                documento.getText(),
                                nombres.getText(),
                                apellidos.getText(),
                                telefono.getText(),
                                correo.getText(),
                                nacimiento.getValue(),
                                clave.getText()
                        );

                txtCorreo.setText(correo.getText().trim());
                txtContrasena.clear();

                lblMensaje.setText(
                        "Cuenta creada. Ahora inicia sesión."
                );

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Tu cuenta fue creada correctamente. "
                                + "Inicia sesión para continuar."
                ).showAndWait();

            } catch (RuntimeException ex) {

                new Alert(
                        Alert.AlertType.ERROR,
                        ex.getMessage()
                ).showAndWait();
            }
        });
    }
}
