package com.main.station.service;

import com.main.station.dto.OptimizedRoute;
import com.main.station.dto.StationDTO;
import com.main.station.dto.StationTimeDTO;
import com.main.station.entity.StationEntity;
import com.main.station.entity.StationTimeEntity;
import com.main.station.repository.StationRepository;
import com.main.station.repository.StationTimeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;
    private final StationTimeRepository stationTimeRepository;

    @AllArgsConstructor
    static class Node {
        int v; // 간선
        int cost; // 가중치
    }


    public List<StationTimeDTO> findAllTime(List<StationTimeEntity> stationTimeEntityList, int station_id) {
        List<StationTimeDTO> stationTimeDTOList = new ArrayList<>();
        for(StationTimeEntity stationTimeEntity : stationTimeEntityList) {
            if (stationTimeEntity.getStation_id() == station_id) {
                stationTimeDTOList.add(StationTimeDTO.toStationDTO(stationTimeEntity));
            }
        }
        return stationTimeDTOList;
    }

    //위의 findAll에 대해 특정 시간을 필터링 ( 시간 비교해서 제일 빠른 출발시간)
    public void getShortestTime(LinkedList<Integer> shortestPath, String now_time){
        shortestTime = new ArrayList<>();
        String compareTime = now_time;
        List<StationTimeEntity> stationTimeEntityList = stationTimeRepository.findAll();
        for(int station:shortestPath){
            List<StationTimeDTO> stationTimeList =findAllTime(stationTimeEntityList,station);
            for(StationTimeDTO stationTimeDTO : stationTimeList){
                if (stationTimeDTO.getStart_time().compareTo(compareTime) > 0){
                    shortestTime.add(stationTimeDTO.getStart_time());
                    compareTime = stationTimeDTO.getStart_time();
                    break;
                }
            }
        }
    }


    public List<StationDTO> findAll(String type) {
        List<StationEntity> stationEntityList = stationRepository.findAll();
        List<StationDTO> stationDTOList = new ArrayList<>();
        if (type.equals("time")) {
            for (StationEntity stationEntity : stationEntityList) {
                stationDTOList.add(StationDTO.toStationDTOTime(stationEntity));
            }
        } else if (type.equals("expense")) {
            for (StationEntity stationEntity : stationEntityList) {
                stationDTOList.add(StationDTO.toStationDTOExpense(stationEntity));
            }
        } else {
            for (StationEntity stationEntity : stationEntityList) {
                stationDTOList.add(StationDTO.toStationDTODistance(stationEntity));
            }
        }
        return stationDTOList;
    }

    static HashMap<Integer, ArrayList<Node>> graph;
    static HashMap<Integer, Integer> dist;
    static LinkedList<Integer> shortestPath;
    static HashSet<String> visitedEdges;
    static List<String> shortestTime;



    public OptimizedRoute search(int start, int end, String type, String time) throws IOException {
        //int e = stationRepository.countAllEdge();
        System.out.println("time = " + time);

        switch (type) {
            case "최단거리":
                type = "distance";
                break;

            case "최소시간":
                type = "time";
                break;

            case "최소비용":
                type = "expense";
                break;
        }
        System.out.println("type = " + type);
        graph = new HashMap<>();
        dist = new HashMap<>();
        visitedEdges = new HashSet<>();

        List<StationDTO> stationDTOList = findAll(type);

        for (StationDTO stationDTO: stationDTOList) {
            int inputU = stationDTO.getStart();
            int inputV = stationDTO.getEnd();
            int inputW;
            if (type.equals("time")) {
                inputW = stationDTO.getTime();
            } else if (type.equals("distance")) {
                inputW = stationDTO.getDistance();
            } else {
                inputW = stationDTO.getExpense();
            }
            String edgeKey = inputU + "-" + inputV;
            if (!visitedEdges.contains(edgeKey)) {
                visitedEdges.add(edgeKey);
                // 양방향 간선 정보를 저장
                graph.computeIfAbsent(inputU, k -> new ArrayList<>()).add(new Node(inputV, inputW));
                graph.computeIfAbsent(inputV, k -> new ArrayList<>()).add(new Node(inputU, inputW));
            }

        }


        dijkstra(start, end);
        getShortestTime(shortestPath, time);

        return OptimizedRoute.setResult(start,end,dist.get(end),shortestPath,shortestTime);
    }

    private void dijkstra(int start, int end){
        PriorityQueue<Node> q = new PriorityQueue<>((o1, o2) -> o1.cost - o2.cost);
        // 시작 노드에 대해서 초기화
        q.add(new Node(start, 0));
        dist.put(start, 0);

        // 최단 경로 역추적을 위한 맵
        HashMap<Integer, Integer> prevNode = new HashMap<>();

        while (!q.isEmpty()) {
            Node now = q.poll();

            if (now.v == end) {
                // 목표 노드에 도달하면 종료
                break;
            }


            for (Node next : graph.getOrDefault(now.v, new ArrayList<>())) {
                int newCost = dist.get(now.v) + next.cost;
                if (!dist.containsKey(next.v) || newCost < dist.get(next.v)) {
                    dist.put(next.v, newCost);
                    prevNode.put(next.v, now.v); // 이전 노드 정보 저장
                    q.add(new Node(next.v, newCost));
                }
            }
        }

        // 최단 경로 추적
        shortestPath = new LinkedList<>();
        int current = end;
        while (current != start) {
            shortestPath.addFirst(current);
            current = prevNode.get(current);
        }
        shortestPath.addFirst(start);
    }
}

