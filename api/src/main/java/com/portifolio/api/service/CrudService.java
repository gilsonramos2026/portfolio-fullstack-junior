package com.portifolio.api.service;

import java.util.List;

/**
 * Contrato CRUD comum aos módulos de dominio
 *
 * @param <REQ> DTO de entrada
 * @param <RES> DTO de saida
 */
public interface CrudService<REQ, RES> {

    List<RES> findAll();

    RES findById(Long id);

    RES create(REQ request);

    RES update(Long id, REQ request);

    void delete(Long id);
}
