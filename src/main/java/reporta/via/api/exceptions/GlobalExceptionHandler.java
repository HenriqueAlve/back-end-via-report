package reporta.via.api.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;


import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestControllerAdvice
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
       List<FieldErrorResponse> erros = e.getBindingResult()
               .getFieldErrors()
               .stream()
               .map(
                       fieldError -> new FieldErrorResponse(
                               fieldError.getField(),
                               fieldError.getDefaultMessage()
                       )
               ).toList();
        String path = request.getDescription(false).replace("uri=", "");

       ApiErrorResponse response = new ApiErrorResponse(
               400,
               "Dados inválidos",
               path,
               LocalDateTime.now(),
               erros
       );
       return ResponseEntity.status(BAD_REQUEST).body(response);

        // pega erros do @Valid
        // monta ApiErrorResponse
        // retorna 400
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String path = request.getDescription(false).replace("uri=", "");

        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Corpo da requisição inválido ou mal formatado",
                path,
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException e, WebRequest request) {
        String path = request.getDescription(false).replace("uri=","");

        ApiErrorResponse response = new ApiErrorResponse(
                404,
                e.getMessage(),
                path,
                LocalDateTime.now(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ApiErrorResponse> handleRegraDeNegocio(RegraDeNegocioException e,WebRequest request) {
        String path = request.getDescription(false).replace("uri=","");

        ApiErrorResponse response = new ApiErrorResponse(
                422,
                e.getMessage(),
                path,
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        // retorna 422
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleErroInterno(Exception e, WebRequest request) {
        String path = request.getDescription(false).replace("uri=","");

        ApiErrorResponse response = new ApiErrorResponse(
                500,
                "Erro interno no servidor",
                path,
                LocalDateTime.now(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        // retorna 500
    }

}
