package co.edu.uniquindio.gestionalojamiento.estructuras;
import java.util.Iterator;
import java.util.Comparator;

public class ArbolBinarioBusqueda<T extends Comparable<T>>
        implements Iterable<T> {

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


    public int altura() {
        return alturaRecursiva(raiz);
    }

    private int alturaRecursiva(NodoArbol<T> actual) {
        if (actual == null) {
            return -1;
        }

        int alturaIzquierda =
                alturaRecursiva(actual.getIzquierdo());

        int alturaDerecha =
                alturaRecursiva(actual.getDerecho());

        return 1 + Math.max(alturaIzquierda, alturaDerecha);
    }


    public ListaDoble<T> inorden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void inordenRecursivo(
            NodoArbol<T> actual, ListaDoble<T> resultado) {

        if (actual == null) {
            return;
        }

        inordenRecursivo(actual.getIzquierdo(), resultado);
        resultado.agregar(actual.getDato());
        inordenRecursivo(actual.getDerecho(), resultado);
    }

    public ListaDoble<T> preorden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        preordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void preordenRecursivo(
            NodoArbol<T> actual, ListaDoble<T> resultado) {

        if (actual == null) {
            return;
        }

        resultado.agregar(actual.getDato());
        preordenRecursivo(actual.getIzquierdo(), resultado);
        preordenRecursivo(actual.getDerecho(), resultado);
    }

    public ListaDoble<T> postorden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        postordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void postordenRecursivo(
            NodoArbol<T> actual, ListaDoble<T> resultado) {

        if (actual == null) {
            return;
        }

        postordenRecursivo(actual.getIzquierdo(), resultado);
        postordenRecursivo(actual.getDerecho(), resultado);
        resultado.agregar(actual.getDato());
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorArbol<>(inorden());
    }


    public <K> T buscarPorClave(
            K clave,
            Comparator<? super K> comparadorClaves,
            java.util.function.Function<? super T, ? extends K> obtenerClave) {

        if (clave == null || comparadorClaves == null
                || obtenerClave == null) {
            throw new IllegalArgumentException(
                    "La clave y los criterios de búsqueda son obligatorios."
            );
        }

        return buscarPorClaveRecursivo(
                raiz, clave, comparadorClaves, obtenerClave
        );
    }

    private <K> T buscarPorClaveRecursivo(
            NodoArbol<T> actual,
            K clave,
            Comparator<? super K> comparadorClaves,
            java.util.function.Function<? super T, ? extends K> obtenerClave) {

        if (actual == null) {
            return null;
        }

        int comparacion = comparadorClaves.compare(
                clave, obtenerClave.apply(actual.getDato())
        );

        if (comparacion == 0) {
            return actual.getDato();
        }

        if (comparacion < 0) {
            return buscarPorClaveRecursivo(
                    actual.getIzquierdo(),
                    clave,
                    comparadorClaves,
                    obtenerClave
            );
        }

        return buscarPorClaveRecursivo(
                actual.getDerecho(),
                clave,
                comparadorClaves,
                obtenerClave
        );
    }


}
