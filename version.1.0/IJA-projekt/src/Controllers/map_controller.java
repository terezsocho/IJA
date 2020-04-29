package Controllers;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import sources.Coordinate;
import sources.Stop;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class map_controller {
    @FXML
    private Pane map_box = null;
    @FXML
    private Pane display = null;

    @FXML
    private TableColumn<String, LocalTime> tcScreenName;
    @FXML
    private TextField input_text_field;
    private List<TimeUpdate> updates = new ArrayList<>();
    private List<LineInfo> lines_info = new ArrayList<>();
    private List<Draw> elements_roads = new ArrayList<>();
    private List<Draw> elements_stops = new ArrayList<>();
    private List<Draw> elements_vehicles = new ArrayList<>();
    private Timer timer = null;
    private List<String> array_buslines_numbers;
    private LocalTime time = LocalTime.of(10,43,14); //inicializes to a same time
    private List<Coordinate> buses_current_positions = new ArrayList<>();
    private boolean bus_line_already_chosen = false;
    private List <LocalTime> transit_schedule = new ArrayList<>();
    private List<LocalTime> array_buslines_leave_times = new ArrayList<>();


    //private LocalTime time = LocalTime.now(); //inicializes to a current time

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
        timer.cancel();//stop previous timer
        startTime(scale);//initiate new timer
    }
    public void setElements(List<Draw> elements_roads, List<Draw> elements_stops, List<Draw> elements_vehicles,
                            List<String> array_buslines_numbers, List<LocalTime> array_buslines_leave_times) {

        this.array_buslines_leave_times = array_buslines_leave_times;
        this.elements_roads = elements_roads;
        this.elements_stops = elements_stops;
        this.elements_vehicles = elements_vehicles;
        this.array_buslines_numbers = array_buslines_numbers;
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
    }

    public void startTime(double scale){
        timer = new Timer(false);

        timer.scheduleAtFixedRate(new TimerTask() {//new timertask
            @Override
            public void run() {
                time = time.plusSeconds(1); // increase time every seconds with a second
                System.out.println("Time is: " + time);
                    //for (TimeUpdate update : updates) {
                for (int index = 0; index<updates.size()-1; index++) {
                    buses_current_positions.set(index, updates.get(index).update(time));
                    map_box.layout();
                }
            }
        }, 0,(long) (1000/scale));//period establishes duration between updates
    }

    @FXML
    private void getLineInfo(MouseEvent mouse_clicked) {
        List<Coordinate> stops_coordinates = new ArrayList<>();
        List<Integer> stops_index_at = new ArrayList<>();
        List<String> stops_names = new ArrayList<>();
        mouse_clicked.consume();// to stay in desired pane
        double x = 0.0;
        double y = 0.0;
        int index_clicked_busline = checkClickedBusLine(mouse_clicked,x, y);

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
                map_box.getChildren().add(new Circle(temp_path.get(index_of_last_stop).getX(), temp_path.get(index_of_last_stop).getY(), 15, Color.YELLOW));
                map_box.getChildren().add(new Text(temp_path.get(index_of_last_stop).getX() - 7.5, temp_path.get(index_of_last_stop).getY() + 5, stops_names.get(index_of_last_namestop)));
            }
            stops_index_at.add(temp_path.size() - 1);//add last stop index
            Coordinate current_bus_position = new Coordinate(x,y);
            int index_of_closest = createTransitSchedule(transit_schedule, temp_path, index_clicked_busline,
                                                         current_bus_position,stops_index_at);
            displayTransitSchedule(transit_schedule, stops_names, index_of_closest);
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

    private int checkClickedBusLine(MouseEvent mouse_clicked, double x, double y){
        double clickedX = mouse_clicked.getX();
        double clickedY = mouse_clicked.getY();
        for (int index = 0; index < buses_current_positions.size(); index++) {
            try {
                x = buses_current_positions.get(index).getX();
                y = buses_current_positions.get(index).getY();
            } catch (NullPointerException e) { // coordinates are null until on the road
                //System.out.println("Bus has not left the station yet.");
                continue;
            }
            //distance between ceenter of bus symbol and click coordinates
            double distance = Math.sqrt(Math.pow(clickedX - x, 2) + Math.pow(clickedY - y, 2));
            if (distance <= 9) {//constant value 9 was estimated to produce best results, simulates radius of bus symbol
                return index;
            }
        }
        return -2; //return number out of bounds if nothing found
    }
    
    private double getDelayAtNextStop(Coordinate a, Coordinate b){
        double distance = Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
        double seconds_to_travel = distance / (55/3.6); // constant 55 is speed of vehicles - rework to get it from Bus.class
        return seconds_to_travel;
    }

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
            for(int p = 0; p < stops_index_at.size(); p++){
                double temp_distance = getDelayAtNextStop(current_bus_position,temp_path.get(stops_index_at.get(p)));
                if(temp_distance < delay_from_closest){
                    temp_index = p;//assign index of closest stop when clicked
                }
            }
        }
        return temp_index;
    }

    private void displayTransitSchedule(List <LocalTime> transit_schedule, List<String> stops_names, int index_of_closest){
        String temp ="\t  Transit Schedule\n\n";
        for (int i=0; i < transit_schedule.size();i++) {
            temp += "\t"+stops_names.get(i) + "\t : \t" + transit_schedule.get(i) + "\n";
        }
        System.out.println(index_of_closest);
        temp += "\n\n\tBus was closest to " +
                "\n\t\tStop \""+stops_names.get(index_of_closest)+"\"" +
                "\n\tFor new information " +
                "\n\tchoose route again.";
        display.getChildren().addAll(new Text(0, 20, temp));//create new "table"
    }
}
