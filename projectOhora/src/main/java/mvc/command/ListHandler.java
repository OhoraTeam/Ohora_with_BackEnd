package mvc.command;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.ConnectionProvider;

import ohora.domain.PagingVO;
import ohora.domain.ProductDTO;
import ohora.persistence.OhoraDAO;
import ohora.persistence.OhoraDAOImpl;

public class ListHandler implements CommandHandler {
    
    private int currentPage = 1;                // 현재 페이지 번호
    private int numberPerPage = 12;             // 한 페이지에 출력할 게시글 수
    private int numberOfPageBlock = 10;         // 한 페이지 블록에 표시할 페이지 수
    private int totalRecords = 0;               // 총 레코드 수
    private int categoryNumber = 0;             // 카테고리 번호

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 페이지 번호 받기 (기본값 1로 설정)
        try {
            currentPage = Integer.parseInt(request.getParameter("currentPage"));
        } catch (Exception e) {
            currentPage = 1; // 기본값 1
        }

        // 카테고리 번호 받기 (기본값 0으로 설정)
        try {
            categoryNumber = request.getParameter("catno") != null ? 
                             Integer.parseInt(request.getParameter("catno")) : 0;
        } catch (Exception e) {
            categoryNumber = 0; // 기본값 0
        }

        // 정렬 방식 파라미터 받기
        String sort = request.getParameter("sort");

        // 검색어 파라미터 받기
        String searchWord = request.getParameter("searchWord");

        // DAO 및 데이터베이스 연결
        Connection conn = null;
        OhoraDAO dao = null;
        ArrayList<ProductDTO> list = null;

        try {
            conn = ConnectionProvider.getConnection();
            dao = new OhoraDAOImpl(conn);

            // 총 레코드 수 계산
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                totalRecords = dao.getTotalRecordsByProductName(searchWord);
            } else {
                totalRecords = dao.getTotalRecords(categoryNumber);
            }

            // 목록 가져오기 (검색어 및 정렬 조건에 따라 DAO 메서드 호출)
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                list = dao.selectByProductName(searchWord, currentPage, numberPerPage);
            } else if ("new".equals(sort)) {
                // 등록순 정렬
                list = dao.selectProductsByCreatedDate(currentPage, numberPerPage, categoryNumber);
            } else if ("sales".equals(sort)) {
                // 판매량 순 정렬
                list = dao.selectProductsBySales(currentPage, numberPerPage, categoryNumber);
            } else {
                // 기본 정렬
                list = dao.selectProducts(currentPage, numberPerPage, categoryNumber);
            }

            // 페이징 객체 생성
            PagingVO pvo;
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                pvo = new PagingVO(currentPage, numberPerPage, numberOfPageBlock, searchWord);
            } else {
                pvo = new PagingVO(currentPage, numberPerPage, numberOfPageBlock, categoryNumber);
            }

            // JSP로 데이터 전송
            request.setAttribute("list", list);  // 상품 목록 전송
            request.setAttribute("pvo", pvo);    // 페이징 정보 전송
            request.setAttribute("categoryNumber", categoryNumber);
            request.setAttribute("searchWord", searchWord);
            request.setAttribute("sort", sort);

        } finally {
            if (conn != null) conn.close();  // DB 연결 닫기
        }

        return "/ohora/prd-nail-page.jsp";
    }
}
