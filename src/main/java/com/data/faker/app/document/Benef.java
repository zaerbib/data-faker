package com.data.faker.app.document;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Benef {
    private Double diff;
    private Double invest;
    private String state;
}
