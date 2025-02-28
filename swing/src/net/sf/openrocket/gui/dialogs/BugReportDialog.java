package net.sf.openrocket.gui.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jogamp.opengl.JoglVersion;

import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.gui.components.StyledLabel;
import net.sf.openrocket.gui.components.URLLabel;
import net.sf.openrocket.gui.util.GUIUtil;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.logging.LogLevelBufferLogger;
import net.sf.openrocket.logging.LogLine;
import net.sf.openrocket.logging.LoggingSystemSetup;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.util.BuildProperties;
import net.sf.openrocket.util.JarUtil;
import net.sf.openrocket.gui.widgets.SelectColorButton;

@SuppressWarnings("serial")
public class BugReportDialog extends JDialog {
	
	private static final String NEW_ISSUES_URL = "https://github.com/openrocket/openrocket/issues/new";
	private static final String REPORT_EMAIL = "openrocket-bugs@lists.sourceforge.net";
	private static final String REPORT_EMAIL_URL = "mailto:" + REPORT_EMAIL;
	
	private static final Translator trans = Application.getTranslator();
	
	
	public BugReportDialog(Window parent, String labelText, final String message, final boolean sendIfUnchanged) {
		//// Bug report
		super(parent, trans.get("bugreport.dlg.title"), Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		
		// Some fscking Swing bug that makes html labels initially way too high
		JLabel label = new JLabel(labelText);
		Dimension d = label.getPreferredSize();
		d.width = 100000;
		label.setMaximumSize(d);
		panel.add(label, "gapleft para, wrap para");
		
		//// <html>If connected to the Internet, you can simply click 
		//// <em>Send bug report</em>.
		label = new JLabel(trans.get("bugreport.dlg.connectedInternet"));
		panel.add(label, "gapleft para, split 2, gapright rel");
		
		panel.add(new URLLabel(NEW_ISSUES_URL), "growx, wrap para");
		
		//// Otherwise, send the text below to the address:
		panel.add(new JLabel(trans.get("bugreport.dlg.otherwise") + " "),
				  "gapleft para, split 2, gapright rel");
		panel.add(new URLLabel(REPORT_EMAIL_URL, REPORT_EMAIL), "growx, wrap para");
		
		
		final JTextArea textArea = new JTextArea(message, 20, 70);
		textArea.setEditable(true);
		panel.add(new JScrollPane(textArea), "grow, wrap");
		
		panel.add(new StyledLabel(trans.get("bugreport.lbl.Theinformation"), -1), "wrap para");
		
		////Close button
		JButton close = new SelectColorButton(trans.get("dlg.but.close"));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BugReportDialog.this.dispose();
			}
		});
		panel.add(close, "right, sizegroup buttons, split");
		
		this.add(panel);
		
		this.validate();
		this.pack();
		this.pack();
		this.setLocationRelativeTo(parent);
		
		GUIUtil.setDisposableDialogOptions(this, close);
	}
	
	/**
	 * Show a general bug report dialog allowing the user to input information about
	 * the bug they encountered.
	 * 
	 * @param parent	the parent window (may be null).
	 */
	public static void showBugReportDialog(Window parent) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("---------- Bug report ----------\n");
		sb.append('\n');
		sb.append("Include detailed steps on how to trigger the bug:\n");
		sb.append('\n');
		sb.append("1. \n");
		sb.append("2. \n");
		sb.append("3. \n");
		sb.append('\n');
		
		sb.append("What does the software do and what in your opinion should it do in the " +
				"case described above:\n");
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		
		sb.append("Include your email address (optional; it helps if we can " +
				"contact you in case we need additional information):\n");
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		
		
		sb.append("(Do not modify anything below this line.)\n");
		sb.append("---------- System information ----------\n");
		addSystemInformation(sb);
		sb.append("---------- Error log ----------\n");
		addErrorLog(sb);
		sb.append("---------- End of bug report ----------\n");
		sb.append('\n');
		
		BugReportDialog reportDialog = new BugReportDialog(parent,
				trans.get("bugreport.reportDialog.txt"), sb.toString(), false);
		reportDialog.setVisible(true);
	}
	
	
	/**
	 * Show a dialog presented when an uncaught exception occurs.
	 * 
	 * @param parent	the parent window (may be null).
	 * @param t			the thread that encountered the exception (may be null).
	 * @param e			the exception.
	 */
	public static void showExceptionDialog(Window parent, Thread t, Throwable e) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("---------- Bug report ----------\n");
		sb.append('\n');
		sb.append("Please include a description about what actions you were " +
				"performing when the exception occurred:\n");
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		
		
		sb.append("Include your email address (optional; it helps if we can " +
				"contact you in case we need additional information):\n");
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		sb.append('\n');
		
		sb.append("(Do not modify anything below this line.)\n");
		sb.append("---------- Exception stack trace ----------\n");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		sb.append(sw.getBuffer());
		sb.append('\n');
		
		
		sb.append("---------- Thread information ----------\n");
		if (t == null) {
			sb.append("Thread is not specified.");
		} else {
			sb.append(t + "\n");
		}
		sb.append('\n');
		
		
		sb.append("---------- System information ----------\n");
		addSystemInformation(sb);
		sb.append("---------- Error log ----------\n");
		addErrorLog(sb);
		sb.append("---------- End of bug report ----------\n");
		sb.append('\n');
		
		BugReportDialog reportDialog =
				//// <html><b>Please include a short description about what you were doing when the exception occurred.</b>
				new BugReportDialog(parent, trans.get("bugreport.reportDialog.txt2"), sb.toString(), true);
		reportDialog.setVisible(true);
	}
	
	private static void addSystemInformation(StringBuilder sb) {
		sb.append("OpenRocket version: " + BuildProperties.getVersion() + "\n");
		sb.append("OpenRocket source: " + BuildProperties.getBuildSource() + "\n");
		sb.append("OpenRocket location: " + JarUtil.getCurrentJarFile() + "\n");
		sb.append("JOGL version: " + JoglVersion.getInstance().getImplementationVersion() + "\n");
		sb.append("Current default locale: " + Locale.getDefault() + "\n");
		sb.append("System properties:\n");
		
		// Sort the keys
		SortedSet<String> keys = new TreeSet<String>();
		for (Object key : System.getProperties().keySet()) {
			keys.add((String) key);
		}
		
		for (String key : keys) {
			String value = System.getProperty(key);
			sb.append("  " + key + "=");
			if (key.equals("line.separator")) {
				for (char c : value.toCharArray()) {
					sb.append(String.format("\\u%04x", (int) c));
				}
			} else {
				sb.append(value);
			}
			sb.append('\n');
		}
	}
	
	private static void addErrorLog(StringBuilder sb) {
		LogLevelBufferLogger buffer = LoggingSystemSetup.getBufferLogger();
		List<LogLine> logs = buffer.getLogs();
		for (LogLine l : logs) {
			sb.append(l.toString()).append('\n');
		}
	}
	
}
