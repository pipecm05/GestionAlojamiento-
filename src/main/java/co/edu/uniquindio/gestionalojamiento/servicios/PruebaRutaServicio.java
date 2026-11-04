package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;

public class PruebaRutaServicio {

    public static void main(String[] args) {

        RutaServicio servicio = new RutaServicio();

        Apartamento apartamento1 = new Apartamento(
                "A101", "Suite Jardin",
                4, 2, 50000, 70000, 90000
        );

        Apartamento apartamento2 = new Apartamento(
                "A102", "Suite Mirador",
                4, 2, 60000, 80000, 100000
        );

        servicio.agregarUbicacion("Recepcion");
        servicio.agregarUbicacion("Escaleras");
        servicio.agregarUbicacion("Aseo");

        servicio.agregarApartamento(apartamento1);
        servicio.agregarApartamento(apartamento2);

        servicio.conectarUbicaciones(
                "Recepcion", "Escaleras", 5);

        servicio.conectarUbicaciones(
                "Escaleras", "A101", 4);

        servicio.conectarUbicaciones(
                "Escaleras", "A102", 7);

        servicio.conectarUbicaciones(
                "Recepcion", "Aseo", 3);

        servicio.conectarUbicaciones(
                "Aseo", "A102", 6);

        System.out.println("--- RUTAS DEL EDIFICIO ---");

        System.out.println("Ubicaciones: "
                + servicio.cantidadUbicaciones());

        ListaDoble<String> ruta =
                servicio.obtenerRutaMasCorta(
                        "Recepcion", "A102");

        System.out.print("Ruta hacia A102: ");

        for (String ubicacion : ruta) {
            System.out.print(ubicacion + " ");
        }

        System.out.println();

        System.out.println("Distancia: "
                + servicio.obtenerDistanciaMinima(
                "Recepcion", "A102")
                + " metros");
    }
}
