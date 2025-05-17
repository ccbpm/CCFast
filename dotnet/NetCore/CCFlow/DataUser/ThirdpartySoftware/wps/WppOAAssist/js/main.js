if (typeof (window.wps) == "undefined") {
    window.wps = window;
}
var time=new Date().getTime() //添加时间戳，防止js文件使用浏览器缓存
document.write("<script language='javascript' src='js/common/common.js?time="+time+"'></script>");
document.write("<script language='javascript' src='js/common/func_oastarter.js?time="+time+"'></script>");
document.write("<script language='javascript' src='js/common/func_docProcess.js?time="+time+"'></script>");
document.write("<script language='javascript' src='js/common/func_tabcontrol.js?time="+time+"'></script>");
document.write("<script language='javascript' src='js/common/func_docEvents.js'?time="+time+"></script>");