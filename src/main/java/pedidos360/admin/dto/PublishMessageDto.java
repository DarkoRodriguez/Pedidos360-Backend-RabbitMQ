package pedidos360.admin.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para publicar mensajes de prueba hacia cualquier exchange de RabbitMQ.
 */
public class PublishMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El exchange de destino es obligatorio")
    private String exchange;

    @NotNull(message = "La routing key es obligatoria (puede ser vacía)")
    private String routingKey;

    @NotBlank(message = "El payload del mensaje no puede estar vacío")
    private String payload;

    public PublishMessageDto() {
    }

    public PublishMessageDto(String exchange, String routingKey, String payload) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
    }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
