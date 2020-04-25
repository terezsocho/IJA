package Controllers;

import Interfaces.Draw;
import Interfaces.TimeUpdate;
import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class map_controller {
    @FXML
    private Pane map_box;
    private List<TimeUpdate> updates = new ArrayList<>();
    private List<Draw> elements = new ArrayList<>();
    private Timer timer;
    private LocalTime time = LocalTime.of(10,43,12); //inicializes to a same time
    //private LocalTime time = LocalTime.now(); //inicializes to a current time

    @FXML
    private void onZoom(ScrollEvent event_zoom){
       // System.out.println("controller linked");
        event_zoom.consume();
        double zoom = event_zoom.getDeltaY() > 0 ? 1.1 : 0.9;
        map_box.setScaleX(zoom * map_box.getScaleX());
        map_box.setScaleY(zoom * map_box.getScaleY());
        map_box.layout();
    }
    public void setElements(List<Draw> elements) {
        this.elements = elements;
        map_box.getChildren().clear();
        for (Draw draw : elements){ //Draw draw = elements[i];
            map_box.getChildren().addAll(draw.getGUI());//paints all the elements onto the scene
            //map_box.getChildren().addAll(new Circle(150, 150, 18, Color.BLUE));
            if (draw instanceof TimeUpdate) {
                updates.add((TimeUpdate) draw);
            }
        }
    }
    public void startTime(map_controller map_controller){
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {//new timertask
            @Override
            public void run() {
                time = time.plusSeconds(1); // increase time every seconds with a second
                System.out.println("Time is: " + time);
                for(TimeUpdate update : updates){
                    update.update(time);
                }
            }
        }, 0, 1000);//period establishes duration between updates
    }
}
