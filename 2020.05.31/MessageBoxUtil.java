import javax.swing.JOptionPane;

// 모달형 다이얼로그 출력.
// JOptionPane.showConfirmDialog 의 파라메터가 많아서 쉽게 사용하기 위해서 static 메서드를 가지는 클래스를 만들어 보았음
public class MessageBoxUtil {
	public static int ShowMessageBox(String msg, String title) {
		int dialogButton = JOptionPane.CLOSED_OPTION;
		int result = JOptionPane.showConfirmDialog(null, msg, title, dialogButton);
		return result;
	}
}
