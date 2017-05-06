package rdt;

class SpecialPacket {
    private int checksum = -1;
    private int seq_num;
    private int flag;
    private int length;
    private long time;
    private byte[] data;
    private byte[] packet = null;
    

    public static final int CHECKSUM_LENGTH = 2;
    public static final int SEQ_NUM_LENGTH = 2;
    public static final int FLAG_LENGTH = 2;
    public static final int LENGTH_LENGTH = 2;
    public static final int TIME_LENGTH = 8;

    public static final int CHECKSUM_OFFSET = 0;
    public static final int SEQ_NUM_OFFSET = 2;
    public static final int FLAG_OFFSET = 4;
    public static final int LENGTH_OFFSET = 6;
    public static final int TIME_OFFSET = 8;

    public static final int HEADER_SIZE = 16;
    public static final int MAX_DATA_SIZE = 1024;

    public static final int FLAG_ACK = 0x10;      
    public static final int FLAG_FILENAME = 0x08;  
    public static final int FLAG_FILESIZE = 0x04; 
    public static final int FLAG_FILEDATA = 0x02;  
    public static final int FLAG_FILEEND = 0x01;   


    public SpecialPacket(int seq_num, int flag, byte[] data, int offset, int length) {
        this.seq_num = seq_num;
        this.flag = flag;
        this.length = length;
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
        time = System.currentTimeMillis();
    }

    public SpecialPacket(int seq_num, int flag, byte[] data) {
        this.seq_num = seq_num;
        this.flag = flag;
        this.length = data.length;
        this.data = new byte[length];
        System.arraycopy(data, 0, this.data, 0, length);
        time = System.currentTimeMillis();
    }

    public SpecialPacket(byte[] pkt, int start, int end) {
        assert end - start + 1 >= HEADER_SIZE;
        checksum = (int) byteToNum(CHECKSUM_LENGTH, pkt, start + CHECKSUM_OFFSET);
        seq_num = (int) byteToNum(SEQ_NUM_LENGTH, pkt, start + SEQ_NUM_OFFSET);
        flag = (int) byteToNum(FLAG_LENGTH, pkt, start + FLAG_OFFSET);
        length = (int) byteToNum(LENGTH_LENGTH, pkt, start + LENGTH_OFFSET);
        time = byteToNum(TIME_LENGTH, pkt, start + TIME_OFFSET);

        if (end - start + 1 != HEADER_SIZE + length) {
            System.out.println(String.format("Warring: packet size do not fix, SEQ = %d.", seq_num));
            length = end - start + 1 - HEADER_SIZE;
        }
        data = new byte[length];
        System.arraycopy(pkt, start + HEADER_SIZE, data, 0, data.length);
    }
    
    public int getSeq() {
        return seq_num;
    }

    public int getFlag() {
        return flag;
    }

    public int getLength() {
        return length;
    }

    public long getTime() {
        return time;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getPacket() {
        if (packet == null) {
            packet = makePacket(seq_num, flag, data, time);
        }
        return packet;
    }

    public int getChecksum() {
        if (checksum == -1) {
            checksum = ((getPacket()[CHECKSUM_OFFSET] & 0xFF) << 8) | (getPacket()[CHECKSUM_OFFSET+1] & 0xFF);
        }
        return checksum;
    }


    public static void numToByte(long value, int byteLength, byte[] data, int index) {
        assert byteLength <= 8; // length should <= 8
        assert data.length >= index + byteLength; // data length should >= index + byteLength
        for (int i=0; i<byteLength; ++i) {
            data[index + byteLength - 1 - i] = (byte)(value & 0xFF);
            value >>>= 8;
        }
    }

    public static long byteToNum(int byteLength, byte[] data, int index) {
        assert byteLength <= 8; // length should <= 8
        assert data.length >= index + byteLength; // data length should >= index + byteLength
        long value = 0;
        for (int i=0; i<byteLength; ++i) {
            value = (value << 8) | (data[index++] & 0xFF);
        }
        return value;
    }

    public static byte[] makeHeader(int seq_num, int flag, byte[] data, long time) {
        byte[] header = new byte[HEADER_SIZE];
        numToByte(seq_num, SEQ_NUM_LENGTH, header, SEQ_NUM_OFFSET);
        numToByte(flag, FLAG_LENGTH, header, FLAG_OFFSET);
        numToByte(data.length, LENGTH_LENGTH, header, LENGTH_OFFSET);
        numToByte(time, TIME_LENGTH, header, TIME_OFFSET);
        return header;
    }

    public static int generateChecksum(byte[] pkt, int offset) {
        return generateChecksum(pkt, offset, pkt.length);
    }

    public static int generateChecksum(byte[] pkt, int offset, int length) {
        int checksum = 0, tmp;
        for (int i=offset; i<offset+length; i+=2) {
            checksum += ((pkt[i] & 0xFF) << 8) | (i+1==offset+length ? 0 : pkt[i+1] & 0xFF);

            while ((tmp = checksum >>> 16) != 0) {
                checksum = (checksum & 0xFFFF) + tmp;
            }
        }
        return checksum;
    }

    public static byte[] makePacket(int seq_num, int flag, byte[] data, long time) {
        byte[] packet = new byte[HEADER_SIZE + data.length];
        byte[] header = makeHeader(seq_num, flag, data, time);
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(data, 0, packet, HEADER_SIZE, data.length);
        numToByte(generateChecksum(packet, CHECKSUM_OFFSET), CHECKSUM_LENGTH, packet, CHECKSUM_OFFSET);
        return packet;
    }
}