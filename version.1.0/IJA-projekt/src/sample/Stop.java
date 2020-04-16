package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;

import java.util.List;

public class Stop  implements Draw {
    private String name;
    private Coordinate coordinates;
    private String on_street;

    public Stop(Coordinate coordinates,String name, String on_street) {
        this.name = name;
        this.coordinates = coordinates;
        this.on_street = on_street;
    }

    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                //new Text((start.getX() + stop.getX()) / 2, (start.getY() + stop.getY())/ 2 , id),
                new Circle(coordinates.getX(), coordinates.getY(), 20, Color.AQUA)

        );
    }
}
