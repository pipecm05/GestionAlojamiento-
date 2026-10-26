
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.NoSuchElementException;

public class Pila<T> {

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
}
