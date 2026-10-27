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


    public boolean eliminar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se permiten valores nulos."
            );
        }

        if (buscar(dato) == null) {
            return false;
        }

        raiz = eliminarRecursivo(raiz, dato);
        tamanio--;
        return true;
    }

    private NodoArbol<T> eliminarRecursivo(
            NodoArbol<T> actual, T dato) {

        if (actual == null) {
            return null;
        }

        int comparacion = dato.compareTo(actual.getDato());

        if (comparacion < 0) {
            actual.setIzquierdo(
                    eliminarRecursivo(actual.getIzquierdo(), dato)
            );

        } else if (comparacion > 0) {
            actual.setDerecho(
                    eliminarRecursivo(actual.getDerecho(), dato)
            );

        } else {
            // Caso 1 y caso 2: cero o un hijo.
            if (actual.getIzquierdo() == null) {
                return actual.getDerecho();
            }

            if (actual.getDerecho() == null) {
                return actual.getIzquierdo();
            }

            // Caso 3: dos hijos.
            NodoArbol<T> sucesor = encontrarMinimo(
                    actual.getDerecho()
            );

            actual.setDato(sucesor.getDato());

            actual.setDerecho(
                    eliminarRecursivo(
                            actual.getDerecho(),
                            sucesor.getDato()
                    )
            );
        }

        return actual;
    }

    private NodoArbol<T> encontrarMinimo(NodoArbol<T> actual) {
        if (actual.getIzquierdo() == null) {
            return actual;
        }

        return encontrarMinimo(actual.getIzquierdo());
    }

}
