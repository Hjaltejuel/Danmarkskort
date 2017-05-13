package bfst17.Enums;

/**
 * Created by Michelle on 5/12/2017.
 */
public enum RoadTypes {
    HIGHWAY_MOTORWAY(new WeighType[]{WeighType.FASTESTCAR}),

    HIGHWAY_TRUNK(new WeighType[]{WeighType.FASTESTCAR}),

    HIGHWAY_PRIMARY(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE, WeighType.SHORTESTFOOT}),

    HIGHWAY_SECONDARY(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_TERTIARY(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_UNCLASSIFIED(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_RESIDENTIAL(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_SERVICE(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_LIVING_STREET(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_PEDESTRIAN(new WeighType[]{WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_BRIDLEWAY(new WeighType[]{WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_CYCLEWAY(new WeighType[]{WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_FOOTWAY(new WeighType[]{WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_PATH(new WeighType[]{WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_STEPS(new WeighType[]{WeighType.SHORTESTFOOT}),

    HIGHWAY_TRACK(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT}),

    HIGHWAY_BUS_GUIDEWAY(new WeighType[0]),

    HIGHWAY_RACEWAY(new WeighType[]{WeighType.FASTESTCAR}),

    HIGHWAY_ROAD(new WeighType[]{WeighType.FASTESTCAR,WeighType.SHORTESTBICYCLE,WeighType.SHORTESTFOOT});



    private final WeighType[] WeighTypes;

    RoadTypes(WeighType[] WeighTypes){
        this.WeighTypes = WeighTypes;
    }
    public WeighType[] getWeighTypes(){return WeighType.values();}
}
