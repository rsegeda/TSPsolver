package com.rsegeda.thesis.location;

import java.util.List;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public interface GenericMapper<D, E> {

    E toDomain(D domain);

    D toDto(E entity);

    List<D> toDtos(List<E> entities);

    @SuppressWarnings("unused")
    List<E> toDomains(List<D> domains);
}
