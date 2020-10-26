package com.food.foodzone.common;

import android.graphics.Typeface;
import android.os.Environment;

import com.food.foodzone.models.MenuItemDo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class AppConstants
{
	public static double FoodZone_Latitude               = 45.4912;
	public static double FoodZone_Longitude              = -73.5864;
	public static double FoodZone_Area                   = 100;
	public static double Table_Reserve_Price             = 10;
	public static double Table_Cancel_Price              = 10;

	public static final String GILL_SANS_TYPE_FACE			= "Gill Sans";
	public static final String MONTSERRAT_MEDIUM_TYPE_FACE  = "montserrat_medium";
	public static Typeface tfMedium;
	public static Typeface tfRegular;
	public static String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString() + File.separator;
	public static final String INTERNET_CHECK			 	 = "InternetCheck";
	public static final DecimalFormat TwoDigitsNumber = new DecimalFormat("00");
	public static final DecimalFormat Decimal_Number = new DecimalFormat("00.00");
	public static int selectedTab						     = 1;
	public static final String Chef_Role					 = "Chef";
	public static final String Manager_Role   				 = "Manager";
	public static final String Customer_Role  				 = "Customer";
	public static final String User_Type     			 = "UserType";//key
	public static final String User_Role     			 = "UserRole";//key
	public static String LoggedIn_User_Type     	     = "LoggedInUserType";
	//tables
	public static final String Table_Users				 = "Users";
	public static final String Table_Rating      		 = "Rating";
	public static final String Table_Support    		 = "Support";
	public static final String Table_Tables      		 = "Tables";
	public static final String Table_Item				 = "Items";
	public static final String Table_Orders				 = "Orders";

	public static final String Profiles_Storage_Path 	 = "Profiles/";
	public static final String Food_Storage_Path     	 = "Food/";
	public static final String GmailSenderMail				 = "yaminireddybanda@gmail.com";
	public static final String GmailSenderPassword   		 = "Project2020";
	public static final String EmailSubject         		 = "Foodzone New Password";
	public static final String Exit							 = "Exit";
	public static final String From							 = "From";
	public static final String DineIn    					 = "DineIn";
	public static final String DineInNow					 = "DineInNow";
	public static final String DineInLater					 = "DineInLater";
	public static final String TakeOut   					 = "TakeOut";
	public static final String Menu     					 = "Menu";
	public static final String ManageMenu     				 = "ManageMenu";
	public static final String Status_Pending     			 = "Pending";
	public static final String Status_Started      			 = "Started";
	public static final String Status_Rejected     			 = "Rejected";
	public static final String Status_Cancelled     	     = "Cancelled";
	public static final String Status_Accepted       	     = "Accepted";

	public static ArrayList<MenuItemDo> Cart_Items			= new ArrayList<>();

	public static final double Discount					    = 1.00;
	public static final double Charges					    = 15.00;

	public static String from								= "";
	public static final int Pickup_Min_Time					= 30;// 30 mins
	public static final int Pickup_Max_Time					= 12;

	public static ArrayList<String> getCountryList() {
		ArrayList<String> countryList = new ArrayList<>();
		countryList.add("CANADA");
		countryList.add("USA");
		countryList.add("INDIA");
		countryList.add("JAPAN");
		countryList.add("AUSTRALIA");
		countryList.add("UK");
		countryList.add("ITALY");
		countryList.add("GERMANY");
		countryList.add("RUSSIA");
		countryList.add("FRANCE");
		countryList.add("AFRICA");
		countryList.add("BRAZIL");
		countryList.add("MEXICO");
		Collections.sort(countryList);
		countryList.add(0, "Select Country");
		return countryList;
	}

	public static ArrayList<String> getProvince() {
		ArrayList<String> provinceList = new ArrayList<>();
		provinceList.add("Alberta");
		provinceList.add("British Columbia");
		provinceList.add("Manitoba");
		provinceList.add("New Brunswick");
		provinceList.add("Newfoundland and Labrador");
		provinceList.add("Northwest Territories");
		provinceList.add("Nova Scotia");
		provinceList.add("NunavuT");
		provinceList.add("Ontario");
		provinceList.add("Prince Edward Island");
		provinceList.add("Quebec");
		provinceList.add("Saskatchewan");
		provinceList.add("Yukon");
		Collections.sort(provinceList);
		provinceList.add(0, "Select Province");
		return provinceList;
	}

}
