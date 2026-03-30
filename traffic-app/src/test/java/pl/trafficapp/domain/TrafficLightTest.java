package pl.trafficapp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightTest {
    @Test
    void setColor_DirectionalLight_ConvertsGreenToArrow() {
        TrafficLight light = new TrafficLight(true);
        light.setColor(LightColor.GREEN);
        assertEquals(LightColor.GREEN_ARROW_LEFT, light.getColor());
    }
}