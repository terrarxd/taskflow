package ru.terrarXD.max_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.terrarXD.max_bot.data.DataManager;
import ru.terrarXD.max_bot.data.structures.Card;
import ru.terrarXD.max_bot.data.structures.CardBoard;
import ru.terrarXD.max_bot.data.structures.User;

@SpringBootApplication
public class MaxBotApplication {
    public static MaxBotApplication instance;
    public DataManager dataManager;

    @Value("${token}")
    public String TOKEN;

	public static void main(String[] args) {
        SpringApplication.run(MaxBotApplication.class, args);
        new MaxBotApplication(args);
	}

    public MaxBotApplication(String[] args){
        if (instance != null){
            return;
        }
        instance = this;
        dataManager = new DataManager();
    }

}
