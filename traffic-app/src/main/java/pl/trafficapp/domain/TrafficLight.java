package pl.trafficapp.domain;

public class TrafficLight {
    private LightColor color;
    private final boolean isDirectional;

    public TrafficLight(LightColor color, boolean isDirectional) {
        this.color = color;
        this.isDirectional = isDirectional;
    }

    public LightColor getColor() {
        return color;
    }
    public void setColor(LightColor color) {
        if (color == LightColor.GREEN && isDirectional) {
            this.color = LightColor.GREEN_ARROW_LEFT;
        } else {
            this.color = color;
        }
    }

    public boolean isDirectional() {
        return isDirectional;
    }
}
