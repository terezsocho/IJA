package Interfaces;
import javafx.scene.shape.Shape;
import sources.Stop;

import java.util.List;


public interface LineInfo {
    List<Shape> getLineSchedule();
    List<sources.Coordinate> getLinePath();
    List<Stop> getLinePathStops();
}
