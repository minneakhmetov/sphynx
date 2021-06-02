package com.razzzil.sphynx.coordinator.app;

import com.razzzil.sphynx.commons.model.database.configs.PostgreSQLConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.PostgreSQLIteration;
import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.configs.PostgreSQLTestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.worker.WorkerConfigurationModel;
import com.razzzil.sphynx.commons.util.KeyUtil;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.core.Tuple;
import org.postgresql.ds.PGSimpleDataSource;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

public class TestClass {

    private static final String TEST_STRING = "VKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BVVKNFOIVNDOIVNREOJNV98BNVUE9BNV98ENB9EB4BV98W4B984V98BWS98EWABV98AB498VABV98AB8BA98B984B84VB84VB98AW4BV98BV";

    private static final String TRANSFORMATION = "AES";
    private static final int KEY_SIZE = 256;

    @SneakyThrows
    public static void main(String[] args) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setPortNumber(5432);
        dataSource.setDatabaseName("postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setServerName("localhost");
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.execute("select * from test");
        connection.commit();
        ResultSet resultSet = statement.getResultSet();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        final int columnCount = resultSetMetaData.getColumnCount();
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject(true);
          //  Object[] values = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
             //   System.out.println(resultSetMetaData.getColumnName(i) + " " + resultSet.getObject(i));
                Object value = resultSet.getObject(i);
                jsonObject.put(resultSetMetaData.getColumnName(i), Objects.nonNull(value) ? value : JSONObject.NULL);
            }
            jsonArray.put(jsonObject);
        }
        System.out.println(jsonArray.toString());
        int i = 0;
    }

    @SneakyThrows
    public static void main2(String[] args) {

        String cipherText = "LoWz1DECc32y7sUoc7GKG2qBkEtm691xxyIa/iMyIXSb/sBm7vtQTWaP98vmfa8FR+FXQ+nIyMYrHpoKzE6hLdCKjGaF9Aty2Q/OevZvAYUxXu/ppYX9UdOV5oZKK7muFSqOqelX/bGfauvUo8TYCBAdm/wwBF6rlX9JxbmQ/FpidZ8xQHZk3HgX1LANckNkQNg8FPT1CBBRZCd2zlsypQ==";
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        String stringKey = "3dXvXKM6iibnl6M6PcOJu0+D6h4CmjS6tGFIe9IZHP0=";
        byte[] bytes = Base64.getDecoder().decode(stringKey);
        SecretKey key = new SecretKeySpec(bytes, 0, bytes.length, TRANSFORMATION);

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));

        String cipheredText = new String(plainText);
        int i = 0;

    }


    @SneakyThrows
    public static void main1(String[] args) {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        byte[] encodedKey = key.getEncoded();

        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        String encoded = encrypt(TEST_STRING, originalKey);

        String decrypted = decrypt(encoded, originalKey);

        int i = 0;
    }

    public static String encrypt(String input, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }


//    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
//        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
//    }
}
