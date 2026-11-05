package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.servicios.FolioServicio;

public class RegistroFolio {

    private final String codigoReserva;
    private final FolioServicio folio;

    public RegistroFolio(
            String codigoReserva,
            FolioServicio folio) {

        if (codigoReserva == null || codigoReserva.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de reserva es obligatorio.");
        }

        if (folio == null) {
            throw new IllegalArgumentException(
                    "El folio es obligatorio.");
        }

        this.codigoReserva = codigoReserva;
        this.folio = folio;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public FolioServicio getFolio() {
        return folio;
    }
}
