package com.asl.pms.viewmodels;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class Error {

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String message;

    public Error(String message) {
        this.message = message;
    }
}
