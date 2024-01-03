package com.data.faker.app.document;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Benef {
    private Double diff;
    private Double invest;
    private String state;
}
