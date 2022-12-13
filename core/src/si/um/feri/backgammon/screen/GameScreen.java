package si.um.feri.backgammon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.actors.Figure;
import si.um.feri.backgammon.assets.AssetDescriptors;
import si.um.feri.backgammon.assets.RegionNames;
import si.um.feri.backgammon.common.GameManager;
import si.um.feri.backgammon.config.GameConfig;
import si.um.feri.backgammon.enums.OrientationEnum;
import si.um.feri.backgammon.actors.Spike;
import si.um.feri.backgammon.enums.ColorEnum;

public class GameScreen extends ScreenAdapter {
    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final BackgammonGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private ColorEnum move = GameManager.INSTANCE.getInitMove();

    public GameScreen(BackgammonGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        // Add elements to stage
        gameplayStage.addActor(createBoard());
        addFigures();
        hudStage.addActor(createExitButton());

        Gdx.input.setInputProcessor(new InputMultiplexer(gameplayStage, hudStage));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.f, 0.f, 0.f, 1f);

        // update
        gameplayStage.act(delta);
        hudStage.act(delta);

        // draw
        gameplayStage.draw();
        hudStage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }

    private Actor createExitButton() {
        final TextButton exitBtn = new TextButton("Exit", skin);
        exitBtn.setWidth(100);
        exitBtn.pad(10);
        final TextureRegion storageBaG = gameplayAtlas.findRegion(RegionNames.STORAGE);
        exitBtn.setPosition(
                storageBaG.getRegionWidth()/2f - exitBtn.getWidth()/2f,
                hudViewport.getWorldHeight() / 2f - exitBtn.getHeight() / 2f
        );
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        return exitBtn;
    }

    private Actor createBoard() {
        final TextureRegion solidBackground = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        final TextureRegion storageBackground = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion barBackground = gameplayAtlas.findRegion(RegionNames.BAR);
        final TextureRegion branding = gameplayAtlas.findRegion(RegionNames.BRANDING);
        final TextureRegion spikeBright = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);
        final TextureRegion spikeDark = gameplayAtlas.findRegion(RegionNames.SPIKE_DARK);
        final TextureRegion figure = gameplayAtlas.findRegion(RegionNames.FIGURE_BRIGHT);


        final Table outerTable = new Table();
        outerTable.setDebug(true);   // turn on all debug lines (table, cell, and widget)
        outerTable.setBackground(new TextureRegionDrawable(solidBackground));

        final Table gameTable = new Table();
        gameTable.setDebug(true);

        for (int i = 0; i < 5; i++) {
            if (i == 0 || i == 4) {
                final Table storageTable = new Table();
                storageTable.setDebug(true);
                storageTable.setBackground(new TextureRegionDrawable(storageBackground));
                gameTable.add(storageTable).width(storageBackground.getRegionWidth());
                if(i == 4) {
                    // house - figures_side
                }
            } else if (i == 2) {
                final Table barTable = new Table();
                barTable.setDebug(true);
                barTable.setBackground(new TextureRegionDrawable(barBackground));
                gameTable.add(barTable).width(barBackground.getRegionWidth());
            } else {
                final Table quadrant = new Table();
                quadrant.setDebug(true);
                for (int row = 0; row < GameManager.rows; row++) {
                    for (int column = 0; column < GameManager.columns / 2; column++) {
                        Spike tmpSpike;
                        OrientationEnum orientation = (row == 0) ? OrientationEnum.DOWN : OrientationEnum.UP;
                        if ((row == 0 && column % 2 == 0) || (row == 1 && column % 2 == 1))
                            tmpSpike = new Spike(spikeDark, orientation);
                        else tmpSpike = new Spike(spikeBright, orientation);
                        quadrant.add(tmpSpike)
                                .height(spikeBright.getRegionHeight())
                                .width(spikeBright.getRegionWidth());
                    }
                    quadrant.row();
                    if (row == 0) {
                        final Table diceArea = new Table();
                        diceArea.setDebug(true);
                        diceArea.setBackground(new TextureRegionDrawable(branding));
                        if (move == ColorEnum.BRIGHT) {

                        } else {

                        }
                        // add cubes here
                        quadrant.add(diceArea)
                                .height(GameConfig.HEIGHT - 2*spikeBright.getRegionHeight())
                                .colspan(6)
                                .expand()
                                .fillX()
                                .row();
                    }
                }
                gameTable.add(quadrant);
            }
        }

        outerTable.add(gameTable).row();
        outerTable.center();
        outerTable.setFillParent(true);
        outerTable.pack();

        return outerTable;
    }

    private void addFigures() {
        final TextureRegion storage = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion bar = gameplayAtlas.findRegion(RegionNames.BAR);

        final TextureRegion figureBright = gameplayAtlas.findRegion(RegionNames.FIGURE_BRIGHT);
        final TextureRegion figureDark = gameplayAtlas.findRegion(RegionNames.FIGURE_DARK);

        final float horizontalOffset = storage.getRegionWidth();

        for(int i=0; i<GameManager.rows; i++) {
            for(int j=0; j<GameManager.columns; j++) {
                float posX = horizontalOffset + j*figureBright.getRegionWidth();;
                if(j >= GameManager.columns/2) {
                    posX += bar.getRegionWidth();
                }
                float posY = (i > 0) ? 0 : viewport.getWorldHeight()-figureBright.getRegionHeight();

                for(int k = 0; k<Math.abs(GameManager.INSTANCE.boardState[i][j]); k++) {
                    float stackOffset = k*figureBright.getRegionHeight();
                    float posYY = posY;
                    if(i > 0) posYY += stackOffset;
                    else posYY -= stackOffset;

                    final Figure tmpFigure;
                    if(GameManager.INSTANCE.boardState[i][j] < 0) {
                        tmpFigure = new Figure(figureDark, ColorEnum.DARK, i*GameManager.columns+j);
                    }
                    else tmpFigure = new Figure(figureBright, ColorEnum.BRIGHT, i*GameManager.columns);
                    tmpFigure.setOrigin(Align.center);
                    tmpFigure.setPosition(posX,posYY);
                    // add click event listener
                    tmpFigure.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // It will be an image for sure ;:^)
                            // Final Figure clickedFigure = (Figure) event.getTarget();
                            final Figure clickedFigure = (Figure) event.getListenerActor();


                            log.debug("clicked");
                        }
                    });
                    gameplayStage.addActor(tmpFigure);
                }
            }
        }
    }
}
