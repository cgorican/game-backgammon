package si.um.feri.backgammon.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Leaderboard {
    public ArrayList<User> users = new ArrayList<User>();
    public Leaderboard() {
        users.add(new User("player1"));
        users.add(new User("player2"));
    }

    @SuppressWarnings("NewApi")
    public void addWin(String username) {
        boolean userFound = false;
        for (User user : users) {
            if (Objects.equals(user.username, username)) {
                user.addWin();
                userFound = true;
                break;
            }
        }
        if(!userFound) {
            User user = new User(username);
            user.addWin();
            users.add(user);
        }
        users.sort(new Comparator<User>() {
            public int compare (User u1, User u2) {
                return u2.getWins() - u1.getWins();
            }
        });
    }
}
