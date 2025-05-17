package bp.difference.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/DataUser/SFTableHandler")
@ResponseBody
public class SFTableHandler_Controller{
	
	@RequestMapping(value = "/Demo_HandlerEmps")
	 public String Demo_HandlerEmps() throws Exception
     {
         bp.port.Emps emps = new bp.port.Emps();
         emps.RetrieveAll();
         return emps.ToJson();
     }

    @RequestMapping(value = "/Demo_CCFlow")
     public int  Demo_CCFlow(){
	    return 1;
     }

}
