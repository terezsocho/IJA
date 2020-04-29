package Interfaces;
import javafx.scene.shape.Shape;
import sources.Coordinate;
import sources.Stop;

import java.util.List;


public interface LineInfo {
    List<sources.Coordinate> getLinePath();
    List<Stop> getLinePathStops();
}
