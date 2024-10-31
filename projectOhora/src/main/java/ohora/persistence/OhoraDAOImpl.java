package ohora.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ohora.domain.DeptVO;
import ohora.domain.PagingVO;
import ohora.domain.ProductDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OhoraDAOImpl implements OhoraDAO{
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public OhoraDAOImpl(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public ArrayList<DeptVO> selectTest() throws SQLException {
		int deptno;
		String dname;
		String loc;
		
		ArrayList<DeptVO> list = null;
		String sql = "SELECT * FROM dept";
		
		DeptVO dvo = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				list = new ArrayList<DeptVO>();
				do {

					deptno = rs.getInt("deptno");
					dname = rs.getString("dname");
					loc = rs.getString("loc");

					dvo = new DeptVO().builder()
							.deptno(deptno)
							.dname(dname)
							.loc(loc)
							.build();

					list.add(dvo);

				} while (rs.next());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	@Override
	public int getTotalRecords(int categoryNumber) throws SQLException {
	    int totalRecords = 0;
	    String sql;

	    // 카테고리 번호가 0일 경우 전체 레코드 조회, 아닐 경우 카테고리별 레코드 조회
	    if (categoryNumber == 0) {
	        sql = "SELECT COUNT(*) FROM O_PRODUCT";  // 전체 상품
	    } else {
	        sql = "SELECT COUNT(*) FROM O_PRODUCT WHERE CAT_ID = ?";  // 카테고리별 상품
	    }

	    try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
	        if (categoryNumber != 0) {
	            pstmt.setInt(1, categoryNumber);  // 카테고리 값이 0이 아닌 경우 설정
	        }
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                totalRecords = rs.getInt(1);  // 총 레코드 수 반환
	            }
	        }
	    }

	    return totalRecords;
	}

	@Override
	public int getTotalRecords(String searchCondition, String searchWord) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalPages(int numberPerPage, int categoryNumber) throws SQLException {
	    int totalRows = 0;
	    String sql;
	    
	    // 카테고리 번호에 따라 쿼리 변경
	    if (categoryNumber == 0) {
	        sql = "SELECT COUNT(*) FROM O_PRODUCT";  // 전체 상품
	    } else {
	        sql = "SELECT COUNT(*) FROM O_PRODUCT WHERE CAT_ID = ?";  // 카테고리별 상품
	    }

	    // 디버깅용 출력: 카테고리 번호 확인
	    System.out.println("Category Number: " + categoryNumber);
	    
	    try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
	        if (categoryNumber != 0) {
	            // 카테고리 번호 설정이 누락되지 않도록 확인
	            pstmt.setInt(1, categoryNumber);  // 카테고리 번호가 0이 아니면 카테고리 값 설정
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                totalRows = rs.getInt(1);  // 총 레코드 수
	            }
	        }
	    }

	    // 총 페이지 수 계산
	    int totalPages = (totalRows + numberPerPage - 1) / numberPerPage;

	    // 디버깅용 출력
	    System.out.println("Total Rows: " + totalRows);  // 디버깅: 총 레코드 수 확인
	    System.out.println("Total Pages: " + totalPages);  // 디버깅: 총 페이지 수 확인

	    return totalPages;
	}


	@Override
	public int getTotalPages(int numberPerPage, String searchCondition, String searchWord) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	// 전체 목록
	@Override
	public ArrayList<ProductDTO> select(int currentPage, int numberPerPage) throws SQLException {

		String pdt_name;
		int pdt_amount;
		int pdt_discount_rate;
		String pdt_img_url;
		int pdt_review_count;
		int pdt_discount_amount;
		ArrayList<ProductDTO> list = null;
		
		
		
		String sql = "SELECT * FROM ( "
				+ "SELECT ROWNUM no, t.* FROM ("
				+ "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_adddate "
				+ "FROM O_PRODUCT "
				// + "ORDER BY pdt_adddate DESC "
				+ ") t "
				+ ") b "
				+ "WHERE no BETWEEN ? AND ? ";
		
		ProductDTO pdt = null;
		int start = (currentPage-1) * numberPerPage + 1;
		int end = start + numberPerPage -1;
		int totalRecords = getTotalRecords();
		if (end > totalRecords) end = totalRecords;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				list = new ArrayList<ProductDTO>();
				do {

					pdt_name = rs.getString("pdt_name");
					pdt_amount = rs.getInt("pdt_amount");
					pdt_discount_rate = rs.getInt("pdt_discount_rate");
					pdt_img_url = rs.getString("pdt_img_url");
					pdt_review_count = rs.getInt("pdt_review_count");
					
					if (pdt_discount_rate != 0) {
						pdt_discount_amount = (int) (pdt_amount - (pdt_amount * (pdt_discount_rate / 100.0f)));							
					} else {
						pdt_discount_amount = pdt_amount;
					}

					pdt = new ProductDTO().builder()
							.pdt_name(pdt_name)
							.pdt_amount(pdt_amount)
							.pdt_discount_rate(pdt_discount_rate)
							.pdt_img_url(pdt_img_url)
							.pdt_review_count(pdt_review_count)
							.pdt_discount_amount(pdt_discount_amount)
							.build();

					list.add(pdt);

				} while (rs.next());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	@Override
	public ArrayList<ProductDTO> selectProducts(int currentPage, int numberPerPage, int categoryNumber) throws SQLException {

	    String pdt_name;
	    int pdt_amount;
	    int pdt_discount_rate;
	    String pdt_img_url;
	    int pdt_review_count;
	    int pdt_discount_amount;
	    
	    ArrayList<ProductDTO> list = new ArrayList<>(); // 리스트 초기화

	    // 카테고리 값에 따라 SQL 쿼리 분리
	    String sql;
	    if (categoryNumber == 0) {
	        // 전체 상품 조회 쿼리 (카테고리 필터 없음)
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_adddate "
	            + "FROM O_PRODUCT "
	            + ") t "
	            + ") b "
	            + "WHERE no BETWEEN ? AND ?";
	    } else {
	        // 카테고리별 상품 조회 쿼리
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_adddate "
	            + "FROM O_PRODUCT "
	            + "WHERE CAT_ID = ? " // 카테고리 필터
	            + ") t "
	            + ") b "
	            + "WHERE no BETWEEN ? AND ?";
	    }

	    // 페이징 값 설정
	    int start = (currentPage - 1) * numberPerPage + 1;
	    int end = start + numberPerPage - 1;
	    int totalRecords = getTotalRecords(categoryNumber); // 총 레코드 수 가져오기
	    if (end > totalRecords) end = totalRecords;

	    // SQL 실행
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        if (categoryNumber == 0) {
	            // 전체 상품 조회일 때
	            pstmt.setInt(1, start); // 페이징 시작
	            pstmt.setInt(2, end);   // 페이징 끝
	        } else {
	            // 카테고리별 상품 조회일 때
	            pstmt.setInt(1, categoryNumber); // 카테고리 값 설정
	            pstmt.setInt(2, start);          // 페이징 시작
	            pstmt.setInt(3, end);            // 페이징 끝
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                pdt_name = rs.getString("pdt_name");
	                pdt_amount = rs.getInt("pdt_amount");
	                pdt_discount_rate = rs.getInt("pdt_discount_rate");
	                pdt_img_url = rs.getString("pdt_img_url");
	                pdt_review_count = rs.getInt("pdt_review_count");

	                if (pdt_discount_rate != 0) {
	                    pdt_discount_amount = (int) (pdt_amount - (pdt_amount * (pdt_discount_rate / 100.0f)));
	                } else {
	                    pdt_discount_amount = pdt_amount;
	                }

	                ProductDTO pdt = new ProductDTO().builder()
	                    .pdt_name(pdt_name)
	                    .pdt_amount(pdt_amount)
	                    .pdt_discount_rate(pdt_discount_rate)
	                    .pdt_img_url(pdt_img_url)
	                    .pdt_review_count(pdt_review_count)
	                    .pdt_discount_amount(pdt_discount_amount)
	                    .build();

	                list.add(pdt);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	@Override
	public ArrayList<ProductDTO> selectByProductName(String searchWord, int currentPage, int numberPerPage) throws SQLException {
	    ArrayList<ProductDTO> list = new ArrayList<>();
	    String sql = "SELECT * FROM ( "
	               + "SELECT ROWNUM no, t.* FROM ("
	               + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count "
	               + "FROM O_PRODUCT "
	               + "WHERE pdt_name LIKE ? ORDER BY pdt_adddate DESC) t "
	               + ") b WHERE no BETWEEN ? AND ?";

	    int start = (currentPage - 1) * numberPerPage + 1;
	    int end = start + numberPerPage - 1;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "%" + searchWord + "%");
	        pstmt.setInt(2, start);
	        pstmt.setInt(3, end);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProductDTO product = new ProductDTO();
	                product.setPdt_name(rs.getString("pdt_name"));
	                product.setPdt_amount(rs.getInt("pdt_amount"));
	                product.setPdt_discount_rate(rs.getInt("pdt_discount_rate"));
	                product.setPdt_img_url(rs.getString("pdt_img_url"));
	                product.setPdt_review_count(rs.getInt("pdt_review_count"));
	                list.add(product);
	            }
	        }
	    }
	    return list;
	}

	@Override
	public int getTotalRecordsByProductName(String searchWord) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM O_PRODUCT WHERE pdt_name LIKE ?";
	    int totalRecords = 0;
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "%" + searchWord + "%");
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                totalRecords = rs.getInt(1);
	            }
	        }
	    }
	    return totalRecords;
	}
	

	@Override
	public int getTotalRecords() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<ProductDTO> selectProductsByCreatedDate(int currentPage, int numberPerPage, int categoryNumber) throws SQLException {
	    ArrayList<ProductDTO> list = new ArrayList<>();
	    String sql;

	    // SQL 쿼리 구성 (카테고리 여부에 따라 구분)
	    if (categoryNumber == 0) {
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_adddate "
	            + "FROM O_PRODUCT ORDER BY pdt_adddate DESC) t "
	            + ") b WHERE no BETWEEN ? AND ?";
	    } else {
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_adddate "
	            + "FROM O_PRODUCT WHERE CAT_ID = ? ORDER BY pdt_adddate DESC) t "
	            + ") b WHERE no BETWEEN ? AND ?";
	    }

	    // 페이징 범위 설정
	    int start = (currentPage - 1) * numberPerPage + 1;
	    int end = start + numberPerPage - 1;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        if (categoryNumber != 0) {
	            pstmt.setInt(1, categoryNumber);
	            pstmt.setInt(2, start);
	            pstmt.setInt(3, end);
	        } else {
	            pstmt.setInt(1, start);
	            pstmt.setInt(2, end);
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProductDTO product = new ProductDTO();
	                product.setPdt_name(rs.getString("pdt_name"));
	                product.setPdt_amount(rs.getInt("pdt_amount"));
	                product.setPdt_discount_rate(rs.getInt("pdt_discount_rate"));
	                product.setPdt_img_url(rs.getString("pdt_img_url"));
	                product.setPdt_review_count(rs.getInt("pdt_review_count"));
	                product.setPdt_adddate(rs.getDate("pdt_adddate"));

	                // 할인 금액 계산
	                int discountAmount = (product.getPdt_discount_rate() != 0) 
	                    ? (int) (product.getPdt_amount() - (product.getPdt_amount() * (product.getPdt_discount_rate() / 100.0f))) 
	                    : product.getPdt_amount();
	                product.setPdt_discount_amount(discountAmount);

	                list.add(product);
	            }
	        }
	    }

	    return list;
	}

	@Override
	public ArrayList<ProductDTO> selectProductsBySales(int currentPage, int numberPerPage, int categoryNumber) throws SQLException {
	    ArrayList<ProductDTO> list = new ArrayList<>();
	    String sql;

	    // SQL 쿼리 구성 (카테고리 여부에 따라 구분)
	    if (categoryNumber == 0) {
	        // 전체 상품 조회 (카테고리 필터 없음)
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_sales_count "
	            + "FROM O_PRODUCT ORDER BY pdt_sales_count DESC) t "
	            + ") b WHERE no BETWEEN ? AND ?";
	    } else {
	        // 카테고리별 상품 조회
	        sql = "SELECT * FROM ( "
	            + "SELECT ROWNUM no, t.* FROM ("
	            + "SELECT pdt_name, pdt_amount, pdt_discount_rate, pdt_img_url, pdt_review_count, pdt_sales_count "
	            + "FROM O_PRODUCT WHERE CAT_ID = ? ORDER BY pdt_sales_count DESC) t "
	            + ") b WHERE no BETWEEN ? AND ?";
	    }

	    // 페이징 범위 설정
	    int start = (currentPage - 1) * numberPerPage + 1;
	    int end = start + numberPerPage - 1;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        if (categoryNumber != 0) {
	            pstmt.setInt(1, categoryNumber);
	            pstmt.setInt(2, start);
	            pstmt.setInt(3, end);
	        } else {
	            pstmt.setInt(1, start);
	            pstmt.setInt(2, end);
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProductDTO product = new ProductDTO();
	                product.setPdt_name(rs.getString("pdt_name"));
	                product.setPdt_amount(rs.getInt("pdt_amount"));
	                product.setPdt_discount_rate(rs.getInt("pdt_discount_rate"));
	                product.setPdt_img_url(rs.getString("pdt_img_url"));
	                product.setPdt_review_count(rs.getInt("pdt_review_count"));
	                product.setPdt_sales_count(rs.getInt("pdt_sales_count"));

	                // 할인 금액 계산
	                int discountAmount = (product.getPdt_discount_rate() != 0) 
	                    ? (int) (product.getPdt_amount() - (product.getPdt_amount() * (product.getPdt_discount_rate() / 100.0f))) 
	                    : product.getPdt_amount();
	                product.setPdt_discount_amount(discountAmount);

	                list.add(product);
	            }
	        }
	    }

	    return list;
	}
}
