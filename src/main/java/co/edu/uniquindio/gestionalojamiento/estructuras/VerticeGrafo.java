package co.edu.uniquindio.gestionalojamiento.estructuras;

public class VerticeGrafo<T> {

    private final T dato;
    private final ListaDoble<AristaGrafo<T>> adyacentes;

    public VerticeGrafo(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "El dato del vértice es obligatorio.");
        }

        this.dato = dato;
        this.adyacentes = new ListaDoble<>();
    }

    public T getDato() {
        return dato;
    }

    public Iterable<AristaGrafo<T>> getAdyacentes() {
        return adyacentes;
    }

    void agregarAdyacente(
            VerticeGrafo<T> destino, double peso) {

        adyacentes.agregar(
                new AristaGrafo<>(destino, peso));
    }
}
