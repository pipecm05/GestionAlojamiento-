package co.edu.uniquindio.gestionalojamiento.estructuras;

public class ArbolBinarioBusqueda<T extends Comparable<T>> {

    private NodoArbol<T> raiz;
    private int tamanio;

    public ArbolBinarioBusqueda() {
        raiz = null;
        tamanio = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean insertar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se permiten elementos nulos."
            );
        }

        if (buscar(dato) != null) {
            return false;
        }

        raiz = insertarRecursivo(raiz, dato);
        tamanio++;
        return true;
    }

    private NodoArbol<T> insertarRecursivo(
            NodoArbol<T> actual, T dato) {

        if (actual == null) {
            return new NodoArbol<>(dato);
        }

        int comparacion = dato.compareTo(actual.getDato());

        if (comparacion < 0) {
            actual.setIzquierdo(
                    insertarRecursivo(actual.getIzquierdo(), dato)
            );
        } else if (comparacion > 0) {
            actual.setDerecho(
                    insertarRecursivo(actual.getDerecho(), dato)
            );
        }

        return actual;
    }

    public T buscar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se permiten búsquedas de valores nulos."
            );
        }

        return buscarRecursivo(raiz, dato);
    }

    private T buscarRecursivo(NodoArbol<T> actual, T dato) {
        if (actual == null) {
            return null;
        }

        int comparacion = dato.compareTo(actual.getDato());

        if (comparacion == 0) {
            return actual.getDato();
        }

        if (comparacion < 0) {
            return buscarRecursivo(actual.getIzquierdo(), dato);
        }

        return buscarRecursivo(actual.getDerecho(), dato);
    }
}
