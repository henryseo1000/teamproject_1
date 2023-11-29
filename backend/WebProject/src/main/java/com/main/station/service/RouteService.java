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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final StationRepository stationRepository;
    private final StationTimeRepository stationTimeRepository;
    private final TransferCalculate transferCalculate;

    @AllArgsConstructor
    static class Node {
        int v; // 간선
        int cost; // 가중치
    }


    public List<StationTimeDTO> findAllTime(List<StationTimeEntity> stationTimeEntityList) {
        List<StationTimeDTO> stationTimeDTOList = new ArrayList<>();
        for(StationTimeEntity stationTimeEntity : stationTimeEntityList) {
            stationTimeDTOList.add(StationTimeDTO.toStationDTO(stationTimeEntity));
        }
        return stationTimeDTOList;
    }


    public List<StationDTO> findAllType() {
        List<StationEntity> stationEntityList = stationRepository.findAll();
        List<StationDTO> stationDTOList = new ArrayList<>();
        for (StationEntity stationEntity : stationEntityList) {
            stationDTOList.add(StationDTO.toStationDTOAll(stationEntity));
        }
        return stationDTOList;
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
    static List<Integer> totalLineList;
    static List<Integer> stationGap;


    //위의 findAll에 대해 특정 시간을 필터링 ( 시간 비교해서 제일 빠른 출발시간)
    public void getShortestTime(List<Integer> totalLineList, String now_time){
        shortestTime = new ArrayList<>();
        List<StationTimeEntity> stationTimeEntityList = stationTimeRepository.findAll();

        //기준 시간
        String compareTime = now_time;
        List<StationTimeDTO> stationTimeList =findAllTime(stationTimeEntityList);

        //환승하면, 갱신될 호선
        int std = 0;

        // 각 역에 대해서 반복
        for(int i=0;i<totalLineList.size();i=i+2){
            int station = totalLineList.get(i);
            int line = totalLineList.get(i+1);
            int gap = stationGap.get(i+1);

            //만약 다른 호선으로 이동할 경우, 환승 시간을 고려해서 출발시간을 조정함
            if (std != line){
                //도착 시간을 갱신함
                compareTime =  stationBoardingTime(stationTimeList, station, line, gap, compareTime);
                std = line;
                System.out.println("갱신한 호선 = " + std);
                // 동일 호선으로 이동할 경우
            } else {
                System.out.println(" 동일 호선 이동 ");
                //출발시간 저장
                shortestTime.add(compareTime);
                compareTime = addSecondsToTime(compareTime,gap);
                //도착시간 저장
                shortestTime.add(compareTime);
            }
        }
    }

    //다른 호선으로 이동할 경우 사용하는 메서드 
    public String stationBoardingTime(List<StationTimeDTO> stationTimeList, int station, int line, int gap, String time) {
        for(StationTimeDTO stationTimeDTO : stationTimeList){
            String start_time = stationTimeDTO.getStart_time();
            if ( (stationTimeDTO.getDirection() == line) && (stationTimeDTO.getStation_id() == station) && (start_time.compareTo(time) > 0) ){
                //출발시간
                shortestTime.add(start_time);
                time = addSecondsToTime(start_time, gap);
                //도착시간
                shortestTime.add(time);
                break;
            }
        }
        //해당 열차의 도착 시간을 반환
        return time;
    }



    public OptimizedRoute search(int start, int end, String type, String time) throws IOException, ParseException {
        totalLineList = new ArrayList<>();
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
        int transferCount=0;
        int timeGap = 0;
        dijkstra(start, end);
        List<Integer> totalList = getTotal();
        getTotalGapList(shortestPath);
        totalLineList = transferCalculate.getTransfer(shortestPath,stationDTOList);
        transferCount = transferCalculate.getTransferCount(totalLineList);
        getShortestTime(totalLineList, time);
        timeGap = calculateTimeGap(shortestTime.get(1), shortestTime.get(shortestTime.size()-1));
        return OptimizedRoute.setResult(start,end,timeGap, totalList.get(0), totalList.get(1),transferCount,shortestPath,shortestTime,totalLineList);
    }


    //역과 역 사이의 걸리는 시간 ---> 형식 : [역1, 소요시간, 역2, 소요시간, ...]
    private void getTotalGapList(LinkedList<Integer> shortestPath) {
        stationGap = new ArrayList<>();
        int i;
        for (i = 0; i < shortestPath.size() - 1; i++) {
            int start = shortestPath.get(i);
            int end = shortestPath.get(i + 1);
            for (StationDTO stationDTO : findAll("time")) {
                int compA = stationDTO.getStart(); int compB =  stationDTO.getEnd();
                if ( (compA==start && compB== end) || (compA==end && compB==start)) {
                    stationGap.add(start);
                    stationGap.add(stationDTO.getTime());
                }
            }
        }
        stationGap.add(shortestPath.get(i));
        stationGap.add(0);

    }

    // 시간, 거리, 비용 데이터를 한번에 저장함.
    private List<Integer> getTotal() {
        List<Integer> totalList = new ArrayList<>();
        int distance=0, expense=0;
        for (int i = 0; i < shortestPath.size() - 1; i++) {

            int start = shortestPath.get(i);
            int end = shortestPath.get(i + 1);
            for (StationDTO stationDTO : findAllType()) {
                int compA = stationDTO.getStart(); int compB =  stationDTO.getEnd();
                if ( (compA==start && compB== end) || (compA==end && compB==start)) {
                    distance += stationDTO.getDistance();
                    expense += stationDTO.getExpense();
                }
            }
        }
        totalList.add(distance);
        totalList.add(expense);
        return totalList;
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

    public String addSecondsToTime(String time, int secondsToAdd) {
        try {
            // 문자열을 Date 객체로 파싱
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = sdf.parse(time);

            // Calendar 객체를 사용하여 시간과 분을 설정
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 초를 더함
            calendar.add(Calendar.SECOND, secondsToAdd);

            // 변경된 시간을 다시 문자열로 변환
            Date updatedDate = calendar.getTime();
            return sdf.format(updatedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int calculateTimeGap(String time1, String time2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date date1 = sdf.parse(time1);
        Date date2 = sdf.parse(time2);

        long differenceInMillis = date2.getTime() - date1.getTime();
        return (int) (differenceInMillis / 1000);
    }


}


