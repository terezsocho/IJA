package sources;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Bus implements Draw, TimeUpdate, LineInfo {

    private Coordinate position;
    private double speed = 0.0;
    private double distance = 0.0;
    private Path path;
    private List<Shape> gui = new ArrayList<>();
    private String id;
    private List<Coordinate> line_coordinates;
    private List<Stop> bus_line_stops;
    private LocalTime least_At;

    /**
     * Constructor for Bus elements.
     * @param id identification String of a busLine
     * @param speed Speed of a vehicle
     * @param path List of Coordinates for specific busline that each bus on thta bus line must pass
     * @param least_At List of LocalTimes that showcase departure times of all buses on busline from their first stop
     * @param bus_line_stops List of Stops on a bus route
     */
    public Bus(String id, double speed, Path path, LocalTime least_At, List<Stop> bus_line_stops) {
        this.line_coordinates = path.getPathCoord();
        this.position = path.getPathCoord().get(0);
        this.bus_line_stops = bus_line_stops;
        this.speed = speed / 3.6;//converting kilometers per hour to meters per second
        this.path = path;
        this.id = id;
        this.least_At = least_At;
        gui.add(new Circle(path.getPathCoord().get(0).getX(), path.getPathCoord().get(0).getY(), 12, Color.RED));
        gui.add(new Text(path.getPathCoord().get(0).getX() - 3.5, path.getPathCoord().get(0).getY() + 4, id));//constants just for visual fixes
    }

    /**
     * Method calculates distance needed for travel between two specified coordinates in meters.
     * @param a Coordinate of starting position for calculation of distance
     * @param b Coordinate of end position for calculation of distance
     * @return Double value of distance to travel between two coordinates
     */
    private double getDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Method returns identification information of a busline
     * @return String containing a name of busline
     */
    @Override
    public String getId() {
        return this.id;
    }

    //variables used in moveGui only
    int index = 1;
    boolean behind_bus_stop = false;//boolean to check if we are behind bus stop
    int text_and_bus_passed = 0;//variable to count drawn objects, bcs every iteration needs 2(text, circle)
    boolean waiting_at_bus_stop = false;//boolean to check if bus is at bus stop

    /**
     * Method moves buses on a canvas, to their next position calculated from their speed. If next step would cause
     * travel behind a bus stop on its route, new position is calculated to prohibit that.
     * @param coordinate Coordinate of next position that bus should be moved to
     */
    private void moveGUI(Coordinate coordinate) {
        for (Shape shape : gui) {
            shape.setVisible(true);
            if (waiting_at_bus_stop == false) { // if bus is currently not at bus stop continue
                //distance needs to be less than the speed that bus makes in 1 step..
                if (getDistance(position, line_coordinates.get(index)) < speed && behind_bus_stop == false) {
                    shape.setTranslateX((line_coordinates.get(index).getX() - position.getX()) + shape.getTranslateX());
                    shape.setTranslateY((line_coordinates.get(index).getY() - position.getY()) + shape.getTranslateY());
                    //shape.toFront();
                    text_and_bus_passed++;
                    if (text_and_bus_passed % 2 == 0) {//there are 2 elements in gui, text and circle.. both need to go together
                        behind_bus_stop = true;//set true so next calculation is not according to old bus stop
                        this.position = line_coordinates.get(index);//set new position at a bus stop or intersection
                        waiting_at_bus_stop = true;
                        //increment to check value againt next intersection or bus stop, until i
                        if (line_coordinates.size() - 1 > index) index++;

                    }
                } else {
                    shape.setTranslateX((coordinate.getX() - position.getX()) + shape.getTranslateX());
                    shape.setTranslateY((coordinate.getY() - position.getY()) + shape.getTranslateY());
                    text_and_bus_passed++;
                    if (text_and_bus_passed % 2 == 0) {//there are 2 elements in gui, text and circle.. both need to go together
                        this.position = coordinate; // set new position as position + speed
                        behind_bus_stop = false;
                    }
                }
            } else {//if bus is already at the bus stop just let him wait for a one cycle
                text_and_bus_passed++;
                if (text_and_bus_passed % 2 == 0) waiting_at_bus_stop = false;
            }

        }
    }

    /**
     * Method removes buses from a canvas once they reach their final stop.
     */
    private void removeGUI() {
        for (Shape shape : gui) {
            shape.setVisible(false);//toggle visibility to false
        }
    }

    /**
     * Method sets all buses to not be visible until their departure time
     * @return List of Shapes to be drawn onto canvas
     */
    @Override
    public List<Shape> getGUI() {
        for (Shape shape : gui) {
            shape.setVisible(false);//sets all busses in system to invisible until on road
        }
        return gui;
    }


    boolean set_immutable_bus_stop = true;

    /**
     * Method updates positions of a bus on a canvas to its next position
     * @param time LocalTime variable used to determine if bus is up for departure
     * @return Coordinate of current bus position.
     */
    @Override
    public Coordinate update(LocalTime time) {
        if (time.isAfter(least_At)) {
            //System.out.println("Time is: " + time);
            distance = distance + speed;
            if (distance > path.getPathSize()) {
                // only moves it to final destination once, if repeated moveGUi still moves it by small margin
                if (set_immutable_bus_stop == true) {
                    System.out.println(path.getPathSize());
                    Coordinate coords = path.getCoorBus(path.getPathSize());
                    moveGUI(coords);
                    removeGUI();
                    set_immutable_bus_stop = false;
                }
                return null; //break before moveGUI happens again
            }
            Coordinate coords = path.getCoorBus(distance);
            moveGUI(coords);
            return coords;
        }
        return null;
    }

    @Override
    public List<sources.Coordinate> getLinePath() {
        return line_coordinates;
    }

    @Override
    public List<Stop> getLinePathStops() {
        return bus_line_stops;
    }

}

