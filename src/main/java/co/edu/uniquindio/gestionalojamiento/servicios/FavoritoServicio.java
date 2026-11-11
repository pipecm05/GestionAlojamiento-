package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.Conjunto;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Alojamiento;
import co.edu.uniquindio.gestionalojamiento.modelo.Favorito;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;

/**
 * Gestiona los alojamientos favoritos de cada huésped.
 *
 * La lista conserva los objetos Favorito y su orden de registro. El conjunto
 * mantiene las claves huésped-alojamiento para consultar y evitar duplicados
 * mediante una estructura de conjunto propia del proyecto.
 */
public class FavoritoServicio {

    private final ListaDoble<Favorito> favoritos;
    private final Conjunto<ClaveFavorito> clavesFavoritos;

    public FavoritoServicio() {
        favoritos = new ListaDoble<>();
        clavesFavoritos = new Conjunto<>();
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

        ClaveFavorito clave = crearClave(
                huesped.getDocumento(),
                alojamiento.getCodigo()
        );

        if (!clavesFavoritos.agregar(clave)) {
            return false;
        }

        favoritos.agregar(new Favorito(huesped, alojamiento));
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
     * Comprueba si un alojamiento está guardado como favorito por un huésped.
     */
    public boolean esFavorito(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
        return clavesFavoritos.contiene(
                crearClave(documentoHuesped, codigoAlojamiento)
        );
    }

    /**
     * Elimina un alojamiento de los favoritos de un huésped.
     */
    public boolean eliminarFavorito(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
        ClaveFavorito clave = crearClave(
                documentoHuesped,
                codigoAlojamiento
        );

        if (!clavesFavoritos.contiene(clave)) {
            return false;
        }

        for (Favorito favorito : favoritos) {
            boolean mismoHuesped = favorito.getHuesped().getDocumento()
                    .equals(documentoHuesped);

            boolean mismoAlojamiento = favorito.getAlojamiento().getCodigo()
                    .equals(codigoAlojamiento);

            if (mismoHuesped && mismoAlojamiento) {
                boolean eliminado = favoritos.eliminar(favorito);

                if (eliminado) {
                    clavesFavoritos.eliminar(clave);
                }

                return eliminado;
            }
        }

        // Mantiene sincronizado el índice si no se encuentra el objeto asociado.
        clavesFavoritos.eliminar(clave);
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
     * Consulta si existen favoritos registrados para un huésped.
     */
    public boolean tieneFavoritos(String documentoHuesped) {
        return !obtenerFavoritos(documentoHuesped).estaVacia();
    }

    private ClaveFavorito crearClave(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
        validarDocumento(documentoHuesped);

        if (codigoAlojamiento == null || codigoAlojamiento.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del alojamiento es obligatorio."
            );
        }

        return new ClaveFavorito(documentoHuesped, codigoAlojamiento);
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

    /** Clave compuesta que identifica un favorito sin mezclar huéspedes. */
    private record ClaveFavorito(
            String documentoHuesped,
            String codigoAlojamiento
    ) {
    }
}
