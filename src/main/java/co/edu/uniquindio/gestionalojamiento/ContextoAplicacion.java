package co.edu.uniquindio.gestionalojamiento;

import co.edu.uniquindio.gestionalojamiento.datos.DatosPrueba;
import co.edu.uniquindio.gestionalojamiento.servicios.CatalogoAlojamiento;
import co.edu.uniquindio.gestionalojamiento.servicios.CanalExternoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.EmpleadoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.FavoritoServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.ReservaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.RutaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TareaOperativaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TarifaServicio;
import co.edu.uniquindio.gestionalojamiento.servicios.TurnoServicio;

public final class ContextoAplicacion {

    private static final ContextoAplicacion INSTANCIA =
            new ContextoAplicacion();

    private final CatalogoAlojamiento catalogo;
    private final ReservaServicio reservaServicio;
    private final CanalExternoServicio canalExternoServicio;
    private final TarifaServicio tarifaServicio;
    private final EmpleadoServicio empleadoServicio;
    private final TareaOperativaServicio tareaOperativaServicio;
    private final TurnoServicio turnoServicio;
    private final FavoritoServicio favoritoServicio;
    private final RutaServicio rutaServicio;

    private ContextoAplicacion() {
        catalogo = new CatalogoAlojamiento();

        empleadoServicio = new EmpleadoServicio();
        tareaOperativaServicio = new TareaOperativaServicio(empleadoServicio);

        // ReservaServicio recibe el servicio compartido para crear tareas
        // de limpieza automáticamente después del check-out.
        reservaServicio = new ReservaServicio(tareaOperativaServicio);
        tareaOperativaServicio.configurarPreparacionServicio(
                reservaServicio.getPreparacionServicio());

        canalExternoServicio = new CanalExternoServicio(reservaServicio);
        tarifaServicio = new TarifaServicio();
        turnoServicio = new TurnoServicio();
        favoritoServicio = new FavoritoServicio();
        rutaServicio = new RutaServicio();

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

    public TareaOperativaServicio getTareaOperativaServicio() {
        return tareaOperativaServicio;
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
