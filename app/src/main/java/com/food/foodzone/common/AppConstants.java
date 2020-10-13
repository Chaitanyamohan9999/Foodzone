package com.food.foodzone.common;

import android.graphics.Typeface;
import android.os.Environment;

import com.food.foodzone.models.MenuItemDo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
	public static final DecimalFormat decimalFormat 		 = new DecimalFormat("00");
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
	public static final String Table_Employees   		 = "Employees";
	public static final String Table_Item				 = "Items";

	public static final String Profiles_Storage_Path 	 = "Profiles/";
	public static final String Food_Storage_Path     	 = "Food/";
	public static final String GmailSenderMail				 = "yaminireddybanda@gmail.com";
	public static final String GmailSenderPassword   		 = "Project2020";
	public static final String EmailSubject         		 = "Foodzone New Password";
	public static final String Exit							 = "Exit";
	public static final String From							 = "From";
	public static final String DiveIn						 = "DiveIn";
	public static final String TakeOut   					 = "TakeOut";
	public static final String Reservation					 = "Reservation";

	public static ArrayList<MenuItemDo> Cart_Items			= new ArrayList<>();

	public static final String Table_Names[]				= {"Table One", "Table Two", "Table Three", "Table Four", "Table Five", "Table Six", "Table Seven", "Table Eight", "Table Nine", "Table Ten"};
	public static final int Table_Capacity[]				= {2,4,6,2,4,6,6,4,2,6};


}
