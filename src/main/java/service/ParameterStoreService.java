// TODO - Reminder that you'll need to set everything up to pull the secret values and AWS keys from AWS itself rather than your local config (https://serverfault.com/questions/1008123/how-to-decrypt-secure-string-values-in-parameter-store-using-net-cores-amazons)
package service;

// import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

public class ParameterStoreService {

  public String getParaValue(String paraName) {
    SsmClient ssmClient = SsmClient.builder()
      .build();
      
    try {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
            .name(paraName)
            .withDecryption(true)
            .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        ssmClient.close();
        return parameterResponse.parameter().value();

    } catch (SsmException e) {
      return e.getMessage();
    }
  }
  
}
