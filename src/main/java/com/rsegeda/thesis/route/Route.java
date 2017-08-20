package com.rsegeda.thesis.route;

import com.rsegeda.thesis.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Route {

    @Id
    Long id;
    @DBRef
    List<Location> locations;
}
