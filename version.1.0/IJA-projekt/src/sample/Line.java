package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private String id;
    private List<String> path;
    private List<Stop> temp_array_stops = new ArrayList<>();
    private List<Street> temp_array_street = new ArrayList<>();
    private List<Coordinate> realpath = new ArrayList<>();
    private String streetname;
    private Street street;

    public Line(String id, List<String> path) {
        this.id = id;
        this.path = path;

    }

    public List<Coordinate> getRealPath(List<Stop> arraystop, List<Street> arraystreet){
        /*for (int o = 0; o<path.size(); o++){
                    System.out.println(path.get(o));
        }
        System.out.println("///////////////////////////////////////////////////////////");
        for (int o = 0; o<arraystop.size(); o++){
            System.out.println(arraystop.get(o).getName());
        }*/
        for(int r = 0; r<path.size(); r++){
            for (int p = 0; p < arraystop.size(); p++){
                if(path.get(r).equals(arraystop.get(p).getName())){// check if stop with the name exists
                    temp_array_stops.add(arraystop.get(p));//if yes add it to the temporary array of stops
                }
            }
        }
        /*
        for (int o = 0; o<temp_array_stops.size(); o++){
                    System.out.println(temp_array_stops.get(o).getOn_street());
        }*/
        for(int a = 0; a<temp_array_stops.size(); a++){
            for (int b = 0; b < arraystreet.size(); b++){
                if(temp_array_stops.get(a).getOn_street().equals(arraystreet.get(b).getId())){//check if temp_array_stops contains only stops on valid streets
                    temp_array_street.add(arraystreet.get(b)); //if yes add it to the temporary array of streets
                }
            }
        }

//*******************************************************************************************
        street = temp_array_street.get(0); // assign first street too variable street
        for (int i = 0; i < path.size(); i++){
            //System.out.println(stopname);
            //System.out.println(arraystop.get(i).getName());
            if (( i == 0 ) || ( i == (path.size()-1))){//if first or last item is detected
                realpath.add(temp_array_stops.get(i).getCoordinates());//add to the list of coordinates realpath coordinates of that stop
            }
            else {
                if (temp_array_stops.get(i).getOn_street().equals(streetname)) {//if street that stop is mounted on is equal to the streetname, so stop is on the same street as revious
                    realpath.add(temp_array_stops.get(i).getCoordinates());//add it to the list of coordinates realpath
                } else {
                    if (street.get_Start_coord().equals(temp_array_street.get(i).get_Start_coord())) {//check if streets have same starting coordinates
                        realpath.add(street.get_Start_coord());//if yes add this starting coordinate into a realpath
                    } else if (street.get_Start_coord().equals(temp_array_street.get(i).get_End_coord())) {
                        realpath.add(street.get_Start_coord());
                    } else if (street.get_End_coord().equals(temp_array_street.get(i).get_End_coord())) {
                        realpath.add(street.get_End_coord());
                    } else {
                        realpath.add(street.get_End_coord());
                    }
                    realpath.add(temp_array_stops.get(i).getCoordinates());
                }
            }

            streetname = temp_array_stops.get(i).getOn_street();
            street = temp_array_street.get(i);
        }

        /*for (int o = 0; o<realpath.size(); o++){
                    System.out.println(realpath.get(o));
        }*/

        //realpath.add(new Coordinate(12,15));
        return realpath;

    }



}
