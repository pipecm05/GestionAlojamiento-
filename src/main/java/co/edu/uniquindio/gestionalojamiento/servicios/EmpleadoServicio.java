package co.edu.uniquindio.gestionalojamiento.servicios;

import co.edu.uniquindio.gestionalojamiento.estructuras.ListaDoble;
import co.edu.uniquindio.gestionalojamiento.enums.TipoEmpleado;
import co.edu.uniquindio.gestionalojamiento.modelo.Empleado;

public class EmpleadoServicio {

    private final ListaDoble<Empleado> empleados;

    public EmpleadoServicio() {
        empleados = new ListaDoble<>();
    }

    /**
     * Registra un empleado nuevo.
     */
    public boolean registrarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado es obligatorio."
            );
        }

        if (buscarEmpleado(empleado.getDocumento()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con ese documento."
            );
        }

        empleados.agregar(empleado);
        return true;
    }

    /**
     * Busca un empleado por su documento.
     */
    public Empleado buscarEmpleado(String documento) {

        validarDocumento(documento);

        for (Empleado empleado : empleados) {

            if (empleado.getDocumento().equals(documento)) {
                return empleado;
            }
        }

        return null;
    }

    /**
     * Consulta todos los empleados registrados.
     */
    public ListaDoble<Empleado> listarEmpleados() {

        ListaDoble<Empleado> resultado = new ListaDoble<>();

        for (Empleado empleado : empleados) {
            resultado.agregar(empleado);
        }

        return resultado;
    }

    /**
     * Consulta solamente los empleados activos.
     */
    public ListaDoble<Empleado> listarEmpleadosActivos() {

        ListaDoble<Empleado> resultado = new ListaDoble<>();

        for (Empleado empleado : empleados) {

            if (empleado.isActivo()) {
                resultado.agregar(empleado);
            }
        }

        return resultado;
    }

    /**
     * Consulta empleados según su tipo.
     */
    public ListaDoble<Empleado> buscarPorTipo(
            TipoEmpleado tipo
    ) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de empleado es obligatorio."
            );
        }

        ListaDoble<Empleado> resultado = new ListaDoble<>();

        for (Empleado empleado : empleados) {

            if (empleado.getTipo() == tipo) {
                resultado.agregar(empleado);
            }
        }

        return resultado;
    }

    /**
     * Modifica los datos de un empleado.
     */
    public boolean actualizarEmpleado(
            String documento,
            String nombres,
            String apellidos,
            String telefono,
            String correo,
            TipoEmpleado tipo
    ) {

        Empleado empleado = buscarEmpleado(documento);

        if (empleado == null) {
            return false;
        }

        if (!empleado.isActivo()) {
            throw new IllegalArgumentException(
                    "No se puede modificar un empleado inactivo."
            );
        }

        // Validar todos los datos antes de modificarlos.
        validarTexto(nombres, "Los nombres");
        validarTexto(apellidos, "Los apellidos");
        validarTexto(telefono, "El teléfono");

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de empleado es obligatorio."
            );
        }

        if (correo == null || !correo.trim().matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido."
            );
        }

        empleado.setNombres(nombres);
        empleado.setApellidos(apellidos);
        empleado.setTelefono(telefono);
        empleado.setCorreo(correo);
        empleado.setTipo(tipo);

        return true;
    }

    /**
     * Desactiva un empleado sin eliminar su información.
     */
    public boolean desactivarEmpleado(String documento) {

        Empleado empleado = buscarEmpleado(documento);

        if (empleado == null || !empleado.isActivo()) {
            return false;
        }

        empleado.cambiarEstado(false);
        return true;
    }

    /**
     * Activa nuevamente un empleado.
     */
    public boolean activarEmpleado(String documento) {

        Empleado empleado = buscarEmpleado(documento);

        if (empleado == null || empleado.isActivo()) {
            return false;
        }

        empleado.cambiarEstado(true);
        return true;
    }

    /**
     * Consulta la cantidad total de empleados.
     */
    public int cantidadEmpleados() {
        return empleados.getTamanio();
    }

    /**
     * Consulta la cantidad de empleados activos.
     */
    public int cantidadEmpleadosActivos() {
        return listarEmpleadosActivos().getTamanio();
    }

    /**
     * Comprueba si existe un empleado.
     */
    public boolean existeEmpleado(String documento) {
        return buscarEmpleado(documento) != null;
    }

    /**
     * Valida el documento.
     */
    private void validarDocumento(String documento) {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento del empleado es obligatorio."
            );
        }
    }

    /**
     * Valida campos de texto.
     */
    private void validarTexto(String texto, String campo) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " son obligatorios."
            );
        }
    }
}
