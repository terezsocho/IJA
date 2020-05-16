package Sources;

import Interfaces.Draw;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import java.util.Arrays;
import java.util.List;

public class Street implements Draw {

    private Coordinate start_coord;// point where street starts
    private Coordinate end_coord;// point were street ends
    private String id;// name of street

    /**
     * Constructor for Stop element.
     * @param id name of street (unique)
     * @param start coordinates where street begins
     * @param stop coordinates where street ends
     */
    public Street(String id, Coordinate start, Coordinate stop) {
        this.id = id;
        this.start_coord = start;
        this.end_coord = stop;
    }

    /**
     * Method returns name of street.
     * @return string value of name street
     */
    public String getId() {
        return id;
    }

    /**
     * Method returns coordinates where street starts.
     * @return coordinate object of street beginning
     */
    public Coordinate get_Start_coord() {
        return start_coord;
    }

    /**
     * Method returns coordinates where street ends.
     * @return coordinate object of street ending
     */
    public Coordinate get_End_coord() {
        return end_coord;
    }

    /**
     * Method returns list of shapes that defines the street.
     * It is text containing name of street and line that represents actual street.
     * @return list of shape for representing the street
     */
    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                new Text((start_coord.getX() + end_coord.getX()) / 2, (start_coord.getY() + end_coord.getY())/ 2 , id),
                new Line(start_coord.getX(), start_coord.getY(), end_coord.getX(), end_coord.getY())
        );
    }
}
