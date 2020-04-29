package sources;

import java.util.Objects;

public class Coordinate{
    private double x;
    private double y;

    public Coordinate (double x, double y){
        this.x = x;
        this.y = y;

    }

    public double getX(){
        return this.x;
    }

    public double getY(){

        return this.y;
    }
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

    /*public int diffY(sources.Coordinate c){
        int resultY = this.y - c.getY();
        return resultY;
    }

    public int diffX(sources.Coordinate c){
        int resultX = this.x - c.getX();
        return resultX;
    }*/

    /*@Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof sources.Coordinate)) {
            return false;
        }

        sources.Coordinate coor = (sources.Coordinate) o;

        return this.x == coor.getX() && this.y == coor.getY();

    }*/
}
