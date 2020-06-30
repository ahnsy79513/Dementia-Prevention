import java.sql.*;
import java.util.Vector;

// MySQL 
// DBManager ������ ������� ó���ϴ� Ŭ����
public class DBManager {
	private final static String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
	private final static String ADMIN = "root"; // ADMIN ����
	private final static String PASS = "thdus99"; // ADMIN ���� �н�����
	private final static String DB_ADDRESS = "127.0.0.1";
	private final static String CONNECT_INFO = "jdbc:mysql://" + DB_ADDRESS + ":3306/dementiaprevention"; // 127.0.0.1 ���ÿ� ��ġ�� MySQL�� �̿� �Ѵ�.
	
	// ���� ���� ó��
	public static boolean InsertJoinAccount(String loginid, String name, String pw, String address, int age, String job, boolean isFemale) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		
		boolean success = false;
		
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);

			String sql = "INSERT INTO account VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, loginid);
			pstmt.setString(2, "");
			pstmt.setString(3, pw);
			pstmt.setInt(4, 0);
			pstmt.setInt(5, 0);
			pstmt.setString(6, "");
			pstmt.setInt(7, 0);
			pstmt.setString(8, name);
			pstmt.setString(9, address);
			pstmt.setInt(10, age);
			pstmt.setString(11, job);
			pstmt.setBoolean(12, isFemale);

			int result = pstmt.executeUpdate();
			success = result > 0 ? true : false;

			pstmt.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return success;
	}
	
	// ��ŷ �����͸� �����´�.
	public static Vector<Account> SelectTotalAccountsForRank(String descColumn) {
		Vector<Account> vecResult = new Vector<Account>();

		Connection connection = null;
		Statement st = null;
		
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);
			st = connection.createStatement();

			String sql = "SELECT * FROM account WHERE nickname != '' and " + descColumn + " > 0 ORDER BY " + descColumn + " desc;";
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
				Account account = new Account();
				account.loginid = GetString(rs.getString(1));;
				account.nickname = GetString(rs.getString(2));
				account.passwrd = GetString(rs.getString(3));
				account.scoreMole = rs.getInt(4);
				account.scorePicture = rs.getInt(5);
				account.infomation = GetString(rs.getString(6));
				account.pointSelfTest = rs.getInt(7);
				account.name = GetString(rs.getString(8));
				account.address = GetString(rs.getString(9));
				account.age = rs.getInt(10);
				account.job = rs.getString(11);
				account.isFemale = rs.getBoolean(12);
				vecResult.add(account);
			}

			rs.close();
			st.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return vecResult;
	}
	
	// �α��� ó��. WHERE�� �н����带 �Բ� ó������ �ʴ� ������ ���� ������ �н����� ������ �����ϱ� ���� ��.
	public static Account Login(String loginid, String passwd) {
		Connection connection = null;
		Statement st = null;
		
		Account login = null;
		
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);
			st = connection.createStatement();

			String sql = "SELECT * FROM account WHERE loginid='" + loginid + "';";
			ResultSet rs = st.executeQuery(sql);
			
			login = new Account();
			
			if( rs == null )
				return null;

			if (rs.next()) {
				login.loginid = loginid;
				login.nickname = GetString(rs.getString(2));
				login.passwrd = GetString(rs.getString(3));
				login.scoreMole = rs.getInt(4);
				login.scorePicture = rs.getInt(5);
				login.infomation = GetString(rs.getString(6));
				login.pointSelfTest = rs.getInt(7);
				login.name = GetString(rs.getString(8));
				login.address = GetString(rs.getString(9));
				login.age = rs.getInt(10);
				login.job = rs.getString(11);
				login.isFemale = rs.getBoolean(12);
			}

			rs.close();
			st.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException se2) {
			}
			
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return login;
	}
	
	// �ߺ� ���� Ȯ��
	public static boolean CanUseLoginID(String loginid) {
		Connection connection = null;
		Statement st = null;
		
		boolean result = true;
		
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);
			st = connection.createStatement();

			String sql = "SELECT * FROM account WHERE loginid='" + loginid + "';";
			ResultSet rs = st.executeQuery(sql);
						
			if( rs == null )
				return true;

			if (rs.next()) {
				if( rs.getString("loginid").equals(loginid) )
					result = false; // �ߺ�
				else
					result = true; // ���̵� ��� ����
			}

			rs.close();
			st.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException se2) {
			}
			
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		return result;
	}

	// ȸ�� ���� ���� ���������� �г��Ӱ� ġ�� ������ ���� ó��
	public static void UpdateNickAndInfo(String loginid, String nick, String info) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);

			String sql = "update account set nickname=?, infomation=? where loginid=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, nick);
			pstmt.setString(2, info);
			pstmt.setString(3, loginid);

			int result = pstmt.executeUpdate();
			System.out.println("result " + result);

			pstmt.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	// �δ��� ��� ���� ���� ����
	public static void UpdateScoreMole(String loginid, int score) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);

			String sql = "update account set gamescore_mole=? where loginid=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, score);
			pstmt.setString(2, loginid);

			int result = pstmt.executeUpdate();
			System.out.println("result " + result);

			pstmt.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	// ���� �׸� ���߱� ���� ����
	public static void UpdateScorePicture(String loginid, int score) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);

			String sql = "update account set gamescore_picture=? where loginid=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, score);
			pstmt.setString(2, loginid);

			int result = pstmt.executeUpdate();
			System.out.println("result " + result);

			pstmt.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	// �ڰ����� ���� ����
	public static void UpdateSelfTest(String loginid, int point) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(CONNECT_INFO, ADMIN, PASS);

			String sql = "update account set selftest=? where loginid=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, point);
			pstmt.setString(2, loginid);

			int result = pstmt.executeUpdate();
			System.out.println("result " + result);

			pstmt.close();
			connection.close();
		} catch (SQLException se1) {
			se1.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	// ���ڿ� ��ȯ ���� null �� ���, "" �� ������ ä�� �ش�. null ���� ������ �߻����� �ʰ� �Ѵ�.
	private static String GetString(String str) {
		if( str == null )
			return "";
		
		return str;
	}

	/*
	public static void Test() {
		MySQLTestMain.InsertJoinAccount("newaccount", "aadffdf", "passwdsdf", "addresss", 11, "job", false);
		MySQLTestMain.UpdateNickAndInfo("test", "aaaa", "bbbbbbb");
		MySQLTestMain.UpdateScoreMole("test", 12);
		MySQLTestMain.UpdateScorePicture("test", 15);
		MySQLTestMain.UpdateSelfTest("test", 13);
		Select();
	}
	*/
}