
public class ConfigDatas {
	public static final String SERVER_IP = "127.0.0.1"; // ������ ������ ������
	public static final int SERVER_PORT = 8000; // ������ ������ ��Ʈ
	public static final int WIN_WIDTH = 700, WIN_HEIGHT = 800; // ȭ�� ���� ���� ũ��
	public static final int MOLE_SHOW_TIME = 1500; // �δ������� 1.5�ʸ��� �δ����� ���´�.
}

// Request�� Ŭ���̾�Ʈ�� ������ ��û, Response�� ������ Ŭ���̾�Ʈ�� ����
enum EMessageCode // ������ DServer.java �� ���ǵ� enum�� ������ ����.
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
