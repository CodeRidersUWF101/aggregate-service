package com.coderiders.AggregateService.models;

import com.coderiders.AggregateService.models.commonutils.models.SmallUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaderboardUser extends SmallUser implements Serializable {
    private int points;
    private int rank;
}