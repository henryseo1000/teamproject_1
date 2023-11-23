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

    /**
     * 최단 경로의 총 비용
     */
    private int totalPrice;

    public static OptimizedRoute setResult(int start, int end, int result, LinkedList<Integer> shortestPath, List<String> shortestTime, int totalPrice) {
        OptimizedRoute optimizedRoute = new OptimizedRoute();
        optimizedRoute.setStart(start);
        optimizedRoute.setEnd(end);
        optimizedRoute.setResult(result);
        optimizedRoute.setShortestPath(shortestPath);
        optimizedRoute.setShortestTime(shortestTime);
        optimizedRoute.setTotalPrice(totalPrice);
        return optimizedRoute;
    }


}
