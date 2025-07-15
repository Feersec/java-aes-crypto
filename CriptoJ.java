// Pacotes para criptografia, manipulação de arquivos e segurança
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class CriptoJ {

    // Constantes para a configuração da criptografia.
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;

    public static void main(String[] args) throws Exception {
        // 1. Validação dos argumentos de entrada
        if (args.length != 4) {
            printUsage();
            return;
        }

        String mode = args[0];
        String password = args[1];
        String inputFile = args[2];
        String outputFile = args[3];

        // 2. Executa a operação de criptografar ou descriptografar
        if ("-e".equals(mode)) {
            System.out.println("Modo: Criptografar...");
            encrypt(password, inputFile, outputFile);
            System.out.println("Arquivo criptografado com sucesso em: " + outputFile);
        } else if ("-d".equals(mode)) {
            System.out.println("Modo: Descriptografar...");
            decrypt(password, inputFile, outputFile);
            System.out.println("Arquivo descriptografado com sucesso em: " + outputFile);
        } else {
            printUsage();
        }
    }
    
    // Função para imprimir as instruções de uso
    public static void printUsage() {
        System.out.println("Uso incorreto!");
        System.out.println("Para criptografar: java CriptoJ -e <senha> <arquivo_original> <arquivo_saida>");
        System.out.println("Para descriptografar: java CriptoJ -d <senha> <arquivo_criptografado> <arquivo_saida>");
    }

    // Função para gerar uma chave AES segura a partir de uma senha e um "sal"
    public static SecretKey getKeyFromPassword(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
        return secret;
    }

    // Função para criptografar um arquivo
    public static void encrypt(String password, String inputFile, String outputFile) throws Exception {
        // Gera um "sal" aleatório. Essencial para a segurança.
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        
        // Gera a chave a partir da senha e do sal
        SecretKey key = getKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // O Vetor de Inicialização (IV) é crucial para a segurança do modo CBC. Deve ser aleatório.
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        try (FileOutputStream fileOut = new FileOutputStream(outputFile);
             FileInputStream fileIn = new FileInputStream(inputFile)) {
            
            // Escrevemos o SAL e o IV no início do arquivo de saída. Precisaremos deles para descriptografar!
            fileOut.write(salt);
            fileOut.write(iv);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileIn.read(buffer)) > 0) {
                byte[] output = cipher.update(buffer, 0, len);
                if (output != null) {
                    fileOut.write(output);
                }
            }
            byte[] output = cipher.doFinal();
            if (output != null) {
                fileOut.write(output);
            }
        }
    }

    // Função para descriptografar um arquivo
    public static void decrypt(String password, String inputFile, String outputFile) throws Exception {
        try (FileInputStream fileIn = new FileInputStream(inputFile)) {
            // Lê o SAL e o Vetor de Inicialização (IV) do início do arquivo criptografado.
            byte[] salt = new byte[16];
            fileIn.read(salt);
            
            byte[] iv = new byte[16];
            fileIn.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Gera a MESMA chave usando a senha e o sal lido do arquivo
            SecretKey key = getKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fileIn.read(buffer)) > 0) {
                    byte[] output = cipher.update(buffer, 0, len);
                    if (output != null) {
                        fileOut.write(output);
                    }
                }
                byte[] output = cipher.doFinal();
                if (output != null) {
                    fileOut.write(output);
                }
            }
        }
    }
}
