package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Pila<T> implements Iterable<T> {

    private Nodo<T> cima;
    private int tamanio;

    public Pila() {
        cima = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void apilar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden apilar elementos nulos."
            );
        }

        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cima);

        if (cima != null) {
            cima.setAnterior(nuevo);
        }

        cima = nuevo;
        tamanio++;
    }

    public T consultarCima() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La pila está vacía."
            );
        }

        return cima.getDato();
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "No se puede desapilar una pila vacía."
            );
        }

        T dato = cima.getDato();
        cima = cima.getSiguiente();

        if (cima != null) {
            cima.setAnterior(null);
        }

        tamanio--;
        return dato;
    }

    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    /**
     * Recorre la pila desde la cima hasta el fondo, respetando
     * el orden LIFO sin modificar la pila original.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Nodo<T> actual = cima;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "No hay más elementos en la pila."
                    );
                }

                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
