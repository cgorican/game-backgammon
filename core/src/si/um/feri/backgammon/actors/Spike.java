package si.um.feri.backgammon.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import si.um.feri.backgammon.enums.OrientationEnum;

public class Spike extends Image {

    public Spike(TextureRegion region) {
        super(region);
    }

    public Spike(TextureRegion region, OrientationEnum orientation) {
        super(region);
        if(orientation == OrientationEnum.DOWN) {
            setOrigin(Align.center);
            setRotation(180);
        }
    }

    public void setDrawable(TextureRegion region) {
        super.setDrawable(new TextureRegionDrawable(region));
    }

}
