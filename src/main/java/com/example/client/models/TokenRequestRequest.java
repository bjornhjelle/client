package com.example.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRequestRequest {

    /*
    {
      "ppid" : "215445-000027",
      "requested_auth_entity_count": 10000
    }
    */

    private String ppid;

    @JsonProperty("requested_auth_entity_count")
    private int requestedAuthEntityCount;

}
