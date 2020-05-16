package Sources;

import Interfaces.Draw;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import java.util.Arrays;
import java.util.List;

public class Stop  implements Draw{
    private String name;
    private Coordinate coordinates;
    private String on_street;

    /**
     * Constructor for Stop object.
     * @param coordinates x and y coordinates. Defines position of stop on a map
     * @param name name of stop (it is unique)
     * @param on_street Name of street on which stop is situated
     */
    public Stop(Coordinate coordinates,String name, String on_street) {
        this.name = name;
        this.coordinates = coordinates;
        this.on_street = on_street;
    }
    /**
     * Method returns name value of stop
     * @return string name of stop
     */
    public String getName() {
        return name;
    }

    /**
     * Method returns coordinates of stop
     * @return coordinate object contains coordinates of stop
     */
    public Coordinate getCoordinates() {
        return coordinates;
    }

    /**
     * Method returns name value of street on which stop is situated
     * @return string name of street
     */
    public String getOn_street() {
        return on_street;
    }

    /**
     * Method returns list of shapes that defines the stop.
     * It is text containing name of stop and circle that represents the  stop.
     * @return list of shape that represents stop on gui
     */
    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                new Circle(coordinates.getX(), coordinates.getY(), 15, Color.AQUA),
                new Text(coordinates.getX()-7.5, coordinates.getY()+5, name) //constants just for visual fixes
        );
    }

    /**
     * Method returns id value of stop
     * @return string id of stop
     */
    @Override
    public String getId(){
        return this.name;
    }

}
