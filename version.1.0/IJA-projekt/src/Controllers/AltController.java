package Controllers;
import Interfaces.Draw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import sources.Main;
import sources.Stop;
import sources.Street;

import java.util.ArrayList;
import java.util.List;

public class AltController{

    private List<Draw> elements_roads = new ArrayList<>();
    private List<Draw> elements_stops = new ArrayList<>();
    private List<Street> streets_list = new ArrayList<>();
    private List<Stop> stops_list = new ArrayList<>();
    private List<Stop> alt_road_list = new ArrayList<>();


    @FXML
    private Pane alt_box = null;

    @FXML
    public Button closeButton;

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        Main.alt_road_list = alt_road_list;
        stage.close();
    }

    /**
     * Method sets and imports streets and stops to the alternative stage
     * @param elements_roads List of Drawable objects containing streets and their names
     * @param elements_stops List of Drawable objects containing stops and their names
     */
    public void createMap(List<Draw> elements_roads, List<Draw> elements_stops){

        this.elements_roads = elements_roads;
        this.elements_stops = elements_stops;

        for (Draw draw : elements_roads){ //Draw draw = elements[i];
            alt_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
        }

        for (Draw draw : elements_stops){ //Draw draw = elements[i];
            alt_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
        }


    }
    /**
     * Method sets and imports streets and stops to the alternative stage
     * @param street_name Name of street that is closed
     * @param alt_streets_list List of streets that are part of map
     * @param alt_stop_list
     */
    public void MarkClosedStreet(String street_name, List<Street> alt_streets_list, List<Stop> alt_stop_list){

        streets_list = alt_streets_list;
        stops_list = alt_stop_list;

        for(Street street : streets_list){
            if (street_name == street.getId()){
                Line red_line = new Line(street.get_Start_coord().getX(), street.get_Start_coord().getY(),
                        street.get_End_coord().getX(), street.get_End_coord().getY());
                red_line.setStroke(Color.GREY);
                red_line.setStrokeWidth(5.0);//set it thicker than before
                alt_box.getChildren().add(red_line);//add it to scene over previously set values
            }
        }
    }

    @FXML
    private void handleOnMouseClicked(MouseEvent event)
    {
        event.consume();
        System.out.println("Clicked! " + event.getTarget());

        if (event.getTarget() instanceof Text) {
            System.out.println("Clicked! " + event.getTarget());
            String alt_stop =((Text) event.getTarget()).getText();
            System.out.println("text is " + ((Text) event.getTarget()).getText());
            for(Stop stop : stops_list){

                if (alt_stop == stop.getId()) {
                    if(alt_road_list.contains(stop)){
                        alt_box.getChildren().add(new Circle(stop.getCoordinates().getX(), stop.getCoordinates().getY(), 15, Color.CYAN));
                        alt_box.getChildren().add(new Text(stop.getCoordinates().getX() - 7.5, stop.getCoordinates().getY() + 5, alt_stop)); //constants just for visual fixes
                        alt_road_list.remove(stop);

                    }
                    else {
                        alt_box.getChildren().add(new Circle(stop.getCoordinates().getX(), stop.getCoordinates().getY(), 15, Color.PINK));
                        alt_box.getChildren().add(new Text(stop.getCoordinates().getX() - 7.5, stop.getCoordinates().getY() + 5, alt_stop)); //constants just for visual fixes
                        alt_road_list.add(stop);
                    }
                }
            }
        }
    }
}
