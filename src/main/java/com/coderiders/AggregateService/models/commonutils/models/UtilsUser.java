package com.coderiders.AggregateService.models.commonutils.models;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@ToString
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UtilsUser implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private String clerkId;
    private String imageUrl;

}
