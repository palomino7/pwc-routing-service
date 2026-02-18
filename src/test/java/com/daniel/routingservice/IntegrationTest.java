package com.daniel.routingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    protected MvcResult performRequestAndReturnResponse(
        String endpoint,
        String... pathVariables)
        throws Exception {

        return mockMvc.perform(
            get(endpoint, (Object[]) pathVariables)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    protected <R> R performRequestAndReturnResponse(
        String endpoint,
        Class<R> responseClass,
        String... pathVariables) throws Exception {

        var result = performRequestForResult(endpoint, pathVariables);

        return objectMapper.readValue(
            result.getResponse().getContentAsString(),
            responseClass
        );
    }

    private MvcResult performRequestForResult(String endpoint, String... pathVariables) throws Exception {

        return mockMvc.perform(
                get(endpoint, (Object[]) pathVariables)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andReturn();
    }
}