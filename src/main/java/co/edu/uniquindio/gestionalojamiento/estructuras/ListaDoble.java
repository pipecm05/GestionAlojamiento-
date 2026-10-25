
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaDoble<T> implements Iterable<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public ListaDoble() {
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

    public void agregar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos."
            );
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.setAnterior(ultimo);
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        tamanio++;
    }

    public void agregarAlInicio(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos."
            );
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.setSiguiente(primero);
            primero.setAnterior(nuevo);
            primero = nuevo;
        }

        tamanio++;
    }

    public T obtenerPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La lista está vacía."
            );
        }

        return primero.getDato();
    }

    public T obtenerUltimo() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La lista está vacía."
            );
        }

        return ultimo.getDato();
    }

    public boolean contiene(T dato) {
        Nodo<T> actual = primero;

        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    public boolean eliminar(T dato) {
        Nodo<T> actual = primero;

        while (actual != null) {
            if (actual.getDato().equals(dato)) {

                if (actual == primero) {
                    primero = actual.getSiguiente();
                } else {
                    actual.getAnterior()
                            .setSiguiente(actual.getSiguiente());
                }

                if (actual == ultimo) {
                    ultimo = actual.getAnterior();
                } else {
                    actual.getSiguiente()
                            .setAnterior(actual.getAnterior());
                }

                tamanio--;
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    public void vaciar() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Nodo<T> actual = primero;

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
        };
    }
}
