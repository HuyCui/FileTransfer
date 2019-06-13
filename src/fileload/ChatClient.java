package fileload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("all")
public class ChatClient {

	private Socket socket;
	private ReceiveFileThread rf = null;
	private String tempfile = "";
	public boolean stopBit = true;
	private Thread sendThread = null;
	private Thread receiveThread = null;
	public ChatClient() {

		try {
			socket = new Socket("127.0.0.1", 8089);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		Thread sendThread = new Thread(new SendThread());
		Thread receiveThread = new Thread(new ReceiceThread());
		sendThread.start();
		receiveThread.start();
	}

	public void stopThreads() {
		stopBit = false;
		receiveThread.interrupt();
		sendThread.interrupt();
	}

	public void deleteFile(String filename) {

		File file = new File(filename);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	public static void main(String[] args) {

		ChatClient chatClient = new ChatClient();
		chatClient.start();
	}

	public class SendThread implements Runnable {

		@Override
		public void run() {
			try {
				OutputStream outputStream = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(outputStream);
				PrintWriter pw = new PrintWriter(osw, true);
				Scanner scanner = new Scanner(System.in);

				while (true && stopBit) {
					/*
					 * String sentStr = scanner.nextLine(); if(!sentStr.equals("")) {
					 * System.out.println("i have sent the message "+sentStr);
					 * pw.println(scanner.nextLine()); }
					 */
					if (scanner.hasNext()) {
						String temp = scanner.nextLine();
						String[] strArray = temp.split(" ");
						System.out.println(temp);
						pw.println(temp);

						System.out.println("--------------");
						if (strArray[0].equals("get")) {
							tempfile = strArray[1];
							rf = new ReceiveFileThread();
							rf.start();
							System.out.println("++++++++++++++");
						} else if (strArray[0].equals("stop")) {
							rf.stopTransmit();
						} else if (strArray[0].equals("cancel")) {
							rf.stopTransmit();
							deleteFile(tempfile + ".temp");
						} else if (strArray[0].equals("quit")) {
							stopThreads();
						}

					}

				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
	}

	public class ReceiceThread implements Runnable {

		@Override
		public void run() {
			System.out.println("the Client thread is running!");
			try {
				InputStream inputStream = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);

				while (true && stopBit) {
					String returnstate = br.readLine();
					System.out.println(returnstate);
					if (returnstate.equals("00")) {
						System.out.println("the file is not exist");
					} else if (returnstate.equals("01")) {
						System.out.println("the file exists");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage().toString());
			}
		}
	}
}
