package com.daniel.routingservice;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingServiceApplicationIT extends IntegrationTest {

    @Test
    void springContextShouldLoad(ApplicationContext applicationContext) {

        assertThat(applicationContext).isNotNull();
    }
}