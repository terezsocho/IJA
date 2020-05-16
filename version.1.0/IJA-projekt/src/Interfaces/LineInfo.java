/*
 * Authors: Terézia Sochova(xsocho14), Adrián Piaček(xpiace00)
 * Source code LineInfo.java contains method declarations used for specific bus route highlights.
 */
package Interfaces;
import Sources.Coordinate;
import Sources.Stop;

import java.util.List;


public interface LineInfo {
    List<Coordinate> getLinePath();
    List<Stop> getLinePathStops();
}
