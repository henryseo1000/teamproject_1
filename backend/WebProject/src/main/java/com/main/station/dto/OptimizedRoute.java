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
    private int time;
    private int distance;
    private int expense;
    private int transferCount;

    private LinkedList<Integer> shortestPath;
    private List<String> shortestTime;
    private List<Integer> totalLineList;

    public static OptimizedRoute setResult(int start, int end, int time, int distance,int expense, int transferCount, LinkedList<Integer> shortestPath, List<String> shortestTime, List<Integer> totalLineList){
        OptimizedRoute optimizedRoute = new OptimizedRoute();
        optimizedRoute.setStart(start);
        optimizedRoute.setEnd(end);
        optimizedRoute.setTime(time);
        optimizedRoute.setDistance(distance);
        optimizedRoute.setExpense(expense);
        optimizedRoute.setShortestPath(shortestPath);
        optimizedRoute.setShortestTime(shortestTime);
        optimizedRoute.setTotalLineList(totalLineList);
        optimizedRoute.setTransferCount(transferCount);
        return optimizedRoute;
    }


}
