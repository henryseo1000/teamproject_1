package com.main.station.dto;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedRoute {
    private int start;
    private int end;
    private int result;
    private LinkedList<Integer> shortestPath;
    private List<String> shortestTime;

    public static OptimizedRoute setResult(int start, int end, int result, LinkedList<Integer> shortestPath, List<String> shortestTime){
        OptimizedRoute optimizedRoute = new OptimizedRoute();
        optimizedRoute.setStart(start);
        optimizedRoute.setEnd(end);
        optimizedRoute.setResult(result);
        optimizedRoute.setShortestPath(shortestPath);
        optimizedRoute.setShortestTime(shortestTime);
        return optimizedRoute;
    }


}