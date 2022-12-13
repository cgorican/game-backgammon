package si.um.feri.backgammon.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.assets.AssetDescriptors;
import si.um.feri.backgammon.assets.RegionNames;
import si.um.feri.backgammon.config.GameConfig;


public class IntroScreen extends ScreenAdapter {
    public static final float INTRO_DURATION = 5f;
    
    private final BackgammonGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private TextureAtlas gameplayAtlas;

    private float duration = 0f;
    private Stage stage;

    public IntroScreen(BackgammonGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT);
        stage = new Stage(viewport,game.getBatch());

        // load assets
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.UI_FONT);
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        stage.addActor(createBackground());
        stage.addActor(createFigureAnimation());
        createSpikeAnimation();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.4f,0.8f,0.8f,0);

        duration += delta;

        if(duration > INTRO_DURATION) {
            game.setScreen(new MenuScreen(game));
        }

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

    private Actor createFigureAnimation() {
        Image figure = new Image(gameplayAtlas.findRegion(RegionNames.FIGURE_BRIGHT));

        float centerX = viewport.getWorldWidth()/2f - figure.getWidth()/2f;
        float centerY = viewport.getWorldHeight()/2f - figure.getHeight()/2f;

        figure.setOrigin(Align.center);
        figure.setPosition(centerX,-figure.getHeight());
        figure.addAction(
                Actions.sequence(
                        Actions.delay(2.5f),
                        Actions.parallel(
                                Actions.rotateBy(360,1f),
                                Actions.moveTo(centerX,centerY,1f)
                        ),
                        Actions.fadeOut(1.5f),
                        Actions.removeActor()
                )
        );
        return figure;
    }

    private Actor createSpikeWithAnimation(int index, int rowCount) {
        Image spike;
        if(index % 2 == 0) {
            spike = new Image(gameplayAtlas.findRegion(RegionNames.SPIKE_INTRO_BRIGHT));
        }
        else {
            spike = new Image(gameplayAtlas.findRegion(RegionNames.SPIKE_INTRO_DARK));
        }

        float posX = index*spike.getWidth();
        float posY = 0;

        spike.setOrigin(Align.center);
        if(index >= rowCount) {
            posX = (index-rowCount)*spike.getWidth();
            spike.setRotation(180);
            posY = viewport.getWorldHeight()-spike.getHeight();
            spike.setPosition(posX,posY);
            spike.addAction(
                    Actions.sequence(
                            Actions.delay(2f),
                            Actions.moveTo(posX,viewport.getWorldHeight(),1.5f),
                            Actions.removeActor()
                    )
            );
        }
        else {
            posX -= spike.getWidth()/2f;
            spike.setPosition(posX,posY);
            spike.addAction(
                    Actions.sequence(
                            Actions.delay(2f),
                            Actions.moveTo(posX,-spike.getHeight(),1.5f),
                            Actions.removeActor()
                    )
            );
        }

        return spike;
    }

    private Actor createBackground() {
        Image backboard = new Image(gameplayAtlas.findRegion(RegionNames.BACKGROUND));

        backboard.setPosition(viewport.getWorldWidth() / 2f - backboard.getWidth() / 2f,
                viewport.getWorldHeight() / 2f - backboard.getHeight() / 2f);

        return backboard;
    }

    private void createSpikeAnimation() {
        Image spikeRef = new Image(gameplayAtlas.findRegion(RegionNames.SPIKE_INTRO_DARK));

        int count = (int)(viewport.getWorldWidth() / spikeRef.getWidth()) + 1;
        if(viewport.getWorldWidth() % spikeRef.getWidth() != 0) {
            count += 1;
        }

        for(int i=0; i<count; i++) {
            stage.addActor(createSpikeWithAnimation(count+i, count));
            stage.addActor(createSpikeWithAnimation(i, count));
        }
    }
}
