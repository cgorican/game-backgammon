package si.um.feri.backgammon.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Dice extends Image {
    private final int value;
    private boolean used;

    public Dice(TextureRegion region, int rollValue) {
        super(region);
        this.value = rollValue;
    }

    // get
    public boolean getUsed() {
        return used;
    }
    public int getValue() {
        return value;
    }
    // set
    public void setUsed() {
        used=true;
    }
}
