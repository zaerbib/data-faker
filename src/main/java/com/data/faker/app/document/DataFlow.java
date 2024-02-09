package com.data.faker.app.document;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
@Jacksonized
public class DataFlow {

    @Id
    private String id;
    private Double open;
    private Double close;
    private Double volume;
    private Double splitFactor;
    private Double dividend;
    private String symbol;
    private String exchange;
    private LocalDateTime date;
    private Benef benef;
}
