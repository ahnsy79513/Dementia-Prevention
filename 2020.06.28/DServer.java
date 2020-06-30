import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


// Request�� Ŭ���̾�Ʈ�� ������ ��û, Response�� ������ Ŭ���̾�Ʈ�� ����
enum EMessageCode // Ŭ���̾�Ʈ�� ConfigDatas.java ���Ͽ� ���ǵ� enum�� ������ ����.
{
	eNone,
	eLoginRequest, // �α���
	eLoginResponse,
	eJoinRequest, // ����
	eJoinResponse,
	eRankingRequest, // ��ŷ ��û
	eRankingResponse,
	eMembershipEditRequest, // �г��� ����
	eMembershipEditResponse,
	eUpdateGameScoreRequest, // ���� ���
	eUpdateGameScoreResponse,
	eSelfTestPointUpdateRequest, // �ڰ����� ���
	eSelfTestPointUpdateResponse,
	eIDCheckRequest, // ���̵� �ߺ� üũ
	eIDCheckResponse,
};

// DementiaPreventionServer �� ���� DServer
public class DServer {
	private ServerSocket srverSocket; // ���� ����
	private boolean serverRun; // DServer�� �����ϴ� ClientSession ( Ŭ���̾�Ʈ�� ���� 1���� 1�� �ν��Ͻ� )���� ������ ���߾��� �� �� ClientSession �鵵 ���߰� �ϴ� �� ����Ѵ�.

	public DServer(int port) {
		serverRun = true;

		try {
			srverSocket = new ServerSocket(port); // ������ Argument �� ���� ��Ʈ 8000���� ���� ����

			System.out.println("Service start!");

			while (true) {
				try {
					Socket socket = srverSocket.accept(); // Ŭ���̾�Ʈ ���� �޾� ���� ����	
					socket.setTcpNoDelay(true); // ������ ���۸� ������ �ʰ� ������ ���� �Ѵ�.
					
					ClientSession client = new ClientSession(socket); // Ŭ���̾�Ʈ ���� �� ������ ������ ����� ���� ��ü ����
					Thread thread = new Thread(client); // Ŭ���̾�Ʈ ���ǿ��� ���� �����͸� ó���� �� �ְ� Thread ����
					thread.start(); // ClientSession�� run ���� Ŭ���̾�Ʈ�� ���� �����͸� Receive �� �� �ְ� �Ѵ�.				
				}catch(Exception e_) {	
				}
			}
		} catch (IOException e) {
			System.exit(0);
		} finally {
			// new ServerSocket(port) ���н� ������ ���� �ȴ�.
			if (srverSocket != null) {
				try {
					srverSocket.close(); // ���� ���� �ݴ� ��.
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
		new DServer(8000); // Ŭ�� ������ ��Ʈ�� 8000���� �Ѵ�. Ŭ���̾�Ʈ������ �ش� ��Ʈ�� connect �ϵ��� �ߴ�.
	}
}