/*--------------------------------------------------------------------------
 *
 * BJCA Adaptive Javascript, Version SAB(Support All Browsers :))
 * This script support bjca client version 2.0 and later
 * Author:BJCA-zys
 *--------------------------------------------------------------------------*/

/* globals var */
var $_$softCertListID = ""; // Soft CertListID, Set by SetUserCertList
var $_$hardCertListID = ""; // USBKeyCertListID, Set by SetUserCertList
var $_$allCertListID = "";  // All CertListID, Set by SetUserCertList
var $_$loginCertID = "";    // logined CertID, Set by SetAutoLogoutParameter
var $_$logoutFunc = null;   // logout Function, Set by SetAutoLogoutParameter
var $_$onUsbKeyChangeCallBackFunc = null; //custom onUsbkeyChange callback function
var $_$XTXAlert = null;     // alert custom function
var $_$XTXAppObj = null;    // XTXAppCOM class Object
var $_$SecXV2Obj = null;    // BJCASecCOMV2 class Object
var $_$SecXObj = null;      // BJCASecCOM class Object
var $_$WebSocketObj = null; // WebSocket class Object
var $_$CurrentObj = null;   // Current use class Object
var $_$GetPicObj = null;    // GetKeyPic class Object

// const var
var CERT_TYPE_HARD = 1;
var CERT_TYPE_SOFT = 2;
var CERT_TYPE_ALL  = 3;

// const var
var CERT_OID_VERSION     = 1;
var CERT_OID_SERIAL      = 2;
var CERT_OID_SIGN_METHOD = 3;
var CERT_OID_ISSUER_C    = 4;
var CERT_OID_ISSUER_O    = 5;
var CERT_OID_ISSUER_OU   = 6;
var CERT_OID_ISSUER_ST   = 7;
var CERT_OID_ISSUER_CN   = 8;
var CERT_OID_ISSUER_L    = 9;
var CERT_OID_ISSUER_E    = 10;
var CERT_OID_NOT_BEFORE  = 11;
var CERT_OID_NOT_AFTER   = 12;
var CERT_OID_SUBJECT_C   = 13;
var CERT_OID_SUBJECT_O   = 14;
var CERT_OID_SUBJECT_OU  = 15;
var CERT_OID_SUBJECT_ST  = 16;
var CERT_OID_SUBJECT_CN  = 17;
var CERT_OID_SUBJECT_L   = 18;
var CERT_OID_SUBJECT_E   = 19;
var CERT_OID_PUBKEY      = 20;
var CERT_OID_SUBJECT_DN  = 33;
var CERT_OID_ISSUER_DN   = 34;


// set auto logout parameters
function SetAutoLogoutParameter(strCertID, logoutFunc)
{
    $_$loginCertID = strCertID;
    $_$logoutFunc = logoutFunc;
    return;
}

function SetLoginCertID(strCertID)
{
    $_$loginCertID = strCertID;
    return;
}

function SetLogoutFunction(logoutFunc)
{
    $_$logoutFunc = logoutFunc;
}

function GetDateNotBefore(strCertValid) {
    var strYear = strCertValid.substring(0, 4);
    var strMonth = strCertValid.substring(4, 6);
    var strDay = strCertValid.substring(6, 8);
    var strHour = strCertValid.substring(8, 10);
    var strMin = strCertValid.substring(10, 12);
    var strSecond = strCertValid.substring(12, 14);
    var RtnDate = new Date();
    RtnDate.setFullYear(Number(strYear), Number(strMonth) - 1, Number(strDay));
    RtnDate.setHours(Number(strHour));
    RtnDate.setMinutes(Number(strMin));
    RtnDate.setSeconds(Number(strSecond));
    return RtnDate;
};

function GetDateNotAfter(strCertValid) {
    var strYear = strCertValid.substring(0, 4);
    var strMonth = strCertValid.substring(4, 6);
    var strDay = strCertValid.substring(6, 8);
    var strHour = strCertValid.substring(8, 10);
    var strMin = strCertValid.substring(10, 12);
    var strSecond = strCertValid.substring(12, 14);
    var RtnDate = new Date();
    RtnDate.setFullYear(Number(strYear), Number(strMonth) - 1, Number(strDay));
    RtnDate.setHours(Number(strHour));
    RtnDate.setMinutes(Number(strMin));
    RtnDate.setSeconds(Number(strSecond));
    return RtnDate;
};



// set user cert list id
function SetUserCertList(strListID, certType)
{
    if (arguments.length == 1) {
        $_$hardCertListID = strListID;
    } else {
        if (certType == CERT_TYPE_HARD) {
            $_$hardCertListID = strListID;
        }
        if (certType == CERT_TYPE_SOFT) {
            $_$softCertListID = strListID;
        }
        if (certType == CERT_TYPE_ALL) {
            $_$allCertListID = strListID;
        }
    }
    GetUserList($pushAllDropListBox);

    return;
}

// set custom usbkeychange callback
function SetOnUsbKeyChangeCallBack(callback)
{
    $_$onUsbKeyChangeCallBackFunc = callback;
}

// set custom alert function
function SetAlertFunction(custom_alert)
{
    $_$XTXAlert = custom_alert;
}

function $checkBrowserISIE()
{
    return (!!window.ActiveXObject || 'ActiveXObject' in window) ? true : false;
}

function $popDropListBoxAll(strListID)
{
    var objListID = eval(strListID);
    if (objListID == undefined) {
        return;
    }
    var i, n = objListID.length;
    for(i = 0; i < n; i++) {
        objListID.remove(0);
    }

    objListID = null;
}

function $pushOneDropListBox(userListArray, strListID)
{
    var objListID = eval(strListID);
    if (objListID == undefined) {
        return;
    }

    var i;
    for (i = 0; i < userListArray.length; i++) {
        var certObj = userListArray[i];
        var objItem = new Option(certObj.certName, certObj.certID);
        objListID.options.add(objItem);
    }

    objListID = null;

    return;
}

function $pushAllDropListBox(certUserListObj)
{
    if ($_$hardCertListID != "") {
        $popDropListBoxAll($_$hardCertListID);
    }
    if ($_$softCertListID != "") {
        $popDropListBoxAll($_$softCertListID);
    }

    if ($_$allCertListID != "") {
        $popDropListBoxAll($_$allCertListID);
    }

    var strUserList = certUserListObj.retVal;
    var allListArray = []
    while (true) {
        var i = strUserList.indexOf("&&&");
        if (i <= 0 ) {
            break;
        }
        var strOneUser = strUserList.substring(0, i);
        var strName = strOneUser.substring(0, strOneUser.indexOf("||"));
        var strCertID = strOneUser.substring(strOneUser.indexOf("||") + 2, strOneUser.length);
        allListArray.push({certName:strName, certID:strCertID});

        if ($_$hardCertListID != "") {
            GetDeviceType(strCertID, function(retObj) {
                if (retObj.retVal == "HARD") {
                    $pushOneDropListBox([retObj.ctx], $_$hardCertListID);
                }
            }, {certName:strName, certID:strCertID});
        }

        if ($_$softCertListID != "") {
            GetDeviceType(strCertID, function(retObj) {
                if (retObj.retVal == "SOFT") {
                    $pushOneDropListBox([retObj.ctx], $_$softCertListID);
                }
            }, {certName:strName, certID:strCertID});
        }
        var len = strUserList.length;
        strUserList = strUserList.substring(i + 3,len);
    }

    if ($_$allCertListID != "") {
        $pushOneDropListBox(allListArray, $_$allCertListID);
    }
}

function $myAutoLogoutCallBack(retObj)
{
    if (retObj.retVal.indexOf($_$loginCertID) <= 0) {
        $_$logoutFunc();
    }
}

//usbkey change default callback function
function $OnUsbKeyChange()
{
    GetUserList($pushAllDropListBox);
    if (typeof $_$onUsbKeyChangeCallBackFunc == 'function') {
        $_$onUsbKeyChangeCallBackFunc();
    }
    if ($_$loginCertID != "" && typeof $_$logoutFunc == 'function') {
        GetUserList($myAutoLogoutCallBack);
    }
}

// IE11 attach event
function $AttachIE11OnUSBKeychangeEvent(strObjName)
{
    var handler = document.createElement("script");
    handler.setAttribute("for", strObjName);
    handler.setAttribute("event", "OnUsbKeyChange");
    handler.appendChild(document.createTextNode("$OnUsbKeyChange()"));
    document.body.appendChild(handler);
}

//load a control
function $LoadControl(CLSID, ctlName, testFuncName, addEvent)
{
    var pluginDiv = document.getElementById("pluginDiv" + ctlName);
    if (pluginDiv) {
        return true;
    }
    pluginDiv = document.createElement("div");
    pluginDiv.id = "pluginDiv" + ctlName;
    document.body.appendChild(pluginDiv);

    try {
        if ($checkBrowserISIE()) {  // IE
            pluginDiv.innerHTML = '<object id="' + ctlName + '" classid="CLSID:' + CLSID + '" style="HEIGHT:0px; WIDTH:0px"></object>';
            if (addEvent) {
                var clt = eval(ctlName);
                if (clt.attachEvent) {
                    clt.attachEvent("OnUsbKeyChange", $OnUsbKeyChange);
                } else {// IE11 not support attachEvent, and addEventListener do not work well, so addEvent ourself
                    $AttachIE11OnUSBKeychangeEvent(ctlName);
                }
            }
        } else {
            var chromeVersion = window.navigator.userAgent.match(/Chrome\/(\d+)\./);
            if (chromeVersion && chromeVersion[1]) {
                if (parseInt(chromeVersion[1], 10) >= 42) { // not support npapi return false
                    document.body.removeChild(pluginDiv);
                    pluginDiv.innerHTML = "";
                    pluginDiv = null;
                    return false;
                }
            }

            if (addEvent) {
                pluginDiv.innerHTML = '<embed id=' + ctlName + ' type=application/x-xtx-axhost clsid={' + CLSID + '} event_OnUsbkeyChange=$OnUsbKeyChange width=0 height=0 />' ;
            } else {
                pluginDiv.innerHTML = '<embed id=' + ctlName + ' type=application/x-xtx-axhost clsid={' + CLSID + '} width=0 height=0 />' ;
            }
        }

        if (testFuncName != null && testFuncName != "" && eval(ctlName + "." + testFuncName) == undefined) {
            document.body.removeChild(pluginDiv);
            pluginDiv.innerHTML = "";
            pluginDiv = null;
            return false;
        }
        return true;
    } catch (e) {
        document.body.removeChild(pluginDiv);
        pluginDiv.innerHTML = "";
        pluginDiv = null;
        return false;
    }
}

function $XTXAlert(strMsg) {
    if (typeof $_$XTXAlert == 'function') {
        $_$XTXAlert(strMsg);
    } else {
        alert(strMsg);
    }
}

function $myOKRtnFunc(retVal, cb, ctx)
{
    if (typeof cb == 'function') {
        var retObj = {retVal:retVal, ctx:ctx};
        cb(retObj);
    }
    return retVal;
}




function $myErrorRtnFunc(retVal, cb, ctx)
{
    if (typeof cb == 'function') {
        var retObj = {retVal:retVal, ctx:ctx};
        cb(retObj);
    }

    return retVal;
}



function $loginSignRandomCallBack(retObj)
{
    if (retObj.retVal == "") {
        $XTXAlert("客户端签名失败!");
        return;
    }
    var objForm = retObj.ctx.objForm;
    var strAction = retObj.ctx.action;
    objForm.UserSignedData.value = retObj.retVal;
    objForm.action = strAction;
    var js_str = $(objForm).attr("js_str");
    eval(js_str);
}

function $loginVerifyServerSignatureCallBack(retObj)
{
    if (!retObj.retVal) {
        $XTXAlert("验证服务器端信息失败!");
        return;
    }

    var strCertID = retObj.ctx.certID;
    SignedData(strCertID, strServerRan, $loginSignRandomCallBack, retObj.ctx);
}
function $loginCheckCertValidNotAfter(retObj)
{

    var notAfterDate = GetDateNotAfter(retObj.retVal);
    var milliseconds = notAfterDate.getTime() - new Date().getTime();
    if (milliseconds < 0) {
        $XTXAlert("您的证书已过期，请尽快到北京数字证书认证中心办理证书更新手续！");
        return;
    }

    days = parseInt(milliseconds / (1000*60*60*24));
    if (days > 0 && days <= 60) {
        $XTXAlert("您的证书还有" + days + "天过期\n请您尽快到北京数字证书认证中心办理证书更新手续！");
    } else if (days == 0) { // 证书有效期天数小于1天
        var hours = parseInt(milliseconds / (1000*60*60));
        if (hours > 0) {
            $XTXAlert("您的证书还有" + hours + "小时过期\n您尽快到北京数字证书认证中心办理证书更新手续！");
        }
        // 证书有效期小于1小时
        var minutes = parseInt(milliseconds / (1000*60));
        if (minutes > 1) {
            $XTXAlert("您的证书还有" + minutes + "分钟过期\n您尽快到北京数字证书认证中心办理证书更新手续！");
        } else {
            $XTXAlert("您的证书已过期，请尽快到北京数字证书认证中心办理证书更新手续！");
            return;
        }
    }

    VerifySignedData(strServerCert, strServerRan, strServerSignedData,
        $loginVerifyServerSignatureCallBack, retObj.ctx);
}

function $loginCheckCertValidNotBefore(retObj)
{
    var notBeforeDate = GetDateNotBefore(retObj.retVal);
    var days = parseInt((notBeforeDate.getTime() - new Date().getTime()) / (1000*60*60*24));
    if (days > 0) {
        $XTXAlert("您的证书尚未生效!距离生效日期还剩" + days + "天!");
        return;
    }

    var strUserCert = retObj.ctx.objForm.UserCert.value;
    GetCertBasicinfo(strUserCert, CERT_OID_NOT_AFTER, $loginCheckCertValidNotAfter, retObj.ctx);

}

function $loginGetSignCertCallBack(retObj)
{
    var strUserCert = retObj.retVal;
    if (strUserCert == "") {
        $XTXAlert("导出用户证书失败!");
        return;
    }
    retObj.ctx.objForm.UserCert.value =  strUserCert;

    GetCertBasicinfo(strUserCert, CERT_OID_NOT_BEFORE, $loginCheckCertValidNotBefore, retObj.ctx);
}

function $loginGetPINRetryCallBack(retObj)
{
    var retryCount = Number(retObj.retVal);
    if (retryCount > 0) {
        $XTXAlert("校验证书密码失败!您还有" + retryCount + "次机会重试!");
        return;
    } else if (retryCount == 0) {
        $XTXAlert("您的证书密码已被锁死,请联系BJCA进行解锁!");
        return;
    } else {
        $XTXAlert("登录失败!");
        return;
    }
}

function $loginVerifyPINCallBack(retObj)
{
    var strCertID = retObj.ctx.certID;
    var objForm = retObj.ctx.objForm;
    if (!retObj.retVal) {
        GetUserPINRetryCount(strCertID, $loginGetPINRetryCallBack);
        return;
    }

    objForm.ContainerName.value = strCertID;

    GetSignCert(strCertID, $loginGetSignCertCallBack, retObj.ctx);
}

function Logout(certid, cb, ctx) {
    if ($_$CurrentObj != null && $_$CurrentObj.Logout != undefined) {
        return $_$CurrentObj.Logout(certid, cb, ctx);
    }
}

//Form login
function Login(formName, strCertID, strPin, strAction) {
    var objForm = eval(formName);
    if (objForm == null) {
        $XTXAlert("表单错误！");
        return;
    }
    if (strCertID == null || strCertID == "") {
        $XTXAlert("请输入证书密码！");
        return;
    }
    if (strPin == null || strPin == "") {
        $XTXAlert("请输入证书密码！");
        return;
    }

    //Add a hidden item ...
    var strSignItem = "<input type=\"hidden\" name=\"UserSignedData\" value=\"\">";
    if (objForm.UserSignedData == null) {
        objForm.insertAdjacentHTML("BeforeEnd", strSignItem);
    }
    var strCertItem = "<input type=\"hidden\" name=\"UserCert\" value=\"\">";
    if (objForm.UserCert == null) {
        objForm.insertAdjacentHTML("BeforeEnd", strCertItem);
    }
    var strContainerItem = "<input type=\"hidden\" name=\"ContainerName\" value=\"\">";
    if (objForm.ContainerName == null) {
        objForm.insertAdjacentHTML("BeforeEnd", strContainerItem);
    }

    var ctx = {certID:strCertID, objForm:objForm, action:strAction};

    VerifyUserPIN(strCertID, strPin, $loginVerifyPINCallBack, ctx);

    return;
}

function GetDeviceType(strCertID,cb,ctx)
{
    GetDeviceInfo(strCertID, 7,cb,ctx);
}



//XTXAppCOM class
function CreateXTXAppObject() {
    var bOK = $LoadControl("3F367B74-92D9-4C5E-AB93-234F8A91D5E6", "XTXAPP", "SOF_GetVersion()", true);
    return bOK;
}


function CreateGetPicObject() {
    var bOK = $LoadControl("3BC3C868-95B5-47ED-8686-E0E3E94EF366", "OGetPic", "GetPic()", false);
    if (!bOK) {
        return null;
    }
}

function CreateDsvsObject() {
    var bOK = $LoadControl("DA4B155C-F991-46C8-896D-2305B364CAA7", "SVSAPP", "GenRandom()", false);
    if (!bOK) {
        return null;
    }
}

//webSocket client class
function CreateWebSocketObject(myonopen, myonerror) {

    var o = new Object();


    o.ws_obj = null;
    o.ws_heartbeat_id = 0;
    o.ws_queue_id = 0; // call_cmd_id
    o.ws_queue_list = {};  // call_cmd_id callback queue
    o.ws_queue_ctx = {};
    o.xtx_version = "";

    o.load_websocket = function () {

        var ws_url = "wss://127.0.0.1:21061/xtxapp/";
        ws_url = "ws://127.0.0.1:21051/xtxapp/";
        //ws_url = "ws://192.168.219.46:21051/xtxapp/";
        try {
            o.ws_obj = new WebSocket(ws_url);
        } catch (e) {
            if (myonerror) {
                myonerror();
            }
            if(console){
                console.log(e);
            }
            return false;
        }

        o.ws_queue_list["onUsbkeyChange"] = $OnUsbKeyChange;

        o.ws_obj.onopen = function (evt) {
            if (myonopen) {
                myonopen();
            }

        };

        o.ws_obj.onerror = function (evt) {
            if (myonerror) {
                myonerror();
            }
        };

        o.ws_obj.onclose = function (evt) {

        };


        o.ws_obj.onmessage = function (evt) {

            var res = JSON.parse(evt.data);
            if (res['set-cookie']) {
                document.cookie = res['set-cookie'];
            }

            //登录失败
            if (res['loginError']) {
                alert(res['loginError']);
            }

            var call_cmd_id = res['call_cmd_id'];
            if (!call_cmd_id) {
                return;
            }

            var execFunc = o.ws_queue_list[call_cmd_id];
            if (typeof(execFunc) != 'function') {
                return;
            }

            var ctx = o.ws_queue_ctx[res['call_cmd_id']];
            ctx = ctx || {returnType: "string"};

            var ret;
            if (ctx.returnType == "bool") {
                ret = res.retVal == "true" ? true : false;
            }
            else if (ctx.returnType == "number") {
                ret = Number(res.retVal);
            }
            else {
                ret = res.retVal;
            }
            var retObj = {retVal: ret, ctx: ctx};

            execFunc(retObj);

            if (res['call_cmd_id'] != "onUsbkeyChange") {
                delete o.ws_queue_list[res['call_cmd_id']];
            }
            delete o.ws_queue_ctx[res['call_cmd_id']];

        };

        return true;
    };


    o.sendMessage = function (sendMsg) {
        if (o.ws_obj.readyState == WebSocket.OPEN) {
            o.ws_obj.send(JSON.stringify(sendMsg));
        } else {
            setTimeout(function () {
                if (sendMsg.count) {
                    sendMsg.count++;
                    if (sendMsg.count === 4) {
                        return;
                    }
                }
                else {
                    sendMsg.count = 1;
                }
                o.sendMessage(sendMsg);
            }, 500);
            console.log("Can't connect to WebSocket server!");
        }
    };

    o.callMethod = function (strMethodName, cb, ctx, returnType, argsArray) {
        o.ws_queue_id++;
        if (typeof(cb) == 'function') {
            o.ws_queue_list['i_' + o.ws_queue_id] = cb;
            ctx = ctx || {};
            ctx.returnType = returnType;
            o.ws_queue_ctx['i_' + o.ws_queue_id] = ctx;
        }

        var sendArray = {};
        //  sendArray['cookie'] = document.cookie;
        sendArray['xtx_func_name'] = strMethodName;
        sendArray['call_cmd_id'] = 'i_' + o.ws_queue_id;


        // if (arguments.length > 4) {
        //     sendArray["param"] = argsArray;
        // }
        if (arguments.length > 4) {
            for (var i = 1; i <= argsArray.length; i++) {
                var strParam = "param_" + i;
                sendArray[strParam] = argsArray[i - 1];
            }
            sendArray["param"] = argsArray;
        }


        o.sendMessage(sendArray);

    };

    if (!o.load_websocket()) {
        return null;
    }
    return o;
}


//Interface
var ComInterface 		= 	{};
var PicComInterface 	= 	{};
var WebsocketInterface 	= 	{};
var CurrentInterface    = 	{};
var DsvsInterface    = 	{};



//init_Interface
function init_Interface(func,altename)
{
    window[func] = function(){
        if(CurrentInterface[func])
        {
            CurrentInterface[func].apply(this,arguments);
            return;
        }

        if(PicComInterface[func])
        {
            PicComInterface[func].apply(this,arguments);
            return;
        }

        if(DsvsInterface[func])
        {
            DsvsInterface[func].apply(this,arguments);
            return;
        }

    }

    if(altename){
        window[altename] = window[func];
    }

}




//初始化
function init(sunccess,error){
    CreateGetPicObject();
    CreateDsvsObject();
    var b = CreateXTXAppObject();
    if(b){
        CurrentInterface = ComInterface;
        if(sunccess){
            sunccess();
        }

        return;
    }

    WebsocketApp = CreateWebSocketObject(sunccess,error);
    if(WebsocketApp){
        CurrentInterface = WebsocketInterface;
    }
}




//template_gen
ComInterface.SOF_SetSignMethod = function(SignMethod,cb,ctx){
    var ret = XTXAPP.SOF_SetSignMethod(SignMethod);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SetSignMethod = function(SignMethod,cb,ctx){
    var paramArray = [SignMethod];
    WebsocketApp.callMethod('SOF_SetSignMethod', cb, ctx, "number", paramArray);
}


ComInterface.SOF_GetSignMethod = function(cb,ctx){
    var ret = XTXAPP.SOF_GetSignMethod();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetSignMethod = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetSignMethod', cb, ctx, "number", paramArray);
}


ComInterface.SOF_SetEncryptMethod = function(EncryptMethod,cb,ctx){
    var ret = XTXAPP.SOF_SetEncryptMethod(EncryptMethod);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SetEncryptMethod = function(EncryptMethod,cb,ctx){
    var paramArray = [EncryptMethod];
    WebsocketApp.callMethod('SOF_SetEncryptMethod', cb, ctx, "number", paramArray);
}


ComInterface.SOF_GetEncryptMethod = function(cb,ctx){
    var ret = XTXAPP.SOF_GetEncryptMethod();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetEncryptMethod = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetEncryptMethod', cb, ctx, "number", paramArray);
}


ComInterface.SOF_GetUserList = function(cb,ctx){
    var ret = XTXAPP.SOF_GetUserList();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetUserList = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetUserList', cb, ctx, "string", paramArray);
}


ComInterface.SOF_ExportUserCert = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_ExportUserCert(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_ExportUserCert = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_ExportUserCert', cb, ctx, "string", paramArray);
}


ComInterface.SOF_Login = function(CertID,PassWd,cb,ctx){
    var ret = XTXAPP.SOF_Login(CertID,PassWd);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Login = function(CertID,PassWd,cb,ctx){
    var paramArray = [CertID,PassWd];
    WebsocketApp.callMethod('SOF_Login', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetPinRetryCount = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_GetPinRetryCount(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetPinRetryCount = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_GetPinRetryCount', cb, ctx, "number", paramArray);
}


ComInterface.SOF_ChangePassWd = function(CertID,oldPass,newPass,cb,ctx){
    var ret = XTXAPP.SOF_ChangePassWd(CertID,oldPass,newPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_ChangePassWd = function(CertID,oldPass,newPass,cb,ctx){
    var paramArray = [CertID,oldPass,newPass];
    WebsocketApp.callMethod('SOF_ChangePassWd', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetCertInfo = function(Cert,type,cb,ctx){
    var ret = XTXAPP.SOF_GetCertInfo(Cert,type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetCertInfo = function(Cert,type,cb,ctx){
    var paramArray = [Cert,type];
    WebsocketApp.callMethod('SOF_GetCertInfo', cb, ctx, "string", paramArray);
}


ComInterface.SOF_GetCertInfoByOid = function(Cert,Oid,cb,ctx){
    var ret = XTXAPP.SOF_GetCertInfoByOid(Cert,Oid);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetCertInfoByOid = function(Cert,Oid,cb,ctx){
    var paramArray = [Cert,Oid];
    WebsocketApp.callMethod('SOF_GetCertInfoByOid', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignData = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_SignData(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignData = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_SignData', cb, ctx, "string", paramArray);
}

WebsocketInterface.SOF_SignDataBase64 = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_SignDataBase64', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedData = function(Cert,InData,SignValue,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedData(Cert,InData,SignValue);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedData = function(Cert,InData,SignValue,cb,ctx){
    var paramArray = [Cert,InData,SignValue];
    WebsocketApp.callMethod('SOF_VerifySignedData', cb, ctx, "bool", paramArray);
}

WebsocketInterface.SOF_VerifySignedDataBase64 = function(Cert,InData,SignValue,cb,ctx){
    var paramArray = [Cert,InData,SignValue];
    WebsocketApp.callMethod('SOF_VerifySignedDataBase64', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_SignFile = function(CertID,InFile,cb,ctx){
    var ret = XTXAPP.SOF_SignFile(CertID,InFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignFile = function(CertID,InFile,cb,ctx){
    var paramArray = [CertID,InFile];
    WebsocketApp.callMethod('SOF_SignFile', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedFile = function(Cert,InFile,SignValue,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedFile(Cert,InFile,SignValue);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedFile = function(Cert,InFile,SignValue,cb,ctx){
    var paramArray = [Cert,InFile,SignValue];
    WebsocketApp.callMethod('SOF_VerifySignedFile', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_EncryptData = function(Cert,InData,cb,ctx){
    var ret = XTXAPP.SOF_EncryptData(Cert,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EncryptData = function(Cert,InData,cb,ctx){
    var paramArray = [Cert,InData];
    WebsocketApp.callMethod('SOF_EncryptData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_DecryptData = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_DecryptData(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_DecryptData = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_DecryptData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_EncryptFile = function(Cert,InFile,OutFile,cb,ctx){
    var ret = XTXAPP.SOF_EncryptFile(Cert,InFile,OutFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EncryptFile = function(Cert,InFile,OutFile,cb,ctx){
    var paramArray = [Cert,InFile,OutFile];
    WebsocketApp.callMethod('SOF_EncryptFile', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_DecryptFile = function(CertID,InFile,OutFile,cb,ctx){
    var ret = XTXAPP.SOF_DecryptFile(CertID,InFile,OutFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_DecryptFile = function(CertID,InFile,OutFile,cb,ctx){
    var paramArray = [CertID,InFile,OutFile];
    WebsocketApp.callMethod('SOF_DecryptFile', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_SignMessage = function(dwFlag,CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_SignMessage(dwFlag,CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignMessage = function(dwFlag,CertID,InData,cb,ctx){
    var paramArray = [dwFlag,CertID,InData];
    WebsocketApp.callMethod('SOF_SignMessage', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedMessage = function(MessageData,InData,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedMessage(MessageData,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedMessage = function(MessageData,InData,cb,ctx){
    var paramArray = [MessageData,InData];
    WebsocketApp.callMethod('SOF_VerifySignedMessage', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetInfoFromSignedMessage = function(SignedMessage,type,cb,ctx){
    var ret = XTXAPP.SOF_GetInfoFromSignedMessage(SignedMessage,type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetInfoFromSignedMessage = function(SignedMessage,type,cb,ctx){
    var paramArray = [SignedMessage,type];
    WebsocketApp.callMethod('SOF_GetInfoFromSignedMessage', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignDataXML = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_SignDataXML(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignDataXML = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_SignDataXML', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedDataXML = function(InData,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedDataXML(InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedDataXML = function(InData,cb,ctx){
    var paramArray = [InData];
    WebsocketApp.callMethod('SOF_VerifySignedDataXML', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetXMLSignatureInfo = function(XMLSignedData,type,cb,ctx){
    var ret = XTXAPP.SOF_GetXMLSignatureInfo(XMLSignedData,type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetXMLSignatureInfo = function(XMLSignedData,type,cb,ctx){
    var paramArray = [XMLSignedData,type];
    WebsocketApp.callMethod('SOF_GetXMLSignatureInfo', cb, ctx, "string", paramArray);
}


ComInterface.SOF_GenRandom = function(RandomLen,cb,ctx){
    var ret = XTXAPP.SOF_GenRandom(RandomLen);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GenRandom = function(RandomLen,cb,ctx){
    var paramArray = [RandomLen];
    WebsocketApp.callMethod('SOF_GenRandom', cb, ctx, "string", paramArray);
}


ComInterface.SOF_PubKeyEncrypt = function(Cert,InData,cb,ctx){
    var ret = XTXAPP.SOF_PubKeyEncrypt(Cert,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_PubKeyEncrypt = function(Cert,InData,cb,ctx){
    var paramArray = [Cert,InData];
    WebsocketApp.callMethod('SOF_PubKeyEncrypt', cb, ctx, "string", paramArray);
}


ComInterface.SOF_PriKeyDecrypt = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_PriKeyDecrypt(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_PriKeyDecrypt = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_PriKeyDecrypt', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SecertSegment = function(Secert,m,n,k,cb,ctx){
    var ret = XTXAPP.SOF_SecertSegment(Secert,m,n,k);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SecertSegment = function(Secert,m,n,k,cb,ctx){
    var paramArray = [Secert,m,n,k];
    WebsocketApp.callMethod('SOF_SecertSegment', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SecertRecovery = function(Seg,cb,ctx){
    var ret = XTXAPP.SOF_SecertRecovery(Seg);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SecertRecovery = function(Seg,cb,ctx){
    var paramArray = [Seg];
    WebsocketApp.callMethod('SOF_SecertRecovery', cb, ctx, "string", paramArray);
}


ComInterface.SOF_GetLastError = function(cb,ctx){
    var ret = XTXAPP.SOF_GetLastError();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetLastError = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetLastError', cb, ctx, "number", paramArray);
}


ComInterface.GetDeviceCount = function(cb,ctx){
    var ret = XTXAPP.GetDeviceCount();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetDeviceCount = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('GetDeviceCount', cb, ctx, "number", paramArray);
}


ComInterface.GetAllDeviceSN = function(cb,ctx){
    var ret = XTXAPP.GetAllDeviceSN();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetAllDeviceSN = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('GetAllDeviceSN', cb, ctx, "string", paramArray);
}


ComInterface.GetDeviceSNByIndex = function(iIndex,cb,ctx){
    var ret = XTXAPP.GetDeviceSNByIndex(iIndex);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetDeviceSNByIndex = function(iIndex,cb,ctx){
    var paramArray = [iIndex];
    WebsocketApp.callMethod('GetDeviceSNByIndex', cb, ctx, "string", paramArray);
}


ComInterface.GetDeviceInfo = function(sDeviceSN,iType,cb,ctx){
    var ret = XTXAPP.GetDeviceInfo(sDeviceSN,iType);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetDeviceInfo = function(sDeviceSN,iType,cb,ctx){
    var paramArray = [sDeviceSN,iType];
    WebsocketApp.callMethod('GetDeviceInfo', cb, ctx, "string", paramArray);
}


ComInterface.ChangeAdminPass = function(sDeviceSN,sOldPass,sNewPass,cb,ctx){
    var ret = XTXAPP.ChangeAdminPass(sDeviceSN,sOldPass,sNewPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ChangeAdminPass = function(sDeviceSN,sOldPass,sNewPass,cb,ctx){
    var paramArray = [sDeviceSN,sOldPass,sNewPass];
    WebsocketApp.callMethod('ChangeAdminPass', cb, ctx, "bool", paramArray);
}


ComInterface.UnlockUserPass = function(sDeviceSN,sAdminPass,sNewUserPass,cb,ctx){
    var ret = XTXAPP.UnlockUserPass(sDeviceSN,sAdminPass,sNewUserPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.UnlockUserPass = function(sDeviceSN,sAdminPass,sNewUserPass,cb,ctx){
    var paramArray = [sDeviceSN,sAdminPass,sNewUserPass];
    WebsocketApp.callMethod('UnlockUserPass', cb, ctx, "bool", paramArray);
}


ComInterface.GenerateKeyPair = function(sDeviceSN,sContainerName,iKeyType,bSign,cb,ctx){
    var ret = XTXAPP.GenerateKeyPair(sDeviceSN,sContainerName,iKeyType,bSign);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GenerateKeyPair = function(sDeviceSN,sContainerName,iKeyType,bSign,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,iKeyType,bSign];
    WebsocketApp.callMethod('GenerateKeyPair', cb, ctx, "bool", paramArray);
}


ComInterface.ExportPubKey = function(sDeviceSN,sContainerName,bSign,cb,ctx){
    var ret = XTXAPP.ExportPubKey(sDeviceSN,sContainerName,bSign);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ExportPubKey = function(sDeviceSN,sContainerName,bSign,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,bSign];
    WebsocketApp.callMethod('ExportPubKey', cb, ctx, "string", paramArray);
}


ComInterface.ImportSignCert = function(sDeviceSN,sContainerName,sCert,cb,ctx){
    var ret = XTXAPP.ImportSignCert(sDeviceSN,sContainerName,sCert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ImportSignCert = function(sDeviceSN,sContainerName,sCert,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,sCert];
    WebsocketApp.callMethod('ImportSignCert', cb, ctx, "bool", paramArray);
}


ComInterface.ImportEncCert = function(sDeviceSN,sContainerName,sCert,sPriKeyCipher,cb,ctx){
    var ret = XTXAPP.ImportEncCert(sDeviceSN,sContainerName,sCert,sPriKeyCipher);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ImportEncCert = function(sDeviceSN,sContainerName,sCert,sPriKeyCipher,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,sCert,sPriKeyCipher];
    WebsocketApp.callMethod('ImportEncCert', cb, ctx, "bool", paramArray);
}


ComInterface.ReadFile = function(sDeviceSN,sFileName,cb,ctx){
    var ret = XTXAPP.ReadFile(sDeviceSN,sFileName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ReadFile = function(sDeviceSN,sFileName,cb,ctx){
    var paramArray = [sDeviceSN,sFileName];
    WebsocketApp.callMethod('ReadFile', cb, ctx, "string", paramArray);
}


ComInterface.WriteFile = function(sDeviceSN,sFileName,sContent,bPrivate,cb,ctx){
    var ret = XTXAPP.WriteFile(sDeviceSN,sFileName,sContent,bPrivate);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.WriteFile = function(sDeviceSN,sFileName,sContent,bPrivate,cb,ctx){
    var paramArray = [sDeviceSN,sFileName,sContent,bPrivate];
    WebsocketApp.callMethod('WriteFile', cb, ctx, "bool", paramArray);
}


ComInterface.IsContainerExist = function(sDeviceSN,sContainerName,cb,ctx){
    var ret = XTXAPP.IsContainerExist(sDeviceSN,sContainerName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.IsContainerExist = function(sDeviceSN,sContainerName,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName];
    WebsocketApp.callMethod('IsContainerExist', cb, ctx, "bool", paramArray);
}


ComInterface.DeleteContainer = function(sDeviceSN,sContainerName,cb,ctx){
    var ret = XTXAPP.DeleteContainer(sDeviceSN,sContainerName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.DeleteContainer = function(sDeviceSN,sContainerName,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName];
    WebsocketApp.callMethod('DeleteContainer', cb, ctx, "bool", paramArray);
}


ComInterface.ExportPKCS10 = function(sDeviceSN,sContainerName,sDN,bSign,cb,ctx){
    var ret = XTXAPP.ExportPKCS10(sDeviceSN,sContainerName,sDN,bSign);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ExportPKCS10 = function(sDeviceSN,sContainerName,sDN,bSign,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,sDN,bSign];
    WebsocketApp.callMethod('ExportPKCS10', cb, ctx, "string", paramArray);
}


ComInterface.InitDevice = function(sDeviceSN,sAdminPass,cb,ctx){
    var ret = XTXAPP.InitDevice(sDeviceSN,sAdminPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.InitDevice = function(sDeviceSN,sAdminPass,cb,ctx){
    var paramArray = [sDeviceSN,sAdminPass];
    WebsocketApp.callMethod('InitDevice', cb, ctx, "bool", paramArray);
}


ComInterface.CertListFormElement = function(cb,ctx){
    var ret = XTXAPP.CertListFormElement();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.CertListFormElement = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('CertListFormElement', cb, ctx, "string", paramArray);
}


ComInterface.CertListFormElement = function(newVal,cb,ctx){
    var ret = XTXAPP.CertListFormElement(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.CertListFormElement = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('CertListFormElement', cb, ctx, "string", paramArray);
}


ComInterface.AlertBeforeCertDate = function(cb,ctx){
    var ret = XTXAPP.AlertBeforeCertDate();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.AlertBeforeCertDate = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('AlertBeforeCertDate', cb, ctx, "string", paramArray);
}


ComInterface.AlertBeforeCertDate = function(newVal,cb,ctx){
    var ret = XTXAPP.AlertBeforeCertDate(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.AlertBeforeCertDate = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('AlertBeforeCertDate', cb, ctx, "string", paramArray);
}


ComInterface.ServerSignedData = function(cb,ctx){
    var ret = XTXAPP.ServerSignedData();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerSignedData = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('ServerSignedData', cb, ctx, "string", paramArray);
}


ComInterface.ServerSignedData = function(newVal,cb,ctx){
    var ret = XTXAPP.ServerSignedData(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerSignedData = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('ServerSignedData', cb, ctx, "string", paramArray);
}


ComInterface.ServerRan = function(cb,ctx){
    var ret = XTXAPP.ServerRan();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerRan = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('ServerRan', cb, ctx, "string", paramArray);
}


ComInterface.ServerRan = function(newVal,cb,ctx){
    var ret = XTXAPP.ServerRan(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerRan = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('ServerRan', cb, ctx, "string", paramArray);
}


ComInterface.ServerCert = function(cb,ctx){
    var ret = XTXAPP.ServerCert();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerCert = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('ServerCert', cb, ctx, "string", paramArray);
}


ComInterface.ServerCert = function(newVal,cb,ctx){
    var ret = XTXAPP.ServerCert(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerCert = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('ServerCert', cb, ctx, "string", paramArray);
}


ComInterface.ServerMode = function(cb,ctx){
    var ret = XTXAPP.ServerMode();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerMode = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('ServerMode', cb, ctx, "string", paramArray);
}


ComInterface.ServerMode = function(newVal,cb,ctx){
    var ret = XTXAPP.ServerMode(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ServerMode = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('ServerMode', cb, ctx, "string", paramArray);
}


ComInterface.ShowError = function(cb,ctx){
    var ret = XTXAPP.ShowError();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ShowError = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('ShowError', cb, ctx, "string", paramArray);
}


ComInterface.ShowError = function(newVal,cb,ctx){
    var ret = XTXAPP.ShowError(newVal);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ShowError = function(newVal,cb,ctx){
    var paramArray = [newVal];
    WebsocketApp.callMethod('ShowError', cb, ctx, "string", paramArray);
}


ComInterface.AddSignInfo = function(sUserPass,cb,ctx){
    var ret = XTXAPP.AddSignInfo(sUserPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.AddSignInfo = function(sUserPass,cb,ctx){
    var paramArray = [sUserPass];
    WebsocketApp.callMethod('AddSignInfo', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetVersion = function(cb,ctx){
    var ret = XTXAPP.SOF_GetVersion();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetVersion = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetVersion', cb, ctx, "string", paramArray);
}


ComInterface.SOF_ExportExChangeUserCert = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_ExportExChangeUserCert(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_ExportExChangeUserCert = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_ExportExChangeUserCert', cb, ctx, "string", paramArray);
}


ComInterface.SOF_ValidateCert = function(Cert,cb,ctx){
    var ret = XTXAPP.SOF_ValidateCert(Cert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_ValidateCert = function(Cert,cb,ctx){
    var paramArray = [Cert];
    WebsocketApp.callMethod('SOF_ValidateCert', cb, ctx, "number", paramArray);
}


ComInterface.GetENVSN = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.GetENVSN(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetENVSN = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('GetENVSN', cb, ctx, "string", paramArray);
}


ComInterface.SetENVSN = function(sDeviceSN,sEnvsn,cb,ctx){
    var ret = XTXAPP.SetENVSN(sDeviceSN,sEnvsn);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SetENVSN = function(sDeviceSN,sEnvsn,cb,ctx){
    var paramArray = [sDeviceSN,sEnvsn];
    WebsocketApp.callMethod('SetENVSN', cb, ctx, "bool", paramArray);
}


ComInterface.IsDeviceExist = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.IsDeviceExist(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.IsDeviceExist = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('IsDeviceExist', cb, ctx, "bool", paramArray);
}


ComInterface.GetContainerCount = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.GetContainerCount(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetContainerCount = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('GetContainerCount', cb, ctx, "number", paramArray);
}


ComInterface.SOF_SymEncryptData = function(sKey,indata,cb,ctx){
    var ret = XTXAPP.SOF_SymEncryptData(sKey,indata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SymEncryptData = function(sKey,indata,cb,ctx){
    var paramArray = [sKey,indata];
    WebsocketApp.callMethod('SOF_SymEncryptData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SymDecryptData = function(sKey,indata,cb,ctx){
    var ret = XTXAPP.SOF_SymDecryptData(sKey,indata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SymDecryptData = function(sKey,indata,cb,ctx){
    var paramArray = [sKey,indata];
    WebsocketApp.callMethod('SOF_SymDecryptData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SymEncryptFile = function(sKey,inFile,outFile,cb,ctx){
    var ret = XTXAPP.SOF_SymEncryptFile(sKey,inFile,outFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SymEncryptFile = function(sKey,inFile,outFile,cb,ctx){
    var paramArray = [sKey,inFile,outFile];
    WebsocketApp.callMethod('SOF_SymEncryptFile', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_SymDecryptFile = function(sKey,inFile,outFile,cb,ctx){
    var ret = XTXAPP.SOF_SymDecryptFile(sKey,inFile,outFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SymDecryptFile = function(sKey,inFile,outFile,cb,ctx){
    var paramArray = [sKey,inFile,outFile];
    WebsocketApp.callMethod('SOF_SymDecryptFile', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetLastErrMsg = function(cb,ctx){
    var ret = XTXAPP.SOF_GetLastErrMsg();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetLastErrMsg = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetLastErrMsg', cb, ctx, "string", paramArray);
}


ComInterface.SOF_Base64Encode = function(sIndata,cb,ctx){
    var ret = XTXAPP.SOF_Base64Encode(sIndata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Base64Encode = function(sIndata,cb,ctx){
    var paramArray = [sIndata];
    WebsocketApp.callMethod('SOF_Base64Encode', cb, ctx, "string", paramArray);
}


ComInterface.SOF_Base64Decode = function(sIndata,cb,ctx){
    var ret = XTXAPP.SOF_Base64Decode(sIndata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Base64Decode = function(sIndata,cb,ctx){
    var paramArray = [sIndata];
    WebsocketApp.callMethod('SOF_Base64Decode', cb, ctx, "string", paramArray);
}


ComInterface.SOF_HashData = function(hashAlg,sInData,cb,ctx){
    var ret = XTXAPP.SOF_HashData(hashAlg,sInData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashData = function(hashAlg,sInData,cb,ctx){
    var paramArray = [hashAlg,sInData];
    WebsocketApp.callMethod('SOF_HashData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_HashFile = function(hashAlg,inFile,cb,ctx){
    var ret = XTXAPP.SOF_HashFile(hashAlg,inFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashFile = function(hashAlg,inFile,cb,ctx){
    var paramArray = [hashAlg,inFile];
    WebsocketApp.callMethod('SOF_HashFile', cb, ctx, "string", paramArray);
}


ComInterface.UnlockUserPassEx = function(sDeviceSN,sAdminPin,sNewUserPass,cb,ctx){
    var ret = XTXAPP.UnlockUserPassEx(sDeviceSN,sAdminPin,sNewUserPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.UnlockUserPassEx = function(sDeviceSN,sAdminPin,sNewUserPass,cb,ctx){
    var paramArray = [sDeviceSN,sAdminPin,sNewUserPass];
    WebsocketApp.callMethod('UnlockUserPassEx', cb, ctx, "bool", paramArray);
}


ComInterface.DeleteOldContainer = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.DeleteOldContainer(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.DeleteOldContainer = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('DeleteOldContainer', cb, ctx, "bool", paramArray);
}


ComInterface.WriteFileEx = function(sDeviceSN,sFileName,sContent,bPrivate,cb,ctx){
    var ret = XTXAPP.WriteFileEx(sDeviceSN,sFileName,sContent,bPrivate);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.WriteFileEx = function(sDeviceSN,sFileName,sContent,bPrivate,cb,ctx){
    var paramArray = [sDeviceSN,sFileName,sContent,bPrivate];
    WebsocketApp.callMethod('WriteFileEx', cb, ctx, "bool", paramArray);
}


ComInterface.ReadFileEx = function(sDeviceSN,sFileName,cb,ctx){
    var ret = XTXAPP.ReadFileEx(sDeviceSN,sFileName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ReadFileEx = function(sDeviceSN,sFileName,cb,ctx){
    var paramArray = [sDeviceSN,sFileName];
    WebsocketApp.callMethod('ReadFileEx', cb, ctx, "string", paramArray);
}


ComInterface.SOF_EncryptDataEx = function(Cert,InData,cb,ctx){
    var ret = XTXAPP.SOF_EncryptDataEx(Cert,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EncryptDataEx = function(Cert,InData,cb,ctx){
    var paramArray = [Cert,InData];
    WebsocketApp.callMethod('SOF_EncryptDataEx', cb, ctx, "string", paramArray);
}


ComInterface.Base64EncodeFile = function(sInFile,cb,ctx){
    var ret = XTXAPP.Base64EncodeFile(sInFile);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.Base64EncodeFile = function(sInFile,cb,ctx){
    var paramArray = [sInFile];
    WebsocketApp.callMethod('Base64EncodeFile', cb, ctx, "string", paramArray);
}


ComInterface.SOF_GetRetryCount = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_GetRetryCount(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetRetryCount = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_GetRetryCount', cb, ctx, "number", paramArray);
}


ComInterface.SOF_GetAllContainerName = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.SOF_GetAllContainerName(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetAllContainerName = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('SOF_GetAllContainerName', cb, ctx, "string", paramArray);
}


ComInterface.CreateSoftDevice = function(sDeviceSN,sLabel,cb,ctx){
    var ret = XTXAPP.CreateSoftDevice(sDeviceSN,sLabel);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.CreateSoftDevice = function(sDeviceSN,sLabel,cb,ctx){
    var paramArray = [sDeviceSN,sLabel];
    WebsocketApp.callMethod('CreateSoftDevice', cb, ctx, "bool", paramArray);
}


ComInterface.DeleteSoftDevice = function(sDeviceSN,sPasswd,cb,ctx){
    var ret = XTXAPP.DeleteSoftDevice(sDeviceSN,sPasswd);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.DeleteSoftDevice = function(sDeviceSN,sPasswd,cb,ctx){
    var paramArray = [sDeviceSN,sPasswd];
    WebsocketApp.callMethod('DeleteSoftDevice', cb, ctx, "bool", paramArray);
}


ComInterface.EnableSoftDevice = function(enable,sDeviceSN,cb,ctx){
    var ret = XTXAPP.EnableSoftDevice(enable,sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.EnableSoftDevice = function(enable,sDeviceSN,cb,ctx){
    var paramArray = [enable,sDeviceSN];
    WebsocketApp.callMethod('EnableSoftDevice', cb, ctx, "bool", paramArray);
}


ComInterface.SoftDeviceBackup = function(sDeviceSN,sPasswd,cb,ctx){
    var ret = XTXAPP.SoftDeviceBackup(sDeviceSN,sPasswd);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SoftDeviceBackup = function(sDeviceSN,sPasswd,cb,ctx){
    var paramArray = [sDeviceSN,sPasswd];
    WebsocketApp.callMethod('SoftDeviceBackup', cb, ctx, "string", paramArray);
}


ComInterface.SoftDeviceRestore = function(sDeviceSN,sPasswd,sInFilePath,cb,ctx){
    var ret = XTXAPP.SoftDeviceRestore(sDeviceSN,sPasswd,sInFilePath);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SoftDeviceRestore = function(sDeviceSN,sPasswd,sInFilePath,cb,ctx){
    var paramArray = [sDeviceSN,sPasswd,sInFilePath];
    WebsocketApp.callMethod('SoftDeviceRestore', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_Logout = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_Logout(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Logout = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_Logout', cb, ctx, "bool", paramArray);
}


ComInterface.SetUserConfig = function(type,strConfig,cb,ctx){
    var ret = XTXAPP.SetUserConfig(type,strConfig);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SetUserConfig = function(type,strConfig,cb,ctx){
    var paramArray = [type,strConfig];
    WebsocketApp.callMethod('SetUserConfig', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_SignByteData = function(CertID,byteLen,cb,ctx){
    var ret = XTXAPP.SOF_SignByteData(CertID,byteLen);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignByteData = function(CertID,byteLen,cb,ctx){
    var paramArray = [CertID,byteLen];
    WebsocketApp.callMethod('SOF_SignByteData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedByteData = function(Cert,byteLen,SignValue,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedByteData(Cert,byteLen,SignValue);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedByteData = function(Cert,byteLen,SignValue,cb,ctx){
    var paramArray = [Cert,byteLen,SignValue];
    WebsocketApp.callMethod('SOF_VerifySignedByteData', cb, ctx, "bool", paramArray);
}


ComInterface.OTP_GetChallengeCode = function(sCertID,cb,ctx){
    var ret = XTXAPP.OTP_GetChallengeCode(sCertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OTP_GetChallengeCode = function(sCertID,cb,ctx){
    var paramArray = [sCertID];
    WebsocketApp.callMethod('OTP_GetChallengeCode', cb, ctx, "string", paramArray);
}


ComInterface.ImportEncCertEx = function(sDeviceSN,sContainerName,sCert,sPriKeyCipher,ulSymAlg,cb,ctx){
    var ret = XTXAPP.ImportEncCertEx(sDeviceSN,sContainerName,sCert,sPriKeyCipher,ulSymAlg);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ImportEncCertEx = function(sDeviceSN,sContainerName,sCert,sPriKeyCipher,ulSymAlg,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,sCert,sPriKeyCipher,ulSymAlg];
    WebsocketApp.callMethod('ImportEncCertEx', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_GetCertEntity = function(sCert,cb,ctx){
    var ret = XTXAPP.SOF_GetCertEntity(sCert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_GetCertEntity = function(sCert,cb,ctx){
    var paramArray = [sCert];
    WebsocketApp.callMethod('SOF_GetCertEntity', cb, ctx, "string", paramArray);
}


ComInterface.SOF_HMAC = function(hashid,key,indata,cb,ctx){
    var ret = XTXAPP.SOF_HMAC(hashid,key,indata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HMAC = function(hashid,key,indata,cb,ctx){
    var paramArray = [hashid,key,indata];
    WebsocketApp.callMethod('SOF_HMAC', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignDataByPriKey = function(sPriKey,sCert,sInData,cb,ctx){
    var ret = XTXAPP.SOF_SignDataByPriKey(sPriKey,sCert,sInData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignDataByPriKey = function(sPriKey,sCert,sInData,cb,ctx){
    var paramArray = [sPriKey,sCert,sInData];
    WebsocketApp.callMethod('SOF_SignDataByPriKey', cb, ctx, "string", paramArray);
}


ComInterface.ImportKeyCertToSoftDevice = function(sDeviceSN,sContainerName,sPriKey,sCert,bSign,cb,ctx){
    var ret = XTXAPP.ImportKeyCertToSoftDevice(sDeviceSN,sContainerName,sPriKey,sCert,bSign);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ImportKeyCertToSoftDevice = function(sDeviceSN,sContainerName,sPriKey,sCert,bSign,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,sPriKey,sCert,bSign];
    WebsocketApp.callMethod('ImportKeyCertToSoftDevice', cb, ctx, "bool", paramArray);
}


ComInterface.InitDeviceEx = function(sDeviceSN,sAdminPass,sUserPin,sKeyLabel,adminPinMaxRetry,userPinMaxRetry,cb,ctx){
    var ret = XTXAPP.InitDeviceEx(sDeviceSN,sAdminPass,sUserPin,sKeyLabel,adminPinMaxRetry,userPinMaxRetry);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.InitDeviceEx = function(sDeviceSN,sAdminPass,sUserPin,sKeyLabel,adminPinMaxRetry,userPinMaxRetry,cb,ctx){
    var paramArray = [sDeviceSN,sAdminPass,sUserPin,sKeyLabel,adminPinMaxRetry,userPinMaxRetry];
    WebsocketApp.callMethod('InitDeviceEx', cb, ctx, "bool", paramArray);
}


ComInterface.SelectFile = function(cb,ctx){
    var ret = XTXAPP.SelectFile();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SelectFile = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SelectFile', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignHashData = function(CertID,b64ashData,hashAlg,cb,ctx){
    var ret = XTXAPP.SOF_SignHashData(CertID,b64ashData,hashAlg);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignHashData = function(CertID,b64ashData,hashAlg,cb,ctx){
    var paramArray = [CertID,b64ashData,hashAlg];
    WebsocketApp.callMethod('SOF_SignHashData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedHashData = function(Cert,b64ashData,SignValue,hashAlg,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedHashData(Cert,b64ashData,SignValue,hashAlg);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedHashData = function(Cert,b64ashData,SignValue,hashAlg,cb,ctx){
    var paramArray = [Cert,b64ashData,SignValue,hashAlg];
    WebsocketApp.callMethod('SOF_VerifySignedHashData', cb, ctx, "bool", paramArray);
}


ComInterface.CheckSoftDeviceEnv = function(cb,ctx){
    var ret = XTXAPP.CheckSoftDeviceEnv();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.CheckSoftDeviceEnv = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('CheckSoftDeviceEnv', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_SignBinaryData = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_SignBinaryData(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignBinaryData = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_SignBinaryData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedBinaryData = function(Cert,SignValue,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedBinaryData(Cert,SignValue);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedBinaryData = function(Cert,SignValue,cb,ctx){
    var paramArray = [Cert,SignValue];
    WebsocketApp.callMethod('SOF_VerifySignedBinaryData', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_EncryptBinaryData = function(Cert,cb,ctx){
    var ret = XTXAPP.SOF_EncryptBinaryData(Cert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EncryptBinaryData = function(Cert,cb,ctx){
    var paramArray = [Cert];
    WebsocketApp.callMethod('SOF_EncryptBinaryData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_EncryptBinaryDataEx = function(Cert,cb,ctx){
    var ret = XTXAPP.SOF_EncryptBinaryDataEx(Cert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EncryptBinaryDataEx = function(Cert,cb,ctx){
    var paramArray = [Cert];
    WebsocketApp.callMethod('SOF_EncryptBinaryDataEx', cb, ctx, "string", paramArray);
}


ComInterface.SOF_DecryptBinaryData = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_DecryptBinaryData(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_DecryptBinaryData = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_DecryptBinaryData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignBinaryMessage = function(dwFlag,CertID,cb,ctx){
    var ret = XTXAPP.SOF_SignBinaryMessage(dwFlag,CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignBinaryMessage = function(dwFlag,CertID,cb,ctx){
    var paramArray = [dwFlag,CertID];
    WebsocketApp.callMethod('SOF_SignBinaryMessage', cb, ctx, "string", paramArray);
}


ComInterface.SOF_VerifySignedBinaryMessage = function(MessageData,cb,ctx){
    var ret = XTXAPP.SOF_VerifySignedBinaryMessage(MessageData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_VerifySignedBinaryMessage = function(MessageData,cb,ctx){
    var paramArray = [MessageData];
    WebsocketApp.callMethod('SOF_VerifySignedBinaryMessage', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_PubKeyBinaryEncrypt = function(Cert,cb,ctx){
    var ret = XTXAPP.SOF_PubKeyBinaryEncrypt(Cert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_PubKeyBinaryEncrypt = function(Cert,cb,ctx){
    var paramArray = [Cert];
    WebsocketApp.callMethod('SOF_PubKeyBinaryEncrypt', cb, ctx, "string", paramArray);
}


ComInterface.SOF_PriKeyBinaryDecrypt = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_PriKeyBinaryDecrypt(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_PriKeyBinaryDecrypt = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_PriKeyBinaryDecrypt', cb, ctx, "string", paramArray);
}


ComInterface.SOF_Base64BinaryEncode = function(cb,ctx){
    var ret = XTXAPP.SOF_Base64BinaryEncode();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Base64BinaryEncode = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_Base64BinaryEncode', cb, ctx, "string", paramArray);
}


ComInterface.SOF_Base64BinaryDecode = function(sIndata,cb,ctx){
    var ret = XTXAPP.SOF_Base64BinaryDecode(sIndata);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_Base64BinaryDecode = function(sIndata,cb,ctx){
    var paramArray = [sIndata];
    WebsocketApp.callMethod('SOF_Base64BinaryDecode', cb, ctx, "string", paramArray);
}


ComInterface.SOF_HashBinaryData = function(hashAlg,sCert,sID,cb,ctx){
    var ret = XTXAPP.SOF_HashBinaryData(hashAlg,sCert,sID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashBinaryData = function(hashAlg,sCert,sID,cb,ctx){
    var paramArray = [hashAlg,sCert,sID];
    WebsocketApp.callMethod('SOF_HashBinaryData', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignBinaryDataByPriKey = function(sPriKey,sCert,cb,ctx){
    var ret = XTXAPP.SOF_SignBinaryDataByPriKey(sPriKey,sCert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignBinaryDataByPriKey = function(sPriKey,sCert,cb,ctx){
    var paramArray = [sPriKey,sCert];
    WebsocketApp.callMethod('SOF_SignBinaryDataByPriKey', cb, ctx, "string", paramArray);
}


ComInterface.ImportPfxToDevice = function(sDeviceSN,sContainerName,bSign,strPfx,strPfxPass,cb,ctx){
    var ret = XTXAPP.ImportPfxToDevice(sDeviceSN,sContainerName,bSign,strPfx,strPfxPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ImportPfxToDevice = function(sDeviceSN,sContainerName,bSign,strPfx,strPfxPass,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,bSign,strPfx,strPfxPass];
    WebsocketApp.callMethod('ImportPfxToDevice', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_HashDataEx = function(hashAlg,sInData,sCert,sID,cb,ctx){
    var ret = XTXAPP.SOF_HashDataEx(hashAlg,sInData,sCert,sID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashDataEx = function(hashAlg,sInData,sCert,sID,cb,ctx){
    var paramArray = [hashAlg,sInData,sCert,sID];
    WebsocketApp.callMethod('SOF_HashDataEx', cb, ctx, "string", paramArray);
}


ComInterface.SOF_HashDataExBase64 = function(hashAlg,sInData,sCert,sID,cb,ctx){
    var ret = XTXAPP.SOF_HashDataExBase64(hashAlg,sInData,sCert,sID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashDataExBase64 = function(hashAlg,sInData,sCert,sID,cb,ctx){
    var paramArray = [hashAlg,sInData,sCert,sID];
    WebsocketApp.callMethod('SOF_HashDataExBase64', cb, ctx, "string", paramArray);
}

ComInterface.SOF_HashFileEx = function(hashAlg,inFile,sCert,sID,cb,ctx){
    var ret = XTXAPP.SOF_HashFileEx(hashAlg,inFile,sCert,sID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_HashFileEx = function(hashAlg,inFile,sCert,sID,cb,ctx){
    var paramArray = [hashAlg,inFile,sCert,sID];
    WebsocketApp.callMethod('SOF_HashFileEx', cb, ctx, "string", paramArray);
}


ComInterface.GetDeviceCountEx = function(type,cb,ctx){
    var ret = XTXAPP.GetDeviceCountEx(type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetDeviceCountEx = function(type,cb,ctx){
    var paramArray = [type];
    WebsocketApp.callMethod('GetDeviceCountEx', cb, ctx, "number", paramArray);
}


ComInterface.GetAllDeviceSNEx = function(type,cb,ctx){
    var ret = XTXAPP.GetAllDeviceSNEx(type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetAllDeviceSNEx = function(type,cb,ctx){
    var paramArray = [type];
    WebsocketApp.callMethod('GetAllDeviceSNEx', cb, ctx, "string", paramArray);
}


ComInterface.SOF_UpdateCert = function(CertID,type,cb,ctx){
    var ret = XTXAPP.SOF_UpdateCert(CertID,type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_UpdateCert = function(CertID,type,cb,ctx){
    var paramArray = [CertID,type];
    WebsocketApp.callMethod('SOF_UpdateCert', cb, ctx, "number", paramArray);
}


ComInterface.OpenSpecifiedFolder = function(backupFilePath,cb,ctx){
    var ret = XTXAPP.OpenSpecifiedFolder(backupFilePath);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OpenSpecifiedFolder = function(backupFilePath,cb,ctx){
    var paramArray = [backupFilePath];
    WebsocketApp.callMethod('OpenSpecifiedFolder', cb, ctx, "string", paramArray);
}


ComInterface.OTP_GetChallengeCodeEx = function(sCertID,szAccount,money,cb,ctx){
    var ret = XTXAPP.OTP_GetChallengeCodeEx(sCertID,szAccount,money);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OTP_GetChallengeCodeEx = function(sCertID,szAccount,money,cb,ctx){
    var paramArray = [sCertID,szAccount,money];
    WebsocketApp.callMethod('OTP_GetChallengeCodeEx', cb, ctx, "string", paramArray);
}


ComInterface.Base64DecodeFile = function(sInData,sFilePath,cb,ctx){
    var ret = XTXAPP.Base64DecodeFile(sInData,sFilePath);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.Base64DecodeFile = function(sInData,sFilePath,cb,ctx){
    var paramArray = [sInData,sFilePath];
    WebsocketApp.callMethod('Base64DecodeFile', cb, ctx, "bool", paramArray);
}


ComInterface.EnumFilesInDevice = function(sDeviceSN,cb,ctx){
    var ret = XTXAPP.EnumFilesInDevice(sDeviceSN);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.EnumFilesInDevice = function(sDeviceSN,cb,ctx){
    var paramArray = [sDeviceSN];
    WebsocketApp.callMethod('EnumFilesInDevice', cb, ctx, "string", paramArray);
}


ComInterface.OTP_Halt = function(sCertID,cb,ctx){
    var ret = XTXAPP.OTP_Halt(sCertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OTP_Halt = function(sCertID,cb,ctx){
    var paramArray = [sCertID];
    WebsocketApp.callMethod('OTP_Halt', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_TSGenREQ = function(b64Hash,hashAlg,bReqCert,policyID,b64Nonce,b64Extension,cb,ctx){
    var ret = XTXAPP.SOF_TSGenREQ(b64Hash,hashAlg,bReqCert,policyID,b64Nonce,b64Extension);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_TSGenREQ = function(b64Hash,hashAlg,bReqCert,policyID,b64Nonce,b64Extension,cb,ctx){
    var paramArray = [b64Hash,hashAlg,bReqCert,policyID,b64Nonce,b64Extension];
    WebsocketApp.callMethod('SOF_TSGenREQ', cb, ctx, "string", paramArray);
}


ComInterface.SOF_TSCompareNonce = function(b64TSReq,b64TSAResp,cb,ctx){
    var ret = XTXAPP.SOF_TSCompareNonce(b64TSReq,b64TSAResp);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_TSCompareNonce = function(b64TSReq,b64TSAResp,cb,ctx){
    var paramArray = [b64TSReq,b64TSAResp];
    WebsocketApp.callMethod('SOF_TSCompareNonce', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_TSGenPDFSignature = function(b64TSAResp,b64OriPDFSignature,cb,ctx){
    var ret = XTXAPP.SOF_TSGenPDFSignature(b64TSAResp,b64OriPDFSignature);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_TSGenPDFSignature = function(b64TSAResp,b64OriPDFSignature,cb,ctx){
    var paramArray = [b64TSAResp,b64OriPDFSignature];
    WebsocketApp.callMethod('SOF_TSGenPDFSignature', cb, ctx, "string", paramArray);
}


ComInterface.SOF_TSVerifyPDFSignature = function(b64TSPDFSignature,cb,ctx){
    var ret = XTXAPP.SOF_TSVerifyPDFSignature(b64TSPDFSignature);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_TSVerifyPDFSignature = function(b64TSPDFSignature,cb,ctx){
    var paramArray = [b64TSPDFSignature];
    WebsocketApp.callMethod('SOF_TSVerifyPDFSignature', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_TSGetPDFSignatureInfo = function(b64TSPDFSignature,iType,cb,ctx){
    var ret = XTXAPP.SOF_TSGetPDFSignatureInfo(b64TSPDFSignature,iType);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_TSGetPDFSignatureInfo = function(b64TSPDFSignature,iType,cb,ctx){
    var paramArray = [b64TSPDFSignature,iType];
    WebsocketApp.callMethod('SOF_TSGetPDFSignatureInfo', cb, ctx, "string", paramArray);
}


ComInterface.OTP_GetState = function(sCertID,bCert,cb,ctx){
    var ret = XTXAPP.OTP_GetState(sCertID,bCert);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OTP_GetState = function(sCertID,bCert,cb,ctx){
    var paramArray = [sCertID,bCert];
    WebsocketApp.callMethod('OTP_GetState', cb, ctx, "string", paramArray);
}


ComInterface.OTP_GetSyncCode = function(sCertID,ChallengeCode,cb,ctx){
    var ret = XTXAPP.OTP_GetSyncCode(sCertID,ChallengeCode);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.OTP_GetSyncCode = function(sCertID,ChallengeCode,cb,ctx){
    var paramArray = [sCertID,ChallengeCode];
    WebsocketApp.callMethod('OTP_GetSyncCode', cb, ctx, "string", paramArray);
}


ComInterface.SOF_IsLogin = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_IsLogin(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_IsLogin = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_IsLogin', cb, ctx, "bool", paramArray);
}


ComInterface.SOF_LoginEx = function(CertID,PassWd,updateFlag,cb,ctx){
    var ret = XTXAPP.SOF_LoginEx(CertID,PassWd,updateFlag);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_LoginEx = function(CertID,PassWd,updateFlag,cb,ctx){
    var paramArray = [CertID,PassWd,updateFlag];
    WebsocketApp.callMethod('SOF_LoginEx', cb, ctx, "bool", paramArray);
}


ComInterface.EnumSupportDeviceList = function(cb,ctx){
    var ret = XTXAPP.EnumSupportDeviceList();
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.EnumSupportDeviceList = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('EnumSupportDeviceList', cb, ctx, "string", paramArray);
}


ComInterface.ExportPfxFromDevice = function(sDeviceSN,sContainerName,bSign,strPfxPass,cb,ctx){
    var ret = XTXAPP.ExportPfxFromDevice(sDeviceSN,sContainerName,bSign,strPfxPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ExportPfxFromDevice = function(sDeviceSN,sContainerName,bSign,strPfxPass,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,bSign,strPfxPass];
    WebsocketApp.callMethod('ExportPfxFromDevice', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignHashMessage = function(CertID,InHashData,hashAlg,cb,ctx){
    var ret = XTXAPP.SOF_SignHashMessage(CertID,InHashData,hashAlg);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignHashMessage = function(CertID,InHashData,hashAlg,cb,ctx){
    var paramArray = [CertID,InHashData,hashAlg];
    WebsocketApp.callMethod('SOF_SignHashMessage', cb, ctx, "string", paramArray);
}


ComInterface.ExportPfxToFile = function(sDeviceSN,sContainerName,bSign,strPfxPass,cb,ctx){
    var ret = XTXAPP.ExportPfxToFile(sDeviceSN,sContainerName,bSign,strPfxPass);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ExportPfxToFile = function(sDeviceSN,sContainerName,bSign,strPfxPass,cb,ctx){
    var paramArray = [sDeviceSN,sContainerName,bSign,strPfxPass];
    WebsocketApp.callMethod('ExportPfxToFile', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignAPK = function(CertID,strOriSignature,cb,ctx){
    var ret = XTXAPP.SOF_SignAPK(CertID,strOriSignature);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignAPK = function(CertID,strOriSignature,cb,ctx){
    var paramArray = [CertID,strOriSignature];
    WebsocketApp.callMethod('SOF_SignAPK', cb, ctx, "string", paramArray);
}


ComInterface.SOF_ListenUKey = function(Parm,cb,ctx){
    var ret = XTXAPP.SOF_ListenUKey(Parm);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_ListenUKey = function(Parm,cb,ctx){
    var paramArray = [Parm];
    WebsocketApp.callMethod('SOF_ListenUKey', cb, ctx, "string", paramArray);
}


ComInterface.SOF_EnableLoginWindow = function(Parm,cb,ctx){
    var ret = XTXAPP.SOF_EnableLoginWindow(Parm);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_EnableLoginWindow = function(Parm,cb,ctx){
    var paramArray = [Parm];
    WebsocketApp.callMethod('SOF_EnableLoginWindow', cb, ctx, "string", paramArray);
}


ComInterface.SOF_SignEnvelope = function(CertID,Cert,InData,cb,ctx){
    var ret = XTXAPP.SOF_SignEnvelope(CertID,Cert,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_SignEnvelope = function(CertID,Cert,InData,cb,ctx){
    var paramArray = [CertID,Cert,InData];
    WebsocketApp.callMethod('SOF_SignEnvelope', cb, ctx, "string", paramArray);
}


ComInterface.SOF_UnSignEnvelope = function(CertID,InData,cb,ctx){
    var ret = XTXAPP.SOF_UnSignEnvelope(CertID,InData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.SOF_UnSignEnvelope = function(CertID,InData,cb,ctx){
    var paramArray = [CertID,InData];
    WebsocketApp.callMethod('SOF_UnSignEnvelope', cb, ctx, "string", paramArray);
}

ComInterface.SOF_GetLastLoginCertID = function(cb,ctx){
    var ret = XTXAPP.SOF_GetLastLoginCertID();
    return $myOKRtnFunc(ret, cb, ctx);
}
WebsocketInterface.SOF_GetLastLoginCertID = function(cb,ctx){
    var paramArray = [];
    WebsocketApp.callMethod('SOF_GetLastLoginCertID', cb, ctx, "string", paramArray);
}

ComInterface.SOF_GetLastSignDataCertID = function(CertID,cb,ctx){
    var ret = XTXAPP.SOF_GetLastSignDataCertID(CertID);
    return $myOKRtnFunc(ret, cb, ctx);
}
WebsocketInterface.SOF_GetLastSignDataCertID = function(CertID,cb,ctx){
    var paramArray = [CertID];
    WebsocketApp.callMethod('SOF_GetLastSignDataCertID', cb, ctx, "string", paramArray);
}



init_Interface("SOF_SetSignMethod","SetSignMethod")
init_Interface("SOF_GetSignMethod")
init_Interface("SOF_SetEncryptMethod")
init_Interface("SOF_GetEncryptMethod")
init_Interface("SOF_GetUserList","GetUserList")
init_Interface("SOF_ExportUserCert","GetSignCert")
init_Interface("SOF_Login","VerifyUserPIN")
init_Interface("SOF_GetPinRetryCount","GetUserPINRetryCount")
init_Interface("SOF_ChangePassWd","ChangeUserPassword")
init_Interface("SOF_GetCertInfo","GetCertBasicinfo")
init_Interface("SOF_GetCertInfoByOid","GetExtCertInfoByOID")
init_Interface("SOF_SignData","SignedData")
init_Interface("SOF_SignDataBase64","SignedDataBase64")
init_Interface("SOF_VerifySignedData","VerifySignedData")
init_Interface("SOF_VerifySignedDataBase64","VerifySignedDataBase64")
init_Interface("SOF_SignFile","SignFile")
init_Interface("SOF_VerifySignedFile","VerifySignFile")
init_Interface("SOF_EncryptData","EncodeP7Enveloped")
init_Interface("SOF_DecryptData","DecodeP7Enveloped")
init_Interface("SOF_EncryptFile")
init_Interface("SOF_DecryptFile")
init_Interface("SOF_SignMessage","SignByP7")
init_Interface("SOF_VerifySignedMessage","VerifyDatabyP7")
init_Interface("SOF_GetInfoFromSignedMessage")
init_Interface("SOF_SignDataXML")
init_Interface("SOF_VerifySignedDataXML")
init_Interface("SOF_GetXMLSignatureInfo")
init_Interface("SOF_GenRandom","GenerateRandom")
init_Interface("SOF_PubKeyEncrypt","PubKeyEncrypt")
init_Interface("SOF_PriKeyDecrypt","PriKeyDecrypt")
init_Interface("SOF_SecertSegment")
init_Interface("SOF_SecertRecovery")
init_Interface("SOF_GetLastError")
init_Interface("GetDeviceCount")
init_Interface("GetAllDeviceSN")
init_Interface("GetDeviceSNByIndex")
init_Interface("GetDeviceInfo")
init_Interface("ChangeAdminPass")
init_Interface("UnlockUserPass")
init_Interface("GenerateKeyPair")
init_Interface("ExportPubKey")
init_Interface("ImportSignCert")
init_Interface("ImportEncCert")
init_Interface("ReadFile")
init_Interface("WriteFile")
init_Interface("IsContainerExist")
init_Interface("DeleteContainer")
init_Interface("ExportPKCS10")
init_Interface("InitDevice")
init_Interface("CertListFormElement")
init_Interface("AlertBeforeCertDate")
init_Interface("ServerSignedData")
init_Interface("ServerRan")
init_Interface("ServerCert")
init_Interface("ServerMode")
init_Interface("ShowError")
init_Interface("AddSignInfo")
init_Interface("SOF_GetVersion")
init_Interface("SOF_ExportExChangeUserCert","GetExchCert")
init_Interface("SOF_ValidateCert","ValidateCert")
init_Interface("GetENVSN")
init_Interface("SetENVSN")
init_Interface("IsDeviceExist")
init_Interface("GetContainerCount")
init_Interface("SOF_SymEncryptData","EncryptData")
init_Interface("SOF_SymDecryptData","DecryptData")
init_Interface("SOF_SymEncryptFile","EncryptFile")
init_Interface("SOF_SymDecryptFile","DecryptFile")
init_Interface("SOF_GetLastErrMsg")
init_Interface("SOF_Base64Encode")
init_Interface("SOF_Base64Decode")
init_Interface("SOF_HashData","SignHashData")
init_Interface("SOF_HashFile","HashFile")
init_Interface("UnlockUserPassEx")
init_Interface("DeleteOldContainer")
init_Interface("WriteFileEx")
init_Interface("ReadFileEx")
init_Interface("SOF_EncryptDataEx","EncodeP7Enveloped")
init_Interface("Base64EncodeFile")
init_Interface("SOF_GetRetryCount")
init_Interface("SOF_GetAllContainerName")
init_Interface("CreateSoftDevice")
init_Interface("DeleteSoftDevice")
init_Interface("EnableSoftDevice")
init_Interface("SoftDeviceBackup")
init_Interface("SoftDeviceRestore")
init_Interface("SOF_Logout","Logout")
init_Interface("SetUserConfig")
init_Interface("SOF_SignByteData")
init_Interface("SOF_VerifySignedByteData")
init_Interface("OTP_GetChallengeCode")
init_Interface("ImportEncCertEx")
init_Interface("SOF_GetCertEntity","GetCertEntity")
init_Interface("SOF_HMAC")
init_Interface("SOF_SignDataByPriKey")
init_Interface("ImportKeyCertToSoftDevice")
init_Interface("InitDeviceEx")
init_Interface("SelectFile")
init_Interface("SOF_SignHashData","SignHashData")
init_Interface("SOF_VerifySignedHashData","VerifySignedHashData")
init_Interface("CheckSoftDeviceEnv")
init_Interface("SOF_SignBinaryData")
init_Interface("SOF_VerifySignedBinaryData")
init_Interface("SOF_EncryptBinaryData")
init_Interface("SOF_EncryptBinaryDataEx")
init_Interface("SOF_DecryptBinaryData")
init_Interface("SOF_SignBinaryMessage")
init_Interface("SOF_VerifySignedBinaryMessage")
init_Interface("SOF_PubKeyBinaryEncrypt")
init_Interface("SOF_PriKeyBinaryDecrypt")
init_Interface("SOF_Base64BinaryEncode")
init_Interface("SOF_Base64BinaryDecode")
init_Interface("SOF_HashBinaryData")
init_Interface("SOF_SignBinaryDataByPriKey")
init_Interface("ImportPfxToDevice")
init_Interface("SOF_HashDataEx")
init_Interface("SOF_HashDataExBase64")
init_Interface("SOF_HashFileEx")
init_Interface("GetDeviceCountEx")
init_Interface("GetAllDeviceSNEx")
init_Interface("SOF_UpdateCert")
init_Interface("OpenSpecifiedFolder")
init_Interface("OTP_GetChallengeCodeEx")
init_Interface("Base64DecodeFile")
init_Interface("EnumFilesInDevice")
init_Interface("OTP_Halt")
init_Interface("SOF_TSGenREQ")
init_Interface("SOF_TSCompareNonce")
init_Interface("SOF_TSGenPDFSignature")
init_Interface("SOF_TSVerifyPDFSignature")
init_Interface("SOF_TSGetPDFSignatureInfo")
init_Interface("OTP_GetState")
init_Interface("OTP_GetSyncCode")
init_Interface("SOF_IsLogin")
init_Interface("SOF_LoginEx")
init_Interface("EnumSupportDeviceList")
init_Interface("ExportPfxFromDevice")
init_Interface("SOF_SignHashMessage")
init_Interface("ExportPfxToFile")
init_Interface("SOF_SignAPK")
init_Interface("SOF_ListenUKey")
init_Interface("SOF_EnableLoginWindow")
init_Interface("SOF_SignEnvelope")
init_Interface("SOF_UnSignEnvelope")
init_Interface("SOF_GetLastLoginCertID")
init_Interface("SOF_GetLastSignDataCertID")

//template_gen_end

//getpic template_gen
PicComInterface.GetPic = function(bstrConName,cb,ctx){
    var ret = OGetPic.GetPic(bstrConName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetPic = function(bstrConName,cb,ctx){
    var paramArray = [bstrConName];
    WebsocketApp.callMethod('GetPic', cb, ctx, "string", paramArray);
}


PicComInterface.Hash = function(inData,cb,ctx){
    var ret = OGetPic.Hash(inData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.Hash = function(inData,cb,ctx){
    var paramArray = [inData];
    WebsocketApp.callMethod('Hash', cb, ctx, "string", paramArray);
}


PicComInterface.ConvertPicFormat = function(inData,type,cb,ctx){
    var ret = OGetPic.ConvertPicFormat(inData,type);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ConvertPicFormat = function(inData,type,cb,ctx){
    var paramArray = [inData,type];
    WebsocketApp.callMethod('ConvertPicFormat', cb, ctx, "string", paramArray);
}


PicComInterface.ConvertGif2Jpg = function(inData,cb,ctx){
    var ret = OGetPic.ConvertGif2Jpg(inData);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ConvertGif2Jpg = function(inData,cb,ctx){
    var paramArray = [inData];
    WebsocketApp.callMethod('ConvertGif2Jpg', cb, ctx, "string", paramArray);
}


PicComInterface.GetPic1 = function(bstrConName,cb,ctx){
    var ret = OGetPic.GetPic1(bstrConName);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.GetPic1 = function(bstrConName,cb,ctx){
    var paramArray = [bstrConName];
    WebsocketApp.callMethod('GetPic1', cb, ctx, "string", paramArray);
}


PicComInterface.ConvertPicSize = function(bstrPic,w,h,cb,ctx){
    var ret = OGetPic.ConvertPicSize(bstrPic,w,h);
    return $myOKRtnFunc(ret, cb, ctx);
}


WebsocketInterface.ConvertPicSize = function(bstrPic,w,h,cb,ctx){
    var paramArray = [bstrPic,w,h];
    WebsocketApp.callMethod('ConvertPicSize', cb, ctx, "string", paramArray);
}



init_Interface("GetPic")
init_Interface("Hash")
init_Interface("ConvertPicFormat")
init_Interface("ConvertGif2Jpg")
init_Interface("GetPic1")
init_Interface("ConvertPicSize")

//getpic template_gen_end


//dsvs interface
// DsvsInterface.GenRandom = function(nRandom,cb,ctx){
//     var ret = SVSAPP.GenRandom(nRandom);
//     return $myOKRtnFunc(ret, cb, ctx);
// }
//
// WebsocketInterface.GenRandom = function(nRandom,cb,ctx){
//     var paramArray = [nRandom];
//     WebsocketApp.callMethod('GenRandom', cb, ctx, "string", paramArray);
// }
//
// ComInterface.VerifySignedData1 = function(bstrCert,bstrData,bstrSignedData,cb,ctx){
//     var ret = SVSAPP.VerifySignedData1(bstrCert,bstrData,bstrSignedData);
//     return $myOKRtnFunc(ret, cb, ctx);
// }
//
// WebsocketInterface.VerifySignedData1 = function(bstrCert,bstrData,bstrSignedData,cb,ctx){
//     var paramArray = [bstrCert,bstrData,bstrSignedData];
//     WebsocketApp.callMethod('VerifySignedData1', cb, ctx, "int", paramArray);
// }
//
// init_Interface("GenRandom")
// init_Interface("VerifySignedData1")
//dsvs interface end





