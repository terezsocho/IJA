package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private String id;
    private List<String> path;
    private List<Stop> temp_array = new ArrayList<>();
    private List<Street> temp_array_street = new ArrayList<>();
    List<Coordinate> realpath = new ArrayList<>();
    private String streetname;
    private Street street;

    public Line(String id, List<String> path) {
        this.id = id;
        this.path = path;

    }

    public List<Coordinate> getRealPath(List<Stop> arraystop, List<Street> arraystreet){
        /*for (int o = 0; o<path.size(); o++){
                    System.out.println(path.get(o));
        }*/
        for(int r = 0; r<path.size(); r++){
            for (int p = 0; p < arraystop.size(); p++){
                if(path.get(r).equals(arraystop.get(p).getName())){
                    temp_array.add(arraystop.get(p));
                }
            }
        }
        for (int o = 0; o<temp_array.size(); o++){
                    System.out.println(temp_array.get(o).getOn_street());
        }
        for(int a = 0; a<temp_array.size(); a++){
            for (int b = 0; b < arraystreet.size(); b++){
                if(temp_array.get(a).getOn_street().equals(arraystreet.get(b).getId())){
                    temp_array_street.add(arraystreet.get(b));
                }
            }
        }

//*******************************************************************************************
        street =  temp_array_street.get(0);
        for (int i = 0; i<path.size(); i++){
            //System.out.println(stopname);
            //System.out.println(arraystop.get(j).getName());
            if (( i == 0 ) || ( i == (path.size()-1))){
                realpath.add(temp_array.get(i).getCoordinates());
            }
            else{
                if (temp_array.get(i).getOn_street().equals(streetname)){
                    realpath.add(temp_array.get(i).getCoordinates());
                }
                else{
                    if(street.getStart().equals(temp_array_street.get(i).getStart())){
                        realpath.add(street.getStart());
                    }
                    else if(street.getStart().equals(temp_array_street.get(i).getStop())){
                        realpath.add(street.getStart());
                    }
                    else if(street.getStop().equals(temp_array_street.get(i).getStop())){
                        realpath.add(street.getStop());
                    }
                    else{
                        realpath.add(street.getStop());

                    }
                    /*start = temp_array_street.get(i-1).getStart();
                    end = temp_array_street.get(i-1).getStop();

                    if(temp_array.get(i).getCoordinates().getX()){
                        realpath.add(start);
                    }
                    else{
                        realpath.add(end);
                    }

                    //System.out.println("to be continued");
*/

                    realpath.add(temp_array.get(i).getCoordinates());
                }
                    //if(path.get(i)){}


            }
            streetname = temp_array.get(i).getOn_street();
            street = temp_array_street.get(i);
        }

        /*for (int o = 0; o<realpath.size(); o++){
                    System.out.println(realpath.get(o));
        }*/


        //realpath.add(new Coordinate(12,15));
        return realpath;

    }



}
