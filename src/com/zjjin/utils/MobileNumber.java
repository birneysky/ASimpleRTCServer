package com.zjjin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileNumber {
	public static boolean isMobileNO(String mobiles) {
		/**
		 * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188，183，184，147，178
	     * 联通：130,131,132,152,155,156,185,186，145，176
	     * 电信：133,1349,153,180,189，181，177，170（虚拟号段）
	     */
		String compy = "^1(34[0-8]|(3[5-9]|47|78|5[017-9]|8[23478])\\d)\\d{7}$";
		String compl = "^1(3[0-2]|45|76|5[256]|8[56])\\d{8}$";
		String compd = "^1((33|53|7[07]|8[09])[0-9]|349)\\d{7}$";
        Pattern py = Pattern.compile(compy);
        Pattern pl = Pattern.compile(compl);
        Pattern pd = Pattern.compile(compd);
        Matcher my = py.matcher(mobiles);
        Matcher ml = pl.matcher(mobiles);
        Matcher md = pd.matcher(mobiles);
        if(my.matches() || ml.matches() || md.matches()){
        	return true;
        }
        return false;
    }
}
