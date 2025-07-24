package com.jusep1983.blackjack;

import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

//@EnableR2dbcRepositories(basePackages = "com.jusep1983.blackjack.player")
//@EnableReactiveMongoRepositories(basePackages = "com.jusep1983.blackjack.game")
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class BlackjackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlackjackApplication.class, args);
    }

}


