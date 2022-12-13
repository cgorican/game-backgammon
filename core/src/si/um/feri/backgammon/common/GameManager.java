package si.um.feri.backgammon.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.enums.ColorEnum;

public class GameManager {
    private static final String KEY_INIT_MOVE = "initMove";
    private static final String[] nicknames = {"bob", "ckf", "crt", "lajgotm", "blitz", "wHzZ"};

    public static final GameManager INSTANCE = new GameManager();
    public static final int rows = 2;
    public static final int columns = 12;
    public static final int figureStackLimit = 5;

    private final Preferences PREFS;
    private ColorEnum initMove = ColorEnum.BRIGHT;
    private boolean displayFieldIndexes = false;

    public HashMap<String, Integer> leaderboard = new HashMap<String, Integer>();
    public int[][] boardState = new int[rows][columns];

    private GameManager() {
        PREFS = Gdx.app.getPreferences(BackgammonGame.class.getSimpleName());
        String moveName = PREFS.getString(KEY_INIT_MOVE, ColorEnum.BRIGHT.name());
        initMove = ColorEnum.valueOf(moveName);

        // HARDCODED LEADERBOARD
        for (int i = 0; i < MathUtils.random(3, nicknames.length); i++) {
            leaderboard.put(nicknames[i], MathUtils.random(0,10));
        }
        resetBoard();
    }

    public ColorEnum getInitMove() {
        return initMove;
    }

    public void setInitMove(ColorEnum color) {
        initMove = color;
        PREFS.putString(KEY_INIT_MOVE, color.name());
        PREFS.flush();
    }

    public void resetBoard() {
        boardState = new int[rows][columns];
        for(int i=0; i<rows; i++) {
            for(int j=0; j<columns; j++) {
                boardState[i][j] = 0;
            }
        }
        // 1st row
        boardState[0][0] = -5;  // negative values represent dark figures
        boardState[0][4] = 3;   // positive values represent bright figures
        boardState[0][6] = 5;
        boardState[0][11] = -2;
        // 2nd row
        boardState[1][0] = 5;
        boardState[1][4] = -3;
        boardState[1][6] = -5;
        boardState[1][11] = 2;
    }
}
