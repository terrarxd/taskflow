package ru.terrarXD.max_bot.data.structures;

import lombok.Getter;
import ru.terrarXD.max_bot.MaxBotApplication;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    @Getter
    private long user_id;

    @Getter
    private transient ArrayList<CardBoard> cardBoards = new ArrayList<>();

    @Getter
    private transient ArrayList<String> tokens = new ArrayList<>();

    public User() {
        this.cardBoards = new ArrayList<>();
        tokens = new ArrayList<>();
    }

    public String auth(){
        String token = String.valueOf(UUID.randomUUID());
        tokens.add(token);
        return token;
    }
    public CardBoard createCardBoard(String title){
        CardBoard cardBoard = new CardBoard(user_id, title);
        getCardBoards().add(cardBoard);
        MaxBotApplication.instance.dataManager.getAllCardBoards().add(cardBoard);
        MaxBotApplication.instance.dataManager.saveCardBoard(cardBoard);
        return cardBoard;
    }

    public User(long user_id){
        this.user_id = user_id;
    }
}
