package si.um.feri.backgammon.common;

public class User {
    public final String username;
    private int wins = 0;

    public User(String username) {
        this.username = username;
    }

    // get
    public int getWins() {
        return wins;
    }

    // set
    public void addWin() {
        wins++;
    }
}
