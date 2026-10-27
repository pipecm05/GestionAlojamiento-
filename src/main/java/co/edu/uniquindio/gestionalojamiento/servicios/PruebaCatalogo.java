package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;

public class PruebaCatalogo {

    public static void main(String[] args) {

        CatalogoAlojamiento catalogo = new CatalogoAlojamiento();

        Huesped huesped = new Huesped(
                "1010", "Ana", "Lopez",
                "3112345678", "ana@correo.com"
        );

        Apartamento apartamento = new Apartamento(
                "A101", "Suite Jardin",
                2, 1, 40000, 55000, 75000
        );

        System.out.println("Registrar huésped: "
                + catalogo.registrarHuesped(huesped));

        System.out.println("Registrar apartamento: "
                + catalogo.registrarApartamento(apartamento));

        System.out.println("Huésped encontrado: "
                + catalogo.buscarHuesped("1010").getNombreCompleto());

        System.out.println("Apartamento encontrado: "
                + catalogo.buscarApartamento("A101").getNombre());

        System.out.println("Total huéspedes: "
                + catalogo.cantidadHuespedes());

        System.out.println("Total apartamentos: "
                + catalogo.cantidadApartamentos());


        System.out.println("\n--- PRUEBAS ADICIONALES ---");

        // Intentar registrar elementos duplicados
        System.out.println("Huésped duplicado: "
                + catalogo.registrarHuesped(huesped));

        System.out.println("Apartamento duplicado: "
                + catalogo.registrarApartamento(apartamento));

        // Buscar elementos inexistentes
        System.out.println("Huésped inexistente: "
                + catalogo.buscarHuesped("9999"));

        System.out.println("Apartamento inexistente: "
                + catalogo.buscarApartamento("A999"));

        // Eliminar elementos existentes
        System.out.println("Eliminar huésped: "
                + catalogo.eliminarHuesped("1010"));

        System.out.println("Eliminar apartamento: "
                + catalogo.eliminarApartamento("A101"));

        // Verificar tamaños
        System.out.println("Total huéspedes: "
                + catalogo.cantidadHuespedes());

        System.out.println("Total apartamentos: "
                + catalogo.cantidadApartamentos());

        // Intentar eliminar elementos inexistentes
        System.out.println("Eliminar huésped inexistente: "
                + catalogo.eliminarHuesped("9999"));

        System.out.println("Eliminar apartamento inexistente: "
                + catalogo.eliminarApartamento("A999"));

    }
}
