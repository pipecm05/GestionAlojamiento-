package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;

public class AsignacionGrupo {

    private final String codigo;
    private final ListaDoble<Huesped> huespedes;
    private final ListaDoble<Apartamento> apartamentos;
    private final ListaDoble<AsignacionHuespedApartamento> asignaciones;

    public AsignacionGrupo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la asignación es obligatorio.");
        }
        this.codigo = codigo;
        this.huespedes = new ListaDoble<>();
        this.apartamentos = new ListaDoble<>();
        this.asignaciones = new ListaDoble<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public ListaDoble<Huesped> getHuespedes() {
        return huespedes;
    }

    public ListaDoble<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public ListaDoble<AsignacionHuespedApartamento> getAsignaciones() {
        return asignaciones;
    }

    public void agregarHuesped(Huesped huesped) {
        if (huesped == null) {
            throw new IllegalArgumentException("El huésped no puede ser nulo.");
        }
        for (Huesped existente : huespedes) {
            if (existente.getDocumento().equals(huesped.getDocumento())) {
                throw new IllegalArgumentException(
                        "Ya existe un huésped con ese documento en el grupo.");
            }
        }
        huespedes.agregar(huesped);
    }

    public void agregarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo.");
        }
        for (Apartamento existente : apartamentos) {
            if (existente.getCodigo().equals(apartamento.getCodigo())) {
                throw new IllegalArgumentException(
                        "Ya existe un apartamento con ese código en el grupo.");
            }
        }
        apartamentos.agregar(apartamento);
    }

    public void asignarHuesped(Huesped huesped, Apartamento apartamento) {
        if (huesped == null || apartamento == null) {
            throw new IllegalArgumentException(
                    "El huésped y el apartamento son obligatorios.");
        }
        if (!perteneceHuesped(huesped)) {
            throw new IllegalArgumentException(
                    "El huésped no pertenece a este grupo.");
        }
        if (!perteneceApartamento(apartamento)) {
            throw new IllegalArgumentException(
                    "El apartamento no pertenece a este grupo.");
        }

        for (AsignacionHuespedApartamento asignacion : asignaciones) {
            if (asignacion.getHuesped().getDocumento()
                    .equals(huesped.getDocumento())) {
                throw new IllegalArgumentException(
                        "El huésped ya tiene un apartamento asignado.");
            }
        }

        int ocupacionActual = 0;
        for (AsignacionHuespedApartamento asignacion : asignaciones) {
            if (asignacion.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {
                ocupacionActual++;
            }
        }

        if (ocupacionActual >= apartamento.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                    "El apartamento ya alcanzó su capacidad máxima.");
        }

        asignaciones.agregar(
                new AsignacionHuespedApartamento(huesped, apartamento));
    }

    public boolean perteneceHuesped(Huesped huesped) {
        if (huesped == null) {
            return false;
        }
        for (Huesped existente : huespedes) {
            if (existente.getDocumento().equals(huesped.getDocumento())) {
                return true;
            }
        }
        return false;
    }

    public boolean perteneceApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            return false;
        }
        for (Apartamento existente : apartamentos) {
            if (existente.getCodigo().equals(apartamento.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public boolean estanTodosLosHuespedesAsignados() {
        if (huespedes.estaVacia()) {
            return false;
        }
        for (Huesped huesped : huespedes) {
            boolean encontrado = false;
            for (AsignacionHuespedApartamento asignacion : asignaciones) {
                if (asignacion.getHuesped().getDocumento()
                        .equals(huesped.getDocumento())) {
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
    public String toString() {
        return "AsignacionGrupo{" +
                "codigo='" + codigo + '\'' +
                ", cantidadHuespedes=" + huespedes.getTamanio() +
                ", cantidadApartamentos=" + apartamentos.getTamanio() +
                ", cantidadAsignaciones=" + asignaciones.getTamanio() +
                '}';
    }
}
