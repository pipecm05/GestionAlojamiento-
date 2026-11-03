package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.enums.CanalReserva;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoApartamento;
import co.edu.uniquindio.gestionalojamiento.enums.EstadoReserva;
import co.edu.uniquindio.gestionalojamiento.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.gestionalojamiento.estructuras.ComparadorApartamentoPreparacion;
import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.modelo.Apartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionGrupo;
import co.edu.uniquindio.gestionalojamiento.modelo.AsignacionHuespedApartamento;
import co.edu.uniquindio.gestionalojamiento.modelo.Huesped;
import co.edu.uniquindio.gestionalojamiento.modelo.Reserva;

import java.time.LocalDate;

public class ReservaServicio {

    private final ArbolBinarioBusqueda<Reserva> reservas;
    private final DisponibilidadServicio disponibilidadServicio;
    private final PreparacionServicio preparacionServicio;

    private final ListaDoble<IndiceReservasApartamento> indicesPorApartamento;


    public ReservaServicio() {
        reservas = new ArbolBinarioBusqueda<>();
        disponibilidadServicio = new DisponibilidadServicio();
        indicesPorApartamento = new ListaDoble<>();

        preparacionServicio = new PreparacionServicio(
                new ComparadorApartamentoPreparacion(reservas));



    }


    public Reserva registrarReserva(
            String codigo,
            Apartamento apartamento,
            Huesped huesped,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        validarCodigoDisponible(codigo);
        validarFechas(fechaEntrada, fechaSalida);

        if (apartamento == null || huesped == null || canal == null) {
            throw new IllegalArgumentException(
                    "El apartamento, el huésped y el canal son obligatorios.");
        }

        // Comprobar que el apartamento esté habilitado
        // para aceptar reservas.
        if (apartamento.getEstado()
                == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalArgumentException(
                    "El apartamento está fuera de servicio.");
        }

        // Obtener el índice propio del apartamento.
        IndiceReservasApartamento indice =
                obtenerOCrearIndice(apartamento);

        // Comprobar las fechas usando su ABB.
        if (!indice.estaLibre(fechaEntrada, fechaSalida)) {
            throw new IllegalArgumentException(
                    "El apartamento no está disponible para esas fechas.");
        }

        Reserva reserva = new Reserva(
                codigo, apartamento, huesped,
                fechaEntrada, fechaSalida, canal);

        // Registrar primero en el árbol general.
        if (!reservas.insertar(reserva)) {
            throw new IllegalStateException(
                    "No fue posible registrar la reserva.");
        }

        // Registrar también en el índice del apartamento.
        // Si falla, revertir la inserción anterior.
        try {
            if (!indice.registrar(reserva)) {
                throw new IllegalStateException(
                        "No fue posible actualizar el índice.");
            }
        } catch (RuntimeException e) {
            reservas.eliminar(reserva);
            throw e;
        }

        return reserva;
    }

    /**
     * Registra una reserva por cada apartamento del grupo.
     * Cada reserva contiene únicamente los huéspedes asignados
     * al apartamento correspondiente.
     */
    public ListaDoble<Reserva> registrarReservaGrupal(
            String codigoBase,
            AsignacionGrupo grupo,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            CanalReserva canal) {

        if (codigoBase == null || codigoBase.isBlank()) {
            throw new IllegalArgumentException(
                    "El código base de la reserva es obligatorio.");
        }

        if (grupo == null) {
            throw new IllegalArgumentException(
                    "El grupo es obligatorio.");
        }

        if (grupo.getApartamentos() == null
                || grupo.getApartamentos().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener apartamentos asignados.");
        }

        if (grupo.getAsignaciones() == null
                || grupo.getAsignaciones().estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener huéspedes asignados a sus apartamentos.");
        }

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva es obligatorio.");
        }

        validarFechas(fechaEntrada, fechaSalida);

        // Primera fase: validar todo sin registrar ninguna reserva.
        ListaDoble<String> codigosRevisados = new ListaDoble<>();

        for (Apartamento apartamento : grupo.getApartamentos()) {

            // Primero verificar que el apartamento no sea nulo.
            if (apartamento == null) {
                throw new IllegalArgumentException(
                        "El grupo contiene un apartamento nulo.");
            }

            // Luego verificar si está repetido.
            for (String codigoRevisado : codigosRevisados) {
                if (codigoRevisado.equals(apartamento.getCodigo())) {
                    throw new IllegalArgumentException(
                            "El apartamento "
                                    + apartamento.getCodigo()
                                    + " está repetido en el grupo.");
                }
            }
            codigosRevisados.agregar(apartamento.getCodigo());

            String codigoReserva =
                    codigoBase + "-" + apartamento.getCodigo();

            validarCodigoDisponible(codigoReserva);

            ListaDoble<Huesped> huespedesApartamento =
                    obtenerHuespedesDelApartamento(grupo, apartamento);

            if (huespedesApartamento.estaVacia()) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " no tiene huéspedes asignados.");
            }

            if (huespedesApartamento.getTamanio()
                    > apartamento.getCapacidadMaxima()) {
                throw new IllegalArgumentException(
                        "Se supera la capacidad del apartamento "
                                + apartamento.getCodigo() + ".");
            }


            if (apartamento.getEstado()
                    == EstadoApartamento.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " está fuera de servicio.");
            }

            IndiceReservasApartamento indice =
                    obtenerOCrearIndice(apartamento);

            if (!indice.estaLibre(fechaEntrada, fechaSalida)) {
                throw new IllegalArgumentException(
                        "El apartamento " + apartamento.getCodigo()
                                + " no está disponible para esas fechas.");
            }

        }

        // Segunda fase: construir todas las reservas.
        ListaDoble<Reserva> nuevasReservas = new ListaDoble<>();

        for (Apartamento apartamento : grupo.getApartamentos()) {

            String codigoReserva =
                    codigoBase + "-" + apartamento.getCodigo();

            ListaDoble<Huesped> huespedesApartamento =
                    obtenerHuespedesDelApartamento(grupo, apartamento);

            Reserva reserva = new Reserva(
                    codigoReserva,
                    apartamento,
                    huespedesApartamento,
                    fechaEntrada,
                    fechaSalida,
                    canal);

            nuevasReservas.agregar(reserva);
        }


        // Tercera fase: registrar en ambas estructuras.
        ListaDoble<Reserva> registradas = new ListaDoble<>();

        for (Reserva reserva : nuevasReservas) {

            IndiceReservasApartamento indice =
                    obtenerOCrearIndice(reserva.getApartamento());

            boolean insertadaGeneral = false;

            try {
                insertadaGeneral = reservas.insertar(reserva);

                if (!insertadaGeneral) {
                    throw new IllegalStateException(
                            "No se pudo registrar " + reserva.getCodigo());
                }

                if (!indice.registrar(reserva)) {
                    throw new IllegalStateException(
                            "No se pudo actualizar el índice de "
                                    + reserva.getCodigo());
                }

                registradas.agregar(reserva);

            } catch (RuntimeException e) {

                // Deshacer la reserva actual si quedó parcialmente registrada.
                if (insertadaGeneral) {
                    reservas.eliminar(reserva);
                    indice.eliminar(reserva);
                }

                // Deshacer las reservas previas de este grupo.
                for (Reserva anterior : registradas) {
                    reservas.eliminar(anterior);

                    IndiceReservasApartamento indiceAnterior =
                            buscarIndice(anterior.getApartamento().getCodigo());

                    indiceAnterior.eliminar(anterior);
                }

                throw e;
            }
        }
        return nuevasReservas;
    }

    private ListaDoble<Huesped> obtenerHuespedesDelApartamento(
            AsignacionGrupo grupo,
            Apartamento apartamento) {

        ListaDoble<Huesped> huespedes = new ListaDoble<>();

        for (AsignacionHuespedApartamento asignacion
                : grupo.getAsignaciones()) {

            if (asignacion.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {

                huespedes.agregar(asignacion.getHuesped());
            }
        }

        return huespedes;
    }

    private void validarCodigoDisponible(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de reserva es obligatorio.");
        }

        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva con el código " + codigo + ".");
        }
    }

    private void validarFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (fechaEntrada == null || fechaSalida == null
                || !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "La fecha de entrada debe ser anterior a la fecha de salida.");
        }
    }

    public Reserva buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equals(codigo)) {
                return reserva;
            }
        }

        return null;
    }

    public ListaDoble<Reserva> listarReservas() {
        return reservas.inorden();
    }


    public boolean estaDisponible(
            Apartamento apartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        validarFechas(fechaEntrada, fechaSalida);

        // Un apartamento fuera de servicio
        // no acepta nuevas reservas.
        if (apartamento.getEstado()
                == EstadoApartamento.FUERA_DE_SERVICIO) {
            return false;
        }

        // Buscar el índice del apartamento.
        IndiceReservasApartamento indice =
                buscarIndice(apartamento.getCodigo());

        // Si todavía no tiene índice,
        // no hay reservas registradas en él.
        if (indice == null) {
            return true;
        }

        // Consultar su árbol de reservas.
        return indice.estaLibre(fechaEntrada, fechaSalida);
    }


    public void confirmarReserva(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes.");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }

    public void cancelarReserva(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() == EstadoReserva.FINALIZADA
                || reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar en su estado actual.");
        }

        reserva.cambiarEstado(EstadoReserva.CANCELADA);
    }

    public void registrarCheckIn(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "La reserva debe estar confirmada para realizar el check-in.");
        }

        if (reserva.getApartamento().getEstado()
                != EstadoApartamento.PREPARADO) {
            throw new IllegalStateException(
                    "El apartamento debe estar preparado.");
        }

        reserva.cambiarEstado(EstadoReserva.EN_CURSO);
        reserva.getApartamento().cambiarEstado(
                EstadoApartamento.OCUPADO, null);
    }

    public void registrarCheckOut(String codigo) {
        Reserva reserva = buscarReservaObligatoria(codigo);

        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede realizar check-out de una reserva en curso.");
        }

        Apartamento apartamento = reserva.getApartamento();

        reserva.cambiarEstado(EstadoReserva.FINALIZADA);

        apartamento.cambiarEstado(
                EstadoApartamento.PENDIENTE_PREPARACION,
                "Pendiente de limpieza después del check-out.");

        preparacionServicio.agregarApartamento(apartamento);
    }

    private Reserva buscarReservaObligatoria(String codigo) {
        Reserva reserva = buscarPorCodigo(codigo);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "No existe una reserva con el código indicado.");
        }

        return reserva;
    }

    public PreparacionServicio getPreparacionServicio() {
        return preparacionServicio;
    }


    private IndiceReservasApartamento buscarIndice(
            String codigoApartamento) {

        for (IndiceReservasApartamento indice
                : indicesPorApartamento) {

            if (indice.getCodigoApartamento()
                    .equals(codigoApartamento)) {
                return indice;
            }
        }

        return null;
    }


    private IndiceReservasApartamento obtenerOCrearIndice(
            Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento es obligatorio.");
        }

        IndiceReservasApartamento indice =
                buscarIndice(apartamento.getCodigo());

        if (indice != null) {
            return indice;
        }

        indice = new IndiceReservasApartamento(apartamento);
        indicesPorApartamento.agregar(indice);

        return indice;
    }

}
