package pcep.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.bind.DatatypeConverter;
public class UefDecoder {
	public static void main(String[] args) throws Exception {
		File file=new File("src/main/resources/MikonTalo.b64");
		byte[] buffer = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(buffer);
		fis.close();
		  
		FileOutputStream fos = new FileOutputStream("src/main/resources/MikonTalo.zip");
		fos.write(DatatypeConverter.parseBase64Binary(new String(buffer)));
		fos.close();
	}
}
