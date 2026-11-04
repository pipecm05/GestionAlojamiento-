package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.Grafo;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;

public class RutaServicio {

    private final Grafo<String> grafo;

    public RutaServicio() {
        grafo = new Grafo<>();
    }

    public boolean agregarUbicacion(String codigo) {
        validarCodigo(codigo);
        return grafo.agregarVertice(codigo);
    }

    public boolean agregarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        return agregarUbicacion(apartamento.getCodigo());
    }

    public boolean conectarUbicaciones(
            String origen, String destino, double distancia) {

        validarCodigo(origen);
        validarCodigo(destino);

        return grafo.agregarArista(origen, destino, distancia);
    }

    public ListaDoble<String> obtenerRutaMasCorta(
            String origen, String destino) {

        validarCodigo(origen);
        validarCodigo(destino);

        return grafo.rutaMasCorta(origen, destino);
    }

    public double obtenerDistanciaMinima(
            String origen, String destino) {

        validarCodigo(origen);
        validarCodigo(destino);

        return grafo.distanciaMinima(origen, destino);
    }

    public ListaDoble<String> recorrerBfs(String origen) {
        validarCodigo(origen);
        return grafo.bfs(origen);
    }

    public ListaDoble<String> recorrerDfs(String origen) {
        validarCodigo(origen);
        return grafo.dfs(origen);
    }

    public int cantidadUbicaciones() {
        return grafo.cantidadVertices();
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de ubicación es obligatorio.");
        }
    }
}
