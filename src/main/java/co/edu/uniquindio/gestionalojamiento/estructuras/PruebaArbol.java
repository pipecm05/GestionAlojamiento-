package co.edu.uniquindio.gestionalojamiento.estructuras;

public class PruebaArbol {

    public static void main(String[] args) {

        ArbolBinarioBusqueda<Integer> arbol =
                new ArbolBinarioBusqueda<>();

        System.out.println("Insertar 50: " + arbol.insertar(50));
        System.out.println("Insertar 30: " + arbol.insertar(30));
        System.out.println("Insertar 70: " + arbol.insertar(70));
        System.out.println("Insertar 30 otra vez: " + arbol.insertar(30));

        System.out.println("Tamaño: " + arbol.getTamanio());
        System.out.println("Buscar 70: " + arbol.buscar(70));
        System.out.println("Buscar 90: " + arbol.buscar(90));

        System.out.println("\n--- PRUEBAS DE ELIMINACIÓN ---");

        ArbolBinarioBusqueda<Integer> prueba =
                new ArbolBinarioBusqueda<>();

        prueba.insertar(50);
        prueba.insertar(30);
        prueba.insertar(70);
        prueba.insertar(20);
        prueba.insertar(40);
        prueba.insertar(60);
        prueba.insertar(80);

        System.out.println("Tamaño inicial: " + prueba.getTamanio());

        System.out.println("Eliminar hoja 20: " + prueba.eliminar(20));
        System.out.println("Eliminar nodo 30: " + prueba.eliminar(30));
        System.out.println("Eliminar raíz 50: " + prueba.eliminar(50));
        System.out.println("Eliminar inexistente 99: " + prueba.eliminar(99));

        System.out.println("Tamaño final: " + prueba.getTamanio());
        System.out.println("Buscar 50: " + prueba.buscar(50));
        System.out.println("Buscar 60: " + prueba.buscar(60));


        System.out.println("\n--- PRUEBAS DE ALTURA ---");

        ArbolBinarioBusqueda<Integer> arbolAltura =
                new ArbolBinarioBusqueda<>();

        System.out.println("Árbol vacío: " + arbolAltura.altura());

        arbolAltura.insertar(50);
        System.out.println("Solo raíz: " + arbolAltura.altura());

        arbolAltura.insertar(30);
        arbolAltura.insertar(70);
        System.out.println("Tres nodos: " + arbolAltura.altura());

        arbolAltura.insertar(20);
        arbolAltura.insertar(10);
        System.out.println("Rama más profunda: " + arbolAltura.altura());



        System.out.println("\n--- PRUEBAS DE RECORRIDOS ---");

        ArbolBinarioBusqueda<Integer> arbolRecorridos =
                new ArbolBinarioBusqueda<>();

        arbolRecorridos.insertar(50);
        arbolRecorridos.insertar(30);
        arbolRecorridos.insertar(70);
        arbolRecorridos.insertar(20);
        arbolRecorridos.insertar(40);
        arbolRecorridos.insertar(60);
        arbolRecorridos.insertar(80);

        System.out.println("INORDEN:");
        for (Integer numero : arbolRecorridos.inorden()) {
            System.out.print(numero + " ");
        }

        System.out.println("\nPREORDEN:");
        for (Integer numero : arbolRecorridos.preorden()) {
            System.out.print(numero + " ");
        }

        System.out.println("\nPOSTORDEN:");
        for (Integer numero : arbolRecorridos.postorden()) {
            System.out.print(numero + " ");
        }
    }
}
