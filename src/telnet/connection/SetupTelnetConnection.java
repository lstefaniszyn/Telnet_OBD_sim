package telnet.connection;

import java.io.InputStream;
import java.io.PrintStream;

import telnet.commandlist.Command;

public class SetupTelnetConnection implements Runnable {
	
	String address = "192.168.1.20";
	int port = 2001;
	
	TelnetStatus telnetStatus;
	
	public SetupTelnetConnection(TelnetStatus telnetStatus, String address, int port) {
		if (address != null ){
			this.address = address;
		}
		if (port != 0){
			this.port = port;
		}
		this.telnetStatus = telnetStatus;
	}
	
	public SetupTelnetConnection(TelnetStatus telnetStatus) {
		this.telnetStatus = telnetStatus;
	}
	
	@Override
	public void run() {
//		synchronized(telnetStatus){
			try {
				TelnetConnection.getConnection(this.address, this.port);
				telnetStatus.setTelnetInstace(TelnetConnection.getInstance());
				
//					// Get input and output stream references
				
				
				InputStream in = TelnetRead.getInputStream(TelnetConnection.getInstance()); 
				telnetStatus.setInputStream(in);
				PrintStream out = TelnetWrite.getOutputStream(TelnetConnection.getInstance());
				telnetStatus.setPrinterStream(out);
				

				// Advance to a prompt
				TelnetRead.readUntil("ser2net port 2001 device /home/pi/dev/pts_out [9600 N81] (Debian GNU/Linux)");
				
				TelnetSendCommand.sendCommand("?");
				TelnetSendCommand.sendCommand("AT SP 0");
				
//				telnetStatus.notifyAll();
				
			}
			catch (Exception e) {
				e.printStackTrace();
				
//			}	
		}
		
		do {
//			System.out.println("telnetStatus.getCommandList(): " + telnetStatus.getCommandList());
			for (Command commandObj: telnetStatus.getCommandList()){
				if (commandObj.getOutput() == null){
					String command = commandObj.getCommand();
					String output = TelnetSendCommand.sendCommand(command);
					synchronized(commandObj){
						commandObj.setOutput(output);
						commandObj.notify();
					}
				} continue;
				
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				break;
			}
		} while (!Thread.currentThread().isInterrupted());
	}
}
		

