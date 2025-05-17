package bp.tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class En3Des {
    private static Logger logger = LoggerFactory.getLogger(En3Des.class);
    private static final String ENKEY = "6#dPz>3F6#dPz>3F";
    
    public static int DECRYPT = 1;
    public static int ENCRYPT = 0;

    static byte KS[][] = new byte[16][48];
    static byte key[] = new byte[64];
    static byte CD[] = new byte[56];

    static byte block[] = new byte[64]; /* unpacked 64-bit input/output block */

    static byte LR[] = new byte[64];
    static byte f[] = new byte[32];
    static byte preS[] = new byte[48];

    static void __unpack8(
            byte packed[], /* packed block (8 bytes at 8 bits/byte) */
            byte binary[] /* unpacked block (64 bytes at 1 bit/byte) */
    ) {
        short i, j, k;

        for (i = 0; i < 8; i++) {
            k = packed[i];
            for (j = 0; j < 8; j++)
                binary[i * 8 + j] = (byte) ((k >> (7 - j)) & 0x01);
        }
    }

    static void __pack8(
            byte packed[], /* packed block ( 8 bytes at 8 bits/byte) */
            byte binary[] /* the unpacked block (64 bytes at 1 bit/byte) */
    ) {
        byte i, j, k;

        for (i = 0; i < 8; i++) {
            k = 0;
            for (j = 0; j < 8; j++)
                k = (byte) ((k << 1) + binary[i * 8 + j]);
            packed[i] = (byte) k;
        }
    }

    // Decrypt or Encrypt data with DES algorithm
    static void __DES(int Decrypt, byte Key[], byte Data[], byte Result[]) {
        // Set decryption or encryption key parameter for DES calculation
        __DES_setkey(Decrypt, Key);

        // Decrypt or Encrypt the data and replace
        __DES_calcul(Data, Result);
    }

    // Perform a triple DES algorithm
    static void __Triple_DES(int Decrypt, byte Key[], byte Data[], byte Result[]) {
        byte Tmp1[] = new byte[16], Tmp2[] = new byte[16];

        byte key8[] = new byte[8];
        System.arraycopy(Key, 8, key8, 0, 8);

        if (Decrypt == ENCRYPT) {
            __DES(ENCRYPT, Key, Data, Tmp1);
            __DES(DECRYPT, key8, Tmp1, Tmp2);
            __DES(ENCRYPT, Key, Tmp2, Result);
        } else {
            __DES(DECRYPT, Key, Data, Tmp1);
            __DES(ENCRYPT, key8, Tmp1, Tmp2);
            __DES(DECRYPT, Key, Tmp2, Result);
        }
        return;
    }

    static void __DES_setkey(int sw, /* type cryption: 0=encrypt,1=decrypt */
            byte pkey[] /* 64-bit key packed into 8 bytes */
    ) {
        short i, j, k, t1, t2;

        /* Schedule of left shifts for C and D blocks */
        short shifts[] = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

        /* PERMUTED CHOICE 1 (PC1) */
        short PC1[] = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60,
                52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5,
                28, 20, 12, 4 };

        /* PERMUTED CHOICE 2 (PC2) */
        short PC2[] = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52,
                31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };

        /* Assume default algorymth = crypt */
        if (sw != 0 && sw != 1)
            sw = 0;

        /* Unpack KEY from 8 bits/byte into 1 bit/byte */
        __unpack8(pkey, key);

        /* Permute unpacked key with PC1 to generate C and D */
        for (i = 0; i < 56; i++)
            CD[i] = key[PC1[i] - 1];

        /* Rotate and permute C and D to generate 16 subkeys */
        for (i = 0; i < 16; i++) {
            /* Rotate C and D */
            for (j = 0; j < shifts[i]; j++) {
                t1 = CD[0];
                t2 = CD[28];
                for (k = 0; k < 27; k++) {
                    CD[k] = CD[k + 1];
                    CD[k + 28] = CD[k + 29];
                }
                CD[27] = (byte) t1;
                CD[55] = (byte) t2;
            }
            /* Set order of subkeys for type of cryption */
            j = (short) ((sw != 0) ? 15 - i : i);
            /* Permute C and D with PC2 to generate KS[j] */
            for (k = 0; k < 48; k++)
                KS[j][k] = CD[PC2[k] - 1];
        }

        return;
    }

    static byte[] __DES_calcul(
            byte in[], /* packed 64-bit INPUT block */ /*
                                                         * packed 64-bit OUTPUT
                                                         * block
                                                         */
            byte out[]) {
        short i, j, k, t;

        /* INITIAL PERMUTATION (IP) */
        short IP[] = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64,
                56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37,
                29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };

        /* REVERSE FINAL PERMUTATION (IP-1) */
        short RFP[] = { 8, 40, 16, 48, 24, 56, 32, 64, 7, 39, 15, 47, 23, 55, 31, 63, 6, 38, 14, 46, 22, 54, 30, 62, 5,
                37, 13, 45, 21, 53, 29, 61, 4, 36, 12, 44, 20, 52, 28, 60, 3, 35, 11, 43, 19, 51, 27, 59, 2, 34, 10, 42,
                18, 50, 26, 58, 1, 33, 9, 41, 17, 49, 25, 57 };

        /* E BIT-SELECTION TABLE */
        short E[] = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19,
                20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };

        /* PERMUTATION FUNCTION P */
        short P[] = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13,
                30, 6, 22, 11, 4, 25 };

        /* 8 S-BOXES */
        short S[][] = { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12,
                11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11,
                3, 14, 10, 0, 6, 13 },

                { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9,
                        11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6,
                        7, 12, 0, 5, 14, 9 },

                { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11,
                        15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15,
                        14, 3, 11, 5, 2, 12 },

                { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10,
                        14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5,
                        11, 12, 7, 2, 14 },

                { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9,
                        8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15,
                        0, 9, 10, 4, 5, 3 },

                { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11,
                        3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14,
                        1, 7, 6, 0, 8, 13 },

                { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15,
                        8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0,
                        15, 14, 2, 3, 12 },

                { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14,
                        9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12,
                        9, 0, 3, 5, 6, 11 } };

        /* Unpack the INPUT block */
        __unpack8(in, block);

        /* Permute unpacked input block with IP to generate L and R */
        for (j = 0; j < 64; j++)
            LR[j] = block[IP[j] - 1];

        /* Perform 16 rounds */
        for (i = 0; i < 16; i++) {
            /* Expand R to 48 bits with E and XOR with i-th subkey */
            for (j = 0; j < 48; j++)
                preS[j] = (byte) (LR[E[j] + 31] ^ KS[i][j]);

            /* Map 8 6-bit blocks into 8 4-bit blocks using S-Boxes */
            for (j = 0; j < 8; j++) {
                /* Compute index t into j-th S-box */
                k = (short) (6 * j);
                t = preS[k];
                t = (short) ((t << 1) | preS[k + 5]);
                t = (short) ((t << 1) | preS[k + 1]);
                t = (short) ((t << 1) | preS[k + 2]);
                t = (short) ((t << 1) | preS[k + 3]);
                t = (short) ((t << 1) | preS[k + 4]);
                /* Fetch t-th entry from j-th S-box */
                t = S[j][t];
                /* Generate 4-bit block from S-box entry */
                k = (short) (4 * j);
                f[k] = (byte) ((t >> 3) & 1);
                f[k + 1] = (byte) ((t >> 2) & 1);
                f[k + 2] = (byte) ((t >> 1) & 1);
                f[k + 3] = (byte) (t & 1);
            }

            for (j = 0; j < 32; j++) {
                /* Copy R */
                t = LR[j + 32];
                /* Permute f w/ P and XOR w/ L to generate new R */
                LR[j + 32] = (byte) (LR[j] ^ f[P[j] - 1]);
                /* Copy original R to new L */
                LR[j] = (byte) t;
            }
        }

        /* Permute L and R with reverse IP-1 to generate output block */
        for (j = 0; j < 64; j++)
            block[j] = LR[RFP[j] - 1];

        /* Pack data into 8 bits per byte */
        __pack8(out, block);
        return out;
    }

    // encrypt 3des
    static byte[] desl_Encrypt(int in_lKey, byte in_pszKey[], int in_lIn, byte in_pszIn[]) {
        int i;
        byte buf[] = new byte[8];

        /* 判断输入的密钥长度 */
        if ((8 != in_lKey) && (16 != in_lKey)) {
            return null;
        }

        byte out_pszOut[] = new byte[(int) (((in_lIn + 7)/ 8) * 8)];

        for (i = 0; i < (in_lIn / 8); i++) {
            byte tmp[] = new byte[8];
            System.arraycopy(in_pszIn, i * 8, tmp, 0, 8);
            byte tmpOut[] = new byte[8];
            /* 根据密钥长度的不同选择不同的算法 */
            if (8 == in_lKey)
                __DES(ENCRYPT, in_pszKey, tmp, tmpOut);
            else
                __Triple_DES(ENCRYPT, in_pszKey, tmp, tmpOut);

            System.arraycopy(tmpOut, 0, out_pszOut, i * 8, 8);
        }

        if ((in_lIn % 8) != 0) {
            byte tmpOut[] = new byte[8];
            System.arraycopy(in_pszIn, i * 8, buf, 0, in_lIn % 8);
            buf[in_lIn % 8] = (byte) 128;
            if (8 == in_lKey)
                __DES(ENCRYPT, in_pszKey, buf, tmpOut);
            else
                __Triple_DES(ENCRYPT, in_pszKey, buf, tmpOut);

            System.arraycopy(tmpOut, 0, out_pszOut, i * 8, 8);

            i++;
        }

        return out_pszOut;
    }
    
    static byte[] desl_Encrypt(byte in_pszKey[], byte in_pszIn[]) {
        
        int in_lKey = in_pszKey.length;
        int in_lIn = in_pszIn.length;
        
        return desl_Encrypt(in_lKey, in_pszKey, in_lIn, in_pszIn);
    }

    // decrypt 3des
    static byte[] desl_Decrypt(int in_lKey, byte in_pszKey[], int in_lIn, byte in_pszIn[]) {
        int i, nPack;

        if ((8 != in_lKey) && (16 != in_lKey)) {
            return null;
        }

        if (in_lIn % 8 != 0) {
            return null;
        }

        byte out_pszOut[] = new byte[in_lIn];
        for (i = 0; i < in_lIn / 8; i++) {
            byte tmp[] = new byte[8];
            System.arraycopy(in_pszIn, i * 8, tmp, 0, 8);
            byte tmpOut[] = new byte[8];
            if (8 == in_lKey)
                __DES(DECRYPT, in_pszKey, tmp, tmpOut);
            else
                __Triple_DES(DECRYPT, in_pszKey, tmp, tmpOut);

            System.arraycopy(tmpOut, 0, out_pszOut, i * 8, 8);
        }

        nPack = 0;

        nPack = 0;
        for (i = 1; i <= 8; i++) {
            if (out_pszOut[in_lIn - i] == (byte) 128) {
                boolean bZero = true;
                for(int j = 1;j < i;j++)
                {
                    if(out_pszOut[in_lIn - j] != 0)
                    {
                        bZero = false;
                        break;
                    }
                }
                if(bZero)
                {
                    nPack = i;
                    break;
                }
                
            }
        }
        

        byte out_pszOut2[] = new byte[in_lIn - nPack];
        System.arraycopy(out_pszOut, 0, out_pszOut2, 0, in_lIn - nPack);
        return out_pszOut2;
    }
    
    static byte[] desl_Decrypt(byte in_pszKey[], byte in_pszIn[]) {
        int in_lKey = in_pszKey.length;
        int in_lIn = in_pszIn.length;
        
        return desl_Decrypt(in_lKey, in_pszKey, in_lIn, in_pszIn);
    }
    
    public static String bytesToHexString(byte[] src){   
        StringBuilder stringBuilder = new StringBuilder("");   
        if (src == null || src.length <= 0) {   
            return null;   
        }   
        for (int i = 0; i < src.length; i++) {   
            int v = src[i] & 0xFF;   
            String hv = Integer.toHexString(v);   
            if (hv.length() < 2) {   
                stringBuilder.append(0);   
            }   
            stringBuilder.append(hv);   
        }   
        return stringBuilder.toString();   
    }   
    
    private static byte charToByte(char c) {   
        return (byte) "0123456789ABCDEF".indexOf(c);   
    }  
    
    public static byte[] hexStringToBytes(String hexString) {   
        if (hexString == null || hexString.equals("")) {   
            return null;   
        }   
        hexString = hexString.toUpperCase();   
        int length = hexString.length() / 2;   
        char[] hexChars = hexString.toCharArray();   
        byte[] d = new byte[length];   
        for (int i = 0; i < length; i++) {   
            int pos = i * 2;   
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
        }   
        return d;   
    }   
    
    //加密字符串
    public static String EncryptString3Des(String strSrc)
    {
        byte[] bRet = desl_Encrypt(ENKEY.getBytes(), strSrc.getBytes());
        
        return bytesToHexString(bRet);
    }
    
    //解密字符串
    public static String DecryptString3Des(String strSrc)
    {
        byte bEnc[] = hexStringToBytes(strSrc);
        byte[] bRet = desl_Decrypt(ENKEY.getBytes(), bEnc);
        return new String(bRet);
    }
    
    //加密文件
    public static void EncryptFile3Des(String strSrcFile, String strDestFile)
    {
      try {
          File file = new File(strSrcFile);
          FileInputStream fis = new FileInputStream(file);
          BufferedInputStream bis = new BufferedInputStream(fis);

          FileOutputStream fos = new FileOutputStream(strDestFile);
          BufferedOutputStream bos = new BufferedOutputStream(fos);

          int c = 0;
          int length = 4*1024;
          byte[] bytes = new byte[length];
          while ((c = bis.read(bytes)) != -1) {
              byte bEnc[] = desl_Encrypt(ENKEY.length(), ENKEY.getBytes(), c, bytes);
              bos.write(bEnc, 0, bEnc.length);
              
          }
          bos.flush();
          bos.close();
          fis.close();
      } catch (IOException e) {
          logger.error(e.getMessage());
      }

    }
    
    //解密文件
    public static void DecryptFile3Des(String strSrcFile, String strDestFile)
    {
        try {
              File file = new File(strSrcFile);
              FileInputStream fis = new FileInputStream(file);
              BufferedInputStream bis = new BufferedInputStream(fis);

              FileOutputStream fos = new FileOutputStream(strDestFile);
              BufferedOutputStream bos = new BufferedOutputStream(fos);

              int c = 0;
              int length = 4*1024;
              byte[] bytes = new byte[length];
              while ((c = bis.read(bytes)) != -1) {
                  byte bDec[] = desl_Decrypt(ENKEY.length(), ENKEY.getBytes(), c, bytes);
                  bos.write(bDec, 0, bDec.length);
              }
              bos.flush();
              bos.close();
              fis.close();
          } catch (IOException e) {
              logger.error(e.getMessage());
          }
    }
    
    public static void main(String[] args) {
//        // TODO Auto-generated method stub
        String strEnc = EncryptString3Des("asdfdfdasfasdfdsafdasfdsf");
        System.out.println(strEnc);
        
        String strDec = DecryptString3Des(strEnc);
        System.out.println(strDec);
        
        EncryptFile3Des("D:\\终端准入版本说明书.docx", "D:\\终端准入版本说明书1.docx");
        DecryptFile3Des("D:\\终端准入版本说明书1.docx", "D:\\终端准入版本说明书2.docx");
        //DecryptFile3Des("1234567812345678", "C:\\Users\\Administrator\\Desktop\\1.jpg.enc", "C:\\Users\\Administrator\\Desktop\\1new.jpg");
    }

}
