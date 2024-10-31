package ohora.persistence;

import java.sql.SQLException;
import java.util.ArrayList;

import ohora.domain.DeptVO;
import ohora.domain.ProductDTO;

public interface OhoraDAO {
	ArrayList<DeptVO> selectTest() throws SQLException;
	
	ArrayList<ProductDTO> select(int currentPage, int numberPerPage) throws SQLException;
	
	// 1-3. 총 레코드 수
	int getTotalRecords() throws SQLException;
	
	int getTotalRecords(int categoryNumber) throws SQLException;
	
	int getTotalRecords(String searchCondition, String searchWord) throws SQLException;
	// 1-4. 총 페이지 수
	int getTotalPages(int numberPerPage, int categoryNumber) throws SQLException;
	
	// 검색시 총페이지
	int getTotalPages(int numberPerPage, String searchCondition, String searchWord) throws SQLException;
	
	// 카테고리 적용해서 조회
	ArrayList<ProductDTO> selectProducts(int currentPage, int numberPerPage, int categoryNumber) throws SQLException;

	int getTotalRecordsByProductName(String searchWord) throws SQLException;

	ArrayList<ProductDTO> selectByProductName(String searchWord, int currentPage, int numberPerPage)
			throws SQLException;

	ArrayList<ProductDTO> selectProductsByCreatedDate(int currentPage, int numberPerPage, int categoryNumber) throws SQLException;

	ArrayList<ProductDTO> selectProductsBySales(int currentPage, int numberPerPage, int categoryNumber) throws SQLException;

}
