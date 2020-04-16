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
import java.util.ArrayList;
import java.util.Arrays;
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
                    JSONObject CoorObj = (JSONObject) ArrayCoordinates .get(k);
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
                }



                JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList");
                for (int j = 0; j < ArrayStop.size(); j++) {
                    JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                    String nameStop =(String) StopObj.get("nameStop");
                    String xStop = (String) StopObj.get("x");
                    String yStop = (String) StopObj.get("y");
                    double stopx = Double.parseDouble(xStop);
                    double stopy = Double.parseDouble(yStop);
                    //elements.add(new Stop(new Coordinate(stopx,stopy), nameStop, name));
                }

                streetCoor.clear();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        //linka 1 -  W Z I H G X D
        elements.add(new Bus(new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(60, 60),
                new Coordinate(480, 125),
                new Coordinate(520, 250),
                new Coordinate(320, 395),
                new Coordinate(300, 500),
                new Coordinate(560, 700),
                new Coordinate(300, 750),
                new Coordinate(120, 600)
        ))));
        //linka 2 - Y K O R E B A
        elements.add(new Bus(new Coordinate(100, 100), 20, new Path(Arrays.asList(
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
        elements.add(new Bus(new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(480, 125),
                new Coordinate(520, 250),
                new Coordinate(750, 410),
                new Coordinate(900, 250),
                new Coordinate(920, 520),
                new Coordinate(750, 410),
                new Coordinate(560, 700),
                new Coordinate(850, 570)
        ))));



        controller.setElements(elements);
        controller.startTime();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
