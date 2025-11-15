package ru.terrarXD.max_bot.data;

import com.google.gson.Gson;
import lombok.Getter;
import ru.terrarXD.max_bot.data.structures.CardBoard;
import ru.terrarXD.max_bot.data.structures.User;

import java.io.*;
import java.util.ArrayList;

public class DataManager {

    @Getter
    ArrayList<CardBoard> allCardBoards = new ArrayList<>();
    @Getter
    ArrayList<User> allUsers = new ArrayList<>();

    public final String path= "max-bot-data";

    public DataManager(){

        firstRun(path);

        loadData(path);
    }

    Gson gson = new Gson();
    private void loadData(String path){

        for (File file : new File(path+"/users").listFiles()){
            if (file.getName().endsWith(".json")){
                try {
                    User user = gson.fromJson(new FileReader(file), User.class);
                    allUsers.add(user);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (File file : new File(path+"/cardboards").listFiles()){
            if (file.getName().endsWith(".json")){
                try {
                    CardBoard cardBoard = gson.fromJson(new FileReader(file), CardBoard.class);
                    allCardBoards.add(cardBoard);
                    long owenID = cardBoard.getOwner_id();
                    getUserByID(owenID).getCardBoards().add(cardBoard);
                    for (long userID : cardBoard.getContributors()){
                        getUserByID(userID).getCardBoards().add(cardBoard);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void saveUser(User user){
        try (FileWriter writer = new FileWriter(new File(path+"/users/"+user.getUser_id()+".json"))) {
            gson.toJson(user, writer);
        } catch (IOException e) {
        }
    }

    public void saveCardBoard(CardBoard cardBoard){
        try (FileWriter writer = new FileWriter(new File(path+"/cardboards/"+cardBoard.getUuid()+".json"))) {
            gson.toJson(cardBoard, writer);
        } catch (IOException e) {
        }
    }

    public User getUserByID(long id){
        for (User user : allUsers){
            if (user.getUser_id() == id){
                return user;
            }
        }
        return null;
    }
    public void revoveCardBoard(CardBoard cardBoard){
        allCardBoards.remove(cardBoard);
        for (User user : allUsers){
            user.getCardBoards().remove(cardBoard);
        }
        new File(path+"/cardboards/"+cardBoard.getUuid()+".json").delete();
    }

    public User getUserByToken(String token){
        for (User user : allUsers){
            for(String token2 : user.getTokens()){
                if (token2.equals(token)) {
                    return user;
                }
            }
        }
        return null;
    }

    public User createUser(long id){
        User user = new User(id);
        allUsers.add(user);
        saveUser(user);
        return user;
    }

    private void firstRun(String path){
        new File(path).mkdir();
        new File(path+"/users").mkdir();
        new File(path+"/cardboards").mkdir();
    }
}
