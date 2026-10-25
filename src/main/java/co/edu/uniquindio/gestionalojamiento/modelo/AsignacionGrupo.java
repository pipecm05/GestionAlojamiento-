
package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;

public class AsignacionGrupo {

    private String codigo;
    private ListaDoble<Huesped> huespedes;
    private ListaDoble<Apartamento> apartamentos;

    public AsignacionGrupo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la asignación es obligatorio."
            );
        }

        this.codigo = codigo;
        this.huespedes = new ListaDoble<>();
        this.apartamentos = new ListaDoble<>();
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

    public void agregarHuesped(Huesped huesped) {
        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped no puede ser nulo."
            );
        }

        huespedes.agregar(huesped);
    }

    public void agregarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        apartamentos.agregar(apartamento);
    }

    @Override
    public String toString() {
        return "AsignacionGrupo{" +
                "codigo='" + codigo + '\'' +
                '}';
    }
}
