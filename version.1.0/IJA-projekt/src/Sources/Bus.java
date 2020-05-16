/*
 * Authors: Terézia Sochova(xsocho14), Adrián Piaček(xpiace00)
 * Source code Bus.java contains methods used for calculations of specific buses. These include their speed, routes and
 * all the additional important measurements.
 */
package Sources;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<Stop> alt_stops_list = new ArrayList<>();
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
     * Constructor for Bus object.
     * @param id identification String of a busLine
     * @param path List of Coordinates for specific busline that each bus on thta bus line must pass
     * @param least_At List of LocalTimes that showcase departure times of all buses on busline from their first stop
     * @param bus_line_stops List of Stops on a bus route
     * @param bus_line_streets List of Streets on a bus route
     *@param speed speed of a bus 
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
     * Method returns identification information of a bus line
     * @return String containing a name of bus line
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
            shape.setVisible(false);//sets all buses in system to invisible until on road
        }
        return gui;
    }

    /**
     * Method calculates part of a route that is changed by closing a street.
     * From stops on alternative route obtains coordinates of the route
     * @param stop_list list of all stops that are part of new route
     * @return List of coordinates for alternative part of route
     */
    public List<Coordinate> Alternative_road(List<Stop>stop_list){
        List <Coordinate> coor_list = new ArrayList<>();
        List <Street> street_list = new ArrayList<>();
        List <String> name_list = new ArrayList<>();
        Street street;
        String streetname = null;

        //only need street names are copied to new list
        // needed streets are the one where stops of alternative route are calculated
        for(Stop stop: stop_list){
            name_list.add(stop.getOn_street());
        }

        //new street list is created
        //contains only street on new bus line
        for (int a = 0; a < name_list.size(); a++) {
            for (int b = 0; b < Main.arraystreet.size(); b++) {
                if (name_list.get(a).equals(Main.arraystreet.get(b).getId())) {//check if I need a street
                    street_list.add(Main.arraystreet.get(b)); //if yes add it to the array of streets
                }
            }
        }

        street = street_list.get(0); // assign first street too variable street
        for (int i = 0; i < stop_list.size(); i++) {
            if ((i == 0)) {//if first
                coor_list.add(stop_list.get(i).getCoordinates());//add to the list of coordinates realpath coordinates of that stop
            }
            else {

                if (stop_list.get(i).getOn_street().equals(streetname)) {//if street that stop is mounted on is equal to the streetname, so stop is on the same street as previous
                    coor_list.add(stop_list.get(i).getCoordinates());//add it to the list of coordinates realpath
                } else {

                    if (street.get_Start_coord().equals(street_list.get(i).get_Start_coord())) {//check if streets have same starting coordinates
                        coor_list.add(street.get_Start_coord());//if yes add this starting coordinate into a realpath
                    } else if (street.get_Start_coord().equals(street_list.get(i).get_End_coord())) {
                        coor_list.add(street.get_Start_coord());
                    } else if (street.get_End_coord().equals(street_list.get(i).get_End_coord())) {
                        coor_list.add(street.get_End_coord());
                    } else if(street.get_End_coord().equals(street_list.get(i).get_Start_coord())){
                        coor_list.add(street.get_End_coord());
                    }
                    coor_list.add(stop_list.get(i).getCoordinates());
                }
            }
            streetname = stop_list.get(i).getOn_street();
            street = street_list.get(i);
        }
        return coor_list;
    }


    /**
     * Method checks from side of street is bus coming and where to strat alternative route
     * according results returns list of stops that is reserved or not.
     * @param ListStops list of stops that are part of new route
     * @param NameStreet name of closed street
     * @return List of stop for alternative part of route in right direction
     */
    public List<Stop> CheckDirection(List<Stop> ListStops, String NameStreet){
        Street close = null;
        // obtain all information (like coordinates and name ) about closed street
        for(Street street : Main.arraystreet){
            if(street.getId() == NameStreet){
                close =  street;
            }
        }

        Coordinate direction = null;
        boolean is_bool = false;
        for(Coordinate coor : this.line_coordinates){// looping in list of bus line coordinates
            // if list contains coordinates of closed street
            if((coor.getX() == close.get_End_coord().getX()) && (coor.getY() == close.get_End_coord().getY()) && is_bool == false){
                direction = close.get_End_coord();
                is_bool = true;// important because we want only coordinate of a street that was in list as a first on
            }
            if((coor.getX() == close.get_Start_coord().getX()) && (coor.getY() == close.get_Start_coord().getY()) && is_bool == false){
                direction = close.get_Start_coord();//assigning to different var for future comparision
                is_bool = true;
            }
        }

        int size = ListStops.size();
        //if bus line contains closed street
        //if not returns the same list you get
        if(direction != null){
            //calculating distance between point that was detected in bus line list and
            //point where first stop of alternative route is situated
            double distance_1 = Math.sqrt(Math.pow((direction.getX() - ListStops.get(0).getCoordinates().getX()), 2)
                    + Math.pow((direction.getY() - ListStops.get(0).getCoordinates().getY()), 2));
            //calculating distance between point that was detected in bus line list and
            //point where stop stop of alternative route is situated
            double distance_2 = Math.sqrt(Math.pow((direction.getX() - ListStops.get(size-1).getCoordinates().getX()), 2)
                    + Math.pow((direction.getY() - ListStops.get(size-1).getCoordinates().getY()), 2));

            if(distance_1 > distance_2){
                Collections.reverse(ListStops);//reverse - last element goes to first index, second last to second index ...
                return ListStops;// return reverse list of stops
            }
            else{
                return ListStops;//return unchanged list of stops
            }

        }

        return ListStops;
    }

    boolean set_immutable_bus_stop = true;
    /**
     * Method updates positions of a bus on a canvas to its next position and check if any street was closed
     * @param time LocalTime variable used to determine if bus is up for departure
     * @param restriction_lvl_1 list of street where speed is slower because of restriction level 1
     * @param restriction_lvl_2 list of street where speed is slower because of restriction level 2
     * @param closedStreet name of street that is closed
     * @param alt_stops_list list of stops which are on alternative part of route
     * @return Coordinate of current bus position.
     */
    @Override
    public Coordinate update(LocalTime time, List<Street> restriction_lvl_1, List<Street> restriction_lvl_2, String closedStreet, List<Stop> alt_stops_list) {
        this.restriction_lvl_1 = restriction_lvl_1;
        this.restriction_lvl_2 = restriction_lvl_2;
        this.alt_stops_list = alt_stops_list;
        this.closedStreet = closedStreet;
        List <Integer> index_st = new ArrayList<>();//list for indexes of coordinates that should be deleted

        //if any street was closed and alternative route was set
        if((this.closedStreet != null) && (this.alt_stops_list.size() > 0)){
            //check direction of a bus
            //this.alt_stops_list = CheckDirection(this.alt_stops_list, this.closedStreet);
            List<Coordinate> alt_coor_list = null;
            alt_coor_list = Alternative_road(this.alt_stops_list);//returns real path of alternative route

            for (Stop stop : bus_line_stops) {//looping in all coordinates that determines real path of bus
                if (stop.getOn_street() == closedStreet) {//if we are on a closed street
                    for (Coordinate coordinates : this.line_coordinates) {//take all coordinates of stops on this street
                        if ((coordinates.getX() == stop.getCoordinates().getX()) && (coordinates.getY() == stop.getCoordinates().getY())) {
                            index_st.add(this.line_coordinates.indexOf(coordinates));
                        }
                    }
                }
            }
            //changing route to alternative route by adding new and deleting old coordinates
            //remove all
            if((index_st.size() > 0)){

                int remove_index = index_st.get(0);
                for(int i = 0; i < index_st.size(); i++){//remove coordinates all given index obtained in above cycle
                    this.line_coordinates.remove(remove_index);
                }

                int w = index_st.get(0);
                int v = 0;
                //add all new coordinates starting on index from where all coordinates were deleted
                while( v < (alt_coor_list.size())){
                    this.line_coordinates.add(w, alt_coor_list.get(v));
                    w++;
                    v++;
                }
            }
        }
        
        if (time.isAfter(least_At)) {// if bus started its route
             distance = getNewPosition(distance);
            if (distance > path.getPathSize()) {
                // only moves it to final destination once, if repeated moveGUi still moves it by small margin
                if (set_immutable_bus_stop == true) {
                    //System.out.println(path.getPathSize());
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

            for (int j = 1; j < indexes_busLine_restr_lvl_1.size() ; j+=2) {
                int temp_1 = indexes_busLine_restr_lvl_1.get(j-1);
                int temp_2 = indexes_busLine_restr_lvl_1.get(j);
                int temp = Math.abs(temp_1 - temp_2);
                //System.out.println("Hodnota temp: " + temp);
                if (temp > 5) {
                    indexes_busLine_restr_lvl_1.set(j, indexes_busLine_restr_lvl_1.get(j-1));
                    indexes_busLine_restr_lvl_1.set(j-1, 0);
                }
            }
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

            for (int j = 1; j < indexes_busLine_restr_lvl_2.size() ; j+=2) {
                int temp_1 = indexes_busLine_restr_lvl_2.get(j-1);
                int temp_2 = indexes_busLine_restr_lvl_2.get(j);
                int temp = Math.abs(temp_1 - temp_2);
                //System.out.println("Hodnota temp: " + temp);
                if (temp > 5) {
                    indexes_busLine_restr_lvl_2.set(j, indexes_busLine_restr_lvl_2.get(j-1));
                    indexes_busLine_restr_lvl_2.set(j-1, 0);
                }
            }
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

        if(restricted_lvl_1_index == lengths_of_restricted_streets_lvl_1.size()){
            //precaution in case that route changes restriction level during bus traveling on a route
            restricted_lvl_1_index = 0;
        }
        if(restricted_lvl_2_index == lengths_of_restricted_streets_lvl_2.size()){
            //precaution in case that route changes restriction level during bus traveling on a route
            restricted_lvl_2_index = 0;
        }
        if(start_restricted_streets_lvl_1.size() > 0 && lengths_of_restricted_streets_lvl_1.size() > 0) {
            restricted = start_restricted_streets_lvl_1.get(restricted_lvl_1_index);
            restricted_length = lengths_of_restricted_streets_lvl_1.get(restricted_lvl_1_index);
            if (distance >= restricted) {
                if (distance < (restricted + restricted_length)) {
                    distance += this.speed / 2;
                }
                else{
                    if(restricted_lvl_1_index < lengths_of_restricted_streets_lvl_1.size()-1){
                        restricted_lvl_1_index++;//index can not exceed length-1
                    }
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
                    if(restricted_lvl_2_index < lengths_of_restricted_streets_lvl_2.size()-1) restricted_lvl_2_index++;//index can not exceed length-1

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
    public List<Coordinate> getLinePath() {
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

