package ca.echiu.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    public String getWeatherForecast(){
        return "It's gonna be FALLing for you";
    }
}
