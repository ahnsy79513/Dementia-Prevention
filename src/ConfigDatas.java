
public class ConfigDatas {
	public static final String SERVER_IP = "127.0.0.1"; // 서버로 접속할 아이피
	public static final int SERVER_PORT = 8000; // 서버로 접속할 포트
	public static final int WIN_WIDTH = 700, WIN_HEIGHT = 800; // 화면 가로 세로 크기
	public static final int MOLE_SHOW_TIME = 1500; // 두더지게임 1.5초마다 두더지가 나온다.
}

// Request는 클라이언트가 서버로 요청, Response는 서버가 클라이언트로 응답
enum EMessageCode // 서버에 DServer.java 에 정의된 enum과 완전히 같다.
{
	eNone,
	eLoginRequest, // 로그인
	eLoginResponse,
	eJoinRequest, // 가입
	eJoinResponse,
	eRankingRequest, // 랭킹 요청
	eRankingResponse,
	eMembershipEditRequest, // 닉네임 수정
	eMembershipEditResponse,
	eUpdateGameScoreRequest, // 게임 결과
	eUpdateGameScoreResponse,
	eSelfTestPointUpdateRequest, // 자가진단 결과
	eSelfTestPointUpdateResponse,
	eIDCheckRequest, // 아이디 중복 체크
	eIDCheckResponse,
};
