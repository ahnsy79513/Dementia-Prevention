import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// ������ ���� �Ͽ� �����͸� �ְ� �ް� �Ѵ�.
public class ClientApplication {
	private Socket clientSocket; // ������ ������ ����
	private BufferedReader clientSocketReceiver; // ������ ���� ������ �Է� ����, �������� ���� ���ҽ��� ������
	private BufferedWriter clientSocketSender; // ������ ������ ������, �������� ���� ���ҽ��� ������
	private MessageHandler msgHandler; // ������ ���� ���� �޽����� ó���ϴ� ó���� (MainWindow Ŭ�������� ó���Ѵ�)

	public ClientApplication(MessageHandler handler) {
		clientSocket = null; // ������ ���ӽÿ� �ʱ�ȭ
		msgHandler = handler;
	}

	// ������ �޽��� ����
	public void SendMessage(String message) {
		try {
			clientSocketSender.write(message + "\n");
			clientSocketSender.flush();

			System.out.println("SendMessage " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ���� ���� ( ������� �� ����� ���ϴ� �޼ҵ尡 �Ǿ��� )
	private void Disconnect() {
		// ���ҽ� ������ ������ ����, ������ ���� �������� �Ѵ�.
		try {
			clientSocketSender.close();
		} catch (Exception e) {
		}

		try {
			clientSocketReceiver.close();
		} catch (Exception e) {
		}

		// �������� ���� ���ҽ��� ���� ���� Buffer ���� close �ϰ�, ���������� ������ ���� �Ѵ�.
		try {
			clientSocket.close();
			clientSocket = null;
		} catch (Exception e) {
		}
	}

	// ������ ���� �Ѵ�.
	public boolean Connect(String strServerIP_, int iPort_) {
		try {
			clientSocket = new Socket(strServerIP_, iPort_);
			clientSocket.setTcpNoDelay(true); // ������ ���۸� ������ �ʰ� ������ ���� �Ѵ�.

			System.out.println("Connect to Server : " + strServerIP_);

			// ������ ����ϴ� �������� ������ ���ڿ��̸� json �� ��� �Ѵ�. BufferdReader��  BufferedWriter�� �̿��� ���� �� �� �ִ�.
			clientSocketReceiver = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "utf-8"));
			clientSocketSender = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "utf-8"));

			if (clientSocketReceiver == null) {
				throw new Exception("BufferedReader null!");
			}

			if (clientSocketSender == null) {
				throw new Exception("BufferedWriter null!");
			}

			// ������ ���� �����͸� ���Źޱ� ���� ������ �غ� �� ����
			SocketReader reader = new SocketReader(this, clientSocketReceiver, msgHandler);
			Thread thread = new Thread(reader); // Receive�� �ٸ� ó���� ���ÿ� �ϱ� ���� Thread�� ó��
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

// ������ ���� �����͸� ȭ�鿡 ��� �Ѵ�.
class SocketReader implements Runnable {
	private BufferedReader clientSocketReceiver; // ������ ���� �����͸� ���۹ޱ� ���� ��Ʈ��
	private ClientApplication client;
	private MessageHandler msgHandler;

	public SocketReader(ClientApplication parent_, BufferedReader receiver_, MessageHandler handler_) {
		this.clientSocketReceiver = receiver_;
		this.client = parent_;
		this.msgHandler = handler_;
	}

	 // Receive�� �ٸ� ó���� ���ÿ� �ϱ� ���� Thread�� ó��
	@Override
	public void run() {
		try {

			while (client.IsConnected()) {
				String message = clientSocketReceiver.readLine(); // ���� ������ ����

				// ������ ���� ���� �޽����� ó�� �Ѵ�. MainWindow �� �� ���̴�.
				try {
					handleMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}

		// �޸𸮰� ���� �� �� �ְ� null �Ҵ�
		client = null;
		clientSocketReceiver = null;
	}

	private void handleMessage(String message) {
		System.out.println("Received " + message);

		JSONParser parser = new JSONParser();
		Object obj = null;

		// ������ ����ϴ� �������� ������ ���ڿ��̸� json �� ��� �Ѵ�.
		// ������ ���� ���� ���ڿ��� json ���� �Ľ� �Ѵ�.
		try {
			obj = parser.parse(message);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		// ������ ���� ���� �޽����� ó�� �Ѵ�. msgHandler �� MainWindow �� �ν��Ͻ� �� ���̴�.
		JSONObject jsonObj = (JSONObject) obj;
		msgHandler.handleMessage(jsonObj);
	}
}