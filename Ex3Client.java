import java.io.InputStream;
import java.net.Socket;
import java.io.OutputStream;
import java.util.Hashtable;
import java.lang.String;
import java.lang.Byte;
import java.nio.ByteBuffer;

public final class Ex3Client {

    public static void main(String[] args) throws Exception {
		
		
		//connecting to socket and setup io
        try (Socket socket = new Socket("codebank.xyz", 38103)) {
			System.out.println("\nConnected to server.");
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			
			
			//Receive and print out the number of expected bytes
			int expectedBytes = is.read();
			System.out.println("Reading " + expectedBytes + " bytes.");
			
			
			
			//Read in the bytes into a byte array
			byte message[] = new byte[expectedBytes];
			int recInt;
			System.out.print("Data received:");
			for(int i = 0; i < expectedBytes; i++) {
				recInt = is.read();
				message[i] = (byte)recInt;
				if(i % 10 == 0) {
					System.out.print("\n   ");
				}
				if(recInt < 16) {
					System.out.print(0);
				}
				System.out.print(Integer.toHexString(recInt));
			}
			
			
			
			//Get the checksum and using a ByteBuffer convert it into a byte array
			short shortChecksum = checksum(message);
			ByteBuffer buffer = ByteBuffer.allocate(2);
			buffer.putShort(shortChecksum);
			byte returnMessage[] = buffer.array();
			
			
			
			//Print out the checksum
			int l = (int)shortChecksum;
			l = l & 0x0000FFFF;
			System.out.println("\nChecksum calculated: 0x" + Integer.toHexString(l) + ".");



			//send return message back to server and receive verification response
			os.write(returnMessage);
			
			if(is.read() == 1) {
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad");
			}
        }
		System.out.println("Disconnected from server.");
    }
	
	
	
	//checksum methods takes in a byte array and returns the checksum as a short
	public static short checksum(byte[] b) {
		long sum = 0;
		int count = b.length;
		long byteComb;
		int i = 0;
		while(count > 1) {
			byteComb = (((b[i] << 8) & 0xFF00) | ((b[i + 1]) & 0xFF));
			sum += byteComb;
			if((sum & 0xFFFF0000) > 0 ) {
				sum &= 0xFFFF;
				sum += 1;
			}
			i += 2;
			count -= 2;
		}
		if(count > 0) {
			sum += (b[b.length-1] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}
		sum = ~sum;
		sum = sum & 0xFFFF;
		return (short)sum;
	}
}