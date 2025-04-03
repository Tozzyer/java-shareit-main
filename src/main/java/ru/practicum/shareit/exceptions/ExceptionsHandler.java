package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundExceptionHandler(NotFoundException e) {
        return Map.of("Error message: ", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> badRequstExceptionHandler(BadRequestException e) {
        return Map.of("Error message: ", e.getMessage());
    }

    @ExceptionHandler(InputDataErrorException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> inputDataErrorExceptionHandler(InputDataErrorException e) {
        return Map.of("Error message: ", e.getMessage());
    }

}
