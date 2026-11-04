package co.edu.uniquindio.gestionalojamiento.estructuras;

import java.util.Iterator;

public class Grafo<T> implements Iterable<T> {

    private final ListaDoble<VerticeGrafo<T>> vertices;

    public Grafo() {
        vertices = new ListaDoble<>();
    }

    public boolean agregarVertice(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "El vértice no puede ser nulo.");
        }

        if (buscarVertice(dato) != null) {
            return false;
        }

        vertices.agregar(new VerticeGrafo<>(dato));
        return true;
    }

    private VerticeGrafo<T> buscarVertice(T dato) {
        if (dato == null) {
            return null;
        }

        for (VerticeGrafo<T> vertice : vertices) {
            if (vertice.getDato().equals(dato)) {
                return vertice;
            }
        }

        return null;
    }

    public boolean contieneVertice(T dato) {
        return buscarVertice(dato) != null;
    }

    public boolean agregarArista(
            T origen, T destino, double peso) {

        if (!Double.isFinite(peso) || peso <= 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser positivo y finito.");
        }

        VerticeGrafo<T> verticeOrigen = buscarVertice(origen);
        VerticeGrafo<T> verticeDestino = buscarVertice(destino);

        if (verticeOrigen == null || verticeDestino == null) {
            throw new IllegalArgumentException(
                    "Ambos vértices deben existir.");
        }

        if (verticeOrigen == verticeDestino) {
            throw new IllegalArgumentException(
                    "No se permiten conexiones consigo mismo.");
        }

        if (existeArista(origen, destino)) {
            return false;
        }

        // Conexión bidireccional.
        verticeOrigen.agregarAdyacente(verticeDestino, peso);
        verticeDestino.agregarAdyacente(verticeOrigen, peso);

        return true;
    }

    public boolean existeArista(T origen, T destino) {

        VerticeGrafo<T> verticeOrigen = buscarVertice(origen);
        VerticeGrafo<T> verticeDestino = buscarVertice(destino);

        if (verticeOrigen == null || verticeDestino == null) {
            return false;
        }

        for (AristaGrafo<T> arista :
                verticeOrigen.getAdyacentes()) {

            if (arista.getDestino() == verticeDestino) {
                return true;
            }
        }

        return false;
    }

    public int cantidadVertices() {
        return vertices.getTamanio();
    }

    @Override
    public Iterator<T> iterator() {

        Iterator<VerticeGrafo<T>> iterador =
                vertices.iterator();

        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return iterador.hasNext();
            }

            @Override
            public T next() {
                return iterador.next().getDato();
            }
        };
    }


    public ListaDoble<T> bfs(T origen) {

        VerticeGrafo<T> inicio = buscarVertice(origen);

        if (inicio == null) {
            throw new IllegalArgumentException(
                    "El vértice de origen no existe.");
        }

        ListaDoble<T> recorrido = new ListaDoble<>();
        ListaDoble<VerticeGrafo<T>> visitados = new ListaDoble<>();
        Cola<VerticeGrafo<T>> pendientes = new Cola<>();

        // Marcar el origen y agregarlo a la cola.
        visitados.agregar(inicio);
        pendientes.encolar(inicio);

        while (!pendientes.estaVacia()) {

            // Procesar el primer vértice de la cola.
            VerticeGrafo<T> actual = pendientes.desencolar();

            recorrido.agregar(actual.getDato());

            // Explorar sus vecinos.
            for (AristaGrafo<T> arista : actual.getAdyacentes()) {

                VerticeGrafo<T> vecino = arista.getDestino();

                if (!fueVisitado(visitados, vecino)) {
                    visitados.agregar(vecino);
                    pendientes.encolar(vecino);
                }
            }
        }

        return recorrido;
    }

    private boolean fueVisitado(
            ListaDoble<VerticeGrafo<T>> visitados,
            VerticeGrafo<T> vertice) {

        for (VerticeGrafo<T> visitado : visitados) {
            if (visitado == vertice) {
                return true;
            }
        }

        return false;
    }


    public ListaDoble<T> dfs(T origen) {

        VerticeGrafo<T> inicio = buscarVertice(origen);

        if (inicio == null) {
            throw new IllegalArgumentException(
                    "El vértice de origen no existe.");
        }

        ListaDoble<T> recorrido = new ListaDoble<>();
        ListaDoble<VerticeGrafo<T>> visitados = new ListaDoble<>();

        dfsRecursivo(inicio, visitados, recorrido);

        return recorrido;
    }

    private void dfsRecursivo(
            VerticeGrafo<T> actual,
            ListaDoble<VerticeGrafo<T>> visitados,
            ListaDoble<T> recorrido) {

        visitados.agregar(actual);
        recorrido.agregar(actual.getDato());

        for (AristaGrafo<T> arista : actual.getAdyacentes()) {

            VerticeGrafo<T> vecino = arista.getDestino();

            if (!fueVisitado(visitados, vecino)) {
                dfsRecursivo(vecino, visitados, recorrido);
            }
        }
    }


    private class EstadoRuta {
        VerticeGrafo<T> vertice;
        double distancia = Double.POSITIVE_INFINITY;
        EstadoRuta anterior;
        boolean visitado;

        EstadoRuta(VerticeGrafo<T> vertice) {
            this.vertice = vertice;
        }
    }

    private EstadoRuta buscarEstado(
            ListaDoble<EstadoRuta> estados,
            VerticeGrafo<T> vertice) {

        for (EstadoRuta estado : estados) {
            if (estado.vertice == vertice) {
                return estado;
            }
        }

        return null;
    }

    private EstadoRuta menorNoVisitado(
            ListaDoble<EstadoRuta> estados) {

        EstadoRuta menor = null;

        for (EstadoRuta estado : estados) {
            if (!estado.visitado
                    && (menor == null
                    || estado.distancia < menor.distancia)) {
                menor = estado;
            }
        }

        return menor;
    }

    private EstadoRuta calcularDijkstra(T origen, T destino) {

        VerticeGrafo<T> inicio = buscarVertice(origen);
        VerticeGrafo<T> fin = buscarVertice(destino);

        if (inicio == null || fin == null) {
            throw new IllegalArgumentException(
                    "El origen y el destino deben existir.");
        }

        ListaDoble<EstadoRuta> estados = new ListaDoble<>();

        for (VerticeGrafo<T> vertice : vertices) {
            estados.agregar(new EstadoRuta(vertice));
        }

        EstadoRuta inicial = buscarEstado(estados, inicio);
        EstadoRuta objetivo = buscarEstado(estados, fin);

        inicial.distancia = 0;

        while (true) {

            EstadoRuta actual = menorNoVisitado(estados);

            if (actual == null
                    || Double.isInfinite(actual.distancia)) {
                break;
            }

            if (actual == objetivo) {
                break;
            }

            actual.visitado = true;

            for (AristaGrafo<T> arista
                    : actual.vertice.getAdyacentes()) {

                EstadoRuta vecino = buscarEstado(
                        estados, arista.getDestino());

                if (vecino.visitado) {
                    continue;
                }

                double nuevaDistancia =
                        actual.distancia + arista.getPeso();

                if (nuevaDistancia < vecino.distancia) {
                    vecino.distancia = nuevaDistancia;
                    vecino.anterior = actual;
                }
            }
        }

        return objetivo;
    }

    public ListaDoble<T> rutaMasCorta(T origen, T destino) {

        EstadoRuta objetivo = calcularDijkstra(origen, destino);
        ListaDoble<T> ruta = new ListaDoble<>();

        if (Double.isInfinite(objetivo.distancia)) {
            return ruta;
        }

        EstadoRuta actual = objetivo;

        while (actual != null) {
            ruta.agregarAlInicio(actual.vertice.getDato());
            actual = actual.anterior;
        }

        return ruta;
    }

    public double distanciaMinima(T origen, T destino) {
        return calcularDijkstra(origen, destino).distancia;
    }

}
