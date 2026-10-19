package pedidos360.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import static org.mockito.Mockito.when;

import pedidos360.admin.dto.CreateQueueDto;
import pedidos360.admin.exception.GlobalAdminExceptionHandler;
import pedidos360.admin.service.RabbitAdminService;

@ExtendWith(MockitoExtension.class)
class RabbitAdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RabbitAdminService adminService;

    @BeforeEach
    void setUp() {
        RabbitAdminController controller = new RabbitAdminController(adminService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalAdminExceptionHandler())
                .build();
    }

    @Test
    void testCrearColaValidaRetorna201Created() throws Exception {
        String jsonPayload = """
            {
                "nombre": "cola.test.valida",
                "durable": true,
                "exclusive": false,
                "autoDelete": false
            }
            """;

        mockMvc.perform(post("/api/admin/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("cola.test.valida"));

        verify(adminService).createQueue(any(CreateQueueDto.class));
    }

    @Test
    void testCrearColaConNombreVacioRetorna400BadRequest() throws Exception {
        // Criterio 8: No es posible crear colas con nombres vacíos ni configuraciones inválidas
        String jsonPayload = """
            {
                "nombre": "   ",
                "durable": true
            }
            """;

        mockMvc.perform(post("/api/admin/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(adminService, never()).createQueue(any());
    }

    @Test
    void testEliminarColaExistenteRetorna200Ok() throws Exception {
        when(adminService.deleteQueue("cola.temporal")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/queues/cola.temporal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(adminService).deleteQueue("cola.temporal");
    }

    @Test
    void testObtenerInfoColaRetornaPropiedades() throws Exception {
        when(adminService.getQueueProperties("pedidos.checkout.queue"))
                .thenReturn(Map.of("exists", true, "messageCount", 0));

        mockMvc.perform(get("/api/admin/queues/pedidos.checkout.queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true));
    }
}
