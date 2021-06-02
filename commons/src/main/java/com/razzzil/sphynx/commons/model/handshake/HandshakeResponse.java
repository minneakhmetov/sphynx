package com.razzzil.sphynx.commons.model.handshake;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HandshakeResponse {
    private HandshakeStatus status;
}
