package sample;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;

import java.util.List;

public class Street  implements Draw {

    private Coordinate start;
    private Coordinate stop;
    private String id;

    public Street(String id, Coordinate start, Coordinate stop) {
        this.id = id;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                new Text((start.getX() + stop.getX()) / 2, (start.getY() + stop.getY())/ 2 , id),
                new Line(start.getX(), start.getY(), stop.getX(), stop.getY())
        );
    }
}
