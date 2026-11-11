package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.estructuras.AristaGrafo;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.RutaServicio;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapaController {

    @FXML
    private TextField txtCodigoUbicacion;

    @FXML
    private TextField txtDistancia;

    @FXML
    private ComboBox<String> cbOrigen;

    @FXML
    private ComboBox<String> cbDestino;

    @FXML
    private ComboBox<String> cbOrigenRecorrido;

    @FXML
    private ListView<String> lvUbicaciones;

    @FXML
    private ListView<String> lvResultadoRuta;

    @FXML
    private Label lblResultado;

    @FXML
    private Pane panelGrafo;

    private CatalogoAlojamiento catalogo;
    private RutaServicio rutaServicio;

    private final List<String> rutaResaltada = new ArrayList<>();
    private final Set<String> nodosResaltados = new HashSet<>();

    @FXML
    public void initialize() {

        panelGrafo.widthProperty().addListener(
                (observable, anterior, nuevo) -> dibujarGrafo()
        );

        panelGrafo.heightProperty().addListener(
                (observable, anterior, nuevo) -> dibujarGrafo()
        );

        lblResultado.setText(
                "Registra ubicaciones y crea conexiones para visualizar el mapa."
        );
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            RutaServicio rutaServicio) {

        if (catalogo == null || rutaServicio == null) {
            throw new IllegalArgumentException(
                    "Los servicios son obligatorios."
            );
        }

        this.catalogo = catalogo;
        this.rutaServicio = rutaServicio;

        cargarApartamentos();
        actualizarUbicaciones();

        Platform.runLater(this::dibujarGrafo);
    }

    private void cargarApartamentos() {

        for (Apartamento apartamento :
                catalogo.listarApartamentos()) {

            rutaServicio.agregarApartamento(apartamento);
        }
    }

    @FXML
    private void agregarUbicacion() {

        try {

            String codigo =
                    txtCodigoUbicacion.getText().trim();

            if (codigo.isBlank()) {
                mostrarInformacion(
                        "Ingresa el código de la ubicación."
                );
                return;
            }

            boolean agregada =
                    rutaServicio.agregarUbicacion(codigo);

            if (!agregada) {
                mostrarInformacion(
                        "La ubicación ya está registrada."
                );
                return;
            }

            txtCodigoUbicacion.clear();

            limpiarResaltado();
            actualizarUbicaciones();

            lblResultado.setText(
                    "Ubicación agregada: " + codigo
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void conectarUbicaciones() {

        try {

            String origen = cbOrigen.getValue();
            String destino = cbDestino.getValue();

            if (origen == null || destino == null) {
                mostrarInformacion(
                        "Selecciona el origen y el destino."
                );
                return;
            }

            if (txtDistancia.getText().isBlank()) {
                mostrarInformacion(
                        "Ingresa la distancia de la conexión."
                );
                return;
            }

            double distancia = Double.parseDouble(
                    txtDistancia.getText()
                            .trim()
                            .replace(",", ".")
            );

            boolean conectadas =
                    rutaServicio.conectarUbicaciones(
                            origen,
                            destino,
                            distancia
                    );

            if (!conectadas) {
                mostrarInformacion(
                        "Estas ubicaciones ya están conectadas."
                );
                return;
            }

            txtDistancia.clear();
            limpiarResaltado();
            dibujarGrafo();

            lblResultado.setText(
                    "Conexión creada: " + origen
                            + " - " + destino
                            + " (" + distancia + ")"
            );

        } catch (NumberFormatException ex) {
            mostrarError(
                    "La distancia debe ser un número válido."
            );
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void consultarRuta() {

        try {

            String origen = cbOrigen.getValue();
            String destino = cbDestino.getValue();

            if (origen == null || destino == null) {
                mostrarInformacion(
                        "Selecciona el origen y el destino."
                );
                return;
            }

            ListaDoble<String> ruta =
                    rutaServicio.obtenerRutaMasCorta(
                            origen,
                            destino
                    );

            lvResultadoRuta.getItems().clear();
            rutaResaltada.clear();
            nodosResaltados.clear();

            if (ruta.estaVacia()) {

                dibujarGrafo();

                lblResultado.setText(
                        "No existe una ruta entre esas ubicaciones."
                );

                return;
            }

            for (String ubicacion : ruta) {
                rutaResaltada.add(ubicacion);
                lvResultadoRuta.getItems().add(ubicacion);
            }

            double distancia =
                    rutaServicio.obtenerDistanciaMinima(
                            origen,
                            destino
                    );

            dibujarGrafo();

            lblResultado.setText(
                    "Ruta más corta: "
                            + String.join(" → ", rutaResaltada)
                            + " | Distancia total: "
                            + distancia
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void recorrerBfs() {

        ejecutarRecorrido(true);
    }

    @FXML
    private void recorrerDfs() {

        ejecutarRecorrido(false);
    }

    private void ejecutarRecorrido(boolean anchura) {

        try {

            String origen = cbOrigenRecorrido.getValue();

            if (origen == null) {
                mostrarInformacion(
                        "Selecciona la ubicación inicial."
                );
                return;
            }

            ListaDoble<String> recorrido;

            if (anchura) {
                recorrido = rutaServicio.recorrerBfs(origen);
            } else {
                recorrido = rutaServicio.recorrerDfs(origen);
            }

            limpiarResaltado();

            for (String ubicacion : recorrido) {

                nodosResaltados.add(ubicacion);

                lvResultadoRuta.getItems().add(ubicacion);
            }

            dibujarGrafo();

            lblResultado.setText(
                    (anchura ? "Recorrido BFS: " : "Recorrido DFS: ")
                            + String.join(" → ",
                            lvResultadoRuta.getItems())
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void limpiarResaltado() {

        rutaResaltada.clear();
        nodosResaltados.clear();

        lvResultadoRuta.getItems().clear();
    }

    @FXML
    private void actualizarUbicaciones() {

        if (rutaServicio == null) {
            return;
        }

        List<String> ubicaciones = new ArrayList<>();

        for (String codigo : rutaServicio.listarUbicaciones()) {
            ubicaciones.add(codigo);
        }

        Collections.sort(ubicaciones);

        lvUbicaciones.setItems(
                FXCollections.observableArrayList(ubicaciones)
        );

        cbOrigen.setItems(
                FXCollections.observableArrayList(ubicaciones)
        );

        cbDestino.setItems(
                FXCollections.observableArrayList(ubicaciones)
        );

        cbOrigenRecorrido.setItems(
                FXCollections.observableArrayList(ubicaciones)
        );

        dibujarGrafo();
    }

    private void dibujarGrafo() {

        if (panelGrafo == null || rutaServicio == null) {
            return;
        }

        panelGrafo.getChildren().clear();

        List<String> ubicaciones = new ArrayList<>();

        for (String codigo : rutaServicio.listarUbicaciones()) {
            ubicaciones.add(codigo);
        }

        Collections.sort(ubicaciones);

        double ancho = Math.max(panelGrafo.getWidth(), 850);
        double alto = Math.max(panelGrafo.getHeight(), 430);

        panelGrafo.setPrefSize(ancho, alto);

        if (ubicaciones.isEmpty()) {

            Text mensaje = new Text(
                    30, 45,
                    "No hay ubicaciones registradas. Agrega una para comenzar."
            );

            mensaje.setFont(Font.font(16));
            mensaje.setFill(Color.DARKSLATEGRAY);

            panelGrafo.getChildren().add(mensaje);
            return;
        }

        Map<String, double[]> posiciones =
                calcularPosiciones(ubicaciones, ancho, alto);

        dibujarConexiones(ubicaciones, posiciones);
        dibujarNodos(ubicaciones, posiciones);
    }

    private Map<String, double[]> calcularPosiciones(
            List<String> ubicaciones,
            double ancho,
            double alto) {

        Map<String, double[]> posiciones = new HashMap<>();

        double centroX = ancho / 2;
        double centroY = alto / 2;

        double radioX = Math.max(100, ancho * 0.36);
        double radioY = Math.max(90, alto * 0.34);

        int cantidad = ubicaciones.size();

        for (int i = 0; i < cantidad; i++) {

            double angulo =
                    (2 * Math.PI * i / cantidad)
                            - Math.PI / 2;

            double x = centroX + radioX * Math.cos(angulo);
            double y = centroY + radioY * Math.sin(angulo);

            posiciones.put(
                    ubicaciones.get(i),
                    new double[]{x, y}
            );
        }

        return posiciones;
    }

    private void dibujarConexiones(
            List<String> ubicaciones,
            Map<String, double[]> posiciones) {

        for (String origen : ubicaciones) {

            Iterable<AristaGrafo<String>> conexiones =
                    rutaServicio.obtenerConexionesDesde(origen);

            for (AristaGrafo<String> arista : conexiones) {

                String destino = arista.getDestino().getDato();

                // Evita dibujar dos veces las conexiones bidireccionales.
                if (origen.compareTo(destino) >= 0) {
                    continue;
                }

                double[] puntoOrigen = posiciones.get(origen);
                double[] puntoDestino = posiciones.get(destino);

                if (puntoOrigen == null || puntoDestino == null) {
                    continue;
                }

                boolean perteneceRuta =
                        perteneceALaRuta(origen, destino);

                Line linea = new Line(
                        puntoOrigen[0],
                        puntoOrigen[1],
                        puntoDestino[0],
                        puntoDestino[1]
                );

                linea.setStroke(
                        perteneceRuta
                                ? Color.FORESTGREEN
                                : Color.web("#9AA7B2")
                );

                linea.setStrokeWidth(
                        perteneceRuta ? 5 : 2
                );

                panelGrafo.getChildren().add(linea);

                double mitadX =
                        (puntoOrigen[0] + puntoDestino[0]) / 2;

                double mitadY =
                        (puntoOrigen[1] + puntoDestino[1]) / 2;

                Text peso = new Text(
                        mitadX + 5,
                        mitadY - 5,
                        String.valueOf(arista.getPeso())
                );

                peso.setFont(Font.font(12));
                peso.setFill(Color.web("#243447"));

                panelGrafo.getChildren().add(peso);
            }
        }
    }

    private boolean perteneceALaRuta(
            String origen,
            String destino) {

        for (int i = 0; i < rutaResaltada.size() - 1; i++) {

            String actual = rutaResaltada.get(i);
            String siguiente = rutaResaltada.get(i + 1);

            if ((actual.equals(origen) && siguiente.equals(destino))
                    || (actual.equals(destino) && siguiente.equals(origen))) {
                return true;
            }
        }

        return false;
    }

    private void dibujarNodos(
            List<String> ubicaciones,
            Map<String, double[]> posiciones) {

        for (String codigo : ubicaciones) {

            double[] posicion = posiciones.get(codigo);

            boolean resaltado =
                    rutaResaltada.contains(codigo)
                            || nodosResaltados.contains(codigo);

            Circle nodo = new Circle(
                    posicion[0],
                    posicion[1],
                    27
            );

            nodo.setFill(
                    resaltado
                            ? Color.web("#F4A340")
                            : Color.web("#2878A8")
            );

            nodo.setStroke(Color.WHITE);
            nodo.setStrokeWidth(3);

            Text etiqueta = new Text(
                    posicion[0] - codigo.length() * 3.5,
                    posicion[1] + 4,
                    codigo
            );

            etiqueta.setFill(Color.WHITE);
            etiqueta.setFont(Font.font(11));

            panelGrafo.getChildren().addAll(nodo, etiqueta);
        }
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Información");
        alerta.setHeaderText("Mapa y rutas");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No fue posible realizar la operación"
        );

        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
