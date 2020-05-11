package sources;

import Interfaces.Draw;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;

import java.util.List;

public class Stop  implements Draw{
    private String name;
    private Coordinate coordinates;
    private String on_street;

    public Stop(Coordinate coordinates,String name, String on_street) {
        this.name = name;
        this.coordinates = coordinates;
        this.on_street = on_street;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public String getOn_street() {
        return on_street;
    }

    @Override
    public List<Shape> getGUI(){
        return Arrays.asList(
                new Circle(coordinates.getX(), coordinates.getY(), 15, Color.AQUA),
                new Text(coordinates.getX()-7.5, coordinates.getY()+5, name) //constants just for visual fixes
        );
    }
    @Override
    public String getId(){
        return this.name;
    }

}
