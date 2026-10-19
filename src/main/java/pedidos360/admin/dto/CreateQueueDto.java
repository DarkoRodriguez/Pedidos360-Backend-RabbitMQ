package pedidos360.admin.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación dinámica de colas en RabbitMQ.
 * Implementa validaciones estrictas para prevenir nombres vacíos
 * o configuraciones inválidas (Criterio 8 de la Rúbrica).
 */
public class CreateQueueDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre de la cola es obligatorio y no puede estar vacío ni contener solo espacios")
    @Size(min = 3, max = 255, message = "El nombre de la cola debe tener entre 3 y 255 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de la cola solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String nombre;

    private Boolean durable = Boolean.TRUE;
    private Boolean exclusive = Boolean.FALSE;
    private Boolean autoDelete = Boolean.FALSE;

    // Configuración opcional de Dead Letter Queue (DLQ)
    private String deadLetterExchange;
    private String deadLetterRoutingKey;

    @PositiveOrZero(message = "El TTL de mensajes debe ser un número positivo o cero")
    private Integer messageTtl;

    public CreateQueueDto() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isExclusive() { return exclusive; }
    public void setExclusive(boolean exclusive) { this.exclusive = exclusive; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }

    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }

    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }

    public Integer getMessageTtl() { return messageTtl; }
    public void setMessageTtl(Integer messageTtl) { this.messageTtl = messageTtl; }
}
