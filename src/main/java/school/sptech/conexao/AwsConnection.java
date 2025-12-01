package school.sptech.conexao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

public class AwsConnection {

    private static AmazonS3 s3Client;

    static {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }


    public static InputStream getArquivo(String bucket, String chaveArquivo) {
        System.out.println("AWS S3: Buscando arquivo '" + chaveArquivo);

        try {
            S3Object objeto = s3Client.getObject(bucket, chaveArquivo);
            return objeto.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException("ERRO S3: " +e);
        }
    }
}