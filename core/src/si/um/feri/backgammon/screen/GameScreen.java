package si.um.feri.backgammon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import si.um.feri.backgammon.actors.Dice;
import si.um.feri.backgammon.actors.Figure;
import si.um.feri.backgammon.actors.FigureSide;
import si.um.feri.backgammon.assets.AssetDescriptors;
import si.um.feri.backgammon.assets.RegionNames;
import si.um.feri.backgammon.common.Bar;
import si.um.feri.backgammon.common.GameManager;
import si.um.feri.backgammon.config.GameConfig;
import si.um.feri.backgammon.enums.GameStateEnum;
import si.um.feri.backgammon.enums.OrientationEnum;
import si.um.feri.backgammon.actors.Spike;
import si.um.feri.backgammon.enums.ColorEnum;

public class GameScreen extends ScreenAdapter {
    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final Sound figureSound = Gdx.audio.newSound(Gdx.files.internal("sfx/ficha-de-ajedrez-34722.mp3"));
    private final Sound diceSound = Gdx.audio.newSound(Gdx.files.internal("sfx/rolling-dice-2-102706.mp3"));
    private final BackgammonGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private ColorEnum move = GameManager.INSTANCE.getInitMove();

    private TextButton rollBtn;
    private TextButton rematchBtn;

    private Label winnerLabel;

    public GameScreen(BackgammonGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        GameManager.INSTANCE.reset();
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
        if(GameManager.INSTANCE.switchFieldIndexes) {
            addSpikeIndexes();
        }
        addFigures();
        addRollBtn();
        addHUDBtns();

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
        figureSound.dispose();
        diceSound.dispose();
    }

    private void addHUDBtns() {
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
        rematchBtn = new TextButton("Rematch", skin);
        rematchBtn.setWidth(100);
        rematchBtn.pad(10);
        rematchBtn.setPosition(
                hudViewport.getWorldWidth()/2f - rematchBtn.getWidth()/2f,
                hudViewport.getWorldHeight()/2f - 4*rematchBtn.getHeight()/2f
        );
        rematchBtn.setVisible(false);
        rematchBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                winnerLabel.remove();
                GameManager.INSTANCE.reset();
                rematchBtn.addAction(Actions.fadeOut(.5f));
                move = GameManager.INSTANCE.getInitMove();
                rollBtn.setVisible(true);
                moveRollBtn();

                gameplayStage.clear();
                hudStage.clear();

                // Add elements to stage
                gameplayStage.addActor(createBoard());
                if(GameManager.INSTANCE.switchFieldIndexes) {
                    addSpikeIndexes();
                }
                addFigures();
                addRollBtn();
                addHUDBtns();
            }
        });
        hudStage.addActor(exitBtn);
        hudStage.addActor(rematchBtn);
    }

    private Actor createBoard() {
        final TextureRegion solidBackground = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        final TextureRegion storageBackground = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion barBackground = gameplayAtlas.findRegion(RegionNames.BAR);
        final TextureRegion branding = gameplayAtlas.findRegion(RegionNames.BRANDING);
        final TextureRegion spikeBright = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);
        final TextureRegion spikeDark = gameplayAtlas.findRegion(RegionNames.SPIKE_DARK);


        final Table outerTable = new Table();
        outerTable.setDebug(false);   // turn on all debug lines (table, cell, and widget)
        outerTable.setBackground(new TextureRegionDrawable(solidBackground));

        final Table gameTable = new Table();
        gameTable.setDebug(false);

        for (int i = 0; i < 5; i++) {
            if (i == 0 || i == 4) {
                final Table storageTable = new Table();
                storageTable.setDebug(false);
                storageTable.setBackground(new TextureRegionDrawable(storageBackground));
                gameTable.add(storageTable).width(storageBackground.getRegionWidth());
            } else if (i == 2) {
                final Table barTable = new Table();
                barTable.setDebug(false);
                barTable.setBackground(new TextureRegionDrawable(barBackground));
                gameTable.add(barTable).width(barBackground.getRegionWidth());
            } else {
                final Table quadrant = new Table();
                quadrant.setDebug(false);
                for (int row = 0; row < GameManager.ROWS; row++) {
                    for (int column = 0; column < GameManager.COLUMNS / 2; column++) {
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
                        diceArea.setDebug(false);
                        diceArea.setBackground(new TextureRegionDrawable(branding));

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

    private float getCenterLeft() {
        final TextureRegion storage = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion spike = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);
        return (storage.getRegionWidth() + 3*spike.getRegionWidth());
    }

    private float getCenterRight() {
        final TextureRegion storage = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion spike = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);
        return (viewport.getWorldWidth() - storage.getRegionWidth() - 3*spike.getRegionWidth());
    }

    private void addRollBtn() {
        rollBtn = new TextButton("Roll", skin);
        rollBtn.setTransform(true);
        rollBtn.setWidth(100);
        rollBtn.pad(10);

        // position (left | right) based on move
        moveRollBtn();

        rollBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //rollBtn.addAction(Actions.fadeOut(.5f));
                rollBtn.setVisible(false);
                throwTheDices();
            }
        });

        gameplayStage.addActor(rollBtn);
    }

    private void addSpikeIndexes() {
        final TextureRegion bar = gameplayAtlas.findRegion(RegionNames.BAR);
        final TextureRegion storage = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion spike = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);

        for(int i=0; i<GameManager.FIELD_COUNT; i++) {
            Label spikeIndexLabel = new Label(""+(i+1), skin);
            int horizontalOffset = i % GameManager.COLUMNS;
            float posX = (i > 11)
                    ? storage.getRegionWidth()+spike.getRegionWidth()/2f-spikeIndexLabel.getWidth()/2f + horizontalOffset*spike.getRegionWidth()
                    : viewport.getWorldWidth()-storage.getRegionWidth()-spike.getRegionWidth()/2f-spikeIndexLabel.getWidth()/2f-horizontalOffset*spike.getRegionWidth();
            if(horizontalOffset > 5) posX += ((i > 11) ? 1 : -1)*bar.getRegionWidth();
            float posY = spike.getRegionHeight();
            if(i > 11) posY = viewport.getWorldHeight() - posY - spikeIndexLabel.getHeight();
            spikeIndexLabel.setPosition(posX,posY);
            gameplayStage.addActor(spikeIndexLabel);
        }
    }

    private void addFigures() {
        final TextureRegion storage = gameplayAtlas.findRegion(RegionNames.STORAGE);
        final TextureRegion bar = gameplayAtlas.findRegion(RegionNames.BAR);

        final TextureRegion figureBright = gameplayAtlas.findRegion(RegionNames.FIGURE_BRIGHT);
        final TextureRegion figureDark = gameplayAtlas.findRegion(RegionNames.FIGURE_DARK);
        final TextureRegion figureSideBright = gameplayAtlas.findRegion(RegionNames.FIGURE_SIDE_BRIGHT);
        final TextureRegion figureSideDark = gameplayAtlas.findRegion(RegionNames.FIGURE_SIDE_DARK);

        final float horizontalOffset = storage.getRegionWidth();
        final float verticalBarOffset = figureBright.getRegionHeight()/2f;

        for(int i = 0; i<GameManager.FIELD_COUNT; i++) {
            int horizontalIndex = i % GameManager.COLUMNS;
            float posY = (i > 11) ? viewport.getWorldHeight()-figureBright.getRegionHeight() : 0;
            float posX = (i > 11)
                ? horizontalOffset + (horizontalIndex*figureBright.getRegionWidth())
                : viewport.getWorldWidth() - horizontalOffset - ((horizontalIndex+1)*figureBright.getRegionWidth());
            if(i < 12 && horizontalIndex > 5) posX -= bar.getRegionWidth();
            else if(i > 11 && horizontalIndex > 5) posX += bar.getRegionWidth();

            for(int k = 0; k<Math.abs(GameManager.INSTANCE.boardState[i]); k++) {
                float posYY = posY;
                if(i < 12) posYY += k*figureBright.getRegionHeight();
                else posYY -= k*figureBright.getRegionHeight();

                final Figure tmpFigure;
                if(GameManager.INSTANCE.boardState[i] < 0) {
                    tmpFigure = new Figure(figureDark, ColorEnum.DARK, i, k);
                    //log.debug("Figure index: "+i + " (dark)");
                }
                else {
                    tmpFigure = new Figure(figureBright, ColorEnum.BRIGHT, i, k);
                    //log.debug("Figure index: "+i + " (bright)");
                }
                tmpFigure.setOrigin(Align.center);
                tmpFigure.setPosition(posX,posYY);
                // add click event listener
                tmpFigure.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(GameManager.GAME_STATE == GameStateEnum.DONE) return;
                        // It will be an image for sure ;:^)
                        // Final Figure clickedFigure = (Figure) event.getTarget();
                        final Figure clickedFigure = (Figure) event.getListenerActor();
                        ColorEnum figureColor = clickedFigure.getFigureColor();
                        if(figureColor != move || !GameManager.DID_ROLL) return;

                        /*
                        log.debug(".............");
                        for(Integer rv : GameManager.INSTANCE.rollValues) {
                            log.debug(rv+"");
                        }
                        log.debug(".............");
                         */

                        if(!GameManager.INSTANCE.rollValues.isEmpty()) {
                            int moveValue = GameManager.INSTANCE.rollValues.first();
                            //log.debug("moveValue: "+moveValue);
                            moveValue *= (figureColor == ColorEnum.DARK) ? (-1): 1;

                            int fieldIndex = clickedFigure.getFieldIndex();
                            //log.debug("fieldIndex: "+fieldIndex);
                            int fieldIndexUpdate = fieldIndex + moveValue;
                            //log.debug("fieldIndexUpdate: "+fieldIndexUpdate);


                            // prevent moving figures (not) on top
                            if(fieldIndex >= 0 && fieldIndex < GameManager.FIELD_COUNT &&
                                ((Math.abs(GameManager.INSTANCE.boardState[fieldIndex]) >= Figure.STACK_LIMIT && clickedFigure.getStackIndex() < Figure.STACK_LIMIT-1) ||
                                (Math.abs(GameManager.INSTANCE.boardState[fieldIndex]) < Figure.STACK_LIMIT && clickedFigure.getStackIndex() < Math.abs(GameManager.INSTANCE.boardState[fieldIndex])-1) ||
                                    figureColor == move && move == ColorEnum.BRIGHT && Bar.FIGURE_COUNT_BRIGHT > 0 ||
                                    figureColor == move && move == ColorEnum.DARK && Bar.FIGURE_COUNT_DARK > 0)) {
                                return;
                            }
                            // store figures off board
                            if((fieldIndexUpdate < 0 || fieldIndexUpdate >= GameManager.FIELD_COUNT)) {
                                if(canStoreFigures(figureColor) && (fieldIndexUpdate == -1 ||
                                    fieldIndexUpdate == GameManager.FIELD_COUNT ||
                                    !canStoreHigherValue(figureColor,fieldIndex,Math.abs(moveValue)))) {

                                    // play sound
                                    if(GameManager.INSTANCE.switchSoundEffects) figureSound.play();
                                    GameManager.INSTANCE.boardState[fieldIndex] += (figureColor == ColorEnum.BRIGHT) ? (-1): 1;

                                    float xOffset = viewport.getWorldWidth()-figureSideBright.getRegionWidth()-85;
                                    float yOffset = 60;
                                    if(figureColor == ColorEnum.BRIGHT) {
                                        yOffset = viewport.getWorldHeight() - yOffset - (GameManager.FIGURES_HOME_BRIGHT+1)*figureSideBright.getRegionHeight();
                                        FigureSide tmpSide = new FigureSide(figureSideBright);
                                        tmpSide.setPosition(xOffset, yOffset);
                                        gameplayStage.addActor(tmpSide);
                                        GameManager.FIGURES_HOME_BRIGHT++;
                                    }
                                    else {
                                        yOffset += GameManager.FIGURES_HOME_DARK*figureSideBright.getRegionHeight();
                                        FigureSide tmpSide = new FigureSide(figureSideDark);
                                        tmpSide.setPosition(xOffset, yOffset);
                                        gameplayStage.addActor(tmpSide);
                                        GameManager.FIGURES_HOME_DARK++;
                                    }
                                    clickedFigure.remove();
                                    // remove dice with same move Value and smallest posX
                                    removeDice(GameManager.INSTANCE.rollValues.removeIndex(0));

                                    // check if the game has concluded
                                    if(isGameDone(figureColor)) {
                                        removeDices();
                                        log.debug("Game has concluded");
                                        GameManager.GAME_STATE = GameStateEnum.DONE;
                                        winnerLabel = new Label(clickedFigure.getFigureColor().name() + " figures won.", skin);
                                        winnerLabel.setScale(2f);
                                        winnerLabel.setPosition(
                                                hudViewport.getWorldWidth()/2f - winnerLabel.getWidth()/2f,
                                                hudViewport.getWorldHeight()/2f - winnerLabel.getHeight()/2f
                                        );
                                        hudStage.addActor(winnerLabel);
                                        // update leaderboard
                                            //int oldWins = //GameManager.INSTANCE.leaderboard.get()
                                            //GameManager.INSTANCE.leaderboard.put(GameManager.USER_1)
                                        // display rematch button
                                        rollBtn.setVisible(false);
                                        rematchBtn.setVisible(true);
                                        rematchBtn.addAction(Actions.fadeIn(0.5f));
                                    }
                                    else if(GameManager.INSTANCE.rollValues.isEmpty() || !canMakeMove()) {
                                        endMove();
                                    }
                                    return;
                                }
                                else return;
                            }
                            // invalid moves
                            else if(fieldIndexUpdate < 0 || fieldIndexUpdate >= GameManager.FIELD_COUNT ||
                                    figureColor == ColorEnum.BRIGHT && GameManager.INSTANCE.boardState[fieldIndexUpdate] < -1 ||
                                    figureColor == ColorEnum.DARK && GameManager.INSTANCE.boardState[fieldIndexUpdate] > 1) {
                                //log.debug("Invalid move detected");
                                return;
                            }


                            // move enemy figure to the bar
                            if(figureColor == ColorEnum.DARK && GameManager.INSTANCE.boardState[fieldIndexUpdate] == 1 ||
                                    figureColor == ColorEnum.BRIGHT && GameManager.INSTANCE.boardState[fieldIndexUpdate] == -1) {
                                // finding and moving the enemy figure
                                for (Actor ac : gameplayStage.getActors()) {
                                    if (ac instanceof Figure) {
                                        Figure f = (Figure) ac;
                                        ColorEnum fColor = f.getFigureColor();
                                        if (f.getFieldIndex() == fieldIndexUpdate) {
                                            f.setStackIndex(0);
                                            f.setFieldIndex((fColor == ColorEnum.BRIGHT)
                                                    ? Bar.BAR_INDEX_BRIGHT
                                                    : Bar.BAR_INDEX_DARK);
                                            f.setPosition(
                                                    viewport.getWorldWidth() / 2f - figureBright.getRegionWidth() / 2f,
                                                    ((fColor == ColorEnum.DARK)
                                                            ? verticalBarOffset
                                                            : viewport.getWorldHeight() - figureBright.getRegionHeight() - verticalBarOffset)
                                            );
                                            // play sound
                                            if(GameManager.INSTANCE.switchSoundEffects) figureSound.play();
                                            log.debug(fColor.name() + " figure sent to bar. Index: " + f.getFieldIndex());
                                            GameManager.INSTANCE.boardState[fieldIndexUpdate] -= (fColor == ColorEnum.BRIGHT) ? 1 : (-1);
                                            if(fColor == ColorEnum.BRIGHT)
                                                Bar.FIGURE_COUNT_BRIGHT++;
                                            else Bar.FIGURE_COUNT_DARK++;
                                            break;
                                        }
                                    }
                                }
                            }

                            // stack index
                            int stackIndexUpdate = Figure.STACK_LIMIT -1;
                            if(Math.abs(GameManager.INSTANCE.boardState[fieldIndexUpdate]) < Figure.STACK_LIMIT) {
                                stackIndexUpdate = Math.abs(GameManager.INSTANCE.boardState[fieldIndexUpdate]);
                            }

                            // update new field
                            if(fieldIndex > Bar.BAR_INDEX_BRIGHT && fieldIndex < GameManager.FIELD_COUNT) {
                                GameManager.INSTANCE.boardState[fieldIndex] += (figureColor == ColorEnum.BRIGHT) ? (-1): 1;
                            }
                            else {
                                if(figureColor == ColorEnum.BRIGHT)
                                    Bar.FIGURE_COUNT_BRIGHT--;
                                else Bar.FIGURE_COUNT_DARK--;
                            }
                            GameManager.INSTANCE.boardState[fieldIndexUpdate] += (figureColor == ColorEnum.BRIGHT) ? 1: (-1);

                            // calculate figure position
                            int horizontalIndex = fieldIndexUpdate % GameManager.COLUMNS;
                            float posY = (fieldIndexUpdate > 11) ? viewport.getWorldHeight()-figureBright.getRegionHeight() : 0;
                            float posX = (fieldIndexUpdate > 11)
                                    ? horizontalOffset + (horizontalIndex*figureBright.getRegionWidth())
                                    : viewport.getWorldWidth() - horizontalOffset - ((horizontalIndex+1)*figureBright.getRegionWidth());
                            if(fieldIndexUpdate < 12 && horizontalIndex > 5) posX -= bar.getRegionWidth();
                            else if(fieldIndexUpdate > 11 && horizontalIndex > 5) posX += bar.getRegionWidth();

                            // calculate vertical position in stack
                            posY += ((posY < viewport.getWorldHeight()/2f) ? 1 : -1) *stackIndexUpdate*figureBright.getRegionHeight();
                            // update position
                            clickedFigure.setPosition(posX, posY);
                            // play sound
                            if(GameManager.INSTANCE.switchSoundEffects) figureSound.play();
                            // update figure fieldIndex
                            clickedFigure.setFieldIndex(fieldIndexUpdate);
                            clickedFigure.setStackIndex(stackIndexUpdate);

                            // remove dice with same move Value and smallest posX
                            removeDice(GameManager.INSTANCE.rollValues.removeIndex(0));
                        }

                        // check if any moves left
                        if(GameManager.INSTANCE.rollValues.isEmpty() || !canMakeMove()) {
                            endMove();
                        }
                        log.debug("Figure clicked");
                    }
                });
                gameplayStage.addActor(tmpFigure);
            }
        }
    }

    private boolean isGameDone(ColorEnum color) {
        for (Actor ac : gameplayStage.getActors()) {
            if(ac instanceof Figure) {
                if(((Figure) ac).getFigureColor() == color) return false;
            }
        }
        return true;
    }

    private boolean canStoreFigures(ColorEnum clickedColor) {
        if(clickedColor != move) return false;
        for (Actor ac : gameplayStage.getActors()) {
            if (ac instanceof Figure) {
                Figure f = (Figure) ac;
                ColorEnum fColor = f.getFigureColor();
                int fIndex = f.getFieldIndex();
                if (fColor == clickedColor) {
                    if (fColor == ColorEnum.BRIGHT && fIndex < GameManager.FIELD_COUNT - GameManager.COLUMNS / 2)
                        return false;
                    else if (fColor == ColorEnum.DARK && fIndex > GameManager.COLUMNS / 2 - 1)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean canStoreHigherValue(ColorEnum clickedColor, int fieldIndex, int absValue) {
        //log.debug("absValue: "+absValue+"\tfieldIndex: "+fieldIndex);
        if(clickedColor == ColorEnum.BRIGHT) {
            for(int i=GameManager.FIELD_COUNT-GameManager.COLUMNS/2; i<fieldIndex; i++) {
                if(GameManager.INSTANCE.boardState[i] > 0) return true;
            }
        }
        else {
            for(int i=GameManager.COLUMNS/2-1; i>fieldIndex; i--) {
                if(GameManager.INSTANCE.boardState[i] < 0) return true;
            }
        }
        return false;
    }

    private void displayDices() {
        removeDices();

        final TextureRegion spike = gameplayAtlas.findRegion(RegionNames.SPIKE_BRIGHT);
        final TextureRegion dice1 = gameplayAtlas.findRegion(RegionNames.DICE_1);
        final TextureRegion dice2 = gameplayAtlas.findRegion(RegionNames.DICE_2);
        final TextureRegion dice3 = gameplayAtlas.findRegion(RegionNames.DICE_3);
        final TextureRegion dice4 = gameplayAtlas.findRegion(RegionNames.DICE_4);
        final TextureRegion dice5 = gameplayAtlas.findRegion(RegionNames.DICE_5);
        final TextureRegion dice6 = gameplayAtlas.findRegion(RegionNames.DICE_6);

        final float posY = hudViewport.getWorldHeight()/2f - dice1.getRegionHeight()/2f;
        final float xOffset = spike.getRegionWidth();
        final float baseWidth = dice1.getRegionWidth();
        float basePosX = ((move == ColorEnum.DARK)
                ? getCenterRight()
                : getCenterLeft()) - baseWidth/2f -3*xOffset/2f;
        if(!GameManager.DOUBLED) {
            basePosX += xOffset;
        }

        int i = 0;
        for(Integer rv : GameManager.INSTANCE.rollValues) {
            final Dice tmpDice;
            switch (rv) {
                case 2: {
                    tmpDice = new Dice(dice2, rv);
                    break;
                }
                case 3: {
                    tmpDice = new Dice(dice3, rv);
                    break;
                }
                case 4: {
                    tmpDice = new Dice(dice4, rv);
                    break;
                }
                case 5: {
                    tmpDice = new Dice(dice5, rv);
                    break;
                }
                case 6: {
                    tmpDice = new Dice(dice6, rv);
                    break;
                }
                default: {
                    tmpDice = new Dice(dice1, rv);
                    break;
                }
            }
            tmpDice.setPosition((basePosX + i*xOffset), posY);
            tmpDice.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!GameManager.DOUBLED && GameManager.INSTANCE.rollValues.size == 2) {
                        for(int i=0; i<gameplayStage.getActors().size; i++) {
                            if(gameplayStage.getActors().items[i] instanceof Dice)
                                gameplayStage.getActors().items[i].remove();
                        }

                        int tmp = GameManager.INSTANCE.rollValues.get(0);
                        GameManager.INSTANCE.rollValues.set(0,GameManager.INSTANCE.rollValues.get(1));
                        GameManager.INSTANCE.rollValues.set(1, tmp);
                        displayDices();
                    }
                }
            });
            gameplayStage.addActor(tmpDice);
            i++;
        }
    }

    private boolean canMakeMove() {
        if(GameManager.INSTANCE.rollValues.isEmpty()) return false;
        boolean p1 = false;
        boolean p2 = false;

        int size = GameManager.INSTANCE.rollValues.size;
        //log.debug("rollValues.size: "+size);

        int move1 = GameManager.INSTANCE.rollValues.get(0);
        int move2 = (size > 1 && !GameManager.DOUBLED)
                ? GameManager.INSTANCE.rollValues.get(1) : 0;

        for (int i=0; i < gameplayStage.getActors().size; i++) {
            if(gameplayStage.getActors().items[i] instanceof Figure) {
                Figure f = (Figure) gameplayStage.getActors().items[i];
                int fIndex = f.getFieldIndex();
                ColorEnum fColor = f.getFigureColor();
                if(fColor == move) {
                    // check if the bar is clear
                    if(fColor == ColorEnum.DARK && Bar.FIGURE_COUNT_DARK > 0) {
                        if(GameManager.INSTANCE.boardState[Bar.BAR_INDEX_DARK-move1] <= 1) return true;
                        if(size > 1 && GameManager.INSTANCE.boardState[Bar.BAR_INDEX_DARK-move2] <= 1)
                            p2 = true;
                    }
                    else if(fColor == ColorEnum.BRIGHT && Bar.FIGURE_COUNT_BRIGHT > 0) {
                        if(GameManager.INSTANCE.boardState[move1-1] >= -1) return true;
                        if(size > 1 && GameManager.INSTANCE.boardState[move2-1] >= -1)
                            p2 = true;
                    }
                    // calculate next index
                    else if(fColor == ColorEnum.BRIGHT && Bar.FIGURE_COUNT_BRIGHT == 0 ||
                        fColor == ColorEnum.DARK && Bar.FIGURE_COUNT_DARK == 0) {
                        int fIndexNext1 = fIndex + ((fColor == ColorEnum.BRIGHT) ? 1 : -1)*move1;
                        if((canStoreFigures(fColor) && (fIndexNext1 < 0 || fIndexNext1 >= GameManager.FIELD_COUNT)) ||
                            (fColor == ColorEnum.BRIGHT && fIndexNext1 >= 0 && fIndexNext1 < GameManager.FIELD_COUNT && GameManager.INSTANCE.boardState[fIndexNext1] >= -1) ||
                            (fColor == ColorEnum.DARK && fIndexNext1 >= 0 && fIndexNext1 < GameManager.FIELD_COUNT && GameManager.INSTANCE.boardState[fIndexNext1] <= 1)) {
                            return true;
                        }
                        // calculate 2nd index if there's more than 1 dice and there was no double
                        if(!GameManager.DOUBLED && move2 > 0) {
                            int fIndexNext2 = fIndex + ((fColor == ColorEnum.BRIGHT) ? 1 : -1)*move2;
                            if((canStoreFigures(fColor) && (fIndexNext2 < 0 || fIndexNext2 >= GameManager.FIELD_COUNT)) ||
                                    (fColor == ColorEnum.BRIGHT && fIndexNext2 >= 0 && fIndexNext2 < GameManager.FIELD_COUNT && GameManager.INSTANCE.boardState[fIndexNext2] >= -1) ||
                                    (fColor == ColorEnum.DARK && fIndexNext2 >= 0 && fIndexNext2 < GameManager.FIELD_COUNT && GameManager.INSTANCE.boardState[fIndexNext2] <= 1)) {
                                p2 = true;
                            }
                        }
                    }
                }
            }
        }

        if(!p1 && p2) {
            int tmp = GameManager.INSTANCE.rollValues.get(0);
            GameManager.INSTANCE.rollValues.set(0,GameManager.INSTANCE.rollValues.get(1));
            GameManager.INSTANCE.rollValues.set(1, tmp);
            return p2;
        }
        return false;
    }

    private void throwTheDices() {
        GameManager.INSTANCE.roll();
        if(GameManager.INSTANCE.switchSoundEffects) {
            diceSound.play();
        }
        /*
        log.debug("Roll-------|");
        for(int i=0; i<GameManager.INSTANCE.rollValues.size; i++) {
            log.debug(""+GameManager.INSTANCE.rollValues.get(i));
        }
        log.debug("---------");
         */
        if(!canMakeMove()) {
            endMove();
        }
        else {
            displayDices();
        }
    }

    private void removeDice(int diceValue) {
        boolean none = true;
        Dice lowestPosXDice = null;
        float lowestPosX = viewport.getWorldWidth();
        for(int i=0; i<gameplayStage.getActors().size; i++) {
            if(gameplayStage.getActors().items[i] instanceof Dice &&
                ((Dice)gameplayStage.getActors().items[i]).getValue() == diceValue) {
                if(none) {
                    lowestPosXDice = (Dice)gameplayStage.getActors().items[i];
                    lowestPosX = gameplayStage.getActors().items[i].getX();
                    none = false;
                    continue;
                }
                float tmpPosX = gameplayStage.getActors().items[i].getX();
                if(tmpPosX < lowestPosX) {
                    lowestPosXDice = (Dice)gameplayStage.getActors().items[i];
                    lowestPosX = tmpPosX;
                }
            }
        }
        if(lowestPosXDice != null) {
            lowestPosXDice.remove();
        }
    }

    private void endMove() {
        removeDices();
        if(move == ColorEnum.BRIGHT) {
            move = ColorEnum.DARK;
        }
        else move = ColorEnum.BRIGHT;
        moveRollBtn();
        GameManager.DID_ROLL = false;
    }

    private void moveRollBtn() {
        float posX = getCenterLeft() - rollBtn.getWidth()/2f;
        final float posY = hudViewport.getWorldHeight()/2f - rollBtn.getHeight()/2f;
        if(move == ColorEnum.DARK) {
            posX = getCenterRight() - rollBtn.getWidth()/2f;
        }
        rollBtn.setPosition(posX, posY);
        if(GameManager.GAME_STATE == GameStateEnum.RUNNING)
            rollBtn.setVisible(true);
        rollBtn.addAction(Actions.fadeIn(0.5f));
    }

    private void removeDices() {
        for(int i=0; i<gameplayStage.getActors().size; i++) {
            if(gameplayStage.getActors().items[i] instanceof Dice)
                gameplayStage.getActors().items[i].remove();
        }
    }
}
