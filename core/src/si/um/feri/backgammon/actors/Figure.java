package si.um.feri.backgammon.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import si.um.feri.backgammon.enums.ColorEnum;

public class Figure extends Image {
    public static final int STACK_LIMIT = 5;

    private final ColorEnum color;
    private int fieldIndex;
    private int stackIndex = 0;

    public Figure(TextureRegion region, ColorEnum color, int position, int stackIndex) {
        super(region);
        this.color = color;
        this.fieldIndex = position;
        this.stackIndex = stackIndex;
    }

    // get
    public int getFieldIndex() {
        return fieldIndex;
    }
    public int getStackIndex() {
        return stackIndex;
    }
    public ColorEnum getFigureColor() {
        return color;
    }
    // set
    public void setFieldIndex(int index) {
        fieldIndex=index;
    }
    public void setStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
    }
}
