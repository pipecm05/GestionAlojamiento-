package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;

public class CatalogoAlojamiento {

    private final ArbolBinarioBusqueda<Huesped> huespedes;
    private final ArbolBinarioBusqueda<Apartamento> apartamentos;

    public CatalogoAlojamiento() {
        huespedes = new ArbolBinarioBusqueda<>();
        apartamentos = new ArbolBinarioBusqueda<>();
    }

    public boolean registrarHuesped(Huesped huesped) {
        return huespedes.insertar(huesped);
    }

    public boolean registrarApartamento(Apartamento apartamento) {
        return apartamentos.insertar(apartamento);
    }

    public Huesped buscarHuesped(String documento) {
        return huespedes.buscarPorClave(
                documento,
                String::compareTo,
                Huesped::getDocumento
        );
    }

    public Apartamento buscarApartamento(String codigo) {
        return apartamentos.buscarPorClave(
                codigo,
                String::compareTo,
                Apartamento::getCodigo
        );
    }

    public int cantidadHuespedes() {
        return huespedes.getTamanio();
    }

    public int cantidadApartamentos() {
        return apartamentos.getTamanio();
    }

    public boolean eliminarHuesped(String documento) {
        Huesped huesped = buscarHuesped(documento);

        if (huesped == null) {
            return false;
        }

        return huespedes.eliminar(huesped);
    }

    public boolean eliminarApartamento(String codigo) {
        Apartamento apartamento = buscarApartamento(codigo);

        if (apartamento == null) {
            return false;
        }

        return apartamentos.eliminar(apartamento);
    }

    public Iterable<Huesped> listarHuespedes() {
        return huespedes;
    }

    public Iterable<Apartamento> listarApartamentos() {
        return apartamentos;
    }

}
