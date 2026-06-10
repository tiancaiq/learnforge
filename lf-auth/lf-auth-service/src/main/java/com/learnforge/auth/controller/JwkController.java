package com.learnforge.auth.controller;

import cn.hutool.core.codec.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.KeyPair;

@RestController
@RequestMapping("jwks")
@ApiIgnore
public class JwkController {

    private final KeyPair keyPair;

    @Autowired
    public JwkController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping
    public String getJwk(){
        // TODO can add clientId and clientSecret validation
        // Get public key and encode
        return Base64.encode(keyPair.getPublic().getEncoded());
    }
}
