package sample;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    @FXML
    private Pane map_box;
    private List<TimeUpdate> updates = new ArrayList<>();
    private List<Draw> elements = new ArrayList<>();
    private Timer timer;
    private LocalTime time = LocalTime.now();


    @FXML
    private void onZoom(ScrollEvent event_zoom){
       // System.out.println("controller linked");
        event_zoom.consume();
        double zoom;
        zoom = event_zoom.getDeltaY() > 0 ? 1.1 : 0.9;
        map_box.setScaleX(zoom * map_box.getScaleX());
        map_box.setScaleY(zoom * map_box.getScaleY());
        map_box.layout();

    }

    public void setElements(List<Draw> elements) {
        this.elements = elements;
        for (Draw draw : elements){
            map_box.getChildren().addAll(draw.getGUI());
            if (draw instanceof TimeUpdate){
                updates.add((TimeUpdate) draw);

            }
        }

    }

    public void startTime(){
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time.plusSeconds(1);
                for(TimeUpdate update : updates){
                    update.update(time);
                }
            }
        }, 0, 1000);
    }
}
