import java.util.Random;

public class Map {
    MapObject[][] map;
    int size;
    Random random = new Random();

    public Map(int size, int countHerb, int countCarn) {
        this.size = size;
        map = new MapObject[size][size]; // Must be quadratic or other functions break

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                map[y][x] = new StaticObject(MapObjectType.EMPTY);
            }
        }

        placeObject((int) (size * size / 10), new StaticObject(MapObjectType.FENCE));
        placeDino(countHerb, MapObjectType.HERBIVORE);
        placeDino(countCarn, MapObjectType.CARNIVORE);

    }

    public void placeDino(int amount, MapObjectType toPlace) {
        for (int i = 0; i < amount; i++) {
            int randomY = random.nextInt(size);
            int randomX = random.nextInt(size);
            var item = getCoordinates(randomX, randomY);

            while (item.getMapObjectType() != MapObjectType.EMPTY) {
                randomY = random.nextInt(size);
                randomX = random.nextInt(size);
                item = getCoordinates(randomX, randomY);
            }
            switch (toPlace) {
                case HERBIVORE:
                    map[randomY][randomX] = new Herbivore(i, getRandomDinoName(toPlace));
                    break;
                case CARNIVORE:
                    map[randomY][randomX] = new Carnivore(i, getRandomDinoName(toPlace));
                    break;
            }
        }
    }

    public String getRandomDinoName(MapObjectType type) {
        String[] dinoNamesHerb = {"Triceratops", "Parasaurolophus", "Stegosaurus", "Brachiosaurus", "Diplodocus", "Ankylosaurus"};
        String[] dinoNamesCarn = {"Tyrannosaurus Rex", "Velociraptor", "Coelophysis", "Liopleurodon", "Allosaurus", "Spinosaurus"};

        int rand;
        switch (type) {
            case HERBIVORE:
                rand = random.nextInt(dinoNamesHerb.length);
                return dinoNamesHerb[rand];
            case CARNIVORE:
                rand = random.nextInt(dinoNamesCarn.length);
                return dinoNamesCarn[rand];
            default:
                throw new IllegalArgumentException("Type must be one of dinos");
        }
    }

    public void placeObject(int amount, MapObject toPlace) {
        for (int i = 0; i < amount; i++) {
            int randomY = random.nextInt(size);
            int randomX = random.nextInt(size);
            MapObject item = getCoordinates(randomX, randomY);

            while (item.getMapObjectType() != MapObjectType.EMPTY) {
                randomY = random.nextInt(size);
                randomX = random.nextInt(size);
                item = getCoordinates(randomX, randomY);
            }
            map[randomY][randomX] = toPlace;
        }
    }

    @Override
    public String toString() {
        String output = new String();
        output += " " + "-".repeat(size * 6) + "\n";
        for (int y = 0; y < size; y++) {

            output += "|";
            for (int x = 0; x < size; x++) {
                var object = getCoordinates(x, y);
                output += object.print();
            }
            output += "|\n";
        }
        output += " " + "-".repeat(size * 6);
        return output;
    }

    public void debug() {
        System.out.println(toString());
    }

    public int countMapObjectByType(MapObjectType type) {
        int count = 0;
        for (MapObject[] row : map) {
            for (MapObject mapObject : row) {
                if (mapObject.getMapObjectType().equals(type)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void eatAt(int x, int y, Carnivore carnivore) {
        for (int i = y - 1; i < y + 1; i++) {
            for (int j = x - 1; j < x + 1; j++) {
                if (i >= 0 && i < map.length && j >= 0 && j < map.length) {
                    if (map[i][j].getMapObjectType().equals(MapObjectType.HERBIVORE)) {
                        if (carnivore.attemptEat()) {
                            map[i][j] = new StaticObject(MapObjectType.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public void innerGameLoop() {

        // Eating stage
        for (int i = 0; i < map.length; i++) {
            var mapObjects = map[i];
            for (int j = 0; j < map.length; j++) {
                var mapObject = mapObjects[j];
                if (mapObject.getMapObjectType().equals(MapObjectType.CARNIVORE)) {
                    eatAt(j, i, (Carnivore) mapObject);
                }
            }
        }

        // Moving stage
        for (int i = 0; i < map.length; i++) {
            var mapObjects = map[i];
            for (int j = 0; j < map.length; j++) {
                var mapObject = mapObjects[j];

                if (!(mapObject instanceof Dino)) {
                    continue;
                }

                if (!mapObject.canMove()) {
                    continue;
                }

                int offsetX = random.nextInt(3) - 1;
                int offsetY = random.nextInt(3) - 1;

                int x = j + offsetX;
                int y = i + offsetY;

                while (
                        y < 0 ||
                                y >= map.length ||
                                x < 0 ||
                                x >= map.length ||
                                !map[y][x].getMapObjectType().equals(MapObjectType.EMPTY)
                ) {
                    offsetX = random.nextInt(3) - 1;
                    offsetY = random.nextInt(3) - 1;
                    x = j + offsetX;
                    y = i + offsetY;
                }
                mapObject.move();
                map[i][j] = new StaticObject(MapObjectType.EMPTY);
                map[y][x] = mapObject;
            }
        }

        for (int i = 0; i < map.length; i++) {
            var mapObjects = map[i];
            for (int j = 0; j < map.length; j++) {
                var mapObject = mapObjects[j];

                mapObject.resetMove();
            }
        }
    }

    public MapObject getCoordinates(int x, int y) {
        return map[y][x];
    }

    public MapObjectType checkCoordinates(int x, int y) {
        return getCoordinates(x, y).getMapObjectType();
    }
}