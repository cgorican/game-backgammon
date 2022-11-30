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
    public static final float INTRO_DURATION = 3f;
    
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
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport,game.getBatch());

        // load assets
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        stage.addActor(createSpike());
        stage.addActor(createAnimation());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f,0.8f,0.8f,0);

        duration += delta;

        if(duration > INTRO_DURATION) {
            game.setScreen(new MenuScreen());
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

    private Actor createSpike() {
        Image spike = new Image(gameplayAtlas.findRegion(RegionNames.SPIKE_DARK));

        spike.setPosition(viewport.getWorldWidth()/2f - spike.getWidth() / 2f,
                viewport.getWorldHeight()/2f-spike.getHeight()/2f);
        return spike;
    }

    private Actor createAnimation() {
        Image spike = new Image(gameplayAtlas.findRegion(RegionNames.SPIKE_DARK));

        float posX = (viewport.getWorldWidth()/2f) - spike.getWidth() / 2f;
        float posY = (viewport.getWorldHeight()/2f) - spike.getHeight() / 2f;

        spike.setOrigin(Align.center);
        spike.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.rotateBy(1080,1.5f),
                                Actions.moveTo(posX,posY,1.5f)
                        ),
                        Actions.rotateBy(-360,1),
                        Actions.scaleTo(0,0,0.5f),
                        Actions.removeActor()
                )
        );

        return spike;
    }
}
