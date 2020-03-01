package com.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.BusBean;
import com.model.TicketBean;
import com.model.UserBean;
import com.service.BusDao;
import com.service.BusFun;
import com.service.TicketDao;
import com.service.TicketFunc;
import com.service.UserDao;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Controller
public class MainController {

	@Autowired
	private UserDao ud;
	@Autowired
	private TicketDao td; 
	@Autowired
	private BusDao bd;
	@Autowired
	private BusFun bf;
	@Autowired
	private TicketFunc tf;
	
	@RequestMapping("/")
	public String first() {
		return "home";
	}

	@RequestMapping("/log")
	public String login(Model m) {
		m.addAttribute("err", 0);
		return "login";
	}

	@RequestMapping("/sign")
	public String sign(Model m) {
		m.addAttribute("err", 0);
		return "signup";
	}

	@RequestMapping("/ma")
	public String main(UserBean ub, Model m, HttpSession sess) {

		UserBean obj = ud.findByName(ub.getName());
		if (obj != null) {
			m.addAttribute("id", obj.getId());
			m.addAttribute("err", 0);
			if (ub.getPassword().equals(obj.getPassword())) {
				m.addAttribute("ub", ub);
				sess.setAttribute("id", obj.getId());
				return "main";
			} else {
				m.addAttribute("err", 2);
				return "login";
			}
		} else {
			System.out.println("no account found");
			m.addAttribute("err", 1);
			return "login";
		}
	}

	@PostMapping("/adda")
	public String add(UserBean entity, Model m, HttpServletRequest req) {
//		System.out.println(entity.getName());
		UserBean obj = ud.findByName(entity.getName());
		if (obj != null) {
			m.addAttribute("err", 1);
			return "signup";
		} else {
			// System.out.println(entity);
			if (entity.getPassword() == "") {
				m.addAttribute("err", 2);
				return "signup";
			}
			ud.save(entity);
			req.getSession().setAttribute("id", ud.findByName(entity.getName()).getId());
			m.addAttribute("id", ud.findByName(entity.getName()).getId());
//			System.out.println(ud.findByName(entity.getName()).getId()+" sign id");
			m.addAttribute("ub", entity);
			return "main";
		}

	}

	@RequestMapping("/book")
	public String book(Model m) {
		// req.getSession().setAttribute("bid", bid);
		m.addAttribute("err", 0);
		return "booking";
	}

//@ModelAttribute("loc")	
//public List<String>  location(HttpServletRequest req){
//List<String> obj=new ArrayList<>();
//Integer x=(Integer)req.getAttribute("bid");
//if(x==1)
//{
//	
//}
//return obj;
//}
	@RequestMapping("/find")
	public String findBus(String busStop1, String busStop2, Model m, HttpSession sess) {
		List<BusBean> bus = new ArrayList<>();
		List<BusBean> foundBus = new ArrayList<>();

		List<Double> price = new ArrayList<>();
//		System.out.println("stop 1: " + busStop1 + "\nstop 2: " + busStop2);
		if (!busStop1.equals(busStop2)) {
			bus = bd.findAll();
			for (BusBean b : bus) {
				if (b.getStop1().equals(busStop1) || b.getStop2().equals(busStop1) || b.getStop3().equals(busStop1)
						|| b.getStop4().equals(busStop1) || b.getStop5().equals(busStop1)) {
					if (b.getStop1().equals(busStop2) || b.getStop2().equals(busStop2) || b.getStop3().equals(busStop2)
							|| b.getStop4().equals(busStop2) || b.getStop5().equals(busStop2)) {
						price.add(bf.calculateCharge(b, busStop1, busStop2));
						foundBus.add(b);
					}
				}
			}

			if (foundBus.size() == 0) {
				m.addAttribute("err", 1);
				return "booking";
			}

		} else {
			m.addAttribute("err", 2);
			return "booking";
		}
		sess.setAttribute("from", busStop1);
		sess.setAttribute("to", busStop2);
		m.addAttribute("fb", foundBus);
		m.addAttribute("pr", price);
//		System.out.println(m.getAttribute("from") + " here");
		return "findBus";
	}

	@RequestMapping(path = "/book/{bn}/{pr}")
	public String bookTickets(@PathVariable("bn") Integer bus_no, @PathVariable("pr") Double price, Model m,
			HttpSession sess) {
		m.addAttribute("bno", bus_no);
		m.addAttribute("price", price);
		Integer id = (Integer) sess.getAttribute("id");
		String from = (String) sess.getAttribute("from");
		String to = (String) sess.getAttribute("to");

		return "tick";
	}

	@RequestMapping("/done")
	public String done(Integer ticket,Double price,Model m,Integer bus_no,HttpSession sess) {

//		System.out.println((ticket*price) +"cost is");
		m.addAttribute("final1",(ticket*price));
		Integer id = (Integer) sess.getAttribute("id");
		String from = (String) sess.getAttribute("from");
		String to = (String) sess.getAttribute("to");

		TicketBean tb=new TicketBean();
		tb.setBus_no(bus_no);
		tb.setActive("yes");
		tb.setBusfrom(from);
		tb.setBusto(to);
		tb.setCount(ticket);
		tb.setUid(id);
		tb.setCost(price*ticket);
		Date d=new Date();
		String date=d.getDate()+"/3"+"/2020"+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		tb.setDate(date);
		td.save(tb);
		return "donePage";
	}
	@RequestMapping("/printq")
	public String print(HttpServletRequest req,HttpServletResponse res) 
	{
		Integer id=0;
	List<TicketBean> li=	td.findAll();
	for(TicketBean t:li)
		id=t.getTid();
	try {
	
		tf.gen(id);
	

	res.setContentType("text/html"); 

    PrintWriter out = res.getWriter(); 

    String filename = id+".png";  

    String filepath = "c:\\nn\\";  

    res.setContentType("APPLICATION/OCTET-STREAM");  

    res.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");  

      

    FileInputStream fileInputStream = new FileInputStream(filepath + filename); 

                

    int i;  

    while ((i=fileInputStream.read()) != -1) { 

    out.write(i);  

    }  

    fileInputStream.close();  

    out.close();  

	}catch(Exception e)
	{
		System.out.println("Cant Generate");
	}
		return "donePage";
	}
	
}
