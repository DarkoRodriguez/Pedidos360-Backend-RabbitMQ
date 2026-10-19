package pedidos360.admin.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ApiResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String mensaje;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseDto(boolean success, String mensaje, T data) {
        this.success = success;
        this.mensaje = mensaje;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponseDto<T> ok(String mensaje, T data) {
        return new ApiResponseDto<>(true, mensaje, data);
    }

    public static <T> ApiResponseDto<T> error(String mensaje) {
        return new ApiResponseDto<>(false, mensaje, null);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
