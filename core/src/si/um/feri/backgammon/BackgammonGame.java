package si.um.feri.backgammon;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

import si.um.feri.backgammon.common.GameManager;
import si.um.feri.backgammon.screen.GameScreen;
import si.um.feri.backgammon.screen.IntroScreen;

public class BackgammonGame extends Game {
	private AssetManager assetManager;
	private SpriteBatch batch;
	public Music music;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Logger.DEBUG);

		batch = new SpriteBatch();

		music = Gdx.audio.newMusic(Gdx.files.internal("music/lifelike-126735.mp3"));
		music.setLooping(true);
		if(GameManager.INSTANCE.getMusicSwitch()) {
			music.setVolume(.8f);
			music.play();
		}
		setScreen(new IntroScreen(this));
	}

	@Override
	public Screen getScreen() {
		return super.getScreen();
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	@Override
	public void dispose () {
		assetManager.dispose();
		batch.dispose();
		music.dispose();
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
