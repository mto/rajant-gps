package com.mto.rajant;

import com.rajant.bcapi.protos.BCAPIProtos;

/**
* @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
* @date: 2/20/17
*/
public interface Callback {

    void massageAuthResponse(BCAPIProtos.BCMessage.Builder msg);

    void messageReceived(Session session, BCAPIProtos.BCMessage msg);
}
