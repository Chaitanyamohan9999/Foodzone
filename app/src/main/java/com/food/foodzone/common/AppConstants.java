package com.food.foodzone.common;

import android.graphics.Typeface;
import android.os.Environment;

import java.io.File;

public class AppConstants
{
	public static int DEVICE_DISPLAY_WIDTH;
	public static int DEVICE_DISPLAY_HEIGHT;

	public static final String GILL_SANS_TYPE_FACE			= "Gill Sans";
	public static final String MONTSERRAT_MEDIUM_TYPE_FACE  = "montserrat_medium";
	public static Typeface tfMedium;
	public static Typeface tfRegular;
	public static String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString() + File.separator;
	public static final String INTERNET_CHECK			 	 = "InternetCheck";

	public static int selectedTab						    = 1;
	public static final String Chef_Role					 = "Chef";
	public static final String Manager_Role   				 = "Manager";
	public static final String Customer_Role  				 = "Customer";
	public static final String User_Type     			 = "UserType";
	public static final String User_Role     			 = "UserRole";
	public static String LoggedIn_User_Type     	     = "LoggedInUserType";
	//tables
	public static final String Table_Users				 = "Users";
	public static final String Table_Rating      		 = "Rating";
	public static final String Table_Support    		 = "Support";

	public static final String Profiles_Storage_Path 	 = "Profiles/";
	public static final String GmailSenderMail				 = "yaminireddybanda@gmail.com";
	public static final String GmailSenderPassword   		 = "Project2020";
	public static final String EmailSubject         		 = "Foodzone New Password";
	public static final String Exit							 = "Exit";
    public static final String From							 = "From";
    public static final String DiveIn						 = "DiveIn";
    public static final String TakeOut   					 = "TakeOut";
    public static final String Reservation					 = "Reservation";
}
