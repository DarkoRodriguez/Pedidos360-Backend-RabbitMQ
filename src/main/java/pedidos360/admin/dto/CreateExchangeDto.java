package pedidos360.admin.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación dinámica de Exchanges en RabbitMQ.
 * Valida tipo (DIRECT, TOPIC, FANOUT, HEADERS) y formato del nombre.
 */
public class CreateExchangeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre del exchange es obligatorio y no puede estar vacío")
    @Size(min = 3, max = 255, message = "El nombre del exchange debe tener entre 3 y 255 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del exchange solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos")
    private String nombre;

    @NotNull(message = "El tipo de exchange es obligatorio (DIRECT, TOPIC, FANOUT, HEADERS)")
    private ExchangeType tipo;

    private Boolean durable = Boolean.TRUE;
    private Boolean autoDelete = Boolean.FALSE;

    public CreateExchangeDto() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public ExchangeType getTipo() { return tipo; }
    public void setTipo(ExchangeType tipo) { this.tipo = tipo; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
}
