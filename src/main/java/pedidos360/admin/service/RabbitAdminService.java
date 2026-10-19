package pedidos360.admin.service;

import java.util.Map;

import pedidos360.admin.dto.CreateBindingDto;
import pedidos360.admin.dto.CreateExchangeDto;
import pedidos360.admin.dto.CreateQueueDto;
import pedidos360.admin.dto.PublishMessageDto;

/**
 * Interfaz del Servicio Administrador de RabbitMQ.
 * Encapsula todas las operaciones de infraestructura de mensajería (Criterio 7).
 */
public interface RabbitAdminService {

    /**
     * Crea una nueva cola en RabbitMQ con las propiedades y DLQ especificadas.
     */
    void createQueue(CreateQueueDto dto);

    /**
     * Elimina una cola existente por su nombre.
     */
    boolean deleteQueue(String queueName);

    /**
     * Consulta las propiedades operativas de una cola (conteo de mensajes, consumidores, etc.).
     */
    Map<String, Object> getQueueProperties(String queueName);

    /**
     * Crea un nuevo Exchange del tipo indicado (DIRECT, TOPIC, FANOUT, HEADERS).
     */
    void createExchange(CreateExchangeDto dto);

    /**
     * Elimina un Exchange por su nombre.
     */
    boolean deleteExchange(String exchangeName);

    /**
     * Vincula una cola a un Exchange mediante una Routing Key.
     */
    void createBinding(CreateBindingDto dto);

    /**
     * Desvincula una cola de un Exchange.
     */
    void deleteBinding(CreateBindingDto dto);

    /**
     * Publica un mensaje de prueba hacia un Exchange con una Routing Key dada.
     */
    void publishMessage(PublishMessageDto dto);
}
