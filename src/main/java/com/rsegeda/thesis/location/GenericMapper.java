package com.rsegeda.thesis.location;

import java.util.List;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public interface GenericMapper<DTO, Domain> {

    Domain toDomain(DTO dto);

    DTO toDto(Domain domain);

    List<DTO> toDtos(List<Domain> domains);

    List<Domain> toDomains(List<DTO> dtos);
}
