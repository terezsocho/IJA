package Interfaces;
import Sources.Coordinate;
import Sources.Stop;

import java.util.List;


public interface LineInfo {
    List<Coordinate> getLinePath();
    List<Stop> getLinePathStops();
}
