
package co.edu.uniquindio.gestionalojamiento.controladores;

import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;

public class FavoritosController {

    @FXML
    private ComboBox<Huesped> cmbHuesped;

    @FXML
    private ComboBox<Apartamento> cmbApartamento;

    @FXML
    private TableView<Favorito> tablaFavoritos;

    @FXML
    private TableColumn<Favorito, String> colDocumento;

    @FXML
    private TableColumn<Favorito, String> colHuesped;

    @FXML
    private TableColumn<Favorito, String> colCodigo;

    @FXML
    private TableColumn<Favorito, String> colApartamento;

    @FXML
    private TableColumn<Favorito, String> colFecha;

    @FXML
    private Label lblCantidad;

    private CatalogoAlojamiento catalogo;
    private FavoritoServicio favoritoServicio;

    private final ObservableList<Favorito> listaFavoritos =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        colDocumento.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getHuesped().getDocumento()
                )
        );

        colHuesped.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getHuesped().getNombreCompleto()
                )
        );

        colCodigo.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getAlojamiento().getCodigo()
                )
        );

        colApartamento.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getAlojamiento().getNombre()
                )
        );

        colFecha.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getFechaAgregado().format(formatoFecha)
                )
        );

        tablaFavoritos.setItems(listaFavoritos);

        configurarConversores();
    }

    public void configurar(
            CatalogoAlojamiento catalogo,
            FavoritoServicio favoritoServicio
    ) {

        if (catalogo == null || favoritoServicio == null) {
            throw new IllegalArgumentException(
                    "El catálogo y el servicio de favoritos son obligatorios."
            );
        }

        this.catalogo = catalogo;
        this.favoritoServicio = favoritoServicio;

        cargarHuespedes();
        cargarApartamentos();
    }

    private void configurarConversores() {

        cmbHuesped.setConverter(new StringConverter<>() {

            @Override
            public String toString(Huesped huesped) {
                if (huesped == null) {
                    return "";
                }

                return huesped.getDocumento()
                        + " - "
                        + huesped.getNombreCompleto();
            }

            @Override
            public Huesped fromString(String texto) {
                return null;
            }
        });

        cmbApartamento.setConverter(new StringConverter<>() {

            @Override
            public String toString(Apartamento apartamento) {
                if (apartamento == null) {
                    return "";
                }

                return apartamento.getCodigo()
                        + " - "
                        + apartamento.getNombre();
            }

            @Override
            public Apartamento fromString(String texto) {
                return null;
            }
        });
    }

    private void cargarHuespedes() {

        cmbHuesped.getItems().clear();

        for (Huesped huesped : catalogo.listarHuespedes()) {
            cmbHuesped.getItems().add(huesped);
        }
    }

    private void cargarApartamentos() {

        cmbApartamento.getItems().clear();

        for (Apartamento apartamento : catalogo.listarApartamentos()) {
            cmbApartamento.getItems().add(apartamento);
        }
    }

    @FXML
    private void agregarFavorito() {

        Huesped huesped = cmbHuesped.getValue();
        Apartamento apartamento = cmbApartamento.getValue();

        if (huesped == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un huésped",
                    "Debes seleccionar el huésped al que deseas agregar el favorito."
            );
            return;
        }

        if (apartamento == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un apartamento",
                    "Debes seleccionar el apartamento que deseas guardar."
            );
            return;
        }

        try {

            boolean agregado = favoritoServicio.agregarFavorito(
                    huesped,
                    apartamento
            );

            if (agregado) {

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Favorito agregado",
                        "El apartamento se agregó correctamente a los favoritos."
                );

                cmbApartamento.getSelectionModel().clearSelection();
                cargarFavoritos();

            } else {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Favorito existente",
                        "Este apartamento ya está guardado en los favoritos del huésped."
                );
            }

        } catch (RuntimeException ex) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al agregar favorito",
                    ex.getMessage()
            );
        }
    }

    @FXML
    private void consultarFavoritos() {

        if (cmbHuesped.getValue() == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un huésped",
                    "Debes seleccionar un huésped para consultar sus favoritos."
            );
            return;
        }

        cargarFavoritos();
    }

    private void cargarFavoritos() {

        Huesped huesped = cmbHuesped.getValue();

        if (huesped == null) {
            listaFavoritos.clear();
            actualizarCantidad();
            return;
        }

        listaFavoritos.clear();

        for (Favorito favorito :
                favoritoServicio.obtenerFavoritos(huesped.getDocumento())) {

            listaFavoritos.add(favorito);
        }

        actualizarCantidad();
    }

    @FXML
    private void eliminarFavorito() {

        Huesped huesped = cmbHuesped.getValue();
        Favorito seleccionado =
                tablaFavoritos.getSelectionModel().getSelectedItem();

        if (huesped == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un huésped",
                    "Debes seleccionar un huésped."
            );
            return;
        }

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un favorito",
                    "Selecciona un favorito de la tabla para eliminarlo."
            );
            return;
        }

        boolean eliminado = favoritoServicio.eliminarFavorito(
                huesped.getDocumento(),
                seleccionado.getAlojamiento().getCodigo()
        );

        if (eliminado) {

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Favorito eliminado",
                    "El favorito se eliminó correctamente."
            );

            cargarFavoritos();

        } else {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo eliminar",
                    "No fue posible eliminar el favorito seleccionado."
            );
        }
    }

    @FXML
    private void limpiarSeleccion() {

        cmbHuesped.getSelectionModel().clearSelection();
        cmbApartamento.getSelectionModel().clearSelection();

        listaFavoritos.clear();
        actualizarCantidad();
    }

    private void actualizarCantidad() {

        lblCantidad.setText(
                "Total de favoritos: " + listaFavoritos.size()
        );
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(
                mensaje == null ? "Ha ocurrido un error." : mensaje
        );
        alerta.showAndWait();
    }
}
