package pedidos360.admin.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import pedidos360.admin.dto.CreateBindingDto;
import pedidos360.admin.dto.CreateExchangeDto;
import pedidos360.admin.dto.CreateQueueDto;
import pedidos360.admin.dto.PublishMessageDto;

/**
 * Implementación dedicada del servicio administrador de RabbitMQ.
 * Toda la interacción con AmqpAdmin / RabbitAdmin se encapsula aquí,
 * manteniendo los controladores limpios y desacoplados (Criterios 6, 7 y 8).
 */
@Service
public class RabbitAdminServiceImpl implements RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminServiceImpl.class);

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    public RabbitAdminServiceImpl(AmqpAdmin amqpAdmin, RabbitTemplate rabbitTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void createQueue(CreateQueueDto dto) {
        log.info("[RABBIT-ADMIN] Creando cola '{}' (durable={}, exclusive={}, autoDelete={})",
                dto.getNombre(), dto.isDurable(), dto.isExclusive(), dto.isAutoDelete());

        QueueBuilder builder = dto.isDurable()
                ? QueueBuilder.durable(dto.getNombre())
                : QueueBuilder.nonDurable(dto.getNombre());

        if (dto.isExclusive()) {
            builder.exclusive();
        }
        if (dto.isAutoDelete()) {
            builder.autoDelete();
        }

        // Configuración de Dead Letter Exchange y Routing Key si se indican
        if (dto.getDeadLetterExchange() != null && !dto.getDeadLetterExchange().isBlank()) {
            builder.withArgument("x-dead-letter-exchange", dto.getDeadLetterExchange().trim());
        }
        if (dto.getDeadLetterRoutingKey() != null && !dto.getDeadLetterRoutingKey().isBlank()) {
            builder.withArgument("x-dead-letter-routing-key", dto.getDeadLetterRoutingKey().trim());
        }
        if (dto.getMessageTtl() != null && dto.getMessageTtl() > 0) {
            builder.withArgument("x-message-ttl", dto.getMessageTtl());
        }

        Queue queue = builder.build();
        amqpAdmin.declareQueue(queue);
        log.info("[RABBIT-ADMIN] Cola '{}' creada exitosamente.", dto.getNombre());
    }

    @Override
    public boolean deleteQueue(String queueName) {
        log.info("[RABBIT-ADMIN] Eliminando cola '{}'", queueName);
        boolean deleted = amqpAdmin.deleteQueue(queueName);
        log.info("[RABBIT-ADMIN] Cola '{}' eliminada. Resultado={}", queueName, deleted);
        return deleted;
    }

    @Override
    public Map<String, Object> getQueueProperties(String queueName) {
        Properties props = amqpAdmin.getQueueProperties(queueName);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("queueName", queueName);

        if (props == null) {
            resultado.put("exists", false);
            resultado.put("message", "La cola no existe o no tiene propiedades disponibles");
            return resultado;
        }

        resultado.put("exists", true);
        resultado.put("messageCount", props.get(org.springframework.amqp.rabbit.core.RabbitAdmin.QUEUE_MESSAGE_COUNT));
        resultado.put("consumerCount", props.get(org.springframework.amqp.rabbit.core.RabbitAdmin.QUEUE_CONSUMER_COUNT));
        resultado.put("rawProperties", props);
        return resultado;
    }

    @Override
    public void createExchange(CreateExchangeDto dto) {
        log.info("[RABBIT-ADMIN] Creando Exchange '{}' de tipo '{}'", dto.getNombre(), dto.getTipo());

        Exchange exchange;
        switch (dto.getTipo()) {
            case DIRECT -> exchange = new DirectExchange(dto.getNombre(), dto.isDurable(), dto.isAutoDelete());
            case TOPIC -> exchange = new TopicExchange(dto.getNombre(), dto.isDurable(), dto.isAutoDelete());
            case FANOUT -> exchange = new FanoutExchange(dto.getNombre(), dto.isDurable(), dto.isAutoDelete());
            case HEADERS -> exchange = new HeadersExchange(dto.getNombre(), dto.isDurable(), dto.isAutoDelete());
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado: " + dto.getTipo());
        }

        amqpAdmin.declareExchange(exchange);
        log.info("[RABBIT-ADMIN] Exchange '{}' creado exitosamente.", dto.getNombre());
    }

    @Override
    public boolean deleteExchange(String exchangeName) {
        log.info("[RABBIT-ADMIN] Eliminando Exchange '{}'", exchangeName);
        boolean deleted = amqpAdmin.deleteExchange(exchangeName);
        log.info("[RABBIT-ADMIN] Exchange '{}' eliminado. Resultado={}", exchangeName, deleted);
        return deleted;
    }

    @Override
    public void createBinding(CreateBindingDto dto) {
        log.info("[RABBIT-ADMIN] Creando Binding: Cola '{}' <---> Exchange '{}' (RoutingKey='{}')",
                dto.getCola(), dto.getExchange(), dto.getRoutingKey());

        Binding binding = new Binding(
                dto.getCola(),
                Binding.DestinationType.QUEUE,
                dto.getExchange(),
                dto.getRoutingKey(),
                null
        );

        amqpAdmin.declareBinding(binding);
        log.info("[RABBIT-ADMIN] Binding creado exitosamente.");
    }

    @Override
    public void deleteBinding(CreateBindingDto dto) {
        log.info("[RABBIT-ADMIN] Eliminando Binding: Cola '{}' <-x-> Exchange '{}' (RoutingKey='{}')",
                dto.getCola(), dto.getExchange(), dto.getRoutingKey());

        Binding binding = new Binding(
                dto.getCola(),
                Binding.DestinationType.QUEUE,
                dto.getExchange(),
                dto.getRoutingKey(),
                null
        );

        amqpAdmin.removeBinding(binding);
        log.info("[RABBIT-ADMIN] Binding eliminado exitosamente.");
    }

    @Override
    public void publishMessage(PublishMessageDto dto) {
        log.info("[RABBIT-ADMIN] Publicando mensaje al exchange '{}' con routing key '{}'",
                dto.getExchange(), dto.getRoutingKey());

        rabbitTemplate.convertAndSend(dto.getExchange(), dto.getRoutingKey(), dto.getPayload());
        log.info("[RABBIT-ADMIN] Mensaje publicado exitosamente.");
    }
}
