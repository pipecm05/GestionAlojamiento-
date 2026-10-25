
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteradorListaDoble<T> implements Iterator<T> {

    private Nodo<T> actual;

    public IteradorListaDoble(Nodo<T> primero) {
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
                    "No hay más elementos en la lista."
            );
        }

        T dato = actual.getDato();
        actual = actual.getSiguiente();

        return dato;
    }
}
