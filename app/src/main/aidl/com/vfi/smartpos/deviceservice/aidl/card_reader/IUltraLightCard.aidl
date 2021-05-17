// IUltraLightCard.aidl
package com.vfi.smartpos.deviceservice.aidl.card_reader;

interface IUltraLightCard {
    /**
     * execute write command
     * @param bAddress address of write
     * @param pData data need to write (16Byte)
     * @return 0:success other:fail
     * @since 3.6.1.rc0100
     */
    int	compatibilityWrite(byte bAddress, in byte[] pData);

    /**
     * @param bStartAddr start address of read data
     * @param bEndAddr end address of read data
     * @return data,
     * @since 3.6.1.rc0100
     */
    byte[] fastRead(byte bStartAddr, byte bEndAddr);

    /**
     * @param pData 64 byte data need to be write, addr start 0XF0 to 0XFF.
     * @return 0:success other:fail
     * @since 3.6.1.rc0100
     */
    int	fastWrite(in byte[] pData);

    /**
     * @return get version
     * @since 3.6.1.rc0100
     */
    String getVersion();

    /**
     *
     * @param bCntNum count number
     * @param pCnt 4byte data of count number, low first.
     * @return 0:success other:fail
     * @since 3.6.1.rc0100
     */
    int	incrCnt(byte bCntNum, in byte[] pCnt);

    /**
     * init UltraLight card
     * @return 0:success other:fail
     * @since 3.6.1.rc0100
     */
    int	init();

    /**
     * Ultralight NANO Lock Signature command
     * @param bLockMode :
     *      0-Option to unlock the signature;
     *      1-Option to temporary lock the signature;
     *      2-Option to permanently lock the signature;
     * @return 0:success other:fail
     * @since 3.6.1.rc0100
     */
    int	lockSign(byte bLockMode);

    /**
     * @param pPwd 4bytes PW data
     * @return 2bytes confirm data
     * @since 3.6.1.rc0100
     */
    byte[] pwdAuth(in byte[] pPwd);

    /**
     * @param bAddress
     * @return 16 bytes data of address, other is fail
     * @since 3.6.1.rc0100
     */
    byte[] read(byte bAddress);
    /**
     * @param bCntNum counter number
     * @return 3 bytes data of counter,low first, other is fail
     * @since 3.6.1.rc0100
     */
    byte[] readCnt(byte bCntNum);

    /**
     *
     * @param bAddr addr is always 0x00
     * @return 32 bytes data, other is fail
     * @since 3.6.1.rc0100
     */
    byte[] readSign(byte bAddr);

    /**
     *
     * @param bSecNo sector No.
     * @return 0-success other-fail
     * @since 3.6.1.rc0100
     */
    int	sectorSelect(byte bSecNo);

    /**
     *
     * @param pbKeyVal 16 bytes key data
     * @return 0-success other-fail
     * @since 3.6.1.rc0100
     */
    int	ulcAuthenticate(in byte[] pbKeyVal);

    /**
     * Ultralight NANO Virtual Card Select Command
     * @param pVCIIDbyte data use to select VC
     * @param bVCIIDLen data of length
     * @return 0-success other-fail
     * @since 3.6.1.rc0100
     */
    int virtualCardSelect(in byte[] pVCIIDbyte, byte bVCIIDLen);

    /**
     * @param bAddress address to write in
     * @param pData 8 bytes data
     * @return 0-success other-fail
     * @since 3.6.1.rc0100
     */
    int	write(byte bAddress, in byte[] pData);

    /**
     * Ultralight NANO Write signature command
     * @param bAddress address of need to write signature
     * @param pData 4 bytes data
     * @return 0-success other-fail
     * @since 3.6.1.rc0100
     */
    int	writeSign(byte bAddress, in byte[] pData);
}