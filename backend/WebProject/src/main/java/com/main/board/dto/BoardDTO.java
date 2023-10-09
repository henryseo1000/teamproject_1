package com.main.board.dto;


import com.main.board.entity.BoardEntity;
import com.main.board.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private List<MultipartFile> boardFile;
    private List<String> originalFileName;    //원본 파일명
    private List<String> storedFileName;      //서버 저장용 파일명
    private int fileAttached;       //파일 첨부 여부(첨부 1 미첨부 0)

    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
        if (boardEntity.getFileAttached() == 0){
            boardDTO.setFileAttached(boardEntity.getFileAttached());
        } else {
            List<String> orginalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            boardDTO.setFileAttached(boardEntity.getFileAttached());
            // 파일 이름 가져오기
            //BoardFileEntity에 저장된 파일의 이름이 있는데, 여기서는 매개변수로 가져오지않음
            //그러나, 연관관계로 가져올 수 있음
            for (BoardFileEntity boardFileEntity:boardEntity.getBoardFileEntityList()) {
                orginalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(orginalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);
        }

        return boardDTO;
    }

}
