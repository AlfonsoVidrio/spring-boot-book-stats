package com.alura.desafio.service;

public interface IDataConverter {
    <T> T getData(String json, Class<T> clazz);
}
