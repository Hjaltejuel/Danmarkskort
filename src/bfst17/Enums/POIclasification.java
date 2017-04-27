package bfst17.Enums;

/**
 * Created by Michelle on 4/18/2017.
 */
public enum POIclasification {
    FOOD_AND_DRINKS, ATTRACTION ,NATURE,HEALTH_CARE,UTILITIES,EMERGENCY,SHOPS;

    public boolean isDrawable() {
        return drawable;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }

    public boolean drawable;
    POIclasification() {
        drawable =false;
    }
}
