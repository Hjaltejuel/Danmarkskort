package bfst17.Enums;


public enum RoadTypes {
    HIGHWAY_MOTORWAY(new VehicleType[]{VehicleType.CAR}, 130),

    HIGHWAY_MOTORWAY_LINK(new VehicleType[]{VehicleType.CAR},130),

    HIGHWAY_TRUNK_LINK(new VehicleType[]{VehicleType.CAR},80),

    HIGHWAY_PRIMARY_LINK(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE},80),

    HIGHWAY_SECONDARY_LINK(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT},50),

    HIGHWAY_TERTIARY_LINK(new VehicleType[]{VehicleType.CAR,VehicleType.FOOT,VehicleType.BICYCLE},80),

    HIGHWAY_TRUNK(new VehicleType[]{VehicleType.CAR}, 80),

    HIGHWAY_PRIMARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE}, 80),

    HIGHWAY_CROSSING(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT},130),

    HIGHWAY_SECONDARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50),

    HIGHWAY_TERTIARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50),

    HIGHWAY_UNCLASSIFIED(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50),

    HIGHWAY_RESIDENTIAL(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 30),

    HIGHWAY_SERVICE(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50),

    HIGHWAY_LIVING_STREET(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 30),

    HIGHWAY_PEDESTRIAN(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}, 0),

    HIGHWAY_BRIDLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}, 0),

    HIGHWAY_CYCLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}, 0),

    HIGHWAY_FOOTWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}, 0),

    HIGHWAY_PATH(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}, 0),

    HIGHWAY_STEPS(new VehicleType[]{VehicleType.FOOT}, 0),

    HIGHWAY_TRACK(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50),

    HIGHWAY_BUS_GUIDEWAY(new VehicleType[0], 0),

    HIGHWAY_RACEWAY(new VehicleType[]{VehicleType.CAR}, 130),

    HIGHWAY_ROAD(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}, 50);

    private final VehicleType[] vehicletypes;
    private Integer maxSpeed;

    RoadTypes(VehicleType[] vehicletypes, Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.vehicletypes = vehicletypes;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public VehicleType[] getVehicletypes(){ return vehicletypes; }
}