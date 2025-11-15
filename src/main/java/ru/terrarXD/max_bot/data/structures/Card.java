package ru.terrarXD.max_bot.data.structures;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Card {

    @Getter
    private UUID uuid;

    @Getter @Setter
    private String text;

    @Getter @Setter
    private CardStatus status;

    public Card(){}

    public Card(String text){
        this.text = text;
        status = CardStatus.TODO;
        uuid = UUID.randomUUID();
    }
}
