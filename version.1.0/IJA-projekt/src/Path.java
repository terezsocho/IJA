import java.util.List;

public class Path {

    private List<Coordinate> path;

    public Path(List<Coordinate> path) {
        this.path = path;
    }

    private double getDistance(Coordinate a, Coordinate b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
    }

    public Coordinate getCoorBus(double distance){
        double len = 0;
        Coordinate a = null;
        Coordinate b = null;
        for (int i = 0; i < path.size() - 1; i++){
             a = path.get(i);
             b = path.get(i+1);

            if (len + getDistance(a, b) >= distance) {
                break;
            }

            len += getDistance(a, b);
        }
        if(a == null || b == null){
            return null;
        }
        double driven = (distance - len) / getDistance(a,b);
        return new Coordinate(a.getX() + (b.getX()-a.getX()) * driven, a.getY() + (b.getY()-a.getY()) * driven );

    }

    public double getPathSize(){
        double size = 0;
        for (int i = 0; i < path.size() - 1; i++ ){
            size = size + getDistance(path.get(i), path.get(i+1));
        }
        return size;
    }
}
