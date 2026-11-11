package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoSolicitudExterna;
import co.edu.uniquindio.gestionalojamiento.estructuras.Cola;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.SolicitudExterna;

public class CanalExternoServicio {

    private final ReservaServicio reservaServicio;
    private final Cola<SolicitudExterna> pendientes;
    private final ListaDoble<SolicitudExterna> historial;

    public CanalExternoServicio(ReservaServicio reservaServicio) {

        if (reservaServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de reservas es obligatorio.");
        }

        this.reservaServicio = reservaServicio;
        this.pendientes = new Cola<>();
        this.historial = new ListaDoble<>();
    }

    public void recibirSolicitud(SolicitudExterna solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "La solicitud es obligatoria.");
        }

        if (solicitud.getEstado()
                != EstadoSolicitudExterna.PENDIENTE) {
            throw new IllegalArgumentException(
                    "La solicitud ya fue procesada.");
        }

        if (existeCodigo(solicitud.getCodigo())
                || reservaServicio.buscarPorCodigo(
                solicitud.getCodigo()) != null) {
            throw new IllegalArgumentException(
                    "El código de solicitud ya existe.");
        }

        pendientes.encolar(solicitud);
    }

    public SolicitudExterna procesarSiguiente() {

        if (pendientes.estaVacia()) {
            throw new IllegalStateException(
                    "No hay solicitudes externas pendientes.");
        }

        SolicitudExterna solicitud =
                pendientes.consultarPrimero();

        boolean disponible = reservaServicio.estaDisponible(
                solicitud.getApartamento(),
                solicitud.getFechaEntrada(),
                solicitud.getFechaSalida()
        );

        if (!disponible) {

            solicitud.marcarRechazada(
                    "Conflicto de canal o apartamento no disponible."
            );

            pendientes.desencolar();
            historial.agregar(solicitud);

            return solicitud;
        }

        reservaServicio.registrarReserva(
                solicitud.getCodigo(),
                solicitud.getApartamento(),
                solicitud.getHuesped(),
                solicitud.getFechaEntrada(),
                solicitud.getFechaSalida(),
                CanalReserva.EXTERNA
        );

        solicitud.marcarAceptada();

        pendientes.desencolar();
        historial.agregar(solicitud);

        return solicitud;
    }

    public int procesarTodas() {

        int procesadas = 0;

        while (!pendientes.estaVacia()) {
            procesarSiguiente();
            procesadas++;
        }

        return procesadas;
    }

    public ListaDoble<SolicitudExterna> obtenerPendientes() {

        ListaDoble<SolicitudExterna> copia = new ListaDoble<>();

        for (SolicitudExterna solicitud : pendientes) {
            copia.agregar(solicitud);
        }

        return copia;
    }

    public int cantidadPendientes() {
        return pendientes.getTamanio();
    }

    public int cantidadProcesadas() {
        return historial.getTamanio();
    }

    public int contarConflictos() {

        int cantidad = 0;

        for (SolicitudExterna solicitud : historial) {
            if (solicitud.getEstado()
                    == EstadoSolicitudExterna.RECHAZADA) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public ListaDoble<SolicitudExterna> obtenerHistorial() {

        ListaDoble<SolicitudExterna> copia =
                new ListaDoble<>();

        for (SolicitudExterna solicitud : historial) {
            copia.agregar(solicitud);
        }

        return copia;
    }

    private boolean existeCodigo(String codigo) {

        for (SolicitudExterna solicitud : pendientes) {
            if (solicitud.getCodigo().equals(codigo)) {
                return true;
            }
        }

        for (SolicitudExterna solicitud : historial) {
            if (solicitud.getCodigo().equals(codigo)) {
                return true;
            }
        }

        return false;
    }
}
