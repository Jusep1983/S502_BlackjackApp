package com.jusep1983.blackjack.config;

import io.asyncer.r2dbc.mysql.MySqlConnectionFactoryProvider;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class R2dbcConfig {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer mysqlCustomizer() {
        return builder -> builder.option(
            MySqlConnectionFactoryProvider.SERVER_ZONE_ID,
            ZoneId.of("UTC")
        );
    }
}

