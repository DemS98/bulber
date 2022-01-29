package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {
    public static class Service {
        @JsonProperty("transition_light_state")
        private LightState transitionState;

        public Service(LightState transitionState) {
            this.transitionState = transitionState;
        }

        @Override
        public String toString() {
            return "Service{" +
                    "transitionState=" + transitionState +
                    '}';
        }
    }

    @JsonProperty("smartlife.iot.smartbulb.lightingservice")
    private Service service;

    public Request(Service service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "Request{" +
                "service=" + service +
                '}';
    }
}
