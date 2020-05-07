package sources;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.security.cert.X509Certificate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bus implements Draw, TimeUpdate, LineInfo {

    private Coordinate position;
    private double distance = 0.0;
    private double speed;
    private Path path;
    private List<Shape> gui = new ArrayList<>();
    private String id;
    private List<Coordinate> line_coordinates;
    private List<Stop> bus_line_stops;  //does not need to ne initialized, constructor does that
    private List<Street> bus_line_streets; //does not need to ne initialized, constructor does that
    private List<Street> alternative_bus_line  = new ArrayList<>();
    private LocalTime least_At;
    private List<Street> restriction_lvl_1 = new ArrayList<>();
    private List<Street> restriction_lvl_2 = new ArrayList<>();
    private String closedStreet;
    private List<Double> lengths_of_restricted_streets_lvl_1 = new ArrayList<>(); // contains lengths of restricted road
    private List<Double> start_restricted_streets_lvl_1 = new ArrayList<>(); // contains starting positions of restricting roads
    private List<Double> lengths_of_restricted_streets_lvl_2 = new ArrayList<>(); // contains lengths of restricted road
    private List<Double> start_restricted_streets_lvl_2 = new ArrayList<>(); // contains starting positions of restricting roads


    /**
     * Constructor for Bus elements.
     * @param id identification String of a busLine
     * @param path List of Coordinates for specific busline that each bus on thta bus line must pass
     * @param least_At List of LocalTimes that showcase departure times of all buses on busline from their first stop
     * @param bus_line_stops List of Stops on a bus route
     */
    public Bus(String id,  Path path, LocalTime least_At, List<Stop> bus_line_stops,List<Street> bus_line_streets, double speed) {
        this.line_coordinates = path.getPathCoord();
        this.position = path.getPathCoord().get(0);
        this.bus_line_stops = bus_line_stops;
        this.bus_line_streets = bus_line_streets;
        this.path = path;
        this.id = id;
        this.least_At = least_At;
        this.speed = speed / 3.6;//convert speed to metres per sec
        gui.add(new Circle(path.getPathCoord().get(0).getX(), path.getPathCoord().get(0).getY(), 12, Color.RED));
        gui.add(new Text(path.getPathCoord().get(0).getX() - 3.5, path.getPathCoord().get(0).getY() + 4, id));//constants just for visual fixes


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
        //System.out.println("MOVEGUI distance "+distance);
        //System.out.println("Restriction level1: "+restriction_lvl_1);
       // System.out.println("Streets on route: "+bus_line_streets);

        for (Shape shape : gui) {
            try {
                shape.setVisible(true);
            } catch (Exception e) {
                System.out.println(shape);
                System.out.println(shape.isVisible());
            } finally {
                shape.setVisible(true);
            }

            if (waiting_at_bus_stop == false) { // if bus is currently not at bus stop continue
                //distance needs to be less than the speed that bus makes in 1 step..
                double distance_to_next_postition = getDistance(position, line_coordinates.get(index));

                if (distance_to_next_postition < speed && behind_bus_stop == false) {
                    shape.setTranslateX((line_coordinates.get(index).getX() - position.getX()) + shape.getTranslateX());
                    shape.setTranslateY((line_coordinates.get(index).getY() - position.getY()) + shape.getTranslateY());
                    //shape.toFront();
                    text_and_bus_passed++;
                    if (text_and_bus_passed % 2 == 0) {//there are 2 elements in gui, text and circle.. both need to go together
                        behind_bus_stop = true;//set true so next calculation is not according to old bus stop
                        this.position = line_coordinates.get(index);//set new position at a bus stop or intersection
                        waiting_at_bus_stop = true;
                        //increment to check value against next intersection or bus stop, until i
                        if (line_coordinates.size() - 1 > index) index++;

                    }
                } else {
                    //System.out.println("coor: " + coordinate);
                    //System.out.println("pos: " + position);
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
    public Coordinate update(LocalTime time, List<Street> restriction_lvl_1, List<Street> restriction_lvl_2, String closedStreet) {
        this.restriction_lvl_1 = restriction_lvl_1;
        this.restriction_lvl_2 = restriction_lvl_2;

        //terez kod
        this.closedStreet = closedStreet;
        System.out.println("closedStreet:" +this.closedStreet);
        for (Street closed: bus_line_streets){
            System.out.print(closed.getId());
            if(closed.getId() == this.closedStreet){
                System.out.println("nothing");
                //Street temp_street = closed;
                //bus_line_streets.remove(temp_street);
            }
            else{
                alternative_bus_line.add(closed);
            }
        }

        //terez kod

        if (time.isAfter(least_At)) {// if bus started its route
             distance = getNewPosition(distance);
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

    /**
     * Method finds indexes of coordinates between whose traffic is slowed, due to the restriction policy of level 1.
     * Additionally evaluates lengths between these coordinates and stores them for future use.
     */
    public void checkRestrictionLevel1(){
        List<Integer> indexes_busLine_restr_lvl_1 = new ArrayList<>();
        boolean start_inserted = false, end_inserted = false;
        Coordinate start_coord_restr_street, end_coord_restr_street;

        if(restriction_lvl_1.size() > 0) { // if list is not empty
            for(Street street: restriction_lvl_1) { // for every street with level 1 restrictions
                if (bus_line_streets.contains(street)) { // check if currently examined busline travels throught street
                    start_coord_restr_street = street.get_Start_coord();
                    end_coord_restr_street = street.get_End_coord();
                } else continue;// if not skip rest of code

                for (int i = 0; i < line_coordinates.size(); i++) {
                    Coordinate temp_line_coord = line_coordinates.get(i);//examine each coordinate on a busline
                    if (temp_line_coord.equals(start_coord_restr_street)){
                        start_inserted = true;
                        indexes_busLine_restr_lvl_1.add(i); //insert index of coordinate from busline coordniate list
                    }
                    else if (temp_line_coord.equals(end_coord_restr_street)) {
                        end_inserted = true;
                        indexes_busLine_restr_lvl_1.add(i); //insert index of coordinate from busline coordniate list
                    }
                    if(end_inserted && start_inserted) break;//already both inserted
                }
                end_inserted = false;
                start_inserted = false;
            }
            //if number of indexes is odd then add 0 to beginning because route started in the middle of a street
            if(indexes_busLine_restr_lvl_1.size() % 2 == 1) indexes_busLine_restr_lvl_1.add(0,0);

            //sorting of a list for easier indexing
            indexes_busLine_restr_lvl_1 =  indexes_busLine_restr_lvl_1.stream().sorted().collect(Collectors.toList());
        }
//Distance ----------------------------------------------------------------------------------------------------------

        start_restricted_streets_lvl_1.clear();
        lengths_of_restricted_streets_lvl_1.clear();
        for (int j = 1; j < indexes_busLine_restr_lvl_1.size() ; j+=2) { //increments by two
            Coordinate temp1 = line_coordinates.get(indexes_busLine_restr_lvl_1.get(j-1));
            Coordinate temp2 = line_coordinates.get(indexes_busLine_restr_lvl_1.get(j));
            //calculates distance from start of restricted street to its end
            double temp_distance_restr = getDistance(temp1, temp2);
            lengths_of_restricted_streets_lvl_1.add(temp_distance_restr);//add calculated length to a list

            double temp_distance_until_restr = 0.0;
            //loop through the streets to calculate distance between start and next restricted street
            for (int i = 0; i < indexes_busLine_restr_lvl_1.get(j-1); i++) {
                temp1 = line_coordinates.get(i);
                temp2 = line_coordinates.get(i+1);
                temp_distance_until_restr += getDistance(temp1, temp2);
            }
            start_restricted_streets_lvl_1.add(temp_distance_until_restr);
        }
        //sort lists from smallest to highest value
        start_restricted_streets_lvl_1 = start_restricted_streets_lvl_1.stream().sorted().collect(Collectors.toList());
        lengths_of_restricted_streets_lvl_1 = lengths_of_restricted_streets_lvl_1.stream().sorted().collect(Collectors.toList());
    }


    /**
     * Method finds indexes of coordinates between whose traffic is slowed, due to the restriction policy of level 2.
     * Additionally evaluates lengths between these coordinates and stores them for future use.
     */
    public void checkRestrictionLevel2(){
        List<Integer> indexes_busLine_restr_lvl_2 = new ArrayList<>(); //create new list for level 2 restrictions
        boolean start_inserted = false, end_inserted = false;
        Coordinate start_coord_restr_street, end_coord_restr_street;

        if(restriction_lvl_2.size() > 0) {
            for(Street street: restriction_lvl_2) { //loop through each street with levl 2 restriction policy
                if (bus_line_streets.contains(street)) {
                    start_coord_restr_street = street.get_Start_coord();
                    end_coord_restr_street = street.get_End_coord();
                } else continue;//if busline does not travel through the street skip rest of code

                for (int i = 0; i < line_coordinates.size(); i++) {
                    Coordinate temp_line_coord = line_coordinates.get(i);
                    if (temp_line_coord.equals(start_coord_restr_street)){ //check if cooordinate equals on of street's ones
                        start_inserted = true;
                        indexes_busLine_restr_lvl_2.add(i);
                    }
                    else if (temp_line_coord.equals(end_coord_restr_street)) {
                        end_inserted = true;
                        indexes_busLine_restr_lvl_2.add(i);
                    }
                    if(end_inserted && start_inserted) break;//already both inserted
                }
                end_inserted = false;
                start_inserted = false;
            }
            //if number of indexes is odd then add 0 to beginning because route started in the middle of a street
            if(indexes_busLine_restr_lvl_2.size() % 2 == 1) indexes_busLine_restr_lvl_2.add(0,0);

            //sorting of a list for easier indexing
            indexes_busLine_restr_lvl_2 =  indexes_busLine_restr_lvl_2.stream().sorted().collect(Collectors.toList());
        }
//Distance ----------------------------------------------------------------------------------------------------------
        start_restricted_streets_lvl_2.clear();
        lengths_of_restricted_streets_lvl_2.clear();
        for (int j = 1; j < indexes_busLine_restr_lvl_2.size() ; j+=2) { //increments by two
            Coordinate temp1 = line_coordinates.get(indexes_busLine_restr_lvl_2.get(j-1));
            Coordinate temp2 = line_coordinates.get(indexes_busLine_restr_lvl_2.get(j));
            //calculates distance from start of restricted street to its end
            double temp_distance_restr = getDistance(temp1, temp2);
            lengths_of_restricted_streets_lvl_2.add(temp_distance_restr);

            double temp_distance_until_restr = 0.0;
            //loop through the streets to calculate distance between start and next restricted street
            for (int i = 0; i < indexes_busLine_restr_lvl_2.get(j-1); i++) {
                temp1 = line_coordinates.get(i);
                temp2 = line_coordinates.get(i+1);
                temp_distance_until_restr += getDistance(temp1, temp2);
            }
            start_restricted_streets_lvl_2.add(temp_distance_until_restr);
        }
        //sort lists according to value froms smallest to highest
        start_restricted_streets_lvl_2 = start_restricted_streets_lvl_2.stream().sorted().collect(Collectors.toList());
        lengths_of_restricted_streets_lvl_2 = lengths_of_restricted_streets_lvl_2.stream().sorted().collect(Collectors.toList());
    }



    int restricted_lvl_1_index = 0;
    int restricted_lvl_2_index = 0;
    /**
     * Method calculates distance from previous position of a bus according to a restrictions on a current street.
     * @param distance value of already travelled distance by a bus from its sorce bus stop
     * @return new value of distance. This may be unaffected by restriction or may affected by 2 restricton groups.
     */
    private double getNewPosition(double distance){
        double restricted, restricted_length;
        checkRestrictionLevel1();
        checkRestrictionLevel2();

        /*System.out.println("Previous distance "+distance);
        System.out.println("Speed + distance would be "+(distance+speed));
*/
        if(start_restricted_streets_lvl_1.size() > 0 && lengths_of_restricted_streets_lvl_1.size() > 0) {
            restricted = start_restricted_streets_lvl_1.get(restricted_lvl_1_index);
            restricted_length = lengths_of_restricted_streets_lvl_1.get(restricted_lvl_1_index);
            if (distance >= restricted) {
                if (distance < (restricted + restricted_length)) {
                    distance += this.speed / 2;
                }
                else{
                    if(index < lengths_of_restricted_streets_lvl_1.size()) index++;//index can not exceed length-1

                    restricted = start_restricted_streets_lvl_1.get(restricted_lvl_1_index);//assign new values from list
                    restricted_length = lengths_of_restricted_streets_lvl_1.get(restricted_lvl_1_index);
                    if (distance < (restricted + restricted_length)) {//check again if next street is also restricted
                        distance += this.speed / 2;
                    }
                    else distance += this.speed;
                }
            }
            else distance += this.speed;
        }
        else if(start_restricted_streets_lvl_2.size() > 0 && lengths_of_restricted_streets_lvl_2.size() > 0) {
            restricted = start_restricted_streets_lvl_2.get(restricted_lvl_2_index);
            restricted_length = lengths_of_restricted_streets_lvl_2.get(restricted_lvl_2_index);
            if (distance >= restricted) {
                if (distance < (restricted + restricted_length)) {
                    distance += this.speed / 3;
                }
                else{
                    if(index < lengths_of_restricted_streets_lvl_2.size()) index++;//index can not exceed length-1

                    restricted = start_restricted_streets_lvl_2.get(restricted_lvl_2_index);//assign new values from list
                    restricted_length = lengths_of_restricted_streets_lvl_2.get(restricted_lvl_2_index);
                    if (distance < (restricted + restricted_length)) {//check again if next street is also restricted
                        distance += this.speed / 3;
                    }
                    else distance += this.speed;
                }
            }
            else distance += this.speed;
        }
        else distance += this.speed;
       /* System.out.println("Returned distance "+distance);
        System.out.println("----------------------------------------------------------------------------------");*/
        return distance;
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
     * Method returns busline coordinates for each bus on this route to be passed.
     * @return List of Coordinates
     */
    @Override
    public List<sources.Coordinate> getLinePath() {
        return line_coordinates;
    }

    /**
     * Method returns busline stops for each bus on this route to be stopped at.
     * @return List of Stops
     */
    @Override
    public List<Stop> getLinePathStops() {
        return bus_line_stops;
    }

}

