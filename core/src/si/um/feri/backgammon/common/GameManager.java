package si.um.feri.backgammon.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.enums.ColorEnum;
import si.um.feri.backgammon.enums.GameStateEnum;

public class GameManager {
    public static Leaderboard LEADERBOARD;

    public static final GameManager INSTANCE = new GameManager();
    public static final int ROWS = 2;
    public static final int COLUMNS = 12;
    public static final int FIELD_COUNT = ROWS*COLUMNS;

    public static GameStateEnum GAME_STATE = GameStateEnum.RUNNING;
    public static int FIGURES_HOME_BRIGHT = 0;
    public static int FIGURES_HOME_DARK = 0;
    public static boolean DOUBLED = false;
    public static boolean DID_ROLL = false;

    // Preferences
    private static final String KEY_INIT_MOVE = "initMove";
    private static final String KEY_FIELD_INDEXES = "fieldIndexes";
    private static final String KEY_MUSIC = "music";
    private static final String KEY_SOUND_EFFECTS = "soundEffects";
    private final Preferences PREFS;
    // Settings (init move & display field indexes)
    private ColorEnum initMove;
    public boolean switchFieldIndexes;
    public boolean switchMusic;
    public boolean switchSoundEffects;
    public String usernameDark = "player1";
    public String usernameBright = "player2";

    public int[] boardState = new int[FIELD_COUNT];
    public Array<Integer> rollValues = new Array<Integer>();

    private final Gson gson;
    private final FileHandle file;

    private GameManager() {
        PREFS = Gdx.app.getPreferences(BackgammonGame.class.getSimpleName());
        String moveName = PREFS.getString(KEY_INIT_MOVE, ColorEnum.BRIGHT.name());
        initMove = ColorEnum.valueOf(moveName);
        switchFieldIndexes = PREFS.getBoolean(KEY_FIELD_INDEXES, false);
        switchMusic = PREFS.getBoolean(KEY_MUSIC, true);
        switchSoundEffects = PREFS.getBoolean(KEY_SOUND_EFFECTS, true);

        gson = new Gson();
        file = Gdx.files.local("backgammon_leaderboard.json");
        if(file.exists()) readJsonFile();
        else LEADERBOARD = new Leaderboard();
        resetBoard();
    }

    private void readJsonFile() {
        if(file.exists()) {
            String fileData = file.readString();
            try {
                LEADERBOARD = gson.fromJson(fileData, Leaderboard.class);
            } catch (Exception e) {
                e.printStackTrace();
                LEADERBOARD = new Leaderboard();
            }
        }
        else LEADERBOARD = new Leaderboard();
    }

    private void writeJsonFile() {
        String jsonString = gson.toJson(LEADERBOARD);
        file.writeString(jsonString, false);
    }

    public void addWin(ColorEnum c) {
        if(c == ColorEnum.DARK) {
            LEADERBOARD.addWin(usernameDark);
        }
        else LEADERBOARD.addWin(usernameDark);
        writeJsonFile();
    }

    // get
    public ColorEnum getInitMove() {
        return initMove;
    }
    public boolean getFieldIndexesSwitch() {
        return switchFieldIndexes;
    }
    public boolean getMusicSwitch() {
        return switchMusic;
    }
    public boolean getSoundEffectsSwitch() {
        return switchSoundEffects;
    }
    // set
    public void setInitMove(ColorEnum color) {
        initMove = color;
        PREFS.putString(KEY_INIT_MOVE, color.name());
        PREFS.flush();
    }
    public void setFieldIndexes(boolean x) {
        switchFieldIndexes = x;
        PREFS.putBoolean(KEY_FIELD_INDEXES, switchFieldIndexes);
        PREFS.flush();
    }
    public void setMusicSwitch(boolean x) {
        switchMusic = x;
        PREFS.putBoolean(KEY_MUSIC, switchMusic);
        PREFS.flush();
    }
    public void setSoundEffectsSwitch(boolean x) {
        switchSoundEffects = x;
        PREFS.putBoolean(KEY_MUSIC, switchSoundEffects);
        PREFS.flush();
    }

    public void resetBoard() {
        boardState = new int[FIELD_COUNT];
        for(int i = 0; i< FIELD_COUNT; i++) {
            boardState[i] = 0;
        }

        // positive values represent bright figures
        boardState[0] = 2;
        boardState[11] = 5;
        boardState[18] = 5;
        boardState[16] = 3;
        // negative values represent dark figures

        boardState[5] = -5;
        boardState[7] = -3;
        boardState[12] = -5;
        boardState[23] = -2;
/*
        // STORAGE TESTING
        boardState[5] = -5;
        boardState[3] = -5;

        boardState[20] = 5;
        boardState[18] = 5;
 */
    }

    public void roll() {
        rollValues.clear();
        int dice1 = MathUtils.random(1,6);
        int dice2 = MathUtils.random(1,6);
        rollValues.add(dice1);
        rollValues.add(dice2);
        if(dice1 == dice2) {
            DOUBLED = true;
            rollValues.add(dice1);
            rollValues.add(dice1);
        }
        else DOUBLED = false;
        DID_ROLL = true;
    }

    public void reset() {
        resetBoard();
        rollValues.clear();

        FIGURES_HOME_BRIGHT = 0;
        FIGURES_HOME_DARK = 0;
        DOUBLED = false;
        DID_ROLL = false;
        GAME_STATE = GameStateEnum.RUNNING;
    }
}
