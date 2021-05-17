package md.intelectsoft.petrolexpert.emvcardreader.parser;

import android.os.RemoteException;
import android.util.Log;

import com.vfi.smartpos.deviceservice.aidl.IRFCardReader;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import md.intelectsoft.petrolexpert.emvcardreader.enums.CommandEnum;
import md.intelectsoft.petrolexpert.emvcardreader.enums.EmvCardScheme;
import md.intelectsoft.petrolexpert.emvcardreader.enums.SwEnum;
import md.intelectsoft.petrolexpert.emvcardreader.exception.CommunicationException;
import md.intelectsoft.petrolexpert.emvcardreader.iso7816emv.EmvTags;
import md.intelectsoft.petrolexpert.emvcardreader.iso7816emv.EmvTerminal;
import md.intelectsoft.petrolexpert.emvcardreader.iso7816emv.TLV;
import md.intelectsoft.petrolexpert.emvcardreader.iso7816emv.TagAndLength;
import md.intelectsoft.petrolexpert.emvcardreader.model.Afl;
import md.intelectsoft.petrolexpert.emvcardreader.model.EmvCard;
import md.intelectsoft.petrolexpert.emvcardreader.model.EmvTransactionRecord;
import md.intelectsoft.petrolexpert.emvcardreader.model.enums.CurrencyEnum;
import md.intelectsoft.petrolexpert.emvcardreader.utils.BytesUtils;
import md.intelectsoft.petrolexpert.emvcardreader.utils.CommandApdu;
import md.intelectsoft.petrolexpert.emvcardreader.utils.ResponseUtils;
import md.intelectsoft.petrolexpert.emvcardreader.utils.TlvUtil;
import md.intelectsoft.petrolexpert.emvcardreader.utils.TrackUtils;


/**
 * Emv Parser.<br/>
 * Class used to read and parse EMV card
 */
public class EmvParser {


	/**
	 * PPSE directory "2PAY.SYS.DDF01"
	 */
	private static final byte[] PPSE = "2PAY.SYS.DDF01".getBytes();

	/**
	 * PSE directory "1PAY.SYS.DDF01"
	 */
	private static final byte[] PSE = "1PAY.SYS.DDF01".getBytes();

	/**
	 * Unknow response
	 */
	public static final int UNKNOW = -1;

	/**
	 * Card holder name separator
	 */
	public static final String CARD_HOLDER_NAME_SEPARATOR = "/";

	/**
	 * Provider
	 */
	private IProvider provider;

	/**
	 * use contact less mode
	 */
	private boolean contactLess;

	/**
	 * Card data
	 */
	private EmvCard card;

	IRFCardReader irfCardReader;

	/**
	 * Constructor
	 *
	 * @param pProvider
	 *            provider to launch command
	 * @param pContactLess
	 *            boolean to indicate if the EMV card is contact less or not
	 */
	public EmvParser(final IProvider pProvider, final boolean pContactLess) {
		provider = pProvider;
		contactLess = pContactLess;
		card = new EmvCard();
	}

	public EmvParser(IRFCardReader irfCardReader) {
		this.irfCardReader = irfCardReader;
	}

	/**
	 * Method used to read public data from EMV card
	 *
	 * @return data read from card or null if any provider match the card type
	 */
	public EmvCard readEmvCard() throws RemoteException {
		// use PSE first
//		if (!readWithPSE()) {
//
//		}

		// Find with AID
		readWithAID();

		return card;
	}

	/**
	 * Method used to select payment environment PSE or PPSE
	 *
	 * @return response byte array
	 * @throws CommunicationException
	 */
	protected byte[] selectPaymentEnvironment() throws RemoteException {
		Log.e("PetrolExpert_BaseApp", "selectPaymentEnvironment: Select " + (contactLess ? "PPSE" : "PSE") + " Application");
		// Select the PPSE or PSE directory
		CommandApdu apduSelect = new CommandApdu(CommandEnum.SELECT, contactLess ? PPSE : PSE, 0);

		Log.e("PetrolExpert_BaseApp", "selectPaymentEnvironment: Select Payment Environment , command SELECT: " + BytesUtils.bytesToString(apduSelect.toBytes()) + " data: " + (contactLess ? BytesUtils.bytesToString(PPSE) : BytesUtils.bytesToString(PSE)));


		byte[] response = irfCardReader.exchangeApdu(apduSelect.toBytes());

		Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(response));

		return response;
	}

	/**
	 * Method used to get the number of pin try left
	 *
	 * @return the number of pin try left
	 * @throws CommunicationException
	 */
	protected int getLeftPinTry() throws RemoteException {
		int ret = UNKNOW;

		Log.e("PetrolExpert_BaseApp", "Get Left PIN try");

		// Left PIN try command
		byte[] data = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.GET_DATA, 0x9F, 0x17, 0).toBytes());

		Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));

		if (ResponseUtils.isSucceed(data)) {
			// Extract PIN try counter
			byte[] val = TlvUtil.getValue(data, EmvTags.PIN_TRY_COUNTER);
			if (val != null) {
				ret = BytesUtils.byteArrayToInt(val);
			}
		}
		return ret;
	}

	/**
	 * Method used to parse FCI Proprietary Template
	 *
	 * @param pData
	 *            data to parse
	 * @return
	 * @throws CommunicationException
	 */
	protected byte[] parseFCIProprietaryTemplate(final byte[] pData) throws RemoteException {
		// GetEmvParserSFI
		byte[] data = TlvUtil.getValue(pData, EmvTags.SFI);

		// Check SFI
		if (data != null) {
			int sfi = BytesUtils.byteArrayToInt(data);

			Log.e("PetrolExpert_BaseApp", "parseFCIProprietaryTemplate: SFI found:" + sfi);

//			data = provider.transceive(new CommandApdu(CommandEnum.READ_RECORD, sfi, sfi << 3 | 4, 0).toBytes());
			data = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.READ_RECORD, sfi, sfi << 3 | 4, 0).toBytes());
			Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));
			// If LE is not correct
			if (ResponseUtils.isEquals(data, SwEnum.SW_6C)) {
				data = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.READ_RECORD, sfi, sfi << 3 | 4, data[data.length - 1]).toBytes());
				Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));
			}
			return data;
		}

		Log.e("PetrolExpert_BaseApp","(FCI) Issuer Discretionary Data is already present");

		return pData;
	}


	/**
	 * Method used to extract application label
	 *
	 * @return decoded application label or null
	 */
	protected String extractApplicationLabel(final byte[] pData) {

			Log.e("PetrolExpert_BaseApp", "extractApplicationLabel: Extract Application label");

		String label = getApplicationTemplate(pData);

		byte[] labelByte = TlvUtil.getValue(pData, EmvTags.APPLICATION_LABEL);
		if (labelByte != null) {
			label = new String(labelByte);
		}

		return label;
	}

	protected String getApplicationTemplate(final byte[] pData) {
		String label = null;
		// Search FCI_PROPRIETARY_TEMPLATE
		List<TLV> listTlv = TlvUtil.getlistTLV(pData, EmvTags.FCI_PROPRIETARY_TEMPLATE);
		// For each FCI_PROPRIETARY_TEMPLATE
		for (TLV tlv : listTlv) {
			//Search File Control Information (FCI) Issuer Discretionary Data
			List<TLV> listTlvFCI = TlvUtil.getlistTLV(tlv.getValueBytes(), EmvTags.FCI_ISSUER_DISCRETIONARY_DATA);
			List<TLV> listTlvLbl = TlvUtil.getlistTLV(tlv.getValueBytes(), EmvTags.APPLICATION_LABEL);
			// For each File Control Information (FCI) Issuer Discretionary Data
			for(TLV item : listTlvFCI){
				//Search Application template
				List<TLV> listTlvAT = TlvUtil.getlistTLV(item.getValueBytes(), EmvTags.APPLICATION_TEMPLATE);
				//for each application template
				for (TLV ap:listTlvAT){
					// Get AID, Kernel_Identifier and application label
					List<TLV> listTlvData = TlvUtil.getlistTLV(item.getValueBytes(), EmvTags.AID_CARD, EmvTags.APPLICATION_LABEL, EmvTags.APPLICATION_PRIORITY_INDICATOR);
					// For each data
					for (TLV data : listTlvData) {
						if (data.getTag() == EmvTags.APPLICATION_LABEL) {
							label = new String(data.getValueBytes());
						}
					}
				}
			}
		}
		return label;
	}

	/**
	 * Read EMV card with Payment System Environment or Proximity Payment System
	 * Environment
	 *
	 * @return true is succeed false otherwise
	 */
	protected boolean readWithPSE() throws RemoteException {
		boolean ret = false;

		Log.e("PetrolExpert_BaseApp","Try to read card with Payment System Environment");

		// Select the PPSE or PSE directory
		byte[] data = selectPaymentEnvironment();
		if (ResponseUtils.isSucceed(data)) {
			// Parse FCI Template

			Log.e("PetrolExpert_BaseApp","parse FCI ProprietaryTemplate");

			Log.e("PetrolExpert_BaseApp","Get Aids");
			List<byte[]> aids = getAids(data);
			for (byte[] aid : aids) {
				String label = extractApplicationLabel(data);
				ret = extractPublicData(aid, label);
				if (ret == true) {
					break;
				}
			}
			if (!ret) {
				card.setNfcLocked(true);
			}


//			data = parseFCIProprietaryTemplate(data);
//			// Extract application label
//			if (ResponseUtils.isSucceed(data)) {
//				// Get Aids
//
//			}
//

		} else
			Log.e("PetrolExpert_BaseApp", "readWithPSE: " + (contactLess ? "PPSE" : "PSE") + " not found -> Use kown AID");


		return ret;
	}

	/**
	 * Method used to get the aid list, if the Kernel Identifier is defined, <br/>
	 * this value need to be appended to the ADF Name in the data field of <br/>
	 * the SELECT command.
	 *
	 * @param pData
	 *            FCI proprietary template data
	 * @return the Aid to select
	 */
	protected List<byte[]> getAids(final byte[] pData) {
		List<byte[]> ret = new ArrayList<byte[]>();
		Log.d("PetrolExpert_BaseApp", "getAids: " + EmvTags.AID_CARD + " kernel: " + EmvTags.KERNEL_IDENTIFIER  );
		List<TLV> listTlv = TlvUtil.getlistTLV(pData, EmvTags.AID_CARD, EmvTags.KERNEL_IDENTIFIER);
		for (TLV tlv : listTlv) {
			if (tlv.getTag() == EmvTags.KERNEL_IDENTIFIER && ret.size() != 0) {
				ret.add(ArrayUtils.addAll(ret.get(ret.size() - 1), tlv.getValueBytes()));
			} else {
				ret.add(tlv.getValueBytes());
			}
		}
		return ret;
	}

	/**
	 * Read EMV card with AID
	 */
	protected void readWithAID() throws RemoteException {
		// Test each card from know EMV AID
		EmvCardScheme[] schemes = EmvCardScheme.values();
		for (EmvCardScheme type : EmvCardScheme.values()) {
			Log.d("PetrolExpert_BaseApp", "readWithAID for: " + type.getName());
			for (byte[] aid : type.getAidByte()) {
				if (extractPublicData(aid, type.getName())) {
					return;
				}
			}
		}
	}

	/**
	 * Select application with AID or RID
	 *
	 * @param pAid
	 *            byte array containing AID or RID
	 * @return response byte array
	 * @throws CommunicationException
	 */
	protected byte[] selectAID(final byte[] pAid) throws RemoteException {

		CommandApdu apd = new CommandApdu(CommandEnum.SELECT, pAid, 0);
		byte[] apdByte = apd.toBytes();
		byte[] data = irfCardReader.exchangeApdu(apdByte);
		Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));
		return data;
	}

	/**
	 * Read public card data from parameter AID
	 *
	 * @param pAid
	 *            card AID in bytes
	 * @param pApplicationLabel
	 *            application scheme (Application label)
	 * @return true if succeed false otherwise
	 */
	protected boolean extractPublicData(final byte[] pAid, final String pApplicationLabel) throws RemoteException {
		boolean ret = false;
		String appLabel = pApplicationLabel;
		// Select AID
		byte[] data = selectAID(pAid);
		// check response
		if (ResponseUtils.isSucceed(data)) {

			byte[] labelByte = TlvUtil.getValue(data, EmvTags.APPLICATION_LABEL);
			if (labelByte != null) {
				appLabel = new String(labelByte);
			}
			// Parse select response
			ret = parse(data, provider);
			if (ret) {
				// Get AID
				String aid = BytesUtils.bytesToStringNoSpace(TlvUtil.getValue(data, EmvTags.DEDICATED_FILE_NAME));

				card.setAid(aid);
//				card.setType(findCardScheme(aid, card.getCardNumber()));
				card.setApplicationLabel(appLabel);
				card.setLeftPinTry(getLeftPinTry());
			}
		}
		return ret;
	}

	/**
	 * Method used to find the real card scheme
	 *
	 * @param pAid
	 *            card complete AID
	 * @param pCardNumber
	 *            card number
	 * @return card scheme
	 */
//	protected EmvCardScheme findCardScheme(final String pAid, final String pCardNumber) {
//		EmvCardScheme type = EmvCardScheme.getCardTypeByAid(pAid);
//		// Get real type for french card
//		if (type == EmvCardScheme.CB) {
//			type = EmvCardScheme.getCardTypeByCardNumber(pCardNumber);
//			if (type != null) {
//				Log.e("PetrolExpert_BaseApp", "findCardScheme: Real type:" + type.getName());
//			}
//		}
//		return type;
//	}

	/**
	 * Method used to extract Log Entry from Select response
	 *
	 * @param pSelectResponse
	 *            select response
	 * @return byte array
	 */
	protected byte[] getLogEntry(final byte[] pSelectResponse) {
		return TlvUtil.getValue(pSelectResponse, EmvTags.LOG_ENTRY, EmvTags.VISA_LOG_ENTRY);
	}

	/**
	 * Method used to parse EMV card
	 */
	protected boolean parse(final byte[] pSelectResponse, final IProvider pProvider) throws RemoteException {
		boolean ret = false;
		// Get TLV log entry
//		byte[] logEntry = getLogEntry(pSelectResponse);

		// Get PDOL
		byte[] pdol = TlvUtil.getValue(pSelectResponse, EmvTags.PDOL);
		// Send GPO Command
		byte[] gpo = getGetProcessingOptions(pdol, pProvider);

		// Check empty PDOL
		if (!ResponseUtils.isSucceed(gpo)) {
			gpo = getGetProcessingOptions(null, pProvider);
			// Check response
			if (!ResponseUtils.isSucceed(gpo)) {
				return false;
			}
		}

		// Extract commons card data (number, expire date, ...)
		if (extractCommonsCardData(gpo)) {

			// Extract log entry
//			card.setListTransactions(extractLogEntry(logEntry));
			ret = true;
		}

		return ret;
	}

	/**
	 * Method used to extract commons card data
	 *
	 * @param pGpo
	 *            global processing options response
	 */
	protected boolean extractCommonsCardData(final byte[] pGpo) throws RemoteException {
		boolean ret = false;
		// Extract data from Message Template 1
		byte data[] = TlvUtil.getValue(pGpo, EmvTags.RESPONSE_MESSAGE_TEMPLATE_1);
		if (data != null) {
			data = ArrayUtils.subarray(data, 2, data.length);
		} else { // Extract AFL data from Message template 2
			ret = TrackUtils.extractTrack2Data(card, pGpo);
			if (!ret) {
				data = TlvUtil.getValue(pGpo, EmvTags.APPLICATION_FILE_LOCATOR);
			} else {
				extractCardHolderName(pGpo);
			}
		}

		if (data != null) {
			// Extract Afl
			List<Afl> listAfl = extractAfl(data);
			// for each AFL
			for (Afl afl : listAfl) {
				// check all records
				for (int index = afl.getFirstRecord(); index <= afl.getLastRecord(); index++) {
					byte[] info = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.READ_RECORD, index, afl.getSfi() << 3 | 4, 0).toBytes());
					Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(info));

					if (ResponseUtils.isEquals(info, SwEnum.SW_6C)) {
						info = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.READ_RECORD, index, afl.getSfi() << 3 | 4,
								info[info.length - 1]).toBytes());
						Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(info));
					}

					// Extract card data
					if (ResponseUtils.isSucceed(info)) {
						String name = extractCardHolderName(info);
						Log.e("PetrolExpert_BaseApp", "extractCommonsCardData: " + name );
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Method used to get log format
	 *
	 * @return list of tag and length for the log format
	 * @throws CommunicationException
	 */
	protected List<TagAndLength> getLogFormat() throws RemoteException {
		List<TagAndLength> ret = new ArrayList<TagAndLength>();

		// Get log format
		byte[] data = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.GET_DATA, 0x9F, 0x4F, 0).toBytes());
		Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));
		if (ResponseUtils.isSucceed(data)) {
			ret = TlvUtil.parseTagAndLength(TlvUtil.getValue(data, EmvTags.LOG_FORMAT));
		}
		return ret;
	}

	/**
	 * Method used to extract log entry from card
	 *
	 * @param pLogEntry
	 *            log entry position
	 */
	protected List<EmvTransactionRecord> extractLogEntry(final byte[] pLogEntry) throws RemoteException {
		List<EmvTransactionRecord> listRecord = new ArrayList<EmvTransactionRecord>();
		// If log entry is defined
		if (pLogEntry != null) {
			List<TagAndLength> tals = getLogFormat();
			// read all records
			for (int rec = 1; rec <= pLogEntry[1]; rec++) {
				byte[] response = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.READ_RECORD, rec, pLogEntry[0] << 3 | 4, 0).toBytes());
				Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(response));
				// Extract data
				if (ResponseUtils.isSucceed(response)) {
					EmvTransactionRecord record = new EmvTransactionRecord();
					record.parse(response, tals);

					// Fix artifact in EMV VISA card
					if (record.getAmount() >= 1500000000) {
						record.setAmount(record.getAmount() - 1500000000);
					}

					// Skip transaction with nul amount
					if (record.getAmount() == null || record.getAmount() == 0) {
						continue;
					}

					if (record != null) {
						// Unknown currency
						if (record.getCurrency() == null) {
							record.setCurrency(CurrencyEnum.XXX);
						}
						listRecord.add(record);
					}
				} else {
					// No more transaction log or transaction disabled
					break;
				}
			}
		}
		return listRecord;
	}

	/**
	 * Extract list of application file locator from Afl response
	 *
	 * @param pAfl
	 *            AFL data
	 * @return list of AFL
	 */
	protected List<Afl> extractAfl(final byte[] pAfl) {
		List<Afl> list = new ArrayList<Afl>();
		ByteArrayInputStream bai = new ByteArrayInputStream(pAfl);
		while (bai.available() >= 4) {
			Afl afl = new Afl();
			afl.setSfi(bai.read() >> 3);
			afl.setFirstRecord(bai.read());
			afl.setLastRecord(bai.read());
			afl.setOfflineAuthentication(bai.read() == 1);
			list.add(afl);
		}
		return list;
	}

	/**
	 * Extract card holder lastname and firstname
	 *
	 * @param pData
	 *            card data
	 */
	protected String extractCardHolderName(final byte[] pData) {
		// Extract Card Holder name (if exist)
		byte[] cardHolderByte = TlvUtil.getValue(pData, EmvTags.CARDHOLDER_NAME);
		if (cardHolderByte != null) {
			Log.e("PetrolExpert_BaseApp", "getGetProcessingOptions: get card holder pan:" + new String(cardHolderByte).trim());
			String[] name = StringUtils.split(new String(cardHolderByte).trim(), CARD_HOLDER_NAME_SEPARATOR);
			if (name != null && name.length == 2) {
				card.setHolderFirstname(StringUtils.trimToNull(name[0]));
				card.setHolderLastname(StringUtils.trimToNull(name[1]));

				return card.getHolderFirstname() + card.getHolderLastname();
			}
		}
		return null;
	}

	/**
	 * Method used to create GPO command and execute it
	 *
	 * @param pPdol
	 *            PDOL data
	 * @param pProvider
	 *            provider
	 * @return return data
	 */
	protected byte[] getGetProcessingOptions(final byte[] pPdol, final IProvider pProvider) throws RemoteException {
		// List Tag and length from PDOL
		List<TagAndLength> list = TlvUtil.parseTagAndLength(pPdol);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(EmvTags.COMMAND_TEMPLATE.getTagBytes()); // COMMAND
			// TEMPLATE
			out.write(TlvUtil.getLength(list)); // ADD total length
			if (list != null) {
				for (TagAndLength tl : list) {
					out.write(EmvTerminal.constructValue(tl));
				}
			}
		} catch (IOException ioe) {
			Log.e("PetrolExpert_BaseApp", "getGetProcessingOptions: Construct GPO Command:" + ioe.getMessage(), ioe);
		}
		byte[] data = irfCardReader.exchangeApdu(new CommandApdu(CommandEnum.GPO, out.toByteArray(), 0).toBytes());
		Log.d("PetrolExpert_BaseApp", "Pretty Print Response APDU Command: " + TlvUtil.prettyPrintAPDUResponse(data));
		return data;
	}

	/**
	 * Method used to get the field card
	 *
	 * @return the card
	 */
	public EmvCard getCard() {
		return card;
	}

}
