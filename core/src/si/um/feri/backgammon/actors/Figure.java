package si.um.feri.backgammon.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import si.um.feri.backgammon.enums.ColorEnum;

public class Figure extends Image {
    private final ColorEnum color;
    private int fieldIndex;

    public Figure(TextureRegion region, ColorEnum color, int position) {
        super(region);
        this.color = color;
        this.fieldIndex = position;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }
    public ColorEnum getFigureColor() {
        return color;
    }
}
