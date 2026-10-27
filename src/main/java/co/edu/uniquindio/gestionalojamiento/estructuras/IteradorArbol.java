package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteradorArbol<T> implements Iterator<T> {

    private final Iterator<T> iterador;

    public IteradorArbol(ListaDoble<T> elementos) {
        this.iterador = elementos.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterador.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException(
                    "No hay más elementos en el árbol."
            );
        }

        return iterador.next();
    }
}
