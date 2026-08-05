package structures;
import java.util.*;

public class MyGraph {
    // Map untuk menyimpan relasi: KodeMK -> List Syarat KodeMK
    private Map<String, List<String>> adjList = new HashMap<>();

    public void addPrerequisite(String courseId, String prereqCourseId) {
        adjList.putIfAbsent(courseId, new ArrayList<>());
        adjList.get(courseId).add(prereqCourseId);
    }

    public List<String> getPrerequisites(String courseId) {
        return adjList.getOrDefault(courseId, new ArrayList<>());
    }
}