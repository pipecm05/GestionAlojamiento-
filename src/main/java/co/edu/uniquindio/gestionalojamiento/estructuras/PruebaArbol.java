package co.edu.uniquindio.gestionalojamiento.estructuras;

import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
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


        System.out.println("\n--- PRUEBA ITERATOR ABB ---");

        ArbolBinarioBusqueda<Integer> arbolIterator =
                new ArbolBinarioBusqueda<>();

        arbolIterator.insertar(50);
        arbolIterator.insertar(30);
        arbolIterator.insertar(70);
        arbolIterator.insertar(20);
        arbolIterator.insertar(40);

        for (Integer numero : arbolIterator) {
            System.out.print(numero + " ");
        }
        System.out.println();


        System.out.println("\n--- CASOS LIMITE DEL ABB ---");

        // Caso 1: eliminar la única raíz
        ArbolBinarioBusqueda<Integer> arbol1 =
                new ArbolBinarioBusqueda<>();

        arbol1.insertar(50);
        System.out.println("Eliminar raíz: " + arbol1.eliminar(50));
        System.out.println("Está vacío: " + arbol1.estaVacio());
        System.out.println("Tamaño: " + arbol1.getTamanio());

        // Caso 2: intentar eliminar un elemento inexistente
        System.out.println("Eliminar 100: " + arbol1.eliminar(100));
        System.out.println("Tamaño después: " + arbol1.getTamanio());

        // Caso 3: recorrer un árbol vacío
        System.out.println("Recorrido del árbol vacío:");
        for (Integer numero : arbol1) {
            System.out.println(numero);
        }
        System.out.println("Recorrido terminado");

        // Caso 4: insertar valores ordenados
        ArbolBinarioBusqueda<Integer> arbol2 =
                new ArbolBinarioBusqueda<>();

        arbol2.insertar(10);
        arbol2.insertar(20);
        arbol2.insertar(30);
        arbol2.insertar(40);
        arbol2.insertar(50);

        System.out.println("Altura con valores ordenados: "
                + arbol2.altura());

        System.out.println("Tamaño: " + arbol2.getTamanio());

        System.out.println("Recorrido inorden:");
        for (Integer numero : arbol2) {
            System.out.print(numero + " ");
        }
        System.out.println();


        System.out.println("\n--- ABB DE HUESPEDES ---");

        ArbolBinarioBusqueda<Huesped> arbolHuespedes =
                new ArbolBinarioBusqueda<>();

        Huesped huesped1 = new Huesped(
                "1030", "Carlos", "Gomez",
                "3101234567", "carlos@correo.com"
        );

        Huesped huesped2 = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        Huesped huesped3 = new Huesped(
                "1050", "Laura", "Ramirez",
                "3123456789", "laura@correo.com"
        );

        arbolHuespedes.insertar(huesped1);
        arbolHuespedes.insertar(huesped2);
        arbolHuespedes.insertar(huesped3);

        System.out.println("Huespedes registrados:");

        for (Huesped huesped : arbolHuespedes) {
            System.out.println(
                    huesped.getDocumento() + " - "
                            + huesped.getNombreCompleto()
            );
        }

        // Búsqueda por documento usando un objeto de consulta
        Huesped consulta = new Huesped(
                "1010", "Consulta", "Temporal",
                "0000000000", "consulta@correo.com"
        );

        Huesped encontrado = arbolHuespedes.buscar(consulta);

        System.out.println(
                "Resultado de búsqueda: " +
                        (encontrado != null
                                ? encontrado.getNombreCompleto()
                                : "No encontrado")
        );


        System.out.println("\n--- ABB DE APARTAMENTOS ---");

        ArbolBinarioBusqueda<Apartamento> arbolApartamentos =
                new ArbolBinarioBusqueda<>();

        Apartamento apto1 = new Apartamento(
                "A103", "Suite Familiar",
                5, 2, 50000, 70000, 90000
        );

        Apartamento apto2 = new Apartamento(
                "A101", "Suite Jardin",
                2, 1, 40000, 55000, 75000
        );

        Apartamento apto3 = new Apartamento(
                "A105", "Suite Mirador",
                4, 2, 60000, 80000, 100000
        );

        arbolApartamentos.insertar(apto1);
        arbolApartamentos.insertar(apto2);
        arbolApartamentos.insertar(apto3);

        System.out.println("Apartamentos registrados:");

        for (Apartamento apartamento : arbolApartamentos) {
            System.out.println(
                    apartamento.getCodigo() + " - "
                            + apartamento.getNombre()
            );
        }

        System.out.println(
                "Buscar codigo A101: "
                        + arbolApartamentos.buscar(apto2)
        );

        System.out.println("\n--- BÚSQUEDA POR CLAVE ---");

        Huesped huespedPorDocumento = arbolHuespedes.buscarPorClave(
                "1010",
                String::compareTo,
                Huesped::getDocumento
        );

        System.out.println("Huésped encontrado: " +
                (huespedPorDocumento != null
                        ? huespedPorDocumento.getNombreCompleto()
                        : "No encontrado"));

        Apartamento apartamentoPorCodigo =
                arbolApartamentos.buscarPorClave(
                        "A101",
                        String::compareTo,
                        Apartamento::getCodigo
                );

        System.out.println("Apartamento encontrado: " +
                (apartamentoPorCodigo != null
                        ? apartamentoPorCodigo.getNombre()
                        : "No encontrado"));

// Búsquedas inexistentes
        System.out.println("Documento inexistente: " +
                arbolHuespedes.buscarPorClave(
                        "9999",
                        String::compareTo,
                        Huesped::getDocumento
                ));

        System.out.println("Apartamento inexistente: " +
                arbolApartamentos.buscarPorClave(
                        "A999",
                        String::compareTo,
                        Apartamento::getCodigo
                ));
    }
}
