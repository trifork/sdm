package com.trifork.stamdata.importer.jobs.sor.xml;


public class AddressInformation
{
	private String streetName;
	private String streetBuildingIdentifier;
	private String postCodeIdentifier;
	private String districtName;
	private String countryIdentificationCode;

	private String emailAddressIdentifier;
	private String website;
	private String telephoneNumberIdentifier;
	private String faxNumberIdentifier;

	private Long eanLocationCode;
	private Boolean entityInheritedIndicator;


	public String getStreetName()
	{

		return streetName;
	}


	public void setStreetName(String streetName)
	{

		this.streetName = streetName;
	}


	public String getStreetBuildingIdentifier()
	{

		return streetBuildingIdentifier;
	}


	public void setStreetBuildingIdentifier(String streetBuildingIdentifier)
	{

		this.streetBuildingIdentifier = streetBuildingIdentifier;
	}


	public String getPostCodeIdentifier()
	{

		return postCodeIdentifier;
	}


	public void setPostCodeIdentifier(String postCodeIdentifier)
	{

		this.postCodeIdentifier = postCodeIdentifier;
	}


	public String getDistrictName()
	{

		return districtName;
	}


	public void setDistrictName(String districtName)
	{

		this.districtName = districtName;
	}


	public String getCountryIdentificationCode()
	{

		return countryIdentificationCode;
	}


	public void setCountryIdentificationCode(String countryIdentificationCode)
	{

		this.countryIdentificationCode = countryIdentificationCode;
	}


	public String getEmailAddressIdentifier()
	{

		return emailAddressIdentifier;
	}


	public void setEmailAddressIdentifier(String emailAddressIdentifier)
	{

		this.emailAddressIdentifier = emailAddressIdentifier;
	}


	public String getWebsite()
	{

		return website;
	}


	public void setWebsite(String website)
	{

		this.website = website;
	}


	public String getTelephoneNumberIdentifier()
	{

		return telephoneNumberIdentifier;
	}


	public void setTelephoneNumberIdentifier(String telephoneNumberIdentifier)
	{

		this.telephoneNumberIdentifier = telephoneNumberIdentifier;
	}


	public String getFaxNumberIdentifier()
	{

		return faxNumberIdentifier;
	}


	public void setFaxNumberIdentifier(String faxNumberIdentifier)
	{

		this.faxNumberIdentifier = faxNumberIdentifier;
	}


	public Long getEanLocationCode()
	{

		return eanLocationCode;
	}


	public void setEanLocationCode(Long eanLocationCode)
	{

		this.eanLocationCode = eanLocationCode;
	}


	public Boolean isEntityInheritedIndicator()
	{

		return entityInheritedIndicator;
	}


	public void setEntityInheritedIndicator(Boolean entityInheritedIndicator)
	{

		this.entityInheritedIndicator = entityInheritedIndicator;
	}

}
