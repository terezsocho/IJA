package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.abs;

public class Bus implements Draw, TimeUpdate {

    private Coordinate position;
    private double speed = 0;
    private double distance = 0;
    private Path path;
    private List<Shape> gui = new ArrayList<>();
    private String id;
    private List<Coordinate> line_coordinates;

    /**
     *
     * @param id
     * @param coordinates
     * @param speed
     * @param path
     */
    public Bus(String id, List<Coordinate> coordinates, double speed, Path path) {
        this.line_coordinates = coordinates;
        this.position = coordinates.get(0);
        this.speed = speed;
        this.path = path;
        this.id = id;
        gui.add(new Circle(coordinates.get(0).getX(), coordinates.get(0).getY(), 12, Color.RED));
        gui.add(new Text(coordinates.get(0).getX(), coordinates.get(0).getY(), id));
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    private double getDistance(Coordinate a, Coordinate b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
    }

    int index = 1;
    boolean behind_bus_stop = false;
    int text_and_bus_passed = 0;
    private void moveGUI(Coordinate coordinate){

    for(Shape shape : gui){
            System.out.println("getDistance(position,line_coordinates.get(index): "+getDistance(position,line_coordinates.get(index)));
           /* System.out.println("text_and_bus_passed: "+text_and_bus_passed);
            System.out.println("position.getX(): "+position.getX());*/
        //distance needs to be less than the speed that bus makes in 1 step..
            if(getDistance(position,line_coordinates.get(index) ) < speed && behind_bus_stop == false) {
                shape.setTranslateX((line_coordinates.get(index).getX() - position.getX()) + shape.getTranslateX());
                shape.setTranslateY((line_coordinates.get(index).getY() - position.getY()) + shape.getTranslateY());
                text_and_bus_passed++;
                if (text_and_bus_passed %2 == 0) {//there are 2 elements in gui, text and circle.. both need to go together
                    behind_bus_stop = true;
                    this.position = line_coordinates.get(index);//set new position at a bus stop or intersection
                    index++;//increment to check value againt next intersection or bus stop
                }
            }
            else {
                shape.setTranslateX((coordinate.getX() - position.getX()) + shape.getTranslateX());
                shape.setTranslateY((coordinate.getY() - position.getY()) + shape.getTranslateY());
                text_and_bus_passed++;
                if (text_and_bus_passed %2 == 0) {//there are 2 elements in gui, text and circle.. both need to go together
                    this.position = coordinate; // set new position as position + speed
                    behind_bus_stop = false;
                }
            }
            System.out.println("/////////////////////////////////");
        }

    }
    private void removeGUI(){
        for(Shape shape : gui){
            shape.setVisible( false );//toggle visibility to false
        }
    }

    @Override
    public List<Shape> getGUI(){
        return gui;
    }

    boolean set_immutable_bus_stop = true;
    @Override
    public void update(LocalTime time){
        distance = distance + speed;
        if(distance > path.getPathSize()){
            // only moves it to final destination once, if repeated moveGUi still moves it by small margin
            if( set_immutable_bus_stop == true) {
                System.out.println(path.getPathSize());
                Coordinate coords = path.getCoorBus(path.getPathSize());
                moveGUI(coords);
                removeGUI();
                set_immutable_bus_stop = false;
            }
            return;
        }
        Coordinate coords = path.getCoorBus(distance);
        moveGUI(coords);

    }

}
