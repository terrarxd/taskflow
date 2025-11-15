package ru.terrarXD.max_bot.data.structures;

import lombok.Getter;
import lombok.Setter;
import ru.terrarXD.max_bot.MaxBotApplication;

import java.util.ArrayList;
import java.util.UUID;

public class CardBoard {
    @Getter
    private UUID uuid;

    @Getter @Setter
    private String title;

    @Getter
    private long owner_id;

    @Getter
    ArrayList<Long> contributors = new ArrayList<>();

    @Getter
    private ArrayList<Card> cards = new ArrayList<>();

    public Card createCard(String text){
        Card card = new Card(text);
        cards.add(card);
        MaxBotApplication.instance.dataManager.saveCardBoard(this);
        return card;
    }

    public Card createCard(){
        return this.createCard("Example card");
    }
    public void removeCard(String card_uuid){
        for (Card card : cards){
            if (card.getUuid().toString().equals(card_uuid)){
                removeCard(card);
                break;
            }
        }
    }

    public void removeCard(Card card) {
        cards.remove(card);
        MaxBotApplication.instance.dataManager.saveCardBoard(this);
    }

    public CardBoard(){
    }

    public CardBoard(long owner_id, String title){
        uuid = UUID.randomUUID();
        this.title = title;
        this.owner_id = owner_id;
    }
}
