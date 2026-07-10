package nl.tno.netnbus.fom.enums;

/** Enumeration representing the sharing enum of interactions and object attributes (present in FOM) */
public enum SharingEnum
{
	PUBLISH,
	SUBSCRIBE,
	PUBLISHSUBSCRIBE,
	NEITHER;

	public static SharingEnum fromFomString( String fomString )
	{
		if( fomString.equalsIgnoreCase("PublishSubscribe") )
			return SharingEnum.PUBLISHSUBSCRIBE;
		if( fomString.equalsIgnoreCase("Subscribe") )
			return SharingEnum.SUBSCRIBE;
		if( fomString.equalsIgnoreCase("Publish") )
			return SharingEnum.PUBLISH;
		return SharingEnum.NEITHER;
	}
};

