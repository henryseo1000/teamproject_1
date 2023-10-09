package com.main.station.service;

import com.main.station.dto.StationDTO;
import com.main.station.entity.StationEntity;
import com.main.station.repository.StationRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;

    @AllArgsConstructor
    static class Node {
        int v; // 간선
        int cost; // 가중치
    }

    public List<StationDTO> findAll() {
        List<StationEntity> stationEntityList = stationRepository.findAll();
        List<StationDTO> stationDTOList = new ArrayList<>();
        for (StationEntity stationEntity: stationEntityList){
            stationDTOList.add(StationDTO.toStationDTOTime(stationEntity));
        }
        return stationDTOList;
    }

    static HashMap<Integer, ArrayList<Node>> graph;
    static HashMap<Integer, Integer> dist;
    static LinkedList<Integer> shortestPath;
    static HashSet<String> visitedEdges;

    public StationDTO search(int start, int end) throws IOException {
        //int e = stationRepository.countAllEdge();
        int link = 139;
        int node = 110;

        graph = new HashMap<>();
        dist = new HashMap<>();
        visitedEdges = new HashSet<>();

        List<StationDTO> stationDTOList = findAll();

        for (StationDTO stationDTO: stationDTOList) {
            int inputU = stationDTO.getStart();
            int inputV = stationDTO.getEnd();
            int inputW = stationDTO.getTime();

            String edgeKey = inputU + "-" + inputV;
            if (!visitedEdges.contains(edgeKey)) {
                visitedEdges.add(edgeKey);
                // 양방향 간선 정보를 저장
                graph.computeIfAbsent(inputU, k -> new ArrayList<>()).add(new Node(inputV, inputW));
                graph.computeIfAbsent(inputV, k -> new ArrayList<>()).add(new Node(inputU, inputW));
            }

        }


        dijkstra(start, end);

        return StationDTO.setResult(start,end,dist.get(end),shortestPath);
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

