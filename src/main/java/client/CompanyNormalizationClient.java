/*
package client;

import com.lieluobo.norm.Company;
import com.lieluobo.norm.CompanyNormalizeServiceGrpc;

import java.util.concurrent.TimeUnit;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


*/
/**
 * Created by Jerry You on 2018/3/30.
 *//*

public class CompanyNormalizationClient {
  private static Logger logger = LoggerFactory.getLogger(CompanyNormalizationClient.class);
  private final ManagedChannel channel;
  private final CompanyNormalizeServiceGrpc.CompanyNormalizeServiceBlockingStub blockingStub;

  public CompanyNormalizationClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
  }

  public CompanyNormalizationClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = CompanyNormalizeServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public Company.CompanyNormReply normalize(Company.CompanyNormRequest req) {
    Company.CompanyNormReply rep = null;
    try {
      rep = blockingStub.normalize(req);
    } catch (StatusRuntimeException e) {
      logger.error("CompanyNormalize rpc failed: {0}", e.getStatus());
    }
    return rep;
  }

  public static void StartMain(String[] args) throws Exception {
    CompanyNormalizationClient client = new CompanyNormalizationClient("172.20.0.81", 26543);
    try {
      Company.CompanyNormReply rep = client.normalize(Company.CompanyNormRequest.newBuilder()
          .addNames(args[0]).build());
      Company.CompanyNormReply.NormedItem normalItem = rep.getItems(0);
      String originTitle = normalItem.getOrigin();
      String normed = "";
      int normalLength = normalItem.getNormedCount();
      if (normalLength > 0) {
        normed = normalItem.getNormed(0);
      }
      String originCoreInd = normalItem.getOriginCoreInd();
      String originLocCoreInd = normalItem.getOriginLocCoreInd();
      System.out.println(originTitle + " , " + normed + " , " + originCoreInd + " , " + originLocCoreInd);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.shutdown();
    }
  }
}
*/
