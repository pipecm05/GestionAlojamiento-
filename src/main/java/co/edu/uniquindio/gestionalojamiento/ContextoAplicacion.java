package co.edu.uniquindio.gestionalojamiento;

import co.edu.uniquindio.gestionalojamiento.datos.DatosPrueba;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.CanalExternoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.RutaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TurnoServicio;

/**
 * Mantiene una única instancia de los servicios durante toda la ejecución
 * de la aplicación. Así, los datos creados por el huésped siguen disponibles
 * cuando se inicia sesión como administrador y viceversa.
 */
public final class ContextoAplicacion {

    private static final ContextoAplicacion INSTANCIA =
            new ContextoAplicacion();

    private final CatalogoAlojamiento catalogo;
    private final ReservaServicio reservaServicio;
    private final CanalExternoServicio canalExternoServicio;
    private final TarifaServicio tarifaServicio;
    private final EmpleadoServicio empleadoServicio;
    private final TurnoServicio turnoServicio;
    private final FavoritoServicio favoritoServicio;
    private final RutaServicio rutaServicio;

    private ContextoAplicacion() {
        catalogo = new CatalogoAlojamiento();
        reservaServicio = new ReservaServicio();

        // Este servicio debe utilizar exactamente el mismo ReservaServicio.
        canalExternoServicio = new CanalExternoServicio(reservaServicio);

        tarifaServicio = new TarifaServicio();
        empleadoServicio = new EmpleadoServicio();
        turnoServicio = new TurnoServicio();
        favoritoServicio = new FavoritoServicio();
        rutaServicio = new RutaServicio();

        // Los datos de prueba se cargan una sola vez al iniciar la aplicación.
        DatosPrueba.cargarDatos(
                catalogo,
                reservaServicio,
                tarifaServicio,
                empleadoServicio,
                turnoServicio,
                favoritoServicio
        );
    }

    public static ContextoAplicacion getInstancia() {
        return INSTANCIA;
    }

    public CatalogoAlojamiento getCatalogo() {
        return catalogo;
    }

    public ReservaServicio getReservaServicio() {
        return reservaServicio;
    }

    public CanalExternoServicio getCanalExternoServicio() {
        return canalExternoServicio;
    }

    public TarifaServicio getTarifaServicio() {
        return tarifaServicio;
    }

    public EmpleadoServicio getEmpleadoServicio() {
        return empleadoServicio;
    }

    public TurnoServicio getTurnoServicio() {
        return turnoServicio;
    }

    public FavoritoServicio getFavoritoServicio() {
        return favoritoServicio;
    }

    public RutaServicio getRutaServicio() {
        return rutaServicio;
    }
}
