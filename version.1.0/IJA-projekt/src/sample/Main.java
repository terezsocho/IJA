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


                /*String  coorStartX = (String) StreetObj.get("coorStartX");
                String coorStartY =(String) StreetObj.get("coorStartY");
                String coorEndX =(String) StreetObj.get("coorEndX");
                String coorEndY =(String) StreetObj.get("coorEndY");
                double xs = Double.parseDouble(coorStartX);
                double ys = Double.parseDouble(coorStartY);
                double xe = Double.parseDouble(coorEndX);
                double ye = Double.parseDouble(coorEndY);*/
                //elements.add(new Street(name, new Coordinate(xs, ys), new Coordinate(xe, ye)));

                /*System.out.println(name);
                System.out.println(coorStartX);
                System.out.println(coorStartY);
                System.out.println(coorEndX);
                System.out.println(coorEndY);*/

                JSONArray ArrayStop = (JSONArray) StreetObj.get("stopList");
                for (int j = 0; j < ArrayStop.size(); j++) {
                    JSONObject StopObj = (JSONObject) ArrayStop.get(j);
                    String nameStop =(String) StopObj.get("nameStop");
                    String xStop = (String) StopObj.get("x");
                    String yStop = (String) StopObj.get("y");
                    /*System.out.println(nameStop);
                    System.out.println(xStop);
                    System.out.println(yStop);*/
                }

                streetCoor.clear();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }


        elements.add(new Bus(new Coordinate(100, 100), 20, new Path(Arrays.asList(
                new Coordinate(100, 100),
                new Coordinate(500, 500)
        ))));

        controller.setElements(elements);
        controller.startTime();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
