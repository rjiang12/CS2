package edu.caltech.cs2.datastructures;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.interfaces.ISet;

import java.io.FileReader;
import java.io.IOException;


public class BeaverMapsGraph extends Graph<Long, Double> {
    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();
        JsonElement buildings = fromFile(buildingsFileName);
        JsonElement waypoints = fromFile(waypointsFileName);
        JsonElement roads = fromFile(roadsFileName);
        for(JsonElement building : buildings.getAsJsonArray()) {
            Location location = new Location(building.getAsJsonObject());
            this.buildings.add(location);
            if(!this.ids.containsKey(location.id)) {
                this.ids.put(location.id, location);
            }
            if(!this.vertices().contains(location.id)) {
                this.addVertex(location.id);
            }

        }
        for(JsonElement waypoint : waypoints.getAsJsonArray()) {
            Location location = new Location(waypoint.getAsJsonObject());
            if(!this.ids.containsKey(location.id)) {
                this.ids.put(location.id, location);
            }
            if(!this.vertices().contains(location.id)) {
                this.addVertex(location.id);
            }
        }
        for(JsonElement road : roads.getAsJsonArray()) {
            JsonArray edgeArray = road.getAsJsonArray();
            long location1 = 0;
            for(JsonElement edge : edgeArray) {
                long location2 = edge.getAsLong();
                if(location1 != 0) {
                    Location loc1 = this.ids.get(location1);
                    Location loc2 = this.ids.get(location2);
                    double distance = loc1.getDistance(loc2);
                    this.addEdge(location1, location2, distance);
                    this.addEdge(location2, location1, distance);
                }
                location1 = location2;
            }
        }
    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        ArrayDeque<Location> locations = new ArrayDeque<>();
        for(Location location : this.ids.values()) {
            if(location.name != null && location.name.equals(locName)) {
                locations.add(location);
            }
        }
        return locations;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        return this.ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        if(this.ids.values().contains(n)){
            return false;
        }
        this.ids.put(n.id, n);
        this.addVertex(n.id);
        return true;
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        Location closestBuilding = null;
        double minDist = Double.MAX_VALUE;
        for(Location building : this.buildings) {
            double distance = building.getDistance(lat, lon);
            if(distance < minDist) {
                minDist = distance;
                closestBuilding = building;
            }
        }
        return closestBuilding;
    }

    /**
     * Returns a set of locations which are reachable along a path that goes no further than `threshold` feet from start
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return
     */
    public ISet<Location> dfs(Location start, double threshold) {
        ISet<Location> reachable = new ChainingHashSet<>();
        ArrayDeque<Long> stack = new ArrayDeque<>();
        stack.push(start.id);
        while(!stack.isEmpty()) {
            long current = stack.pop();
            Location location = this.ids.get(current);
            if(start.getDistance(location) <= threshold) {
                reachable.add(location);
                for(Long neighbor : neighbors(current)) {
                    Location neighborLoc = this.ids.get(neighbor);
                    if(!reachable.contains(neighborLoc)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return reachable;
    }

    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        LinkedDeque<Location> shortestPath = new LinkedDeque<>();
        shortestPath.add(target);
        if (start.equals(target)) {
            return shortestPath;
        }
        ChainingHashDictionary<Long, Double> distances = new ChainingHashDictionary<>(MoveToFrontDictionary::new); // for distances
        ChainingHashDictionary<Long, Long> prev = new ChainingHashDictionary<>(MoveToFrontDictionary::new); // back pointers
        MinFourHeap<Long> vertexes = new MinFourHeap<>(); // for vertexes in the map
        dijkstraSetup(start, distances, vertexes); // filling prev and vertexes
        dijkstra(vertexes, distances, prev, target); // actual dijkstra algorithm
        Long curr = target.id;
        while(!curr.equals(start.id)) {
            Long past = prev.get(curr); // prev does not contain target.id
            if(past == null) {
                return null;
            }
            shortestPath.addFront(this.ids.get(past));
            curr = past;
        }
        return shortestPath;
    }

    private void dijkstraSetup(Location start, ChainingHashDictionary<Long, Double> distances, MinFourHeap<Long> vertexes) {
        for(Long v : this.vertices()) {
            if(v.equals(start.id)) {
                distances.put(v, 0.0);
            }
            else {
                distances.put(v, Double.POSITIVE_INFINITY);
            }
            IPriorityQueue.PQElement<Long> element = new IPriorityQueue.PQElement<>(v, distances.get(v));
            vertexes.enqueue(element);
        }
    }

    private void dijkstra(MinFourHeap<Long> vertexes, ChainingHashDictionary<Long, Double> distances, ChainingHashDictionary<Long, Long> prev, Location target) {
        while(vertexes.size() > 0) {
            Long currPoint = vertexes.dequeue().data;
            for(Long neighbor : neighbors(currPoint)) {
                Location neighborID = this.ids.get(neighbor);
                if(this.buildings.contains(neighborID) && !neighbor.equals(target.id)) {
                    continue;
                }
                double pDistance = distances.get(currPoint) + adjacent(currPoint, neighbor);
                double oDistance = distances.get(neighbor);
                if(oDistance > pDistance) {
                    distances.put(neighbor, pDistance);
                    prev.put(neighbor, currPoint);
                    IPriorityQueue.PQElement<Long> key = new IPriorityQueue.PQElement<>(neighbor, pDistance);
                    vertexes.decreaseKey(key);
                }
            }
        }
    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
    private static JsonElement fromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            return null;
        }
    }
}

