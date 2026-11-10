
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ListaCircular<T> implements Iterable<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public ListaCircular() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int getTamanio() {
        return tamanio;
    }

    // Agrega un elemento al final.
    public void agregar(T dato) {
        validarDato(dato);

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
            nuevo.setSiguiente(primero);
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
            ultimo.setSiguiente(primero);
        }

        tamanio++;
    }

    // Agrega un elemento al inicio.
    public void agregarAlInicio(T dato) {
        validarDato(dato);

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
            nuevo.setSiguiente(primero);
        } else {
            nuevo.setSiguiente(primero);
            primero = nuevo;
            ultimo.setSiguiente(primero);
        }

        tamanio++;
    }

    // Consulta el primer elemento.
    public T obtenerPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La lista circular está vacía."
            );
        }

        return primero.getDato();
    }

    // Consulta el último elemento.
    public T obtenerUltimo() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La lista circular está vacía."
            );
        }

        return ultimo.getDato();
    }

    // Busca un elemento.
    public boolean contiene(T dato) {
        if (estaVacia()) {
            return false;
        }

        Nodo<T> actual = primero;

        for (int i = 0; i < tamanio; i++) {
            if (Objects.equals(actual.getDato(), dato)) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    // Elimina la primera aparición de un elemento.
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }

        Nodo<T> actual = primero;
        Nodo<T> anterior = ultimo;

        for (int i = 0; i < tamanio; i++) {

            if (Objects.equals(actual.getDato(), dato)) {

                if (tamanio == 1) {
                    primero = null;
                    ultimo = null;
                } else {
                    anterior.setSiguiente(actual.getSiguiente());

                    if (actual == primero) {
                        primero = actual.getSiguiente();
                    }

                    if (actual == ultimo) {
                        ultimo = anterior;
                    }

                    ultimo.setSiguiente(primero);
                }

                tamanio--;
                return true;
            }

            anterior = actual;
            actual = actual.getSiguiente();
        }

        return false;
    }

    // Elimina y devuelve el primer elemento.
    public T eliminarPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "No se puede eliminar de una lista vacía."
            );
        }

        T dato = primero.getDato();

        if (tamanio == 1) {
            primero = null;
            ultimo = null;
        } else {
            primero = primero.getSiguiente();
            ultimo.setSiguiente(primero);
        }

        tamanio--;
        return dato;
    }

    // Elimina todos los elementos.
    public void vaciar() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    private void validarDato(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos."
            );
        }
    }

    // Permite recorrer la lista sin entrar en un ciclo infinito.
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private Nodo<T> actual = primero;
            private int elementosRecorridos = 0;

            @Override
            public boolean hasNext() {
                return elementosRecorridos < tamanio;
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
                elementosRecorridos++;

                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("[");
        int posicion = 0;

        for (T dato : this) {
            resultado.append(dato);

            if (posicion < tamanio - 1) {
                resultado.append(", ");
            }

            posicion++;
        }

        resultado.append("]");
        return resultado.toString();
    }
}
