package model;

public class Classroom {
    private int classroomId;
    private String building;
    private String roomName;
    private int capacity;

    public Classroom(int classroomId, String building, String roomName, int capacity) {
        this.classroomId = classroomId;
        this.building = building;
        this.roomName = roomName;
        this.capacity = capacity;
    }

    public int getClassroomId() { return classroomId; }
    public String getBuilding() { return building; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }

    // Ini yang tampil di dropdown ruangan
    @Override
    public String toString() {
        return building + " - " + roomName + " (kapasitas " + capacity + ")";
    }
}