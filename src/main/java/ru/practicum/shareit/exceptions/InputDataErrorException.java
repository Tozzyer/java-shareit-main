package ru.practicum.shareit.exceptions;

public class InputDataErrorException extends RuntimeException {
    public InputDataErrorException(String message) {
        super(message);
    }
}
