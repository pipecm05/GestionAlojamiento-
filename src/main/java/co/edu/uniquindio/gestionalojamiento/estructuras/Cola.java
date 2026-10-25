
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Cola<T> implements Iterable<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public Cola() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden encolar elementos nulos."
            );
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            nuevo.setAnterior(ultimo);
            ultimo = nuevo;
        }

        tamanio++;
    }

    public T consultarPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola está vacía."
            );
        }

        return primero.getDato();
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "No se puede desencolar una cola vacía."
            );
        }

        T dato = primero.getDato();
        primero = primero.getSiguiente();

        if (primero == null) {
            ultimo = null;
        } else {
            primero.setAnterior(null);
        }

        tamanio--;
        return dato;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorCola<>(primero);
    }
}
