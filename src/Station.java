import java.util.*;

public class Station {
    public String type;// a=[1X1], b=[2X2], c=[1,2], d=[2,1]
    public int id; // unique identifier for each shape since there is going to be 60
    public List<int[]> position; // list of top right corners

    public Station(String type, int id) {
        this.type = type;
        this.id = id;
        this.position = new ArrayList<>();
    }

    public List<int[]> getPosition() {
        return position;
    }

    public void setPosition(List<int[]> position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(type).append(", Positions: ");
        for (int[] pos : position) {
            sb.append("[").append(pos[0]).append(", ").append(pos[1]).append("] ");
        }
        return sb.toString();
    }

}