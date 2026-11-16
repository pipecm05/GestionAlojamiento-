package co.edu.uniquindio.gestionalojamiento.modelo;

import co.edu.uniquindio.gestionalojamiento.enums.EstadoTareaOperativa;
import co.edu.uniquindio.gestionalojamiento.enums.TipoTareaOperativa;

import java.time.LocalDateTime;

public class TareaOperativa {

    private final String codigo;
    private final TipoTareaOperativa tipo;
    private final Apartamento apartamento;
    private final String motivo;
    private final LocalDateTime fechaCreacion;

    private Empleado empleado;
    private EstadoTareaOperativa estado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String observaciones;

    public TareaOperativa(
            String codigo,
            TipoTareaOperativa tipo,
            Apartamento apartamento,
            String motivo
    ) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la tarea es obligatorio.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de tarea es obligatorio.");
        }
        if (apartamento == null) {
            throw new IllegalArgumentException("El apartamento es obligatorio.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el motivo de la tarea.");
        }

        this.codigo = codigo.trim();
        this.tipo = tipo;
        this.apartamento = apartamento;
        this.motivo = motivo.trim();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoTareaOperativa.PENDIENTE;
        this.observaciones = "";
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoTareaOperativa getTipo() {
        return tipo;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public EstadoTareaOperativa getEstado() {
        return estado;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void asignarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado es obligatorio.");
        }
        if (estado == EstadoTareaOperativa.FINALIZADA
                || estado == EstadoTareaOperativa.CANCELADA) {
            throw new IllegalStateException("No se puede asignar una tarea cerrada.");
        }

        this.empleado = empleado;
        this.fechaAsignacion = LocalDateTime.now();
        this.fechaInicio = null;
        this.estado = EstadoTareaOperativa.ASIGNADA;
    }

    public void iniciar() {
        if (estado != EstadoTareaOperativa.ASIGNADA || empleado == null) {
            throw new IllegalStateException(
                    "La tarea debe estar asignada antes de iniciarse."
            );
        }
        this.estado = EstadoTareaOperativa.EN_PROCESO;
        this.fechaInicio = LocalDateTime.now();
    }

    public void finalizar(String observaciones) {
        if (estado != EstadoTareaOperativa.ASIGNADA
                && estado != EstadoTareaOperativa.EN_PROCESO) {
            throw new IllegalStateException(
                    "Solo se puede finalizar una tarea asignada o en proceso."
            );
        }
        this.observaciones = observaciones == null ? "" : observaciones.trim();
        this.fechaFinalizacion = LocalDateTime.now();
        this.estado = EstadoTareaOperativa.FINALIZADA;
    }

    public void cancelar(String observaciones) {
        if (estado == EstadoTareaOperativa.FINALIZADA
                || estado == EstadoTareaOperativa.CANCELADA) {
            throw new IllegalStateException("La tarea ya está cerrada.");
        }
        this.observaciones = observaciones == null ? "" : observaciones.trim();
        this.estado = EstadoTareaOperativa.CANCELADA;
    }

    public boolean estaActiva() {
        return estado == EstadoTareaOperativa.ASIGNADA
                || estado == EstadoTareaOperativa.EN_PROCESO;
    }

    @Override
    public String toString() {
        return codigo + " - " + tipo + " - "
                + apartamento.getCodigo() + " - " + estado;
    }
}
