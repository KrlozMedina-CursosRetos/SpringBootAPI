package med.voll.api.paciente;

import jakarta.validation.Valid;
import med.voll.api.direccion.DatosDireccion;

public record DatosActualizacionPaciente(Long id, String nombre, String telefono, @Valid DatosDireccion direccion) {
}
