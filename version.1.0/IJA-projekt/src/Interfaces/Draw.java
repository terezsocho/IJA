/*
 * Authors: Terézia Sochova(xsocho14), Adrián Piaček(xpiace00)
 * Source code Draw.java contains method declarations used to gather object to be drawn onto canvas.
 */
package Interfaces;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import java.util.List;

public interface Draw {
    List<Shape> getGUI();
    String getId();

}
