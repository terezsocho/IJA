package sources;

import sources.Coordinate;

import java.util.List;

public class Path {

    private List<Coordinate> path;
    public Path(List<Coordinate> path) {
        this.path = path;
    }

    /**
     * Method calculates distance needed to be travelled between two specified coordinates in meters.
     * @param a Coordinate of starting position for calculation of distance
     * @param b Coordinate of end position for calculation of distance
     * @return Double value of meters to travel between two coordinates
     */
    private double getDistance(Coordinate a, Coordinate b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
    }

    /**
     * Method returns List of Coordinates for specific busline to be travelled upon.
     * @return List of Coordinates
     */
    public List<Coordinate> getPathCoord() { return path;}

    /**
     *  Method calculates a current coordinate of a bus once new distance that was travelled is added.
     * @param distance double value representing value in meters that bus travelled
     * @return Coordinate of new bus position.
     */
    public Coordinate getCoorBus(double distance) {

        double len = 0;
        Coordinate a = null;
        Coordinate b = null;
        for (int i = 0; i < path.size() - 1; i++) {
            a = path.get(i);
            b = path.get(i + 1);
            if (len + getDistance(a, b) >= distance) {
                break;
            }
            len += getDistance(a, b);
        }
        if (a == null || b == null) {
            return null;
        }
        double driven = (distance - len) / getDistance(a, b);
        return new Coordinate(a.getX() + (b.getX() - a.getX()) * driven, a.getY() + (b.getY() - a.getY()) * driven);
    }

    /**
     * Method calculates distance that bus on a specific busline travels everytime its on a road.
     * @return double value in meters representing lengths of whole route
     */
    public double getPathSize() {
        double size = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            size = size + getDistance(path.get(i), path.get(i + 1));
        }
        return size;
    }
}
