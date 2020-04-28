package Controllers;

import Interfaces.Draw;
import Interfaces.LineInfo;
import Interfaces.TimeUpdate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import sources.Coordinate;
import sources.Stop;

import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class map_controller {
    @FXML
    private Pane map_box = null;
    @FXML
    private TextField input_text_field;
    private List<TimeUpdate> updates = new ArrayList<>();
    private List<LineInfo> lines_info = new ArrayList<>();
    private List<Draw> elements = new ArrayList<>();
    private Timer timer = null;
    private List<String> array_buslines_numbers;
    private LocalTime time = LocalTime.of(10,43,14); //inicializes to a same time
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

    public void setElements(List<Draw> elements, List<String> array_buslines_numbers) {
        this.elements = elements;
        this.array_buslines_numbers = array_buslines_numbers;
        map_box.getChildren().clear();
        for (Draw draw : elements){ //Draw draw = elements[i];
            map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            //map_box.getChildren().addAll(new Circle(150, 150, 18, Color.BLUE));
            if (draw instanceof TimeUpdate) {
                updates.add((TimeUpdate) draw);
                lines_info.add((LineInfo) draw);
            }
        }
    }
    @FXML
    private void getLineInfo(MouseEvent mouse_clicked){
        mouse_clicked.consume();// to stay in desired pane
        System.out.println("mouse click detected! "+mouse_clicked.getX());
        int index_clicked_busline = Integer.parseInt(array_buslines_numbers.get(0));

        List<Stop> temp_path_stops = lines_info.get(index_clicked_busline).getLinePathStops();
        List<Coordinate> temp_path = lines_info.get(index_clicked_busline).getLinePath();

        List<Coordinate> stops_coordinates = new ArrayList<>();
        List<String> stops_names = new ArrayList<>();
        for (Stop stop : temp_path_stops) { //parsing of stops
            stops_coordinates.add(stop.getCoordinates());//store coordinate of stop
            stops_names.add(stop.getName());//store name of stop
        }
        //List<Shape> temp_shapes = line_info.get(0).getLineSchedule();
        for(int index = 0, name_index = 0; index < temp_path.size()-1; index++){
            //create new line between all two consecutive coordinates
            Line temp_line =new Line(temp_path.get(index).getX(), temp_path.get(index).getY(), temp_path.get(index+1).getX(), temp_path.get(index+1).getY());
            temp_line.setStroke(Color.YELLOW);
            temp_line.setStrokeWidth(2.0);//set it thicker than before
            map_box.getChildren().add(temp_line);//add it to scene
            for(Coordinate stop_coord : stops_coordinates) {//loop through all coordinates of stops
                if (stop_coord.equals(temp_path.get(index))){//if currently examined coordinate is stop set new color and name
                    map_box.getChildren().add(new Circle(temp_path.get(index).getX(), temp_path.get(index).getY(), 15, Color.YELLOW));
                    //constants for visual fix of stop name
                    map_box.getChildren().add(new Text(temp_path.get(index).getX()-7.5, temp_path.get(index).getY()+5, stops_names.get(name_index)));
                    if(name_index < stops_names.size()-2)name_index++;//increment name of stop index, but without last one
                }
            }
            //set end of route of busline
            int index_of_last_stop = temp_path.size()-1;
            int inddex_of_last_namestop = stops_names.size()-1;
            map_box.getChildren().add(new Circle(temp_path.get(index_of_last_stop).getX(), temp_path.get(index_of_last_stop).getY(), 15, Color.YELLOW));
            map_box.getChildren().add(new Text(temp_path.get(index_of_last_stop).getX()-7.5, temp_path.get(index_of_last_stop).getY()+5, stops_names.get(inddex_of_last_namestop)));
        }
    }
    public void startTime(double scale){
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {//new timertask
            @Override
            public void run() {
                time = time.plusSeconds(1); // increase time every seconds with a second
                System.out.println("Time is: " + time);
                    for (TimeUpdate update : updates) {
                        update.update(time);
                        map_box.layout();
                    }

            }
        }, 0,(long) (1000/scale));//period establishes duration between updates
    }

}
