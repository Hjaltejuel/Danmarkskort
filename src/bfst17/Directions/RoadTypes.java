package bfst17.Directions;

import bfst17.Enums.FillType;

import java.awt.*;

/**
 * Created by Michelle on 5/12/2017.
 */
public enum RoadTypes {
    HIGHWAY_MOTORWAY(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_TRUNK(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_PRIMARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE}),

    HIGHWAY_SECONDARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_TERTIARY(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_UNCLASSIFIED(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_RESIDENTIAL(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_SERVICE(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_LIVING_STREET(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_PEDESTRIANnew(new VehicleType[]{VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_BRIDLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_CYCLEWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_FOOTWAY(new VehicleType[]{VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_PATH(new VehicleType[]{VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_STEPS(new VehicleType[]{VehicleType.ONFOOT}),

    HIGHWAY_TRACK(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT}),

    HIGHWAY_BUS_GUIDEWAY(new VehicleType[0]),

    HIGHWAY_RACEWAY(new VehicleType[]{VehicleType.CAR}),

    HIGHWAY_ROAD(new VehicleType[]{VehicleType.CAR,VehicleType.BICYCLE,VehicleType.ONFOOT});



    private final VehicleType[] vehicletypes;

    RoadTypes(VehicleType[] vehicletypes){
        this.vehicletypes = vehicletypes;
    }
    public VehicleType[] getVehicletypes(){return VehicleType.values();}
}
