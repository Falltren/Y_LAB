package org.fallt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRs {

    private String name;

    private Boolean success;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
}
