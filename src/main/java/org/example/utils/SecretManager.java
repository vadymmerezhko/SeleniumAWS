package org.example.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * AWS secret manager class.
 */
public class SecretManager {

    /**
     * Returns secret valur by its name.
     * @param secretName The secret name.
     * @return The secret value.
     */
    public static String getSecret(String secretName) {
        Region region = Region.US_WEST_1;
        System.setProperty("AWS_ACCESS_KEY_ID", "AKIAXZRTVXLO42XFAHH6");
        System.setProperty("AWS_SECRET_ACCESS_KEY", "AWS_EC2_Acees_Key");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String name = getSecretValueResponse.name();
            String secret = getSecretValueResponse.secretString();
            return secret;
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }
    }
}
