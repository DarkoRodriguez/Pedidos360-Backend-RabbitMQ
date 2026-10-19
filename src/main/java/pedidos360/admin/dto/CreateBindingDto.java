package pedidos360.admin.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la vinculación (Binding) entre una Cola y un Exchange.
 */
public class CreateBindingDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String cola;

    @NotBlank(message = "El nombre del exchange es obligatorio")
    private String exchange;

    @NotNull(message = "La routing key es obligatoria (puede ser una cadena vacía para Fanout Exchange)")
    private String routingKey;

    public CreateBindingDto() {
    }

    public CreateBindingDto(String cola, String exchange, String routingKey) {
        this.cola = cola;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getCola() { return cola; }
    public void setCola(String cola) { this.cola = cola; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
}
