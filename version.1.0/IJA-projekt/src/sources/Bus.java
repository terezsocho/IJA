package sources;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

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
     * @param id
     * @param speed
     * @param path
     */
    public Bus(String id, double speed, Path path, String least_At, List<Stop> bus_line_stops) {
        this.line_coordinates = path.getPathCoord();
        this.position = path.getPathCoord().get(0);
        this.bus_line_stops = bus_line_stops;
        this.speed = speed / 3.6;//converting kilometers per hour to meters per second
        this.path = path;
        this.id = id;
        // parsing of hh:mm:ss format
        int hours = Integer.parseInt(least_At.substring(0, 2));
        int minutes = Integer.parseInt(least_At.substring(3, 5));
        int seconds = Integer.parseInt(least_At.substring(6, 8));
        this.least_At = LocalTime.of(hours, minutes, seconds);
        gui.add(new Circle(path.getPathCoord().get(0).getX(), path.getPathCoord().get(0).getY(), 12, Color.RED));
        gui.add(new Text(path.getPathCoord().get(0).getX() - 3.5, path.getPathCoord().get(0).getY() + 4, id));//constants just for visual fixes
    }

    /**
     * @param a
     * @param b
     * @return
     */
    private double getDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    @Override
    public String getId() {
        return this.id;
    }

    //variables used in moveGui only
    int index = 1;
    boolean behind_bus_stop = false;//boolean to check if we are behind bus stop
    int text_and_bus_passed = 0;//variable to count drawn objects, bcs every iteration needs 2(text, circle)
    boolean waiting_at_bus_stop = false;//boolean to check if bus is at bus stop

    private void moveGUI(Coordinate coordinate) {
        for (Shape shape : gui) {
            shape.setVisible(true);
            if (waiting_at_bus_stop == false) { // if bus is currently not at bus stop continue
                //distance needs to be less than the speed that bus makes in 1 step..
                if (getDistance(position, line_coordinates.get(index)) < speed && behind_bus_stop == false) {
                    shape.setTranslateX((line_coordinates.get(index).getX() - position.getX()) + shape.getTranslateX());
                    shape.setTranslateY((line_coordinates.get(index).getY() - position.getY()) + shape.getTranslateY());
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

    private void removeGUI() {
        for (Shape shape : gui) {
            shape.setVisible(false);//toggle visibility to false
        }
    }

    @Override
    public List<Shape> getGUI() {
        for (Shape shape : gui) {
            shape.setVisible(false);//sets all busses in system to invisible until on road
        }
        return gui;
    }

    boolean set_immutable_bus_stop = true;
    @Override
    public void update(LocalTime time) {
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
                return; //break before moveGUI happens again
            }
            Coordinate coords = path.getCoorBus(distance);
            moveGUI(coords);
        }
    }

    @Override
    public List<Shape> getLineSchedule() {
        return gui;
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

