package reporta.via.api.exceptions;

public class OcorrenciaNaoEncontradaException extends RecursoNaoEncontradoException {

    public OcorrenciaNaoEncontradaException(){
        super("Ocorrência não encontrada");
    }
}
