package sources;

import java.util.Objects;

public class Coordinate{
    private double x;
    private double y;

    public Coordinate (double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Method returns x value of coordinate
     * @return double value of x coordinate
     */
    public double getX(){
        return this.x;
    }

    /**
     * Method returns y value of coordinate
     * @return double value of y coordinate
     */
    public double getY(){  return this.y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return  "x=" + x +", y=" + y;
    }
}
