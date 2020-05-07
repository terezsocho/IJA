package sources;

import Controllers.map_controller;
import Interfaces.Draw;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static int NORMAL_BUS_SPEED = 55;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Resources/public_transit.fxml"));
        Parent root = loader.load();// the root of the scene shown in the main window
        primaryStage.setTitle("Application of public transport");
        primaryStage.setScene(new Scene(root, 1350, 860));// add scene to the stage
        primaryStage.show();// make the stage visible

        map_controller map_controller = loader.getController();

        List<Draw> elements_roads = new ArrayList<>();
        List<Draw> elements_stops = new ArrayList<>();
        List<Draw> elements_vehicles = new ArrayList<>();
        List<Coordinate> streetCoor = new ArrayList<>();
        List<String> linepath = new ArrayList<>();
        BusLine busLine = null;
        List<Coordinate> arraypath = new ArrayList<>();
        List<Street> arraystreet = new ArrayList<>();
        List<Stop> arraystop = new ArrayList<>();
        JSONParser parser = new JSONParser();
        List<String> array_buslines_numbers = new ArrayList<>();
        List<LocalTime> array_buslines_leave_times = new ArrayList<>();


        //each parsing needs new object
        Object obj = parser.parse(new FileReader("data/data.json"));
        City_Map_Init(obj, arraystop, arraystreet, streetCoor, elements_roads, elements_stops);
        Traffic_Init(obj, arraypath, busLine, linepath, elements_vehicles, arraystop, arraystreet,
                array_buslines_numbers, array_buslines_leave_times);


        map_controller.setElements(elements_roads, elements_stops, elements_vehicles, array_buslines_numbers,
                                    array_buslines_leave_times, arraystreet );
        map_controller.startTime(1);

    }

    /**
     * Method gather data from JSON and stores them into appropriate variables.
     * @param obj Object containing JSOM data
     * @param stops_list List of all Stops
     * @param streets_list List of all Streets
     * @param street_Coordinates List of all Coordinates that create streets
     * @param elements_roads List of Drawable objects containing streets and their names
     * @param elements_stops List of Drawable objects containing stops and their names
     */
    public void City_Map_Init(Object obj, List<Stop> stops_list,List<Street> streets_list, List<Coordinate> street_Coordinates,
                              List<Draw> elements_roads, List<Draw> elements_stops ){
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
            elements_roads.add(new Street(name, street_Coordinates.get(0), street_Coordinates.get(1))); // add street to list of items to be drawn
            streets_list.add(new Street(name, street_Coordinates.get(0), street_Coordinates.get(1))); //add street to list of streets
            street_Coordinates.clear(); // clear the buffer
//Stops----------------------------------------------------------------------------------------------------------------------
            JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList"); //stores into jsonArray list of stops from json
            for (int j = 0; j < ArrayStop.size(); j++) {//loop through all stops of specific street
                JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                String stop_Name =(String) StopObj.get("nameStop");
                double stop_x = Double.parseDouble((String) StopObj.get("x"));//convert coordinates into double
                double stop_y = Double.parseDouble((String) StopObj.get("y"));
                elements_stops.add(new Stop(new Coordinate(stop_x,stop_y), stop_Name, name)); // add new stop into list of things to be drawn
                stops_list.add(new Stop(new Coordinate(stop_x,stop_y), stop_Name, name));// add new stop into list of stops
            }
        }
    }

    /**
     * Method initializes traffic from data gathered from JSON.
     * @param obj Object containing JSOM data
     * @param path_Coord_list List of Coordinates that bus must pass on its route
     * @param bus_Route List of Strings that illustrate stops from which bus path is created
     * @param bus_Route_path List of all Coordinates that create busline path
     * @param elements_vehicles List of Drawable objects containing buses and their names
     * @param stops_list List of all Stops
     * @param streets_list List of all Streets
     * @param array_buslines_numbers List of Strings that contains all possible bus line numbers
     * @param transit_schedule List of LocalTimes that contains all the departure times of all buses in simulation
     */
    public void Traffic_Init(Object obj, List<Coordinate> path_Coord_list, BusLine bus_Route, List<String> bus_Route_path,
                             List<Draw> elements_vehicles, List<Stop> stops_list, List<Street> streets_list,
                             List<String> array_buslines_numbers, List<LocalTime> transit_schedule ) {

        JSONObject jsonObjLINE = (JSONObject) obj;//store object into JSON Object
        JSONArray ArrayLine = (JSONArray) jsonObjLINE.get("line"); // create array from all instances of line from JSON
        List<LocalTime> transit_schedule_one_busline = new ArrayList<>();

        for (int m = 0; m < ArrayLine.size(); m++) {// loop through all lines stored in an array ArrayLine
            JSONObject LineObj = (JSONObject) ArrayLine.get(m);//take one line at a time
            String bus_Route_Number = (String) LineObj.get("lineNumber");
            JSONArray ArrayPath = (JSONArray) LineObj.get("StopList"); // store into array names of all stops line is required to stop at
            JSONArray transit_scheduled_leave = (JSONArray) LineObj.get("LeavesAt");

            for (int n = 0; n < ArrayPath.size(); n++) { // loop through the array of stop names
                bus_Route_path.add((String) ArrayPath.get(n)); // add it to another list of string containing stop names
            }
            for (int n = 0; n < transit_scheduled_leave.size(); n++) {//insert all times bus leave any station
                String least_At= (String) transit_scheduled_leave.get(n);
                int hours = Integer.parseInt(least_At.substring(0, 2));        // parsing of hh:mm:ss format
                int minutes = Integer.parseInt(least_At.substring(3, 5));
                int seconds = Integer.parseInt(least_At.substring(6, 8));
                transit_schedule.add(LocalTime.of(hours, minutes, seconds));
                transit_schedule_one_busline.add(LocalTime.of(hours, minutes, seconds));
            }

            bus_Route = new BusLine(bus_Route_Number, bus_Route_path); //instantiation of bus_Route
            path_Coord_list = bus_Route.getRealPath(stops_list, streets_list);
            List<Stop> list_stops_bus_route = bus_Route.getStops();
            List<Street> list_streets_bus_route = bus_Route.getStreets();

            for(LocalTime schedule: transit_schedule_one_busline){
                array_buslines_numbers.add(bus_Route_Number);//intentionally contains duplicates
                Bus instance_of_bus = new Bus(bus_Route_Number, new Path(path_Coord_list), schedule,
                                                list_stops_bus_route, list_streets_bus_route, NORMAL_BUS_SPEED);
                elements_vehicles.add(instance_of_bus);//add a new bus on a road
            }
            transit_schedule_one_busline.clear();
            bus_Route_path.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);//launch the application
    }
}
