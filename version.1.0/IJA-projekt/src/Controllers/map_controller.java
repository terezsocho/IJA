package Controllers;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import com.sun.scenario.animation.AbstractMasterTimer;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sources.Coordinate;
import sources.Main;
import sources.Stop;
import sources.Street;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class map_controller {
    @FXML
    private BorderPane rootPane;
    @FXML
    private Pane map_box = null;
    @FXML
    private Pane display = null;

    @FXML
    private TextField input_text_field;
    @FXML
    private ChoiceBox<String> choiceBox_street = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> choiceBox_level = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> closeStreet = new ChoiceBox<>();

    public static int NORMAL_BUS_SPEED = 55;

    private List<TimeUpdate> updates = new ArrayList<>();
    private List<LineInfo> lines_info = new ArrayList<>();
    private List<Draw> elements_roads = new ArrayList<>();
    private List<Draw> elements_stops = new ArrayList<>();
    private List<Draw> elements_vehicles = new ArrayList<>();
    private List<String> array_buslines_numbers;
    private LocalTime time = LocalTime.of(10,43,14); //inicializes to a same time
    private List<Coordinate> buses_current_positions = new ArrayList<>();
    private boolean bus_line_already_chosen = false;
    private List <LocalTime> transit_schedule = new ArrayList<>();
    private List<LocalTime> array_buslines_leave_times = new ArrayList<>();
    private List<Street> streets_list = new ArrayList<>();

    private List<Street> restriction_lvl_1 = new ArrayList<>();
    private List<Street> restriction_lvl_2 = new ArrayList<>();
    private String ClosedStreet;
    private Main main;


    /**
     * Method stores values from choiceboxes to be later used in restriction policy for streets.
     */
    @FXML
    private void onRoadRestrictionChange(){
        String chosen_street = choiceBox_street.getValue();
        String chosen_level = choiceBox_level.getValue();
        System.out.println("does nothing " + chosen_street +" + "+ chosen_level);
        for(Street street : streets_list){
            if ( chosen_street == street.getId() ){
                if(chosen_level == "1"){
                    if(!restriction_lvl_1.contains(street))//check if it is not there
                        restriction_lvl_1.add(street);
                    if(restriction_lvl_2.contains(street)) //check if it is not already in different list if so delete
                        restriction_lvl_2.remove(street);
                }
                else if(chosen_level == "2"){
                    if(!restriction_lvl_2.contains(street))//check if it is not there
                        restriction_lvl_2.add(street);
                    if(restriction_lvl_1.contains(street)) //check if it is already in different list if so delete
                        restriction_lvl_1.remove(street);
                }
                else{//remove from lists if set to 0 restriction level
                    if(restriction_lvl_1.contains(street)) //check if it is not already in different list if so delete
                        restriction_lvl_1.remove(street);
                    if(restriction_lvl_2.contains(street)) //check if it is not already in different list if so delete
                        restriction_lvl_2.remove(street);
                }
                //System.out.println("LVL1 "+ restriction_lvl_1);
                //System.out.println("LVL2 "+ restriction_lvl_2);
            }
        }

    }

    /**
     * Method zooms or unzooms canvas by 10% in each direction depending on scroll direction.
     * @param event_zoom ScrollEvent created by an user during a simulation
     */
    @FXML
    private void onZoom(ScrollEvent event_zoom){
       // System.out.println("controller linked");
        event_zoom.consume();
        double zoom = event_zoom.getDeltaY() > 0 ? 1.1 : 0.9;// increased and decreased by a constant value
        // zoom or unzoom by 10% thus 1.1 and 0.9
        map_box.setScaleX(zoom * map_box.getScaleX());
        map_box.setScaleY(zoom * map_box.getScaleY());
        map_box.layout();
    }

    /**
     * Method scales time. Using the value from Input text field.
     * If wrong format of data is inserted method informs user about it.
     * 0 > data < 1 - acceleration
     *  data > 1 - deceleration
     *  data = 0 - stops movement
     */
    @FXML
    private void onTimeScaleChange(){
        double scale = 1.0;
        try {
            scale = Double.parseDouble(input_text_field.getText());//parse value from input text
            if (scale < 0){ //negative value is not alowed
                Alert wrong_input = new Alert(Alert.AlertType.WARNING,"Text that was inputed is invalid.");
                wrong_input.show();
                return;
            }
        }
        catch (NumberFormatException e){//character format not allowed
            Alert wrong_input = new Alert(Alert.AlertType.WARNING,"Text that was inputed is invalid.");
            wrong_input.show();
        }
        animationTimer.stop();
        startTime(scale);//initiate new timer
    }

    @FXML
    private void OnCloseStreet(ActionEvent event) throws IOException {
        /*ClosedStreet = closeStreet.getValue();
        for(Street street : streets_list){
            if (ClosedStreet == street.getId()){
                Line red_line = new Line(street.get_Start_coord().getX(), street.get_Start_coord().getY(),
                        street.get_End_coord().getX(), street.get_End_coord().getY());
                red_line.setStroke(Color.GREY);
                red_line.setStrokeWidth(5.0);//set it thicker than before
                map_box.getChildren().add(red_line);//add it to scene over previously set values
            }
        }*/
        // uzavreta Septimova
        // obchadzka cez Einsteinovu a Radarovu
        main.ShowNewStage();
    }

    /**
     * Method sets and imports all the important variables as well as draws initial City Map. Buses are not visible until
     * they leave the first bus stop on their route.
     * @param elements_roads List of Drawable objects containing streets and their names
     * @param elements_stops List of Drawable objects containing stops and their names
     * @param elements_vehicles List of Drawable objects containing buses and their names
     * @param array_buslines_numbers List of Strings that contains all possible bus line numbers
     * @param array_buslines_leave_times List of LocalTimes that contains all the departure times of all buses in simulation
     */
    public void setElements(List<Draw> elements_roads, List<Draw> elements_stops, List<Draw> elements_vehicles,
                            List<String> array_buslines_numbers, List<LocalTime> array_buslines_leave_times, List<Street> arraystreet) {

        this.array_buslines_leave_times = array_buslines_leave_times;
        this.elements_roads = elements_roads;
        this.elements_stops = elements_stops;
        this.elements_vehicles = elements_vehicles;
        this.array_buslines_numbers = array_buslines_numbers;
        this.streets_list = arraystreet;
        System.out.println("Number of road elements: "+elements_roads.size());
        for (Draw draw : elements_roads){ //Draw draw = elements[i];
            map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
        }

        System.out.println("Number of stop elements: "+elements_stops.size());
        for (Draw draw : elements_stops){ //Draw draw = elements[i];
            map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
        }

        Coordinate temp = new Coordinate(0,0);//initialize list of current coordintaes so method set can be used in getLineInfo
        System.out.println("Number of vehicles: "+elements_vehicles.size());
        for (Draw draw : elements_vehicles) { //Draw draw = elements[i];
            map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            updates.add((TimeUpdate) draw);
            lines_info.add((LineInfo) draw);
            buses_current_positions.add(temp);//add initial coordinates for latter use of list.set() method
        }
        initChoiceBox();//populates choice boxes
    }

    private AnimationTimer animationTimer;


    /**
     * Method creates timertask to be repeated every second to simulate movement of buses.
     * @param scale Double value used during fastening of the simulation
     */
    public void startTime(double scale) {



        animationTimer = new AnimationTimer() {
            int frameCount = 0;
            public void handle(long l) {
                if(frameCount == Math.round(scale*60) ) {
                    time = time.plusSeconds(1) ;
                    for (int index=0; index < updates.size(); index++) {
                        Coordinate bus_pos_temp = updates.get(index).update(time, restriction_lvl_1, restriction_lvl_2, ClosedStreet);
                        buses_current_positions.set(index, bus_pos_temp);
                        map_box.layout();
                    }
                    frameCount = 0;
                }
                frameCount++;
            }
        };

        animationTimer.start();




    }

    /**
     * Method gather user input from mouse click and highlights specific busline.
     * @param mouse_clicked MouseEvent variable used in further checks to determine position of click on canvas
     */
    @FXML
    private void getLineInfo(MouseEvent mouse_clicked) {
        List<Coordinate> stops_coordinates = new ArrayList<>();
        List<Integer> stops_index_at = new ArrayList<>();
        List<String> stops_names = new ArrayList<>();
        mouse_clicked.consume();// to stay in desired pane
        double x = 0.0;
        double y = 0.0;
        Coordinate current_bus_position = new Coordinate(x,y);

        int index_clicked_busline = checkClickedBusLine(mouse_clicked, current_bus_position);

        //if index is one of the bus line's instances and another bus line is not chosen go inside an if statement
        if (((index_clicked_busline >= 0 && index_clicked_busline <= lines_info.size()-1)) && bus_line_already_chosen == false) {
            bus_line_already_chosen = true;
            //get list of stops for corresponding bus line
            List<Stop> temp_path_stops = lines_info.get(index_clicked_busline).getLinePathStops();
            //get all important coordinates bus passes through
            List<Coordinate> temp_path = lines_info.get(index_clicked_busline).getLinePath();
            for (Stop stop : temp_path_stops) { //parsing of stops
                stops_coordinates.add(stop.getCoordinates());//store coordinate of stop
                stops_names.add(stop.getName());//store name of stop
            }

            for (int index = 0, name_index = 0; index < temp_path.size() - 1; index++) {
                //create new line between all two consecutive coordinates
                Line temp_line = new Line(temp_path.get(index).getX(), temp_path.get(index).getY(), temp_path.get(index + 1).getX(), temp_path.get(index + 1).getY());
                temp_line.setStroke(Color.YELLOW);
                temp_line.setStrokeWidth(2.0);//set it thicker than before
                map_box.getChildren().add(temp_line);//add it to scene over previously set values

                for (Coordinate stop_coord : stops_coordinates) {//loop through all coordinates of stops
                    if (stop_coord.equals(temp_path.get(index))) {//if currently examined coordinate is stop set new color and name
                        map_box.getChildren().add(new Circle(temp_path.get(index).getX(), temp_path.get(index).getY(), 15, Color.YELLOW));
                        //constants for visual fix of stop name
                        map_box.getChildren().add(new Text(temp_path.get(index).getX() - 7.5, temp_path.get(index).getY() + 5, stops_names.get(name_index)));
                        if (name_index < stops_names.size() - 2)
                            name_index++;//increment name of stop index, but without last one

                        stops_index_at.add(index);
                    }
                }
                //set end of route of busline
                int index_of_last_stop = temp_path.size() - 1;
                int index_of_last_namestop = stops_names.size() - 1;
                double last_stop_x_coord = temp_path.get(index_of_last_stop).getX();
                double last_stop_y_coord = temp_path.get(index_of_last_stop).getY();
                String last_stop_name = stops_names.get(index_of_last_namestop);

                map_box.getChildren().add(
                        new Circle(last_stop_x_coord, last_stop_y_coord, 15, Color.YELLOW));
                map_box.getChildren().add(
                        new Text(last_stop_x_coord - 7.5, last_stop_y_coord + 5, last_stop_name));
            }
            stops_index_at.add(temp_path.size() - 1);//add last stop index

            int index_of_closest = createTransitSchedule(transit_schedule, temp_path, index_clicked_busline,
                                                         current_bus_position,stops_index_at);
            String name_of_chosen_busline = array_buslines_numbers.get(index_clicked_busline);
            displayTransitSchedule(transit_schedule, stops_names, index_of_closest, name_of_chosen_busline);
        }
        else {//repaint Yellow lines of chosen busline back to black and aqua
            display.getChildren().clear();//clean the transit schedule view
            bus_line_already_chosen = false;
            map_box.getChildren().clear();
            for (Draw draw : elements_roads) { //Draw draw = elements[i];
               map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            }
            for (Draw draw : elements_stops) { //Draw draw = elements[i];
                map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            }
            for (Draw draw : elements_vehicles) { //Draw draw = elements[i];
                map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            }
        }

    }

    /**
     * Method determines which bus symbol was clicked. Thus establishes which busline is going to be highlighted.
     * @param mouse_clicked MouseEvent variable used in further checks
     * @param current_bus_position Coordinate reference to current bus position
     * @return Index in List of Coordinates buses_current_positions, that points to clicked bus symbol on canvas
     */
    private int checkClickedBusLine(MouseEvent mouse_clicked,  Coordinate current_bus_position){
        double clickedX = mouse_clicked.getX();
        double clickedY = mouse_clicked.getY();
        for (int index = 0; index < buses_current_positions.size(); index++) {
            try {
                current_bus_position.setX(buses_current_positions.get(index).getX());
                current_bus_position.setY(buses_current_positions.get(index).getY());
            } catch (NullPointerException e) { // coordinates are null until on the road
                //System.out.println("Bus has not left the station yet.");
                continue;
            }
            //distance between center of bus symbol and click coordinates
            double distance = Math.sqrt(Math.pow(clickedX - current_bus_position.getX(), 2)
                            + Math.pow(clickedY - current_bus_position.getY(), 2));
            if (distance <= 9) {//constant value 9 was estimated to produce best results, simulates radius of bus symbol
                return index;
            }
        }
        return -2; //return number out of bounds if nothing found
    }

    /**
     * Method calculates time needed for travel between two specified coordinates in seconds.
     * @param a Coordinate of starting position for calculation of distance and consecutively time
     * @param b Coordinate of end position for calculation of distance and consecutively time
     * @return Double value of seconds to travel between two coordinates
     */
    private double getDelayAtNextStop(Coordinate a, Coordinate b){
        double distance = Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
        double seconds_to_travel = distance / (NORMAL_BUS_SPEED/3.6); // constant 55 is speed of vehicles - rework to get it from Bus.class
        return seconds_to_travel;
    }

    /**
     * Method creates transit schedule for specific bus on chosen busline.
     * @param transit_schedule List of LocalTimes containing time departure for each stop
     * @param temp_path List of Coordinates containing all places where bus need to stop. Intersections, Bus stops.
     * @param index_clicked_busline Int value defining which busline is examined and transit schedule is created for.
     * @param current_bus_position Coordinate of current position of chosen bus on chosen busline
     * @param stops_index_at List of Integers defining indexes that are bus stops in temp_path
     * @return Index of closest bus stop from position when transit schedule was loaded.
     */
    private int createTransitSchedule(List <LocalTime> transit_schedule, List<Coordinate> temp_path,
                                      int index_clicked_busline, Coordinate current_bus_position,
                                      List<Integer> stops_index_at){

        //initialization of first delay with length to first bus stop
        double delay_from_closest = getDelayAtNextStop(current_bus_position,temp_path.get(stops_index_at.get(0)));
        //Time schedule appending into a list
        transit_schedule.clear();//clear out previous transit schedule
        LocalTime next_stop_time = array_buslines_leave_times.get(index_clicked_busline);//get departure value of clicked bus
        transit_schedule.add(next_stop_time);

        double delay_in_seconds = 0.0;
        int temp_index = 0;
        for(int j = 1, k = 1; j < temp_path.size(); j++){//loop through path of busline
            Coordinate temp1 = temp_path.get(j-1);
            Coordinate temp2 = temp_path.get(j);
            if( (j) == stops_index_at.get(k)){//if index is equal to index of a stop proceed
                delay_in_seconds += getDelayAtNextStop(temp1,temp2) + 0.5;//round double up
                next_stop_time = next_stop_time.plusSeconds((long)delay_in_seconds);
                transit_schedule.add(next_stop_time);// add new time to transit schedule for specific bus
                delay_in_seconds = 0.0;
                if(k < stops_index_at.size()) k++;//increment index of next stop
            }
            else{//if path is not ending in bus stop just add length to overall distance between two stops
                delay_in_seconds += getDelayAtNextStop(temp1,temp2);
            }
        }
        for(int p = 0; p < stops_index_at.size(); p++){
            double temp_distance = getDelayAtNextStop(current_bus_position,temp_path.get(stops_index_at.get(p)));
            if(temp_distance < delay_from_closest){
                temp_index = p;//assign index of closest stop when clicked
                delay_from_closest = temp_distance;
            }
        }
        return temp_index;
    }

    /**
     * Method display final transit schedule on left Pane. For every stop there is departure time.
     * Small deviations in departure time may occur.
     * @param transit_schedule List of LocalTimes containing time departure for each stop
     * @param stops_names List of String containtin name for each stop
     * @param index_of_closest Int value containing index in stop_names that contains closest bus stop
     * @param name_of_chosen_busline String value contains name of busline that bus is part of
     */
    private void displayTransitSchedule(List <LocalTime> transit_schedule, List<String> stops_names, int index_of_closest, String name_of_chosen_busline){
        String temp ="\t  Transit Schedule\n"+
                     "\t\tLine \""+name_of_chosen_busline+"\"\n\n";
        for (int i=0; i < transit_schedule.size();i++) {
            temp += "\t"+stops_names.get(i) + "\t : \t" + transit_schedule.get(i) + "\n";
        }
        temp += "\n\n\tBus was closest to " +
                "\n\t\tStop \""+stops_names.get(index_of_closest)+"\"" +
                "\n\tFor new information " +
                "\n\tchoose route again.";
        display.getChildren().addAll(new Text(0, 20, temp));//create new "table"
    }

    /**
     * Method only populates choice boxes as well as sets default value for each
     */
    private void initChoiceBox(){
        List<String> temp_street_names_list = new ArrayList<>();
        for(Street street : streets_list){
            temp_street_names_list.add(street.getId());
        }
        choiceBox_street.getItems().addAll(temp_street_names_list);
        choiceBox_street.setValue(temp_street_names_list.get(0));

        closeStreet.getItems().addAll(temp_street_names_list);
        closeStreet.setValue(temp_street_names_list.get(0));

        choiceBox_level.getItems().add("0");
        choiceBox_level.getItems().add("1");
        choiceBox_level.getItems().add("2");
        choiceBox_level.setValue("0");
    }
}
