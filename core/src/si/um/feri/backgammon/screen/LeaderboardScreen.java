package si.um.feri.backgammon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Menu;
import java.util.HashMap;
import java.util.Map;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.assets.AssetDescriptors;
import si.um.feri.backgammon.assets.RegionNames;
import si.um.feri.backgammon.config.GameConfig;

public class LeaderboardScreen extends ScreenAdapter {
    private final BackgammonGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public LeaderboardScreen(BackgammonGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.INTRO_WIDTH, GameConfig.INTRO_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        stage.addActor(createLeaderboard());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.4f, 0.8f, 0.8f, 0);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createLeaderboard() {
        Table table = new Table();
        table.defaults().pad(20);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // Inner table
        Table contentTable = new Table();
        contentTable.defaults().padLeft(20).padRight(20);

        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.MENU_BACKGROUND);
        contentTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));

        // Apply items to inner table
        contentTable.add(new Label("Leaderboard", skin))
                .padBottom(40)
                .align(Align.top)
                .colspan(2)
                .row();
        contentTable.add(new Label("Username", skin));
        contentTable.add(new Label("Wins", skin))
                .padBottom(5)
                .row();
        for (HashMap.Entry<String, Integer> x : game.leaderboard.entrySet()) {
            contentTable.add(new Label(x.getKey(), skin));
            Label scoreLabel = new Label(x.getValue() + "", skin);
            contentTable.add(scoreLabel)
                    .padBottom(5)
                    .row();
        }

        // back btn
        TextButton leaderboardButton = new TextButton("Back", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        contentTable.add(leaderboardButton).padTop(20).colspan(2).row();

        // Apply inner table to global table
        contentTable.center();

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
