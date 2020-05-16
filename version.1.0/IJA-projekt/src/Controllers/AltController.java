/*
 * Authors: Terézia Sochova(xsocho14), Adrián Piaček(xpiace00)
 * Source code AltController.java contains methods used for handling of user input concerning bus closure and its
 * subsequent consequences, such as new canvas for bus detour.
 */
package Controllers;
import Interfaces.Draw;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Sources.Main;
import Sources.Stop;
import Sources.Street;
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

    /**
     * Method that closes window for choosing alternative route after clicking close button
     * and fill the list with stops on alternative route
     */
    @FXML
    public void handleCloseButtonAction() {
        Main.alt_road_list = null;
        Stage stage = (Stage) closeButton.getScene().getWindow();//chooses second stage
        Main.alt_road_list = alt_road_list;
        stage.close();//closes second window
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
     * @param alt_stop_list List of stop that are part of alternative route
     */
    public void MarkClosedStreet(String street_name, List<Street> alt_streets_list, List<Stop> alt_stop_list){

        streets_list = alt_streets_list;
        stops_list = alt_stop_list;

        for(Street street : streets_list){//looping over list of all street
            if (street_name == street.getId()){//if names (name) coincides street is marked
                Line red_line = new Line(street.get_Start_coord().getX(), street.get_Start_coord().getY(),
                        street.get_End_coord().getX(), street.get_End_coord().getY());
                red_line.setStroke(Color.RED);
                red_line.setStrokeWidth(5.0);//set it thicker than before
                alt_box.getChildren().add(red_line);//add it to scene
            }
        }
    }

    /**
     * Method processes click mouse event. If stop was clicked it is added to list of alternative route.
     * To make it more visually friendly clicked stops changes the color.
     * @param event when mouse is clicked
     */
    @FXML
    private void handleOnMouseClicked(MouseEvent event)
    {
        event.consume();

        if (event.getTarget() instanceof Text) {//if it is stop

            String alt_stop =((Text) event.getTarget()).getText();//get id of a stop
            for(Stop stop : stops_list){//looping over all stops

                if (alt_stop == stop.getId()) {// if names of stops are the same (undo)
                    if(alt_road_list.contains(stop)){//if stops is already in list remove it
                        alt_box.getChildren().add(new Circle(stop.getCoordinates().getX(), stop.getCoordinates().getY(), 15, Color.CYAN));
                        alt_box.getChildren().add(new Text(stop.getCoordinates().getX() - 7.5, stop.getCoordinates().getY() + 5, alt_stop)); //constants just for visual fixes
                        alt_road_list.remove(stop);

                    }
                    else {// if it is not in the list add it and change color
                        alt_box.getChildren().add(new Circle(stop.getCoordinates().getX(), stop.getCoordinates().getY(), 15, Color.GREENYELLOW));
                        alt_box.getChildren().add(new Text(stop.getCoordinates().getX() - 7.5, stop.getCoordinates().getY() + 5, alt_stop)); //constants just for visual fixes
                        alt_road_list.add(stop);
                    }
                }
            }
        }
    }
}
