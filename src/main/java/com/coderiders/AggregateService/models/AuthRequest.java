package com.coderiders.AggregateService.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRequest {
    private String firstName;
    private int iat;
    private String iss;
    private String jti;
    private String lastName;
    private int nbf;
    private String sub;
    private String userId;
    private String username;
}
