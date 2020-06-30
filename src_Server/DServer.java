import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


// Request는 클라이언트가 서버로 요청, Response는 서버가 클라이언트로 응답
enum EMessageCode // 클라이언트의 ConfigDatas.java 파일에 정의된 enum과 완전히 같다.
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

// DementiaPreventionServer 의 약자 DServer
public class DServer {
	private ServerSocket srverSocket; // 서버 소켓
	private boolean serverRun; // DServer를 참조하는 ClientSession ( 클라이언트의 접속 1개당 1개 인스턴스 )에서 서버가 멈추었을 때 각 ClientSession 들도 멈추게 하는 데 사용한다.

	public DServer(int port) {
		serverRun = true;

		try {
			srverSocket = new ServerSocket(port); // 생성자 Argument 로 받은 포트 8000으로 서버 시작

			System.out.println("Service start!");

			while (true) {
				try {
					Socket socket = srverSocket.accept(); // 클라이언트 접속 받아 소켓 생성	
					socket.setTcpNoDelay(true); // 데이터 버퍼를 모으지 않고 즉시즉시 전송 한다.
					
					ClientSession client = new ClientSession(socket); // 클라이언트 정보 및 데이터 수신을 담당할 세션 객체 생성
					Thread thread = new Thread(client); // 클라이언트 세션에서 수신 데이터를 처리할 수 있게 Thread 생성
					thread.start(); // ClientSession의 run 에서 클라이언트로 부터 데이터를 Receive 할 수 있게 한다.				
				}catch(Exception e_) {	
				}
			}
		} catch (IOException e) {
			System.exit(0);
		} finally {
			// new ServerSocket(port) 실패시 서버가 종료 된다.
			if (srverSocket != null) {
				try {
					srverSocket.close(); // 서버 소켓 닫는 다.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		serverRun = false;
	}

	public boolean IsRun() {
		return serverRun;
	}

	public static void main(String[] args) {
		new DServer(8000); // 클라가 접속할 포트는 8000으로 한다. 클라이언트에서는 해당 포트로 connect 하도록 했다.
	}
}