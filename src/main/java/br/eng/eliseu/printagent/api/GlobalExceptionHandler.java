package br.eng.eliseu.printagent.api;

import br.eng.eliseu.printagent.api.ApiDtos.*;
import br.eng.eliseu.printagent.service.ApiException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<EnvelopeRespostaDTO<Map<String, Object>>> api(ApiException e) {
        log.warn("Requisição rejeitada [{}] campo={}: {}", e.getCodigo(), e.getCampo(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(EnvelopeRespostaDTO.erro("Não foi possível realizar a operação.", List.of(new ErroDTO(e.getCodigo(), e.getMessage(), e.getCampo()))));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<EnvelopeRespostaDTO<Map<String, Object>>> validacao(MethodArgumentNotValidException e) {
        List<ErroDTO> erros = e.getBindingResult().getFieldErrors().stream().map(x -> new ErroDTO("CAMPO_OBRIGATORIO", mensagem(x.getDefaultMessage()), x.getField())).toList();
        log.warn("Requisição inválida: {}", erros);
        return ResponseEntity.badRequest().body(EnvelopeRespostaDTO.erro("Requisição inválida.", erros));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<EnvelopeRespostaDTO<Map<String, Object>>> parametro(Exception e) {
        return ResponseEntity.badRequest().body(EnvelopeRespostaDTO.erro("Parâmetro inválido.", List.of(new ErroDTO("VALOR_INVALIDO", e.getMessage(), null))));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<EnvelopeRespostaDTO<Map<String, Object>>> json(HttpMessageNotReadableException e) {
        Throwable c = e.getCause();
        while (c != null && !(c instanceof UnrecognizedPropertyException)) c = c.getCause();
        String campo = c instanceof UnrecognizedPropertyException u ? u.getPropertyName() : null;
        log.warn("JSON inválido, campo={}: {}", campo, e.getMessage());
        return ResponseEntity.badRequest().body(EnvelopeRespostaDTO.erro("JSON inválido.", List.of(new ErroDTO("REQUISICAO_INVALIDA", "O corpo da requisição não pôde ser interpretado.", campo))));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<EnvelopeRespostaDTO<Map<String, Object>>> interno(Exception e) {
        log.error("Erro interno não tratado.", e);
        return ResponseEntity.internalServerError().body(EnvelopeRespostaDTO.erro("Erro interno do Print Agent.", List.of(new ErroDTO("ERRO_INTERNO", "Ocorreu um erro inesperado.", null))));
    }

    private String mensagem(String m) {
        return m == null ? "Valor inválido." : m;
    }
}
