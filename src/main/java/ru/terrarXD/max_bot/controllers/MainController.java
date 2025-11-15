package ru.terrarXD.max_bot.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.terrarXD.max_bot.MaxBotApplication;
import ru.terrarXD.max_bot.data.structures.User;
import ru.terrarXD.max_bot.data.structures.CardBoard;

import java.time.Duration;
import java.util.List;

@Controller
public class MainController {

    @ModelAttribute("cardboard")
    public CardBoard cardboardModel() {
        return new CardBoard();
    }

    @GetMapping("/")
    public String main(Model model, HttpServletResponse response, HttpServletRequest request) {
        /*
        String referer = request.getHeader("Referer");
        if (referer != null){
            if (referer.contains("max")){

            }
        }
                return "No " + referer;
         */
        return "main-max";
    }

    @GetMapping("/cardboards")
    public String cardboards(Model model, @CookieValue(value = "token", required=false) String token) {
        try {
            User user = MaxBotApplication.instance.dataManager.getUserByToken(token);
            List<CardBoard> boards = user.getCardBoards();
            model.addAttribute("cardboards", boards);
            return "cardboards";
        } catch (Exception e) {
            return "redirect:/";
        }

    }

    @PostMapping("/cardboards")
    public String createCardboard(Model model, @RequestParam("title") String title, @CookieValue(value = "token", required=false) String token) {
        try {
            User user = MaxBotApplication.instance.dataManager.getUserByToken(token);
            user.createCardBoard(title);
            return "redirect:/cardboards";
        }catch (Exception e) {
            return "redirect:/";
        }
    }

    @PostMapping("/cardboards/{id}/delete")
    public String deleteCardboard(@PathVariable("id") String id, @CookieValue(value = "token", required=false) String token) {
        try {
            User user = MaxBotApplication.instance.dataManager.getUserByToken(token);
            if (user != null) {
                CardBoard toRemove = null;
                for (CardBoard b : user.getCardBoards()){
                    if (b.getUuid().toString().equals(id)){
                        toRemove = b;
                        break;
                    }
                }
                if (toRemove != null){
                    MaxBotApplication.instance.dataManager.revoveCardBoard(toRemove);
                }
            }
            return "redirect:/cardboards";
        } catch (Exception e){
            return "redirect:/cardboards";
        }
    }

}
