package club.nsdn.nyasamatelecom.network.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * Created by drzzm32 on 2018.12.13.
 */
@WebService
public interface ITelecom {

    @WebMethod
    @WebResult(name = "fromDevice")
    String fromDevice(
            @WebParam(name = "token") String token,
            @WebParam(name = "id") String id,
            @WebParam(name = "key") String key
    );

    @WebMethod
    @WebResult(name = "toDevice")
    String toDevice(
            @WebParam(name = "token") String token,
            @WebParam(name = "id") String id,
            @WebParam(name = "key") String key,
            @WebParam(name = "state") boolean state
    );

    @WebMethod
    @WebResult(name = "toDeviceExt")
    String toDeviceExt(
            @WebParam(name = "token") String token,
            @WebParam(name = "id") String id,
            @WebParam(name = "key") String key,
            @WebParam(name = "data") Object data
    );

    @WebMethod
    @WebResult(name = "requestToken")
    String requestToken();

    @WebMethod
    @WebResult(name = "ping")
    String ping(@WebParam(name = "token") String token);

    static void publish(ITelecom service, String url) {
        Endpoint.publish(url, service);
    }

}
