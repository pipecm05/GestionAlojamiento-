package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Alojamiento;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;

public class FavoritoServicio {

    private final ListaDoble<Favorito> favoritos;

    public FavoritoServicio() {
        favoritos = new ListaDoble<>();
    }

    /**
     * Agrega un alojamiento a los favoritos de un huésped.
     */
    public boolean agregarFavorito(
            Huesped huesped,
            Alojamiento alojamiento
    ) {
        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped es obligatorio."
            );
        }

        if (alojamiento == null) {
            throw new IllegalArgumentException(
                    "El alojamiento es obligatorio."
            );
        }

        if (esFavorito(huesped.getDocumento(),
                alojamiento.getCodigo())) {
            return false;
        }

        Favorito favorito = new Favorito(huesped, alojamiento);
        favoritos.agregar(favorito);

        return true;
    }

    /**
     * Consulta los favoritos de un huésped.
     */
    public ListaDoble<Favorito> obtenerFavoritos(
            String documentoHuesped
    ) {
        validarDocumento(documentoHuesped);

        ListaDoble<Favorito> resultado = new ListaDoble<>();

        for (Favorito favorito : favoritos) {
            if (favorito.getHuesped().getDocumento()
                    .equals(documentoHuesped)) {
                resultado.agregar(favorito);
            }
        }

        return resultado;
    }

    /**
     * Comprueba si un alojamiento está guardado
     * como favorito por un huésped.
     */
    public boolean esFavorito(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
        validarDocumento(documentoHuesped);

        if (codigoAlojamiento == null
                || codigoAlojamiento.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del alojamiento es obligatorio."
            );
        }

        for (Favorito favorito : favoritos) {
            boolean mismoHuesped =
                    favorito.getHuesped().getDocumento()
                            .equals(documentoHuesped);

            boolean mismoAlojamiento =
                    favorito.getAlojamiento().getCodigo()
                            .equals(codigoAlojamiento);

            if (mismoHuesped && mismoAlojamiento) {
                return true;
            }
        }

        return false;
    }

    /**
     * Elimina un alojamiento de los favoritos
     * de un huésped.
     */
    public boolean eliminarFavorito(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
        validarDocumento(documentoHuesped);

        if (codigoAlojamiento == null
                || codigoAlojamiento.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del alojamiento es obligatorio."
            );
        }

        for (Favorito favorito : favoritos) {
            boolean mismoHuesped =
                    favorito.getHuesped().getDocumento()
                            .equals(documentoHuesped);

            boolean mismoAlojamiento =
                    favorito.getAlojamiento().getCodigo()
                            .equals(codigoAlojamiento);

            if (mismoHuesped && mismoAlojamiento) {
                return favoritos.eliminar(favorito);
            }
        }

        return false;
    }

    /**
     * Cuenta los alojamientos favoritos de un huésped.
     */
    public int contarFavoritos(String documentoHuesped) {
        return obtenerFavoritos(documentoHuesped).getTamanio();
    }

    /**
     * Consulta la cantidad total de favoritos registrados.
     */
    public int cantidadTotalFavoritos() {
        return favoritos.getTamanio();
    }

    /**
     * Consulta si existen favoritos registrados.
     */
    public boolean tieneFavoritos(String documentoHuesped) {
        return !obtenerFavoritos(documentoHuesped).estaVacia();
    }

    /**
     * Valida el documento del huésped.
     */
    private void validarDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento del huésped es obligatorio."
            );
        }
    }
}
