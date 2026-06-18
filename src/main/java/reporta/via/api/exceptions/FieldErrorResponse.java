package reporta.via.api.exceptions;

public record FieldErrorResponse(
        String campo,
        String mensagem
) {
}
