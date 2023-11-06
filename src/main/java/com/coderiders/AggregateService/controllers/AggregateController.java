package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.models.AuthRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RefreshScope
@RestController
@RequiredArgsConstructor
public class AggregateController {

    @Value("${keys.jwt}")
    private String secretKey;

    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthRequest claimsObj) {
        log.info("/generateToken POST ENDPOINT HIT");
        return generateJWTToken(claimsObj);
    }

    @GetMapping("/")
    public String myRoute() {
        log.info("Base Route Hit");
        return "Successful AggregateController";
    }

    private String generateJWTToken(AuthRequest claimsObj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> claims = objectMapper.convertValue(claimsObj, Map.class);

            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                    .compact();
        } catch (Exception e) {
            return null;
        }
    }
}
