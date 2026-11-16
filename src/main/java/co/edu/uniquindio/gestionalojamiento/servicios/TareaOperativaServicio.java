package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoTareaOperativa;
import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTareaOperativa;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;
import co.edu.uniquindio.gestionalojamiento.modelo.TareaOperativa;


public class TareaOperativaServicio {

    private final EmpleadoServicio empleadoServicio;
    private final ListaDoble<TareaOperativa> tareas;
    private int consecutivo;

    public TareaOperativaServicio(EmpleadoServicio empleadoServicio) {
        if (empleadoServicio == null) {
            throw new IllegalArgumentException(
                    "El servicio de empleados es obligatorio."
            );
        }
        this.empleadoServicio = empleadoServicio;
        this.tareas = new ListaDoble<>();
        this.consecutivo = 1;
    }

    public TareaOperativa crearTarea(
            TipoTareaOperativa tipo,
            Apartamento apartamento,
            String motivo
    ) {
        validarTipo(tipo);
        if (apartamento == null) {
            throw new IllegalArgumentException("El apartamento es obligatorio.");
        }

        if (tipo == TipoTareaOperativa.MANTENIMIENTO
                && apartamento.getEstado() != EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalStateException(
                    "El mantenimiento solo se puede crear para un apartamento fuera de servicio."
            );
        }

        if (tipo == TipoTareaOperativa.LIMPIEZA
                && apartamento.getEstado() != EstadoApartamento.PENDIENTE_PREPARACION
                && apartamento.getEstado() != EstadoApartamento.EN_PREPARACION) {
            throw new IllegalStateException(
                    "La limpieza requiere un apartamento pendiente o en preparación."
            );
        }

        for (TareaOperativa tarea : tareas) {
            if (tarea.getApartamento().getCodigo().equals(apartamento.getCodigo())
                    && tarea.getTipo() == tipo
                    && tarea.getEstado() != EstadoTareaOperativa.FINALIZADA
                    && tarea.getEstado() != EstadoTareaOperativa.CANCELADA) {
                throw new IllegalStateException(
                        "Ya existe una tarea activa de " + tipo
                                + " para el apartamento " + apartamento.getCodigo() + "."
                );
            }
        }

        String codigo = String.format("TO-%04d", consecutivo++);
        TareaOperativa tarea = new TareaOperativa(
                codigo, tipo, apartamento, motivo
        );
        tareas.agregar(tarea);
        return tarea;
    }

    public void asignarEmpleado(String codigoTarea, String documentoEmpleado) {
        TareaOperativa tarea = buscarTareaObligatoria(codigoTarea);
        Empleado empleado = empleadoServicio.buscarEmpleado(documentoEmpleado);

        validarEmpleadoParaTarea(empleado, tarea);

        if (tarea.getEmpleado() != null && tarea.estaActiva()) {
            throw new IllegalStateException(
                    "La tarea ya tiene un empleado asignado. Usa reasignarEmpleado."
            );
        }

        if (tarea.getEstado() != EstadoTareaOperativa.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden asignar tareas pendientes."
            );
        }

        tarea.asignarEmpleado(empleado);
    }

    public void reasignarEmpleado(String codigoTarea, String documentoEmpleado) {
        TareaOperativa tarea = buscarTareaObligatoria(codigoTarea);
        if (!tarea.estaActiva()) {
            throw new IllegalStateException(
                    "Solo se pueden reasignar tareas activas."
            );
        }

        Empleado nuevoEmpleado = empleadoServicio.buscarEmpleado(documentoEmpleado);
        validarEmpleadoParaTarea(nuevoEmpleado, tarea);

        if (tarea.getEmpleado() != null
                && tarea.getEmpleado().getDocumento().equals(documentoEmpleado)) {
            throw new IllegalArgumentException(
                    "Ese empleado ya está asignado a la tarea."
            );
        }

        tarea.asignarEmpleado(nuevoEmpleado);
    }

    public void iniciarTarea(String codigoTarea) {
        buscarTareaObligatoria(codigoTarea).iniciar();
    }

    public void finalizarTarea(String codigoTarea, String observaciones) {
        buscarTareaObligatoria(codigoTarea).finalizar(observaciones);
    }

    public void cancelarTarea(String codigoTarea, String observaciones) {
        buscarTareaObligatoria(codigoTarea).cancelar(observaciones);
    }

    public TareaOperativa buscarTarea(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de tarea es obligatorio.");
        }
        for (TareaOperativa tarea : tareas) {
            if (tarea.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return tarea;
            }
        }
        return null;
    }

    public ListaDoble<TareaOperativa> listarTareas() {
        return copiar(tareas);
    }

    public ListaDoble<TareaOperativa> listarTareasActivas() {
        ListaDoble<TareaOperativa> resultado = new ListaDoble<>();
        for (TareaOperativa tarea : tareas) {
            if (tarea.estaActiva()
                    || tarea.getEstado() == EstadoTareaOperativa.PENDIENTE) {
                resultado.agregar(tarea);
            }
        }
        return resultado;
    }

    public ListaDoble<TareaOperativa> listarPorEmpleado(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        ListaDoble<TareaOperativa> resultado = new ListaDoble<>();
        for (TareaOperativa tarea : tareas) {
            if (tarea.getEmpleado() != null
                    && tarea.getEmpleado().getDocumento().equals(documento)) {
                resultado.agregar(tarea);
            }
        }
        return resultado;
    }

    public ListaDoble<TareaOperativa> listarPorApartamento(String codigoApartamento) {
        if (codigoApartamento == null || codigoApartamento.isBlank()) {
            throw new IllegalArgumentException("El código del apartamento es obligatorio.");
        }
        ListaDoble<TareaOperativa> resultado = new ListaDoble<>();
        for (TareaOperativa tarea : tareas) {
            if (tarea.getApartamento().getCodigo().equals(codigoApartamento)) {
                resultado.agregar(tarea);
            }
        }
        return resultado;
    }

    public ListaDoble<Empleado> listarEmpleadosDisponibles(TipoTareaOperativa tipo) {
        validarTipo(tipo);
        TipoEmpleado tipoEmpleado = convertirTipoEmpleado(tipo);
        ListaDoble<Empleado> resultado = new ListaDoble<>();

        for (Empleado empleado : empleadoServicio.listarEmpleadosActivos()) {
            if (empleado.getTipo() == tipoEmpleado
                    && !tieneTareaActiva(empleado.getDocumento())) {
                resultado.agregar(empleado);
            }
        }
        return resultado;
    }

    public boolean estaDisponible(String documentoEmpleado) {
        Empleado empleado = empleadoServicio.buscarEmpleado(documentoEmpleado);
        return empleado != null && empleado.isActivo()
                && !tieneTareaActiva(documentoEmpleado);
    }

    public boolean tieneTareaActiva(String documentoEmpleado) {
        for (TareaOperativa tarea : tareas) {
            if (tarea.estaActiva()
                    && tarea.getEmpleado() != null
                    && tarea.getEmpleado().getDocumento().equals(documentoEmpleado)) {
                return true;
            }
        }
        return false;
    }

    public int contarTareasPendientes() {
        int total = 0;
        for (TareaOperativa tarea : tareas) {
            if (tarea.getEstado() == EstadoTareaOperativa.PENDIENTE) total++;
        }
        return total;
    }

    public int contarTareasEnProceso() {
        int total = 0;
        for (TareaOperativa tarea : tareas) {
            if (tarea.getEstado() == EstadoTareaOperativa.EN_PROCESO) total++;
        }
        return total;
    }

    public int contarTareasFinalizadas() {
        int total = 0;
        for (TareaOperativa tarea : tareas) {
            if (tarea.getEstado() == EstadoTareaOperativa.FINALIZADA) total++;
        }
        return total;
    }

    private void validarEmpleadoParaTarea(
            Empleado empleado,
            TareaOperativa tarea
    ) {
        if (empleado == null) {
            throw new IllegalArgumentException("No existe el empleado indicado.");
        }
        if (!empleado.isActivo()) {
            throw new IllegalStateException("El empleado está inactivo.");
        }

        TipoEmpleado requerido = convertirTipoEmpleado(tarea.getTipo());
        if (empleado.getTipo() != requerido) {
            throw new IllegalArgumentException(
                    "La tarea requiere un empleado de tipo " + requerido + "."
            );
        }

        if (tieneTareaActiva(empleado.getDocumento())
                && (tarea.getEmpleado() == null
                || !tarea.getEmpleado().getDocumento().equals(empleado.getDocumento()))) {
            throw new IllegalStateException(
                    "El empleado ya tiene otra tarea activa."
            );
        }
    }

    private TareaOperativa buscarTareaObligatoria(String codigo) {
        TareaOperativa tarea = buscarTarea(codigo);
        if (tarea == null) {
            throw new IllegalArgumentException("No existe la tarea " + codigo + ".");
        }
        return tarea;
    }

    private TipoEmpleado convertirTipoEmpleado(TipoTareaOperativa tipo) {
        return tipo == TipoTareaOperativa.LIMPIEZA
                ? TipoEmpleado.LIMPIEZA
                : TipoEmpleado.MANTENIMIENTO;
    }

    private void validarTipo(TipoTareaOperativa tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de tarea es obligatorio.");
        }
    }

    private ListaDoble<TareaOperativa> copiar(
            Iterable<TareaOperativa> origen
    ) {
        ListaDoble<TareaOperativa> copia = new ListaDoble<>();
        for (TareaOperativa tarea : origen) copia.agregar(tarea);
        return copia;
    }
}
