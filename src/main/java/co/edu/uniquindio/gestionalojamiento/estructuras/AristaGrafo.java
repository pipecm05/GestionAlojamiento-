package co.edu.uniquindio.gestionalojamiento.estructuras;

public class AristaGrafo<T> {

    private final VerticeGrafo<T> destino;
    private final double peso;

    public AristaGrafo(VerticeGrafo<T> destino, double peso) {
        if (destino == null) {
            throw new IllegalArgumentException(
                    "El destino es obligatorio.");
        }

        if (!Double.isFinite(peso) || peso <= 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser positivo y finito.");
        }

        this.destino = destino;
        this.peso = peso;
    }

    public VerticeGrafo<T> getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }
}
