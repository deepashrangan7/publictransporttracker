package com.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.model.TicketBean;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Component
public class TicketFunc {
	@Autowired
	private TicketDao td;

	public void gen(Integer id) throws IOException
	{
		TicketBean tb=new TicketBean();
		Optional<TicketBean> o=td.findById(id);
		if(o.isPresent())
			tb=o.get();
		String details="This Is Your Ticket\n "+"Ticekt id : "+tb.getTid()+"\nUser Id : "+tb.getUid()+
				"\nFrom : "+tb.getBusfrom()+"\nTo : "+tb.getBusto()+
				"\nTotal Cost : "+tb.getCost()+"\nNo_Of_Ticket : "+tb.getCount()+"\nDate : "+tb.getDate()
				+"\nBus No :"+tb.getBus_no()+"\nActive : "+tb.getActive();
		
//		String details = "\n\n		This is Your Ticket " + name + "\n		Your Ticket Id is 0 \n" 
//				+ "\n		Bus No is  1 \n date is "+datet ;
	
		ByteArrayOutputStream out1 = QRCode.from(details).to(ImageType.JPG).stream();
		String path = "c:\\nn\\";
//		String filepath = "c:\\nn\\";
		path +="" + tb.getTid() + ".png";
		java.io.File fs = new java.io.File(path);
		FileOutputStream f = new FileOutputStream(fs);
		f.write(out1.toByteArray());
		f.flush();
		System.out.println("\n\n!!!!!!QR SUCCESFULLY generated!!!!!!");
		
		
	////res.setContentType("text/html"); 
	////
		//////
				}

	
}
