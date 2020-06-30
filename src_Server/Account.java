
// 유저 계정 정보. 서버와 클라이언트가 같은 클래스를 정의 해서 사용
public class Account{
	public String loginid;
	public String nickname;
	public String passwrd;
	public String infomation;
	public String name;
	public String address;
	public String job;
	public int scoreMole; // 두더지게임 점수
	public int scorePicture; // 그림 맞추기 점수
	public int pointSelfTest;
	public int age;
	public boolean isFemale;
	
	public Account() {
		loginid = "";
		nickname = "";
		passwrd = "";
		infomation = "";
		name = "";
		address = "";
		job = "";
		scoreMole = 0;
		scorePicture = 0;
		pointSelfTest = 0;
		age = 0;
		isFemale = false;
	}
}