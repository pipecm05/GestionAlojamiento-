package co.edu.uniquindio.gestionalojamiento.estructuras;

public class PruebaGrafo {

    public static void main(String[] args) {

        Grafo<String> grafo = new Grafo<>();

        System.out.println("Agregar recepción: "
                + grafo.agregarVertice("Recepcion"));

        System.out.println("Agregar A101: "
                + grafo.agregarVertice("A101"));

        System.out.println("Agregar A102: "
                + grafo.agregarVertice("A102"));

        System.out.println("Agregar recepción repetida: "
                + grafo.agregarVertice("Recepcion"));

        System.out.println("Conectar recepción con A101: "
                + grafo.agregarArista(
                "Recepcion", "A101", 8));

        System.out.println("Conectar A101 con A102: "
                + grafo.agregarArista(
                "A101", "A102", 5));

        System.out.println("Conexión inversa A101-Recepción: "
                + grafo.existeArista(
                "A101", "Recepcion"));

        System.out.println("Conexión directa Recepción-A102: "
                + grafo.existeArista(
                "Recepcion", "A102"));

        System.out.println("Total vértices: "
                + grafo.cantidadVertices());

        System.out.print("Vértices: ");

        for (String lugar : grafo) {
            System.out.print(lugar + " ");
        }

        System.out.println();


        System.out.println("\n--- RECORRIDO BFS ---");

        // Agregar dos ubicaciones nuevas.
        grafo.agregarVertice("A103");
        grafo.agregarVertice("A104");

        // Conectar los nuevos apartamentos.
        grafo.agregarArista("Recepcion", "A103", 6);
        grafo.agregarArista("A102", "A104", 4);

        // Recorrer el grafo desde recepción.
        ListaDoble<String> recorridoBfs =
                grafo.bfs("Recepcion");

        System.out.print("BFS desde Recepcion: ");

        for (String lugar : recorridoBfs) {
            System.out.print(lugar + " ");
        }

        System.out.println();


        System.out.println("\n--- RECORRIDO DFS ---");

        ListaDoble<String> recorridoDfs =
                grafo.dfs("Recepcion");

        System.out.print("DFS desde Recepcion: ");

        for (String lugar : recorridoDfs) {
            System.out.print(lugar + " ");
        }

        System.out.println();


        System.out.println("\n--- DIJKSTRA ---");

        // Nuevo pasillo entre A103 y A104.
        grafo.agregarArista("A103", "A104", 3);

        ListaDoble<String> ruta =
                grafo.rutaMasCorta("Recepcion", "A104");

        System.out.print("Ruta más corta: ");

        for (String lugar : ruta) {
            System.out.print(lugar + " ");
        }

        System.out.println();

        System.out.println("Distancia mínima: "
                + grafo.distanciaMinima("Recepcion", "A104")
                + " metros");


        System.out.println("\n--- CASOS ESPECIALES DE DIJKSTRA ---");

        // Ubicación sin conexiones.
        grafo.agregarVertice("Bodega");

        ListaDoble<String> rutaInexistente =
                grafo.rutaMasCorta("Recepcion", "Bodega");

        System.out.println("Ruta a Bodega vacía: "
                + rutaInexistente.estaVacia());

        System.out.println("Distancia a Bodega infinita: "
                + Double.isInfinite(
                grafo.distanciaMinima("Recepcion", "Bodega")
        ));

        // El origen y destino son el mismo.
        ListaDoble<String> rutaMismoLugar =
                grafo.rutaMasCorta("Recepcion", "Recepcion");

        System.out.println("Ruta al mismo lugar tiene 1 vértice: "
                + (rutaMismoLugar.getTamanio() == 1));

        System.out.println("Distancia al mismo lugar: "
                + grafo.distanciaMinima("Recepcion", "Recepcion"));

        // Rechazar vértices inexistentes.
        try {
            grafo.rutaMasCorta("Recepcion", "A999");
            System.out.println("ERROR: se aceptó A999");
        } catch (IllegalArgumentException e) {
            System.out.println("Vértice inexistente rechazado");
        }


        System.out.println("\n--- VALIDACIONES DEL GRAFO ---");

// No debe permitir una conexión duplicada.
        System.out.println("Arista duplicada rechazada: "
                + !grafo.agregarArista(
                "Recepcion", "A101", 8));

        // No debe permitir conectar un vértice consigo mismo.
        try {
            grafo.agregarArista("A101", "A101", 5);
            System.out.println("Autoconexión rechazada: false");
        } catch (IllegalArgumentException e) {
            System.out.println("Autoconexión rechazada: true");
        }

        // No debe aceptar pesos negativos.
        try {
            grafo.agregarArista("A101", "Bodega", -3);
            System.out.println("Peso negativo rechazado: false");
        } catch (IllegalArgumentException e) {
            System.out.println("Peso negativo rechazado: true");
        }

        // BFS y DFS no deben visitar una bodega desconectada.
        ListaDoble<String> bfsFinal = grafo.bfs("Recepcion");
        ListaDoble<String> dfsFinal = grafo.dfs("Recepcion");

        System.out.println("BFS omite Bodega: "
                + (bfsFinal.getTamanio() == 5));

        System.out.println("DFS omite Bodega: "
                + (dfsFinal.getTamanio() == 5));

    }
}
