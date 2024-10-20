import java.util.List;

public class Chromosome {

    private List<Station> stations;
    private int fitness;
    private Grid grid;

    public Chromosome(List<Station> stations, Grid grid) {
        this.stations = stations;
        this.grid = grid;
        this.fitness = calculateFitness(stations);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getFitness() {
        return fitness;
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chromosome:\n");
        for (Station station : stations) {
            sb.append(station.toString()).append("\n");
        }
        return sb.toString();
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
        this.fitness = calculateFitness(stations);
    }

    public int calculateFitness(List<Station> stations) {
        int fitness = 0;

        for (int i = 0; i < stations.size(); i++) {
            Station x = stations.get(i);
            String type = x.getType();
            for (int j = i + 1; j < stations.size(); j++) {
                Station side = stations.get(j);
                String sideType = side.getType();

                if (affinityCheck(type, sideType)) {
                    fitness += 1;
                }
            }
        }

        return fitness;

    }

    public boolean affinityCheck(String typeX, String typeY) {
        if ((typeX.equals("a") && typeY.equals("d")) || (typeX.equals("d") && typeY.equals("a"))
                || (typeX.equals("c") && typeY.equals("b")) || (typeX.equals("b") && typeY.equals("c"))) {
            return true;
        }
        return false;
    }

}