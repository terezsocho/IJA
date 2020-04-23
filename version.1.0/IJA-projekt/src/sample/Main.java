package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Application of public transport");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();

        Controller controller = loader.getController();
        List<Draw> elements = new ArrayList<>();
        List<Coordinate> streetCoor = new ArrayList<>();
        List<String>  linepath = new ArrayList<>();
        Line line = null;
        List<Coordinate> arraypath = new ArrayList<>();
        List<Street> arraystreet = new ArrayList<>();
        List<Stop>  arraystop = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            //each parsing needs new object
            Object obj = parser.parse(new FileReader("data/data.json"));
            streets_dejson(obj, arraystop, arraystreet, streetCoor, elements);
            lines_dejson(obj, arraypath, line, linepath, elements, arraystop, arraystreet);
        } catch(Exception e) {
            e.printStackTrace();
        }
        controller.setElements(elements);
        controller.startTime();

    }

    /**
     * Method parses and inicialises private variables with data taken in JSON format
     * @param obj parsed data from file data.json
     * @param arraystop list of stops for a street
     * @param arraystreet list of streets
     * @param streetCoor coordinates of streets
     * @param elements list of eleemnts to be drawn onto a screen
     */
    public void streets_dejson(Object obj, List<Stop>  arraystop,List<Street> arraystreet,  List<Coordinate> streetCoor,List<Draw> elements ){
        JSONObject jsonObject = (JSONObject)obj; // conversion of object to jsonobject
        JSONArray ArrayStreets = (JSONArray) jsonObject.get("streets"); // streets from json stored into jsonarray

        for (int i = 0; i < ArrayStreets.size(); i++) { // loop through the streets in jsonarray ArrayStreets
            JSONObject StreetObj = (JSONObject) ArrayStreets.get(i); //store street on index i into additional object StreetObj
            String name =(String) StreetObj.get("name"); // conversion of street name to string from jsonobject

            JSONArray ArrayCoordinates = (JSONArray) StreetObj.get("coordinates"); // store coordinates of json object into json array ArrayCoordinates
            for (int k = 0; k < ArrayCoordinates.size(); k++) { // loop through coordinates in ArrayCoordinates
                JSONObject CoorObj = (JSONObject) ArrayCoordinates.get(k);
                String xcoor =(String) CoorObj.get("x");
                String ycoor = (String) CoorObj.get("y");
                double x = Double.parseDouble(xcoor); //convert strings to double
                double y = Double.parseDouble(ycoor);
                streetCoor.add(new Coordinate(x, y)); //adding coordinates into a list of coordinates streetCoord
                //System.out.println(xcoor);
                //System.out.println(ycoor);
            }
            int size = streetCoor.size();
            //System.out.println("Size of list = " + size);
            for(int l = 0; l < (size - 1); l++ ){ //loop throught list of coordinates streetCoor
                elements.add(new Street(name, streetCoor.get(l), streetCoor.get(l+1))); // add street to list of items to be drawn
                arraystreet.add(new Street(name, streetCoor.get(l), streetCoor.get(l+1))); //add street to list of streets
            }

            JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList"); //stores into jsonArray list of stops from json
            for (int j = 0; j < ArrayStop.size(); j++) {//loop through all stops of specific street
                JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                String nameStop =(String) StopObj.get("nameStop");
                String xStop = (String) StopObj.get("x");
                String yStop = (String) StopObj.get("y");
                double stopx = Double.parseDouble(xStop);//convert coordinates into double
                double stopy = Double.parseDouble(yStop);
                elements.add(new Stop(new Coordinate(stopx,stopy), nameStop, name)); // add new stop into list of things to be drawn
                arraystop.add(new Stop(new Coordinate(stopx,stopy), nameStop, name));// add new stop into list of stops
                    /*System.out.println(arraystop.get(j));
                    elements.add(arraystop.get(j));*/
            }
            streetCoor.clear(); // clear the buffer
        }
        return;
    }

    /**
     * @param obj parsed data from file data.json
     * @param arraypath list of coordinates
     * @param line line that vehicles travels on
     * @param linepath list of string(names of stops)
     * @param elements list of eleemnts to be drawn onto a screen
     * @param arraystop list of stops for a street
     * @param arraystreet list of streets
     */
    public void lines_dejson(Object obj, List<Coordinate> arraypath, Line line,List<String> linepath,
                             List<Draw> elements, List<Stop> arraystop, List<Street> arraystreet ) {
        JSONObject jsonObjLINE = (JSONObject) obj;//store object into JSON Object
        JSONArray ArrayLine = (JSONArray) jsonObjLINE.get("line"); // create array from all instances of line from JSON
        for (int m = 0; m < ArrayLine.size(); m++) {// loop through all lines stored in an array ArrayLine
            JSONObject LineObj = (JSONObject) ArrayLine.get(m);//take one line at a time
            String LineNum = (String) LineObj.get("lineNumber");
            JSONArray ArrayPath = (JSONArray) LineObj.get("StopList"); // store into array names of all stops line is required to stop at
                /*Iterator<String> iterator = ArrayPath.iterator();
                while(iterator.hasNext()) {
                    System.out.println(iterator.next());
                }*/
            for (int n = 0; n < ArrayPath.size(); n++) { // loop through the array of stop names
                linepath.add((String) ArrayPath.get(n)); // add it to another list of string containing stop names
               // System.out.println(linepath.get(n));
            }
            //System.out.println("///////////////////////////////");
            line = new Line(LineNum, linepath); //instantiation of line
            arraypath = line.getRealPath(arraystop, arraystreet);
            /*for (int o = 0; o < arraypath.size(); o++) {
                 System.out.println(arraypath.get(o));
            }*/

            elements.add(new Bus(LineNum, arraypath.get(0), 20, new Path(arraypath)));

            linepath.clear();
            //arraypath.clear();
        }

    }




    public static void main(String[] args) {
        launch(args);
    }
}
