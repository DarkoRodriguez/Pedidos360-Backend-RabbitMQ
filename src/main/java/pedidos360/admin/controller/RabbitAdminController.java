package pedidos360.admin.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pedidos360.admin.dto.ApiResponseDto;
import pedidos360.admin.dto.CreateBindingDto;
import pedidos360.admin.dto.CreateExchangeDto;
import pedidos360.admin.dto.CreateQueueDto;
import pedidos360.admin.dto.PublishMessageDto;
import pedidos360.admin.service.RabbitAdminService;

/**
 * Controlador REST para la Administración de RabbitMQ.
 * Expone endpoints estandarizados para gestión de Colas, Exchanges y Bindings.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    // ── GESTIÓN DE COLAS (QUEUES) ─────────────────────────────────────────────

    @PostMapping("/queues")
    public ResponseEntity<ApiResponseDto<CreateQueueDto>> createQueue(@Valid @RequestBody CreateQueueDto dto) {
        adminService.createQueue(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Cola '" + dto.getNombre() + "' creada exitosamente en RabbitMQ", dto));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<ApiResponseDto<Boolean>> deleteQueue(@PathVariable String name) {
        boolean deleted = adminService.deleteQueue(name);
        if (deleted) {
            return ResponseEntity.ok(ApiResponseDto.ok("Cola '" + name + "' eliminada exitosamente", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("La cola '" + name + "' no existe o no pudo ser eliminada"));
        }
    }

    @GetMapping("/queues/{name}")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getQueueInfo(@PathVariable String name) {
        Map<String, Object> info = adminService.getQueueProperties(name);
        return ResponseEntity.ok(ApiResponseDto.ok("Información de la cola '" + name + "' obtenida", info));
    }

    // ── GESTIÓN DE EXCHANGES ──────────────────────────────────────────────────

    @PostMapping("/exchanges")
    public ResponseEntity<ApiResponseDto<CreateExchangeDto>> createExchange(@Valid @RequestBody CreateExchangeDto dto) {
        adminService.createExchange(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Exchange '" + dto.getNombre() + "' de tipo " + dto.getTipo() + " creado exitosamente", dto));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<ApiResponseDto<Boolean>> deleteExchange(@PathVariable String name) {
        boolean deleted = adminService.deleteExchange(name);
        if (deleted) {
            return ResponseEntity.ok(ApiResponseDto.ok("Exchange '" + name + "' eliminado exitosamente", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("El exchange '" + name + "' no existe o no pudo ser eliminado"));
        }
    }

    // ── GESTIÓN DE BINDINGS ───────────────────────────────────────────────────

    @PostMapping("/bindings")
    public ResponseEntity<ApiResponseDto<CreateBindingDto>> createBinding(@Valid @RequestBody CreateBindingDto dto) {
        adminService.createBinding(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Binding creado entre cola '" + dto.getCola() + "' y exchange '" + dto.getExchange() + "'", dto));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<ApiResponseDto<CreateBindingDto>> deleteBinding(@Valid @RequestBody CreateBindingDto dto) {
        adminService.deleteBinding(dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Binding eliminado entre cola '" + dto.getCola() + "' y exchange '" + dto.getExchange() + "'", dto));
    }

    // ── UTILIDAD: ENVÍO DE MENSAJES DE PRUEBA ─────────────────────────────────

    @PostMapping("/messages/publish")
    public ResponseEntity<ApiResponseDto<PublishMessageDto>> publishMessage(@Valid @RequestBody PublishMessageDto dto) {
        adminService.publishMessage(dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Mensaje de prueba publicado exitosamente al exchange '" + dto.getExchange() + "'", dto));
    }
}
