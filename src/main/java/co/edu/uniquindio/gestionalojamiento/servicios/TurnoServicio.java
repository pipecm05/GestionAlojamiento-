package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoTurno;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Turno;

public class TurnoServicio {

    private final ListaDoble<Turno> turnos;

    public TurnoServicio() {
        turnos = new ListaDoble<>();
    }

    public boolean registrarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "El turno es obligatorio."
            );
        }

        if (!turno.getEmpleado().isActivo()) {
            throw new IllegalArgumentException(
                    "No se puede asignar un turno a un empleado inactivo."
            );
        }

        if (buscarTurno(turno.getCodigo()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un turno con ese código."
            );
        }

        for (Turno existente : turnos) {
            if (turno.seCruzaCon(existente)) {
                throw new IllegalArgumentException(
                        "El empleado ya tiene un turno en ese horario."
                );
            }
        }

        turnos.agregar(turno);
        return true;
    }

    public Turno buscarTurno(String codigo) {
        validarCodigo(codigo);

        for (Turno turno : turnos) {
            if (turno.getCodigo().equals(codigo)) {
                return turno;
            }
        }

        return null;
    }

    public ListaDoble<Turno> obtenerTurnosEmpleado(
            String documentoEmpleado
    ) {
        validarCodigo(documentoEmpleado);

        ListaDoble<Turno> resultado = new ListaDoble<>();

        for (Turno turno : turnos) {
            if (turno.getEmpleado().getDocumento()
                    .equals(documentoEmpleado)) {
                resultado.agregar(turno);
            }
        }

        return resultado;
    }

    public ListaDoble<Turno> listarTurnos() {
        ListaDoble<Turno> resultado = new ListaDoble<>();

        for (Turno turno : turnos) {
            resultado.agregar(turno);
        }

        return resultado;
    }

    public boolean cancelarTurno(String codigo) {
        Turno turno = buscarTurno(codigo);

        if (turno == null) {
            return false;
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new IllegalArgumentException(
                    "No se puede cancelar un turno completado."
            );
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            return false;
        }

        turno.cambiarEstado(EstadoTurno.CANCELADO);
        return true;
    }

    public boolean completarTurno(String codigo) {
        Turno turno = buscarTurno(codigo);

        if (turno == null) {
            return false;
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new IllegalArgumentException(
                    "No se puede completar un turno cancelado."
            );
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            return false;
        }

        turno.cambiarEstado(EstadoTurno.COMPLETADO);
        return true;
    }

    public int cantidadTurnos() {
        return turnos.getTamanio();
    }

    public boolean existeTurno(String codigo) {
        return buscarTurno(codigo) != null;
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código o documento es obligatorio."
            );
        }
    }
}
