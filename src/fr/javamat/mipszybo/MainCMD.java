package fr.javamat.mipszybo;

public class MainCMD {
	public static void main(String[] args) {
//		int a = 0x8FFFFFFF;
//		int b = 0x00000001;
//		long c = Integer.toUnsignedLong(a) + Integer.toUnsignedLong(b);
//		int c1 = (int) c;
//		
//		System.out.println(a + " + " + b + " = " + c + " (" + c1 + ")");
//		
//		System.out.println((a >> 31) + " " + (Integer.toUnsignedLong(a) >> 31));
		
		int i = 2;
		MemoryRAM ram = new MemoryRAM();
		ram.setAddr(0);ram.write(0xCCCCCCCC);
		ram.setAddr(4);ram.write(0xCCCCCCCC);
		ram.setAddr(i);ram.write(0xAAAAAAAA);
		System.out.println(Integer.toHexString(ram.getDataAt(0)) + "\n" + Integer.toHexString(ram.getDataAt(4)) + "\n\n"+Integer.toHexString(ram.getDataAt(i)));
	}
}
