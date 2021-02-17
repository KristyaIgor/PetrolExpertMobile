package md.intelectsoft.petrolexpert.emvcardreader.parser.apdu;


import java.util.List;

import md.intelectsoft.petrolexpert.emvcardreader.iso7816emv.TagAndLength;


/**
 * Interface for File to parse
 */
public interface IFile {

	/**
	 * Method to parse byte data
	 * 
	 * @param pData
	 *            byte to parse
	 * @param pList
	 *            Tag and length
	 */
	void parse(final byte[] pData, final List<TagAndLength> pList);

}
