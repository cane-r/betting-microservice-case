package com.bilyoner.assignment.mocks;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.bilyoner.assignment.couponapi.model.UserBalanceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

public class WireMockExtension implements BeforeAllCallback, AfterAllCallback {

    private final WireMockServer wireMockServer = new WireMockServer();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

        configureFor("localhost", 9090);
        wireMockServer.start();

        UserBalanceDto dto = new UserBalanceDto(1L, BigDecimal.TEN);
        ObjectMapper objectMapper = new ObjectMapper(null, null, null);

        wireMockServer.stubFor(get(urlEqualTo("/balances/*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(dto))));

        wireMockServer.stubFor(put(urlEqualTo("/balances/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")));                           
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        wireMockServer.stop();
    }
}
