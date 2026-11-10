package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;
import java.util.Objects;

public class Conjunto<T> implements Iterable<T> {

    private final ListaDoble<T> elementos;

    public Conjunto() {
        elementos = new ListaDoble<>();
    }

    public boolean estaVacio() {
        return elementos.estaVacia();
    }

    public int getTamanio() {
        return elementos.getTamanio();
    }

    public boolean agregar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos."
            );
        }

        if (contiene(dato)) {
            return false;
        }

        elementos.agregar(dato);
        return true;
    }

    public boolean contiene(T dato) {
        if (dato == null) {
            return false;
        }

        return elementos.contiene(dato);
    }

    public boolean eliminar(T dato) {
        if (dato == null) {
            return false;
        }

        return elementos.eliminar(dato);
    }

    public void vaciar() {
        elementos.vaciar();
    }

    public Conjunto<T> union(Conjunto<T> otro) {
        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : elementos) {
            resultado.agregar(elemento);
        }

        for (T elemento : otro) {
            resultado.agregar(elemento);
        }

        return resultado;
    }

    public Conjunto<T> interseccion(Conjunto<T> otro) {
        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : elementos) {
            if (otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    public Conjunto<T> diferencia(Conjunto<T> otro) {
        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : elementos) {
            if (!otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    public boolean esSubconjuntoDe(Conjunto<T> otro) {
        validarConjunto(otro);

        for (T elemento : elementos) {
            if (!otro.contiene(elemento)) {
                return false;
            }
        }

        return true;
    }

    private void validarConjunto(Conjunto<T> otro) {
        if (otro == null) {
            throw new IllegalArgumentException(
                    "El conjunto no puede ser nulo."
            );
        }
    }

    @Override
    public Iterator<T> iterator() {
        return elementos.iterator();
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("{");
        Iterator<T> iterador = iterator();

        while (iterador.hasNext()) {
            resultado.append(iterador.next());

            if (iterador.hasNext()) {
                resultado.append(", ");
            }
        }

        resultado.append("}");
        return resultado.toString();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof Conjunto<?> otro)) {
            return false;
        }

        if (getTamanio() != otro.getTamanio()) {
            return false;
        }

        for (T elemento : elementos) {
            boolean encontrado = false;

            for (Object otroElemento : otro) {
                if (Objects.equals(elemento, otroElemento)) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int resultado = 0;

        for (T elemento : elementos) {
            resultado += Objects.hashCode(elemento);
        }

        return resultado;
    }
}
