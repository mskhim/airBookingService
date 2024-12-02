package com.airplaneService;


import com.airplaneService.controller.BookingRegisterManager;
import com.airplaneService.model.CustomerVO;

public class Test {

	public static void main(String[] args) {
		BookingRegisterManager brm = new BookingRegisterManager();
		CustomerVO cvo = new CustomerVO("0000001", null, null, null, null, null); 
//		brm.insertManager();
		
		brm.selectManager();
		
//		brm.deleteManager();
		
//		brm.bookingInquiryManager(cvo);
		
//		brm.bookingManager(cvo);

	}

}
