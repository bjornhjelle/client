package com.example.client.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRequestResponse {

    /*
    {
      "request_id":"06a2cae3-8aed-475d-98f2-ea30c2c8d00c",
      "download_availability": "2018-01-06 05:44:06.0”
    }
    */

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("download_availability")
    private String downloadAvailability;

    @JsonIgnore
    private OffsetDateTime downloadAvailabilityDateTime;

}
