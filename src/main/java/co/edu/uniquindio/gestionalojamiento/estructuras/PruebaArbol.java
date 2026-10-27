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

    }
}
