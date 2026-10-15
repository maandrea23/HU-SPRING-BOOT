package com.example.productoapi.exception;

public class RecursoNoEncontradoException extends ResourceNotFoundException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
