
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

    private static final String RECEPCION = "RECEPCION";
    private static final String PASILLO_CENTRAL = "PASILLO";
    private static final String ESCALERA_A = "ESCALERA_A";
    private static final String ESCALERA_B = "ESCALERA_B";
    private static final String ASEO_A = "ASEO_A";
    private static final String ASEO_B = "ASEO_B";

    @FXML
    private ComboBox<String> cbApartamentoDisponible;

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
                "Selecciona alojamientos del catálogo para agregarlos al mapa."
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

        actualizarUbicaciones();
        actualizarApartamentosDisponibles();

        Platform.runLater(this::dibujarGrafo);
    }

    /*
     * AGREGAR ALOJAMIENTOS EXISTENTES
     */

    private void actualizarApartamentosDisponibles() {

        if (catalogo == null || rutaServicio == null) {
            return;
        }

        List<String> disponibles = new ArrayList<>();

        for (Apartamento apartamento : catalogo.listarApartamentos()) {

            String codigo = apartamento.getCodigo();

            if (!estaRegistrado(codigo)) {
                disponibles.add(codigo);
            }
        }

        Collections.sort(disponibles);

        cbApartamentoDisponible.setItems(
                FXCollections.observableArrayList(disponibles)
        );

        if (!disponibles.isEmpty()) {
            cbApartamentoDisponible.getSelectionModel().selectFirst();
        }
    }

    private boolean estaRegistrado(String codigo) {

        for (String ubicacion : rutaServicio.listarUbicaciones()) {

            if (ubicacion.equals(codigo)) {
                return true;
            }
        }

        return false;
    }

    @FXML
    private void agregarUbicacion() {

        try {

            String codigo = cbApartamentoDisponible.getValue();

            if (codigo == null) {
                mostrarInformacion(
                        "No hay apartamentos disponibles para agregar. "
                                + "Primero registra alojamientos en el catálogo."
                );
                return;
            }

            Apartamento apartamentoEncontrado = null;

            for (Apartamento apartamento : catalogo.listarApartamentos()) {

                if (apartamento.getCodigo().equals(codigo)) {
                    apartamentoEncontrado = apartamento;
                    break;
                }
            }

            if (apartamentoEncontrado == null) {
                mostrarError(
                        "El apartamento seleccionado ya no existe en el catálogo."
                );
                actualizarApartamentosDisponibles();
                return;
            }

            boolean agregado =
                    rutaServicio.agregarApartamento(apartamentoEncontrado);

            if (!agregado) {
                mostrarInformacion(
                        "Este apartamento ya está registrado en el mapa."
                );
                actualizarApartamentosDisponibles();
                return;
            }

            limpiarResaltado();
            actualizarUbicaciones();
            actualizarApartamentosDisponibles();

            lblResultado.setText(
                    "Apartamento agregado al mapa: " + codigo
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /*
     * ESCENARIO DE PRUEBA
     */

    @FXML
    private void cargarEscenarioPrueba() {

        try {

            agregarSiNoExiste(RECEPCION);
            agregarSiNoExiste(PASILLO_CENTRAL);
            agregarSiNoExiste(ESCALERA_A);
            agregarSiNoExiste(ESCALERA_B);
            agregarSiNoExiste(ASEO_A);
            agregarSiNoExiste(ASEO_B);

            conectarSiEsPosible(RECEPCION, PASILLO_CENTRAL, 3);
            conectarSiEsPosible(PASILLO_CENTRAL, ESCALERA_A, 5);
            conectarSiEsPosible(PASILLO_CENTRAL, ESCALERA_B, 5);
            conectarSiEsPosible(PASILLO_CENTRAL, ASEO_A, 4);
            conectarSiEsPosible(PASILLO_CENTRAL, ASEO_B, 4);

            List<String> apartamentos = obtenerCodigosApartamentos();

            Collections.sort(apartamentos);

            int contador = 0;

            for (String codigo : apartamentos) {

                agregarSiNoExiste(codigo);

                if (contador % 2 == 0) {
                    conectarSiEsPosible(ESCALERA_A, codigo, 3);
                } else {
                    conectarSiEsPosible(ESCALERA_B, codigo, 3);
                }

                contador++;
            }

            limpiarResaltado();
            actualizarUbicaciones();
            actualizarApartamentosDisponibles();

            lblResultado.setText(
                    "Escenario de prueba cargado. "
                            + "Se agregaron los apartamentos existentes "
                            + "y los puntos de servicio."
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private List<String> obtenerCodigosApartamentos() {

        List<String> codigos = new ArrayList<>();

        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            codigos.add(apartamento.getCodigo());
        }

        return codigos;
    }

    private void agregarSiNoExiste(String codigo) {

        if (!estaRegistrado(codigo)) {
            rutaServicio.agregarUbicacion(codigo);
        }
    }

    private void conectarSiEsPosible(
            String origen,
            String destino,
            double distancia) {

        if (origen == null || destino == null || origen.equals(destino)) {
            return;
        }

        boolean existe = false;

        for (AristaGrafo<String> arista :
                rutaServicio.obtenerConexionesDesde(origen)) {

            if (arista.getDestino().getDato().equals(destino)) {
                existe = true;
                break;
            }
        }

        if (!existe) {
            rutaServicio.conectarUbicaciones(
                    origen,
                    destino,
                    distancia
            );
        }
    }

    /*
     * CONEXIONES
     */

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

            if (origen.equals(destino)) {
                mostrarInformacion(
                        "No puedes conectar una ubicación consigo misma."
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

            if (distancia <= 0) {
                mostrarInformacion(
                        "La distancia debe ser mayor que cero."
                );
                return;
            }

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
                            + " (" + distancia + " metros)"
            );

        } catch (NumberFormatException ex) {
            mostrarError(
                    "La distancia debe ser un número válido."
            );
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /*
     * DIJKSTRA
     */

    @FXML
    private void consultarRuta() {

        try {

            String origen = cbOrigen.getValue();
            String destino = cbDestino.getValue();

            if (origen == null || destino == null) {
                mostrarInformacion(
                        "Selecciona el origen y el destino en la sección de conexiones."
                );
                return;
            }

            ListaDoble<String> ruta =
                    rutaServicio.obtenerRutaMasCorta(origen, destino);

            limpiarResaltado();

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
                            + distancia + " metros"
            );

        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /*
     * BFS Y DFS
     */

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
                            + String.join(
                            " → ",
                            lvResultadoRuta.getItems()
                    )
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

    /*
     * ACTUALIZAR INTERFAZ
     */

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

    /*
     * DIBUJO DEL GRAFO
     */

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
        double alto = Math.max(panelGrafo.getHeight(), 480);

        panelGrafo.setPrefSize(ancho, alto);

        if (ubicaciones.isEmpty()) {

            Text mensaje = new Text(
                    30, 45,
                    "No hay ubicaciones registradas."
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
        double inicioY = 65;
        double separacionY = 105;

        posiciones.put(
                RECEPCION,
                new double[]{centroX, inicioY}
        );

        posiciones.put(
                PASILLO_CENTRAL,
                new double[]{centroX, inicioY + separacionY}
        );

        posiciones.put(
                ESCALERA_A,
                new double[]{centroX - 250, inicioY + separacionY * 2}
        );

        posiciones.put(
                ESCALERA_B,
                new double[]{centroX + 250, inicioY + separacionY * 2}
        );

        posiciones.put(
                ASEO_A,
                new double[]{centroX - 390, inicioY + separacionY}
        );

        posiciones.put(
                ASEO_B,
                new double[]{centroX + 390, inicioY + separacionY}
        );

        int indiceA = 0;
        int indiceB = 0;

        for (String codigo : ubicaciones) {

            if (posiciones.containsKey(codigo)) {
                continue;
            }

            boolean grupoA = indiceA <= indiceB;

            if (grupoA) {

                posiciones.put(
                        codigo,
                        new double[]{
                                centroX - 250 + (indiceA % 3) * 100,
                                inicioY + separacionY * 3
                                        + (indiceA / 3) * 100
                        }
                );

                indiceA++;

            } else {

                posiciones.put(
                        codigo,
                        new double[]{
                                centroX + 50 + (indiceB % 3) * 100,
                                inicioY + separacionY * 3
                                        + (indiceB / 3) * 100
                        }
                );

                indiceB++;
            }
        }

        return posiciones;
    }

    private void dibujarConexiones(
            List<String> ubicaciones,
            Map<String, double[]> posiciones) {

        for (String origen : ubicaciones) {

            for (AristaGrafo<String> arista :
                    rutaServicio.obtenerConexionesDesde(origen)) {

                String destino = arista.getDestino().getDato();

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
                                ? Color.web("#F59E0B")
                                : Color.web("#9AA7B2")
                );

                linea.setStrokeWidth(
                        perteneceRuta ? 5 : 2
                );

                panelGrafo.getChildren().add(linea);

                Text peso = new Text(
                        (puntoOrigen[0] + puntoDestino[0]) / 2 + 5,
                        (puntoOrigen[1] + puntoDestino[1]) / 2 - 5,
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

            Color color;

            if (resaltado) {
                color = Color.web("#F59E0B");
            } else if (codigo.equals(RECEPCION)) {
                color = Color.web("#16845B");
            } else if (codigo.startsWith("ESCALERA")) {
                color = Color.web("#7C3AED");
            } else if (codigo.startsWith("ASEO")) {
                color = Color.web("#0891B2");
            } else if (codigo.equals(PASILLO_CENTRAL)) {
                color = Color.web("#64748B");
            } else {
                color = Color.web("#2878A8");
            }

            Circle nodo = new Circle(
                    posicion[0],
                    posicion[1],
                    27
            );

            nodo.setFill(color);
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

    /*
     * MENSAJES
     */

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Información");
        alerta.setHeaderText("Mapa y rutas");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No fue posible realizar la operación"
        );

        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
