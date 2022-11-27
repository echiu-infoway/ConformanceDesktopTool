package ca.echiu.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    public String getWeatherForecast(){
        return "Winter is coming...";
    }
}
