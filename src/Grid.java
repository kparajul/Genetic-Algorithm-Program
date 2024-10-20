import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Grid {

    private boolean[][] grid;

    public Grid(int x, int y) {
        grid = new boolean[x][y];
    }

    public void placeStation(Station station) {
        List<int[]> holes = getHoles(station);

        switch (station.getType()) {
            case "a":
                int r = ThreadLocalRandom.current().nextInt(0, holes.size());
                int[] randomPosition = holes.get(r);
                grid[randomPosition[0]][randomPosition[1]] = true;
                station.setPosition(Collections.singletonList(randomPosition));
                holes.remove(r);
                break;
            case "b":
                boolean done = false;
                while (!done) {
                    int random = ThreadLocalRandom.current().nextInt(0, holes.size());
                    int i = holes.get(random)[0];
                    int j = holes.get(random)[1];
                    if (check(i, j) && check(i - 1, j - 1) && check(i, j - 1) && check(i - 1, j)) {
                        List<int[]> position = Arrays.asList(new int[] { i, j }, new int[] { i - 1, j - 1 },
                                new int[] { i, j - 1 }, new int[] { i - 1, j });
                        station.setPosition(position);
                        grid[i][j] = true;
                        grid[i - 1][j - 1] = true;
                        grid[i][j - 1] = true;
                        grid[i - 1][j] = true;
                        done = true;
                    }
                }
                break;
            case "c":
                boolean done1 = false;
                while (!done1) {
                    int random = ThreadLocalRandom.current().nextInt(0, holes.size());
                    int i = holes.get(random)[0];
                    int j = holes.get(random)[1];
                    if (check(i, j) && check(i, j + 1)) {
                        List<int[]> position = Arrays.asList(new int[] { i, j }, new int[] { i, j + 1 });
                        station.setPosition(position);
                        grid[i][j] = true;
                        grid[i][j + 1] = true;
                        done1 = true;
                    }
                }
                break;
            case "d":
                boolean done2 = false;
                while (!done2) {
                    int random = ThreadLocalRandom.current().nextInt(0, holes.size());
                    int i = holes.get(random)[0];
                    int j = holes.get(random)[1];
                    if (check(i, j) && check(i + 1, j)) {
                        List<int[]> position = Arrays.asList(new int[] { i, j }, new int[] { i + 1, j });
                        station.setPosition(position);
                        grid[i][j] = true;
                        grid[i + 1][j] = true;
                        done2 = true;
                    }
                }
                break;
            default:
                break;
        }

    }

    public List<int[]> getHoles(Station station) {
        List<int[]> holes = new ArrayList<>();
        String type = station.type;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                switch (type) {
                    case "a":
                        if (check(i, j)) {
                            holes.add(new int[] { i, j });
                        }
                        break;
                    case "b":
                        if (check(i, j) && check(i - 1, j - 1) && check(i, j - 1) && check(i - 1, j)) {
                            int[][] positions = {
                                    { i, j },
                                    { i - 1, j - 1 },
                                    { i, j - 1 },
                                    { i - 1, j }
                            };

                            for (int[] position : positions) {
                                if (!contains(holes, position)) {
                                    holes.add(position);
                                }
                            }
                        }
                        break;
                    case "c":
                        if (check(i, j) && check(i, j + 1)) {
                            int[][] positions = {
                                    { i, j },
                                    { i, j + 1 }
                            };

                            for (int[] position : positions) {
                                if (!contains(holes, position)) {
                                    holes.add(position);
                                }
                            }

                        }
                        break;
                    case "d":
                        if (check(i, j) && check(i + 1, j)) {
                            int[][] positions = {
                                    { i, j },
                                    { i + 1, j }
                            };

                            for (int[] position : positions) {
                                if (!contains(holes, position)) {
                                    holes.add(position);
                                }
                            }

                        }
                        break;

                }
            }
        }
        return holes;
    }

    private boolean contains(List<int[]> list, int[] arr) {
        for (int[] l : list) {
            if (Arrays.equals(l, arr)) {
                return true;
            }
        }
        return false;
    }

    public boolean check(int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && !grid[x][y];
    }

    public void occupy(int x, int y) {
        grid[x][y] = true;
    }

    public void remove(int x, int y) {
        grid[x][y] = false;
    }
}
