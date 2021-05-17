package com.vfi.smartpos.deviceservice.aidl;

import com.vfi.smartpos.deviceservice.aidl.IBeeper;
import com.vfi.smartpos.deviceservice.aidl.IDeviceInfo;
import com.vfi.smartpos.deviceservice.aidl.ILed;
import com.vfi.smartpos.deviceservice.aidl.IMagCardReader;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;
import com.vfi.smartpos.deviceservice.aidl.IInsertCardReader;
import com.vfi.smartpos.deviceservice.aidl.IPBOC;
import com.vfi.smartpos.deviceservice.aidl.IPinpad;
import com.vfi.smartpos.deviceservice.aidl.IPrinter;
import com.vfi.smartpos.deviceservice.aidl.IRFCardReader;
import com.vfi.smartpos.deviceservice.aidl.IScanner;
import com.vfi.smartpos.deviceservice.aidl.ISerialPort;
import com.vfi.smartpos.deviceservice.aidl.IExternalSerialPort;
import com.vfi.smartpos.deviceservice.aidl.IUsbSerialPort;
import com.vfi.smartpos.deviceservice.aidl.ISmartCardReader;
import com.vfi.smartpos.deviceservice.aidl.IEMV;
import com.vfi.smartpos.deviceservice.aidl.key_manager.IDukpt;
import com.vfi.smartpos.deviceservice.aidl.card_reader.IFelica;
import com.vfi.smartpos.deviceservice.aidl.utils.IUtils;
import com.vfi.smartpos.deviceservice.aidl.ISmartCardReaderEx;
import com.vfi.smartpos.deviceservice.aidl.card_reader.INtagCard;
import com.vfi.smartpos.deviceservice.aidl.key_manager.IKLD;
import com.vfi.smartpos.deviceservice.aidl.card_reader.IICodeCard;
import com.vfi.smartpos.deviceservice.aidl.card_reader.IUltraLightCard;

/**
 * <p> Device service, get each service interface (object) in this interface
 */
interface IDeviceService {
    /**
     * <p> get the IBeeper interface object for Beeper.
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IBeeper}
     * @since 1.x.x
     */
    IBeeper getBeeper();
    
    /**
     * <p> get the ILed interface object for Led.
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.ILed}
     * @since 1.x.x
     */
    ILed getLed();
    
    /**
     * <p> get the ISerialPort interface object for Serial Port.
     *
     * @param {@code String} deviceType
     * <p> the key of deviceType param is as follow:
     * <ul>
     *   <li>"rs232"(description:the port via build in serial chip micro USB cable (one side is micro USB connect to terminal, another side is 9 pin interface connect to COM port in PC.))</li>
     *   <li>"usb-rs232"(description:the port via micro USB cable）</li>
     *   <li>set VID (and PID) directly or only VID, such as "usb2rs232-11CA-0204", "usb2rs232-11CA".
     *   <li>"usb2rs232-VF-RS232"(same as "usb2rs232-11CA-0204"), (description:definied in @{link com.verifone.smartpos.devicemanager.util.SerialPortChart.VF_RS232})</li>
     *   <li>"usb2rs232-VF-V34Modem"(same as "usb2rs232-11CA-0205"), (description:definied in @{link com.verifone.smartpos.devicemanager.util.SerialPortChart.VF_V34Modem})</li>
     *   <li>"usb2rs232-VF-C680DongleModem"(same as "usb2rs232-11CA-0240"), (description:definied in @{link com.verifone.smartpos.devicemanager.util.SerialPortChart.VF_C680DongleModem})</li>
     *   <li>"usb2rs232-Z-TEK"(same as "usb2rs232-0403-6001"), (description:definied in @{link com.verifone.smartpos.devicemanager.util.SerialPortChart.Z_TEK})</li>
     * </ul>
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.ISerialPort}
     * @since 1.x.x
     */
    ISerialPort getSerialPort(String deviceType);
    
    /**
     * <p> get the IScanner interface object for scanner
     *
     * @param cameraId 1:set front scanner, 0:set rear scanner
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IScanner}
     * @since 1.x.x
     */
    IScanner getScanner(int cameraId);

    /**
     * <p> get the IMagCardReader interface object for magnetic card reader
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IMagCardReader}
     * @since 1.x.x
     */
    IMagCardReader getMagCardReader();
    
    /**
     * <p> get the IInsertCardReader interface object for smart card and PSAM card
     *
     * @param slotNo slotNo value as follow:
     * <ul>
     *     <li>{@code 0}:IC card slot</li>
     *     <li>{@code 1}:SAM1 card slot</li>
     *     <li>{@code 2}:SAM2 card slot</li>
     * </ul>
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IInsertCardReader}
     * @since 1.x.x
     */
    IInsertCardReader getInsertCardReader(int slotNo);
    
    /**
     * <p> get the IRFCardReader interface object for CTLS card
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IRFCardReader}
     * @since 1.x.x
     */
    IRFCardReader getRFCardReader();
    
    /**
     * <p>kapId get IPinpad interface object for Pinpad
     *
     * @param kapId : the index refer the keys set
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IPinpad}
     * @since 1.x.x
     */
    IPinpad getPinpad(int kapId);
    
    /**
     * <p> get IPrinter interface object for printer
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IPrinter}
     * @since 1.x.x
     */
    IPrinter getPrinter();
    
    /**
     * <p> get IPBOC interface object for PBOC or EMV
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IPBOC}
     * @since 1.x.x
     * @deprecated  please use IEMV to instead, {@see com.vfi.smartpos.deviceservice.aidl.IEMV}
     */
    IPBOC getPBOC();
    
    /**
     * <p> get IDeviceInfo interface object for device information
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IDeviceInfo}
     * @since 1.x.x
     */
    IDeviceInfo getDeviceInfo();

     /**
     * <p>  get IExternalSerialPort interface object for external serial port
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IExternalSerialPort}
     * @since 1.x.x
     */
    IExternalSerialPort getExternalSerialPort();

    /**
     * <p> get the usb-serial device(such as X9, C520H) connect with OTG cable
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IUsbSerialPort}
     * @since 1.x.x
     */
    IUsbSerialPort getUsbSerialPort();

    /**
     * <p>slotNo slotNo value as follow:
     * <ul>
     *     <li>{@code 0}:IC card slot</li>
     *     <li>{@code 1}:SAM1 card slot</li>
     *     <li>{@code 2}:SAM2 card slot</li>
     * </ul>
     * @return {@link com.vfi.smartpos.deviceservice.aidl.ISmartCardReader}
     * @since 1.x.x
     */
    ISmartCardReader getSmartCardReader(int slotNo);

    /**
     * <p> get IEMV interface object.
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IEMV}
     * @since 2.0.0
     */
    IEMV getEMV();

    /**
     * <p> get IDukpt interface object.
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IDukpt}
     * @since 2.0.0
     */
    IDukpt getDUKPT();

    /**
     * <p> get IFelica interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IFelica}
     * @since 2.1.23.1
     */
    IFelica getFelica();

    /**
     * <p> get IUtils interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IUtils}
     * @since 2.4.3.1
     */
    IUtils getUtils();

    /**
     * <p> get ISmartCardReaderEx interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.ISmartCardReaderEx}
     * @since 3.4.2.rc3
     */
    ISmartCardReaderEx getSmartCardReaderEx();

    /**
     * <p> get IKLD interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IKLD}
     * @since 3.3.2.rc3
     */
    IKLD getIKLD();

    /**
     * <p> get INtagCard interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.INtagCard}
     * @since 3.2.2.rc3
     */
    INtagCard getNtag();

    /**
     * <p> get ICode interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IICodeCard}
     * @since 3.6.0
     */
    IICodeCard getICode();

    /**
     * <p> get UltraLightCard interface object
     *
     * @return {@link com.vfi.smartpos.deviceservice.aidl.IUltraLightCard}
     * @since 3.6.1.rc0100
     */
    IUltraLightCard getUtrlLightManager();
}
