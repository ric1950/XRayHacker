package me.ric.xrayhacker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingWorker;

//import nickguletskii200.SpyerAdmin.SpyerLog;

public class MineLogger {
	public static File log = new File("plugins/XRayHacker/xrh.log");
	public static String queued = "";
	public static SwingWorker<Object, Object> run = new SwingWorker<Object, Object>() {

		@Override
		protected Object doInBackground() throws Exception {
			while (!isCancelled()) {
				Thread.sleep(10000);
				appendFile(queued);
				queued = "";
			}
			return null;
		}
	};
	public static void start() {
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		run.execute();
		queued = "** " + dateTime() + " ** SESSION START\r\n";
	}
	public static void stop() {
		run.cancel(false);
//		queued += "[" + dateTime() + "]**SESSION STOP**";
//		appendFile(queued);
		queued = "";
	}

	public static void log(String str) {
		append("[" + time() + "]" + str + "\r\n");
	}

	private static void append(String str) {
		queued += str;
	}

	private static String dateTime() {
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	    Date date = new Date();

		String dateFormat = DateFormat.getDateTimeInstance(
	            DateFormat.LONG, DateFormat.SHORT).format(date);
		
//		Date date = new Date();
//		return dateFormat.format(date);
		return dateFormat;
	}
	private static String time() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private static void appendFile(String arg) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(log, true));
			bw.write(arg);
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
			}
		}
		
	}
//	System.out.println( "got to here 5");
}
