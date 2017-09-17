package com.rsegeda.thesis.route;

import java.util.List;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public interface GenericMapper<D, E> {

    E toDomain(D dto);

    D toDto(E entity);

    List<D> toDtos(List<E> entities);

    List<E> toDomains(List<D> dtos);
}
