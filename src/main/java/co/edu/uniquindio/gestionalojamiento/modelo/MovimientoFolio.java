
package co.edu.uniquindio.gestionalojamiento.modelo;

import java.time.LocalDateTime;

public class MovimientoFolio {

    private String codigo;
    private String descripcion;
    private double valor;
    private LocalDateTime fecha;
    private Reserva reserva;

    public MovimientoFolio(
            String codigo,
            String descripcion,
            double valor,
            LocalDateTime fecha,
            Reserva reserva
    ) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del movimiento es obligatorio."
            );
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción es obligatoria."
            );
        }

        if (!Double.isFinite(valor) || valor == 0) {
            throw new IllegalArgumentException(
                    "El valor debe ser válido y diferente de cero."
            );
        }

        if (fecha == null || reserva == null) {
            throw new IllegalArgumentException(
                    "La fecha y la reserva son obligatorias."
            );
        }

        this.codigo = codigo;
        this.descripcion = descripcion;
        this.valor = valor;
        this.fecha = fecha;
        this.reserva = reserva;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public boolean esCargo() {
        return valor > 0;
    }

    public boolean esPago() {
        return valor < 0;
    }

    public double getValorAbsoluto() {
        return Math.abs(valor);
    }

    @Override
    public String toString() {
        return "MovimientoFolio{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", valor=" + valor +
                ", fecha=" + fecha +
                ", reserva=" + reserva.getCodigo() +
                '}';
    }
}
