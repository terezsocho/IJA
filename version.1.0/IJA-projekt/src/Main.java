import Controllers.map_controller;
import Interfaces.Draw;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Resources/public_transit.fxml"));
        Parent root = loader.load();// the root of the scene shown in the main window
        primaryStage.setTitle("Application of public transport");
        primaryStage.setScene(new Scene(root, 1100, 950));// add scene to the stage
        primaryStage.show();// make the stage visible

        map_controller map_controller = loader.getController();

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
            City_Map_Init(obj, arraystop, arraystreet, streetCoor, elements);
            Traffic_Init(obj, arraypath, line, linepath, elements, arraystop, arraystreet);

        } catch(Exception e) {
            e.printStackTrace();
        }
        map_controller.setElements(elements);
        map_controller.startTime(map_controller);
    }

    /**
     * Method parses and inicialises private variables with data taken in JSON format
     * @param obj parsed data from file data.json
     * @param stops_list list of stops for a street
     * @param streets_list list of streets
     * @param street_Coordinates coordinates of streets
     * @param elements list of eleemnts to be drawn onto a screen
     */
    public void City_Map_Init(Object obj, List<Stop> stops_list,List<Street> streets_list,  List<Coordinate> street_Coordinates,List<Draw> elements ){
        JSONObject jsonObject = (JSONObject)obj; // conversion of object to jsonobject
// Streets-------------------------------------------------------------------------------------------------------------------
        JSONArray ArrayStreets = (JSONArray) jsonObject.get("streets"); // streets from json stored into jsonarray
        for (int i = 0; i < ArrayStreets.size(); i++) { // loop through the streets in jsonarray ArrayStreets
            JSONObject StreetObj = (JSONObject) ArrayStreets.get(i); //store street on index i into additional object StreetObj
            String name =(String) StreetObj.get("name"); // conversion of street name to string from jsonobject

            JSONArray ArrayCoordinates = (JSONArray) StreetObj.get("coordinates"); // store coordinates of json object into json array ArrayCoordinates
            for (int k = 0; k < ArrayCoordinates.size(); k++) { // loop through coordinates in ArrayCoordinates
                JSONObject CoorObj = (JSONObject) ArrayCoordinates.get(k);
                double street_x = Double.parseDouble((String) CoorObj.get("x")); //convert string of X coordinate to double and store it
                double street_y = Double.parseDouble((String) CoorObj.get("y")); //convert string of Y coordinate to double and store it
                street_Coordinates.add(new Coordinate(street_x, street_y)); //adding coordinates into a list of coordinates street_Coordinates
            }
            elements.add(new Street(name, street_Coordinates.get(0), street_Coordinates.get(1))); // add street to list of items to be drawn
            streets_list.add(new Street(name, street_Coordinates.get(0), street_Coordinates.get(1))); //add street to list of streets
            street_Coordinates.clear(); // clear the buffer
//Stops----------------------------------------------------------------------------------------------------------------------
            JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList"); //stores into jsonArray list of stops from json
            for (int j = 0; j < ArrayStop.size(); j++) {//loop through all stops of specific street
                JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                String stop_Name =(String) StopObj.get("nameStop");
                double stop_x = Double.parseDouble((String) StopObj.get("x"));//convert coordinates into double
                double stop_y = Double.parseDouble((String) StopObj.get("y"));
                elements.add(new Stop(new Coordinate(stop_x,stop_y), stop_Name, name)); // add new stop into list of things to be drawn
                stops_list.add(new Stop(new Coordinate(stop_x,stop_y), stop_Name, name));// add new stop into list of stops
            }
        }
    }

    /**
     * @param obj parsed data from file data.json
     * @param path_Coord_list list of coordinates
     * @param bus_Route line that vehicles travels on
     * @param bus_Route_path list of string(names of stops)
     * @param elements list of eleemnts to be drawn onto a screen
     * @param stops_list list of stops for a street
     * @param streets_list list of streets
     */
    public void Traffic_Init(Object obj, List<Coordinate> path_Coord_list, Line bus_Route,List<String> bus_Route_path,
                             List<Draw> elements, List<Stop> stops_list, List<Street> streets_list ) {

        JSONObject jsonObjLINE = (JSONObject) obj;//store object into JSON Object
        JSONArray ArrayLine = (JSONArray) jsonObjLINE.get("line"); // create array from all instances of line from JSON
        for (int m = 0; m < ArrayLine.size(); m++) {// loop through all lines stored in an array ArrayLine
            JSONObject LineObj = (JSONObject) ArrayLine.get(m);//take one line at a time
            String bus_Route_Number = (String) LineObj.get("lineNumber");
            JSONArray ArrayPath = (JSONArray) LineObj.get("StopList"); // store into array names of all stops line is required to stop at
            String transit_scheduled_leave = (String) LineObj.get("LeavesAt");

            for (int n = 0; n < ArrayPath.size(); n++) { // loop through the array of stop names
                bus_Route_path.add((String) ArrayPath.get(n)); // add it to another list of string containing stop names
            }
            //System.out.println("///////////////////////////////");
            bus_Route = new Line(bus_Route_Number, bus_Route_path); //instantiation of bus_Route
            path_Coord_list = bus_Route.getRealPath(stops_list, streets_list);

            elements.add(new Bus(bus_Route_Number, path_Coord_list, 20, new Path(path_Coord_list),transit_scheduled_leave));//add a new bus on a road
            bus_Route_path.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);//launch the application
    }
}
