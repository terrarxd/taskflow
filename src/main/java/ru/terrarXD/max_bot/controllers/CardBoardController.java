package ru.terrarXD.max_bot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.terrarXD.max_bot.MaxBotApplication;
import ru.terrarXD.max_bot.data.structures.Card;
import ru.terrarXD.max_bot.data.structures.CardBoard;
import ru.terrarXD.max_bot.data.structures.CardStatus;
import ru.terrarXD.max_bot.data.structures.User;

import java.util.Optional;
import java.util.UUID;

@Controller
public class CardBoardController {

    @GetMapping("/cardboard/{id}")
    public String viewCardBoard(@PathVariable("id") String id, Model model,
                                @CookieValue(value = "token", required=true) String token) {
        User user = MaxBotApplication.instance.dataManager.getUserByToken(token);
        CardBoard board = findBoardById(id, user);

        if (board == null) {
            return "redirect:/cardboards";
        }
        model.addAttribute("board", board);
        model.addAttribute("user", user);
        return "cardboard";
    }

    @PostMapping("/cardboard/{boardId}/card/{cardId}/edit")
    public String editCardText(@PathVariable String boardId,
                               @PathVariable String cardId,
                               @RequestParam("text") String text,
                               @CookieValue(value = "token", required=true) String token) {
        User user = MaxBotApplication.instance.dataManager.getUserByToken(token);

        CardBoard board = findBoardById(boardId, user);
        if (board != null) {
            Optional<Card> opt = board.getCards().stream().filter(c -> c.getUuid().toString().equals(cardId)).findFirst();
            if (opt.isPresent()) {
                Card card = opt.get();
                card.setText(text);
                MaxBotApplication.instance.dataManager.saveCardBoard(board);
            }
        }
        return "redirect:/cardboard/" + boardId;
    }

    @PostMapping("/cardboard/{boardId}/card/{cardId}/move")
    public String moveCard(@PathVariable String boardId,
                           @PathVariable String cardId,
                           @RequestParam("status") String status,
                           @CookieValue(value = "token", required=true) String token) {
        User user = MaxBotApplication.instance.dataManager.getUserByToken(token);

        CardBoard board = findBoardById(boardId, user);
        Optional<Card> opt = board.getCards().stream().filter(c -> c.getUuid().toString().equals(cardId)).findFirst();
        if (opt.isPresent()) {
            Card card = opt.get();
            try {
                CardStatus st = CardStatus.valueOf(status);
                card.setStatus(st);
                MaxBotApplication.instance.dataManager.saveCardBoard(board);
            } catch (Exception ignored) {
            }
        }

        return "redirect:/cardboard/" + boardId;
    }

    @PostMapping("/cardboard/{boardId}/card/{cardId}/delete")
    public String deleteCard(@PathVariable String boardId,
                             @PathVariable String cardId,
                             @CookieValue(value = "token", required=true) String token){
        User user = MaxBotApplication.instance.dataManager.getUserByToken(token);
        CardBoard board = findBoardById(boardId, user);
        if (board != null){
            board.removeCard(cardId);
        }
        return "redirect:/cardboard/" + boardId;
    }

    @PostMapping("/cardboard/{boardId}/card/create")
    public String createCard(@PathVariable String boardId,
                             @RequestParam(name = "text", required = false) String text,
                             @CookieValue(value = "token", required=true) String token) {
        User user = MaxBotApplication.instance.dataManager.getUserByToken(token);

        CardBoard board = findBoardById(boardId, user);
        if (board != null) {
            if (text == null || text.trim().isEmpty()) {
                board.createCard();
            } else {
                Card card = board.createCard();
                card.setText(text.trim());
                MaxBotApplication.instance.dataManager.saveCardBoard(board);
            }
        }
        return "redirect:/cardboard/" + boardId;
    }

    private CardBoard findBoardById(String id, User user) {
        try {
            UUID uuid = UUID.fromString(id);
            for (CardBoard b : user.getCardBoards()) {
                if (b.getUuid().equals(uuid)) return b;
            }
        } catch (Exception e) {
        }
        return null;
    }
}
