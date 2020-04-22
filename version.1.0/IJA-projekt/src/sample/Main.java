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
        List<Stop>  arraystop = new ArrayList<>();
        Line line;
        List<Coordinate> arraypath = new ArrayList<>();
        List<Street> arraystreet = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("data/data.json"));
            JSONObject jsonObject = (JSONObject)obj;

            JSONArray ArrayStreets = (JSONArray) jsonObject.get("streets");

            for (int i = 0; i < ArrayStreets.size(); i++) {
                JSONObject StreetObj = (JSONObject) ArrayStreets.get(i);
                String name =(String) StreetObj.get("name");

                JSONArray ArrayCoordinates = (JSONArray) StreetObj.get("coordinates");
                for (int k = 0; k < ArrayCoordinates.size(); k++) {
                    JSONObject CoorObj = (JSONObject) ArrayCoordinates.get(k);
                    String xcoor =(String) CoorObj.get("x");
                    String ycoor = (String) CoorObj.get("y");
                    double x = Double.parseDouble(xcoor);
                    double y = Double.parseDouble(ycoor);
                    streetCoor.add(new Coordinate(x, y));


                    System.out.println(xcoor);
                    System.out.println(ycoor);
                }

                int size = streetCoor.size();
                System.out.println("Size of list = " + size);
                for(int l = 0; l < (size - 1); l++ ){
                    elements.add(new Street(name, streetCoor.get(l), streetCoor.get(l+1)));
                    arraystreet.add(new Street(name, streetCoor.get(l), streetCoor.get(l+1)));
                }



                JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList");
                for (int j = 0; j < ArrayStop.size(); j++) {
                    JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                    String nameStop =(String) StopObj.get("nameStop");
                    String xStop = (String) StopObj.get("x");
                    String yStop = (String) StopObj.get("y");
                    double stopx = Double.parseDouble(xStop);
                    double stopy = Double.parseDouble(yStop);
                    elements.add(new Stop(new Coordinate(stopx,stopy), nameStop, name));
                    arraystop.add(new Stop(new Coordinate(stopx,stopy), nameStop, name));
                    /*System.out.println(arraystop.get(j));
                    elements.add(arraystop.get(j));*/
                }


                streetCoor.clear();
            }

            JSONObject jsonObjLINE = (JSONObject)obj;
            JSONArray ArrayLine = (JSONArray) jsonObjLINE.get("line");
            for (int m = 0; m < ArrayLine.size(); m++) {
                JSONObject LineObj = (JSONObject) ArrayLine.get(m);
                String LineNum = (String) LineObj.get("lineNumber");
                System.out.println(LineNum);
                JSONArray ArrayPath = (JSONArray) LineObj.get("StopList");
                /*Iterator<String> iterator = ArrayPath.iterator();
                while(iterator.hasNext()) {
                    System.out.println(iterator.next());
                }*/
                for(int n = 0; n < ArrayPath.size(); n++ ){
                    linepath.add((String) ArrayPath.get(n));
                    System.out.println(linepath.get(n));
                }


                line = new Line(LineNum, linepath);
                arraypath = line.getRealPath(arraystop, arraystreet);
                for (int o = 0; o<arraypath.size(); o++){
                    System.out.println(arraypath.get(o));
                }

                elements.add(new Bus(LineNum, arraypath.get(0), 20, new Path(arraypath)));

                linepath.clear();
                //arraypath.clear();





            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        //elements.add(new Bus("1", new Coordinate(100, 100), 20, new Path(arraypath)));

        //linka 1 -  W Z I H G X D
        /*elements.add(new Bus("1", new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(60, 60),
                new Coordinate(480, 125),
                new Coordinate(520, 250),
                new Coordinate(320, 395),
        new Coordinate(300, 500),
                new Coordinate(560, 700),
                new Coordinate(300, 750),
                new Coordinate(120, 600)
        ))));*/
        //linka 2 - Y K O R E B A
       /* elements.add(new Bus("2", new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(560, 700),
                new Coordinate(940, 840),
                new Coordinate(850, 570),
                new Coordinate(750, 410),
                new Coordinate(560, 700),
                new Coordinate(520, 250),
                new Coordinate(200, 200),
                new Coordinate(60, 60)

        ))));
        // linka 3 - Z S T U N R J
        elements.add(new Bus( "3", new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(480, 125),
                new Coordinate(520, 250),
                new Coordinate(750, 410),
                new Coordinate(900, 250),
                new Coordinate(920, 520),
                new Coordinate(750, 410),
                new Coordinate(560, 700),
                new Coordinate(850, 570)
        ))));
        */


        controller.setElements(elements);
        controller.startTime();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
