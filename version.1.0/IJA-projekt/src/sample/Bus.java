package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Bus implements Draw, TimeUpdate {

    private Coordinate position;
    private double speed = 0;
    private double distance = 0;
    private Path path;
    private List<Shape> gui;

    public Bus(Coordinate position, double speed, Path path) {
        this.position = position;
        this.speed = speed;
        this.path = path;
        gui = new ArrayList<>();
        gui.add(new Circle(position.getX(), position.getY(), 12, Color.RED));
    }
    private void moveGUI(Coordinate coordinate){
        for(Shape shape : gui){
            shape.setTranslateX( (coordinate.getX()- position.getX()) + shape.getTranslateX() );
            shape.setTranslateY( (coordinate.getY()- position.getY()) + shape.getTranslateY() );
        }
    }

    @Override
    public List<Shape> getGUI(){
        return gui;
    }

    @Override
    public void update(LocalTime time){
        distance = distance + speed;
        if(distance > path.getPathSize()){
            return;
        }
        Coordinate coords = path.getCoorBus(distance);
        moveGUI(coords);
        position = coords;
    }

}
