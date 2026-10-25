
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteradorCola<T> implements Iterator<T> {

    private Nodo<T> actual;

    public IteradorCola(Nodo<T> primero) {
        this.actual = primero;
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException(
                    "No hay más elementos en la cola."
            );
        }

        T dato = actual.getDato();
        actual = actual.getSiguiente();

        return dato;
    }
}
