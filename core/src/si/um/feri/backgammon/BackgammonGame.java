package si.um.feri.backgammon;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

import si.um.feri.backgammon.screen.IntroScreen;

public class BackgammonGame extends Game {
	private AssetManager assetManager;
	private SpriteBatch batch;

	private final String[] nicknames = {"bob", "ckf", "crt", "lajgotm", "blitz", "wHzZ"};
	public HashMap<String, Integer> leaderboard = new HashMap<String, Integer>();
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Logger.DEBUG);

		batch = new SpriteBatch();

		setScreen(new IntroScreen(this));

		// HARDCODED LEADERBOARD
		for (int i = 0; i < MathUtils.random(3, nicknames.length); i++) {
			leaderboard.put(nicknames[i], MathUtils.random(0,10));
		}
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
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
