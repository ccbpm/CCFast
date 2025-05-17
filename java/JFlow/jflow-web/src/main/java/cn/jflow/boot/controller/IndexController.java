/**
 * 
 */
package cn.jflow.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页
 * @author Bryce Han
 *
 */
@Controller
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
}
