import Interfaces.Draw;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;

import java.util.List;

public class Street implements Draw {

    private Coordinate start_coord;
    private Coordinate end_coord;
    private String id;

    public Street(String id, Coordinate start, Coordinate stop) {
        this.id = id;
        this.start_coord = start;
        this.end_coord = stop;
    }

    public String getId() {
        return id;
    }

    public Coordinate get_Start_coord() {
        return start_coord;
    }

    public Coordinate get_End_coord() {
        return end_coord;
    }

    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                new Text((start_coord.getX() + end_coord.getX()) / 2, (start_coord.getY() + end_coord.getY())/ 2 , id),
                new Line(start_coord.getX(), start_coord.getY(), end_coord.getX(), end_coord.getY())
        );
    }
}
