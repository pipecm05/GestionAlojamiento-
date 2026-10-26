
package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class ColaPrioridad<T> {

    private T[] elementos;
    private int tamanio;
    private Comparator<T> comparador;

    @SuppressWarnings("unchecked")
    public ColaPrioridad(int capacidadInicial, Comparator<T> comparador) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero."
            );
        }

        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El comparador es obligatorio."
            );
        }

        elementos = (T[]) new Object[capacidadInicial];
        tamanio = 0;
        this.comparador = comparador;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void insertar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException(
                    "No se pueden insertar elementos nulos."
            );
        }

        asegurarCapacidad();

        elementos[tamanio] = elemento;
        subir(tamanio);
        tamanio++;
    }

    public T consultarPrioridad() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola de prioridad está vacía."
            );
        }

        return elementos[0];
    }

    public T extraerPrioridad() {
        if (estaVacia()) {
            throw new NoSuchElementException(
                    "No hay elementos para extraer."
            );
        }

        T resultado = elementos[0];

        tamanio--;

        if (tamanio > 0) {
            elementos[0] = elementos[tamanio];
            bajar(0);
        }

        elementos[tamanio] = null;

        return resultado;
    }

    private void subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;

            if (comparador.compare(
                    elementos[indice],
                    elementos[padre]
            ) >= 0) {
                break;
            }

            intercambiar(indice, padre);
            indice = padre;
        }
    }

    private void bajar(int indice) {
        while (true) {
            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int mejor = indice;

            if (izquierdo < tamanio
                    && comparador.compare(
                    elementos[izquierdo],
                    elementos[mejor]
            ) < 0) {
                mejor = izquierdo;
            }

            if (derecho < tamanio
                    && comparador.compare(
                    elementos[derecho],
                    elementos[mejor]
            ) < 0) {
                mejor = derecho;
            }

            if (mejor == indice) {
                break;
            }

            intercambiar(indice, mejor);
            indice = mejor;
        }
    }

    private void intercambiar(int primero, int segundo) {
        T temporal = elementos[primero];
        elementos[primero] = elementos[segundo];
        elementos[segundo] = temporal;
    }

    @SuppressWarnings("unchecked")
    private void asegurarCapacidad() {
        if (tamanio == elementos.length) {
            T[] nuevos = (T[]) new Object[elementos.length * 2];

            for (int i = 0; i < tamanio; i++) {
                nuevos[i] = elementos[i];
            }

            elementos = nuevos;
        }
    }
}
