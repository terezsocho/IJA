package Sources;

import java.util.Objects;

public class Coordinate{
    private double x;
    private double y;
    /**
     * Constructor for Coordinate object.
     * @param x x coordinate
     * @param y y coordinate
     */
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

    /**
     * Method sets value of coordinate x
     * @param x value of x that should be set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Method sets value of coordinate x
     * @param y value of x that should be set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Overriding equals method
     * @param o object that is compared
     * @return boolean value if object is the same or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    /**
     * Overriding hashCode method
     * @return value of integer obtained in hash function
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Overriding toString method
     * @return value of string that contains x and y value
     */
    @Override
    public String toString() {
        return  "x=" + x +", y=" + y;
    }
}
