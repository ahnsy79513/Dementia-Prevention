import javax.swing.JOptionPane;

// ����� ���̾�α� ���.
// JOptionPane.showConfirmDialog �� �Ķ���Ͱ� ���Ƽ� ���� ����ϱ� ���ؼ� static �޼��带 ������ Ŭ������ ����� ������
public class MessageBoxUtil {
	public static int ShowMessageBox(String msg, String title) {
		int dialogButton = JOptionPane.CLOSED_OPTION;
		int result = JOptionPane.showConfirmDialog(null, msg, title, dialogButton);
		return result;
	}
}
