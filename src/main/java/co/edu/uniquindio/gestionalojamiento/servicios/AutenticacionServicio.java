
package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.RolUsuario;
import co.edu.uniquindio.gestionalojamiento.modelo.CuentaUsuario;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AutenticacionServicio {

    private static final AutenticacionServicio INSTANCIA =
            new AutenticacionServicio();

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_CLAVE = 256;

    private final Map<String, CuentaUsuario> cuentas =
            new HashMap<>();

    private AutenticacionServicio() {

        String salt = generarSalt();

        cuentas.put(
                "1@gmail.com",
                new CuentaUsuario(
                        "1@gmail.com",
                        generarHash("123".toCharArray(), salt),
                        salt,
                        RolUsuario.ADMINISTRADOR,
                        null
                )
        );
    }

    public static AutenticacionServicio getInstancia() {
        return INSTANCIA;
    }

    public CuentaUsuario autenticar(
            String correo,
            String contrasena) {

        if (correo == null || contrasena == null) {
            return null;
        }

        String correoNormalizado =
                correo.trim().toLowerCase();

        CuentaUsuario cuenta =
                cuentas.get(correoNormalizado);

        if (cuenta == null) {
            return null;
        }

        String hashIngresado = generarHash(
                contrasena.toCharArray(),
                cuenta.getSalt()
        );

        if (hashIngresado.equals(
                cuenta.getHashContrasena())) {
            return cuenta;
        }

        return null;
    }

    public CuentaUsuario registrarHuesped(
            String documento,
            String nombres,
            String apellidos,
            String telefono,
            String correo,
            LocalDate nacimiento,
            String contrasena) {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio."
            );
        }

        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException(
                    "Los nombres son obligatorios."
            );
        }

        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios."
            );
        }

        if (correo == null || correo.isBlank()
                || !correo.contains("@")) {
            throw new IllegalArgumentException(
                    "Ingresa un correo válido."
            );
        }

        if (contrasena == null || contrasena.length() < 3) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 3 caracteres."
            );
        }

        if (nacimiento == null
                || !nacimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Selecciona una fecha de nacimiento válida."
            );
        }

        String correoNormalizado =
                correo.trim().toLowerCase();

        if (cuentas.containsKey(correoNormalizado)) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con ese correo."
            );
        }

        boolean documentoRegistrado =
                cuentas.values().stream()
                        .filter(cuenta ->
                                cuenta.getHuesped() != null)
                        .anyMatch(cuenta ->
                                cuenta.getHuesped()
                                        .getDocumento()
                                        .equalsIgnoreCase(
                                                documento.trim()
                                        ));

        if (documentoRegistrado) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con ese documento."
            );
        }

        Huesped huesped = new Huesped(
                documento.trim(),
                nombres.trim(),
                apellidos.trim(),
                telefono == null ? "" : telefono.trim(),
                correoNormalizado,
                nacimiento
        );

        String salt = generarSalt();

        CuentaUsuario cuenta = new CuentaUsuario(
                correoNormalizado,
                generarHash(contrasena.toCharArray(), salt),
                salt,
                RolUsuario.HUESPED,
                huesped
        );

        cuentas.put(correoNormalizado, cuenta);

        return cuenta;
    }

    private String generarSalt() {

        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    private String generarHash(
            char[] contrasena,
            String saltBase64) {

        PBEKeySpec spec = new PBEKeySpec(
                contrasena,
                Base64.getDecoder().decode(saltBase64),
                ITERACIONES,
                LONGITUD_CLAVE
        );

        try {

            byte[] hash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                    "No fue posible proteger la contraseña.",
                    ex
            );

        } finally {
            spec.clearPassword();
        }
    }
}
