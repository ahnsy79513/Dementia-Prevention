import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// 서버로 접속 하여 데이터를 주고 받게 한다.
public class ClientApplication {
	private Socket clientSocket; // 서버에 접속할 소켓
	private BufferedReader clientSocketReceiver; // 서버로 부터 데이터 입력 받음, 소켓으로 부터 리소스를 가져옴
	private BufferedWriter clientSocketSender; // 서버에 데이터 전송함, 소켓으로 부터 리소스를 가져옴
	private MessageHandler msgHandler; // 서버로 부터 받은 메시지를 처리하는 처리자 (MainWindow 클래스에서 처리한다)

	public ClientApplication(MessageHandler handler) {
		clientSocket = null; // 소켓은 접속시에 초기화
		msgHandler = handler;
	}

	// 서버로 메시지 전송
	public void SendMessage(String message) {
		try {
			clientSocketSender.write(message + "\n");
			clientSocketSender.flush();

			System.out.println("SendMessage " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 접속 종료 ( 만들었는 데 사용은 안하는 메소드가 되었다 )
	private void Disconnect() {
		// 리소스 해제는 생성의 역순, 소켓을 가장 마지막에 한다.
		try {
			clientSocketSender.close();
		} catch (Exception e) {
		}

		try {
			clientSocketReceiver.close();
		} catch (Exception e) {
		}

		// 소켓으로 부터 리소스를 제공 받은 Buffer 들을 close 하고, 마지막으로 소켓을 해제 한다.
		try {
			clientSocket.close();
			clientSocket = null;
		} catch (Exception e) {
		}
	}

	// 서버에 접속 한다.
	public boolean Connect(String strServerIP_, int iPort_) {
		try {
			clientSocket = new Socket(strServerIP_, iPort_);
			clientSocket.setTcpNoDelay(true); // 데이터 버퍼를 모으지 않고 즉시즉시 전송 한다.

			System.out.println("Connect to Server : " + strServerIP_);

			// 서버와 통신하는 데이터의 포맷은 문자열이며 json 을 사용 한다. BufferdReader와  BufferedWriter를 이용해 구현 할 수 있다.
			clientSocketReceiver = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "utf-8"));
			clientSocketSender = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "utf-8"));

			if (clientSocketReceiver == null) {
				throw new Exception("BufferedReader null!");
			}

			if (clientSocketSender == null) {
				throw new Exception("BufferedWriter null!");
			}

			// 서버가 보낸 데이터를 수신받기 위한 스레드 준비 후 시작
			SocketReader reader = new SocketReader(this, clientSocketReceiver, msgHandler);
			Thread thread = new Thread(reader); // Receive를 다른 처리와 동시에 하기 위해 Thread로 처리
			thread.start();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean IsConnected() {
		return clientSocket != null && clientSocket.isConnected();
	}
}

// 서버나 보낸 데이터를 화면에 출력 한다.
class SocketReader implements Runnable {
	private BufferedReader clientSocketReceiver; // 서버로 부터 데이터를 전송받기 위한 스트림
	private ClientApplication client;
	private MessageHandler msgHandler;

	public SocketReader(ClientApplication parent_, BufferedReader receiver_, MessageHandler handler_) {
		this.clientSocketReceiver = receiver_;
		this.client = parent_;
		this.msgHandler = handler_;
	}

	 // Receive를 다른 처리와 동시에 하기 위해 Thread로 처리
	@Override
	public void run() {
		try {

			while (client.IsConnected()) {
				String message = clientSocketReceiver.readLine(); // 서버 데이터 수신

				// 서버로 부터 받은 메시지를 처리 한다. MainWindow 가 될 것이다.
				try {
					handleMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}

		// 메모리가 해제 될 수 있게 null 할당
		client = null;
		clientSocketReceiver = null;
	}

	private void handleMessage(String message) {
		System.out.println("Received " + message);

		JSONParser parser = new JSONParser();
		Object obj = null;

		// 서버와 통신하는 데이터의 포맷은 문자열이며 json 을 사용 한다.
		// 서버로 부터 받은 문자열을 json 으로 파싱 한다.
		try {
			obj = parser.parse(message);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		// 서버로 부터 받은 메시지를 처리 한다. msgHandler 는 MainWindow 의 인스턴스 일 것이다.
		JSONObject jsonObj = (JSONObject) obj;
		msgHandler.handleMessage(jsonObj);
	}
}