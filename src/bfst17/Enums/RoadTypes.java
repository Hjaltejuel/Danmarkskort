package bfst17.Enums;

/**
 * Created by Michelle on 5/12/2017.
 */
public enum RoadTypes {
    HIGHWAY_MOTORWAY(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_TRUNK(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_PRIMARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE}),

    HIGHWAY_SECONDARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_TERTIARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_UNCLASSIFIED(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_RESIDENTIAL(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_SERVICE(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_LIVING_STREET(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_PEDESTRIAN(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_BRIDLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_CYCLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_FOOTWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_PATH(new VehicleType[]{VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_STEPS(new VehicleType[]{VehicleType.FOOT}),

    HIGHWAY_TRACK(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT}),

    HIGHWAY_BUS_GUIDEWAY(new VehicleType[0]),

    HIGHWAY_RACEWAY(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_ROAD(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.FOOT});

    private final VehicleType[] vehicletypes;

    RoadTypes(VehicleType[] vehicletypes){
        this.vehicletypes = vehicletypes;
    }
    public VehicleType[] getVehicletypes(){ return vehicletypes; }
}