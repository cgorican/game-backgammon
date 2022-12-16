package si.um.feri.backgammon.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.backgammon.BackgammonGame;
import si.um.feri.backgammon.assets.AssetDescriptors;
import si.um.feri.backgammon.assets.RegionNames;
import si.um.feri.backgammon.common.GameManager;
import si.um.feri.backgammon.config.GameConfig;
import si.um.feri.backgammon.enums.ColorEnum;

public class SettingsScreen extends ScreenAdapter {
    private final BackgammonGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public SettingsScreen(BackgammonGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        stage.addActor(createSettings());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.f, 0.f, 0.f, 0);

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

    private Actor createSettings() {
        Table table = new Table();
        table.defaults().pad(20);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // Inner table
        Table contentTable = new Table();
        contentTable.defaults().pad(10);
        contentTable.setDebug(false);

        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.MENU_BACKGROUND);
        contentTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));

        // Apply items to inner table
        Label settingsLabel = new Label("Settings", skin);
        settingsLabel.setAlignment(Align.center);
        contentTable.add(settingsLabel)
                .align(Align.center)
                .colspan(3)
                .expandX()
                .fillX()
                .row();

        // usernames
        final TextField tfDark = new TextField("", skin);
        final TextField tfBright = new TextField("", skin);
        tfDark.setText(GameManager.INSTANCE.usernameDark);
        tfBright.setText(GameManager.INSTANCE.usernameBright);

        contentTable.add(new Label("Dark: ", skin))
                .align(Align.left)
                .expandX()
                .fillX();
        contentTable.add(tfDark)
                .align(Align.left)
                .colspan(2)
                .expandX()
                .fillX()
                .row();
        contentTable.add(new Label("Bright: ", skin))
                .align(Align.left)
                .expandX()
                .fillX();
        contentTable.add(tfBright)
                .align(Align.left)
                .colspan(2)
                .expandX()
                .fillX()
                .row();

        // field indexes
        final CheckBox spikeIndexCheckBox = new CheckBox("Display field indexes", skin);
        spikeIndexCheckBox.setChecked(GameManager.INSTANCE.getFieldIndexesSwitch());
        spikeIndexCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox cb = (CheckBox) event.getListenerActor();
                if (cb == spikeIndexCheckBox) {
                    spikeIndexCheckBox.setChecked(cb.isChecked());
                    GameManager.INSTANCE.setFieldIndexes(cb.isChecked());
                }
                Gdx.graphics.setContinuousRendering(spikeIndexCheckBox.isChecked());
            }
        });
        contentTable.add(new Label("",skin));
        contentTable.add(spikeIndexCheckBox)
                .align(Align.left)
                .colspan(2)
                .expandX()
                .fillX()
                .row();

        // initial move
        final CheckBox cbBright = new CheckBox(ColorEnum.BRIGHT.name(), skin);
        final CheckBox cbDark = new CheckBox(ColorEnum.DARK.name(), skin);
        final ButtonGroup<CheckBox> cbInitMoveGroup = new ButtonGroup<>(cbBright, cbDark);
        cbInitMoveGroup.setChecked(GameManager.INSTANCE.getInitMove().name());

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checked = cbInitMoveGroup.getChecked();
                if (checked == cbBright) {
                    GameManager.INSTANCE.setInitMove(ColorEnum.BRIGHT);
                } else if (checked == cbDark) {
                    GameManager.INSTANCE.setInitMove(ColorEnum.DARK);
                }
            }
        };
        cbBright.addListener(listener);
        cbDark.addListener(listener);

        contentTable.add(new Label("Initial move:", skin))
                .align(Align.left)
                .expandX()
                .fillX();
        contentTable.add(cbBright)
                .align(Align.left)
                .expandX()
                .fillX();
        contentTable.add(cbDark)
                .align(Align.left)
                .expandX()
                .fillX()
                .row();

        final CheckBox musicCheckBox = new CheckBox("Music", skin);
        musicCheckBox.setChecked(GameManager.INSTANCE.getMusicSwitch());
        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox cb = (CheckBox) event.getListenerActor();
                if (cb == musicCheckBox) {
                    musicCheckBox.setChecked(cb.isChecked());
                    GameManager.INSTANCE.setMusicSwitch(cb.isChecked());
                    if(cb.isChecked() && !game.music.isPlaying()) {
                        game.music.play();
                    }
                    else if(!cb.isChecked() && game.music.isPlaying()) {
                        game.music.stop();
                    }
                }
                Gdx.graphics.setContinuousRendering(musicCheckBox.isChecked());
            }
        });

        final CheckBox sfxCheckBox = new CheckBox("SFX", skin);
        sfxCheckBox.setChecked(GameManager.INSTANCE.getSoundEffectsSwitch());
        sfxCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox cb = (CheckBox) event.getListenerActor();
                if (cb == sfxCheckBox) {
                    sfxCheckBox.setChecked(cb.isChecked());
                    GameManager.INSTANCE.setSoundEffectsSwitch(cb.isChecked());
                }
                Gdx.graphics.setContinuousRendering(sfxCheckBox.isChecked());
            }
        });
        contentTable.add(new Label("Sound:", skin));
        contentTable.add(musicCheckBox)
                .align(Align.left)
                .expandX()
                .fillX();
        contentTable.add(sfxCheckBox)
                .align(Align.left)
                .expandX()
                .fillX()
                .row();

        // back btn
        TextButton backBtn = new TextButton("Back", skin);
        backBtn.setTransform(true);
        backBtn.setScale(1f,1.2f);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.INSTANCE.usernameDark = tfDark.getText();
                GameManager.INSTANCE.usernameBright = tfBright.getText();
                game.setScreen(new MenuScreen(game));
            }
        });
        contentTable.add(backBtn)
                .padTop(20)
                .padBottom(20)
                .colspan(3)
                .expandX()
                .fillX()
                .row();

        // Apply inner table to global table
        contentTable.center();

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
