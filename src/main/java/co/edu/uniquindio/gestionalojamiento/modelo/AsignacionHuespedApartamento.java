
package co.edu.uniquindio.gestionalojamiento.modelo;

public class AsignacionHuespedApartamento {

    private final Huesped huesped;
    private final Apartamento apartamento;

    public AsignacionHuespedApartamento(
            Huesped huesped,
            Apartamento apartamento) {

        if (huesped == null || apartamento == null) {
            throw new IllegalArgumentException(
                    "El huésped y el apartamento son obligatorios.");
        }

        this.huesped = huesped;
        this.apartamento = apartamento;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    @Override
    public String toString() {
        return "AsignacionHuespedApartamento{" +
                "huesped=" + huesped.getDocumento() +
                ", apartamento=" + apartamento.getCodigo() +
                '}';
    }
}
