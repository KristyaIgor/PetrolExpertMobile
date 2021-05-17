package com.vfi.smartpos.deviceservice.aidl.key_manager;

/**
 * Created by RuoYi
 * @since >= 3.x.x
 */

interface IKLD {

    /**
     * keyStore TR34 Payload
     * @param data json file that download from VHQ, then convert to byte data.
     * @return 0 - success, other - fail
     */
    int keyStoreTR34Payload(in byte[] data);
}
