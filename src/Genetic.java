import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Genetic {

    public static void main(String[] args) {

        List<Station> stations = initializeStations();

        Grid grid = new Grid(150, 150);
        for (Station station : stations) {
            grid.placeStation(station);
        }

        List<BlockingQueue<Chromosome>> queues = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            queues.add(new LinkedBlockingQueue<Chromosome>());
        }

        ConcurrentHashMap<String, Chromosome> map = new ConcurrentHashMap<>();
        ReentrantLock lock = new ReentrantLock();

        for (int i = 0; i < 35; i++) {
            Thread thread = new Thread(new Task(stations, grid, queues.get(i), map, lock), "thread" + (i + 1));
            thread.start();
        }

    }

    static class Task implements Runnable {
        private final List<Station> stations;
        private final Grid grid;
        private final BlockingQueue<Chromosome> queue;
        ConcurrentHashMap<String, Chromosome> map;
        ReentrantLock lock;

        public Task(List<Station> stations, Grid grid, BlockingQueue<Chromosome> queue,
                ConcurrentHashMap<String, Chromosome> map, ReentrantLock lock) {
            this.stations = stations;
            this.grid = grid;
            this.queue = queue;
            this.map = map;
            this.lock = lock;
        }

        public void run() {
            Chromosome initialChromosome = new Chromosome(stations, grid);
            Chromosome mutateChromosome;
            while (true) {
                for (int i = 0; i < 20; i++) {
                    mutateChromosome = mutate(initialChromosome);
                    try {
                        queue.put(mutateChromosome);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    initialChromosome = mutateChromosome;
                }

                Chromosome favoriteChromosome = findFavoriteChromosome(queue);

                lock.lock();
                try {
                    map.put(Thread.currentThread().getName(), favoriteChromosome);
                } finally {
                    lock.unlock();
                }

                if (map.size() == 34) {
                    crossover(map);
                }

            }
        }

    }

    public static void crossover(ConcurrentHashMap<String, Chromosome> map) {

        List<Chromosome> chromosomes = new ArrayList<>();
        for (String key : map.keySet()) {
            chromosomes.add(map.get(key));
        }

        while (chromosomes.size() > 1) {
            chromosomes = reduceChromosomes(chromosomes);
        }

        Chromosome result = chromosomes.get(0);

        System.out.println(result);

    }

    public static List<Chromosome> reduceChromosomes(List<Chromosome> chromosomes) {
        List<Chromosome> newChild = new ArrayList<>();
        for (int i = 0; i < chromosomes.size(); i += 2) {
            if (i + 1 < chromosomes.size()) {
                Chromosome parent1 = chromosomes.get(i);
                Chromosome parent2 = chromosomes.get(i + 1);
                Chromosome child = crossoverFinal(parent1, parent2);
                newChild.add(child);
            }
        }
        return newChild;
    }

    public static Chromosome crossoverFinal(Chromosome parent1, Chromosome parent2) {

        List<Station> station1 = parent1.getStations();
        List<Station> station2 = parent2.getStations();
        List<Station> stationChild = new ArrayList<>();

        Grid gridChild = new Grid(150, 150);

        for (Station station : station1) {
            if (!(station.getType().equals("a"))) {
                stationChild.add(station);

                for (int[] position : station.getPosition()) {
                    gridChild.occupy(position[0], position[1]);
                }

            }
        }

        for (Station station : station2) {
            if (station.getType().equals("a")) {
                if (gridChild.check(station.getPosition().get(0)[0], station.getPosition().get(0)[1])) {
                    stationChild.add(station);
                    gridChild.occupy(station.getPosition().get(0)[0], station.getPosition().get(0)[1]);
                } else {
                    gridChild.placeStation(station);
                    stationChild.add(station);
                }
            }
        }

        return new Chromosome(stationChild, gridChild);

    }

    // public static Chromosome crossoverFinal(Chromosome parent1, Chromosome
    // parent2) {
    // List<Station> station1 = parent1.getStations();
    // List<Station> station2 = parent2.getStations();
    // List<Station> stationChild = new ArrayList<>();

    // Grid gridChild = new Grid(150, 150);

    // for (Station station : station1) {
    // stationChild.add(station);
    // gridChild.occupy(station.getPosition().get(0)[0],
    // station.getPosition().get(0)[1]);
    // }

    // for (int i = 0; i < stationChild.size(); i++) {
    // Station one = stationChild.get(i);
    // Station two = stationChild.get(i + 1);

    // if (one.type.equals(two.type)) {
    // for (Station rep : station2) {

    // }
    // }
    // }

    // }

    // Chromosome parent1 = map.get("thread1");
    // Chromosome parent2 = map.get("thread2");
    // Chromosome parent3 = map.get("thread3");
    // Chromosome parent4 = map.get("thread4");
    // Chromosome childA = crossoverFinal(parent1, parent2);
    // Chromosome childB = crossoverFinal(parent3, parent4);
    // if (childA.getFitness() > childB.getFitness()) {
    // Chromosome finalChild = childA;
    // } else {
    // Chromosome finalChild = childB;
    // }

    public static Chromosome mutate(Chromosome chromosome) {
        Grid gridX = chromosome.getGrid();

        List<Station> stations = chromosome.getStations();
        int size = stations.size();
        int[] rand = new int[5];
        for (int x = 0; x < 5; x++) {
            rand[x] = ThreadLocalRandom.current().nextInt(0, size);
        }
        for (int i = 0; i < rand.length; i++) {
            Station station = stations.get(rand[i]);
            String type = station.type;
            List<int[]> pos = station.getPosition();

            if (type.equals("c")) {
                int a = pos.get(0)[0];
                int b = pos.get(0)[1];
                if (gridX.check(a + 1, b)) {
                    List<int[]> newPos = new ArrayList<>();
                    newPos.add(new int[] { a, b });
                    newPos.add(new int[] { a + 1, b });
                    station.setPosition(newPos);
                    gridX.occupy(a + 1, b);
                    gridX.remove(a, b + 1);
                }
            } else if (type.equals("d")) {
                int a = pos.get(0)[0];
                int b = pos.get(0)[1];
                if (gridX.check(a, b + 1)) {
                    List<int[]> newPos = new ArrayList<>();
                    newPos.add(new int[] { a, b });
                    newPos.add(new int[] { a, b + 1 });
                    station.setPosition(newPos);
                    gridX.occupy(a, b + 1);
                    gridX.remove(a + 1, b);
                }

            }

        }

        return new Chromosome(stations, gridX);
    }

    public static List<Station> initializeStations() {
        List<Station> stations = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            stations.add(new Station("a", i));
        }
        for (int j = 20; j < 30; j++) {
            stations.add(new Station("b", j));
        }
        for (int k = 30; k < 45; k++) {
            stations.add(new Station("c", k));
        }
        for (int l = 20; l < 60; l++) {
            stations.add(new Station("d", l));
        }

        return stations;

    }

    public static Chromosome findFavoriteChromosome(BlockingQueue<Chromosome> chromosomes) {
        int max = 0;
        Chromosome favoritChromosome = null;
        for (Chromosome c : chromosomes) {
            int x = c.getFitness();
            if (x > max) {
                max = x;
                favoritChromosome = c;
            }
        }
        return favoritChromosome;
    }

}