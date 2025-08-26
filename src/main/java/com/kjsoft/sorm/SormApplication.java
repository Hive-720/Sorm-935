package com.kjsoft.sorm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SormApplication {

    public static void main(String[] args) {
        SpringApplication.run(SormApplication.class, args);
    }

    //TODO при запуске сгенерировать уведомление restartDB

    //TODO при запуске нужно сверить graphQL schema с той которая будет в базе и отправить уведомление
}