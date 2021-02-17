package md.intelectsoft.petrolexpert.emvcardreader.iso7816emv;


import md.intelectsoft.petrolexpert.emvcardreader.enums.TagTypeEnum;
import md.intelectsoft.petrolexpert.emvcardreader.enums.TagValueTypeEnum;

public interface ITag {

	enum Class {
		UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
	}

	boolean isConstructed();

	byte[] getTagBytes();

	String getName();

	String getDescription();

	TagTypeEnum getType();

	TagValueTypeEnum getTagValueType();

	Class getTagClass();

	int getNumTagBytes();

}
